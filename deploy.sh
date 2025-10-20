#!/bin/bash

set -e

APP_NAME="storyboard-app"
DOCKER_IMAGE="$1"
NEW_PORT="$2"
CURRENT_PORT="$3"
NGINX_CONFIG_PATH="/etc/nginx/sites-available/storyboard"
PROPERTIES_FILE="/opt/storyboard/application-prod.properties"

echo "Starting Blue/Green deployment..."
echo "New image: $DOCKER_IMAGE"
echo "New port: $NEW_PORT"
echo "Current port: $CURRENT_PORT"

check_health() {
    local port=$1
    local max_attempts=30
    local attempt=1
    
    echo "Checking health on port $port..."
    
    while [ $attempt -le $max_attempts ]; do
        if curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
            echo "Health check passed on port $port"
            return 0
        fi
        
        echo "Health check attempt $attempt/$max_attempts failed, waiting 10 seconds..."
        sleep 10
        attempt=$((attempt + 1))
    done
    
    echo "Health check failed after $max_attempts attempts"
    return 1
}

# Stop and remove existing container on new port if exists
echo "Cleaning up existing container on port $NEW_PORT..."
docker stop ${APP_NAME}-$NEW_PORT 2>/dev/null || true
docker rm ${APP_NAME}-$NEW_PORT 2>/dev/null || true

# Start new container
echo "Starting new container on port $NEW_PORT..."
docker run -d \
    --name ${APP_NAME}-$NEW_PORT \
    -p $NEW_PORT:8080 \
    -v $PROPERTIES_FILE:/app/application-prod.properties:ro \
    --restart unless-stopped \
    $DOCKER_IMAGE \
    --spring.config.additional-location=file:/app/application-prod.properties

# Wait for new container to be healthy
if ! check_health $NEW_PORT; then
    echo "New container failed health check, rolling back..."
    docker stop ${APP_NAME}-$NEW_PORT 2>/dev/null || true
    docker rm ${APP_NAME}-$NEW_PORT 2>/dev/null || true
    exit 1
fi

echo "New container is healthy, updating nginx upstream configuration..."
echo "Switching upstream from port $CURRENT_PORT to port $NEW_PORT"

# Update nginx upstream configuration to point to new port
sudo sed -i "/upstream storyboard_backend/,/}/ s/server 127.0.0.1:[0-9]*;/server 127.0.0.1:$NEW_PORT;/" $NGINX_CONFIG_PATH

# Test nginx configuration
if ! sudo nginx -t; then
    echo "Nginx configuration test failed, rolling back..."
    sudo sed -i "/upstream storyboard_backend/,/}/ s/server 127.0.0.1:$NEW_PORT;/server 127.0.0.1:$CURRENT_PORT;/" $NGINX_CONFIG_PATH
    docker stop ${APP_NAME}-$NEW_PORT 2>/dev/null || true
    docker rm ${APP_NAME}-$NEW_PORT 2>/dev/null || true
    exit 1
fi

# Reload nginx
echo "Reloading nginx..."
sudo systemctl reload nginx

# Give some time for nginx to fully switch
sleep 5

# Final health check through nginx
echo "Performing final health check through nginx..."
if ! curl -f https://storyboardai.site/actuator/health > /dev/null 2>&1; then
    echo "Final health check failed, rolling back..."
    sudo sed -i "/upstream storyboard_backend/,/}/ s/server 127.0.0.1:$NEW_PORT;/server 127.0.0.1:$CURRENT_PORT;/" $NGINX_CONFIG_PATH
    sudo systemctl reload nginx
    docker stop ${APP_NAME}-$NEW_PORT 2>/dev/null || true
    docker rm ${APP_NAME}-$NEW_PORT 2>/dev/null || true
    exit 1
fi

echo "Deployment successful! Cleaning up old container..."

# Stop and remove old container
docker stop ${APP_NAME}-$CURRENT_PORT 2>/dev/null || true
docker rm ${APP_NAME}-$CURRENT_PORT 2>/dev/null || true

# Clean up old images
echo "Cleaning up old Docker images..."
docker images $APP_NAME --format "table {{.Tag}}\t{{.ID}}" | tail -n +2 | sort -r | tail -n +4 | awk '{print $2}' | xargs -r docker rmi || true

echo "Blue/Green deployment completed successfully!"
echo "Application is now running on port $NEW_PORT"
echo "Nginx upstream is now pointing to 127.0.0.1:$NEW_PORT"
echo "Traffic is being served through the new container"
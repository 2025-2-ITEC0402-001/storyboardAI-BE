#!/bin/bash

NGINX_CONFIG_PATH="/etc/nginx/sites-available/storyboard"

if [ -f "$NGINX_CONFIG_PATH" ]; then
    CURRENT_PORT=$(grep -A 5 'upstream storyboard_backend' $NGINX_CONFIG_PATH | grep -o 'server 127.0.0.1:[0-9]*' | grep -o '[0-9]*$' | head -1)
    if [ -n "$CURRENT_PORT" ]; then
        echo $CURRENT_PORT
    else
        echo "8080"  # Default
    fi
else
    echo "8080"  # Default
fi
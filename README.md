# Storyboard AI Backend

## 프로젝트 개요

AI 기반 스토리보드 자동 생성 플랫폼의 백엔드 API 서버입니다. 사용자가 텍스트 프롬프트를 입력하면 AI가 스토리보드 이미지를 생성하고, 생성된 이미지들을 캔버스 형태로 관리할 수 있는 시스템을 제공합니다.

## 시스템 아키텍처

### 전체 플로우
1. **사용자 인증**: OAuth 2.0 (카카오) 기반 로그인
2. **프로젝트 생성**: 스토리보드 프로젝트 생성 및 관리
3. **AI 이미지 생성**: 텍스트 프롬프트를 통한 이미지 생성
4. **캔버스 관리**: 생성된 이미지들을 캔버스에 배치 및 편집
5. **실시간 알림**: SSE를 통한 AI 작업 진행상황 실시간 전달

### 도메인 구조
```
├── auth/          # 인증 및 JWT 토큰 관리
├── user/          # 사용자 정보 관리
├── project/       # 스토리보드 프로젝트 관리
├── ai/            # AI 이미지 생성 API 연동
├── aiimage/       # 비동기 이미지 생성 및 SSE 스트리밍
└── common/        # 공통 설정 및 예외 처리
```

## 핵심 기능 상세

### 1. Project Canvas 저장 방식

#### 데이터 형식
- **저장 포맷**: JSON 구조로 캔버스 상태 관리
- **기본 스키마**:
```json
{
  "nodes": [
    {
      "id": "node_uuid",
      "type": "image",
      "position": { "x": 100, "y": 150 },
      "data": {
        "imageUrl": "https://s3.amazonaws.com/bucket/image.png",
        "prompt": "사용자가 입력한 프롬프트",
        "width": 512,
        "height": 768
      }
    }
  ],
  "connections": [
    {
      "id": "connection_uuid",
      "source": "source_node_id",
      "target": "target_node_id",
      "type": "default"
    }
  ]
}
```

#### 캔버스 업데이트 방식
- **JSON Patch** 방식 사용으로 효율적인 부분 업데이트
- **요청 예시**:
```json
[
  { "op": "add", "path": "/nodes/-", "value": {...} },
  { "op": "replace", "path": "/nodes/0/position", "value": {"x": 200, "y": 300} },
  { "op": "remove", "path": "/nodes/1" }
]
```

### 2. AI 이미지 생성 시스템

#### AI 도메인 (ai/)
**역할**: 외부 AI 서버와의 직접적인 통신 담당

**입력 형식**:
```json
// 이미지 생성
{
  "prompt": "A beautiful landscape with mountains",
  "height": 1024,
  "width": 768,
  "guidanceScale": 3.5,
  "numInferenceSteps": 20,
  "seed": 42
}

// 이미지 수정
{
  "image": "(MultipartFile)",
  "revisedPrompt": "Add a sunset in the background",
  "strength": 0.8,
  "guidanceScale": 3.5,
  "numInferenceSteps": 25,
  "seed": 42
}
```

**출력 형식**: 바이너리 이미지 데이터 (byte[])

#### AIImage 도메인 (aiimage/)
**역할**: 비동기 이미지 생성 및 실시간 진행상황 전달

**처리 과정**:
1. 클라이언트 요청 접수 → 즉시 taskId 반환
2. 백그라운드에서 AI 서버 호출
3. SSE를 통한 실시간 상태 전달:
   - `PENDING`: 대기중
   - `IN_PROGRESS`: AI 생성중 (진행률 포함)
   - `UPLOADING`: S3 업로드중
   - `COMPLETED`: 완료 (이미지 URL 포함)
   - `FAILED`: 실패 (에러 메시지 포함)

**SSE 이벤트 형식**:
```json
{
  "taskId": "uuid",
  "status": "IN_PROGRESS",
  "message": "AI 모델이 이미지를 생성중입니다",
  "progress": 30,
  "imageUrl": null,
  "timestamp": "2024-10-09T10:30:00"
}
```

### 3. API 엔드포인트

#### 프로젝트 관리
- `POST /api/projects` - 프로젝트 생성
- `GET /api/projects/{id}` - 프로젝트 조회
- `PUT /api/projects/{id}` - 프로젝트 정보 수정
- `PATCH /api/projects/{id}/canvas` - 캔버스 업데이트 (JSON Patch)
- `DELETE /api/projects/{id}` - 프로젝트 삭제

#### AI 이미지 생성
- `POST /api/ai/images/generate` - 이미지 생성 요청
- `POST /api/ai/images/revise` - 이미지 수정 요청
- `GET /api/ai-images/stream/{taskId}` - SSE 스트림 연결

### 4. 기술 스택

- **Framework**: Spring Boot 3.5.6
- **Database**: MySQL 8.0
- **Cache**: Redis
- **Storage**: AWS S3
- **Authentication**: JWT + OAuth 2.0 (Kakao)
- **HTTP Client**: WebClient (Spring WebFlux)
- **Real-time**: Server-Sent Events (SSE)
- **Documentation**: OpenAPI 3.0 (Swagger)

### 5. 환경 설정

#### 필수 환경변수
```properties
# Database
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_USER=root
MYSQL_PASSWORD=password
MYSQL_DATABASE=storyboard

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=password

# JWT
SECRET_KEY=your_jwt_secret_key

# OAuth
KAKAO_REST_API_KEY=your_kakao_api_key
KAKAO_BACKEND_REDIRECT_URI=http://localhost:8080/auth/oauth/kakao/callback

# AWS S3
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
S3_BUCKET_NAME=your_bucket_name

# AI Server
AI_GENERATE_SERVER_URL=http://localhost:5000
AI_REVISE_SERVER_URL=http://localhost:5001
```

## 프로젝트 구조

각 도메인은 헥사고날 아키텍처 패턴을 따라 구성되어 있습니다:

```
domain/
├── business/
│   ├── dto/           # 데이터 전송 객체
│   ├── port/          # 인터페이스 정의
│   └── service/       # 비즈니스 로직
├── domain/            # 도메인 모델
├── infrastructure/    # 외부 시스템 연동
│   ├── entity/        # JPA 엔티티
│   └── persistence/   # 데이터베이스 접근
├── presentation/      # API 레이어
│   ├── api/           # API 인터페이스
│   └── controller/    # REST 컨트롤러
└── exception/         # 도메인 예외
```

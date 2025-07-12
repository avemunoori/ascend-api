# Training Plan API Documentation

## Overview

The Training Plan API provides endpoints for managing persistent, user-specific training plan progress in the Ascend climbing app. All user-specific endpoints require authentication via JWT token.

## Base URL

```
https://your-api-domain.com/api/training
```

## Authentication

All endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Endpoints

### 1. Get Training Plan Templates

**GET** `/templates`

Retrieves available training plan templates with optional filtering.

**Query Parameters:**
- `difficulty` (optional): Filter by difficulty level (BEGINNER, INTERMEDIATE, ADVANCED)
- `category` (optional): Filter by category (STRENGTH, ENDURANCE, TECHNIQUE)

**Response:**
```json
[
  {
    "id": "uuid",
    "name": "Beginner Strength Foundation",
    "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
    "totalWeeks": 4,
    "sessionsPerWeek": 3,
    "difficulty": "BEGINNER",
    "category": "STRENGTH",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

**Example:**
```bash
curl -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/templates?difficulty=BEGINNER"
```

### 2. Start a Training Plan

**POST** `/user-plans`

Starts a new training plan for the authenticated user.

**Request Body:**
```json
{
  "templateId": "uuid-of-template"
}
```

**Response:**
```json
{
  "id": "user-plan-uuid",
  "name": "Beginner Strength Foundation",
  "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
  "status": "ACTIVE",
  "currentWeek": 1,
  "currentSession": 1,
  "startedAt": "2024-01-01T10:00:00",
  "pausedAt": null,
  "completedAt": null,
  "lastActivityAt": "2024-01-01T10:00:00",
  "template": {
    "id": "template-uuid",
    "name": "Beginner Strength Foundation",
    "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
    "totalWeeks": 4,
    "sessionsPerWeek": 3,
    "difficulty": "BEGINNER",
    "category": "STRENGTH",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00"
  },
  "completedSessions": 0,
  "totalSessions": 12,
  "progressPercentage": 0.0
}
```

**Example:**
```bash
curl -X POST \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"templateId": "uuid-of-template"}' \
     "https://your-api-domain.com/api/training/user-plans"
```

### 3. Get User's Training Plans

**GET** `/user-plans`

Retrieves all training plans for the authenticated user, ordered by start date (newest first).

**Response:**
```json
[
  {
    "id": "user-plan-uuid",
    "name": "Beginner Strength Foundation",
    "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
    "status": "ACTIVE",
    "currentWeek": 1,
    "currentSession": 1,
    "startedAt": "2024-01-01T10:00:00",
    "pausedAt": null,
    "completedAt": null,
    "lastActivityAt": "2024-01-01T10:00:00",
    "template": { /* template details */ },
    "completedSessions": 0,
    "totalSessions": 12,
    "progressPercentage": 0.0
  }
]
```

**Example:**
```bash
curl -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/user-plans"
```

### 4. Get Active Training Plan

**GET** `/user-plans/active`

Retrieves the user's currently active training plan.

**Response:**
```json
{
  "id": "user-plan-uuid",
  "name": "Beginner Strength Foundation",
  "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
  "status": "ACTIVE",
  "currentWeek": 1,
  "currentSession": 1,
  "startedAt": "2024-01-01T10:00:00",
  "pausedAt": null,
  "completedAt": null,
  "lastActivityAt": "2024-01-01T10:00:00",
  "template": { /* template details */ },
  "completedSessions": 0,
  "totalSessions": 12,
  "progressPercentage": 0.0
}
```

**Example:**
```bash
curl -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/user-plans/active"
```

### 5. Get Plan Details

**GET** `/user-plans/{planId}`

Retrieves detailed information about a specific training plan.

**Path Parameters:**
- `planId`: UUID of the training plan

**Response:**
```json
{
  "id": "user-plan-uuid",
  "name": "Beginner Strength Foundation",
  "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
  "status": "ACTIVE",
  "currentWeek": 1,
  "currentSession": 1,
  "startedAt": "2024-01-01T10:00:00",
  "pausedAt": null,
  "completedAt": null,
  "lastActivityAt": "2024-01-01T10:00:00",
  "template": { /* template details */ },
  "weeks": [
    {
      "id": "week-uuid",
      "weekNumber": 1,
      "status": "IN_PROGRESS",
      "startedAt": "2024-01-01T10:00:00",
      "completedAt": null,
      "weekTemplate": { /* week template details */ },
      "sessions": [
        {
          "id": "session-uuid",
          "sessionNumber": 1,
          "status": "COMPLETED",
          "startedAt": "2024-01-01T10:00:00",
          "completedAt": "2024-01-01T11:00:00",
          "actualDurationMinutes": 60,
          "notes": "Great session!",
          "sessionTemplate": { /* session template details */ },
          "exercises": [ /* exercise details */ ]
        }
      ]
    }
  ],
  "completedSessions": 1,
  "totalSessions": 12,
  "progressPercentage": 8.33
}
```

**Example:**
```bash
curl -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/user-plans/user-plan-uuid"
```

### 6. Complete a Training Session

**POST** `/user-plans/{planId}/sessions/{sessionId}/complete`

Marks a training session as completed.

**Path Parameters:**
- `planId`: UUID of the training plan
- `sessionId`: Session number (integer)

**Request Body:**
```json
{
  "sessionNumber": 1,
  "actualDurationMinutes": 60,
  "notes": "Great session! Felt strong today."
}
```

**Response:**
```json
{
  "id": "user-plan-uuid",
  "name": "Beginner Strength Foundation",
  "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
  "status": "ACTIVE",
  "currentWeek": 1,
  "currentSession": 2,
  "startedAt": "2024-01-01T10:00:00",
  "pausedAt": null,
  "completedAt": null,
  "lastActivityAt": "2024-01-01T11:00:00",
  "template": { /* template details */ },
  "completedSessions": 1,
  "totalSessions": 12,
  "progressPercentage": 8.33
}
```

**Example:**
```bash
curl -X POST \
     -H "Authorization: Bearer <token>" \
     -H "Content-Type: application/json" \
     -d '{"sessionNumber": 1, "actualDurationMinutes": 60, "notes": "Great session!"}' \
     "https://your-api-domain.com/api/training/user-plans/user-plan-uuid/sessions/1/complete"
```

### 7. Pause a Training Plan

**POST** `/user-plans/{planId}/pause`

Pauses an active training plan.

**Path Parameters:**
- `planId`: UUID of the training plan

**Response:**
```json
{
  "id": "user-plan-uuid",
  "name": "Beginner Strength Foundation",
  "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
  "status": "PAUSED",
  "currentWeek": 1,
  "currentSession": 1,
  "startedAt": "2024-01-01T10:00:00",
  "pausedAt": "2024-01-01T12:00:00",
  "completedAt": null,
  "lastActivityAt": "2024-01-01T12:00:00",
  "template": { /* template details */ },
  "completedSessions": 0,
  "totalSessions": 12,
  "progressPercentage": 0.0
}
```

**Example:**
```bash
curl -X POST \
     -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/user-plans/user-plan-uuid/pause"
```

### 8. Resume a Training Plan

**POST** `/user-plans/{planId}/resume`

Resumes a paused training plan.

**Path Parameters:**
- `planId`: UUID of the training plan

**Response:**
```json
{
  "id": "user-plan-uuid",
  "name": "Beginner Strength Foundation",
  "description": "A 4-week program designed to build fundamental climbing strength for beginners.",
  "status": "ACTIVE",
  "currentWeek": 1,
  "currentSession": 1,
  "startedAt": "2024-01-01T10:00:00",
  "pausedAt": null,
  "completedAt": null,
  "lastActivityAt": "2024-01-01T13:00:00",
  "template": { /* template details */ },
  "completedSessions": 0,
  "totalSessions": 12,
  "progressPercentage": 0.0
}
```

**Example:**
```bash
curl -X POST \
     -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/user-plans/user-plan-uuid/resume"
```

### 9. Get Plan Progress (Analytics)

**GET** `/user-plans/{planId}/progress`

Retrieves detailed progress analytics for a training plan.

**Path Parameters:**
- `planId`: UUID of the training plan

**Response:**
Same as Get Plan Details endpoint, but specifically for analytics purposes.

**Example:**
```bash
curl -H "Authorization: Bearer <token>" \
     "https://your-api-domain.com/api/training/user-plans/user-plan-uuid/progress"
```

## Error Responses

### 400 Bad Request
```json
{
  "error": "Invalid request data",
  "message": "Template ID is required"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing authentication token"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied",
  "message": "You don't have permission to access this resource"
}
```

### 404 Not Found
```json
{
  "error": "Resource not found",
  "message": "Training plan not found"
}
```

### 409 Conflict
```json
{
  "error": "Conflict",
  "message": "User already has an active training plan"
}
```

## Status Codes

- **ACTIVE**: Plan is currently being followed
- **PAUSED**: Plan has been paused and can be resumed
- **COMPLETED**: Plan has been finished successfully
- **ABANDONED**: Plan was abandoned by the user

## Session Status Codes

- **NOT_STARTED**: Session hasn't been started yet
- **IN_PROGRESS**: Session is currently being worked on
- **COMPLETED**: Session has been finished

## Week Status Codes

- **NOT_STARTED**: Week hasn't been started yet
- **IN_PROGRESS**: Week is currently being worked on
- **COMPLETED**: Week has been finished

## Exercise Status Codes

- **NOT_STARTED**: Exercise hasn't been started yet
- **IN_PROGRESS**: Exercise is currently being worked on
- **COMPLETED**: Exercise has been finished

## Frontend Integration Notes

1. **Authentication**: Ensure JWT token is included in all requests
2. **Error Handling**: Implement proper error handling for all API responses
3. **Progress Tracking**: Use the `progressPercentage` field to display progress bars
4. **Session Completion**: Call the complete session endpoint when user finishes a training session
5. **Plan Management**: Allow users to pause/resume plans as needed
6. **Data Persistence**: All progress is automatically saved and synced across devices

## Testing

You can test the API endpoints using tools like:
- Postman
- cURL
- Insomnia
- Any HTTP client

Make sure to:
1. Register/login to get a JWT token
2. Include the token in the Authorization header
3. Test all CRUD operations
4. Verify error handling
5. Test with invalid data to ensure proper validation 
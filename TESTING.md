# Testing Guide for Ascend API

This document provides comprehensive information about testing the Ascend API endpoints.

## Test Structure

The test suite is organized into the following test classes:

### 1. `AuthControllerTest` - Authentication Endpoints
Tests the authentication-related endpoints:
- **POST /api/auth/login** - User login with valid/invalid credentials
- **POST /api/auth/validate** - Token validation with valid/invalid tokens
- **POST /api/auth/register** - User registration

**Coverage:**
- ✅ Valid login credentials return JWT token
- ✅ Invalid email returns 401
- ✅ Invalid password returns 401
- ✅ Valid token returns user info
- ✅ Invalid token returns 401
- ✅ Missing authorization header returns 401
- ✅ User not found returns 401

### 2. `UserControllerTest` - User Management Endpoints
Tests user management endpoints:
- **POST /api/users** - Create new user
- **GET /api/users/{id}** - Get user by ID
- **GET /api/users/me** - Get current user

**Coverage:**
- ✅ Valid user creation returns user response
- ✅ Invalid user creation returns 400
- ✅ Valid user ID returns user response
- ✅ Invalid user ID returns 404
- ✅ Valid token returns current user
- ✅ Invalid token returns 401
- ✅ Missing authorization header returns 401
- ✅ User not found returns 404

### 3. `SessionControllerTest` - Session Management Endpoints
Tests all session-related endpoints:
- **POST /api/sessions** - Create new session
- **GET /api/sessions** - Get user sessions (with filters)
- **GET /api/sessions/{sessionId}** - Get session by ID
- **GET /api/sessions/discipline/{discipline}** - Get sessions by discipline
- **GET /api/sessions/date/{date}** - Get sessions by date
- **PATCH /api/sessions/{sessionId}** - Update session
- **PUT /api/sessions/{sessionId}** - Replace session
- **DELETE /api/sessions/{sessionId}** - Delete session
- **GET /api/sessions/analytics** - Get session analytics
- **GET /api/sessions/stats/overview** - Get stats overview
- **GET /api/sessions/stats/progress** - Get progress stats
- **GET /api/sessions/stats/highest** - Get highest grades
- **GET /api/sessions/stats/average** - Get average grades
- **GET /api/sessions/grades/{discipline}** - Get grades for discipline

**Coverage:**
- ✅ Session creation with valid request
- ✅ Session creation with invalid request returns 400
- ✅ Get all sessions without filters
- ✅ Get sessions with discipline filter
- ✅ Get sessions with date filter
- ✅ Get session by valid ID
- ✅ Get sessions by discipline
- ✅ Get sessions by date
- ✅ Update session with valid request
- ✅ Replace session with valid request
- ✅ Delete session returns 204
- ✅ Get analytics returns session analytics
- ✅ Get stats overview returns analytics
- ✅ Get progress stats returns progress analytics
- ✅ Get highest grades returns grade map
- ✅ Get average grades returns average map
- ✅ Get grades for discipline returns grade array
- ✅ All endpoints with invalid token return 401
- ✅ All endpoints with missing auth header return 401

### 4. `HealthControllerTest` - Health Check Endpoints
Tests health check endpoints:
- **GET /health** - Health check
- **GET /** - Root endpoint

**Coverage:**
- ✅ Health endpoint returns "OK"
- ✅ Root endpoint returns welcome message

### 5. `IntegrationTest` - Full Application Tests
Tests the complete application context:
- ✅ Application context loads successfully
- ✅ Health endpoint is accessible
- ✅ Root endpoint is accessible
- ✅ Protected endpoints require authentication

## Running Tests

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests AuthControllerTest
./gradlew test --tests UserControllerTest
./gradlew test --tests SessionControllerTest
./gradlew test --tests HealthControllerTest
./gradlew test --tests IntegrationTest
```

### Run Tests with Coverage
```bash
./gradlew test jacocoTestReport
```

### Run Tests in IDE
1. Right-click on the test class in your IDE
2. Select "Run Test" or "Debug Test"
3. Or run individual test methods

## Test Configuration

### Test Properties (`src/test/resources/application-test.properties`)
- Uses H2 in-memory database for testing
- JWT secret configured for testing
- Debug logging enabled
- Test user credentials configured

### Dependencies
- Spring Boot Test Starter
- Spring Security Test
- H2 Database (for in-memory testing)
- JUnit 5

## Test Data

### Test Users
- Email: `test@example.com`
- Password: `password123`

### Test Sessions
- Discipline: `BOULDER`
- Grade: `V4`
- Date: Current date
- Notes: "Test session"

### Test Tokens
- Valid token: `valid.jwt.token`
- Invalid token: `invalid.token`

## Mocking Strategy

The tests use Mockito to mock:
- **UserRepository** - User data access
- **UserService** - User business logic
- **SessionService** - Session business logic
- **JwtService** - JWT token operations

This allows testing the controller layer in isolation while ensuring proper integration with the service layer.

## Security Testing

All protected endpoints are tested for:
- ✅ Valid authentication
- ✅ Invalid authentication
- ✅ Missing authentication
- ✅ Token validation
- ✅ User authorization

## Error Handling Testing

Tests cover various error scenarios:
- ✅ 400 Bad Request for invalid input
- ✅ 401 Unauthorized for authentication failures
- ✅ 404 Not Found for missing resources
- ✅ 500 Internal Server Error for unexpected exceptions

## Best Practices

1. **Test Naming**: Tests follow the pattern `methodName_condition_expectedResult`
2. **Setup**: Each test class has a `@BeforeEach` method for common setup
3. **Mocking**: Services are mocked to isolate controller testing
4. **Assertions**: Tests verify both HTTP status codes and response content
5. **Coverage**: Tests cover both success and failure scenarios
6. **Isolation**: Tests are independent and don't rely on each other

## Continuous Integration

The test suite is designed to run in CI/CD pipelines:
- Fast execution with in-memory database
- No external dependencies
- Comprehensive coverage of all endpoints
- Clear pass/fail results

## Troubleshooting

### Common Issues

1. **Test Database Connection**: Ensure H2 dependency is included
2. **JWT Configuration**: Check test properties for JWT secret
3. **Mock Setup**: Verify all required mocks are configured
4. **Test Isolation**: Ensure tests don't interfere with each other

### Debug Mode
Enable debug logging in test properties:
```properties
logging.level.com.ascend=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Future Enhancements

Consider adding:
- Performance tests for high-load scenarios
- Contract tests for API compatibility
- End-to-end tests with real database
- Security penetration tests
- Load testing for concurrent users 
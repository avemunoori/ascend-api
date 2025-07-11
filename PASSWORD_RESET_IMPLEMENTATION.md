# Password Reset Implementation

This document describes the implementation of forgot password and reset password functionality for the Ascend API.

## Overview

The implementation includes:
- Secure token generation and validation
- Email-based password reset
- Rate limiting for security
- Domain validation for registration
- Automatic cleanup of expired tokens

## New Endpoints

### 1. Forgot Password Request
```
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "message": "If an account with that email exists, a password reset link has been sent"
}
```

### 2. Verify Reset Code
```
POST /api/auth/verify-reset-code
Content-Type: application/json

{
  "code": "123456"
}
```

### 3. Reset Password
```
POST /api/auth/reset-password
Content-Type: application/json

{
  "code": "123456",
  "newPassword": "new-password-here"
}
```

**Response:**
```json
{
  "message": "Password has been reset successfully"
}
```

## Database Changes

A new table `password_reset_tokens` has been added with the following structure:

```sql
CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    code VARCHAR(6) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Components

### 1. PasswordResetToken Entity
- JPA entity with proper relationships
- Built-in validation methods (`isExpired()`, `isValid()`)
- Automatic timestamp management

### 2. DTOs
- `ForgotPasswordRequest`: Validates email format
- `ResetPasswordRequest`: Validates token and password requirements

### 3. Repository
- `PasswordResetTokenRepository`: Custom queries for token management
- Methods for finding valid tokens, cleanup, and user-specific operations

### 4. Services
- `PasswordResetService`: Core business logic
- `EmailService`: Handles email sending
- `DomainValidator`: Validates email domains

### 5. Controllers
- Enhanced `AuthController` with new endpoints
- Proper error handling and validation

## Security Features

### 1. Code Security
- 6-digit numeric code generation
- 15-minute expiration time
- Maximum 3 attempts per code
- Single-use codes
- Automatic cleanup of expired codes

### 2. Rate Limiting
- 3 requests per hour per IP for forgot password
- Uses Bucket4j for efficient rate limiting
- Proper IP address detection (supports proxies)

### 3. Email Security
- No information disclosure about user existence
- Secure reset codes sent via email
- Professional email templates with prominent code display

### 4. Domain Validation
- Restricted email domains for registration
- Configurable allowed domains list
- Prevents abuse from disposable email services

## Configuration

### Email Configuration (application.properties)
```properties
# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Frontend URL for reset links
app.frontend.url=${FRONTEND_URL:http://localhost:3000}
```

### Environment Variables
- `MAIL_USERNAME`: Gmail address for sending emails
- `MAIL_PASSWORD`: Gmail app password (not regular password)
- `FRONTEND_URL`: Frontend URL for reset links

## Email Setup

### Gmail Configuration
1. Enable 2-factor authentication on your Gmail account
2. Generate an App Password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate a password for "Mail"
3. Use the generated password in `MAIL_PASSWORD`

### Alternative Email Providers
The configuration can be easily adapted for other providers:
- SendGrid: Use SMTP with SendGrid credentials
- AWS SES: Use AWS SES SMTP settings
- Custom SMTP: Configure with your own SMTP server

## Error Handling

The implementation provides consistent error responses:

```json
{
  "message": "Error description"
}
```

Common error scenarios:
- Invalid email format
- User not found (forgot password)
- Invalid/expired token
- Token already used
- Email sending failure
- Rate limit exceeded

## Scheduled Tasks

### Token Cleanup
- Runs every 6 hours
- Removes expired and used tokens
- Prevents database bloat
- Logs cleanup activities

## Testing

### Manual Testing
1. Request password reset with valid email
2. Check email for reset link
3. Use token to reset password
4. Verify login with new password
5. Test with invalid/expired tokens

### Integration Testing
- Test rate limiting
- Test domain validation
- Test email sending
- Test token expiration

## Monitoring and Logging

The implementation includes comprehensive logging:
- Password reset requests
- Email sending success/failure
- Token validation attempts
- Cleanup operations
- Rate limiting events

## Dependencies Added

- `spring-boot-starter-mail`: Email functionality
- `bucket4j-core`: Rate limiting
- Existing validation and JPA dependencies

## Frontend Integration

The frontend should:
1. Provide a forgot password form
2. Handle the reset password flow
3. Display appropriate success/error messages
4. Redirect users after successful reset

Example frontend flow:
1. User enters email → POST `/api/auth/forgot-password`
2. User clicks email link → Navigate to reset page with token
3. User enters new password → POST `/api/auth/reset-password`
4. Redirect to login page

## Security Best Practices

1. **HTTPS Only**: All endpoints should be accessed via HTTPS
2. **Token Security**: Tokens are cryptographically secure and time-limited
3. **Rate Limiting**: Prevents brute force attacks
4. **No Information Disclosure**: Doesn't reveal if email exists
5. **Input Validation**: Comprehensive validation on all inputs
6. **Logging**: Security events are logged for monitoring
7. **Cleanup**: Automatic cleanup prevents resource exhaustion

## Future Enhancements

Potential improvements:
1. Email templates with HTML formatting
2. SMS-based password reset
3. Multi-factor authentication integration
4. Password strength requirements
5. Account lockout after failed attempts
6. Audit trail for password changes 
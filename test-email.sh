#!/bin/bash

# Test Email Functionality Script
# This script tests the password reset email functionality

echo "🧗 Testing Ascend Climbing Email Configuration"
echo "=============================================="

# Check if application is running
echo "1. Checking if application is running..."
if curl -s http://localhost:8080/api/health > /dev/null; then
    echo "✅ Application is running on http://localhost:8080"
else
    echo "❌ Application is not running. Please start the application first."
    echo "   Run: ./gradlew bootRun"
    exit 1
fi

# Test password reset endpoint
echo ""
echo "2. Testing password reset email endpoint..."
echo "   Sending test email to: test@example.com"

RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}')

echo "   Response: $RESPONSE"

if echo "$RESPONSE" | grep -q "password reset link has been sent"; then
    echo "✅ Password reset endpoint is working"
else
    echo "❌ Password reset endpoint failed"
    echo "   Check application logs for details"
fi

echo ""
echo "3. Next steps:"
echo "   - Check your email (test@example.com) for the password reset link"
echo "   - Check application logs for any SMTP errors"
echo "   - Verify the email comes from noreply@ascendclimbing.xyz"
echo "   - Password reset links will use: exp://localhost:8081/reset-password?token=..."
echo ""
echo "4. To test with a real email, update the email address in the curl command above"
echo "5. For React Native/Expo: Configure deep linking to handle reset password links" 
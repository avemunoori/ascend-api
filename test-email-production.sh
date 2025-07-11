#!/bin/bash

# Test Email Functionality with Production Backend
# This script tests the password reset email functionality

PRODUCTION_URL="https://ascend-api-production.up.railway.app"
TEST_EMAIL="avemunoori@gmail.com"

echo "🧗 Testing Ascend Climbing Email Configuration (Production)"
echo "=========================================================="
echo "Backend URL: $PRODUCTION_URL"
echo "Test Email: $TEST_EMAIL"
echo ""

# Check if backend is accessible
echo "1. Checking if backend is accessible..."
if curl -s "$PRODUCTION_URL/health" > /dev/null; then
    echo "✅ Backend is accessible"
else
    echo "❌ Backend is not accessible"
    exit 1
fi

# Test password reset endpoint
echo ""
echo "2. Testing password reset email endpoint..."
echo "   Sending test email to: $TEST_EMAIL"

RESPONSE=$(curl -s -X POST "$PRODUCTION_URL/api/auth/forgot-password" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\"}")

echo "   Response: $RESPONSE"

if echo "$RESPONSE" | grep -q "password reset link has been sent"; then
    echo "✅ Password reset endpoint is working"
    echo ""
    echo "📧 Email Details:"
    echo "   - Sent to: $TEST_EMAIL"
    echo "   - From: noreply@ascendclimbing.xyz"
    echo "   - Reset link format: exp://localhost:8081/reset-password?token=..."
    echo ""
    echo "🔍 Next steps:"
    echo "   1. Check your email inbox (and spam folder)"
    echo "   2. Look for email from noreply@ascendclimbing.xyz"
    echo "   3. Subject: 'Reset Your Ascend Climbing Password'"
    echo "   4. Click the 'Reset My Password' button"
    echo "   5. The link should open your Expo app"
    echo ""
    echo "⚠️  Important Notes:"
    echo "   - If you don't have a user account with this email, no email will be sent"
    echo "   - Check your spam/junk folder if you don't see the email"
    echo "   - The email contains a beautiful HTML template with your branding"
    echo ""
    echo "🔗 To create a test account first:"
    echo "   curl -X POST $PRODUCTION_URL/api/auth/register \\"
    echo "     -H 'Content-Type: application/json' \\"
    echo "     -d '{\"email\": \"$TEST_EMAIL\", \"password\": \"testpass123\", \"firstName\": \"Test\", \"lastName\": \"User\"}'"
    echo ""
    echo "📱 For React Native/Expo testing:"
    echo "   - Make sure your Expo app is running"
    echo "   - Configure deep linking as per REACT_NATIVE_DEEP_LINKING.md"
    echo "   - The reset link should open your app and navigate to reset screen"
else
    echo "❌ Password reset endpoint failed"
    echo "   Check the response above for error details"
fi 
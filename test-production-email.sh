#!/bin/bash

# Test Email Functionality with Production Backend
# This script tests the password reset email functionality

# Default production URL - update this with your actual URL
PRODUCTION_URL=${1:-"https://ascend-api-production.up.railway.app"}

echo "🧗 Testing Ascend Climbing Email Configuration (Production)"
echo "=========================================================="
echo "Backend URL: $PRODUCTION_URL"
echo ""

# Check if backend is accessible
echo "1. Checking if backend is accessible..."
if curl -s "$PRODUCTION_URL/health" > /dev/null; then
    echo "✅ Backend is accessible"
else
    echo "❌ Backend is not accessible. Please check the URL: $PRODUCTION_URL"
    exit 1
fi

# Get email from user
echo ""
echo "2. Enter your email address to test password reset:"
read -p "Email: " TEST_EMAIL

if [ -z "$TEST_EMAIL" ]; then
    echo "❌ Email address is required"
    exit 1
fi

# Test password reset endpoint
echo ""
echo "3. Testing password reset email endpoint..."
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
    echo "   - Reset code format: 6-digit code (e.g., 123456)"
    echo ""
    echo "🔍 Next steps:"
    echo "   1. Check your email inbox (and spam folder)"
    echo "   2. Look for email from noreply@ascendclimbing.xyz"
    echo "   3. Enter the 6-digit code in your app"
    echo "   4. Create your new password"
    echo ""
    echo "⚠️  Note: If you don't have a user account with this email,"
    echo "   the email won't be sent (for security reasons)"
else
    echo "❌ Password reset endpoint failed"
    echo "   Check the response above for error details"
fi 
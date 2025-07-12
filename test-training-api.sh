#!/bin/bash

# Training Plan API Test Script
# This script tests the training plan endpoints

# Configuration
API_BASE_URL="http://localhost:8080/api"
JWT_TOKEN=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "SUCCESS")
            echo -e "${GREEN}✓ $message${NC}"
            ;;
        "ERROR")
            echo -e "${RED}✗ $message${NC}"
            ;;
        "INFO")
            echo -e "${YELLOW}ℹ $message${NC}"
            ;;
    esac
}

# Function to make API requests
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    
    local headers=""
    if [ ! -z "$token" ]; then
        headers="-H 'Authorization: Bearer $token'"
    fi
    
    if [ ! -z "$data" ]; then
        headers="$headers -H 'Content-Type: application/json'"
        response=$(curl -s -w "\n%{http_code}" -X $method "$API_BASE_URL$endpoint" -d "$data" $headers)
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$API_BASE_URL$endpoint" $headers)
    fi
    
    # Extract status code (last line)
    status_code=$(echo "$response" | tail -n1)
    # Extract response body (all lines except last)
    body=$(echo "$response" | head -n -1)
    
    echo "$status_code|$body"
}

# Test 1: Get training templates (no auth required)
print_status "INFO" "Testing GET /api/training/templates"
result=$(make_request "GET" "/training/templates")
status_code=$(echo "$result" | cut -d'|' -f1)
body=$(echo "$result" | cut -d'|' -f2-)

if [ "$status_code" = "200" ]; then
    print_status "SUCCESS" "Training templates retrieved successfully"
    echo "Response: $body" | head -c 200
    echo "..."
else
    print_status "ERROR" "Failed to get training templates. Status: $status_code"
    echo "Response: $body"
fi

echo ""

# Test 2: Get training templates with difficulty filter
print_status "INFO" "Testing GET /api/training/templates?difficulty=BEGINNER"
result=$(make_request "GET" "/training/templates?difficulty=BEGINNER")
status_code=$(echo "$result" | cut -d'|' -f1)
body=$(echo "$result" | cut -d'|' -f2-)

if [ "$status_code" = "200" ]; then
    print_status "SUCCESS" "Training templates filtered by difficulty retrieved successfully"
    echo "Response: $body" | head -c 200
    echo "..."
else
    print_status "ERROR" "Failed to get filtered training templates. Status: $status_code"
    echo "Response: $body"
fi

echo ""

# Test 3: Try to start a plan without authentication (should fail)
print_status "INFO" "Testing POST /api/training/user-plans (without auth - should fail)"
result=$(make_request "POST" "/training/user-plans" '{"templateId":"test-uuid"}')
status_code=$(echo "$result" | cut -d'|' -f1)
body=$(echo "$result" | cut -d'|' -f2-)

if [ "$status_code" = "401" ] || [ "$status_code" = "403" ]; then
    print_status "SUCCESS" "Authentication required as expected. Status: $status_code"
else
    print_status "ERROR" "Expected authentication failure but got status: $status_code"
    echo "Response: $body"
fi

echo ""

# Test 4: Try to get user plans without authentication (should fail)
print_status "INFO" "Testing GET /api/training/user-plans (without auth - should fail)"
result=$(make_request "GET" "/training/user-plans")
status_code=$(echo "$result" | cut -d'|' -f1)
body=$(echo "$result" | cut -d'|' -f2-)

if [ "$status_code" = "401" ] || [ "$status_code" = "403" ]; then
    print_status "SUCCESS" "Authentication required as expected. Status: $status_code"
else
    print_status "ERROR" "Expected authentication failure but got status: $status_code"
    echo "Response: $body"
fi

echo ""

# Test 5: Try to get active plan without authentication (should fail)
print_status "INFO" "Testing GET /api/training/user-plans/active (without auth - should fail)"
result=$(make_request "GET" "/training/user-plans/active")
status_code=$(echo "$result" | cut -d'|' -f1)
body=$(echo "$result" | cut -d'|' -f2-)

if [ "$status_code" = "401" ] || [ "$status_code" = "403" ]; then
    print_status "SUCCESS" "Authentication required as expected. Status: $status_code"
else
    print_status "ERROR" "Expected authentication failure but got status: $status_code"
    echo "Response: $body"
fi

echo ""

print_status "INFO" "Basic API tests completed!"
print_status "INFO" "To test authenticated endpoints, you need to:"
print_status "INFO" "1. Register/login to get a JWT token"
print_status "INFO" "2. Set the JWT_TOKEN variable in this script"
print_status "INFO" "3. Run the authenticated tests"

echo ""

# If JWT token is provided, run authenticated tests
if [ ! -z "$JWT_TOKEN" ]; then
    print_status "INFO" "JWT token provided, running authenticated tests..."
    
    # Test 6: Get user plans with authentication
    print_status "INFO" "Testing GET /api/training/user-plans (with auth)"
    result=$(make_request "GET" "/training/user-plans" "" "$JWT_TOKEN")
    status_code=$(echo "$result" | cut -d'|' -f1)
    body=$(echo "$result" | cut -d'|' -f2-)

    if [ "$status_code" = "200" ]; then
        print_status "SUCCESS" "User plans retrieved successfully"
        echo "Response: $body" | head -c 200
        echo "..."
    else
        print_status "ERROR" "Failed to get user plans. Status: $status_code"
        echo "Response: $body"
    fi

    echo ""

    # Test 7: Get active plan with authentication
    print_status "INFO" "Testing GET /api/training/user-plans/active (with auth)"
    result=$(make_request "GET" "/training/user-plans/active" "" "$JWT_TOKEN")
    status_code=$(echo "$result" | cut -d'|' -f1)
    body=$(echo "$result" | cut -d'|' -f2-)

    if [ "$status_code" = "200" ] || [ "$status_code" = "404" ]; then
        print_status "SUCCESS" "Active plan check completed. Status: $status_code"
        if [ "$status_code" = "404" ]; then
            echo "No active plan found (expected for new users)"
        else
            echo "Response: $body" | head -c 200
            echo "..."
        fi
    else
        print_status "ERROR" "Failed to get active plan. Status: $status_code"
        echo "Response: $body"
    fi

    echo ""

    print_status "INFO" "Authenticated tests completed!"
else
    print_status "INFO" "To run authenticated tests, set JWT_TOKEN variable and run again"
fi

echo ""
print_status "INFO" "Test script completed!" 
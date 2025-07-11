#!/bin/bash

# Setup Environment Variables Script
echo "🧗 Setting up environment variables for Ascend Climbing API"
echo "=========================================================="

# Check if .env file already exists
if [ -f ".env" ]; then
    echo "⚠️  .env file already exists!"
    read -p "Do you want to overwrite it? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "Setup cancelled. Your existing .env file was preserved."
        exit 0
    fi
fi

# Create .env file from template
echo "📝 Creating .env file from template..."
cp env-template.txt .env

if [ $? -eq 0 ]; then
    echo "✅ .env file created successfully!"
    echo ""
    echo "📋 Your environment variables are now set up:"
    echo "   - MAILJET_API_KEY: 27ed38a02b4313f83e5f804394a4f273"
    echo "   - MAILJET_SECRET_KEY: 72d6fe739f72fc8d5a6a7a67dacbe811"
    echo "   - FRONTEND_URL: http://localhost:3000"
    echo ""
    echo "🔒 Security Note:"
    echo "   - The .env file is automatically ignored by git"
    echo "   - Never commit this file to version control"
    echo "   - For production, set these variables in your deployment platform"
    echo ""
    echo "🚀 You can now start your application:"
    echo "   ./gradlew bootRun"
    echo ""
    echo "🧪 To test the email functionality:"
    echo "   ./test-email.sh"
else
    echo "❌ Failed to create .env file"
    exit 1
fi 
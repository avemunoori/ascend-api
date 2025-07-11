# Mailjet Email Configuration Setup

## Environment Variables

Set the following environment variables in your deployment environment (Railway, local development, etc.):

```bash
# Mailjet API Credentials
MAILJET_API_KEY=27ed38a02b4313f83e5f804394a4f273
MAILJET_SECRET_KEY=72d6fe739f72fc8d5a6a7a67dacbe811

# Frontend URL (update this for production)
# For Expo development: exp://localhost:8081
# For Expo production: your-app-scheme://
FRONTEND_URL=exp://localhost:8081
```

## Local Development

For local development, you can create a `.env` file in your project root:

```bash
MAILJET_API_KEY=27ed38a02b4313f83e5f804394a4f273
MAILJET_SECRET_KEY=72d6fe739f72fc8d5a6a7a67dacbe811
FRONTEND_URL=exp://localhost:8081
```

## Railway Deployment

In your Railway dashboard, add these environment variables:

1. Go to your project settings
2. Navigate to the "Variables" tab
3. Add the following variables:
   - `MAILJET_API_KEY`: `27ed38a02b4313f83e5f804394a4f273`
   - `MAILJET_SECRET_KEY`: `72d6fe739f72fc8d5a6a7a67dacbe811`
   - `FRONTEND_URL`: Your production frontend URL

## Testing Email Functionality

### Option 1: Run the Integration Test
```bash
./gradlew test --tests "com.ascend.auth.MailjetIntegrationTest"
```

### Option 2: Test via API Endpoint
1. Start your application
2. Make a POST request to `/api/auth/forgot-password` with:
   ```json
   {
     "email": "your-test-email@example.com"
   }
   ```

## React Native/Expo Configuration

### Development Setup
- **Frontend URL**: `exp://localhost:8081` (Expo development server)
- **Password Reset Links**: Will open in your Expo app using deep linking

### Production Setup
- **Frontend URL**: `your-app-scheme://` (e.g., `ascend://`)
- **Deep Linking**: Configure your app to handle password reset links

### Deep Linking Configuration
In your React Native app, you'll need to handle the password reset links:

```javascript
// In your App.js or navigation setup
import { Linking } from 'react-native';

// Handle incoming links
Linking.addEventListener('url', handleDeepLink);

function handleDeepLink(event) {
  const { url } = event;
  if (url.includes('reset-password')) {
    // Extract token and navigate to reset password screen
    const token = url.split('token=')[1];
    // Navigate to your reset password screen with the token
  }
}
```

## Email Configuration Details

- **SMTP Host**: `in-v3.mailjet.com`
- **SMTP Port**: `587`
- **Protocol**: `SMTP with STARTTLS`
- **From Address**: `noreply@ascendclimbing.xyz`
- **Authentication**: API Key/Secret Key

## Security Features

✅ **Domain Verification**: Your domain `ascendclimbing.xyz` is verified with Mailjet
✅ **SPF Record**: Configured to allow Mailjet servers
✅ **DKIM**: Configured for email authentication
✅ **Retry Logic**: Automatic retry on temporary failures (3 attempts with exponential backoff)
✅ **Error Handling**: Comprehensive error handling for various failure scenarios

## Email Template Features

- **Responsive Design**: Works on desktop and mobile
- **Branded**: Uses Ascend Climbing branding
- **Security Warnings**: Clear security notices
- **Fallback Link**: Text link if button doesn't work
- **Professional Styling**: Modern gradient design

## Troubleshooting

### Common Issues:

1. **Authentication Failed**: Check that API key and secret are correct
2. **Connection Timeout**: Verify network connectivity to Mailjet servers
3. **Email Not Received**: Check spam folder and verify recipient email
4. **Domain Issues**: Ensure SPF and DKIM records are properly configured

### Logs to Check:
- Application logs for SMTP errors
- Mailjet dashboard for delivery status
- DNS records for domain verification 
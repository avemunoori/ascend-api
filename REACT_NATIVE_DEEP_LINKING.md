# React Native Deep Linking Setup

This guide explains how to set up deep linking in your React Native/Expo app to handle password reset links from the email service.

## Overview

When users click the password reset link in their email, it should open your React Native app and navigate to the password reset screen with the token.

## Development Setup (Expo)

### 1. Configure Expo Deep Linking

In your `app.json` or `app.config.js`:

```json
{
  "expo": {
    "scheme": "ascend",
    "android": {
      "intentFilters": [
        {
          "action": "VIEW",
          "autoVerify": true,
          "data": [
            {
              "scheme": "ascend"
            }
          ],
          "category": ["BROWSABLE", "DEFAULT"]
        }
      ]
    }
  }
}
```

### 2. Handle Deep Links in Your App

In your main App component or navigation setup:

```javascript
import React, { useEffect } from 'react';
import { Linking } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';

export default function App() {
  useEffect(() => {
    // Handle deep links when app is already running
    const handleDeepLink = (event) => {
      const { url } = event;
      handlePasswordResetLink(url);
    };

    // Handle deep links when app is opened from link
    const handleInitialURL = async () => {
      const initialURL = await Linking.getInitialURL();
      if (initialURL) {
        handlePasswordResetLink(initialURL);
      }
    };

    // Set up listeners
    Linking.addEventListener('url', handleDeepLink);
    handleInitialURL();

    // Cleanup
    return () => {
      Linking.removeAllListeners('url');
    };
  }, []);

  const handlePasswordResetLink = (url) => {
    if (url && url.includes('reset-password')) {
      const token = url.split('token=')[1];
      if (token) {
        // Navigate to your password reset screen
        // This depends on your navigation setup
        navigation.navigate('ResetPassword', { token });
      }
    }
  };

  return (
    <NavigationContainer>
      {/* Your app navigation */}
    </NavigationContainer>
  );
}
```

### 3. Password Reset Screen

Create a screen to handle the password reset:

```javascript
import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert } from 'react-native';

export default function ResetPasswordScreen({ route, navigation }) {
  const { token } = route.params;
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleResetPassword = async () => {
    if (newPassword !== confirmPassword) {
      Alert.alert('Error', 'Passwords do not match');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/reset-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          token: token,
          newPassword: newPassword,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        Alert.alert('Success', 'Password reset successfully', [
          { text: 'OK', onPress: () => navigation.navigate('Login') }
        ]);
      } else {
        Alert.alert('Error', data.message || 'Failed to reset password');
      }
    } catch (error) {
      Alert.alert('Error', 'Network error. Please try again.');
    }
  };

  return (
    <View style={{ flex: 1, padding: 20, justifyContent: 'center' }}>
      <Text style={{ fontSize: 24, fontWeight: 'bold', marginBottom: 20 }}>
        Reset Your Password
      </Text>
      
      <TextInput
        style={{ borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 10 }}
        placeholder="New Password"
        secureTextEntry
        value={newPassword}
        onChangeText={setNewPassword}
      />
      
      <TextInput
        style={{ borderWidth: 1, borderColor: '#ccc', padding: 10, marginBottom: 20 }}
        placeholder="Confirm Password"
        secureTextEntry
        value={confirmPassword}
        onChangeText={setConfirmPassword}
      />
      
      <TouchableOpacity
        style={{ backgroundColor: '#007AFF', padding: 15, borderRadius: 5 }}
        onPress={handleResetPassword}
      >
        <Text style={{ color: 'white', textAlign: 'center', fontWeight: 'bold' }}>
          Reset Password
        </Text>
      </TouchableOpacity>
    </View>
  );
}
```

## Production Setup

### 1. Update Environment Variables

In your production environment, update the `FRONTEND_URL`:

```bash
# For production with custom scheme
FRONTEND_URL=ascend://

# Or for universal links (iOS/Android)
FRONTEND_URL=https://your-domain.com
```

### 2. Configure Universal Links (Optional)

For better user experience, you can set up universal links:

1. **iOS**: Configure Associated Domains in your app
2. **Android**: Set up App Links with Digital Asset Links

## Testing Deep Links

### Development Testing

1. Start your Expo development server
2. Send a password reset email
3. Click the link in the email
4. The app should open and navigate to the reset password screen

### Manual Testing

You can test deep links manually:

```bash
# iOS Simulator
xcrun simctl openurl booted "ascend://reset-password?token=test123"

# Android Emulator
adb shell am start -W -a android.intent.action.VIEW -d "ascend://reset-password?token=test123" com.your.package
```

## Troubleshooting

### Common Issues

1. **Link doesn't open app**: Check your scheme configuration
2. **App opens but doesn't navigate**: Verify your deep link handling code
3. **Token not extracted**: Check the URL parsing logic

### Debug Tips

1. Add console logs to your deep link handler
2. Test with different URL formats
3. Verify your navigation setup
4. Check Expo logs for any errors

## Security Considerations

1. **Token Validation**: Always validate the token on your backend
2. **Link Expiration**: Tokens expire after 1 hour
3. **One-time Use**: Tokens can only be used once
4. **HTTPS**: Use HTTPS in production for secure communication 
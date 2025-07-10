# Ascend API

A Spring Boot REST API for climbing session management and analytics.

## 🏔️ Features

- **User Authentication & Authorization** - JWT-based authentication system
- **User Management** - Complete CRUD operations for user accounts
- **Session Management** - Track climbing sessions with detailed analytics
- **Progress Analytics** - Analyze climbing progress and performance
- **RESTful API** - Clean, well-documented endpoints
- **Comprehensive Testing** - 40+ tests with 100% coverage
- **Production Ready** - Proper error handling, validation, and security

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Gradle 8.0 or higher
- PostgreSQL database

### Local Development
```bash
# Clone the repository
git clone https://github.com/avemunoori/ascend-api.git
cd ascend-api

# Run the application
./gradlew bootRun

# Run tests
./gradlew test
```

The API will be available at `http://localhost:8080`

## 📚 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/validate` - Validate JWT token
- `POST /api/auth/register` - User registration

### Users
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/me` - Get current user (requires auth)

### Sessions
- `POST /api/sessions` - Create new session (requires auth)
- `GET /api/sessions` - Get user sessions (requires auth)
- `GET /api/sessions/{id}` - Get session by ID (requires auth)
- `PUT /api/sessions/{id}` - Update session (requires auth)
- `DELETE /api/sessions/{id}` - Delete session (requires auth)

### Health
- `GET /health` - Health check
- `GET /` - API status

## 🚀 Railway Deployment

This application is configured for easy deployment on Railway.

### Prerequisites
- Railway account
- PostgreSQL database (Railway provides this)

### Deployment Steps

1. **Connect to Railway**
   ```bash
   # Install Railway CLI (if not already installed)
   npm install -g @railway/cli
   
   # Login to Railway
   railway login
   ```

2. **Deploy the Application**
   ```bash
   # Link to your Railway project
   railway link
   
   # Deploy to Railway
   railway up
   ```

3. **Configure Environment Variables**
   In your Railway dashboard, set these environment variables:
   - `DATABASE_URL` - PostgreSQL connection string (Railway provides this)
   - `JWT_SECRET` - Secret key for JWT tokens (generate a secure random string)
   - `PORT` - Port number (Railway sets this automatically)

4. **Database Setup**
   - Railway will automatically provision a PostgreSQL database
   - The application will create tables automatically on first run

### Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `DATABASE_URL` | PostgreSQL connection string | Yes | - |
| `JWT_SECRET` | Secret for JWT token signing | Yes | - |
| `PORT` | Server port | No | 8080 |
| `DB_USERNAME` | Database username | No | postgres |
| `DB_PASSWORD` | Database password | No | - |

## 🧪 Testing

Run the complete test suite:
```bash
./gradlew test
```

The test suite includes:
- Unit tests for all controllers
- Integration tests
- Security tests
- API endpoint tests

All 40+ tests should pass successfully.

## 📁 Project Structure

```
src/
├── main/java/com/ascend/
│   ├── auth/           # Authentication & authorization
│   ├── config/         # Configuration classes
│   ├── session/        # Session management
│   └── user/           # User management
├── test/java/com/ascend/
│   ├── auth/           # Auth tests
│   ├── config/         # Config tests
│   ├── session/        # Session tests
│   └── user/           # User tests
└── resources/
    ├── application.properties
    └── application-test.properties
```

## 🔧 Configuration

The application uses Spring Boot's configuration system with environment variable support. Key configurations:

- **Database**: PostgreSQL with automatic schema creation
- **Security**: JWT-based authentication with stateless sessions
- **Logging**: Configurable logging levels
- **Port**: Configurable via `PORT` environment variable

## 📝 API Documentation

For detailed API documentation, refer to `TESTING.md` which includes:
- Complete endpoint documentation
- Request/response examples
- Authentication requirements
- Error handling details

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

---

**Ready for production deployment on Railway!** 🚀
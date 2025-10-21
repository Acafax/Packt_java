# Packt - Social Trip Planning Platform

## 📋 Project Description

Packt is a comprehensive social application designed for organizing and managing group trips. The platform enables users to connect, form travel groups, plan trips together, share photos and memories, track shared expenses, and create and schedule travel events. Built with Spring Boot and secured with Firebase Authentication, it provides a complete solution for collaborative trip planning and management.
## 🚀 Key Features

- **User Management**: Registration, authentication, and profile management
- **Group Trip Management**: Create and manage travel groups with multiple participants
- **Trip Planning**: Organize and plan group trips and excursions
- **Event Management**: Create a schedule to manage travel events and activities
- **Expense Tracking**: Track and split shared travel expenses within groups
- **Photo Gallery**: Upload, store, and share trip photos and memories
- **Invitation System**: Invite users to join travel groups
- **Document Management**: Attach documents, tickets, and travel-related files
- **Real-time Monitoring**: Prometheus metrics and Spring Boot Actuator endpoints

## 🛠️ Technology Stack

### Backend Framework
- **Spring Boot 3.4.3** - Main application framework
- **Java 23** - Programming language
- **Spring Data JPA** - Data persistence layer
- **Spring Security** - Security framework
- **Spring Web** - REST web services

### Database
- **PostgreSQL** - Primary database
- **Hibernate** - ORM framework

### Authentication & Security
- **Firebase Admin SDK 9.2.0** - User authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Custom JWT Filter** - Firebase token validation

### Additional Technologies
- **Lombok** - Boilerplate code reduction
- **Docker** - Containerization
- **Prometheus** - Metrics and monitoring
- **Spring Boot Actuator** - Application monitoring and management

### Testing
- **JUnit 5** - Unit testing framework
- **Testcontainers** - Integration testing with PostgreSQL containers
- **MockMvc** - REST API testing

## 🏗️ Architecture

### Controller Layer
The application follows a REST architecture with the following controllers:

- **UserController** (`/user/**`) - User management and registration
- **GroupController** (`/group/**`) - Group CRUD operations
- **ExpensesController** (`/budget/**`) - Expense management
- **InvitationController** (`/invitation/**`) - Group invitation system
- **EventController** (`/event/**`) - Event management
- **DocumentController** (`/doc/**`) - Document handling
- **PhotoController** (`/photo/**`) - Photo storage and retrieval
- **ProfilePhotoController** (`/profile/**`) - Profile photo management
- **UserGroupController** - User-group relationship management

### Service Layer
Business logic is encapsulated in service classes:
- `UserService` - User operations
- `GroupService` - Group operations
- `ExpenseService` - Expense operations
- `InvitationService` - Invitation handling
- `SecurityService` - Authorization checks
- `EventService` - Event management
- `DocumentService` - Document management
- `PhotoService` - Photo management

### Repository Layer
Data access through Spring Data JPA repositories:
- `UserRepository`
- `GroupRepository`
- `ExpensesRepository`
- `InvitationRepository`
- And more specialized repositories

### Security Architecture
```
Client Request → JWT Filter → Firebase Token Validation → Spring Security → Controller
```

## 🔐 Authentication & Authorization

### Firebase Authentication
The application uses Firebase Authentication for secure user management:

1. **Token-based Authentication**: Stateless JWT tokens from Firebase
2. **Custom JWT Filter**: `JwtAuthenticationFilter` validates Firebase tokens
3. **Session Management**: Stateless sessions (no server-side sessions)

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Stateless session management
    // Firebase JWT filter before UsernamePasswordAuthenticationFilter
    // Public endpoints: /user/register, /public/**, /error
    // All other endpoints require authentication
}
```

### Authorization Rules
- **Public Access**: User registration endpoint
- **Authenticated Access**: All other endpoints require valid Firebase token
- **Role-based Access**: Admin roles for group management
- **Resource-level Security**: Custom `@PreAuthorize` annotations using `SecurityService`

### SecurityService Methods
- `isGroupMember()` - Check if user belongs to group
- `isGroupAdministrator()` - Check if user is group admin
- `isExpenseCreator()` - Check if user created expense
- `isRequestingUserisAuthorizedForAccount()` - Check user access rights

## 📦 Data Models

### Core Entities
- **User**: User accounts with Firebase UID
- **Group**: Trip groups with members
- **Expense**: Individual expenses within groups
- **Invitation**: Group invitations
- **Event**: Events associated with expenses
- **Document**: File attachments
- **Photo**: Image storage


## 🚦 Getting Started

### Prerequisites
- Java 23 or higher
- PostgreSQL database
- Firebase project with Admin SDK credentials
- Maven 3.8+
- Docker (optional)
- **`.env` file** with database credentials (required for Docker deployment)

> **⚠️ IMPORTANT**: The application **will not start** without a valid Firebase Admin SDK configuration file. You must create a Firebase project and download the `adminsdk.json` file before running the application. See Firebase Configuration section below.

### Environment Setup

#### 1. Create `.env` File
Create a `.env` file in the project root directory with the following content:

```bash
# Database Configuration
DB_USERNAME=your_username_here
DB_PASSWORD=your_password_here
```

**Example `.env` file:**
```bash
DB_USERNAME=postgres
DB_PASSWORD=password123
```


#### 2. Environment Variables
The application uses the following environment variables:

**For Docker Deployment (from .env file):**
```bash
DB_USERNAME=postgres          # Database username
DB_PASSWORD=your_password     # Database password
```

**Auto-configured by Docker Compose:**
```bash
DB_PORT=postgres:5432        # Database host and port (Docker internal network)
DB_NAME=projektZespolowy     # Database name
```

### Firebase Configuration
Place your Firebase Admin SDK JSON file at:
```
src/main/resources/packt-firebase-adminsdk.json
```

**How to obtain Firebase Admin SDK credentials:**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Navigate to Project Settings → Service Accounts
4. Click "Generate New Private Key"
5. Save the downloaded JSON file as `packt-firebase-adminsdk.json`
6. Place it in `src/main/resources/` directory

**Without this file, the application will fail to start with initialization errors.**

### Running the Application


#### Using Docker
```bash
docker-compose up -d
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserControllerIntegrationTest

# Run with coverage
mvn clean test jacoco:report
```

## 📊 API Endpoints

### User Endpoints
- `GET /user/all` - Get all users
- `GET /user/{UId}` - Get user by Firebase UID
- `GET /user/{UId}/groups` - Get user with groups
- `GET /user/{UId}/groups/details` - Get user with group details
- `POST /user/register` - Register new user
- `PUT /user/update` - Update user
- `PATCH /user/patch/{UId}` - Partial user update
- `DELETE /user/{UId}` - Delete user

### Group Endpoints
- `GET /group/all` - Get all groups
- `GET /group/{id}` - Get group by ID
- `GET /group/all/with-users/{groupId}` - Get group with users
- `GET /group/{groupId}/details` - Get group details
- `POST /group/create` - Create new group
- `PUT /group/update` - Update group
- `PATCH /group/patch/{groupId}` - Partial group update
- `DELETE /group/{id}` - Delete group

### Expense Endpoints
- `GET /budget/{groupId}` - Get expenses for group
- `GET /budget/{groupId}/{expId}` - Get expense by ID
- `POST /budget/{groupId}` - Create expense
- `PUT /budget` - Update expense
- `DELETE /budget/{expId}` - Delete expense

### Invitation Endpoints
- `POST /invitation/invite/{email}/{groupId}` - Invite user to group
- `GET /invitation/{UId}` - Get user invitations
- `DELETE /invitation/{UId}/{groupId}` - Delete invitation

### Monitoring Endpoints
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus metrics

## 🧪 Testing

The project includes comprehensive integration tests:

### Test Structure
- **UserControllerIntegrationTest** - User endpoint tests
- **GroupControllerIntegrationTest** - Group endpoint tests
- **ExpensesControllerIntegrationTest** - Expense endpoint tests
- **InvitationControllerIntegrationTest** - Invitation endpoint tests

### Test Configuration
- **Testcontainers**: PostgreSQL containers for integration tests
- **Test Profiles**: Separate configuration for testing
- **Test Security Config**: Mock authentication for tests
- **Test Data**: SQL scripts for test data initialization

### Running Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=GroupControllerIntegrationTest

# Integration tests only
mvn verify
```

## 📁 Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── org/example/springprojektzespolowy/
│   │       ├── config/              # Configuration classes
│   │       │   ├── SecurityConfig.java
│   │       │   └── firebase/        # Firebase configuration
│   │       ├── controllers/         # REST controllers
│   │       ├── services/           # Business logic
│   │       ├── repositories/       # Data access layer
│   │       ├── models/             # Entity classes
│   │       └── dto/                # Data Transfer Objects
│   └── resources/
│       └── application.properties
└── test/
    ├── java/
    │   └── org/example/springprojektzespolowy/
    │       ├── controllers/        # Integration tests
    │       ├── config/             # Test configuration
    │       └── services/           # Test security service
    └── resources/
        ├── application-test.properties
        └── data/                   # Test data SQL scripts
```

## 🔧 Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://${DB_PORT:localhost:5432}/${DB_NAME:projektZespolowy}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:password}
spring.jpa.hibernate.ddl-auto=update
```

### Security Configuration
- Stateless session management
- JWT token validation via Firebase
- CSRF disabled for REST API
- Public endpoints: `/user/register`, `/public/**`

### Actuator Configuration
```properties
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
management.endpoint.prometheus.enabled=true
```

## 🐳 Docker Support

### Docker Compose
```bash
docker-compose up -build
```

This starts:
- Application container
- PostgreSQL database
- Prometheus (optional)

## 📈 Monitoring

### Prometheus Metrics
Available at: `/actuator/prometheus`

### Health Check
Available at: `/actuator/health`

### Application Metrics
- JVM metrics
- HTTP request metrics
- Database connection pool metrics
- Custom business metrics

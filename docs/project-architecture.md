# Project Architecture

## Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL with Spring Data JPA
- **Build Tool**: Gradle
- **Java Version**: 17
- **Additional**: Lombok for code generation, MapStruct for object mapping, Logback for logging, Logstash for JSON logging

## Project Structure

```plain
src/main/java/com/reythecoder/organization/
├── OrganizationServiceApplication.java          # Main application class
├── config/                                     # Configuration classes
│   ├── DatabaseConfig.java                    # Database configuration
│   ├── SecurityConfig.java                    # Security configuration
│   └── SwaggerConfig.java                     # API documentation config
├── controller/                                # REST controllers
│   ├── OrganizationController.java            # Organization endpoints
│   └── dto/                                   # DTO classes
│       ├── request/                           # Request DTOs
│       │   ├── CreateOrganizationRequest.java
│       │   └── UpdateOrganizationRequest.java
│       └── response/                          # Response DTOs
│           ├── OrganizationResponse.java
│           └── ErrorResponse.java
├── service/                                   # Business logic layer
│   ├── OrganizationService.java                # Service interface
│   └── impl/                                  # Service implementations
│       └── OrganizationServiceImpl.java
├── repository/                                # Data access layer
│   └── OrganizationRepository.java            # JPA repository
├── entity/                                    # JPA entities
│   └── Organization.java                      # Entity class
├── mapper/                                    # MapStruct mappers
│   └── OrganizationMapper.java                # Entity-DTO mapper
├── exception/                                 # Custom exceptions
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java            # Global exception handler
└── utils/                                     # Utility classes
    └── DateUtils.java

src/test/java/com/reythecoder/organization/
├── controller/                                # Controller tests
│   └── OrganizationControllerTest.java
├── service/                                   # Service tests
│   └── OrganizationServiceTest.java
├── repository/                                # Repository tests
│   └── OrganizationRepositoryTest.java
└── integration/                               # Integration tests
    └── OrganizationIntegrationTest.java

src/main/resources/
├── application.yml                            # Main configuration with common settings
├── application-local.yml                      # Local development profile with environment-specific settings
├── application-dev.yml                        # Development profile with environment-specific settings
└── application-prod.yml                       # Production profile with environment-specific settings

db/
└── migration/                             # Database migration files
    └── V1__Create_organization_table.sql

./
├── .env                                       # Environment variables for local development (not committed)
└── .env.example                               # Template for environment variables
```

## Configuration Files

The application supports multiple configuration profiles:

- `application.yml`: Base configuration
- `application-local.yml`: Local development configuration
- `application-dev.yml`: Development environment configuration (to be created)
- `application-prod.yml`: Production environment configuration (to be created)

Environment variables can be set in:

- `.env`: Local development environment variables
- `.env.example`: Template for environment variables

## API Endpoints

(TBD - Endpoints will be documented as they are implemented)

## Logging

The application uses Logback for logging with the following configuration:

- Console and file logging enabled
- Log files are stored in the `logs/` directory
- Rolling policy: 10MB max file size, 30 days retention
- JSON logging support via Logstash encoder
- Profile-specific logging configurations (local, dev, prod)

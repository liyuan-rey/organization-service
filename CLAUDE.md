# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application for Organization Management Service, named "organization-service". The project uses Java 17 with Gradle build system and PostgreSQL database.

## Build and Development Commands

### Essential Commands
- `./gradlew build` - Build the project
- `./gradlew test` - Run all tests
- `./gradlew bootRun` - Start the application
- `./gradlew clean` - Clean build artifacts
- `./gradlew check` - Run all checks including tests

### Testing
- `./gradlew test --tests *SpecificTestClass` - Run specific test class
- `./gradlew test --info` - Run tests with detailed output

## Project Architecture

### Technology Stack
- **Framework**: Spring Boot 3.5.4
- **Database**: PostgreSQL with Spring Data JPA
- **Build Tool**: Gradle
- **Java Version**: 17
- **Additional**: Lombok for code generation, MapStruct for object mapping

### Package Structure
```
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
├── application.yml                            # Main configuration
├── application-dev.yml                        # Development profile
├── application-prod.yml                       # Production profile
└── db/
    └── migration/                             # Database migration files
        └── V1__Create_organization_table.sql
```

### Current State
The project is in early development stage with basic Spring Boot structure. The main application class is `OrganizationServiceApplication.java` and basic test structure is in place.

## Configuration Notes

### Database Configuration
The application is configured for PostgreSQL but connection details need to be added to `application.yml`. You'll need to configure:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`

## Development Guidelines

### Entity and Repository Pattern
When adding new domain objects:
1. Create JPA entities in the `entity` package
2. Create Spring Data JPA repositories in the `repository` package extending `JpaRepository`
3. Use Lombok annotations to reduce boilerplate code
4. Follow naming conventions: Entity names should be singular, Repository names should be plural

### Object Mapping with MapStruct
Use MapStruct for mapping between entity and DTO objects:
1. Create mapper interfaces with `@Mapper` annotation in the `mapper` package
2. Add `componentModel = "spring"` for Spring dependency injection
3. Use `@Mapping` annotation for custom field mappings
4. Separate request and response DTOs in `controller/dto/request` and `controller/dto/response`

### REST API Structure
Follow Spring Boot conventions for REST controllers:
- Use `@RestController` for API endpoints in the `controller` package
- Implement service layer for business logic (interface in `service`, implementation in `service/impl`)
- Use proper HTTP status codes and error handling
- Implement global exception handling in `exception/GlobalExceptionHandler.java`
- Create separate DTOs for requests and responses

### Service Layer Architecture
- Define service interfaces in the `service` package
- Implement services in `service/impl` package
- Use `@Service` annotation for service implementations
- Inject repositories and mappers via constructor injection
- Follow business logic separation: controllers handle HTTP, services handle business logic

### Testing Strategy
- Use `@SpringBootTest` for integration tests in `integration` package
- Use `@WebMvcTest` for controller layer tests in `controller` test package
- Use `@DataJpaTest` for repository layer tests in `repository` test package
- Use `@MockBean` for mocking dependencies in unit tests
- Test both happy path and error scenarios

### Configuration Management
- Place configuration classes in the `config` package
- Use profile-specific configurations (`application-{profile}.yml`)
- Separate database, security, and other configurations
- Use `@ConfigurationProperties` for external configuration

### Exception Handling
- Create custom exceptions in the `exception` package
- Implement `@ControllerAdvice` for global exception handling
- Use appropriate HTTP status codes for different error types
- Provide consistent error response format
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
- **Additional**: Lombok for code generation, MapStruct for object mapping, Logback for logging, Logstash for JSON logging

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

### Logging Best Practices
Follow comprehensive logging strategy throughout the application:

#### Logging Configuration
- Use Logback as the logging framework (default in Spring Boot)
- Configure logging in `logback-spring.xml` for advanced features
- Use profile-specific logging configurations
- Implement both console and file logging
- Add JSON logging for centralized log management

#### Logging Levels
- **ERROR**: Critical errors that require immediate attention
- **WARN**: Unexpected situations that don't stop the application
- **INFO**: Important business events and application lifecycle
- **DEBUG**: Detailed information for troubleshooting
- **TRACE**: Very detailed information for development

#### Logger Usage
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationServiceImpl {
    
    public void createOrganization(Organization organization) {
        log.info("Creating organization: {}", organization.getName());
        try {
            // Business logic
            log.debug("Organization created successfully with ID: {}", organization.getId());
        } catch (Exception e) {
            log.error("Failed to create organization: {}", organization.getName(), e);
            throw e;
        }
    }
}
```

#### Logging Patterns
- Use structured logging with consistent message formats
- Include relevant context in log messages
- Log at appropriate entry/exit points
- Log exceptions with stack traces
- Use parameterized logging for performance

#### Performance Logging
- Log slow operations with timing information
- Monitor database query performance
- Track external API calls
- Log business metrics and KPIs

#### Security Logging
- Log authentication attempts (success/failure)
- Log authorization decisions
- Audit sensitive operations
- Don't log sensitive data (passwords, tokens, PII)

#### Environment-Specific Logging
- Development: DEBUG level with detailed logging
- Testing: INFO level with essential logging
- Production: WARN level with structured logging
- Use Spring profiles for environment-specific configuration

### Externalized Version Properties

The project uses externalized version properties defined in `gradle.properties` for centralized dependency management.

#### Version Property Structure
```properties
### organization-service 自定义配置 ###

### 版本约定 ###
javaVersion=17

# Spring Boot
springBootVersion=3.5.4
springDependencyManagementVersion=1.1.7
lombokPluginVersion=8.14

# External dependencies
mapstructVersion=1.6.3
logstashLogbackEncoderVersion=7.4

### 项目配置 ###
defaultProjectGroup=com.reythecoder
defaultProjectVersion=0.0.1-SNAPSHOT
```

#### Using Version Properties in Build Files
In `build.gradle`, reference properties using `${property.name}` syntax:

```gradle
dependencies {
    // Spring Boot managed dependencies
    implementation 'org.springframework.boot:spring-boot-starter-web'
    
    // External dependencies with version properties
    implementation "org.mapstruct:mapstruct:${mapstructVersion}"
    annotationProcessor "org.mapstruct:mapstruct-processor:${mapstructVersion}"
    
    // Logstash encoder
    implementation 'net.logstash.logback:logstash-logback-encoder:${logstashLogbackEncoderVersion}'
}

// Use project properties
group = "${defaultProjectGroup}"
version = "${defaultProjectVersion}"
```

#### Version Management Guidelines
1. **Spring Boot Dependencies**: Use Spring Boot's dependency management when possible
2. **External Dependencies**: Define version properties for dependencies not managed by Spring Boot
3. **Naming Convention**: Use `Version` suffix for version properties (e.g., `mapstructVersion`)
4. **Group Related Properties**: Comment sections clearly (e.g., `# Spring Boot`, `# External dependencies`)
5. **Chinese Comments**: The project uses Chinese comments for internal documentation

#### Adding New Dependencies
1. Check if the dependency is managed by Spring Boot BOM
2. If not managed, add version property to `gradle.properties`
3. Reference the property in `build.gradle` using `${propertyName}`
4. Update documentation in the appropriate section

#### Repository Configuration
The project uses mirror repositories for faster dependency resolution:
- Aliyun Maven Repository: `https://maven.aliyun.com/repository/public`
- Tencent Maven Repository: `https://mirrors.cloud.tencent.com/maven/`

#### Build Performance Optimization
Gradle performance settings in `gradle.properties`:
- Parallel builds enabled: `org.gradle.parallel=true`
- Daemon enabled: `org.gradle.daemon=true`
- Build cache enabled: `org.gradle.caching=true`
- JVM memory optimized: `org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8`

### Git Commit Message Best Practices

Follow the Conventional Commits specification for clear, consistent commit messages:

#### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Commit Types
- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Code style changes (formatting, missing semi-colons, etc.)
- **refactor**: Code refactoring (neither bug fix nor feature)
- **perf**: Performance improvements
- **test**: Adding or modifying tests
- **chore**: Maintenance tasks, build process, auxiliary tools, libraries

#### Subject Line Rules
- Use imperative mood: "Add feature" not "Added feature"
- Keep under 50 characters
- Capitalize first letter
- No period at the end
- Include scope when applicable: `feat(controller): Add organization endpoint`

#### Body Guidelines
- Wrap at 72 characters
- Explain what and why, not how
- Use bullet points for multiple changes
- Include motivation and context

#### Breaking Changes
- Add `BREAKING CHANGE:` footer with description
- Use `feat` type with `!` after type/scope: `feat(api)!: Remove deprecated endpoint`

#### Examples
**Good:**
```
feat(service): Add organization creation functionality

- Implement createOrganization method with validation
- Add business logic for duplicate name checking
- Integrate with database repository layer

Closes #123
```

**Bad:**
```
Added organization creation
fixed some bugs
updated dependencies
```

#### Footer Usage
- Reference issues: `Closes #123`, `Fixes #456`
- Add breaking change notices
- Include co-authorship for collaborative work
- Add sign-off when required

#### Commit Message Workflow
1. Stage changes with `git add`
2. Create commit with clear message
3. Verify with `git log --oneline -5`
4. Push to remote when ready

🤖 Generated commits should include attribution line:
```
🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>
```
# Development Guidelines

## Code Style

- Follow standard Java naming conventions
- Use Lombok to reduce boilerplate code
- Use MapStruct for entity-DTO mapping

## Entity and Repository Pattern

When adding new domain objects:

1. Create JPA entities in the `entity` package
2. Create Spring Data JPA repositories in the `repository` package extending `JpaRepository`
3. Use Lombok annotations to reduce boilerplate code
4. Follow naming conventions: Entity names should be singular, Repository names should be plural

## Object Mapping with MapStruct

Use MapStruct for mapping between entity and DTO objects:

1. Create mapper interfaces with `@Mapper` annotation in the `mapper` package
2. Add `componentModel = "spring"` for Spring dependency injection
3. Use `@Mapping` annotation for custom field mappings
4. Separate request and response DTOs in `controller/dto/request` and `controller/dto/response`

## REST API Structure

Follow Spring Boot conventions for REST controllers:

- Use `@RestController` for API endpoints in the `controller` package
- Implement service layer for business logic (interface in `service`, implementation in `service/impl`)
- Create separate DTOs for requests and responses

## Service Layer Architecture

- Define service interfaces in the `service` package
- Implement services in `service/impl` package
- Use `@Service` annotation for service implementations
- Inject repositories and mappers via constructor injection
- Follow business logic separation: controllers handle HTTP, services handle business logic

## Testing Strategy

- Use `@SpringBootTest` for integration tests in `integration` package
- Use `@WebMvcTest` for controller layer tests in `controller` test package
- Use `@DataJpaTest` for repository layer tests in `repository` test package
- Use `@MockBean` for mocking dependencies in unit tests
- Test both happy path and error scenarios

## Configuration Management

- Follow Spring Boot best practices for multi-environment configuration:
  - Keep common configurations in `application.yml`
  - Place environment-specific configurations in `application-{profile}.yml`
    - `application-local.yml` for local development
    - `application-dev.yml` for development environment
    - `application-prod.yml` for production environment
  - Use environment variables for sensitive values like database credentials
  - Leverage Spring Boot's configuration hierarchy where profile-specific configs override default configs
- Manage sensitive configuration values:
  - Store sensitive values in environment variables, not in configuration files
  - Use `.env` files for local development only
  - Never commit sensitive values to version control
  - Use `.env.example` as a template for required environment variables
- Place configuration classes in the `config` package
- Separate database, security, and other configurations
- Use `@ConfigurationProperties` for external configuration

### Database Configuration

The application is configured for PostgreSQL with a multi-environment approach:

- Common database settings (driver, connection pool) are configured in `application.yml`
- Environment-specific connection details (url, username, password) are configured in profile-specific files:
- Sensitive values (username, password) are injected via environment variables

## Exception Handling

- Create custom exceptions in the `exception` package
- Implement global exception handling in `exception/GlobalExceptionHandler.java`
- Implement `@ControllerAdvice` for global exception handling
- Use appropriate HTTP status codes for different error types
- Provide consistent error response format

## Logging Best Practices

Follow comprehensive logging strategy throughout the application:

### Logging Configuration

- Use Logback as the logging framework (default in Spring Boot)
- Configure logging in `logback-spring.xml` for advanced features
- Use profile-specific logging configurations
- Implement both console and file logging
- Add JSON logging for centralized log management

### Logging Levels

- **ERROR**: Critical errors that require immediate attention
- **WARN**: Unexpected situations that don't stop the application
- **INFO**: Important business events and application lifecycle
- **DEBUG**: Detailed information for troubleshooting
- **TRACE**: Very detailed information for development

### Logger Usage

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

### Logging Patterns

- Use structured logging with consistent message formats
- Include relevant context in log messages
- Log at appropriate entry/exit points
- Log exceptions with stack traces
- Use parameterized logging for performance

### Performance Logging

- Log slow operations with timing information
- Monitor database query performance
- Track external API calls
- Log business metrics and KPIs

### Security Logging

- Log authentication attempts (success/failure)
- Log authorization decisions
- Audit sensitive operations
- Don't log sensitive data (passwords, tokens, PII)

### Environment-Specific Logging

- Development: DEBUG level with detailed logging
- Testing: INFO level with essential logging
- Production: WARN level with structured logging
- Use Spring profiles for environment-specific configuration

## Externalized Version Properties

The project uses externalized version properties defined in `gradle.properties` for centralized dependency management.

### Version Property Structure

```conf
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

### Using Version Properties in Build Files

In `build.gradle`, reference properties using `${property.name}` syntax:

```groovy
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

### Version Management Guidelines

1. **Spring Boot Dependencies**: Use Spring Boot's dependency management when possible
2. **External Dependencies**: Define version properties for dependencies not managed by Spring Boot
3. **Naming Convention**: Use `Version` suffix for version properties (e.g., `mapstructVersion`)
4. **Group Related Properties**: Comment sections clearly (e.g., `# Spring Boot`, `# External dependencies`)

### Adding New Dependencies

1. Check if the dependency is managed by Spring Boot BOM
2. If not managed, add version property to `gradle.properties`
3. Reference the property in `build.gradle` using `${propertyName}`
4. Update documentation in the appropriate section

### Repository Configuration

The project uses mirror repositories for faster dependency resolution:

- Aliyun Maven Repository: `https://maven.aliyun.com/repository/public`
- Tencent Maven Repository: `https://mirrors.cloud.tencent.com/maven/`

### Build Performance Optimization

Gradle performance settings in `gradle.properties`:

- Parallel builds enabled: `org.gradle.parallel=true`
- Daemon enabled: `org.gradle.daemon=true`
- Build cache enabled: `org.gradle.caching=true`
- JVM memory optimized: `org.gradle.jvmargs=-Xmx2g -XX:MaxMetaspaceSize=512m -Dfile.encoding=UTF-8`

# Organization Service

A Spring Boot application for Organization Management Service.

## Tech Stack

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **Additional**: Lombok, MapStruct, Logback, Logstash

## Getting Started

### Prerequisites

- Java 17
- PostgreSQL database
- Gradle

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```bash
   cd organization-service
   ```

3. Configure the database in `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/organization_db
       username: your_username
       password: your_password
   ```

4. Build the project:
   ```bash
   ./gradlew build
   ```

### Running the Application

Start the application using Gradle:
```bash
./gradlew bootRun
```

The application will start on port 8080.

### Testing

Run all tests:
```bash
./gradlew test
```

## Project Structure

```
src/main/java/com/reythecoder/organization/
├── OrganizationServiceApplication.java  # Main application class
├── config/                             # Configuration classes
├── controller/                         # REST controllers
├── service/                            # Business logic layer
├── repository/                         # Data access layer
├── entity/                             # JPA entities
├── mapper/                             # MapStruct mappers
├── exception/                          # Custom exceptions
└── utils/                              # Utility classes
```

## API Endpoints

(TBD - Endpoints will be documented as they are implemented)

## Logging

The application uses Logback for logging with the following configuration:

- Console and file logging enabled
- Log files are stored in the `logs/` directory
- Rolling policy: 10MB max file size, 30 days retention
- JSON logging support via Logstash encoder

## Development

### Code Style

- Follow standard Java naming conventions
- Use Lombok to reduce boilerplate code
- Use MapStruct for entity-DTO mapping

### Build Process

- Use `./gradlew build` to compile and package the application
- Use `./gradlew test` to run tests
- Use `./gradlew check` to run all quality checks

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
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
- Gradle
- Docker (optional, for local development environment)

### Environment Configuration

The application supports environment-specific configurations using `.env` files:

1. Copy the example environment file:

   ```bash
   cp .env.example .env.local
   ```

2. Customize the values in `.env.local` for your local development environment.

### Installation

1. Clone the repository:

   ```bash
   git clone <repository-url>
   ```

2. Navigate to the project directory:

   ```bash
   cd organization-service
   ```

3. Configure environment variables by copying `.env.example` to `.env.local` and modifying as needed.

4. (Optional) Start the Docker PostgreSQL environment:

   ```bash
   docker-compose up -d
   ```

   The database will be available at `localhost:5432` with:
   - Database: `organization_db`
   - Username: `postgres`
   - Password: `postgres`

5. Build the project:

   ```bash
   ./gradlew build
   ```

### Running the Application

Start the application using Gradle:

```bash
./gradlew bootRun
```

To run with a specific profile:

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

The application will start on port 8080.

### Running with Docker Environment

To run the application with the Docker PostgreSQL environment:

1. Start the PostgreSQL database:

   ```bash
   docker-compose up -d
   ```

2. The database will automatically initialize with the scripts in the `./db/init-scripts` directory.

3. Start the application:

   ```bash
   ./gradlew bootRun
   ```

4. To stop the Docker environment:

   ```bash
   docker-compose down
   ```

### Testing

Run all tests:

```bash
./gradlew test
```

## Project Structure

```plain
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

## Configuration Files

The application supports multiple configuration profiles:

- `application.yml`: Base configuration
- `application-local.yml`: Local development configuration
- `application-dev.yml`: Development environment configuration (to be created)
- `application-prod.yml`: Production environment configuration (to be created)

Environment variables can be set in:

- `.env.local`: Local development environment variables
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

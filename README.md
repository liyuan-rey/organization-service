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

### Build

1. Clone the repository:

   ```bash
   git clone <repository-url>
   ```

2. Navigate to the project directory:

   ```bash
   cd organization-service
   ```

3. Configure environment variables by copying `.env.example` to `.env` and modifying as needed.

   ```bash
   cp .env.example .env

   # You can customize the values in `.env` for your local development environment.
   ```

4. Build the project:

   ```bash
   ./gradlew build
   ```

### Running with Docker Environment

To run the application with the Docker PostgreSQL environment:

1. Start the Docker PostgreSQL environment:

   ```bash
   docker-compose up -d
   ```

   The database will be available at `localhost:5432` with:
   - Database: `organization_db`
   - Username: `postgres`
   - Password: `postgres`

2. The database will automatically initialize with the scripts in the `./db/init-scripts` directory.

3. Start the application using Gradle:

   ```bash
   ./gradlew bootRun
   ```

   To run with a specific profile:

   ```bash
   SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
   ```

   The application will start on port 8080.

4. To stop the Docker environment:

   ```bash
   docker-compose down
   ```

## Project Architecture

See [Project Architecture](docs/project-architecture.md)

## Development

### Code Style

- Follow standard Java naming conventions
- Use Lombok to reduce boilerplate code
- Use MapStruct for entity-DTO mapping

### Essential Commands

- `./gradlew build` - Build and package the application
- `./gradlew test` - Run all tests
- `./gradlew bootRun` - Start the application
- `./gradlew clean` - Clean build artifacts
- `./gradlew check` - Run all quality checks including tests

To run with a specific profile:

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

### Testing

- `./gradlew test` - Run all tests
- `./gradlew test --tests *SpecificTestClass` - Run specific test class
- `./gradlew test --info` - Run tests with detailed output

Run tests with a specific profile:

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

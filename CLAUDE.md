# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application for Organization Service, recently renamed from "oms" to "organization-service". The project uses Java 17 with Gradle build system and PostgreSQL database.

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
- **Additional**: Lombok for code generation

### Package Structure
- `com.reythecoder.organization` - Main package for the organization service
- `src/main/java/com/reythecoder/organization/` - Main application code
- `src/test/java/com/reythecoder/organization/` - Test code

### Current State
The project is in early development stage with basic Spring Boot structure. The main application class is `OrganizationServiceApplication.java` and basic test structure is in place.

## Configuration Notes

### Database Configuration
The application is configured for PostgreSQL but connection details need to be added to `application.properties`. You'll need to configure:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`

### Application Name
The `application.properties` file still contains `spring.application.name=oms` which should be updated to `organization-service` to match the project rename.

## Development Guidelines

### Entity and Repository Pattern
When adding new domain objects:
1. Create JPA entities in the main package
2. Create Spring Data JPA repositories extending `JpaRepository`
3. Use Lombok annotations to reduce boilerplate code

### REST API Structure
Follow Spring Boot conventions for REST controllers:
- Use `@RestController` for API endpoints
- Implement service layer for business logic
- Use proper HTTP status codes and error handling

### Testing Strategy
- Use `@SpringBootTest` for integration tests
- Use `@WebMvcTest` for controller layer tests
- Use `@DataJpaTest` for repository layer tests
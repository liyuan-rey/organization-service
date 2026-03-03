# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application for Organization Management Service, named "organization-service".

Reference: @./README.md

## Project Architecture

See: @./docs/project-architecture.md

## Database Design Conventions

See: @./docs/database-design-develop-guide-for-postgresql.md

## Development Guidelines

See: @./docs/development-guidelines.md

## Additional Instructions

Git instructions: @./docs/git-instructions.md

## Current State

The project has implemented core organization and personnel management features:

**Core Entities:**
- Department, Personnel, Position
- DepartmentPersonnel, DepartmentPosition, PersonnelPosition (association tables)
- Group, GroupHierarchy, GroupDepartment, GroupPersonnel
- DepartmentHierarchy

**Key Components:**
- Main application class: `OrganizationServiceApplication.java`
- REST controllers: `DepartmentController`, `PersonnelController`, `PositionController`, `GroupController`, and association controllers
- Service layer with interfaces and implementations
- DTOs: `ApiResult` uses `code` field for response status
- MapStruct mappers for entity-DTO mapping
- Global exception handling and AOP logging

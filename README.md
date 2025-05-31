# Vibe - Messaging Application

A modern messaging application built with Java Spring Boot and Angular.

## Project Structure

- **server**: Backend Java Spring Boot application
- **ui**: Frontend Angular application
- **deploy**: Docker and Terraform scripts for deployment
- **specs**: Solution documentation
- **file-storage**: Directory for local file storage
- **postgres-data**: Directory for PostgreSQL data

## Technology Stack

- **Backend**: Java 21, Spring Boot
- **Frontend**: Angular 18
- **Database**: PostgreSQL
- **Message Queue**: Apache ActiveMQ (in-memory)
- **Local Infrastructure**: Docker
- **Cloud Infrastructure**: AWS (via Terraform)

## Development Setup

### Prerequisites

- Java 21 JDK
- Node.js 18 and npm
- PostgreSQL 14
- Docker and Docker Compose (for local deployment)

### Backend Development

1. Start PostgreSQL using the provided script:
   ```bash
   ./postgres.sh start
   ```

2. Open the project in IntelliJ IDEA and run the `VibeServerApplication` class.

### Frontend Development

1. Navigate to the ui directory:
   ```bash
   cd ui
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. Open your browser and navigate to http://localhost:4200

## Local Deployment

A complete local deployment infrastructure is provided using Docker Compose. This allows you to run the entire application stack (PostgreSQL, backend, and frontend) with a single command.

### Using the Deployment Script

1. Navigate to the deploy/docker directory:
   ```bash
   cd deploy/docker
   ```

2. Run the deployment script:
   ```bash
   ./deploy-local.sh
   ```

   This will start all services. For more options, run:
   ```bash
   ./deploy-local.sh help
   ```

### Using Docker Compose Directly

1. Start the application:
   ```bash
   docker compose -f deploy/docker/docker-compose.yml up
   ```

2. Access the application:
   - Frontend: http://localhost
   - Backend API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html

For more detailed information about the local deployment infrastructure, see the [deploy/docker/README.md](deploy/docker/README.md) file.

## Cloud Deployment

Cloud deployment to AWS is configured using Terraform. For more information, see the documentation in the deploy/terraform directory.
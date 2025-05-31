# Local Deployment Infrastructure

This directory contains Docker configuration files for local deployment of the Vibe application.

## Prerequisites

- [Docker](https://www.docker.com/get-started) installed on your machine
- [Docker Compose](https://docs.docker.com/compose/install/) installed on your machine (usually comes with Docker Desktop)

## Components

The local deployment infrastructure consists of the following components:

1. **PostgreSQL Database**
   - Container name: vibe-postgres
   - Version: 14
   - Database name: vibe
   - Username: postgres
   - Password: postgres
   - Port: 5432
   - Data volume: vibe-postgres-data

2. **Backend Service (Spring Boot)**
   - Container name: vibe-backend
   - Port: 8080
   - API endpoint: http://localhost:8080/api
   - File storage volume: vibe-file-storage

3. **Frontend Service (Angular)**
   - Container name: vibe-frontend
   - Port: 80
   - Web interface: http://localhost

## Usage

### Starting the Application

To start the entire application stack, run the following command from the repository root:

```bash
docker compose -f deploy/docker/docker-compose.yml up
```

Or navigate to the `deploy/docker` directory and run:

```bash
docker compose up
```

Add the `-d` flag to run in detached mode (background):

```bash
docker compose up -d
```

### Stopping the Application

To stop the application, press `Ctrl+C` if running in the foreground, or run:

```bash
docker compose -f deploy/docker/docker-compose.yml down
```

### Rebuilding the Application

If you've made changes to the code and want to rebuild the containers:

```bash
docker compose -f deploy/docker/docker-compose.yml up --build
```

### Accessing the Application

- Frontend: http://localhost
- Backend API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- PostgreSQL: localhost:5432 (use a database client like pgAdmin or IntelliJ IDEA's database tools)

## Volumes

The application uses two named volumes for persistent storage:

1. **vibe-postgres-data**: Stores PostgreSQL database files
2. **vibe-file-storage**: Stores application assets (message attachments, etc.)

These volumes persist data even when containers are stopped or removed.

## Troubleshooting

### Viewing Logs

To view logs for all services:

```bash
docker compose -f deploy/docker/docker-compose.yml logs
```

To view logs for a specific service:

```bash
docker compose -f deploy/docker/docker-compose.yml logs [service_name]
```

Where `[service_name]` is one of: `postgres`, `backend`, or `frontend`.

### Resetting the Environment

To completely reset the environment, including volumes:

```bash
docker compose -f deploy/docker/docker-compose.yml down -v
```

**Warning**: This will delete all data stored in the volumes.
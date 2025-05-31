#!/bin/bash

# PostgreSQL Docker configuration
CONTAINER_NAME="vibe-postgres"
POSTGRES_VERSION="14"
POSTGRES_DB="vibe"
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="postgres"
POSTGRES_PORT="5432"

# Function to display usage information
show_usage() {
  echo "Usage: ./postgres.sh [COMMAND]"
  echo ""
  echo "Commands:"
  echo "  start       Start PostgreSQL container"
  echo "  stop        Stop PostgreSQL container"
  echo "  restart     Restart PostgreSQL container"
  echo "  status      Check if PostgreSQL container is running"
  echo "  logs        Show PostgreSQL container logs"
  echo "  shell       Open a psql shell in the container"
  echo "  reset       Remove container and volume (WARNING: All data will be lost)"
  echo "  help        Show this help message"
  echo ""
}

# Function to check if Docker is installed
check_docker() {
  if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed or not in PATH"
    echo "Please install Docker and try again"
    exit 1
  fi
}


# Function to start the PostgreSQL container
start_postgres() {
  if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
      echo "PostgreSQL container is already running"
    else
      echo "Starting existing PostgreSQL container..."
      docker start ${CONTAINER_NAME}
      echo "PostgreSQL container started"
    fi
  else
    echo "Creating and starting PostgreSQL container..."

    docker run --name ${CONTAINER_NAME} \
      -e POSTGRES_DB=${POSTGRES_DB} \
      -e POSTGRES_USER=${POSTGRES_USER} \
      -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
      -p ${POSTGRES_PORT}:5432 \
      -v "${CONTAINER_NAME}-data:/var/lib/postgresql/data" \
      -d postgres:${POSTGRES_VERSION}

    echo "PostgreSQL container created and started"
    echo "Database: ${POSTGRES_DB}"
    echo "Username: ${POSTGRES_USER}"
    echo "Password: ${POSTGRES_PASSWORD}"
    echo "Port: ${POSTGRES_PORT}"
    echo "Data volume: ${CONTAINER_NAME}-data"
  fi
}

# Function to stop the PostgreSQL container
stop_postgres() {
  if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Stopping PostgreSQL container..."
    docker stop ${CONTAINER_NAME}
    echo "PostgreSQL container stopped"
  else
    echo "PostgreSQL container is not running"
  fi
}

# Function to check the status of the PostgreSQL container
status_postgres() {
  if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "PostgreSQL container is running"
    docker ps --filter "name=${CONTAINER_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
  else
    echo "PostgreSQL container is not running"
  fi
}

# Function to show logs of the PostgreSQL container
logs_postgres() {
  if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    docker logs ${CONTAINER_NAME}
  else
    echo "PostgreSQL container does not exist"
  fi
}

# Function to open a psql shell in the container
shell_postgres() {
  if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Opening psql shell..."
    docker exec -it ${CONTAINER_NAME} psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}
  else
    echo "PostgreSQL container is not running"
  fi
}

# Function to reset the PostgreSQL container (remove container and volume)
reset_postgres() {
  if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Stopping PostgreSQL container..."
    docker stop ${CONTAINER_NAME}
  fi

  if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Removing PostgreSQL container..."
    docker rm ${CONTAINER_NAME}
  fi

  echo "WARNING: This will delete all your PostgreSQL data in the Docker volume ${CONTAINER_NAME}-data"
  read -p "Are you sure you want to continue? (y/n): " confirm
  if [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]]; then
    echo "Removing Docker volume..."
    docker volume rm ${CONTAINER_NAME}-data 2>/dev/null || true
    echo "Docker volume removed"
  else
    echo "Reset operation cancelled"
  fi
}

# Main script logic
check_docker

if [ $# -eq 0 ]; then
  show_usage
  exit 0
fi

case "$1" in
  start)
    start_postgres
    ;;
  stop)
    stop_postgres
    ;;
  restart)
    stop_postgres
    start_postgres
    ;;
  status)
    status_postgres
    ;;
  logs)
    logs_postgres
    ;;
  shell)
    shell_postgres
    ;;
  reset)
    reset_postgres
    ;;
  help)
    show_usage
    ;;
  *)
    echo "Unknown command: $1"
    show_usage
    exit 1
    ;;
esac

exit 0

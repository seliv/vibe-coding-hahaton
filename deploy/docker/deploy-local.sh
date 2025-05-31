#!/bin/bash

# Script to manage local deployment of the Vibe application

# Function to display usage information
show_usage() {
  echo "Usage: ./deploy-local.sh [COMMAND]"
  echo ""
  echo "Commands:"
  echo "  up          Start the application (default)"
  echo "  down        Stop the application"
  echo "  build       Rebuild and start the application"
  echo "  logs        Show logs from all services"
  echo "  logs-backend Show logs from the backend service"
  echo "  logs-frontend Show logs from the frontend service"
  echo "  logs-postgres Show logs from the PostgreSQL service"
  echo "  reset       Reset the environment (WARNING: All data will be lost)"
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

# Function to check if Docker Compose is installed
check_docker_compose() {
  if ! docker compose version &> /dev/null; then
    echo "Error: Docker Compose is not installed or not in PATH"
    echo "Please install Docker Compose and try again"
    exit 1
  fi
}

# Function to start the application
start_app() {
  echo "Starting the Vibe application..."
  docker compose -f docker-compose.yml up $1
}

# Function to stop the application
stop_app() {
  echo "Stopping the Vibe application..."
  docker compose -f docker-compose.yml down
}

# Function to show logs
show_logs() {
  if [ -z "$1" ]; then
    docker compose -f docker-compose.yml logs --follow
  else
    docker compose -f docker-compose.yml logs --follow $1
  fi
}

# Function to reset the environment
reset_app() {
  echo "WARNING: This will delete all your data in the Docker volumes"
  read -p "Are you sure you want to continue? (y/n): " confirm
  if [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]]; then
    echo "Resetting the environment..."
    docker compose -f docker-compose.yml down -v
    echo "Environment reset complete"
  else
    echo "Reset operation cancelled"
  fi
}

# Main script logic
check_docker
check_docker_compose

# Change to the script directory
cd "$(dirname "$0")"

if [ $# -eq 0 ]; then
  start_app
  exit 0
fi

case "$1" in
  up)
    start_app
    ;;
  down)
    stop_app
    ;;
  build)
    start_app "--build"
    ;;
  logs)
    show_logs
    ;;
  logs-backend)
    show_logs "backend"
    ;;
  logs-frontend)
    show_logs "frontend"
    ;;
  logs-postgres)
    show_logs "postgres"
    ;;
  reset)
    reset_app
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
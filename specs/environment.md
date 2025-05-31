# Solution technologies and infrastructure

## Technology Stack

* Backend: Java 21, Spring Boot application
* Frontend: Angular 18
* Backend build toot: Gradle, Kotlin-style
* Frontend build tool: npm 
* Database: PostgreSQL
* Local infrastructure: Rancher Desktop (Docker-compatible engine)
* Deployment infrastructure: AWS
* Deployment IaC tool: Terraform

## Infrastructure

### Development

Containerized PostreSQL instance must be provided, accessible locally for browsing from IntelliJ IDEA.
A local directory must be used for persistent application assets storage. 
Backend application must be buildable and runnable from IntelliJ IDEA natively (no containers).
Frontend application must be buildable and runnable from the command line natively (no containers).  

### Local Deployment

Dockerized build and run scripts must be provided.
The local application instance must spawn up with a single "docker compose up" command.
Dockerized application must use a detachable volume mapped to a local directory for application assets storage.

### Cloud Deployment

Terraform project/scripts must be composed to provision and deploy the required infrastructure.
Deployment scripts must be cloud-agnostic wherever possible, but the primary target cloud is AWS.
Backend and frontend applications must be deployed to cloud from locally build docker images.

#### Resources

- Managed PostgreSQL instance
- S3 bucket for attachments
- AWS Fargate cluster with 1 EC instance

General common requirements for provisioned resources are:

- All resources must be private, not publicly available.
- All resources must be indentify an owner where applicable; the owner is "seliv-vibe-code-hahaton".

IAM users and roles should not be configured.
Network configuration (VPCs, Subnets, Gateways) should not be configured.

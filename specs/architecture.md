# Solution Architecture Decisions

## Repository Structure

The root folder should contain the following folders:
- specs: solution documentation (provided by the product owner)
- server: the root of the backend application; underlying structure must be typical for Java application (i.e. src/main)
- ui: the root of the frontend application; underlying structure must be typical for Angular application (i.e. src/app)
- deploy: Docker build scripts and Terraform scripts for local and cloud deployment

The default src folder at the project root level should be removed.
The backend project must still be buildable by Gradle from the project root level.
The UI project must be buildable by npm from the UI folder.

## Backend Architecture

The backend application is the Java Spring Boot application.
It provides REST API for the UI application.

### External Dependencies

The backend application connects to PostgreSQL for persistent structured data storage.

It connects to a file storage for unstructured data storage (message attachments).
This storage should be backed by the S3 storage in AWS and a configurable disk folder locally.
The solution should provide an adapter and configuration to configure the storage.

### API

REST API is defined in Java code inline with default @RestController annotations.
Swagger UI must be provided in the development profile for debugging but must be disabled in production deployments.

### Security

The API supports user authentication and authorization and maintains user sessions. 
Anonymous access is allowed for a limited set of endpoints, the other endpoints must be protected with authorization.
Default Spring JWT-based sessions are used.

### Event Processing

In-memory Apache ActiveMQ must be used for event queue implementation.
The API should process user's requests synchronously and transform them into events published to the queue for further background processing.
A separate processing component consumes the events from the queue asynchronously to process.
Processing can be configured to use multiple processing instances (multiple threads). 
No need to guarantee sequential processing.

The processing engine must guarantee:
1. Successful event processing.
2. Retrying to process if the processing fails.
3. Each event must processed (delivered) only once.

## Frontend Architecture

The frontend application connects to the backend application via REST API.
The application is a thin client not designed to work offline (without server connectivity available).
The frontend is a SPA application.

## Client-Server Interaction

The server generates new events for the user asynchronously. These events must be delivered to the UI as the server-initiated calls.
For version 1 a polling mechanism must be implemented: the UI sends periodic status poll requests to check if there are new events.
This mechanism should be isolated as a separate component (both in the backend and in the frontend) 
to allow for further replacement with another solution.

The server should maintain a queue of messages pending per each connected user and deliver them with the next poll request.

Regular synchronous client-initiated REST calls are also supported.

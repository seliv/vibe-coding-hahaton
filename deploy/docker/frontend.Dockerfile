FROM node:18 AS build

WORKDIR /app

# Copy package.json and package-lock.json
COPY ui/package.json ui/package-lock.json ./

# Install dependencies
RUN npm ci

# Copy the rest of the application
COPY ui/ ./

# Build the application
RUN npm run build

# Runtime stage
FROM nginx:alpine

# Copy the build output to replace the default nginx contents
COPY --from=build /app/dist/vibe-ui /usr/share/nginx/html

# Copy custom nginx configuration if needed
# COPY deploy/docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
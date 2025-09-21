# Config Repository

This is the configuration repository for Spring Cloud Config Server, containing configuration files for all microservices.

## File Structure

```
config-repo/
├── application.yml          # Shared default configuration
├── employee-service.yml     # Employee Service specific configuration
├── department-service.yml   # Department Service specific configuration
├── api-gateway.yml         # API Gateway specific configuration
├── discovery-service.yml   # Discovery Service specific configuration
└── README.md              # This document
```

## Configuration Refresh Demo

### 1. Start Config Server

```bash
mvn -pl config-server spring-boot:run
```

Config Server will start on port 8888.

### 2. Start Other Services

```bash
# Start Discovery Service
mvn -pl discovery-service spring-boot:run

# Start API Gateway
mvn -pl api-gateway spring-boot:run

# Start Employee Service
mvn -pl employee-service spring-boot:run

# Start Department Service
mvn -pl department-service spring-boot:run
```

### 3. Configuration Refresh Demo

#### Step 1: Modify Log Level

Edit the `employee-service.yml` file, change log level from DEBUG to INFO:

```yaml
logging:
  level:
    com.example.employee: INFO  # Changed from DEBUG to INFO
```

#### Step 2: Refresh Configuration

Call Employee Service's refresh endpoint:

```bash
curl -X POST http://localhost:8081/actuator/refresh
```

#### Step 3: Verify Changes

Check Employee Service's log output, you should see the log level has changed.

### 4. Other Refreshable Configuration Examples

#### Modify Pagination Size

In `employee-service.yml`:

```yaml
employee:
  service:
    pagination:
      default-page-size: 50  # Changed from 20 to 50
```

Then call the refresh endpoint.

#### Modify Feature Toggle

In `department-service.yml`:

```yaml
feature:
  enable-employee-count: false  # Changed from true to false
```

Then call the refresh endpoint.

## Configuration Verification

### Check if Configuration is Loaded Correctly

Access Config Server endpoints to verify configuration:

```bash
# Get Employee Service configuration
curl http://localhost:8888/employee-service/default

# Get Department Service configuration
curl http://localhost:8888/department-service/default

# Get API Gateway configuration
curl http://localhost:8888/api-gateway/default
```

### Check Service Health Status

```bash
# Employee Service health check
curl http://localhost:8081/actuator/health

# Department Service health check
curl http://localhost:8082/actuator/health

# API Gateway health check
curl http://localhost:8080/actuator/health
```

## Troubleshooting

### If Config Server is Unavailable

If Config Server fails to start, services will use local configuration (local profile in bootstrap.yml).

### If Configuration Refresh Fails

1. Check if Config Server is running
2. Check if configuration file syntax is correct
3. Check error messages in service logs

### Common Issues

1. **Port Conflict**: Ensure Config Server's port 8888 is not occupied
2. **Configuration File Syntax Error**: Use YAML validator to check syntax
3. **Network Connection Issues**: Ensure services can access Config Server

## Best Practices

1. **Version Control**: Include configuration repository in version control
2. **Environment Separation**: Create different configuration files for different environments
3. **Sensitive Information**: Use encryption to store sensitive configuration information
4. **Monitoring**: Monitor configuration changes and refresh operations

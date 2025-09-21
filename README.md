# Spring Boot 3.0 Microservices (Java 17)

A realistic microservices starter with:

- **Discovery**: Eureka server (`discovery-service`)
- **API Gateway**: Spring Cloud Gateway (`api-gateway`)
- **Services**: Employee + Department (each with Postgres + Flyway)
- **DB**: Postgres connection is shared, with **separate schemas** per service
- **Migrations**: Flyway (`classpath:/db/migration`)

## Prerequisites

- Java 17
- Maven 3.8+
- Postgres running locally and accessible at (you can set it up by yourself):
  - `jdbc:postgresql://localhost:5432/postgres`
  - username: `postgres`
  - password: `123456!`

> Adjust credentials in each service's `application.yml` if yours differ.

## Order to Run (separate terminals)

```bash
# 1) Start Config Server (NEW - must be first)
mvn -pl config-server spring-boot:run

# 2) Start discovery
mvn -pl discovery-service spring-boot:run

# 3) Start gateway
mvn -pl api-gateway spring-boot:run

# 4) Start services (can be in any order after discovery is up)
mvn -pl department-service spring-boot:run
mvn -pl employee-service spring-boot:run
```

## Service URLs

- **Config Server**: http://localhost:8888
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080

Gateway will expose routes:

- `GET http://localhost:8080/departments` (rewritten to `DEPARTMENT-SERVICE /api/v1/departments`)
- `GET http://localhost:8080/employees` (rewritten to `EMPLOYEE-SERVICE /api/v1/employees`)

OpenAPI UIs (service level):

- Employee: http://localhost:8081/swagger-ui.html
- Department: http://localhost:8082/swagger-ui.html

## Configuration Management

This project now uses **Spring Cloud Config Server** for centralized configuration management.

### Config Refresh Demo

1. **Modify configuration**: Edit files in `config-repo/` directory
2. **Refresh service**: Call `POST /actuator/refresh` on any service
3. **Verify changes**: Check logs or service behavior

Example:
```bash
# Change log level in config-repo/employee-service.yml
# Then refresh the service
curl -X POST http://localhost:8081/actuator/refresh
```

See `config-repo/README.md` for detailed configuration management guide.

## Notes

- Each service uses **Flyway** and its own schema (`employee`, `department`) with separate history tables.
- The `employee-service` uses **OpenFeign** to enrich employees with department details.
- Health endpoints: `/actuator/health`
- Default ports:
  - Discovery: 8761
  - Gateway: 8080
  - Employee: 8081
  - Department: 8082
- After you start the services, please make sure you are able to execute the following code:
  - Employees Service (Expanded API)
    - curl -s http://localhost:8080/employees
    - curl -s http://localhost:8080/employees?page=0&size=10&sort=lastName,asc
    - curl -s http://localhost:8080/employees?email=test@example.com&departmentId=1
    - curl -s http://localhost:8080/employees/1?enrich=true
    - curl -s http://localhost:8080/employees/search?query=alice
    - curl -s http://localhost:8080/employees/stats
    - curl -s -X POST http://localhost:8080/employees \
      -H "Content-Type: application/json" \
      -d '{ "firstName": "Dina", "lastName": "Khan", "email": "dina@example.com", "departmentId": 1 }'
    - curl -s -X PUT http://localhost:8080/employees/1 \
      -H "Content-Type: application/json" \
      -d '{ "firstName": "Updated", "lastName": "Name", "email": "updated@example.com", "departmentId": 2 }'
    - curl -s -X PATCH http://localhost:8080/employees/1 \
      -H "Content-Type: application/json" \
      -d '{ "firstName": "Patched" }'
    - curl -s -X DELETE http://localhost:8080/employees/1
    - curl -s -X POST http://localhost:8080/employees/bulkCreate \
      -H "Content-Type: application/json" \
      -d '{ "employees": [{ "firstName": "Alice", "lastName": "Smith", "email": "alice@example.com", "departmentId": 1 }] }'
  - Department Service (Expanded API)
    - curl -s http://localhost:8080/departments
    - curl -s http://localhost:8080/departments?page=0&size=10&sort=name,asc
    - curl -s http://localhost:8080/departments?name=IT&code=IT001
    - curl -s http://localhost:8080/departments/1
    - curl -s http://localhost:8080/departments/by-code/IT001
    - curl -s http://localhost:8080/departments/1/employees
    - curl -s -X POST http://localhost:8080/departments/ \
      -H "Content-Type: application/json" \
      -d '{ "name": "Finance", "code": "FIN001", "description": "Money things" }'
    - curl -s -X PUT http://localhost:8080/departments/1 \
      -H "Content-Type: application/json" \
      -d '{ "name": "Updated Finance", "code": "FIN002", "description": "Updated Money things" }'
    - curl -s -X PATCH http://localhost:8080/departments/1 \
      -H "Content-Type: application/json" \
      -d '{ "name": "Patched Finance" }'
    - curl -s -X DELETE http://localhost:8080/departments/1

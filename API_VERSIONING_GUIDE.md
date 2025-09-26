# API Versioning Strategy

## Overview
This document describes the API versioning strategy implemented for the microservices architecture.

## Versioning Methods

### 1. Header-based Versioning
Clients can specify the API version using the `API-Version` header:

```bash
curl -H "API-Version: v1" http://localhost:8080/api/employees
curl -H "API-Version: v2" http://localhost:8080/api/employees
```

### 2. Accept Header Versioning
Clients can specify the version in the Accept header:

```bash
curl -H "Accept: application/json; version=v1" http://localhost:8080/api/employees
curl -H "Accept: application/json; version=v2" http://localhost:8080/api/employees
```

### 3. Default Version
If no version is specified, the API defaults to v1.

## Version Differences

### Employee Service

#### V1 Features
- Basic CRUD operations
- Pagination and filtering
- Search functionality
- Statistics endpoint
- Bulk operations

#### V2 Features
- All V1 features
- Enhanced department enrichment
- Improved error handling
- Additional metadata in responses

### Department Service

#### V1 Features
- Basic CRUD operations
- Pagination and filtering
- Department lookup by code
- Employee listing by department

#### V2 Features
- All V1 features
- Enhanced employee integration
- Improved performance metrics
- Additional validation

## Implementation Details

### Controller Structure
```
src/main/java/com/example/{service}/web/
├── {Service}Controller.java          # V1 API
└── v2/
    └── {Service}ControllerV2.java   # V2 API
```

### Version Detection
The system uses custom `ApiVersionRequestCondition` to match requests based on:
1. `API-Version` header
2. `Accept` header with version parameter
3. Default to v1 if no version specified

### Backward Compatibility
- V1 APIs remain fully functional
- New features are added in V2
- Breaking changes are avoided in V1
- Deprecation warnings are provided for old endpoints

## Migration Guide

### For Clients
1. **Immediate**: Continue using V1 APIs
2. **Short-term**: Test V2 APIs in development
3. **Long-term**: Migrate to V2 APIs for new features

### For Developers
1. **New Features**: Add to V2 controllers
2. **Bug Fixes**: Apply to both V1 and V2
3. **Breaking Changes**: Create new V3 if necessary

## Best Practices

1. **Version Headers**: Always specify version in headers
2. **Error Handling**: Handle version-specific errors
3. **Documentation**: Keep API docs updated for each version
4. **Testing**: Test all versions in CI/CD pipeline
5. **Monitoring**: Track usage of different versions

## Example Usage

### Employee Service V1
```bash
# Get all employees
curl -H "API-Version: v1" http://localhost:8081/api/employees

# Get employee by ID
curl -H "API-Version: v1" http://localhost:8081/api/employees/1
```

### Employee Service V2
```bash
# Get all employees with department enrichment
curl -H "API-Version: v2" http://localhost:8081/api/employees?enrichWithDepartment=true

# Get employee by ID with department info
curl -H "API-Version: v2" http://localhost:8081/api/employees/1?enrichWithDepartment=true
```

### Department Service V1
```bash
# Get all departments
curl -H "API-Version: v1" http://localhost:8082/api/departments

# Get department by code
curl -H "API-Version: v1" http://localhost:8082/api/departments/code/IT
```

### Department Service V2
```bash
# Get all departments with enhanced features
curl -H "API-Version: v2" http://localhost:8082/api/departments

# Get department employees
curl -H "API-Version: v2" http://localhost:8082/api/departments/1/employees
```

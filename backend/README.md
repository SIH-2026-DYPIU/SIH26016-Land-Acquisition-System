# Backend Integration

This folder contains the NLAMS backend integration files available from the previous integration step.

Package root:
`com.sih.landacquisitionsystem`

## Included

- `controller/`
- `service/AuthService.java`
- `service/DashboardService.java`
- `config/CorsConfig.java`
- `src/main/resources/application.properties.snippet`

## Existing backend files still required

The original Spring Boot project should provide the existing:

- `pom.xml` / Maven wrapper
- main Spring Boot application class
- model/entity classes
- DTO classes
- repository classes
- original ProjectService
- original LandParcelService
- original AcquisitionStageService
- original CompensationService
- original AffectedFamilyService
- security/password encoder configuration
- database configuration

Do not replace those blindly with placeholders.

# Spring Boot 4 External Config (Gradle)

Minimal Spring Boot 4.0.2 example showing two approaches for external configuration:

1. **Using `spring.config.import`** - Import external YAML files
2. **Using `@PropertySource`** - Load properties from .properties files

## Run

```bash
./gradlew bootRun
```

## Verify

### 1. External YAML Config (spring.config.import)
```bash
curl http://localhost:8082/info
```
You should see values from `external/application-external.yml` overriding the classpath defaults.

### 2. PropertySource Example
```bash
curl http://localhost:8082/api/property-source/info
```
This demonstrates loading properties from `app.properties` using `@PropertySource`.

## Code Examples

### @PropertySource Usage
```java
@Component
@PropertySource("classpath:app.properties")
public class SimplePropertySourceExample {
    @Value("${app.name}")
    private String appName;
}
```

### External Config Files
- `external/application-external.yml` - External YAML config (imported via spring.config.import)
- `src/main/resources/app.properties` - Properties loaded via @PropertySource

## Notes

- External config files are optional - the app starts even if they're missing
- @PropertySource is useful for loading custom properties files
- spring.config.import is the modern way to load external YAML/properties files

# Spring Boot 4 External Config (Gradle)

Minimal Spring Boot 4.0.2 example showing external configuration using `spring.config.import`.

## Run

```bash
./gradlew bootRun
```

## Verify

```bash
curl http://localhost:8082/info
```

You should see values from `external/application-external.yml` overriding the classpath defaults in `src/main/resources/application.yml`.

## Notes

- External config file: `external/application-external.yml`
- Import is optional, so the app still starts without the external file.

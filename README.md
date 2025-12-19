# Demo Project

This project is a Spring Boot application that provides a price search service based on specific criteria like brand, product, and application date. It follows a **Hexagonal (Ports and Adapters) Architecture**.

## Guidelines and Documentation

### Build and Configuration

- **Requirements**: Java 21 and Maven (or Docker for containerized execution).
- **Build**: Run `./mvnw clean install` to build the project and run all tests.
- **Run**: Execute `./mvnw spring-boot:run` to start the application.
- **Database**: The project uses an in-memory H2 database. It is automatically initialized using `src/main/resources/schema.sql` and `src/main/resources/data.sql`.
- **API Documentation**: Once the application is running, the Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Running with Docker

The easiest way to run the application is using Docker Compose:

```bash
# Build and start the application
docker compose up --build

# Run in detached mode (background)
docker compose up --build -d

# View logs
docker compose logs -f

# Stop the application
docker compose down
```

Once running, access the application at:
- **API Endpoint**: `http://localhost:8080/brand/{brandId}/product/{productId}/prices`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/actuator/health`

#### Example API Request

```bash
# Get price for brand 1, product 35455 at a specific date/time
curl "http://localhost:8080/brand/1/product/35455/prices?applicationDate=2020-06-14T10:00:00"
```

#### Docker Commands Reference

```bash
# Build only (without starting)
docker compose build

# Rebuild without cache
docker compose build --no-cache

# Stop and remove containers, networks
docker compose down

# Stop and remove everything including volumes
docker compose down -v
```

### Testing Guidelines

- **Test Framework**: JUnit 5, Mockito, and Spring Boot Test.
- **Running Tests**:
  - All tests: `./mvnw test`
  - Specific test class: `./mvnw test -Dtest=ClassName`
- **Adding New Tests**:
  - **Unit Tests**: Follow the pattern in `PriorityPriceServiceTest.java`. Use Mockito for dependencies.
  - **Integration Tests**: Use `@SpringBootTest` and `@AutoConfigureMockMvc`. See `PriceControllerIntegrationTest.java` for reference.
  - **Test Data**: Use the existing data in `data.sql` for integration tests or provide your own via `@Sql` annotation or manual setup in unit tests.

### Development Information

- **Architecture**: The project follows a Hexagonal (Ports and Adapters) architecture:
  - `domain`: Contains business logic and domain models (using Java Records).
  - `application`: Contains use cases.
  - `adapter`: Contains inbound (REST) and outbound (Persistence) adapters.
  - `infrastructure`: Configuration and framework-specific code.
- **Code Style**: 
  - Use Java Records for immutable domain models and DTOs where appropriate.
  - Use Lombok to reduce boilerplate in Entities and some DTOs (e.g., `@RequiredArgsConstructor`, `@Builder`).
  - Follow the existing package structure to maintain separation of concerns.
- **Error Handling**: Centrally managed in `GlobalExceptionHandler.java`. Custom exceptions should be placed in `com.example.demo.domain.exception`.
- **Mapping**: Data transfer between layers is handled by mappers (e.g., `PriceMapper.java`). Avoid leaking persistence entities into the domain or REST layers.

## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.9-SNAPSHOT/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.9-SNAPSHOT/maven-plugin/build-image.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.9-SNAPSHOT/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.5.9-SNAPSHOT/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.9-SNAPSHOT/reference/web/servlet.html)

### Docker Compose support

This project includes full Docker support:

- **Dockerfile**: Multi-stage build using Maven and Eclipse Temurin JDK 21.
- **compose.yaml**: Docker Compose configuration with health checks.
- **.dockerignore**: Optimized build context.

To run the application in a container, simply execute `docker compose up --build`.

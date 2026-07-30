# E-CommerceApplication

A microservices-based e-commerce application (frontend + multiple Spring Boot backend services) prepared for college submission.

## Repository layout

- backend/ - Spring Boot microservices and supporting infrastructure (Eureka, config, API gateway)
  - services/
    - admin-service
    - auth-service
    - cart-service
    - customer-service
    - product-service
    - seller-service
    - wishlist-service
- frontend/ - React (Vite) frontend
- docker-compose.yml - optional compose setup (if provided)

## Prerequisites

- Java 21 (JDK)
- Apache Maven 3.8+
- Node.js (18+) and npm
- Optional: Docker & Docker Compose (if you plan to run containers)

## Build backend (produce fat JARs)

To build all backend services and produce runnable JARs, run the following from the repository root:

```bash
# Build every Maven service (skip tests to speed up for submission)
for d in backend/services/*/; do
  if [ -f "$d/pom.xml" ]; then
    echo "Building $d"
    (cd "$d" && mvn -DskipTests clean package)
  fi
done
```

After a successful build each service's runnable JAR will be in the service's `target/` directory (for example `backend/services/product-service/target/`).

To run a built service:

```bash
cd backend/services/<service-name>
java -jar target/<artifact-id>-<version>.jar
```

Replace `<service-name>` and `<artifact-id>-<version>.jar` with the appropriate names from the `target` folder.

Note: Some services may depend on configuration servers, Eureka, or databases. For a minimal local run you can:
- Start supporting infrastructure (discovery/config) first if present (each is a separate module under `backend/`).
- Or run services in Docker Compose if a compose file is provided and configured.

## Build frontend

From the `frontend/` folder:

```bash
cd frontend
npm install
npm run build
```

This will create a production build in `frontend/dist` (Vite default). If the backend serves static files, copy or configure accordingly.

## Docker (optional)

If `docker-compose.yml` is provided and configured for this project, run:

```bash
docker-compose up --build
```

Check `docker-compose.yml` for required environment variables and database images.

## Submission checklist

- [ ] All backend services build cleanly (JARs in `target/` folders)
- [ ] Frontend builds (`frontend/dist` exists)
- [ ] README contains build/run instructions (this file)
- [ ] Include any additional submission notes (database dumps, sample env files) in `submission/` if required

## Troubleshooting

- Maven build errors: ensure JDK 21 and Maven are installed and `JAVA_HOME` is set.
- Port conflicts: services may assume default ports; stop other services or change ports in `application-*.yml` files.

---

Prepared for college submission. If you'd like, next steps I can perform:
- Build all backend services now and attach the produced JAR paths
- Create a `submission/` ZIP containing source and compiled artifacts
- Generate sample environment files (.env or application.yml) for quick local runs


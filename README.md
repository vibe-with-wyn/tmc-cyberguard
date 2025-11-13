# TMC Secure System (Dockerized)

Run this Spring Boot + PostgreSQL demo on any device using Docker Compose. No local Java/Maven/Postgres setup required.

## Prerequisites (once per device)
- Install Docker Desktop (Windows) and enable “Use the WSL 2 based engine”.

---

## Step 1) What’s included (Docker files)
- Dockerfile (multi-stage): builds the JAR and runs on a slim JRE with the `docker` profile.
- docker-compose.yml: starts Postgres, the app, and optional pgAdmin.
- src/main/resources/application-docker.yml: container-specific Spring config.
- .env.example: example environment variables used by Compose and the app.

Key ports:
- App: http://localhost:8080
- Postgres: localhost:5432 (exposed for local tools)
- pgAdmin: http://localhost:5050

---

## Step 2) Create your .env from the example
From the project root (PowerShell on Windows):
```powershell
Copy-Item .env.example .env
```
You can keep the demo values as-is for school use.

---

## Step 3) Build and run with Docker Compose
```powershell
docker compose up -d --build
docker compose logs -f app
```

Open:
- App: http://localhost:8080
- pgAdmin: http://localhost:5050  
  - Login with PGADMIN_EMAIL / PGADMIN_PASSWORD from .env  
  - Add a server:
    - Name: TMC
    - Host: postgres
    - Port: 5432
    - Username: DB_USER (from .env)
    - Password: DB_PASSWORD (from .env)

Demo users (seeded automatically):
- admin / Admin@123  (ROLE_ADMIN)
- operations / OT@12345  (ROLE_OT_OPERATOR)
- analyst / Analyst@123  (ROLE_IT_ANALYST)
- compliance / Compliance@123  (ROLE_COMPLIANCE_OFFICER)
- ciso / Ciso@12345  (ROLE_CISO)

---

## Step 4) Run on another device
- Install Docker Desktop.
- Clone the repo.
- Copy `.env.example` to `.env` (or bring your `.env`).
- Run:
```powershell
docker compose up -d --pull always --build
```

---

## How it works (in short)
- Compose builds your app image from the Dockerfile and starts all services on an isolated network.
- The app connects to Postgres by service name `postgres` (not `localhost`) via env vars.
- Volumes (`pg_data`, `pgadmin_data`) keep data between restarts.
- Same images + same config = the app runs identically anywhere Docker runs.

## Useful commands
- Stop: `docker compose down`
- Stop and remove volumes (resets DB): `docker compose down -v`
- Rebuild only app: `docker compose build app --no-cache && docker compose up -d`

## Troubleshooting
- Port in use: change the left side of the mapping in `docker-compose.yml` (e.g., `"8081:8080"`).
- App can’t reach DB: ensure JDBC URL points to `jdbc:postgresql://postgres:5432/...` (already set by Compose).
# TMC Secure System

A role–segmented Spring Boot 3.5 (Java 21) application for secure ingestion, encryption-at-rest, controlled analyst access, and complete auditing/incident tracking.

## Core Features

- Role-based dashboards: Admin, OT Operator, IT Analyst, Compliance Officer, CISO.
- Strong separation of Audit vs Incident logs (normal actions vs security-relevant events).
- Excel (.xlsx) validation before encryption/storage.
- AES‑256 GCM authenticated encryption of uploaded files (integrity + confidentiality).
- File assignment workflow (Admin → Analyst) with revocation & optional expiry.
- Account lockout on repeated failed logins.
- Centralized date formatting and log enrichment (IP, session, actor).
- CSV export of Incident and Audit logs (Compliance).
- Consistent minimal UI (Bootstrap + custom CSS).

## Technology Stack

| Layer | Implementation |
|-------|----------------|
| Runtime | Spring Boot 3.5, Java 21 |
| Security | Spring Security, BCrypt, role-based authorization |
| DB | PostgreSQL (JPA/Hibernate) |
| Encryption | AES-GCM 256 (single symmetric key) |
| Templates | Thymeleaf |
| Build | Maven |
| Logging | Structured DB entities (AuditLog, IncidentLog) |

## Domain Entities (High-Level)

- User: credentials, role (`RoleName`), status (lockout logic).
- EncryptedFile: encrypted payload + metadata (IV, key ref, hash, uploader).
- FileAssignment: Analyst access grants (status: ACTIVE/REVOKED/EXPIRED).
- AuditLog: accountability trail for normal or expected actions.
- IncidentLog: elevated security or risk events (denials, lockouts, integrity issues).

## Encryption Model

1. Upload (.xlsx) → validation (`ExcelValidator`) ensures required headers.
2. `EncryptionService.encrypt()`:
   - Generates 12‑byte IV (SecureRandom).
   - Performs `AES/GCM/NoPadding`: returns ciphertext + GCM tag.
   - Computes SHA‑256 Base64 of ciphertext (stored separately).
3. `EncryptedFile` persists:
   - `ciphertext` (BYTEA, lazy) – raw GCM output.
   - `iv` (Base64) – unique per encryption.
   - `fileHash` – SHA‑256 of ciphertext (second integrity layer).
   - `aesKeyRef` – static key id (supports future rotation).
4. Decryption (`DecryptionService.decryptWithIntegrity`):
   - Recomputes SHA‑256 and compares with stored `fileHash`.
   - Initializes AES-GCM with stored IV; validates auth tag implicitly.
   - Any mismatch triggers integrity incident (logged as `INTEGRITY_MISMATCH`).

Why secure:
- GCM provides confidentiality + authenticity (tag). The extra stored hash protects against accidental truncation or persistence corruption before GCM is attempted.
- Key never stored alongside plaintext; only the reference (`aesKeyRef`).
- IV randomized per file; prevents nonce reuse attacks in GCM.

## Authentication & Authorization

- Custom `UserDetailsService` loads `User`.
- Account locked after 5 failed attempts for 15 minutes (`AuthEventListener`).
- Roles mapped to dashboards via `DashboardResolver`.
- `SecurityConfig`:
  - CSRF enabled (cookie token).
  - Session creation: `IF_REQUIRED`.
  - Endpoint whitelisting: static assets, login, 403.
  - `LoggingAccessDeniedHandler` generates `UNAUTHORIZED_ACCESS` incidents.

## Logging Strategy

| Type | Purpose | Trigger Examples |
|------|---------|------------------|
| AuditLog | Accountability, normal lifecycle | login success, logout, file stored, decrypt success, validation failed |
| IncidentLog | Security & anomaly capture | failed decrypt permission, account locked, unauthorized access, integrity mismatch |

`LogHelper` populates IP, session ID, actor reference uniformly. This keeps controllers/services lean and consistent.

## File Access Control

Admin assigns file → Analyst via `AssignmentService`:
- Single permission (DECRYPT) now implicit; simplifies UI & logic.
- Revocation sets status REVOKED; optional expiry field (future enforcement).
- Analyst download endpoint checks active assignment and logs success/denials.

## Pagination

- Independent parameters for each log list:
  - Admin: `incPage/incSize`, `audPage/audSize`
  - Compliance: `incPage/incSize`, `audPage/audSize`
  - CISO: identical pattern
- Prevents cross-coupling of pages.

## CSV Export

`CsvExportService` escapes fields (quotes doubled) and formats timestamps via `UiDates` (`yyyy-MM-dd HH:mm:ss`). Available for both Incident and Audit sets under Compliance.

## Date Utilities

- `DateRanges` converts ISO date filters to start/end `LocalDateTime`.
- `UiDates` centralizes rendering to keep all tables visually consistent.

## Excel Validation

`ExcelValidator`:
- Canonicalizes headers (lowercase, strip non-alphanumeric) to be tolerant.
- Ensures all required headers are present.
- Requires at least one data row beyond header.
- Returns structured `ValidationResult` with error aggregation.

## Incident Severity Usage

- LOW: routine minor events.
- MEDIUM: permission denials, unauthorized page hits.
- HIGH: integrity mismatch, account locked threshold.
- CRITICAL: reserved for escalated definitions (extend rules as needed).

## Directory Overview (Simplified)

```
src/main/java/com/tmc/system/tmc_secure_system/
  config/               # Crypto + data seed + security chain
  controller/           # Role dashboards + workflow endpoints
  dto/                  # Lightweight projections (AssignmentView)
  entity/               # JPA entities (core domain)
  entity/enums/         # Enumerations (roles, statuses, actions)
  repository/           # Spring Data JPA repos + specifications
  security/             # Authentication handlers & listeners
  service/              # Encryption, file storage, logging, assignments, audits
  util/                 # Date parsing, user lookup, UI formatting
  util/crypto/          # Hash utilities
  util/excel/           # Excel validation
resources/
  templates/            # Thymeleaf views
  static/css/           # Unified styling
```

## Running Locally

1. Start PostgreSQL (Docker):
   ```
   docker compose up -d
   ```
2. Build & Run:
   ```
   mvn clean package
   mvn spring-boot:run
   ```
3. Login with seeded accounts (see `DataInitializer`):
   - admin / Admin@123
   - operations / OT@12345
   - analyst / Analyst@123
   - compliance / Compliance@123
   - ciso / Ciso@12345

## Environment / Configuration

`application.yml` holds DB + AES key (Base64). For production:
- Move secret to vault (HashiCorp Vault / AWS KMS).
- Remove `show-sql`.
- Enable Flyway migrations and version schema explicitly.

| API Layer | Add REST controllers returning JSON for integration with external tools |

## Testing Suggestions

- Unit: EncryptionService (IV length, tag, integrity fail path).
- Integration: Upload → validate → decrypt round-trip.
- Security: Lockout sequence with 5 failures, subsequent denial.
- Repository specs: filter edge cases (date bounds, severity null).

## Disclaimer

This implementation is educational/demo grade. Production deployments must include formal secret management, structured monitoring, stricter input sanitization, and compliance controls (e.g. immutable audit log guarantees).

---

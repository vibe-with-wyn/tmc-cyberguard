# Architecture & Security Overview

## 1. High-Level Flow

1. OT Operator uploads Excel.
2. Validation (header canonicalization).
3. Encryption (AES-GCM 256) → persist `EncryptedFile`.
4. Admin assigns file (implicit DECRYPT permission) to Analyst.
5. Analyst views assignments → decrypt/download (integrity + auth tag verified).
6. Compliance exports logs; CISO oversees global KPIs.
7. All actions produce structured Audit or Incident logs for traceability.

## 2. Encryption Model

Components: `EncryptionService`, `DecryptionService`, `CryptoConfig`, `HashUtil`, `EncryptedFile` entity.

- Cipher: AES/GCM/NoPadding (AES‑256).
- IV: 12 bytes (random per encryption).
- Auth Tag: validated by GCM on decrypt.
- Additional integrity: SHA‑256 of ciphertext stored in `fileHash`.
- Key reference: `aesKeyRef` stored for the active key.

`EncryptedFile` stores:
- filename, contentType, size
- ciphertext (encrypted bytes)
- iv (Base64)
- fileHash (Base64 SHA‑256 of ciphertext)
- aesKeyRef, algorithm
- uploader linkage and timestamps

## 3. Authentication & Session

Components: `SecurityConfig`, `CustomUserDetailsService`, `RoleBasedAuthSuccessHandler`, `AuthEventListener`.

- Users authenticated via username/email + password (BCrypt hashes).
- Login success/failure audited.
- Account lockout after repeated failures (with unlock window).
- CSRF protection enabled for form posts.

## 4. Authorization & Routing

Components: `SecurityConfig`, `DashboardResolver`, controllers under `/api/*`.

- Role-based URL access:
  - Admin → `/api/admin/**`
  - IT Analyst → `/api/analyst/**`
  - OT Operator → `/api/ot/**`
  - Compliance → `/api/compliance/**`
  - CISO → `/api/ciso/**`
- Login success redirects via `DashboardResolver`.
- Access denied handled by `LoggingAccessDeniedHandler` (incident entry).

## 5. Logging Model

Entities: `AuditLog`, `IncidentLog`.

- AuditLog (normal activity): LOGIN_SUCCESS, LOGOUT, FILE_UPLOAD_STORED, VALIDATION_FAILED, DECRYPTION_SUCCESS, ADMIN_ACTION, LOGS_EXPORTED.
- IncidentLog (security-relevant): unauthorized access, account lock events, integrity failures.
- `LogHelper` enriches with actor, IP, session where available.

## 6. File Assignment

Components: `AssignmentService`, `FileAssignment`, `AssignmentStatus`.

- Admin assigns files to analysts.
- Permission model simplified: DECRYPT only.
- Revocation supported (status → REVOKED).
- Optional expiry field on assignment.

## 7. Excel Validation

Component: `ExcelValidator`.

- Canonicalizes header names (lowercase, strip non-alphanumerics).
- Verifies all required headers exist.
- Requires at least one data row after header.
- Returns a structured `ValidationResult`.

## 8. Dashboards & Pagination

- Admin, Compliance, CISO dashboards display Incident and Audit logs.
- Independent pagination parameters:
  - Incidents: `incPage`, `incSize`
  - Audits: `audPage`, `audSize`
- Severity badges used in tables:
  - CRITICAL → danger
  - HIGH → warning
  - MEDIUM → secondary
  - LOW → light

## 9. Security Controls Implemented

| Control | Implementation |
|--------|-----------------|
| Password hashing | BCrypt |
| CSRF | Enabled for form submissions |
| RBAC | Role-based URL patterns + controller guards |
| Account lockout | Count failures → temporary lock |
| Encryption at rest | AES‑256 GCM with per-file IV |
| Integrity | GCM tag + SHA‑256 ciphertext hash |
| Audit trail | AuditLog + IncidentLog entries with context |

## 10. Known Limits (Current Demo)

- Single AES key reference (no rotation mechanism).
- Assignment expiry is optional; enforcement depends on checks at access time.
- Logs are stored in the application database.

## 11. Summary

The system encrypts uploaded files using AES‑GCM, stores necessary metadata for decryption and integrity, restricts access via per-file assignments, and records both audit and incident events. Role-specific dashboards provide filtered, paginated visibility into system activity.

---
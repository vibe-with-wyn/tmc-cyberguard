# TMC Secure System — Guide

This guide explains what the system does, who uses it, and how to use each screen without technical terms.

## What the system does

- Keeps uploaded Excel files safe by locking them with strong encryption.
- Lets an Admin give specific Analysts permission to download a file.
- Tracks what happens in the system using two types of logs:
  - Audit Logs: normal activities (like login, upload, download).
  - Incident Logs: important security events (like denied access or lockouts).
- Shows dashboards for different roles: Admin, OT Operator, IT Analyst, Compliance, and CISO.

## Who uses it and what they do

- OT Operator: uploads Excel data files.
- Admin: manages users and gives Analysts access to specific files.
- IT Analyst: downloads assigned files.
- Compliance Officer: reviews and exports logs.
- CISO: sees overall status and key security indicators.

## How files are protected

- When a file is uploaded, the system locks it with a digital lock (encryption).
- Each file uses a unique “lock code” so files can’t be mixed up.
- A fingerprint (hash) is saved for the encrypted file to detect tampering.
- Only when the Admin grants access can an Analyst download a file. All activity is recorded in logs.

## Logging in

1. Go to the Login page.
2. Enter your username (or email) and password.
3. If you enter the wrong password too many times, your account is locked for a while.
4. After login, you are sent to your role’s dashboard automatically.

## Dashboards

All dashboards share a simple, clean layout with a top bar and a Logout button.

### OT Operator

Purpose: Upload Excel (.xlsx) files.

Steps:
1. Go to the “Upload OT Excel Data” section.
2. Choose a .xlsx file.
3. Submit.
4. If required headers are missing, you see an error. When accepted, the file is stored securely.

Required headers (first row in Excel):
- Timestamp, MachineID, Temperature (°C), Pressure (bar), Vibration (Hz), OutputRate (units/hr), EnergyConsumption (kWh), OperatorID, Status

### Admin

Purpose: Manage users, assign files to Analysts, view logs.

- User Management:
  - Create users with a role.
  - Lock/Unlock users.
  - Reset passwords.

- Assign Files to Analysts:
  - Pick a File and an Analyst.
  - Permission is “DECRYPT (default)” which means the Analyst can “Decrypt & Download.”
  - Optional: set an expiry date.
  - You can revoke an assignment later.

- Logs:
  - Filter by user, severity, and date.
  - Two tables:
    - Incident Logs (security events)
    - Audit Logs (normal actions)
  - Each table has its own pagination (Next/Prev) so they don’t affect each other.
  - Severity labels:
    - CRITICAL (red), HIGH (yellow), MEDIUM (gray), LOW (light)

### IT Analyst

Purpose: Download assigned files.

Steps:
1. See “Active Assignments.”
2. Click “Decrypt & Download” for a file you’ve been assigned.
3. If your access is revoked or expired, you’ll be denied.

Note: Analysts only have one action—download assigned files. No other views or controls.

### Compliance Officer

Purpose: Review and export logs.

- Filters at the top (user, severity, date range).
- Incident Logs table with severity labels.
- Audit Logs table for normal activity.
- Export CSV buttons for both.
- Incident and Audit tables paginate separately.

### CISO

Purpose: See key security indicators and recent activity.

- KPI cards (counts of files, assignments, open incidents, locked users).
- Two tables: Incident Logs and Audit Logs (with separate pagination).
- Severity labels in the Incident table.

## Understanding the logs

- Audit Logs (normal):
  - Login success, Logout
  - File accepted and stored
  - Validation failed (e.g., Excel headers missing)
  - Decryption/Download success
  - Admin actions (like assigning/revoking access)
  - Log exports

- Incident Logs (security-important):
  - Unauthorized access (trying to open something you shouldn’t)
  - Account lockout after many failed logins
  - Integrity problems (file doesn’t match its saved fingerprint)

## Common questions

- Why can’t I download a file?
  - You need an active assignment from the Admin. If it was revoked or expired, access is denied.

- Why was my account locked?
  - Too many wrong password attempts. Try again later or ask the Admin.

- Why is my Excel rejected?
  - The first row must contain all required headers, exactly as shown above. Also ensure there’s at least one data row.

## Visual cues

- Severity labels:
  - CRITICAL = red
  - HIGH = yellow
  - MEDIUM = gray
  - LOW = light
- Separate Next/Prev controls for Incident and Audit logs ensure one does not change the other.

## Summary

- Files are kept safe with strong encryption.
- Admin decides which Analyst can download which file.
- All actions are recorded.
- Each role has a simple dashboard to do their tasks.
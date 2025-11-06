-- Update incident_logs.event_type check list to include new values
ALTER TABLE incident_logs DROP CONSTRAINT IF EXISTS incident_logs_event_type_check;

ALTER TABLE incident_logs
  ADD CONSTRAINT incident_logs_event_type_check
  CHECK (event_type IN (
    'FAILED_LOGIN',
    'UNAUTHORIZED_ACCESS',
    'FILE_TAMPER',
    'INTEGRITY_MISMATCH',
    'DECRYPTION_DENIED',
    'LOGIN_SUCCESS',
    'ACCOUNT_LOCKED',
    'LOGOUT'
  ));
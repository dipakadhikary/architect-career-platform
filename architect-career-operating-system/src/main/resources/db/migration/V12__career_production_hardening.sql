-- Career production hardening: soft-delete-aware uniqueness and query indexes.
--
-- Replaces hard unique constraints that conflict with soft-delete reuse of names/emails,
-- and enforces at most one PENDING non-archived offer per application.

ALTER TABLE acos.career_companies
    DROP CONSTRAINT IF EXISTS uk_career_companies_owner_name;

CREATE UNIQUE INDEX IF NOT EXISTS uk_career_companies_owner_name_active
    ON acos.career_companies (owner_id, name)
    WHERE archived = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_career_recruiters_owner_email_active
    ON acos.career_recruiters (owner_id, lower(email))
    WHERE email IS NOT NULL AND archived = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_career_offers_application_pending_active
    ON acos.career_offers (application_id)
    WHERE archived = FALSE AND offer_status = 'PENDING';

CREATE INDEX IF NOT EXISTS ix_career_interviews_application_round
    ON acos.career_interviews (application_id, interview_round);

COMMENT ON INDEX acos.uk_career_companies_owner_name_active IS
    'Active company names are unique per owner';
COMMENT ON INDEX acos.uk_career_recruiters_owner_email_active IS
    'Active recruiter emails are unique per owner';
COMMENT ON INDEX acos.uk_career_offers_application_pending_active IS
    'At most one pending non-archived offer per application';

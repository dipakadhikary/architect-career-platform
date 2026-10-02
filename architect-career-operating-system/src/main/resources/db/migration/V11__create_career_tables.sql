-- Career module soft-delete alignment.
--
-- Logical tables (companies, recruiters, job_applications, interviews, offers,
-- application_status_history) were created in V8/V9 as acos.career_*.
-- This migration completes soft-delete support on every career aggregate.

ALTER TABLE acos.career_companies
    ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

ALTER TABLE acos.career_recruiters
    ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

ALTER TABLE acos.career_job_applications
    ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

ALTER TABLE acos.career_interviews
    ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

ALTER TABLE acos.career_offers
    ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

ALTER TABLE acos.career_application_status_history
    ADD COLUMN IF NOT EXISTS archived BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS ix_career_companies_owner_archived
    ON acos.career_companies (owner_id, archived);

CREATE INDEX IF NOT EXISTS ix_career_recruiters_owner_archived
    ON acos.career_recruiters (owner_id, archived);

CREATE INDEX IF NOT EXISTS ix_career_job_applications_owner_archived
    ON acos.career_job_applications (owner_id, archived);

CREATE INDEX IF NOT EXISTS ix_career_interviews_application_archived
    ON acos.career_interviews (application_id, archived);

CREATE INDEX IF NOT EXISTS ix_career_offers_application_archived
    ON acos.career_offers (application_id, archived);

CREATE INDEX IF NOT EXISTS ix_career_app_status_history_application_archived
    ON acos.career_application_status_history (application_id, archived);

COMMENT ON COLUMN acos.career_companies.archived IS 'Soft-delete flag';
COMMENT ON COLUMN acos.career_recruiters.archived IS 'Soft-delete flag';
COMMENT ON COLUMN acos.career_job_applications.archived IS 'Soft-delete flag';
COMMENT ON COLUMN acos.career_interviews.archived IS 'Soft-delete flag';
COMMENT ON COLUMN acos.career_offers.archived IS 'Soft-delete flag';
COMMENT ON COLUMN acos.career_application_status_history.archived IS 'Soft-delete flag';

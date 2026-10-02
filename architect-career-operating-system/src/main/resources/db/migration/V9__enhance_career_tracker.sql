-- Enhance career tracker: soft archive, status machine statuses, AI-ready interviews,
-- rich offers/recruiters, status history, and business audit log.

DROP TABLE IF EXISTS acos.career_offers CASCADE;
DROP TABLE IF EXISTS acos.career_interviews CASCADE;
DROP TABLE IF EXISTS acos.career_job_applications CASCADE;
DROP TABLE IF EXISTS acos.career_recruiters CASCADE;
DROP TABLE IF EXISTS acos.career_companies CASCADE;

CREATE TABLE acos.career_companies (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    name         VARCHAR(200)             NOT NULL,
    website      VARCHAR(500),
    industry     VARCHAR(100),
    location     VARCHAR(200),
    notes        VARCHAR(2000),
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_career_companies PRIMARY KEY (id),
    CONSTRAINT uk_career_companies_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_career_companies_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_career_companies_owner_id ON acos.career_companies (owner_id);
CREATE INDEX ix_career_companies_owner_industry ON acos.career_companies (owner_id, industry);

CREATE TABLE acos.career_recruiters (
    id                   UUID                     NOT NULL,
    owner_id             UUID                     NOT NULL,
    company_id           UUID,
    full_name            VARCHAR(200)             NOT NULL,
    email                VARCHAR(320),
    phone                VARCHAR(50),
    linkedin_url         VARCHAR(500),
    last_contact_date    DATE,
    next_follow_up_date  DATE,
    status               VARCHAR(32)              NOT NULL,
    notes                VARCHAR(2000),
    created_at           TIMESTAMPTZ              NOT NULL,
    updated_at           TIMESTAMPTZ              NOT NULL,
    version              BIGINT                   NOT NULL,
    CONSTRAINT pk_career_recruiters PRIMARY KEY (id),
    CONSTRAINT fk_career_recruiters_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_career_recruiters_company_id FOREIGN KEY (company_id)
        REFERENCES acos.career_companies (id) ON DELETE SET NULL,
    CONSTRAINT ck_career_recruiters_status CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'DO_NOT_CONTACT'))
);

CREATE INDEX ix_career_recruiters_owner_id ON acos.career_recruiters (owner_id);
CREATE INDEX ix_career_recruiters_company_id ON acos.career_recruiters (company_id);
CREATE INDEX ix_career_recruiters_owner_follow_up
    ON acos.career_recruiters (owner_id, next_follow_up_date);

CREATE TABLE acos.career_job_applications (
    id                     UUID                     NOT NULL,
    owner_id               UUID                     NOT NULL,
    company_id             UUID                     NOT NULL,
    recruiter_id           UUID,
    title                  VARCHAR(200)             NOT NULL,
    job_description        VARCHAR(10000),
    source                 VARCHAR(100),
    status                 VARCHAR(40)              NOT NULL,
    salary_expectation     NUMERIC(12, 2),
    currency               VARCHAR(3),
    resume_version         VARCHAR(100),
    applied_on             DATE                     NOT NULL,
    location               VARCHAR(200),
    job_url                VARCHAR(500),
    notes                  VARCHAR(2000),
    archived               BOOLEAN                  NOT NULL DEFAULT FALSE,
    archived_at            TIMESTAMPTZ,
    created_at             TIMESTAMPTZ              NOT NULL,
    updated_at             TIMESTAMPTZ              NOT NULL,
    version                BIGINT                   NOT NULL,
    CONSTRAINT pk_career_job_applications PRIMARY KEY (id),
    CONSTRAINT fk_career_job_applications_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_career_job_applications_company_id FOREIGN KEY (company_id)
        REFERENCES acos.career_companies (id),
    CONSTRAINT fk_career_job_applications_recruiter_id FOREIGN KEY (recruiter_id)
        REFERENCES acos.career_recruiters (id) ON DELETE SET NULL,
    CONSTRAINT ck_career_job_applications_status CHECK (
        status IN (
            'DRAFT',
            'APPLIED',
            'SCREENING',
            'TECHNICAL_INTERVIEW',
            'MANAGER_INTERVIEW',
            'HR_INTERVIEW',
            'OFFER',
            'ACCEPTED',
            'DECLINED',
            'REJECTED',
            'WITHDRAWN'
        ))
);

CREATE INDEX ix_career_job_applications_owner_id
    ON acos.career_job_applications (owner_id);
CREATE INDEX ix_career_job_applications_company_id
    ON acos.career_job_applications (company_id);
CREATE INDEX ix_career_job_applications_recruiter_id
    ON acos.career_job_applications (recruiter_id);
CREATE INDEX ix_career_job_applications_owner_status
    ON acos.career_job_applications (owner_id, status);
CREATE INDEX ix_career_job_applications_owner_applied_on
    ON acos.career_job_applications (owner_id, applied_on);
CREATE INDEX ix_career_job_applications_owner_archived
    ON acos.career_job_applications (owner_id, archived);
CREATE INDEX ix_career_job_applications_owner_salary
    ON acos.career_job_applications (owner_id, salary_expectation);

CREATE TABLE acos.career_interviews (
    id                       UUID                     NOT NULL,
    application_id           UUID                     NOT NULL,
    interview_round          VARCHAR(40)              NOT NULL,
    interviewer              VARCHAR(200),
    interview_date           TIMESTAMPTZ              NOT NULL,
    duration_minutes         INTEGER,
    status                   VARCHAR(32)              NOT NULL,
    rating                   INTEGER,
    feedback                 VARCHAR(4000),
    questions_asked          VARCHAR(10000),
    strengths                VARCHAR(4000),
    weaknesses               VARCHAR(4000),
    improvement_areas        VARCHAR(4000),
    candidate_notes          VARCHAR(4000),
    confidence_rating        INTEGER,
    interview_reminder_date  TIMESTAMPTZ,
    location_or_link         VARCHAR(500),
    notes                    VARCHAR(2000),
    created_at               TIMESTAMPTZ              NOT NULL,
    updated_at               TIMESTAMPTZ              NOT NULL,
    version                  BIGINT                   NOT NULL,
    CONSTRAINT pk_career_interviews PRIMARY KEY (id),
    CONSTRAINT fk_career_interviews_application_id FOREIGN KEY (application_id)
        REFERENCES acos.career_job_applications (id),
    CONSTRAINT ck_career_interviews_round CHECK (
        interview_round IN (
            'SCREENING',
            'TECHNICAL',
            'MANAGER',
            'HR',
            'FINAL',
            'OTHER'
        )),
    CONSTRAINT ck_career_interviews_status CHECK (
        status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED')),
    CONSTRAINT ck_career_interviews_rating CHECK (rating IS NULL OR (rating BETWEEN 1 AND 5)),
    CONSTRAINT ck_career_interviews_confidence CHECK (
        confidence_rating IS NULL OR (confidence_rating BETWEEN 1 AND 5)),
    CONSTRAINT ck_career_interviews_duration CHECK (
        duration_minutes IS NULL OR duration_minutes > 0)
);

CREATE INDEX ix_career_interviews_application_id ON acos.career_interviews (application_id);
CREATE INDEX ix_career_interviews_interview_date ON acos.career_interviews (interview_date);
CREATE INDEX ix_career_interviews_round ON acos.career_interviews (interview_round);
CREATE INDEX ix_career_interviews_reminder ON acos.career_interviews (interview_reminder_date);

CREATE TABLE acos.career_offers (
    id                  UUID                     NOT NULL,
    application_id      UUID                     NOT NULL,
    base_salary         NUMERIC(12, 2)           NOT NULL,
    currency            VARCHAR(3)               NOT NULL,
    joining_bonus       NUMERIC(12, 2),
    annual_bonus        NUMERIC(12, 2),
    stock_options       VARCHAR(500),
    location            VARCHAR(200),
    work_mode           VARCHAR(32),
    joining_date        DATE,
    notice_period_days  INTEGER,
    offer_status        VARCHAR(32)              NOT NULL,
    offer_expiry_date   DATE,
    notes               VARCHAR(2000),
    created_at          TIMESTAMPTZ              NOT NULL,
    updated_at          TIMESTAMPTZ              NOT NULL,
    version             BIGINT                   NOT NULL,
    CONSTRAINT pk_career_offers PRIMARY KEY (id),
    CONSTRAINT fk_career_offers_application_id FOREIGN KEY (application_id)
        REFERENCES acos.career_job_applications (id),
    CONSTRAINT ck_career_offers_status CHECK (
        offer_status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED', 'WITHDRAWN')),
    CONSTRAINT ck_career_offers_work_mode CHECK (
        work_mode IS NULL OR work_mode IN ('ONSITE', 'REMOTE', 'HYBRID')),
    CONSTRAINT ck_career_offers_notice CHECK (
        notice_period_days IS NULL OR notice_period_days >= 0)
);

CREATE INDEX ix_career_offers_application_id ON acos.career_offers (application_id);
CREATE INDEX ix_career_offers_status ON acos.career_offers (offer_status);
CREATE INDEX ix_career_offers_expiry ON acos.career_offers (offer_expiry_date);

CREATE TABLE acos.career_application_status_history (
    id               UUID                     NOT NULL,
    application_id   UUID                     NOT NULL,
    old_status       VARCHAR(40),
    new_status       VARCHAR(40)              NOT NULL,
    changed_at       TIMESTAMPTZ              NOT NULL,
    changed_by       UUID                     NOT NULL,
    comments         VARCHAR(2000),
    created_at       TIMESTAMPTZ              NOT NULL,
    updated_at       TIMESTAMPTZ              NOT NULL,
    version          BIGINT                   NOT NULL,
    CONSTRAINT pk_career_application_status_history PRIMARY KEY (id),
    CONSTRAINT fk_career_app_status_history_application_id FOREIGN KEY (application_id)
        REFERENCES acos.career_job_applications (id),
    CONSTRAINT fk_career_app_status_history_changed_by FOREIGN KEY (changed_by)
        REFERENCES acos.users (id),
    CONSTRAINT ck_career_app_status_history_new_status CHECK (
        new_status IN (
            'DRAFT',
            'APPLIED',
            'SCREENING',
            'TECHNICAL_INTERVIEW',
            'MANAGER_INTERVIEW',
            'HR_INTERVIEW',
            'OFFER',
            'ACCEPTED',
            'DECLINED',
            'REJECTED',
            'WITHDRAWN'
        )),
    CONSTRAINT ck_career_app_status_history_old_status CHECK (
        old_status IS NULL OR old_status IN (
            'DRAFT',
            'APPLIED',
            'SCREENING',
            'TECHNICAL_INTERVIEW',
            'MANAGER_INTERVIEW',
            'HR_INTERVIEW',
            'OFFER',
            'ACCEPTED',
            'DECLINED',
            'REJECTED',
            'WITHDRAWN'
        ))
);

CREATE INDEX ix_career_app_status_history_application_id
    ON acos.career_application_status_history (application_id);
CREATE INDEX ix_career_app_status_history_changed_at
    ON acos.career_application_status_history (changed_at);

CREATE TABLE acos.career_audit_logs (
    id            UUID                     NOT NULL,
    owner_id      UUID                     NOT NULL,
    actor_id      UUID                     NOT NULL,
    action        VARCHAR(64)              NOT NULL,
    entity_type   VARCHAR(64)              NOT NULL,
    entity_id     UUID                     NOT NULL,
    details       VARCHAR(2000),
    occurred_at   TIMESTAMPTZ              NOT NULL,
    created_at    TIMESTAMPTZ              NOT NULL,
    updated_at    TIMESTAMPTZ              NOT NULL,
    version       BIGINT                   NOT NULL,
    CONSTRAINT pk_career_audit_logs PRIMARY KEY (id),
    CONSTRAINT fk_career_audit_logs_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_career_audit_logs_actor_id FOREIGN KEY (actor_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_career_audit_logs_owner_id ON acos.career_audit_logs (owner_id);
CREATE INDEX ix_career_audit_logs_entity ON acos.career_audit_logs (entity_type, entity_id);
CREATE INDEX ix_career_audit_logs_occurred_at ON acos.career_audit_logs (occurred_at);

COMMENT ON TABLE acos.career_companies IS 'User-owned companies for job tracking';
COMMENT ON TABLE acos.career_recruiters IS 'User-owned recruiter contacts with follow-up dates';
COMMENT ON TABLE acos.career_job_applications IS 'User-owned job applications with soft-archive support';
COMMENT ON TABLE acos.career_interviews IS 'Interviews with AI-ready feedback fields';
COMMENT ON TABLE acos.career_offers IS 'Offers associated with job applications';
COMMENT ON TABLE acos.career_application_status_history IS 'Immutable application status transition history';
COMMENT ON TABLE acos.career_audit_logs IS 'Career business-action audit trail';

-- Career tracker: companies, recruiters, job applications, interviews, offers.

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

CREATE TABLE acos.career_recruiters (
    id            UUID                     NOT NULL,
    owner_id      UUID                     NOT NULL,
    company_id    UUID,
    full_name     VARCHAR(200)             NOT NULL,
    email         VARCHAR(320),
    phone         VARCHAR(50),
    linkedin_url  VARCHAR(500),
    notes         VARCHAR(2000),
    created_at    TIMESTAMPTZ              NOT NULL,
    updated_at    TIMESTAMPTZ              NOT NULL,
    version       BIGINT                   NOT NULL,
    CONSTRAINT pk_career_recruiters PRIMARY KEY (id),
    CONSTRAINT fk_career_recruiters_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_career_recruiters_company_id FOREIGN KEY (company_id)
        REFERENCES acos.career_companies (id) ON DELETE SET NULL
);

CREATE INDEX ix_career_recruiters_owner_id ON acos.career_recruiters (owner_id);
CREATE INDEX ix_career_recruiters_company_id ON acos.career_recruiters (company_id);

CREATE TABLE acos.career_job_applications (
    id                   UUID                     NOT NULL,
    owner_id             UUID                     NOT NULL,
    company_id           UUID                     NOT NULL,
    recruiter_id         UUID,
    role_title           VARCHAR(200)             NOT NULL,
    status               VARCHAR(32)              NOT NULL,
    source               VARCHAR(100),
    job_url              VARCHAR(500),
    location             VARCHAR(200),
    salary_expectation   NUMERIC(12, 2),
    currency             VARCHAR(3),
    applied_on           DATE                     NOT NULL,
    notes                VARCHAR(2000),
    created_at           TIMESTAMPTZ              NOT NULL,
    updated_at           TIMESTAMPTZ              NOT NULL,
    version              BIGINT                   NOT NULL,
    CONSTRAINT pk_career_job_applications PRIMARY KEY (id),
    CONSTRAINT fk_career_job_applications_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_career_job_applications_company_id FOREIGN KEY (company_id)
        REFERENCES acos.career_companies (id),
    CONSTRAINT fk_career_job_applications_recruiter_id FOREIGN KEY (recruiter_id)
        REFERENCES acos.career_recruiters (id) ON DELETE SET NULL,
    CONSTRAINT ck_career_job_applications_status CHECK (
        status IN ('DRAFT', 'APPLIED', 'SCREENING', 'INTERVIEWING', 'OFFERED', 'ACCEPTED', 'REJECTED', 'WITHDRAWN'))
);

CREATE INDEX ix_career_job_applications_owner_id ON acos.career_job_applications (owner_id);
CREATE INDEX ix_career_job_applications_company_id ON acos.career_job_applications (company_id);
CREATE INDEX ix_career_job_applications_owner_status ON acos.career_job_applications (owner_id, status);

CREATE TABLE acos.career_interviews (
    id                UUID                     NOT NULL,
    application_id    UUID                     NOT NULL,
    scheduled_at      TIMESTAMPTZ              NOT NULL,
    interview_type    VARCHAR(32)              NOT NULL,
    status            VARCHAR(32)              NOT NULL,
    location_or_link  VARCHAR(500),
    interviewer_name  VARCHAR(200),
    feedback          VARCHAR(4000),
    notes             VARCHAR(2000),
    created_at        TIMESTAMPTZ              NOT NULL,
    updated_at        TIMESTAMPTZ              NOT NULL,
    version           BIGINT                   NOT NULL,
    CONSTRAINT pk_career_interviews PRIMARY KEY (id),
    CONSTRAINT fk_career_interviews_application_id FOREIGN KEY (application_id)
        REFERENCES acos.career_job_applications (id) ON DELETE CASCADE,
    CONSTRAINT ck_career_interviews_type CHECK (
        interview_type IN ('PHONE', 'VIDEO', 'ONSITE', 'TECHNICAL', 'BEHAVIORAL', 'HIRING_MANAGER', 'FINAL')),
    CONSTRAINT ck_career_interviews_status CHECK (
        status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED'))
);

CREATE INDEX ix_career_interviews_application_id ON acos.career_interviews (application_id);
CREATE INDEX ix_career_interviews_scheduled_at ON acos.career_interviews (scheduled_at);

CREATE TABLE acos.career_offers (
    id                UUID                     NOT NULL,
    application_id    UUID                     NOT NULL,
    base_salary       NUMERIC(12, 2)           NOT NULL,
    currency          VARCHAR(3)               NOT NULL,
    equity            VARCHAR(200),
    bonus             NUMERIC(12, 2),
    benefits          VARCHAR(2000),
    offered_on        DATE                     NOT NULL,
    expires_on        DATE,
    status            VARCHAR(32)              NOT NULL,
    notes             VARCHAR(2000),
    created_at        TIMESTAMPTZ              NOT NULL,
    updated_at        TIMESTAMPTZ              NOT NULL,
    version           BIGINT                   NOT NULL,
    CONSTRAINT pk_career_offers PRIMARY KEY (id),
    CONSTRAINT fk_career_offers_application_id FOREIGN KEY (application_id)
        REFERENCES acos.career_job_applications (id) ON DELETE CASCADE,
    CONSTRAINT ck_career_offers_status CHECK (
        status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED', 'WITHDRAWN'))
);

CREATE INDEX ix_career_offers_application_id ON acos.career_offers (application_id);

COMMENT ON TABLE acos.career_companies IS 'User-owned companies for job tracking';
COMMENT ON TABLE acos.career_recruiters IS 'User-owned recruiter contacts';
COMMENT ON TABLE acos.career_job_applications IS 'User-owned job applications';
COMMENT ON TABLE acos.career_interviews IS 'Interviews scheduled for job applications';
COMMENT ON TABLE acos.career_offers IS 'Offers associated with job applications';

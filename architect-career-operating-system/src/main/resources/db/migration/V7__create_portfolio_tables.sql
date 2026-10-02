-- Portfolio management: projects, technologies, skills, achievements, certifications.

CREATE TABLE acos.portfolio_technologies (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    name         VARCHAR(100)             NOT NULL,
    category     VARCHAR(100),
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_portfolio_technologies PRIMARY KEY (id),
    CONSTRAINT uk_portfolio_technologies_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_portfolio_technologies_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_portfolio_technologies_owner_id ON acos.portfolio_technologies (owner_id);

CREATE TABLE acos.portfolio_projects (
    id              UUID                     NOT NULL,
    owner_id        UUID                     NOT NULL,
    title           VARCHAR(200)             NOT NULL,
    summary         VARCHAR(500)             NOT NULL,
    description     TEXT                     NOT NULL,
    repository_url  VARCHAR(500),
    live_url        VARCHAR(500),
    status          VARCHAR(32)              NOT NULL,
    start_date      DATE,
    end_date        DATE,
    created_at      TIMESTAMPTZ              NOT NULL,
    updated_at      TIMESTAMPTZ              NOT NULL,
    version         BIGINT                   NOT NULL,
    CONSTRAINT pk_portfolio_projects PRIMARY KEY (id),
    CONSTRAINT fk_portfolio_projects_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT ck_portfolio_projects_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);

CREATE INDEX ix_portfolio_projects_owner_id ON acos.portfolio_projects (owner_id);
CREATE INDEX ix_portfolio_projects_owner_status ON acos.portfolio_projects (owner_id, status);
CREATE INDEX ix_portfolio_projects_title ON acos.portfolio_projects (owner_id, title);
CREATE INDEX ix_portfolio_projects_summary ON acos.portfolio_projects (owner_id, summary);

CREATE TABLE acos.portfolio_project_technologies (
    project_id     UUID NOT NULL,
    technology_id  UUID NOT NULL,
    CONSTRAINT pk_portfolio_project_technologies PRIMARY KEY (project_id, technology_id),
    CONSTRAINT fk_portfolio_project_technologies_project_id FOREIGN KEY (project_id)
        REFERENCES acos.portfolio_projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_portfolio_project_technologies_technology_id FOREIGN KEY (technology_id)
        REFERENCES acos.portfolio_technologies (id) ON DELETE CASCADE
);

CREATE INDEX ix_portfolio_project_technologies_technology_id
    ON acos.portfolio_project_technologies (technology_id);

CREATE TABLE acos.portfolio_skills (
    id                    UUID                     NOT NULL,
    owner_id              UUID                     NOT NULL,
    name                  VARCHAR(100)             NOT NULL,
    proficiency_level     VARCHAR(32)              NOT NULL,
    years_of_experience   NUMERIC(4, 1),
    description           VARCHAR(1000),
    created_at            TIMESTAMPTZ              NOT NULL,
    updated_at            TIMESTAMPTZ              NOT NULL,
    version               BIGINT                   NOT NULL,
    CONSTRAINT pk_portfolio_skills PRIMARY KEY (id),
    CONSTRAINT uk_portfolio_skills_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_portfolio_skills_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT ck_portfolio_skills_proficiency CHECK (
        proficiency_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT'))
);

CREATE INDEX ix_portfolio_skills_owner_id ON acos.portfolio_skills (owner_id);

CREATE TABLE acos.portfolio_achievements (
    id             UUID                     NOT NULL,
    owner_id       UUID                     NOT NULL,
    title          VARCHAR(200)             NOT NULL,
    description    VARCHAR(2000)            NOT NULL,
    achieved_on    DATE                     NOT NULL,
    organization   VARCHAR(200),
    created_at     TIMESTAMPTZ              NOT NULL,
    updated_at     TIMESTAMPTZ              NOT NULL,
    version        BIGINT                   NOT NULL,
    CONSTRAINT pk_portfolio_achievements PRIMARY KEY (id),
    CONSTRAINT fk_portfolio_achievements_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_portfolio_achievements_owner_id ON acos.portfolio_achievements (owner_id);

CREATE TABLE acos.portfolio_certifications (
    id              UUID                     NOT NULL,
    owner_id        UUID                     NOT NULL,
    name            VARCHAR(200)             NOT NULL,
    issuer          VARCHAR(200)             NOT NULL,
    credential_id   VARCHAR(200),
    credential_url  VARCHAR(500),
    issued_on       DATE                     NOT NULL,
    expires_on      DATE,
    created_at      TIMESTAMPTZ              NOT NULL,
    updated_at      TIMESTAMPTZ              NOT NULL,
    version         BIGINT                   NOT NULL,
    CONSTRAINT pk_portfolio_certifications PRIMARY KEY (id),
    CONSTRAINT fk_portfolio_certifications_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_portfolio_certifications_owner_id ON acos.portfolio_certifications (owner_id);

COMMENT ON TABLE acos.portfolio_projects IS 'User-owned portfolio showcase projects';
COMMENT ON TABLE acos.portfolio_technologies IS 'User-scoped technology catalog for projects';
COMMENT ON TABLE acos.portfolio_project_technologies IS 'Project to technology membership';
COMMENT ON TABLE acos.portfolio_skills IS 'User-owned professional skills';
COMMENT ON TABLE acos.portfolio_achievements IS 'User-owned career achievements';
COMMENT ON TABLE acos.portfolio_certifications IS 'User-owned professional certifications';

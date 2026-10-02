-- Learning roadmap: plans, milestones, and topics.

CREATE TABLE acos.learning_plans (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    title        VARCHAR(200)             NOT NULL,
    description  VARCHAR(2000),
    status       VARCHAR(32)              NOT NULL,
    target_date  DATE,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_learning_plans PRIMARY KEY (id),
    CONSTRAINT fk_learning_plans_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT ck_learning_plans_status CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'ARCHIVED'))
);

CREATE INDEX ix_learning_plans_owner_id ON acos.learning_plans (owner_id);
CREATE INDEX ix_learning_plans_owner_status ON acos.learning_plans (owner_id, status);

CREATE TABLE acos.learning_milestones (
    id           UUID                     NOT NULL,
    plan_id      UUID                     NOT NULL,
    title        VARCHAR(200)             NOT NULL,
    description  VARCHAR(2000),
    sort_order   INTEGER                  NOT NULL,
    target_date  DATE,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_learning_milestones PRIMARY KEY (id),
    CONSTRAINT fk_learning_milestones_plan_id FOREIGN KEY (plan_id)
        REFERENCES acos.learning_plans (id) ON DELETE CASCADE,
    CONSTRAINT uk_learning_milestones_plan_sort UNIQUE (plan_id, sort_order)
);

CREATE INDEX ix_learning_milestones_plan_id ON acos.learning_milestones (plan_id);

CREATE TABLE acos.learning_topics (
    id             UUID                     NOT NULL,
    milestone_id   UUID                     NOT NULL,
    title          VARCHAR(200)             NOT NULL,
    description    VARCHAR(2000),
    status         VARCHAR(32)              NOT NULL,
    sort_order     INTEGER                  NOT NULL,
    created_at     TIMESTAMPTZ              NOT NULL,
    updated_at     TIMESTAMPTZ              NOT NULL,
    version        BIGINT                   NOT NULL,
    CONSTRAINT pk_learning_topics PRIMARY KEY (id),
    CONSTRAINT fk_learning_topics_milestone_id FOREIGN KEY (milestone_id)
        REFERENCES acos.learning_milestones (id) ON DELETE CASCADE,
    CONSTRAINT uk_learning_topics_milestone_sort UNIQUE (milestone_id, sort_order),
    CONSTRAINT ck_learning_topics_status CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED'))
);

CREATE INDEX ix_learning_topics_milestone_id ON acos.learning_topics (milestone_id);
CREATE INDEX ix_learning_topics_milestone_status ON acos.learning_topics (milestone_id, status);

COMMENT ON TABLE acos.learning_plans IS 'User-owned learning roadmap plans';
COMMENT ON TABLE acos.learning_milestones IS 'Ordered milestones within a learning plan';
COMMENT ON TABLE acos.learning_topics IS 'Ordered topics within a learning milestone';

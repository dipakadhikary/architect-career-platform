-- Tutorials: hierarchical topics, concept markdown, Q&A, PostgreSQL FTS.
-- FTS uses expression GIN indexes (compatible across PostgreSQL 12+ without generated-column restrictions).

CREATE TABLE acos.tutorial_topics (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    parent_id    UUID,
    title        VARCHAR(200)             NOT NULL,
    slug         VARCHAR(200)             NOT NULL,
    path         VARCHAR(2000)            NOT NULL,
    sort_order   INTEGER                  NOT NULL,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_tutorial_topics PRIMARY KEY (id),
    CONSTRAINT fk_tutorial_topics_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_tutorial_topics_parent_id FOREIGN KEY (parent_id)
        REFERENCES acos.tutorial_topics (id) ON DELETE CASCADE,
    CONSTRAINT ck_tutorial_topics_sort_order CHECK (sort_order >= 0),
    CONSTRAINT ck_tutorial_topics_slug CHECK (slug ~ '^[a-z0-9]+(-[a-z0-9]+)*$'),
    CONSTRAINT ck_tutorial_topics_path CHECK (char_length(path) > 0)
);

CREATE UNIQUE INDEX uk_tutorial_topics_owner_path
    ON acos.tutorial_topics (owner_id, path);

CREATE UNIQUE INDEX uk_tutorial_topics_owner_root_slug
    ON acos.tutorial_topics (owner_id, slug)
    WHERE parent_id IS NULL;

CREATE UNIQUE INDEX uk_tutorial_topics_owner_parent_slug
    ON acos.tutorial_topics (owner_id, parent_id, slug)
    WHERE parent_id IS NOT NULL;

CREATE INDEX ix_tutorial_topics_owner_id ON acos.tutorial_topics (owner_id);
CREATE INDEX ix_tutorial_topics_parent_id ON acos.tutorial_topics (parent_id);
CREATE INDEX ix_tutorial_topics_owner_parent_sort
    ON acos.tutorial_topics (owner_id, parent_id, sort_order);

CREATE TABLE acos.tutorial_concepts (
    id           UUID                     NOT NULL,
    topic_id     UUID                     NOT NULL,
    content      TEXT                     NOT NULL,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_tutorial_concepts PRIMARY KEY (id),
    CONSTRAINT fk_tutorial_concepts_topic_id FOREIGN KEY (topic_id)
        REFERENCES acos.tutorial_topics (id) ON DELETE CASCADE,
    CONSTRAINT uk_tutorial_concepts_topic_id UNIQUE (topic_id)
);

CREATE INDEX ix_tutorial_concepts_search_vector
    ON acos.tutorial_concepts
    USING GIN (to_tsvector('english', coalesce(content, '')));

CREATE TABLE acos.tutorial_questions (
    id           UUID                     NOT NULL,
    topic_id     UUID                     NOT NULL,
    question     TEXT                     NOT NULL,
    answer       TEXT                     NOT NULL,
    sort_order   INTEGER                  NOT NULL,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_tutorial_questions PRIMARY KEY (id),
    CONSTRAINT fk_tutorial_questions_topic_id FOREIGN KEY (topic_id)
        REFERENCES acos.tutorial_topics (id) ON DELETE CASCADE,
    CONSTRAINT ck_tutorial_questions_sort_order CHECK (sort_order >= 0),
    CONSTRAINT uk_tutorial_questions_topic_sort UNIQUE (topic_id, sort_order)
);

CREATE INDEX ix_tutorial_questions_topic_id ON acos.tutorial_questions (topic_id);
CREATE INDEX ix_tutorial_questions_search_vector
    ON acos.tutorial_questions
    USING GIN (
        to_tsvector(
            'english',
            coalesce(question, '') || ' ' || coalesce(answer, '')
        )
    );

COMMENT ON TABLE acos.tutorial_topics IS 'User-owned hierarchical tutorial topics with stable slug paths';
COMMENT ON TABLE acos.tutorial_concepts IS 'Markdown concept content per tutorial topic';
COMMENT ON TABLE acos.tutorial_questions IS 'Markdown question/answer pairs per tutorial topic';

-- Knowledge management: categories, tags, notes, and note-tag membership.

CREATE TABLE acos.categories (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    name         VARCHAR(100)             NOT NULL,
    description  VARCHAR(500),
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_categories_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_categories_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_categories_owner_id ON acos.categories (owner_id);

CREATE TABLE acos.tags (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    name         VARCHAR(50)              NOT NULL,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_tags PRIMARY KEY (id),
    CONSTRAINT uk_tags_owner_name UNIQUE (owner_id, name),
    CONSTRAINT fk_tags_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id)
);

CREATE INDEX ix_tags_owner_id ON acos.tags (owner_id);

CREATE TABLE acos.knowledge_notes (
    id           UUID                     NOT NULL,
    owner_id     UUID                     NOT NULL,
    category_id  UUID,
    title        VARCHAR(200)             NOT NULL,
    summary      VARCHAR(500)             NOT NULL,
    content      TEXT                     NOT NULL,
    created_at   TIMESTAMPTZ              NOT NULL,
    updated_at   TIMESTAMPTZ              NOT NULL,
    version      BIGINT                   NOT NULL,
    CONSTRAINT pk_knowledge_notes PRIMARY KEY (id),
    CONSTRAINT fk_knowledge_notes_owner_id FOREIGN KEY (owner_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_knowledge_notes_category_id FOREIGN KEY (category_id)
        REFERENCES acos.categories (id)
);

CREATE INDEX ix_knowledge_notes_owner_id ON acos.knowledge_notes (owner_id);
CREATE INDEX ix_knowledge_notes_category_id ON acos.knowledge_notes (category_id);
CREATE INDEX ix_knowledge_notes_title ON acos.knowledge_notes (owner_id, title);
CREATE INDEX ix_knowledge_notes_summary ON acos.knowledge_notes (owner_id, summary);

CREATE TABLE acos.knowledge_note_tags (
    note_id UUID NOT NULL,
    tag_id  UUID NOT NULL,
    CONSTRAINT pk_knowledge_note_tags PRIMARY KEY (note_id, tag_id),
    CONSTRAINT fk_knowledge_note_tags_note_id FOREIGN KEY (note_id)
        REFERENCES acos.knowledge_notes (id) ON DELETE CASCADE,
    CONSTRAINT fk_knowledge_note_tags_tag_id FOREIGN KEY (tag_id)
        REFERENCES acos.tags (id) ON DELETE CASCADE
);

CREATE INDEX ix_knowledge_note_tags_tag_id ON acos.knowledge_note_tags (tag_id);

COMMENT ON TABLE acos.categories IS 'User-scoped knowledge categories';
COMMENT ON TABLE acos.tags IS 'User-scoped knowledge tags';
COMMENT ON TABLE acos.knowledge_notes IS 'Markdown knowledge notes owned by users';
COMMENT ON TABLE acos.knowledge_note_tags IS 'Knowledge note to tag membership';

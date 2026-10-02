-- Auth identity foundation: users, roles, and membership.

CREATE TABLE acos.roles (
    id          UUID                     NOT NULL,
    name        VARCHAR(50)              NOT NULL,
    created_at  TIMESTAMPTZ              NOT NULL,
    updated_at  TIMESTAMPTZ              NOT NULL,
    version     BIGINT                   NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name),
    CONSTRAINT ck_roles_name CHECK (name IN ('USER', 'ADMIN'))
);

CREATE TABLE acos.users (
    id             UUID                     NOT NULL,
    email          VARCHAR(320)             NOT NULL,
    password_hash  VARCHAR(100)             NOT NULL,
    first_name     VARCHAR(100)             NOT NULL,
    last_name      VARCHAR(100)             NOT NULL,
    enabled        BOOLEAN                  NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMPTZ              NOT NULL,
    updated_at     TIMESTAMPTZ              NOT NULL,
    version        BIGINT                   NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE acos.user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user_id FOREIGN KEY (user_id)
        REFERENCES acos.users (id),
    CONSTRAINT fk_user_roles_role_id FOREIGN KEY (role_id)
        REFERENCES acos.roles (id)
);

CREATE INDEX ix_user_roles_role_id ON acos.user_roles (role_id);

COMMENT ON TABLE acos.users IS 'Platform user accounts';
COMMENT ON TABLE acos.roles IS 'Authorization roles';
COMMENT ON TABLE acos.user_roles IS 'User to role membership';

INSERT INTO acos.roles (id, name, created_at, updated_at, version)
VALUES
    (gen_random_uuid(), 'USER', NOW(), NOW(), 0),
    (gen_random_uuid(), 'ADMIN', NOW(), NOW(), 0);

CREATE TABLE users
(
    id            UUID         NOT NULL,
    email         VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE processing_log
(
    id          UUID        NOT NULL,
    user_id     UUID        NOT NULL,
    input_text  TEXT        NOT NULL,
    output_text TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_processing_log PRIMARY KEY (id),
    CONSTRAINT fk_processing_log_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_processing_log_user_id ON processing_log (user_id);
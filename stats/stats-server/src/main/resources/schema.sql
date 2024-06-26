CREATE TABLE IF NOT EXISTS hits
(
    id           BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    app          VARCHAR(255)                NOT NULL,
    uri          VARCHAR(512)                NOT NULL,
    ip           VARCHAR(15)                 NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
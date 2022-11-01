-- postgres async_task table create statement

CREATE TABLE IF NOT EXISTS async_task
(
    id serial NOT NULL,
    state integer NOT NULL,
    ts timestamp without time zone NOT NULL,
    ttl integer NOT NULL,
    data text,
    token character varying(36) NOT NULL,
    CONSTRAINT async_task_pkey PRIMARY KEY (id)
)
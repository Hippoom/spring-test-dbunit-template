create table t_event (
    id VARCHAR(32) not null,
    name VARCHAR(50) not null,
    status varchar(50) not null,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT uk_event_name UNIQUE KEY (name),
);


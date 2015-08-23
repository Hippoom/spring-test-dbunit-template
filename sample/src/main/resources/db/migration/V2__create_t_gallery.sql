CREATE TABLE t_gallery (
  id VARCHAR(32) NOT NULL,
  event_id VARCHAR(32) NOT NULL,
  CONSTRAINT pk_gallery PRIMARY KEY (id),
  CONSTRAINT uk_gallery_event UNIQUE KEY (event_id),
  CONSTRAINT fk_gallery_event FOREIGN KEY (event_id) REFERENCES t_event (id)
    ON DELETE RESTRICT
);


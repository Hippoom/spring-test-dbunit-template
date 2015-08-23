CREATE TABLE t_gallery_cover (
  gallery_id VARCHAR(32) NOT NULL,
  file_name VARCHAR(100) NOT NULL,
  CONSTRAINT pk_gallery_cover PRIMARY KEY (gallery_id, file_name),
  CONSTRAINT fk_gallery_cover_gallery FOREIGN KEY (gallery_id) REFERENCES t_gallery (id)
    ON DELETE RESTRICT
);


CREATE TABLE t_gallery_photo (
  id VARCHAR(32) NOT NULL,
  gallery_id VARCHAR(32) NOT NULL,
  file_name VARCHAR(100) NOT NULL,
  CONSTRAINT pk_gallery_photo PRIMARY KEY (id),
  CONSTRAINT uk_gallery_photo UNIQUE KEY (gallery_id, file_name),
  CONSTRAINT fk_gallery_photo_gallery FOREIGN KEY (gallery_id) REFERENCES t_gallery (id)
    ON DELETE RESTRICT
);


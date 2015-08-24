package com.github.hippoom.springtestdbunittemplate.sample;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "t_gallery_photo")
public class GalleryPhoto {
    @Id
    private String id;

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(
            name = "gallery_id")
    private Gallery gallery;

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }
}

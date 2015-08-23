package com.github.hippoom.springtestdbunittemplate.sample;

import javax.persistence.*;

@Entity
@Table(name = "gallery_photo")
public class GalleryPhoto {
    @Id
    private Long id;

    private String fileName;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "gallery_id")
    private Event gallery;
}

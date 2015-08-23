package com.github.hippoom.springtestdbunittemplate.sample;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_gallery")
public class Gallery {
    @Id
    private String id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private Event event;

    @AttributeOverrides({
            @AttributeOverride(name = "fileName", column = @Column(name = "file_name"))
    })
    @ElementCollection
    @CollectionTable(name = "t_gallery_cover")
    @OrderBy("fileName")
    private List<GalleryCover> covers = new ArrayList<>();

    /**
     * for frameworks only
     */
    public Gallery() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public List<GalleryCover> getCovers() {
        return covers;
    }
}

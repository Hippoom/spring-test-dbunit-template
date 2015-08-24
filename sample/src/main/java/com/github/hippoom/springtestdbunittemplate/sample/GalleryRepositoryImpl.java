package com.github.hippoom.springtestdbunittemplate.sample;

import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

@Component
public class GalleryRepositoryImpl implements GalleryQuery {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Gallery> byEventStatus(Event.Status status) {
        QGallery gallery = QGallery.gallery;
        QEvent event = QEvent.event;
        JPAQuery query = new JPAQuery(entityManager);

        query.from(gallery).join(gallery.event, event).where(event.status.eq(status.getCode()));

        query.setHint(LOAD.getKey(), entityManager.getEntityGraph("Gallery.event"));

        return query.list(gallery);
    }
}

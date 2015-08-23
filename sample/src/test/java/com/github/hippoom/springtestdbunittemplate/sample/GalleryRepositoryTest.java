package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.Test;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import java.util.List;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class GalleryRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private GalleryRepository subject;

    @Autowired
    private EventRepository eventRepository;

    @DatabaseSetup("classpath:gallery_save_before.xml")
    @ExpectedDatabase(
            value = "classpath:gallery_save_after.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenSave_() throws Exception {
        final Gallery toBeSaved = newTransactionTemplate().execute(new TransactionCallback<Gallery>() {
            @Override
            public Gallery doInTransaction(TransactionStatus status) {

                final Gallery prototype = subject.findOne("9");
                final Gallery gallery = cloneFrom("10", prototype);
                return gallery;
            }
        });
        subject.save(toBeSaved);
    }

    @DatabaseSetup("classpath:gallery_update_before.xml")
    @ExpectedDatabase(
            value = "classpath:gallery_update_after.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenUpdate_() throws Exception {

        newTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                final Gallery prototype = subject.findOne("11");
                final Gallery toBeUpdated = subject.findOne("12");

                cloneFrom(prototype, toBeUpdated);
            }
        });
    }

    @DatabaseSetup("classpath:gallery_find_by_event_status.xml")
    @Test
    public void whenFindByStatus_itShouldFetchEventToAvoidNPlusOneQuery() throws Exception {

        final List<Gallery> found = subject.byEventStatus(Event.Status.DONE);

        assertThat(found.size(), is(3));

    }

    private void cloneFrom(final Gallery prototype, final Gallery toBeUpdated) {
        getModelMapper().addMappings(new PropertyMap<Gallery, Gallery>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getEvent());
                map(prototype.getCovers(), destination.getCovers());
            }
        });
        getModelMapper().map(new Gallery(), toBeUpdated);
    }

    private Gallery cloneFrom(final String toBeSavedId, final Gallery prototype) {

        final Event event = eventRepository.findOne(toBeSavedId);

        final PropertyMap<Gallery, Gallery> propertyMap = new PropertyMap<Gallery, Gallery>() {
            @Override
            protected void configure() {
                map(toBeSavedId, destination.getId());
                skip(destination.getEvent());
            }
        };
        getModelMapper().addMappings(propertyMap);

        final Gallery g = getModelMapper().map(prototype, Gallery.class);
        g.setEvent(event);
        return g;
    }


}

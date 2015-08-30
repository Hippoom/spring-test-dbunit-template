package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.junit.Test;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class EventRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private EventRepository subject;

    @DatabaseSetup("before:classpath:event_save.xml")
    @ExpectedDatabase(
            value = "after:classpath:event_save.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenSave_() throws Exception {

        final Event prototype = subject.findOne("1");
        final Event toBeSaved = cloneFrom("2", "event_save_subject", prototype);

        subject.save(toBeSaved);
    }

    @DatabaseSetup("classpath:event_update_before.xml")
    @ExpectedDatabase(
            value = "classpath:event_update_after.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenUpdate_() throws Exception {

        newTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                final Event prototype = subject.findOne("3");
                final Event toBeUpdated = subject.findOne("4");

                cloneFrom(prototype, toBeUpdated);
            }
        });
    }

    @DatabaseSetup("classpath:event_find_by_status.xml")
    @Test
    public void whenFindByStatus_() throws Exception {
        QEvent event = QEvent.event;
        final Page<Event> found = subject.findAll(
                event.status.eq(Event.Status.ACTIVE.getCode()),
                new PageRequest(0, 2, new Sort(Sort.Direction.DESC, "name")));

        assertThat(found.getTotalElements(), is(3L));
        assertThat(found.getTotalPages(), is(2));
        assertThat(found.getContent().get(0).getId(), is("8"));//verify sorting
    }

    private void cloneFrom(Event prototype, Event toBeUpdated) {
        getModelMapper().addMappings(new PropertyMap<Event, Event>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getName());
            }
        });
        getModelMapper().map(prototype, toBeUpdated);
    }

    private Event cloneFrom(final String toBeSavedId, final String toBeSavedName, Event prototype) {
        getModelMapper().addMappings(new PropertyMap<Event, Event>() {
            @Override
            protected void configure() {
                map(toBeSavedId, destination.getId());
                map(toBeSavedName, destination.getName());
            }
        });

        return getModelMapper().map(prototype, Event.class);
    }


}

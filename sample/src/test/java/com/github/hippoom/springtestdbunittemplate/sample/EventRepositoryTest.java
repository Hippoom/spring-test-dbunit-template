package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PersistenceConfig.class,
        FlywayConfig.class,
        EventRepository.class
})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        FlywayTestExecutionListener.class
})
@FlywayTest(invokeCleanDB = false)
public class EventRepositoryTest {

    @Autowired
    private EventRepository subject;

    @Autowired
    private PlatformTransactionManager txManager;

    private ModelMapper modelMapper;

    @Before
    public void setUp() throws Exception {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE);

    }

    @DatabaseSetup("classpath:event_save_prototype.xml")
    @ExpectedDatabase(
            value = "classpath:event_save_expect.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenSave_() throws Exception {

        final Event prototype = subject.findOne("1");
        final Event toBeSaved = cloneFrom("2", "event_save_actual", prototype);

        subject.save(toBeSaved);
    }

    @DatabaseSetup("classpath:event_update_before.xml")
    @ExpectedDatabase(
            value = "classpath:event_update_after.xml",
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenUpdate_() throws Exception {

        new TransactionTemplate(txManager).execute(new TransactionCallbackWithoutResult() {
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
        modelMapper.addMappings(new PropertyMap<Event, Event>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getName());
            }
        });
        modelMapper.map(prototype, toBeUpdated);
    }

    private Event cloneFrom(final String toBeSavedId, final String toBeSavedName, Event prototype) {
        modelMapper.addMappings(new PropertyMap<Event, Event>() {
            @Override
            protected void configure() {
                map(toBeSavedId, destination.getId());
                map(toBeSavedName, destination.getName());
            }
        });

        return modelMapper.map(prototype, Event.class);
    }


}

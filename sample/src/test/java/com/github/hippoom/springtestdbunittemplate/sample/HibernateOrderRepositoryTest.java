package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.hippoom.springtestdbunittemplate.dataset.CreateTemplateDataSet;
import com.github.hippoom.springtestdbunittemplate.dataset.CreateTemplateModifier;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.dataset.DataSetModifier;
import org.dbunit.dataset.IDataSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static com.github.springtestdbunit.assertion.DatabaseAssertionMode.NON_STRICT_UNORDERED;
import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, HibernateOrderRepository.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class HibernateOrderRepositoryTest {

    @Autowired
    private HibernateOrderRepository subject;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @DatabaseSetup("classpath:order_save_template.xml")
    @ExpectedDatabase(value = "classpath:order_save_template.xml", assertionMode = NON_STRICT_UNORDERED, modifiers = {OrderCreateTemplateModifier.class})
    @Test
    public void should_saves_order() throws Exception {
        final String trackingIdOfPrototype = "1";
        final String trackingIdOfToBeSaved = "2";

        final Order toBeSaved = new TransactionTemplate(transactionManager)
                .execute(new TransactionCallback<Order>() {
                    @Override
                    public Order doInTransaction(TransactionStatus status) {
                        final Optional<Order> orderMaybe = subject.findByTrackingId(trackingIdOfPrototype);
                        return HibernateOrderRepositoryTest.this.clone(orderMaybe.get(), trackingIdOfToBeSaved);
                    }
                });

        subject.store(toBeSaved);
    }

    private class OrderCreateTemplateModifier extends CreateTemplateModifier {

        public OrderCreateTemplateModifier() {
            super("tracking_id", "2");
        }
    }

    private Order clone(Order prototype, final String newTrackingId) {
        ModelMapper mapper = new ModelMapper();

        PropertyMap<Order, Order> orderMap = new PropertyMap<Order, Order>() {
            protected void configure() {
                map(newTrackingId, destination.getTrackingId());
            }
        };
        mapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        mapper.addMappings(orderMap);

        return mapper.map(prototype, Order.class);
    }
}

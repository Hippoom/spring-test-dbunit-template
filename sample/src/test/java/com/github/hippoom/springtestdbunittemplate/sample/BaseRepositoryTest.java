package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PersistenceConfig.class,
        FlywayConfig.class,
        GalleryRepositoryImpl.class
})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class,
        FlywayTestExecutionListener.class
})
@FlywayTest(invokeCleanDB = false)
@DatabaseSetup(value = "classpath:all.xml", type = DatabaseOperation.DELETE_ALL)
public abstract class BaseRepositoryTest {
    protected ModelMapper modelMapper;
    @Autowired
    protected PlatformTransactionManager txManager;

    @Before
    public void setUp() throws Exception {
        modelMapper = new ModelMapper();
        getModelMapper().getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(PRIVATE);

    }

    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    protected TransactionTemplate newTransactionTemplate() {
        return new TransactionTemplate(txManager);
    }
}

# spring-test-dbunit-template [![Build Status](https://travis-ci.org/Hippoom/spring-test-dbunit-template.svg?branch=master)](https://travis-ci.org/Hippoom/spring-test-dbunit-template)

An extension to simplify CRUD test with [spring-test-dbunit](http://springtestdbunit.github.io/spring-test-dbunit)

## Latest Release
* __0.0.1__

You can download the binary at maven central repository:


* Gradle

```` json
    testCompile 'com.github.hippoom:spring-test-dbunit-template:0.0.1'
````

* Maven

```` xml
    <dependency>
    	<groupId>com.github.hippoom</groupId>
    	<artifactId>spring-test-dbunit-template</artifactId>
    	<version>0.0.1</version>
    </dependency>
````

## Why

Test against persistence components(daos / repositories) involves external stateful services,
which makes fixture cleanup and setup, test data designing much difficult than unit tests.

[spring-test-dbunit](http://springtestdbunit.github.io/spring-test-dbunit) does help a lot in this area,
but there is still lots of boilerplate code.

What I want is to minimize the effort for developing persistent tests.


## Quick Start

Below is an example of an create test using [spring-test-dbunit](http://springtestdbunit.github.io/spring-test-dbunit).
The fixture setup and result assertion are neat and declarative.
The test case
* load an prototype (setup by @DatabaseSetup)
* create a new order (you can easily achieve this by a mapper library, such as [model mapper](http://modelmapper.org/))
* store the created order
* validate the result (by @ExpectedDatabase)

````java
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

    @DatabaseSetup("classpath:order_save_fixture.xml")
    @ExpectedDatabase(value = "classpath:order_save_expected.xml", assertionMode = NON_STRICT_UNORDERED)
    @Test
    public void should_saves_order() throws Exception {
        final String trackingIdOfPrototype = "1";
        final String trackingIdOfToBeSaved = "2";

        final Order toBeSaved = new TransactionTemplate(transactionManager)
                .execute(status -> {
                        final Order prototype = subject.findByTrackingId(trackingIdOfPrototype);
                        return HibernateOrderRepositoryTest.this.clone(prototype, trackingIdOfToBeSaved);
                });

        subject.store(toBeSaved);
    }
````

But still, there is duplicate in the data files:

order_save_fixture.xml
````xml

<dataset>
    <t_order tracking_id="1"
             status="WAIT_PAYMENT"/>
    <t_order_item tracking_id="1"
                  name="item1"
                  quantity="1"/>
    <t_order_item tracking_id="1"
                  name="item2"
                  quantity="2"/>
</dataset>

````

order_save_expected.xml
````xml

<dataset>
    <t_order tracking_id="1"
             status="WAIT_PAYMENT"/>
    <t_order_item tracking_id="1"
                  name="item1"
                  quantity="1"/>
    <t_order_item tracking_id="1"
                  name="item2"
                  quantity="2"/>

    <t_order tracking_id="2"
             status="WAIT_PAYMENT"/>
    <t_order_item tracking_id="2"
                  name="item1"
                  quantity="1"/>
    <t_order_item tracking_id="2"
                  name="item2"
                  quantity="2"/>
</dataset>

````

With spring-test-dbunit-template, you will not have to copy data from fixture file to expect file,
you don't even need to create the expect file.
By extending CreateTemplateModifier, the library will prepare the expect dataset for you.

````java

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

    @DatabaseSetup("classpath:order_save_fixture.xml")
    @ExpectedDatabase(value = "classpath:order_save_fixture.xml",
                      assertionMode = NON_STRICT_UNORDERED,
                      modifiers = {OrderCreateTemplateModifier.class})
    @Test
    public void should_saves_order() throws Exception {
        final String trackingIdOfPrototype = "1";
        final String trackingIdOfToBeSaved = "2";

        final Order toBeSaved = new TransactionTemplate(transactionManager)
                .execute(status -> {
                        final Order prototype = subject.findByTrackingId(trackingIdOfPrototype);
                        return HibernateOrderRepositoryTest.this.clone(prototype, trackingIdOfToBeSaved);
                });

        subject.store(toBeSaved);
    }

    private class OrderCreateTemplateModifier extends CreateTemplateModifier {

        public OrderCreateTemplateModifier() {
            super("tracking_id", "2");
        }
    }
    
````



## Contributing
Any suggestion and pull request is welcome.

## License

Licensed under MIT License (the "License"); You may obtain a copy of the License in the LICENSE file, or at [here](LICENSE).
# spring-test-dbunit-template [![Build Status](https://travis-ci.org/Hippoom/spring-test-dbunit-template.svg?branch=master)](https://travis-ci.org/Hippoom/spring-test-dbunit-template)

An extension to simplify CRUD test with [spring-test-dbunit](http://springtestdbunit.github.io/spring-test-dbunit)

## Latest Release
* __0.1.1__

You can download the binary at maven central repository:


* Gradle

```` json
    testCompile 'com.github.hippoom:spring-test-dbunit-template:0.1.1'
````

* Maven

```` xml
    <dependency>
    	<groupId>com.github.hippoom</groupId>
    	<artifactId>spring-test-dbunit-template</artifactId>
    	<version>0.1.1</version>
    </dependency>
````

## Why

Test against persistence components(daos / repositories) involves external stateful services,
which makes fixture cleanup and setup, test data designing much difficult than unit tests.

[spring-test-dbunit](http://springtestdbunit.github.io/spring-test-dbunit) does help a lot in this domain,
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
@ContextConfiguration(classes = {PersistenceConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public class EventRepositoryTest {

    @Autowired
    private EventRepository subject;

    @DatabaseSetup("classpath:event_save_before.xml") 
    @ExpectedDatabase(
            value = "classpath:event_save_after.xml", 
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenSave_() throws Exception {

        final Event prototype = subject.findOne("1");
        final Event toBeSaved = cloneFrom("2", "event_save_subject", prototype);

        subject.save(toBeSaved);
    }
    
    private Event cloneFrom(final String toBeSavedId, final String toBeSavedName, Event prototype) {
        //...
    }
}
````

But still, there is duplicate in the data files:

event_save_before.xml
````xml

<dataset>
    <t_event id="1" name="event_save_prototype" status="A"/>
</dataset>

````

event_save_after.xml
````xml

<dataset>
    <t_event id="1" name="event_save_prototype" status="A"/>
    <t_event id="2" name="event_save_actual" status="A"/>
</dataset>

````

With spring-test-dbunit-template, you will not have to copy data from fixture file to expect file,
you don't even need to create the expect file.
By using GivenWhenThenFlatXmlDataSetLoader, the library will prepare both dataset for you.

````java

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DbUnitConfiguration(dataSetLoader = GivenWhenThenFlatXmlDataSetLoader.class) //using this DataSetLoader
public class EventRepositoryTest {

    @Autowired
    private EventRepository subject;

    @DatabaseSetup("given:classpath:event_save.xml") //decorate xml file with "given:" prefix
    @ExpectedDatabase(
            value = "then:classpath:event_save.xml", //decorate xml file with "then:" prefix
            assertionMode = NON_STRICT_UNORDERED
    )
    @Test
    public void whenSave_() throws Exception {

        final Event prototype = subject.findOne("1");
        final Event toBeSaved = cloneFrom("2", "event_save_subject", prototype);

        subject.save(toBeSaved);
    }
    
    private Event cloneFrom(final String toBeSavedId, final String toBeSavedName, Event prototype) {
        //...
    }
}
    
````

This is what event_save.xml looks like:

````xml

<dataset>
    <given>
        <t_event id="1" name="event_save_prototype" status="A"/>
    </given>

    <then>
        <added>
            <t_event id="2" name="event_save_subject" status="A"/>
        </added>
    </then>
</dataset>

````

GivenWhenThenFlatXmlDataSetLoader will generate an xml named "event_save_given.xml" using elements from <given/> tag
which will be used to setup the database.  
It will also generate an xml named "event_update_then.xml" using elements from <then/> tag 
which will be used as expected result.
 
### added
 
Elements from <added/> tag: after the test is executed, these table rows should be added to the database. 

### deleted

Elements from <deleted/> tag: after the test is executed, these table rows should be removed to the database.  
You don't have to provide all the fields of the table row, usually the primary key column is enough.
 
e.g.

````xml

<dataset>
    <given>
        <t_event id="11" name="gallery_update_prototype" status="D"/>
        <t_event id="12" name="gallery_update_actual" status="A"/>
    </given>
    <then>
        <deleted>
            <t_event id="12" />
        </deleted>
    </then>
</dataset>

````

### modified

Elements from <modified/> tag: after the test is executed, these table rows should be modified to the database.

With current FlatXmlDataSet, GivenWhenThenFlatXmlDataSetLoader cannot get table meta data.
Therefore, you have to provide the primary key column(s) to help GivenWhenThenFlatXmlDataSetLoader find the table rows.
Again, you may provide columns need to be modified only.
  
e.g.

````xml

<dataset>
    <given>
        <t_event id="11" name="gallery_update_prototype" status="D"/>
        <t_event id="12" name="gallery_update_actual" status="A"/>
    </given>
    <then>
        <modified pk="id"> <!--use ',' as delimiter with multiple columns-->
            <t_event id="12" status="D"/>
        </deleted>
    </then>
</dataset>

````

## sample

You may find samples [here](sample)

## Contributing
Any suggestion and pull request is welcome.

## License

Licensed under MIT License (the "License"); You may obtain a copy of the License in the LICENSE file, or at [here](LICENSE).
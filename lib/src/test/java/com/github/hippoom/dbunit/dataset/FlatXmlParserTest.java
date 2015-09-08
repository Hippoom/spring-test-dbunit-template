package com.github.hippoom.dbunit.dataset;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FlatXmlParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private FlatXmlParser subject = new FlatXmlParser();

    @Test
    public void it_should_find_the_only_before_tag() throws DocumentException {
        String path = "src/test/data/sample.xml";
        File inputXml = new File(path);
        SAXReader saxReader = new SAXReader();


        Document document = saxReader.read(inputXml);
        String xpath = "/dataset/before";
        List list = document.selectNodes(xpath);

        assertThat(list.size(), is(1));

        Element o = (Element) list.get(0);
        List elements = o.elements();

        assertThat(elements.size(), is(1));


        Document newDoc = DocumentHelper.createDocument();
        Element dataset = newDoc.addElement("dataset");
        dataset.add(o.detach());
    }

    @Test
    public void it_should_throw_given_too_many_before_tag() throws DocumentException {

        thrown.expectMessage("Too many <before/> found, expect 1 but got 2.");

        String path = "src/test/data/too_many_before.xml";
        File inputXml = new File(path);
        SAXReader saxReader = new SAXReader();


        Document document = saxReader.read(inputXml);
        String xpath = "/dataset/before";
        List list = document.selectNodes(xpath);

        if (list.size() != 1) {
            throw new RuntimeException(format("Too many <before/> found, expect 1 but got %d.", list.size()));
        }

    }
}

package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class DiffDataSet extends FlatXmlDataSetLoader {
    protected IDataSet createDataSet(Resource resource) throws Exception {

        final String description = resource.getDescription();

        if (description.startsWith("before")) {

            DOMParser parser = new DOMParser();
            Document document = parser.parse(resource.getURI().getPath());
            //get root element
            Element rootElement = document.getDocumentElement();

            final NodeList before = rootElement.getElementsByTagName("before");


            NodeList tables = before.item(0).getChildNodes();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element dataset = doc.createElement("dataset");
            doc.appendChild(dataset);

            for (int i = 0; i < tables.getLength(); i++) {
                Node node = tables.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Node n = doc.importNode(node, true);
                    dataset.appendChild(n);
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(resource.getURI().getPath() + "1");

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            return super.createDataSet(new FileSystemResource(resource.getURI().getPath() + "1"));
        } else if (description.startsWith("after")) {

            DOMParser parser = new DOMParser();
            Document document = parser.parse(resource.getURI().getPath());
            //get root element
            Element rootElement = document.getDocumentElement();

            final NodeList before = rootElement.getElementsByTagName("before");

            final Node after = rootElement.getElementsByTagName("after").item(0);

            final NodeList added = ((Element) after).getElementsByTagName("added").item(0).getChildNodes();






            NodeList tables = before.item(0).getChildNodes();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element dataset = doc.createElement("dataset");
            doc.appendChild(dataset);

            for (int i = 0; i < tables.getLength(); i++) {
                Node node = tables.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Node n = doc.importNode(node, true);
                    dataset.appendChild(n);
                }
            }

            for (int i = 0; i < added.getLength(); i++) {
                Node node = added.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Node n = doc.importNode(node, true);
                    dataset.appendChild(n);
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(resource.getURI().getPath() + "2");

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            return super.createDataSet(new FileSystemResource(resource.getURI().getPath() + "2"));
        } else {
            return super.createDataSet(resource);
        }

    }

    /**
     * Gets the {@link ResourceLoader} that will be used to load the dataset {@link Resource}s.
     *
     * @param testClass The class under test
     * @return a resource loader
     */
    protected ResourceLoader getResourceLoader(Class<?> testClass) {
        return new BeforeResourceLoader(testClass);
    }
}

class DOMParser {

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

    //Load and parse XML file into DOM
    public Document parse(String filePath) throws ParserConfigurationException, IOException, SAXException {
        Document document = null;

        //DOM parser instance
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        //parse an XML file into a DOM tree
        document = builder.parse(new File(filePath));

        return document;
    }
}


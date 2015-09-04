package com.github.hippoom.springtestdbunittemplate.sample;

import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.*;
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
            final String path = getFilePathFor(resource, "_before");
            StreamResult result = new StreamResult(path);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            return super.createDataSet(new FileSystemResource(path));
        } else if (description.startsWith("after")) {

            DOMParser parser = new DOMParser();
            Document document = parser.parse(resource.getURI().getPath());
            //get root element
            Element rootElement = document.getDocumentElement();

            final NodeList before = rootElement.getElementsByTagName("before");

            final Node after = rootElement.getElementsByTagName("after").item(0);


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

            NodeList added1 = ((Element) after).getElementsByTagName("added");
            for (int a = 0; a < added1.getLength(); a++) {
                final NodeList added = added1.item(a).getChildNodes();

                for (int i = 0; i < added.getLength(); i++) {
                    Node node = added.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final Node n = doc.importNode(node, true);
                        dataset.appendChild(n);
                    }
                }
            }

            NodeList deleted1 = ((Element) after).getElementsByTagName("deleted");
            for (int a = 0; a < deleted1.getLength(); a++) {

                final NodeList deleted = deleted1.item(a).getChildNodes();
                for (int i = 0; i < deleted.getLength(); i++) {
                    Node node = deleted.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final String tableName = node.getNodeName();

                        final NodeList tableRows = dataset.getElementsByTagName(tableName);

                        for (int j = 0; j < tableRows.getLength(); j++) {
                            final Node row = tableRows.item(j);
                            boolean match = true;

                            if (row.getNodeType() == Node.ELEMENT_NODE) {
                                final NamedNodeMap attributes = row.getAttributes();

                                final NamedNodeMap attributes1 = node.getAttributes();

                                for (int k = 0; k < attributes1.getLength(); k++) {

                                    final Node attr1 = attributes1.item(k);
                                    if (!attr1.getNodeValue().equals(attributes.getNamedItem(attr1.getNodeName()).getNodeValue())) {
                                        match = false;
                                    }
                                }
                                if (match == true) {
                                    dataset.removeChild(row);
                                }
                            }
                        }

                    }
                }
            }

            NodeList modified = ((Element) after).getElementsByTagName("modified");
            for (int a = 0; a < modified.getLength(); a++) {

                NamedNodeMap attrs = modified.item(a).getAttributes();
                Node pk = attrs.getNamedItem("pk");

                String[] pkColumns = pk.getNodeValue().split(",");

                final NodeList deleted = modified.item(a).getChildNodes();
                for (int i = 0; i < deleted.getLength(); i++) {
                    Node node = deleted.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final String tableName = node.getNodeName();

                        final NodeList tableRows = dataset.getElementsByTagName(tableName);

                        for (int j = 0; j < tableRows.getLength(); j++) {
                            final Node row = tableRows.item(j);
                            boolean match = true;

                            if (row.getNodeType() == Node.ELEMENT_NODE) {
                                final NamedNodeMap attributes = row.getAttributes();
                                final NamedNodeMap attributes1 = node.getAttributes();


                                for (String pkColumn : pkColumns) {

                                    final Node attr1 = attributes1.getNamedItem(pkColumn);
                                    if (!attr1.getNodeValue().equals(attributes.getNamedItem(attr1.getNodeName()).getNodeValue())) {
                                        match = false;
                                    }
                                }
                                if (match == true) {
                                    for (int k = 0; k < attributes1.getLength(); k++) {

                                        final Node attr1 = attributes1.item(k);
                                        attributes.getNamedItem(attr1.getNodeName()).setNodeValue(attr1.getNodeValue());
                                    }
                                }
                            }
                        }

                    }
                }
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            final String path = getFilePathFor(resource, "_after");
            StreamResult result = new StreamResult(path);

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            return super.createDataSet(new FileSystemResource(path));
        } else

        {
            return super.createDataSet(resource);
        }

    }

    private String getFilePathFor(Resource resource, String suffix) throws IOException {

        final String dir = resource.getFile().getParent();
        final String filename = resource.getFilename();
        final int extensionIndexMaybe = filename.lastIndexOf(".");
        if (extensionIndexMaybe != -1) {
            return dir + "/" + filename.substring(0, extensionIndexMaybe) + suffix + filename.substring(extensionIndexMaybe);
        } else {
            return dir + "/" + filename + suffix;
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


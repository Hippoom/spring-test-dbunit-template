package com.github.hippoom.dbunit.dataset;

import com.github.hippoom.dbunit.spring.BeforeResourceLoader;
import com.github.springtestdbunit.dataset.FlatXmlDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

public class DiffDataSet extends FlatXmlDataSetLoader {
    protected IDataSet createDataSet(Resource resource) throws Exception {

        final String description = resource.getDescription();
        final org.dom4j.Document document = loadDocument(resource);

        if (description.startsWith("before")) {


            org.dom4j.Element before = getBeforeElement(document);

            org.dom4j.Document beforeXml = anEmptyDataSetDocument();

            org.dom4j.Element dataset = beforeXml.getRootElement();

            addTableRowsInBeforeTagToDataSet(dataset, before);

            final String path = getFilePathFor(resource, "_before");
            writeXml(path, beforeXml);
            return super.createDataSet(new FileSystemResource(path));
        } else if (description.startsWith("after")) {

            org.dom4j.Element before = getBeforeElement(document);

            org.dom4j.Document afterXml = anEmptyDataSetDocument();

            org.dom4j.Element dataset = afterXml.getRootElement();

            addTableRowsInBeforeTagToDataSet(dataset, before);

            addTableRowsInAddedToDataSet(dataset, document);

            removeTableRowsInDeletedTagFromDataSet(dataset, document);

            modifyTablesRowsAccordingToModifiedTagToDataSet(dataset, document);

            final String path = getFilePathFor(resource, "_after");
            writeXml(path, afterXml);
            return super.createDataSet(new FileSystemResource(path));
        } else

        {
            return super.createDataSet(resource);
        }

    }

    private void modifyTablesRowsAccordingToModifiedTagToDataSet(Element dataset, org.dom4j.Document document) {
        List modifiedMaybe = document.selectNodes("/dataset/after/modified");

        for (Object o : modifiedMaybe) {
            Element modified = (Element) o;

            String[] pkColumns = modified.attributeValue("pk").split(",");

            List tobeModifiedTableRows = modified.elements();
            for (Object ob : tobeModifiedTableRows) {
                Element tableRow = (Element) ob;

                String tableName = tableRow.getName();
                List columns = tableRow.attributes();

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < pkColumns.length; i++) {
                    String column = pkColumns[i].trim();

                    builder.append(format("@%s='%s'", column, tableRow.attributeValue(column)));
                    if (i != pkColumns.length - 1) {
                        builder.append(" and ");
                    }
                }

                String xpath = format("/dataset/%s[%s]", tableName, builder.toString());
                List toBeModifiedNodes = dataset.selectNodes(xpath);
                for (Object m : toBeModifiedNodes) {
                    Element modifiedTableRow = (Element) m;
                    for (Object c : columns) {
                        Attribute column = (Attribute) c;

                        modifiedTableRow.addAttribute(column.getName(), column.getValue());
                    }
                }
            }
        }
    }

    private void removeTableRowsInDeletedTagFromDataSet(Element dataset, org.dom4j.Document document) {
        List deletedMaybe = document.selectNodes("/dataset/after/deleted");

        for (Object o : deletedMaybe) {
            Element deleted = (Element) o;
            List toBeDeletedTableRows = deleted.elements();
            for (Object ob : toBeDeletedTableRows) {
                Element tableRow = (Element) ob;

                String tableName = tableRow.getName();
                List columns = tableRow.attributes();

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < columns.size(); i++) {
                    Attribute attr = (Attribute) columns.get(i);

                    builder.append(format("@%s='%s'", attr.getName(), attr.getValue()));
                    if (i != columns.size() - 1) {
                        builder.append(" and ");
                    }
                }

                String xpath = format("/dataset/%s[%s]", tableName, builder.toString());
                List toBeDeletedNodes = dataset.selectNodes(xpath);
                for (Object d : toBeDeletedNodes) {
                    dataset.remove((Element) d);
                }
            }
        }
    }

    private void addTableRowsInAddedToDataSet(org.dom4j.Element dataset, org.dom4j.Document document) {
        List addedMaybe = document.selectNodes("/dataset/after/added");
        for (Object a : addedMaybe) {
            org.dom4j.Element added = (Element) a;
            for (Object t : added.elements()) {
                org.dom4j.Element tableRow = (Element) t;
                dataset.add(tableRow.detach());
            }
        }
    }

    private void addTableRowsInBeforeTagToDataSet(org.dom4j.Element dataset, org.dom4j.Element before) {
        List tableRows = before.elements();


        for (Object tableRow : tableRows) {
            dataset.add(((org.dom4j.Element) tableRow).detach());
        }
    }

    private org.dom4j.Element getBeforeElement(org.dom4j.Document document) {
        List beforeNodes = document.selectNodes("/dataset/before");

        return (org.dom4j.Element) beforeNodes.get(0);
    }

    private org.dom4j.Document loadDocument(Resource resource) throws IOException, DocumentException {
        File inputXml = new File(resource.getURI().getPath());
        SAXReader saxReader = new SAXReader();
        return saxReader.read(inputXml);
    }

    private org.dom4j.Document anEmptyDataSetDocument() {
        org.dom4j.Document newDoc = DocumentHelper.createDocument();
        newDoc.setRootElement(new DefaultElement("dataset"));
        return newDoc;
    }

    private void writeXml(String path, org.dom4j.Document newDoc) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(
                new FileWriter(path), format
        );
        writer.write(newDoc);
        writer.close();
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
package com.github.hippoom.dbunit.dataset;

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
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;

public class DiffDataSet extends FlatXmlDataSetLoader {

    private static final String GIVEN = "given";
    private static final String THEN = "then";
    private static final String UNDER_SCORE = "_";

    protected IDataSet createDataSet(Resource resource) throws Exception {

        final String description = resource.getDescription();
        final org.dom4j.Document document = loadDocument(resource);

        if (GIVEN.equals(description)) {


            org.dom4j.Element before = getBeforeElement(document);

            org.dom4j.Document beforeXml = anEmptyDataSetDocument();

            org.dom4j.Element dataset = beforeXml.getRootElement();

            addTableRowsInBeforeTagToDataSet(dataset, before);

            final String path = getFilePathFor(resource, suffixWith(GIVEN));
            writeXml(path, beforeXml);
            return super.createDataSet(new FileSystemResource(path));
        } else if (THEN.equals(description)) {

            org.dom4j.Element before = getBeforeElement(document);

            org.dom4j.Document afterXml = anEmptyDataSetDocument();

            org.dom4j.Element dataset = afterXml.getRootElement();

            addTableRowsInBeforeTagToDataSet(dataset, before);

            addTableRowsInAddedToDataSet(dataset, document);

            removeTableRowsInDeletedTagFromDataSet(dataset, document);

            modifyTablesRowsAccordingToModifiedTagToDataSet(dataset, document);

            final String path = getFilePathFor(resource, suffixWith(THEN));
            writeXml(path, afterXml);
            return super.createDataSet(new FileSystemResource(path));
        } else

        {
            return super.createDataSet(resource);
        }

    }

    private String suffixWith(String text) {
        return UNDER_SCORE + text;
    }

    private void modifyTablesRowsAccordingToModifiedTagToDataSet(Element dataset, org.dom4j.Document document) {
        List modifiedMaybe = document.selectNodes("/dataset/" + THEN + "/modified");

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
        List deletedMaybe = document.selectNodes("/dataset/" + THEN + "/deleted");

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
        List addedMaybe = document.selectNodes("/dataset/" + THEN + "/added");
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
        List beforeNodes = document.selectNodes("/dataset/" + GIVEN);

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
     * Gets the {@link org.springframework.core.io.ResourceLoader} that will be used to load the dataset {@link Resource}s.
     *
     * @param testClass The class under test
     * @return a resource loader
     */
    protected org.springframework.core.io.ResourceLoader getResourceLoader(Class<?> testClass) {
        return new ResourceLoader(testClass);
    }

    /**
     * With the built-in {@link Resource}, it is not possible to differ
     * <p/>
     * the incoming resource is used for setup or verification.
     * <p/>
     * With this customized {@link org.springframework.core.io.ResourceLoader}, one can inject some meta data to the resource
     * <p/>
     * and get it by using {@link Resource#getDescription()}
     */
    public static class ResourceLoader extends ClassRelativeResourceLoader {


        private static final String COLON = ":";
        private static final String SETUP_META = GIVEN + COLON;
        private static final String VERIFICATION_META = THEN + COLON;

        public ResourceLoader(Class<?> clazz) {
            super(clazz);
        }

        @Override
        public Resource getResource(String location) {
            if (location.startsWith(SETUP_META)) {
                return new ResourceWrapper(super.getResource(location.substring((SETUP_META.length()))), GIVEN);
            } else if (location.startsWith(VERIFICATION_META)) {
                return new ResourceWrapper(super.getResource(location.substring((VERIFICATION_META.length()))), THEN);
            } else {
                return super.getResource(location);
            }
        }

    }

    public static class ResourceWrapper implements Resource {
        private String description;
        private Resource target;

        public ResourceWrapper(Resource target, String description) {
            this.description = description;
            this.target = target;
        }

        @Override
        public boolean exists() {
            return target.exists();
        }

        @Override
        public boolean isReadable() {
            return target.isReadable();
        }

        @Override
        public boolean isOpen() {
            return target.isOpen();
        }

        @Override
        public URL getURL() throws IOException {
            return target.getURL();
        }

        @Override
        public URI getURI() throws IOException {
            return target.getURI();
        }

        @Override
        public File getFile() throws IOException {
            return target.getFile();
        }

        @Override
        public long contentLength() throws IOException {
            return target.contentLength();
        }

        @Override
        public long lastModified() throws IOException {
            return target.lastModified();
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            return target.createRelative(relativePath);
        }

        @Override
        public String getFilename() {
            return target.getFilename();
        }

        @Override
        public String getDescription() {
            return description;
        }


        @Override
        public InputStream getInputStream() throws IOException {
            return target.getInputStream();
        }
    }
}
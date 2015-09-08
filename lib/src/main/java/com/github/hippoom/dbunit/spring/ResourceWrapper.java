package com.github.hippoom.dbunit.spring;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class ResourceWrapper implements Resource {
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

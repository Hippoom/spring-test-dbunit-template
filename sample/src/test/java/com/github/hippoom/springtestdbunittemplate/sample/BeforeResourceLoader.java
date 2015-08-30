package com.github.hippoom.springtestdbunittemplate.sample;

import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;

public class BeforeResourceLoader extends ClassRelativeResourceLoader {


    /**
     * Create a new ClassRelativeResourceLoader for the given class.
     *
     * @param clazz the class to load resources through
     */
    public BeforeResourceLoader(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public Resource getResource(String location) {
        final String before = "before:";
        final String after = "after:";
        if (location.startsWith(before)) {
            return new ResourceWrapper(super.getResource(location.substring((before.length()))), "before");
        } else if (location.startsWith(after)) {
            return new ResourceWrapper(super.getResource(location.substring((after.length()))), "after");
        } else {
            return super.getResource(location);
        }
    }

}

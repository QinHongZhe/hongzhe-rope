package com.starblues.rope.common.databases.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ChainingClassLoader extends ClassLoader{

    public static final Logger LOG = LoggerFactory.getLogger(ChainingClassLoader.class);

    private final List<ClassLoader> classLoaders = new CopyOnWriteArrayList<>();

    public ChainingClassLoader(ClassLoader parent) {
        classLoaders.add(parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> cls = null;
        for (ClassLoader classLoader : classLoaders) {
            try {
                cls = classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                LOG.trace("Class " + name + " not found", e);
            }

            if (cls != null) {
                break;
            }
        }

        if (cls == null) {
            throw new ClassNotFoundException("Class " + name + " not found.");
        }

        return cls;
    }

    @Override
    public URL getResource(String name) {
        URL url = null;
        for (ClassLoader classLoader : classLoaders) {
            url = classLoader.getResource(name);

            if (url != null) {
                break;
            }
        }

        if (url == null && LOG.isTraceEnabled()) {
            LOG.trace("Resource " + name + " not found.");
        }

        return url;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        final List<URL> urls = new ArrayList<>();
        for (ClassLoader classLoader : classLoaders) {
            final Enumeration<URL> resources = classLoader.getResources(name);

            if (resources.hasMoreElements()) {
                urls.addAll(Collections.list(resources));
            }
        }

        if (urls.isEmpty() && LOG.isTraceEnabled()) {
            LOG.trace("Resource " + name + " not found.");
        }

        return Collections.enumeration(urls);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream stream = null;
        for (ClassLoader classLoader : classLoaders) {
            stream = classLoader.getResourceAsStream(name);

            if (stream != null) {
                break;
            }
        }

        if (stream == null && LOG.isTraceEnabled()) {
            LOG.trace("Resource " + name + " not found.");
        }

        return stream;
    }

    public List<ClassLoader> getClassLoaders() {
        return Collections.unmodifiableList(classLoaders);
    }

    public boolean addClassLoader(ClassLoader classLoader) {
        return classLoaders.add(classLoader);
    }

}

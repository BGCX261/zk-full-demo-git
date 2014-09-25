package org.hxzon.util.classloader;

import java.net.URL;
import java.net.URLClassLoader;

public class ReloadableClassLoader extends URLClassLoader {

//    private Set<Class<?>> reloadableClasses = new HashSet<Class<?>>();
//
//    public void addClass(Class<?> clazz) {
//        reloadableClasses.add(clazz);
//    }

    public ReloadableClassLoader() {
        super(new URL[0], chooseParent());
        addURL(this.getResource("."));
    }

    public <T> T createObj(Class<T> clazz) {
        try {
//            if (!reloadableClasses.contains(clazz)) {
//                return (T) super.loadClass(clazz.getName());
//            }
            return clazz.newInstance();
            //return (T) load(clazz.getName()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public Class<?> load(String name) throws ClassNotFoundException {
//        return load(name, false);
//    }

//    public Class<?> load(String name, boolean resolve) throws ClassNotFoundException {
//        if (null != super.findLoadedClass(name)) {
//            return reload(name, resolve);
//        }
//        Class<?> clazz = super.findClass(name);
//
//        if (resolve) {
//            super.resolveClass(clazz);
//        }
//        return clazz;
//    }

//    public Class<?> reload(String name, boolean resolve) throws ClassNotFoundException {
//        ReloadableClassLoader cl = new ReloadableClassLoader();
//        try {
//            return cl.load(name, resolve);
//        } finally {
//            IOUtils.closeQuietly(cl);
//        }
//    }

    //
    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url == null) {
            url = super.getResource(name);
        }
        return url;
    }

    private static ClassLoader chooseParent() {
        ClassLoader cl = ReloadableClassLoader.class.getClassLoader();
        if (cl != null) {
            return cl;
        }
        return ClassLoader.getSystemClassLoader();
    }
}

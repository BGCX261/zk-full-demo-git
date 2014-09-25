package org.hxzon.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hxzon.util.classloader.ReloadableClassLoader;

public class Binder {
    private Map<Class<?>, Object> implMap = new ConcurrentHashMap<Class<?>, Object>();
    private ReloadableClassLoader classLoader = new ReloadableClassLoader();

    public <S, T extends S> S bindReloadable(Class<S> clazz, Class<T> implClazz) {
        return bind(clazz, classLoader.createObj(implClazz));
    }

    public <T> T bindReloadable(Class<T> implClazz) {
        return bindReloadable(implClazz, implClazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T bind(Class<T> clazz, Object instance) {
        implMap.put(clazz, instance);
        return (T) instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T bind(T instance) {
        return (T) bind(instance.getClass(), instance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) implMap.get(clazz);
    }
}

package org.hxzon.admcmd;

import java.util.HashMap;
import java.util.Map;

import org.hxzon.util.Binder;
import org.hxzon.util.db.Db;
import org.springframework.context.ApplicationContext;

//
public class Gs {

    private static Binder binder = new Binder();
    private static Map<String, Db> dbs = new HashMap<String, Db>();

    public static <S, T extends S> S bindReloadable(Class<S> clazz, Class<T> implClazz) {
        return binder.bindReloadable(clazz, implClazz);
    }

    public static <T> T bindReloadable(Class<T> implClazz) {
        return binder.bindReloadable(implClazz);
    }

    public static <T> T bind(Class<T> clazz, Object instance) {
        return binder.bind(clazz, instance);
    }

    public static <T> T bind(T instance) {
        return binder.bind(instance);
    }

    public static <T> T get(Class<T> clazz) {
        return binder.get(clazz);
    }

    //================

    public static AdmCmdHandler getAdmCmdHandler() {
        return get(AdmCmdHandler.class);
    }

    public static AdmClientManager getAdmClientManager() {
        return get(AdmClientManager.class);
    }

    //
    public static void addDb(Db db) {
        dbs.put(db.getName(), db);
    }

    public static Db getDb(String name) {
        return dbs.get(name);
    }

    //
    public static void init(ApplicationContext appCtx) {
        Gs.bind(ApplicationContext.class, appCtx);
    }

    public static <T> T getSer(Class<T> serClazz) {
        return Gs.get(ApplicationContext.class).getBean(serClazz);
    }
}

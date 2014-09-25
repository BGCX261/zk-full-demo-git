package org.hxzon.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//数据容器，数据存储，或数据传递
//tmp和非tmp视为相同数据，优先读取（或删除，检查）tmp开头的数据
public class Data {
    private static final String tmp = "tmp.";
    private Map<String, Object> map;

    protected Data(Map<String, Object> map) {
        this.map = map;
    }

    //
    public static Data createData(Map<String, Object> map) {
        return new Data(map);
    }

    public static Data createData() {
        return new Data(new HashMap<String, Object>());
    }

    public static Data createConcurrentData() {
        return new Data(new ConcurrentHashMap<String, Object>());
    }

    public static Data createOrderedData() {
        return new Data(new LinkedHashMap<String, Object>());
    }

    //
    public int getInt(String key, int d) {
        return Dt.toInt(get0(key), d);
    }

    public long getLong(String key, long d) {
        return Dt.toLong(get0(key), d);
    }

    public float getFloat(String key, float d) {
        return Dt.toFloat(get0(key), d);
    }

    public double getDouble(String key, double d) {
        return Dt.toDouble(get0(key), d);
    }

    public boolean getBoolean(String key, boolean d) {
        return Dt.toBoolean(get0(key), d);
    }

    public String getString(String key, String d) {
        return Dt.toString(get0(key), d);
    }

    //
    public Object get(String key) {
        return get0(key);
    }

    public Object get(String key, Object d) {
        Object o = get0(key);
        return o == null ? d : o;
    }

    @SuppressWarnings("unchecked")
    public <T> T getT(String key, Class<T> clazz) {
        return (T) get0(key);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        return (List<T>) get0(key);
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getMap(String key, Class<T> clazz) {
        return (Map<String, T>) get0(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getArray(String key, Class<T> clazz) {
        return (T[]) get0(key);
    }

    //
    public Set<String> keys() {
        return map.keySet();
    }

    public boolean contains(String key) {
        boolean r = map.containsKey(tmp(key));
        if (r) {
            return r;
        }
        return map.containsKey(key);
    }

    //修改性操作
    public Object put(String key, Object o) {
        return map.put(key, o);
    }

    public Object putTmp(String key, Object o) {
        return put(tmp(key), o);
    }

    public void putAll(Map<String, ?> m) {
        map.putAll(m);
    }

    public void putAllTmp(Map<String, ?> m) {
        for (Map.Entry<String, ?> e : m.entrySet()) {
            putTmp(e.getKey(), e.getValue());
        }
    }

    public Object remove(String key) {
        Object v = map.remove(tmp(key));
        if (v == null) {
            v = map.remove(key);
        }
        return v;
    }

    public void clear() {
        map.clear();
    }

    //
    public static String tmp(String key) {
        if (key.startsWith(tmp)) {
            return key;
        }
        return tmp + key;
    }

    private Object get0(String key) {
        Object v = map.get(tmp(key));
        if (v == null) {
            v = map.get(key);
        }
        return v;
    }
}

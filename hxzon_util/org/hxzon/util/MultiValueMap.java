package org.hxzon.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiValueMap<K, V> {
    private final Map<K, List<V>> map;

    public MultiValueMap(Map<K, List<V>> map) {
        this.map = map;
    }

    public MultiValueMap() {
        this(new HashMap<K, List<V>>());
    }

    public void put(K key, V value) {
        List<V> values = map.get(key);
        if (values == null) {
            values = new ArrayList<V>();
        }
        values.add(value);
        map.put(key, values);
    }

    public List<V> get(K key) {
        return map.get(key);
    }

    public Set<Map.Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }
}

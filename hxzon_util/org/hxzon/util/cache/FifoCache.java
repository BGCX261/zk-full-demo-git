package org.hxzon.util.cache;

import java.util.HashMap;
import java.util.Map;

public class FifoCache<K, V> {
    private Map<K, CacheElement<K, V>> table;
    private CacheElement<K, V>[] cache;
    private int current = 0;
    private int size = 0;

    public FifoCache(int size) {
        table = new HashMap<K, CacheElement<K, V>>();
        cache = new CacheElement[size];
    }

    public final synchronized void remove(K key) {
        if (key == null) {
            return;
        }
        CacheElement<K, V> element = table.get(key);
        if (element != null) {
            element.setValue(null);
            element.setKey(null);
            table.remove(key);
        }
    }

    public final synchronized void add(K key, V value) {
        int index;
        CacheElement<K, V> element = table.get(key);

        if (element != null) {
            element.setValue(value);
            element.setKey(key);

            return;
        }

        if (!isFull()) {
            index = size;
            size++;
            cache[index] = new CacheElement<K, V>();
        } else {
            index = current;
            current++;
            if (current >= cache.length) {
                current = 0;
            }
            table.remove(cache[index].getKey());
        }

        cache[index].setValue(value);
        cache[index].setKey(key);
        table.put(key, cache[index]);
    }

    public synchronized V get(K key) {

        CacheElement<K, V> element = table.get(key);

        if (element != null) {
            element.setHitCount(element.getHitCount() + 1);
            return element.getValue();
//			V value=element.getValue();
//			if(value==null){
//				System.err.print("get cache value null!!!");
//			}
//			return value;
        }
        return null;
    }

    public boolean isFull() {
        return size >= cache.length;
    }
}

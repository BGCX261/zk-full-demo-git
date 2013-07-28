package org.hxzon.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListUtil {
    private static Logger logger = LoggerFactory.getLogger(ListUtil.class);

    public static List<Object> asList(Object source) {
        List<Object> list = new ArrayList<Object>();
        if (source == null) {
            return list;
        }
        if (source instanceof Object[]) {
            Object[] items = (Object[]) source;
            for (Object item : items) {
                list.add(item);
            }
        } else if (source instanceof Collection) {
            Collection<Object> items = (Collection<Object>) source;
            for (Object item : items) {
                list.add(item);
            }
        }
        return list;
    }

    public static List<String> asStringList(Object source) {
        List<String> list = new ArrayList<String>();
        if (source == null) {
            return list;
        }
        if (source instanceof Object[]) {
            Object[] items = (Object[]) source;
            for (Object item : items) {
                list.add(item.toString());
            }
        } else if (source instanceof Collection) {
            Collection<Object> items = (Collection<Object>) source;
            for (Object item : items) {
                list.add(item.toString());
            }
        }
        return list;
    }

    public static List<String> asStringList(Object source, String pattern) {
        List<String> list = new ArrayList<String>();
        if (source == null || pattern == null || pattern.isEmpty()) {
            return list;
        }
        if (source instanceof Object[]) {
            Object[] items = (Object[]) source;
            for (Object item : items) {

                list.add(OgnlUtil.getStringValueOrEmpty(item, pattern));
            }
        } else if (source instanceof Collection) {
            Collection<Object> items = (Collection<Object>) source;
            for (Object item : items) {
                list.add(OgnlUtil.getStringValueOrEmpty(item, pattern));
            }
        }
        return list;
    }

    public static Map<String, Object> valueMap(List<Object> source, String valuePattern) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (source == null || valuePattern == null || valuePattern.isEmpty()) {
            logger.warn("empty map");
            return map;
        }
        for (Object item : source) {
            String key = OgnlUtil.getStringValueOrEmpty(item, valuePattern);
            logger.trace("add key:{}", key);
            map.put(key, item);
        }
        return map;
    }
}

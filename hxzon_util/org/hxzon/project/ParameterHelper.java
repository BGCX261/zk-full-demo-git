package org.hxzon.project;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hxzon.util.OgnlUtil;

public class ParameterHelper {

    public static Map<String, Object> buildParametersMap(String names[], Object objects[]) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (names == null || objects == null || names.length != objects.length) {
            throw new RuntimeException("names and objects must not be null,and names's length must equals objects's length");
        }
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], objects[i]);
        }
        return map;
    }

    public static Map<String, Object> buildParametersMap(String name, Object object) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(name, object);
        return map;
    }

    public static void fillParameters(Map<String, Object> map, Map<String, String[]> parameters) {
        for (Entry<String, String[]> entry : parameters.entrySet()) {
            OgnlUtil.setValue(map, entry.getKey(), entry.getValue());
        }
    }
}

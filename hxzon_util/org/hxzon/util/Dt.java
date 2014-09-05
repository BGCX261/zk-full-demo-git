package org.hxzon.util;

import java.util.HashMap;
import java.util.Map;

//数据类型转换DataType
public class Dt {

    public static int toInt(Object t, int d) {
        if (t == null) {
            return d;
        }
        if (t instanceof Boolean) {
            return ((Boolean) t) ? 1 : 0;
        }
        if (t instanceof Number) {
            return ((Number) t).intValue();
        }
        if (t instanceof String) {
            try {
                return Integer.parseInt((String) t);
            } catch (Exception e) {
            }
        }
        return d;
    }

    public static long toLong(Object t, long d) {
        if (t == null) {
            return d;
        }
        if (t instanceof Boolean) {
            return ((Boolean) t) ? 1 : 0;
        }
        if (t instanceof Number) {
            return ((Number) t).longValue();
        }
        if (t instanceof String) {
            try {
                return Long.parseLong((String) t);
            } catch (Exception e) {
            }
        }
        return d;
    }

    public static float toFloat(Object t, float d) {
        if (t == null) {
            return d;
        }
        if (t instanceof Boolean) {
            return ((Boolean) t) ? 1 : 0;
        }
        if (t instanceof Number) {
            return ((Number) t).floatValue();
        }
        if (t instanceof String) {
            try {
                return Float.parseFloat((String) t);
            } catch (Exception e) {
            }
        }
        return d;
    }

    public static double toDouble(Object t, double d) {
        if (t == null) {
            return d;
        }
        if (t instanceof Boolean) {
            return ((Boolean) t) ? 1 : 0;
        }
        if (t instanceof Number) {
            return ((Number) t).doubleValue();
        }
        if (t instanceof String) {
            try {
                return Double.parseDouble((String) t);
            } catch (Exception e) {
            }
        }
        return d;
    }

    public static boolean toBoolean(Object t, boolean d) {
        if (t == null) {
            return d;
        }
        if (t instanceof Boolean) {
            return ((Boolean) t);
        }
        if (t instanceof Number) {
            //只有1转为true，0转为false，其它数值返回d
            long n = ((Number) t).longValue();
            return n == 1 ? true : (n == 0 ? false : d);
        }
        if (t instanceof String) {
            String s = (String) t;
            if ("true".equalsIgnoreCase(s) || "t".equalsIgnoreCase(s) || "1".equals(s)) {
                return true;
            }
            if ("false".equalsIgnoreCase(s) || "f".equalsIgnoreCase(s) || "0".equals(s)) {
                return false;
            }
        }
        return d;
    }

    public static String toString(Object t, String d) {
        return t == null ? d : t.toString();
    }

    //
    public static int toInt(Map<String, ?> map, String key, int d) {
        return toInt(map.get(key), d);
    }

    public static long toLong(Map<String, ?> map, String key, long d) {
        return toLong(map.get(key), d);
    }

    public static float toFloat(Map<String, ?> map, String key, float d) {
        return toFloat(map.get(key), d);
    }

    public static double toDouble(Map<String, ?> map, String key, double d) {
        return toDouble(map.get(key), d);
    }

    public static boolean toBoolean(Map<String, ?> map, String key, boolean d) {
        return toBoolean(map.get(key), d);
    }

    public static String toString(Map<String, ?> map, String key, String d) {
        return toString(map.get(key), d);
    }

    //
    public static Map<String, Object> toStringMap(Object... objects) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (objects == null) {
            return map;
        }
        for (int i = 0; i < objects.length / 2; i++) {
            map.put((String) objects[i * 2], objects[i * 2 + 1]);
        }
        return map;
    }
}

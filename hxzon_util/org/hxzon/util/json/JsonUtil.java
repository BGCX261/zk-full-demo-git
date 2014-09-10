package org.hxzon.util.json;

public class JsonUtil {

    public static Object parseJson(String str) {
        JSONTokener tokener = new JSONTokener(str);
        return tokener.nextValue();
    }
}

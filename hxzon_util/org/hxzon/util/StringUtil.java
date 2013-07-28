package org.hxzon.util;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
    public static String escapeSql(String input) {
        input = StringUtils.replace(input, "'", "''");
//        input = StringUtils.replace(input, "%", "\\%");
//        input = StringUtils.replace(input, "_", "\\_");
        return input;
    }

    public static String[] splitString(String origString, String split) {
        if (origString == null || origString.isEmpty()) {
            return new String[0];
        }
        String[] result = StringUtils.split(split, " ", -1);
        return result;
    }

    public static String strip(String origString) {
        return StringUtils.strip(origString);
    }
}

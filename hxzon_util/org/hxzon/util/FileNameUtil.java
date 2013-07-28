package org.hxzon.util;

import org.apache.commons.io.FilenameUtils;

public class FileNameUtil {

    public static String windowsFileName(String name) {
        //\ * / ? | : " < >
        return name.replaceAll("[\\\\\\*\\/\\?\\|\\:\\\"\\<\\>]", "-");
    }

    public static String replaceFileExtension(String filename, String newExtension) {
        return FilenameUtils.removeExtension(filename) + "." + newExtension;
    }
}

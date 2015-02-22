package com.github.bmsantos.core.cola.utils;

import static java.io.File.separator;

import java.io.File;
import java.util.List;

public final class ColaUtils {

    public static final String CLASS_EXT = ".class";

    public static boolean isSet(final String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isSet(final List<?> value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isSet(final Object value) {
        return value != null;
    }

    public static boolean isSet(final Object[] value) {
        return value != null && value.length > 0;
    }

    public static boolean isClassFile(final String name) {
        return name.endsWith(CLASS_EXT);
    }

    public static String osToBinary(final String path) {
        return path.replace(separator, ".");
    }

    public static String classToBinary(final String path) {
        return osToBinary(path.replace(CLASS_EXT, ""));
    }

    public static String binaryToOS(final String binaryFormat) {
        return binaryFormat.replace(".", separator);
    }

    public static String binaryToOsClass(final String binaryFormat) {
        String result = binaryToOS(binaryFormat);
        if (!isClassFile(result)) {
            result += CLASS_EXT;
        }
        return result;
    }

    public static boolean binaryFileExists(final String dir, final String clazz) {
        return isSet(dir) && isSet(clazz) && new File(dir + separator + binaryToOsClass(clazz)).exists();
    }

    public static String toOSPath(final String path) {
        return path.replace("/", separator).replace("\\", separator);
    }
}

package xwc.xwcjava.utils;

import java.util.List;

public class StringUtil {

    public static String toCsv(List<String> src) {
        return join(src, ", ");
    }

    public static String join(List<String> src, String delimiter) {
        if(src == null) {
            return null;
        }
        if(delimiter == null) {
            delimiter = "";
        }
        StringBuilder builder = new StringBuilder();
        for(int i=0;i<src.size();i++) {
            if(i>0) {
                builder.append(delimiter);
            }
            builder.append(src.get(i));
        }
        return builder.toString();
    }

    public static String capitaliseFirstLetter(String string) {
        return string != null && string.length() != 0 ? string.substring(0, 1).toUpperCase() + string.substring(1) : string;
    }

    public static String lowercaseFirstLetter(String string) {
        return string != null && string.length() != 0 ? string.substring(0, 1).toLowerCase() + string.substring(1) : string;
    }

    public static String zeros(int n) {
        return repeat('0', n);
    }

    public static String repeat(char value, int n) {
        return (new String(new char[n])).replace("\u0000", String.valueOf(value));
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}

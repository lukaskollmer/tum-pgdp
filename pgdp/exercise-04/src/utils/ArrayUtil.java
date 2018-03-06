package utils;

import java.util.List;

public class ArrayUtil {
    // Turn an array of strings into a single string by joining its elements with `separator`
    public static String join(List<String> list, String separator) {
        String string = "";

        for (int i = 0; i < list.size(); i++) {
            string += list.get(i) + ((i == list.size() - 1) ? "" : separator);
        }

        return string;
    }
}

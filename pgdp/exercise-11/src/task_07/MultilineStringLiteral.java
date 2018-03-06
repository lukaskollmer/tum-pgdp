/*
*
* Multiline String Literals
*
* (more info: https://github.com/lukaskollmer/MultilineStringLiteral)
*
* */

package task_07;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MultilineStringLiteral {

    public static String directory = "./";

    private static Map<String, String> cache = new HashMap<>();


    public static class Template {
        public final String template;
        public final String substitution;

        public Template(String template, String substitution) {
            this.template = template;
            this.substitution = substitution;

        }
    }

    // [precondition] no duplicates
    public static String read(Template... templates) {
        StackTraceElement element = new RuntimeException().getStackTrace()[1];
        String name = directory + element.getClassName().replace('.', '/') + ".java";

        String identifier = String.format("%s:%s", name, element.getLineNumber());

        String text = cache.get(identifier);

        if (text == null) {
            InputStream in = null;
            try {
                in = new FileInputStream(name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String s = convertStreamToString(in, element.getLineNumber());

            text = s.substring(s.indexOf("/*") + 2, s.indexOf("*/"));

            cache.put(identifier, text);
        }

        for (Template template : templates) {
            text = text.replace(template.template, template.substitution);
        }

        return text.trim();
    }

    private static String convertStreamToString(InputStream is, int lineNum) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        int i = 1;
        try {
            while ((line = reader.readLine()) != null) {
                if (i++ >= lineNum) {
                    sb.append(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}

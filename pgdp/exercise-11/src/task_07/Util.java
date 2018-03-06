package task_07;

import java.io.FileReader;
import java.util.*;
import java.util.function.Predicate;

/**
 * Utils
 * */
public class Util {

    public static boolean DEBUG = false;


    public static String f(String format, Object... args) {
        return String.format(format, args);
    }


    public static <T> T nullCoalescing(T a, T b) {
        return a != null ? a : b;
    }


    // log to stdout if the debug flag is set
    public static void log(String format, Object... args) {
        if (DEBUG) {
            System.out.format(format, args);
        }
    }

    // Reverse an integer array
    public static int[] reversed(int[] input) {
        int[] reversed = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            reversed[input.length - i - 1] = input[i];
        }

        return reversed;
    }


    // read a single line from stdin
    public static String readSingleLine(String prompt) {
        System.out.print(prompt);
        return new Scanner(System.in).nextLine();
    }


    // Read multiple lines from stdin
    public static List<String> readMultipleLines(String prompt, int numberOfEmptyLinesRequiredToReturn) {
        System.out.print(prompt);

        List<String> input = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        int numberOfEmptyLines = 0;

        while (numberOfEmptyLines < numberOfEmptyLinesRequiredToReturn) {
            String nextLine = scanner.nextLine();
            input.add(nextLine);

            numberOfEmptyLines += nextLine.length() == 0 ? 1 : 0;
        }

        return input;
    }

    
    static String readFile(String path) {
        StringBuilder contents = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new FileReader(path));

            while (scanner.hasNextLine()) {
                contents.append(scanner.nextLine());
                contents.append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return contents.toString();
    }


    public enum StartLocation { BEGINNING, END }

    // Get the index of the first element in the list where `predicate` evaluates to true
    public static <T> int firstWhere(List<T> collection, Predicate<T> predicate, StartLocation startLocation) {
        int start = startLocation == StartLocation.BEGINNING ? 0 : collection.size() - 1;
        int end   = startLocation == StartLocation.BEGINNING ? collection.size() : 0;

        int i = start;
        while (i != end) {
            if (predicate.test(collection.get(i))) {
                return i;
            }

            if (startLocation == StartLocation.BEGINNING) {
                i++;
            } else {
                i--;
            }
        }

        return -1;
    }
}

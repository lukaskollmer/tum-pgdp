package task_09;

import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    public static <T> boolean containsWhere(List<T> list, Predicate<T> predicate) {
        return indexWhere(list, predicate) != -1;
    }

    public static <T> int indexWhere(List<T> list, Predicate<T> predicate) {
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) return i;
        }

        return -1;
    }

    public static <T> boolean hasDuplicates(List<T> list) {
        List<T> copy = new ArrayList<>(list);

        while (!copy.isEmpty()) {
            T element = copy.remove(0);

            for (T remainingElement : copy) {
                if (remainingElement.equals(element) || remainingElement == element) {
                    return true;
                }
            }
        }

        return false;
    }


    // split `input` by one or more one-character delimiters, but keep the delimiters in the resulting array
    // this internally uses a regex, but you do not need to escape the delimiters
    public static List<String> split(String input, String... delimiters) {

        // Create a regex pattern for splitting by a specific character
        java.util.function.Function<String, String> regexPattern = delimiter -> String.format("((?<=\\%s)|(?=\\%s))", delimiter, delimiter);

        List<List<String>> components = new ArrayList<>();

        for (String delimiter : delimiters) {
            if (components.isEmpty()) {
                for (String s : input.split(regexPattern.apply(delimiter))) {
                    components.add(Collections.singletonList(s));
                }
            } else {
                components = components
                        .stream()
                        .map(sublist -> sublist
                                .stream()
                                .map(s -> Arrays.asList(s.split(regexPattern.apply(delimiter))))
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                        .collect(Collectors.toList());
            }
        }

        return components
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    //
    // java is such a fucking joke
    //

    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Throwable> {
        void apply(T arg0) throws E;
    }

    // 3 parameter lambda
    @FunctionalInterface
    interface TriFunction<T, U, V, R> {
        R apply(T arg0, U arg1, V arg2);
    }

    // 4 parameter lambda
    @FunctionalInterface
    interface QuadFunction<T, U, V, W, R> {
        R apply(T arg0, U arg1, V arg2, W arg3);
    }

    // 3 parameter lambda w/out a return type
    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void apply(T arg0, U arg1, V arg2);
    }


    //
    // random number generation
    //


    public static int random() {
        return random(Integer.MIN_VALUE, Integer.MAX_VALUE - 1);
    }

    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    private static Set<Integer> _uniqueRandomNumber_cache = new HashSet<>(Collections.singletonList(null));

    public static int uniqueRandomNumber() {
        Integer number = null;

        while (_uniqueRandomNumber_cache.contains(number)) {
            number = random(0, 100_000);
        }

        _uniqueRandomNumber_cache.add(number);

        return number;
    }
}

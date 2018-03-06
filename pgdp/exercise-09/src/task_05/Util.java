package task_05;


import java.util.concurrent.ThreadLocalRandom;

public class Util {

    public static void logm(String format, Object... args) {
        String message = String.format(format, args);
        StackTraceElement callingStackFrame = Thread.currentThread().getStackTrace()[2];

        System.out.format("[%s %s] %s", callingStackFrame.getClassName(), callingStackFrame.getMethodName(), message);
    }


    static <T> T getRandom(T[] array) {
        return array[random(0, array.length-1)];
    }

    static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    // generate a random boolean w/ probability
    static boolean random(double probability) {
        return random(0, 100) < probability * 100;
    }



    // get the first value of `array` that is also in `acceptableValues`
    static <T> T getAnyMatching(T[] array, T... acceptableValues) {
        for (T value : array) {
            for (T acceptableValue : acceptableValues) {
                if (value.equals(acceptableValue)) {
                    return value;
                }
            }
        }

        return null;
    }


    // shamelessly stolen from `BitteNichtAbgeben.java`
    // this is what allows us to get a list of all different kinds of objects at the same position

    static int fgShift(int arg) {
        return arg & 0x000000FF;
    }

    static int bgShift(int arg) {
        return (arg >> 8) & 0x000000FF;
    }
}
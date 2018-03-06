/*
 * exercise-03/task-08
 * Check whether a number is a palindrome (eg 100010001, 545)
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-08
 * status   in-progress
 */


import java.util.ArrayList;

class Task08_Utils {

    // Lambda for evaluating a generic condition
    interface Predicate<T> {
        boolean evaluate(T obj);
    }

    // Ask the user to enter an integer. This is repeated until `condition` evaluates to true
    static int getInt(String message, Task08_Utils.Predicate<Integer> condition) {
        Integer input;

        do {
            input = MiniJava.readInt(message);
        } while (!condition.evaluate(input));

        return input;
    }
}

public class Main_Task_08 extends MiniJava {

    public static void main(String[] args) {

        // Request a positive number (greater than 0)
        int input = Task08_Utils.getInt("Enter a positive number", n -> n > 0);
        int value = input; // `value` will be mutated, but we preserve `input` to include it in the final output

        // Split the number into separate digits and store them in an array
        // (we can't use a c-style array since we don't know the number of digits in advance)
        // We get the individual digits via the modulo operation, then divide `value` by 10 to get rid of the last digit
        // (`digits` will contain the digits in reverse order, but we can safely ignore that)

        ArrayList<Integer> digits = new ArrayList<>();

        while (value != 0) {
            digits.add(value % 10);
            value *= 0.1;
        }

        // loop over the array and compare each value with its 'counterpart' at the other end of the array
        // if the two values are not equal, the number isn't a palindrome

        // since we compare the same two values twice, this might seem like a bad implementation
        // a different approach would be to iterate only over the first half of the array (that's sufficient to detect a palindrome)
        // however, that introduces the extra step of calculating the index of the middle of the array ( `Math.ceil(array.size() / 2)` )
        // and therefore is a lot slower than the implementation below
        // (I ran some tests and the current implementation is about twice as fast as the approach I describe above)


        int size = digits.size();    // fetching the size before the loop makes it run faster
        boolean isPalindrome = true; // let's start by assuming that the number is in fact a palindrome

        for (int i = 0; i < size; i++) {
            int reverseIndex = size - 1 - i;

            if (digits.get(i) != digits.get(reverseIndex)) {
                isPalindrome = false;
            }
        }

        write(String.format("The number %s is %s palindrome", input, isPalindrome ? "a" : "not a"));
    }
}

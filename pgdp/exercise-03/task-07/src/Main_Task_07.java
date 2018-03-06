/*
 * exercise-03/task-07
 * Request array size and elements from user, then
 * 1. sum all elements at odd indices and subtract it from the sum of all elements at even indices
 * 2. Find the second-largest element
 * 3. Sum all pairs of neighboring elements and override the first one with the sum
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-08
 * status   in-progress
 */

import java.util.Arrays; // Arrays.toString


class Task07_Utils {

    // Lambda for evaluating a generic condition
    interface Predicate<T> {
        boolean evaluate(T obj);
    }

    // Ask the user to enter an integer
    static int getInt(String message) {
        return getInt(message, n -> true);
    }

    // Ask the user to enter an integer. This is repeated until `condition` evaluates to true
    static int getInt(String message, Task07_Utils.Predicate<Integer> condition) {
        Integer input;

        do {
            input = MiniJava.readInt(message);
        } while (!condition.evaluate(input));

        return input;
    }
}


public class Main_Task_07 extends MiniJava {

    public static void main(String[] args) {
        //
        // Create the array (ask for size and then request the individual elements)
        //

        int size = Task07_Utils.getInt("How big should the array be (enter a number >= 2)", n -> n >= 2);
        
        int array[] = new int[size];

        for (int i = 0; i < array.length; i++) {
            array[i] = Task07_Utils.getInt(String.format("Enter a number for #%s", i));
        }
        

        //
        // Calculate the sum all elements at even indices and subtract that from the sum of all elements at odd indices
        //
        
        int evenSum = 0;
        int oddSum  = 0;
        
        for (int i = 0; i < array.length; i++) {
            if (i % 2 == 0) { // is even
                evenSum += array[i];
            } else { // is odd
                oddSum += array[i];
            }
        }

        write(String.format("Sum of all values at odd indices, subtracted form the sum of all values at even indices: %s\n", oddSum - evenSum));


        //
        // Finding the second-largest element in the array
        //
        
        // 1. Get the largest value
        // 2. Get the largest value smaller than the largest we found in the first iteration
        
        int largest = Integer.MIN_VALUE;        
        for (int value : array) {
            if (value > largest) {
                largest = value;
            }
        }
        
        int secondLargest = Integer.MIN_VALUE;
        for (int value : array) {
            if (value > secondLargest && value < largest) {
                secondLargest = value;
            }
        }
        
        write(String.format("\nSecond-largest value in the array: %s\n", secondLargest));


        //
        // Sum all pairs of neighboring elements and override the first one with the sum
        //
        
        for (int i = 0; i < array.length-1; i += 2) {
            array[i] += array[i + 1];
        }
        
        write(String.format("\nResult after adding neighboring fields: %s", Arrays.toString(array)));
    }
}

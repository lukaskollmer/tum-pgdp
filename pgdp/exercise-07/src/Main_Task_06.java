/*
* exercise-07/task-06
*
* Implementing two recursive sorting algorithms: slowSort and evenSlowerSort
*
* */

import java.util.Arrays; // Arrays.toString

public class Main_Task_06 {
    public static void main(String... args) {
        int[] input_0 = {1, 3, 1, 9, 4, 2};
        int[] input_1 = {5, 4, 8, 7, 2, 6, 3, 1, 9, 0};

        System.out.format("before slowsort: %s\n", Arrays.toString(input_0));
        slowsort(input_0);
        System.out.format("after slowsort:  %s\n", Arrays.toString(input_0));

        System.out.format("before evenSlowerSort: %s\n", Arrays.toString(input_1));
        evenSlowerSort(input_1);
        System.out.format("after evenSlowerSort:  %s\n", Arrays.toString(input_1));
    }



    // slowsort entry point
    static void slowsort(int[] array) {
        slowsort_imp(array, 0, array.length - 1);
    }

    // The actual slowsort implementation
    // This function recursively sorts the part of the array between `lowerBound` and `upperBound`
    static void slowsort_imp(int[] array, int lowerBound, int upperBound) {
        // Only proceed sorting the subarray within `lowerBound` and `upperBound` into smaller subarrays
        // if `lowerBound` is still smaller than `upperBound` (eg there is still at least one element left)
        if (lowerBound >= upperBound) {
            return;
        }

        // Calculate the index of the middle of the array
        int middle = (lowerBound + upperBound) / 2;

        // Now we sort the subarray between `lowerBound` and `middle` and the subarray between `middle + 1` and `upperBound`
        slowsort_imp(array, lowerBound, middle);
        slowsort_imp(array, middle + 1, upperBound);

        // check whether the last element of the first half of the array is greater
        // than the last element of the second half (ie the entire array) and swap them if necessary
        if (array[middle] > array[upperBound]) {
            swapElement(array, middle, upperBound);
        }

        // sort the entire array again, this time excluding the last element
        slowsort_imp(array, lowerBound, upperBound - 1);
    }




    // evenSlowerSort entry point
    static void evenSlowerSort(int[] array) {
        evenSlowerSort_imp(array, 0, array.length - 1);
    }

    // The actual evenSlowerSort implementation
    // This function recursively sorts the part of the array between `lowerBound` and `upperBound`
    // This basically works like the slowsort function above, except that we break down the array into three subarrays (instead of two)
    static void evenSlowerSort_imp(int[] array, int lowerBound, int upperBound) {
        if (lowerBound >= upperBound) {
            return;
        }

        // We first have to calculate the bounds of three subarrays of the current array (within the given bounds)
        double total = lowerBound + upperBound;
        int firstThirdUpperBound  = (int) (total * 1/3);
        int secondThirdUpperBound = (int) (total * 2/3);

        // We also have to make sure that the start indexes don't exceed the current bounds
        firstThirdUpperBound  = Math.max(lowerBound, firstThirdUpperBound);
        secondThirdUpperBound = Math.min(upperBound, secondThirdUpperBound);

        // Sort each of the three subarrays
        evenSlowerSort_imp(array, lowerBound, firstThirdUpperBound);
        evenSlowerSort_imp(array, firstThirdUpperBound  + 1, secondThirdUpperBound);
        evenSlowerSort_imp(array, secondThirdUpperBound + 1, upperBound);


        // Compare each of the last elements of the first two sub-arrays with the last element
        // and replace them if necessary

        if (array[firstThirdUpperBound] > array[upperBound]) {
            swapElement(array, firstThirdUpperBound, upperBound);
        }

        if (array[secondThirdUpperBound] > array[upperBound]) {
            swapElement(array, secondThirdUpperBound, upperBound);
        }


        // sort the entire array again, this time excluding the last element
        evenSlowerSort_imp(array, lowerBound, upperBound - 1);
    }

    // lil' helper function to swap the elements at the indices `x` and `y`
    static void swapElement(int[] array, int x, int y) {
        int temp = array[x];
        array[x] = array[y];
        array[y] = temp;
    }

}

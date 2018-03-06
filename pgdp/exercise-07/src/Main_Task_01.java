import java.util.Arrays;

public class Main_Task_01 {
    public static void main(String[] args) {
        int[] test = {5, 1, 3, 9, 1, 5, 3};
        //swap(test, 0, 2);
        System.out.format("%s\n", Arrays.toString(quicksorted(test)));
    }


    static int[] quicksorted(int[] input) {
        int[] array = input.clone();
        quicksort_in_place(array, 0, array.length - 1);
        return array;
    }


    static void quicksort_in_place(int[] input, int start, int end) {
        int pivotElementIndex = partition(input, start, end);

        if (start < end) {
            quicksort_in_place(input, start, pivotElementIndex - 1);
            System.out.format("after first iteration: %s\n", Arrays.toString(input));

            quicksort_in_place(input, pivotElementIndex + 1, end);
            System.out.format("after scnd_ iteration: %s\n", Arrays.toString(input));
        }
    }

    static void swap(int[] numbers, int a , int b) {
        int temp = numbers[a];

        numbers[a] = numbers[b];
        numbers[b] = temp;
    }


    static int partition(int[] numbers, int left, int right) {
        System.out.format("%s\n", "partition() called with: numbers = [" + numbers + "], left = [" + left + "], right = [" + right + "]");
        int pivot = numbers[right];

        int leftIndex = left;
        int rightIndex = right - 1;


        while (numbers[leftIndex] < pivot) {
            leftIndex++;
        }

        while (numbers[rightIndex] > pivot) {
            rightIndex--;
        }

        System.out.format("leftIndex: %s\n", leftIndex);
        System.out.format("rightIndex: %s\n", rightIndex);

        swap(numbers, leftIndex, rightIndex);
        swap(numbers, right - 1, rightIndex);

        System.out.format("after swap: %s\n", Arrays.toString(numbers));


        return rightIndex - 1;
    }
}

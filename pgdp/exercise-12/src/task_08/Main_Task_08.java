package task_08;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Main_Task_08 {
    public static void main(String... args) {

        int[] array = new int[100_000];

        for (int i = 0; i < array.length; i++) {
            array[i] = new Random().nextInt();
        }

        //int[] array = {1, 6, 9, 4, 2, 7, 0, 3, 2, 6, 7, 4, 9, 1};
        int[] array2 = array.clone();
        int[] array3 = array.clone();


        BiConsumer<String, Runnable> benchmark = (name, runnable) -> {
            long start = System.nanoTime();

            runnable.run();

            System.out.format("[duration] %s: %s\n", name, System.nanoTime() - start);
        };


        benchmark.accept("normal  ", () -> NormalMergeSort.mergeSort(array2));
        benchmark.accept("parallel", () -> ParallelMergeSort.mergeSort(array3));



        Arrays.sort(array);

        if (Arrays.equals(array, array) && Arrays.equals(array2, array)) {
            System.out.format("success\n");
        } else {
            System.out.format("failure\n");
        }
    }
}

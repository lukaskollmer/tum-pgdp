package task_08;

public class ParallelMergeSort {

    public static int numberOfThreads = 8;
    private static int currentNumberOfThreads = 0;


    public static void mergeSort(int[] array)  {

        // reset the current number of threads
        currentNumberOfThreads = 0;
        mergeSort(array, 0, array.length - 1);
    }

    private static void mergeSort(int[] arr, int low, int high) {
        if (low < high) {
            int mid = (low + high) / 2;

            // spawn new threads until we reach the limit
            if (currentNumberOfThreads++ < numberOfThreads) {
                Thread t = new Thread(() -> {
                    mergeSort(arr, low, mid);
                    mergeSort(arr, mid + 1, high);
                });

                // start the thread,
                t.start();

                // wait for the thread to finish
                // this might seem a bit stupid at first, but you have to keep in mind that this is recursive
                // and the thread we're waiting for will spawn new threads on its own
                // why do we have to wait? because there's no point merging the sub arrays until they're all sorted
                try { t.join(); } catch (InterruptedException ignored) {}

                NormalMergeSort.merge(arr, low, mid, high);

            } else {
                NormalMergeSort.mergeSort(arr, low, mid);
                NormalMergeSort.mergeSort(arr, mid + 1, high);
                NormalMergeSort.merge(arr, low, mid, high);
            }
        }
    }
}
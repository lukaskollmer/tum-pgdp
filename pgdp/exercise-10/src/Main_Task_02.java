import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.function.Function;

public class Main_Task_02 {

    public static void main(String... args) {
        System.out.format("main\n");


        Heap heap = new Heap(1, 3, 4, 8, 5, 0, 4, 6);
        //Heap heap = new Heap(9, 3, 4);
        System.out.format("BEFORE: %s\n", Arrays.toString(heap.values));
        //heap.build();
        //System.out.format("AFTER : %s\n", Arrays.toString(heap.values));

        System.out.format("sorted: %s\n", Arrays.toString(heap.sorted()));


        //HeapNode<Integer> heap = new HeapNode<>(5);
        //heap.a
    }
}




class Heap {
    int[] values;

    Heap(int... values) {
        this.values = values;
    }

    int[] sorted() {
        int[] tmp_sorted = new int[values.length];
        this.build();

        for (int i = 0; i < values.length; i++) {
            // get the frontmost element
            tmp_sorted[i] = values[0];

            // move an element back to the front
            values[0] = values[values.length - i - 1];

            // start pushing down, but stop when we reach the end (the tree gets smaller over time)
            down(0, this.values.length - i - 2);
        }

        return tmp_sorted;
    }


    void build() {
        for (int i = values.length / 2; i >= 0; i--) {
            down(i, values.length - 1);
        }
    }


    void down(int index, int stop) {

        if (index > stop) {
            return;
        }
        int mindex = minIndex(index, idx_leftChild(index), idx_rightChild(index), stop);
        if (mindex != index && values[mindex] < values[index]) {
            swap(index, mindex);
            down(mindex, stop);
        }

        if (true) return;

        if (idx_leftChild(index) < stop) {
            int leftChild = values[idx_leftChild(index)];
            if (values[index] <= leftChild) {
                // value smaller or equal to left child
            } else {
                //value greater than to left child
                swap(index, idx_leftChild(index));
                down(idx_leftChild(index), stop);
            }
        }


        if (idx_rightChild(index) < stop) {
            int rightChild = values[idx_rightChild(index)];
            if (values[index] <= rightChild) {
                //value smaller or equal to right child
            } else {
                // value greater than to right child
                swap(index, idx_rightChild(index));
                down(idx_rightChild(index), stop);
            }
        }
    }

    // Mindestens i1 muss <= stop sein!
    private int minIndex(int i1, int i2, int i3, int stop) {
        if (i2 > stop || values[i1] <= values[i2]) {
            return i3 > stop || values[i1] <= values[i3] ? i1 : i3;
        } else {
            return i3 > stop || values[i2] <= values[i3] ? i2 : i3;
        }
    }


    void swap(int a, int b) {
        int temp = values[a];
        values[a] = values[b];
        values[b] = temp;
    }





    // utils
    static int idx_leftChild(int index) {
        return (2 * index) + 1;
    }

    static int idx_rightChild(int index) {
        return (2 * index) + 2;
    }
}






class HeapNode<T extends Comparable<T>> {

    private boolean isRoot = true; // true until it becomes another node's child

    private T value;

    HeapNode<T> leftChild;
    HeapNode<T> rightChild;
    WeakReference<HeapNode<T>> parent; // nil if `isRoot == true`

    // todo second initializer that accepts a custom comparator function?
    HeapNode(T value) {
        this.value = value;
    }
}
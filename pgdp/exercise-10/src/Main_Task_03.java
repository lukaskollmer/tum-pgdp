import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main_Task_03 {

    public static void main(String... args) {

        Task t1_5 = new Task(5);
        Task t2_7 = new Task(7);
        Task t3_0 = new Task(0);
        Task t4_1000 = new Task(1000);
        Task t5_neg_12 = new Task(-12);
        Task t6_8 = new Task(8);


        Queue<Task> queue = new Queue<>();

        for (Task task : Arrays.asList(t1_5, t2_7, t3_0, t4_1000, t5_neg_12, t6_8)) {
            queue.add(task);
        }

        while (queue.size() > 0) {
            System.out.format("next: %s\n", queue.poll());
        }

    }
}


class Task implements Comparable<Task> {

    private final int importance;

    Task(int importance) {

        this.importance = importance;
    }

    @Override
    public int compareTo(Task otherTask) {
        return Integer.compare(this.importance, otherTask.importance);
    }

    @Override
    public String toString() {
        return String.format("<Task importance=%s >", importance);
    }
}


class Queue<T extends Comparable<T>> {
    List<T> elements = new ArrayList<>();

    Queue() {
    }



    // total # of all nonnull elements currently in the queue
    int size() {
        return this.elements.size();
        //return this.elements.stream().filter(Objects::nonNull).collect(Collectors.toList()).size();
    }


    void clear() {
        this.elements.clear();
    }


    void add(T element) {
        this.elements.add(element);
        this.sort();
    }


    // get next element. removes the element from our backing
    T poll() {
        T element = this.elements.remove(0);
        sort();
        return element;
    }


    // get the next element. keeps the element in our backing
    T peek() {
        return this.elements.get(0);
    }



    private void build_heap() {
        for (int i = this.elements.size() / 2; i >= 0; i--) {
            down(i, elements.size() - 1);
        }
    }

    void sort() {
        List<T> sorted = new ArrayList<>();

        this.build_heap();

        for (int i = 0; i < elements.size(); i++) {
            // get the frontmost element
            sorted.add(i, peek());

            // move an element back to the front
            elements.set(0, elements.get(elements.size() - i - 1));
            //values[0] = values[values.length - i - 1];

            // start pushing down, but stop when we reach the end (the tree gets smaller over time)
            down(0, this.elements.size() - i - 2);
        }


        this.elements = sorted;
    }


    void down(int index, int stop) {
        if (index > stop) {
            return;
        }
        int mindex = minIndex(index, idx_leftChild(index), idx_rightChild(index), stop);

        if (mindex != index && compare(elements.get(mindex), elements.get(index)) == ComparisonResult.Descending) {
            swap(index, mindex);
            down(mindex, stop);
        }
    }


    // Mindestens i1 muss <= stop sein!
    private int minIndex(int i1, int i2, int i3, int stop) {
        if (i2 > stop ||
                compare(elements.get(i1), elements.get(i2)) == ComparisonResult.Descending ||
                compare(elements.get(i1), elements.get(i2)) == ComparisonResult.Same) {

            if (i3 > stop ||
                    compare(elements.get(i1), elements.get(i3)) == ComparisonResult.Descending ||
                    compare(elements.get(i1), elements.get(i3)) == ComparisonResult.Same) {
                return i1;
            } else {
                return i3;
            }
        } else {

            if (i3 > stop ||
                    compare(elements.get(i2), elements.get(i3)) == ComparisonResult.Descending ||
                    compare(elements.get(i2), elements.get(i3)) == ComparisonResult.Same) {
                return i2;
            } else {
                return i3;
            }
        }
    }


    void swap(int a, int b) {
        T temp = elements.get(a);
        elements.set(a, elements.get(b));
        elements.set(b, temp);
    }


    // utils
    static int idx_leftChild(int index) {
        return (2 * index) + 1;
    }

    static int idx_rightChild(int index) {
        return (2 * index) + 2;
    }









    //
    // Comparisons
    //

    enum ComparisonResult {
        Ascending,
        Same,
        Descending,
        Undefined
    }


    // compare two values
    static <T extends Comparable<T>> ComparisonResult compare(T left, T right) {
        if (left == null || right == null) {
            return ComparisonResult.Undefined;
        }

        int cmp = left.compareTo(right);
        if (cmp < 0) {
            return ComparisonResult.Ascending;
        } else if (cmp == 0) {
            return ComparisonResult.Same;
        } else {
            return ComparisonResult.Descending;
        }
    }




}

package task_07.interpreter;

import task_07.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

// todo make this generic, if possible
public class Heap {


    public static class Slice implements task_07.arc.Trackable {

        final WeakReference<Heap> heap;

        private int lowerBound;
        private int upperBound;

        Slice(Heap heap, int lowerBound, int upperBound) {
            this.heap = new WeakReference<>(heap);
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }


        public int get(int index) {
            if (Util.DEBUG) System.out.format("index: %s, size: %s, lowerBound: %s\n", index, getSize(), lowerBound);
            if (index < -1 || index > getSize() - 1) throw new RuntimeException("Accessing invalid element!");

            return this.heap.get().backing.get(lowerBound + index + 1);
        }

        public int set(int index, int value) {
            if (index <= -1 || index > getSize() - 1) throw new RuntimeException("Accessing invalid element!");

            int oldValue = get(index);
            this.heap.get().backing.set(lowerBound + index + 1, value);
            return oldValue;
        }



        public List<Integer> getElements() {
            List<Integer> elements = new ArrayList<>();
            for (int i = lowerBound + 1; i <= upperBound; i++) {
                elements.add(heap.get().backing.get(i));
            }
            return elements;
        }

        int getSize() {
            return this.heap.get().backing.get(lowerBound);
        }


        @Override
        public String toString() {
            return String.format("<Heap.Slice bound=[%s, %s], elements: %s size: %s refcount: %s >", lowerBound, upperBound, getElements(), getSize(), retainCount());
        }


        @Override
        public void dealloc() {
            if (Util.DEBUG) System.out.format("[DEALLOC] %s\n", this);
            this.heap.get().deallocate(this);
        }
    }





    public final int size;

    //private final int[] backing;
    private final List<Integer> backing;

    // we keep track of all known slices and update them if the backing array changed
    private List<WeakReference<Slice>> knownSlices = new ArrayList<>();

    public Heap(int size) {
        this.size = size;

        this.backing = new ArrayList<>(size);
        fillAsLongAsNecessary();
    }


    private void fillAsLongAsNecessary() {
        while (backing.size() <= this.size) backing.add(-1);
    }


    private void setAll(int lowerBound, int upperBound, int value) {
        for (int i = lowerBound; i <= upperBound; i++) this.backing.set(i, value);
    }

    // Get the index of the first free cell on the heap (aka the cell following the last cell of the last array)
    private int getUpperBound() {
        if (backing.get(0).equals(-1)) return 0;

        int upperBound = 0;
        while (true) {
            upperBound += backing.get(upperBound) + 1;
            if (backing.get(upperBound).equals(-1)) break;
        }

        return upperBound;
    }


    //
    // Allocating new data
    //

    // allocate a new array of the specified size
    public Slice allocate(int size) {

        if (Util.DEBUG) System.out.format("[ALLOC]\n");

        int lowerBound = getUpperBound();
        int upperBound = lowerBound + size;

        Slice slice = new Slice(this, lowerBound, upperBound);
        setAll(lowerBound, upperBound, 0);

        this.backing.set(lowerBound, size);

        knownSlices.add(new WeakReference<>(slice));

        return slice;
    }


    private void deallocate(Slice slice) {
        // - remove the elements within the slice's bounds
        // - update the bounds of the other slices (this applies only to the slices stored _after_ the one we're removing)

        int sizeOfRemovedSlice = slice.getSize() + 1;

        for (WeakReference<Slice> slice_ref : knownSlices) {
            Slice _slice = slice_ref.get();
            if  (_slice.lowerBound > slice.upperBound) {
                _slice.lowerBound -= sizeOfRemovedSlice;
                _slice.upperBound -= sizeOfRemovedSlice;
            }
        }


        BiConsumer<Integer, Integer> resetInRange = (lower, upper) -> {
            for (int j = upper; j >= lower; j--) {
                backing.remove(j);
            }
        };

        resetInRange.accept(slice.lowerBound, slice.upperBound);


        for (WeakReference<Slice> slice_ref : knownSlices) {
            if (slice_ref.get() == slice) {
                knownSlices.remove(slice_ref);
                break;
            }
        }

        fillAsLongAsNecessary();
    }




    //
    // Size
    //


    public boolean isEmpty() {
        return getNumberOfElements() == 0;
    }

    public int getNumberOfElements() {
        return knownSlices.size();
    }



    //
    // Debugging
    //


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(backing);

        return stringBuilder.toString();
    }
}

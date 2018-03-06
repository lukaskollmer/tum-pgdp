package task_07.interpreter;

import java.util.ArrayList;
import java.util.List;

import task_07.Util;

/**
 * Stack
 *
 * The `Stack` class implements a generic LIFO stack
 *
 * NOTE: Due to how the stack is implemented, the contents of the stack (or, more specific,
 * the stack's backing array) do not always represent the actual state of the stack
 *
 * Example:
 * - push 1
 * - push 2
 * - push 3
 * - pop
 *
 * after the three pushes, the stack's contents are [1, 2, 3, ...], as one would expect
 * after the pop however, the stack's contents are still [1, 2, 3, ...], since the pop just decrements the stack pointer, but doesn't zero out the popped fields
 *
 * As long as you just use the methods exposed as part of Stack's public API to manipulate the stack, you can safely ignore this
 * */
public class Stack<T> {
    final int size;
    private final List<T> backing;

    // The initial value of all elements in the stack
    // This is used when resetting the stack
    private final T initialValue;

    // Only mutate these if you know what you're doing
    public int framePointer = -1;
    public int stackPointer = -1;

    // Create a new stack of the specified size, filled with `null` values
    Stack(int size) {
        this(size, null);
    }

    // Create a new Stack of the specified size, filled with `initialValue`
    Stack(int size, T initialValue) {
        this.size = size;
        this.backing = new ArrayList<>(size);
        this.initialValue = initialValue;

        // Fill the backing list w/ initialValue
        this.reset();
    }

    // Push a value onto the stack
    // This increments the stack pointer by one
    public void push(T value) {
        assertStackPointer();
        backing.set(++stackPointer, value);
    }


    // Pop a value off the stack
    // This decrements the stack pointer by one
    public T pop() {
        assertStackPointer();
        return this.backing.get(stackPointer--);
    }


    // Push a value onto the frame, at the specified index
    // The actual location on the frame will be calculated by adding the current stack pointer
    public void pushFrame(int index, T value) {
        this.backing.set(this.framePointer + index, value);
    }


    // Get the value in the frame at the specified index
    // This function is non-mutating
    public T getFrameElement(int index) {
        return this.backing.get(this.framePointer + index);
    }


    // Check whether the stack is currently empty
    public boolean isEmpty() {
        return this.stackPointer == -1;
    }


    // Reset the stack
    // This sets all fields to the initial value and resets both the stack- and the framepointer
    public void reset() {
        this.backing.clear();
        for (int i = 0; i < size; i++) {
            this.backing.add(initialValue);
        }

        stackPointer = -1;
        framePointer = -1;
    }


    // Make sure the stack pointer is valid
    private void assertStackPointer() {
        if (stackPointer >= this.size - 1) {
            throw new RuntimeException("Stack Overflow");
        } else if (stackPointer <= -2) {
            throw new RuntimeException("Stack Underflow");
        }
    }


    /**
     * Get a textual representation of the stack
     *
     * Note:
     * 1) This does not include the entire stack, just the subrange that currently stores values
     * 2) This might not represent the actual state of the stack, for more information see above
     * */
    @Override
    public String toString() {
        return String.format("<Stack sptr=%s fptr=%s elements=%s>", this.stackPointer, this.framePointer, this.backingToString(5));
    }


    /**
     * Turn the backing array into a string
     *
     * Why is this necessary, instead of just using `Arrays.toString`?
     * `Arrays.toString` returns a string describing the entire array, but since our stack might be very large, this can get messy pretty quick
     *
     * Instead, we calculate the index of the last nonzero element in the stack and create a string representation of all elements up to that one
     * In addition to that, we also accept a `minimumNumberOfElements` parameter which (as the name suggests) specifies the minimum number of elements
     * that will be included in the string representation, even if they are all zero
     * */
    private String backingToString(int minimumNumberOfElements) {
        // Always include fields that currently store values, even if these values are 0
        minimumNumberOfElements = Math.max(minimumNumberOfElements, Math.max(stackPointer, framePointer));

        // note that we don't use `!i.equals(initialValue)`, but instead compare the pointers
        int index = Util.firstWhere(this.backing, i -> i != this.initialValue, Util.StartLocation.END);

        if (index < minimumNumberOfElements) {
            index = minimumNumberOfElements;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("[");
        for (int i = 0; i <= index; i++) {
            builder.append(String.format("%s, ", this.backing.get(i)));
        }
        builder.append("...]");

        return builder.toString();
    }
}
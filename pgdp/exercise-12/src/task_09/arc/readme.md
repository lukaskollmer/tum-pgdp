# arc

> Automatic Reference Counting

## Terminology

|                  |                                               |
| ---------------- | --------------------------------------------- |
| **retain count** | The current number of references to an object |
| **retain**       | Increase an object's retain count             |
| **release**      | Decrease an object's retain count             |



## Why do we need this?

- When we allocate a new array on the heap, we store a pointer to that array on the stack
- We can now pass this array to another function, which might try to access its elements or might even mutate it
- We could also get the pointer to an array returned by another function
- The problem with all of this is that we need to keep track of the current owner of the array, as well as the number of references to it
- This is important to know when it's safe to remove the array from the heap (aka deallocate it)

## How is this implemented?

1. The `Trackable` interface  
```java
interface Trackable {
    // These are implemented by default
    default void retain();
    default void release();
    default int retainCount();

    // The class adopting `Trackable` is responsible for implementing `dealloc`
    void dealloc();
}
```
Over the course of an object's lifetime, our interpreter calls `retain` and `release`, if necessary:
- When we pass an array to another function, we call `retain` to increase the array's retain count
- When we return from a function and delete the function's arguments and local variables from the stack, we call `release` to decrement the retain count

2. The `ReferencePool`  
The reference pool is an internal class we use to keep track of an object's retain count. This is implemented using a `HashMap`. The reference pool stores a strong reference to the object, which is deleted when the object's retain count reaches 0.

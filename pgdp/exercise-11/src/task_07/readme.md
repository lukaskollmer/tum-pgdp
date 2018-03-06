# task_06 / task_07

> - Formatting ASTs to a textual representation of the code  
> - Adding arrays to MiniJava


## how does the heap work / how do we store arrays?

The `Heap` class has an internal `List<Integer>` which is used as the backing for the arrays stored on the heap.


### Allocating new arrays on the heap
- Allocating a new array of size `n` sets `n + 1` elements on the heap's backing list to `0`, starting right after the previously last array stored on the heap
- The first element of that newly allocated range stores the length of the array
- The `allocate` function returns a `Heap.Slice` object, which acts as a Proxy for accessing a specific sub-range of the heap's backing array
  - The heap slice is aware of it's lower and upper bound (aka the range on the heap's backing array where the slice's data is stored)
  - This heap slice has functions like `int get(int index)`, `int set(int index, int value)` and `int getSize()`  
  Since the heap knows its bounds, it also ensures that we can only access elements stores within its bounds and throws an exception if we're trying to access an element outside the array
  - The heap slice also has a weak reference to its `Heap`

**Sidenote:**
- Since we had to change the structure of the heap to support knowing an array's length, we also don't need the header area at the end of the backing array anymore
- Instead of using that header area to look up each array's start and end index, we can simply look at the backing array's first element to get the length of first array, jump to the beginning of the next array, look at that array's length and so on...
- Since we always delete arrays from the heap when they go out of scope (see below), we are guaranteed to always be able to find the end of the last array on the heap


### Heap + Heap.Slice + Automatic Reference Counting = ♥️
- Heap slices support ARC (by implementing the `Trackable` interface)
- When a heap slice's retain count reaches 0, it automatically deletes itself from the heap

### Deleting heap slices from the heap
1. The elements of the array represented by that heap slice are removed from the heap's backing array
2. The `lowerBound` and `upperBound` properties of all other known slices are updated, if necessary
3. The backing array is filled until it reaches the specified size

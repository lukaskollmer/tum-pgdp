# task_09

> java tokenizer + parser + compiler + interpreter

## Usage

### Syntax
- Basically like Java, with a global `main` function
- Classes are supported, but just at the file's top level (aka no nested classes)
- A valid program consists of:
  - a global `main` function returning `int`
  - other top level functions returning either `int`, an object or an array

<details>
 <summary>More Info about Functions and Classes</summary>
  
### Functions
- Global functions (aka not bound to any scope)
- Instance methods of a class (bound to the classes' scope, get `this` as implicit first argument)
- Every function has to return _something_, if in a function call, the return value isn't assigned to some variable or somehow further used, it's automatically discarded

### Classes
- define a class like you would in java
- no method overloading
- all ivars are defined at the top of the class
- initializers have to call their superclasses' initializer at the beginning of the initializer
- use `super` to call the superclasses' implementation of a method
- all ivars are private, all instance methods are public
</details>


## Implementation
- primarily stack based
- we also have a heap

### control flow

at the beginnnig of the program, we push the address of the `main` function onto the stack and call it, passing 0 arguments.
after the `main` function returns, we jump to the `end` label.  
the `end` label is guaranteed to:
1. always be the last label in the generated code and
2. never contain any instructions

After the program ended, the top value on the stack is the value returned from the `main` function.

```asm
============ [BEGIN] ============
ldi main
call 0

ldi -1
jump end

main:
[MAIN INSTRUCTIONS]

[OTHER FUNCTIONS]

end:
============= [END] =============
```


#### if/while statements

- `if`/`while` statements are implemented using unique labels
- label naming convention: `[function_name]_{if|while}_[counter]_{body|cond}_{main|else}`
- this naming convention ensures that, no matter how many if/while statements our code contains we can always uniquely identify their labels, as well as the function they are part of

**Example 1: if**

```java
if (a < b) {
    [if_body_main]
}
[...]
```

```asm
ldi a
ldi b
lt
jump main_if_00_body_main

ldi -1
jump main_if_00_body_end

main_if_00_body_main:
[if_body_main]

main_if_00_body_end:
[...]
```

<details>
 <summary>More examples (if/else, while)</summary>

**Example 2: if else**

```java
if (a < b) {
    [if_body_main]
} else {
    [if_body_else]
}
[...]
```

```asm
ldi b
ldi a
LT
jump main_if_00_body_main

ldi -1
jump main_if_00_body_else

main_if_00_body_main:
[if_body_main]
ldi -1
jump main_if_00_body_end

main_if_00_body_else:
[if_body_else]
jump main_if_00_body_end

main_if_00_body_end:
[...]
```

**Example 3: while**

```java
while (temp < n) {
    [while_body]
}
[...]

```

```asm
main_while_00_cond:
lds n
lds temp
LT
jump main_while_00_body

ldi -1
jump main_while_00_end

main_while_00_body:
[while_body]

ldi -1
jump main_while_00_cond

main_while_00_end:
[...]
```
</details>







### The Heap
- The heap is essentially just an array storage
- The `Heap` class has an internal `List<Object>` which is used as the backing for the arrays stored on the heap

#### Allocating new arrays on the heap
- Allocating a new array of size `n` sets `n + 1` elements on the heap's backing list to `0`, starting right after the previously last array stored on the heap
- The first element of that newly allocated range stores the length of the array
- The `allocate` function returns a `Heap.Slice` object, which acts as a Proxy for accessing a specific sub-range of the heap's backing array
  - The heap slice is aware of it's lower and upper bound (aka the range on the heap's backing array where the slice's data is stored)
  - This heap slice has functions like `Object get(int index)`, `Object set(int index, Object value)` and `int getSize()`  
  Since the heap knows its bounds, it also ensures that we can only access elements stores within its bounds and throws an exception if we're trying to access an element outside the array
  - The heap slice also has a weak reference to its `Heap`

**Sidenote:**
- Since we had to change the structure of the heap to support knowing an array's length, we also don't need the header area at the end of the backing array anymore
- Instead of using that header area to look up each array's start and end index, we can simply look at the backing array's first element to get the length of first array, jump to the beginning of the next array, look at that array's length and so on...
- Since we always delete arrays from the heap when they go out of scope (see below), we are guaranteed to always be able to find the end of the last array on the heap


#### Heap + Heap.Slice + Automatic Reference Counting = ♥️
- Heap slices support ARC (by implementing the `Trackable` interface)
- When a heap slice's retain count reaches 0, it automatically deletes itself from the heap

#### Deleting heap slices from the heap
1. The elements of the array represented by that heap slice are removed from the heap's backing array
2. The `lowerBound` and `upperBound` properties of all other known slices are updated, if necessary
3. The backing array is filled until it reaches the specified size


### Classes
- All objects are references to arrays allocated on the heap
- An object's first element is its vtable (an array containing the addresses of the object's methods)

#### Object Initialization
- When a new object is created (via the `new` keyword), we allocate two arrays on the heap:
  - the object itself (the size of that array is the number of the object's ivars + 1)
  - the object's vtable, which we then set as the object's first element
- For this, we use the [`alloc`](compiler/CodeGenerationVisitor.java#L228) function
  - `alloc` takes two parameters (the size of the object and the size of the object's vtable) and returns the uninitialized object
  ```java
  // this is basically what the alloc function does
  int[] alloc(int objectSize, int vtableSize) {
      int[] object = new int[objectSize];
      int[] vtable = new int[vtableSize];
      object[0] = vtable;
      return object;
  }
  ```

**Example:**
```java
// When you're initializing a new object like this
XXX obj;
obj = new XXX();

// it essentially gets turned into this
XXX obj;
XXX_init(alloc(sizeof(XXX), sizeof_vtable(XXX)));
```

#### ivar access
- accessing a variable outside the current scope is interpreted as an ivar access
- all ivars are stored in the object (which essentially is just an array), at different offsets

#### instance methods
- classes can override methods of their superclass
- the addresses of a classes' instance methods are stored in the classes' vtable. the exact values are assigned at compile time
- when a class overrides methods from its superclass, we override that method's entry in the vtable
- explicit method invocations of the superclasses' implementation (ie `super.foo()`) works via typechecking: we know an object's type at compile time, as well as its superclass
- we keep track of the types of all variables in the current scope and perform some checks to make sure that an object does actually respond to a selector

#### classes + arc
- since instances of classes are essentially just arrays allocated on the heap, they get arc



## Licence

(c) 2018 Lukas Kollmer

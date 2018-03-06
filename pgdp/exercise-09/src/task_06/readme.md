(PSA this was initially my brainstorming file but i guess it's kinda useful to understand how the code works and why it works the way it does)

# task_06

- there are some unit tests in task_06_test.java


## how does this work?

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












## brainstorming
TODO DELETE EVERYTHING BELOW BEFORE UPLOADING ON MOODLE

### function calls
- encode the expected number of arguments into the label generated from the function name (this would also allow function overloading)
- implement forward declarations by always pushing the dest label onto the stack?
- somehow find out how many times we push onto the stack and allocate the correct amount of stack frames in advance


### variables
- we don't have registers
- therefore, we somehow need to keep track of our variables
- we need to know which variable is where on the stack at what point in time
- do we need to `alloc` space for variables declared at the beginning of the function? (probably, right?)
- for this to work, we might need to know how individual operations modify the stack (eg # delta)

idea:
- keep everything on the stack/frame/whatever all the time and just move them to the front when they're needed
- todo some other fancy ideas


### assignments
```
[res] = [exp]
x = y [OP] z
```
- we need to work w/ three variables
1. execute the expression
2. move the top value on the stack (the result of the expression) onto the frame
(maybe?)


### errors
- [x] duplicate global symbols (we just need to check for function names, if/while labels are names after theire containing function and therefore also covered)
- [ ] duplicate local symbols (function arguments, local variables)
- [x] accessing undefined symbols (functions/variables). this one is easy since the internal visitors are already aware of their scope
- no need to check that variables are assigned a value before reading it since their initial value is 0
- function that doesn't have a return statement? easiest would be a runtime check (which would require introducing additional instructions) (we don't have to handle this one)

### 32 bit constants
- problem: `ldi` throws if the immediate exceeds 16 bits
- solution: split up all number literals > 16 bits, push the sub-parts individually and add them back together
- the stack is 32 bits wide, the bottleneck are the instruction opcodes
- we would split this up in two ways:
  - (the easy one) simply push Short.MAX_VALUE and the rest until that represents the number literal, then add the sufficient number of `add` instructions
  - (the nice one)
    - push the upper 16 bits of the number literal
    - shift them 16 bits to the left
    - push the lower 16 bits
    - `add`
    **update**
    - doing this in 16 bit chunks is idiotic bc leading 1s are interpreted as negative numbers and it only randomly works
    - solution: use 8 bit chunks

### progress
- [x] function calls
- [x] local variable declarations
- [x] if/else
- [x] while
- [x] Unary
- [x] conditions
  - [x] True
  - [x] False
  - [x] Binary
    - [x] And (`&&`)
    - [x] Or (`||`)
  - [x] Comparisons
    - [x] Equals (`==`)
    - [x] Not Equals (`!=`)
    - [x] Less Equal (`<=`)
    - [x] Less (`<`)
    - [x] Greater Equal (`>=`)
    - [x] Greater (`>`)
  - [x] Unary (`!`)
- [x] 32 bit constants




### optimizations
if there is some time/willpower left, there's a bunch of edge cases we could optimize for

1. returning the local variable that also happens to be at the top of the stack

```java
Function main = new Function(
        "main",
        null,
        Arrays.asList(new Declaration("input")),
        Arrays.asList(
                new Read("input"),
                new Return(new Variable("input"))
        )
);
```

```asm
ldi main
call 0

ldi -1
jump end

main:
alloc 1
in
sts 1
lds 1
return 1

end:
```

as you can see, before returning from `main`, we move the local variable we just read to off the stack onto the frame, only to copy it back onto the stack directly afterwards.
this is not necessary

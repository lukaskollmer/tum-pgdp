/*
 * Custom Assembly Interpreter
 *
 * USAGE
 *   java Main_Task_08 [<filename>|--stdin] [--debug]
 *
 *     <filename>  relative path to a file containing the assembly instructions (should be ASCII-encoded)
 *     --stdin     Set the --stdin flag to read the source code of the script from the console, instead of reading it from a file
 *     --debug     Set the --debug flag to enable verbose logging
 *
 *
 * MORE INFORMATION
 *
 * Assembly code syntax:
 * - all code is case-insensitive
 * - each line consists of a single command (operation), followed by up to one immediate (the value that will be passed to the command)
 * - all integer literals passed as immediates are interpreted in base 10
 * - empty lines are allowed
 * - all lines that consist of a single continuous string of text (no spaces) and end with a colon (':') will be interpreted as labels
 * - all lines starting with a semicolon (';') are interpreted as comments and will be ignored
 * - comments have to start at the beginning of a line, it's illegal to have an instruction and a comment on the same line
 *
 * Instructions
 * - nop, add, sub, mul, mod, ldi, lds, sts, jump, je, jne, jlt, call, return, in, out, halt, alloc
 * - custom instructions
 *   - debug (prints the contents of the stack to stdout)
 *
 *
 * LICENSE
 *
 * MIT @ Lukas Kollmer (lukaskollmer.me)
 *
 * */


import java.io.FileReader;  // Reading the input file
import java.nio.file.*;     // Filepath handling
import java.util.*;         // List, ArrayList, Scanner, Arrays
import java.util.function.Predicate;



public class Main_Task_08 {

    // Boolean determining whether calls to `log` should actually be written to stdout
    private static boolean DEBUG = false;


    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.print("Invalid input. See the source file for usage instructions");
            System.exit(1);
        }

        // set the DEBUG flag, if necessary
        if (args.length > 1) {
            DEBUG = args[1].equalsIgnoreCase("--debug");
        }

        ExecutableProgram program;

        if (args[0].equalsIgnoreCase("--stdin")) {
            List<String> code = Utils.readMultipleLines("Enter the instructions of the program. End input w/ two newlines\n", 2);
            program = new ExecutableProgram(code);
        } else {
            Path filepath = Paths.get(System.getProperty("user.dir"), args[0]);
            program = new ExecutableProgram(filepath.toString());
        }

        try {
            program.compile();
        } catch (ExecutableProgram.CompileException e) {
            e.printStackTrace();
            System.exit(1);
        }

        log("%s\n", program);

        int retval = program.run();

        System.out.format("retval: %s\n", retval);
    }




    /**
     * The `ExecutableProgram` class interprets a program written in our custom assembly language and evaluates it
     * */
    public static class ExecutableProgram {

        static class CompileException extends Exception {
            CompileException(String format, Object... args) {
                super(String.format(format, args));
            }
        }

        // List containing the raw source code of the program, line by line (this is used to resolve labels passed as immediates)
        private final List<String> instructions = new ArrayList<>();

        // the opcodes of the individual operations.
        // The opcode is stored in the upper 16 bits, and the immediate passed to the operation is stored in the lower 16 bits
        private int[] opcodes;

        // The operations parsed from the opcodes
        private final List<Operation> operations = new ArrayList<>();

        // The stack used when running the program
        private final Stack<Integer> stack = new Stack<>(128);

        // Boolean value indicating whether the program has already successfully compiled
        private boolean didCompile = false;

        // Boolean value indicating whether the program is currently running
        private boolean isRunning = false;

        // Instruction Pointer
        int instructionPointer = -1;


        /**
         * Create a new `ExecutableProgram` with the contents of the file at the path
         *
         * This throws an exception if we were unable to find the file
         * */
        ExecutableProgram(String filepath) {
            try {
                Scanner scanner = new Scanner(new FileReader(filepath));

                while (scanner.hasNextLine()) {
                    this.instructions.add(scanner.nextLine());
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

        }


        /**
         * Create a new `ExecutableProgram` from a List<String> containing the individual instructions of the program
         * */
        ExecutableProgram(List<String> code) {
            this.instructions.addAll(code);
        }


        /**
         * Create a new `ExecutableProgram` from an array of integers containing precompiled instructions
         * */
        ExecutableProgram(int[] rawInstructions) {
            this.opcodes = rawInstructions;
            this.didCompile = true;

            for (int rawInstruction : rawInstructions) {
                // Fetch the opcodes and load the operations
                int opcode = rawInstruction >> 16;
                Operation operation = Operation.operationForOpcode(opcode);
                this.operations.add(operation);

                // "disassemble" the raw opcodes and immediates and turn them into instructions
                int immediate = (rawInstruction << 16) >> 16;
                this.instructions.add(String.format("%s %s", operation.getOperationName().toLowerCase(), immediate));
            }
        }

        /**
         * Parse the program's instructions into the corresponding operations
         *
         * This is part one of the compilation phase and should always be invoked before `parseImmediates`
         * */
        private void parseInstructions() throws CompileException {
            this.opcodes = new int[this.instructions.size()];

            // Go through all instructions (the individual lines) and fetch the correct operation and opcode
            for (int i = 0; i < this.instructions.size(); i++) {
                Operation operation = Operation.operationForInstruction(this.instructions.get(i));
                if (operation == null) {
                    throw new CompileException("Unable to process instruction '%s'", this.instructions.get(i));
                }
                this.operations.add(operation);

                // save the instruction's opcode and shift it left by 16 bits
                int opcode = operation.getOpcode();
                this.opcodes[i] = opcode << 16;
            }
        }


        /**
         * Parse the immediates passed to the individual instructions
         *
         * This is part two of the compilation phase and should always be invoked after `parseInstructions`
         * */
        private void parseImmediates() throws CompileException {

            // go through all instructions (the individual lines), check whether they have an immediate and save it if necessary
            for (int i = 0; i < this.instructions.size(); i++) {
                int immediate = 0;
                String[] splitInstruction = this.instructions.get(i).split(" ");

                // skip the current instruction if
                // a) it doesn't have an immediate, or
                // b) it's a comment
                if (splitInstruction.length < 2 || splitInstruction[0].startsWith(";")) {
                    continue;
                }

                // we first try to interpret the immediate as an integer literal
                // if that throws an exception, we interpret it as a label
                try {
                    immediate = Integer.valueOf(splitInstruction[1]);
                } catch (Exception e) {
                    // the immediate is a label
                    // we now loop over the entire code and check which line equals `{immediate_string_value}:`
                    // if we found one, we replace the immediate w/ the number of that line (ie the address of the label specified by the immediate)
                    boolean didFindLabel = false;
                    for (int ip = 0; ip < this.instructions.size(); ip++) { // ip: instructionPointer
                        if (this.instructions.get(ip).equalsIgnoreCase(String.format("%s:", splitInstruction[1]))) {
                            immediate = ip;
                            didFindLabel = true;
                            break;
                        }
                    }

                    // we didn't find a label to jump to
                    if (!didFindLabel) {
                        throw new CompileException("Unable to find label '%s'", splitInstruction[1]);
                    }
                }

                // make sure the immediate fits in the lower 16 bits of the opcode
                // we check this by casting it down to a short (which is 16 bit wide) and comparing that w/ the actual value
                if ((short)immediate != immediate) {
                    throw new CompileException("Immediate at line %s exceeds 16 bit limit", i);
                }

                // set the upper 16 bits to 0 to avoid UB when dealing with negative numbers passed as immediate
                for (int j = 31; j >= 16; j--) {
                    immediate &= ~(1 << j);
                }

                // set the immediate as the lower 16 bits of the opcode
                // this works because we previously shifted the opcode 16 bits to the left
                this.opcodes[i] |= immediate;
            }
        }

        /**
         * Compile the program
         *
         * This is a two step operation:
         *   - we first parse the instructions into the corresponding opcodes
         *   - then we parse the immediates and set them on the lower 16 bits of the cached opcodes
         *
         *   This method throws an exception if something went wrong
         * */
        void compile() throws CompileException {
            if (didCompile) {
                // programs are immutable, there's no point in compiling it more than once
                return;
            }

            parseInstructions();
            parseImmediates();

            didCompile = true;
        }


        /**
         * Evaluate the program
         *
         * Returns the first value on the stack, or INT_MIN if the stack is empty
         * */
        int run() {
            if (!didCompile) {
                throw new RuntimeException("Can't run non-compiled code");
            }

            if (isRunning) {
                throw new RuntimeException("The program is already running");
            }

            // Prepare execution
            // This resets the stack and the instruction pointer, and sets the `isRunning` flag
            this.stack.reset();
            this.isRunning = true;
            this.instructionPointer = 0;


            // evaluate the next instruction until
            // a) we reach the end of the program
            // b) the instruction pointer has been set to -1 (this is done by the HALT instruction)
            while (instructionPointer < this.instructions.size() && instructionPointer != -1) {
                log("\n\n==========\n");

                // We first laod the operation for the instruction at the instruction pointer
                Operation operation = this.operations.get(instructionPointer);

                // Now, we load the immediate for the current instruction
                int immediate = this.getImmediate(instructionPointer);

                log("[%s] immediate: %s\n", operation.getOperationName(), immediate);

                // cache the current value of the instruction pointer (before the operation was evaluated)
                int previousInstructionPointer = instructionPointer;

                log("(%s) stack: %s\n", instructionPointer, this.stack);
                log("%s\n", this.instructions.get(instructionPointer));
                // Invoke the operation
                // we pass the immediate and the program
                operation.invoke(immediate, this);
                log("(%s) stack: %s\n", instructionPointer, this.stack);

                // check whether the operation mutated the instruction pointer
                // if it didn't, increment it by one to jump to the next line
                if (instructionPointer == previousInstructionPointer) {
                    instructionPointer++;
                }

            }

            isRunning = false;

            // check whether there are still elements on the stack and return the first value, if possible
            if (!stack.isEmpty()) {
                return stack.pop();
            }
            return Integer.MIN_VALUE;
        }


        /**
         * Get the immediate of the instruction at `index`
         * */
        int getImmediate(int index) {
            // In order to get the correct value, we need to zero out the upper 16 bits:
            // 1. remove the first 16 bits (the opcode) by shifting the immediate left by 16 bits
            // 2. move the immediate value back to the lower bits
            return (this.opcodes[index] << 16) >> 16;
        }


        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<ExecutableProgram instructions=(\n");

            for (int i = 0; i < this.instructions.size(); i++) {
                int opcode = this.opcodes[i];
                String source = this.instructions.get(i);

                builder.append(String.format("  (%03d) [%02d] %s\n", i, opcode >> 16, source));
            }
            builder.append(")>");

            return builder.toString();
        }
    }



    /**
     * Operation is an abstract class that the implementations of the different operations (add, alloc, etc) subclass
     * Use the `Operation.operationForInstruction(_)` method to get the operation for a specific instruction
     * You should not use any of the OP_XXX subclasses directly
     * */
    private static abstract class Operation {
        // Hash Map mapping the opcodes to their respective operation classes
        private static HashMap<Integer, Class> operationsByOpcode = new HashMap<>();

        // Returns an operation's opcode
        abstract int getOpcode();

        // Invoke the operation
        abstract void invoke(int immediate, ExecutableProgram program);

        String getOperationName() {
            return this.getClass().getName().split("_")[3];
        }

        /**
         * Dynamically load the `Operation` subclass for an instruction
         *
         * How does this work?
         * If the instruction's length is 0 (empty string), if it starts w/ a semicolon (comment) or if it ends w/ a colon (a label), we return the noop operation (OP_NOP)
         * In all other cases, we split the instruction by ' ', fetch the first match (the instruction name) and lookup the class w/ the name `OP_{UPPERCASE_INSTRUCTION_NAME}`
         * If a class w/ that name doesn't exist, we simply return null
         *
         * For example, the instruction 'alloc 5' would be split into ['alloc', '5']
         * We then fetch the first element ('alloc'), make it uppercase and load the corresponding `Operation` subclass
         * 'alloc 5' would return an instance of `OP_ALLOC`
         *
         * */
        static Operation operationForInstruction(String instruction) {
            boolean is_noop = instruction.isEmpty() || instruction.startsWith(";") || instruction.matches(".*:");

            if (is_noop) {
                return new OP_NOP();
            }

            String[] split = instruction.split(" ");
            if (split.length > 0) {
                String instructionName = split[0].toUpperCase();
                try {
                    Class cls = Class.forName(String.format("Main_Task_08$OP_%s", instructionName));
                    Operation op = (Operation) cls.newInstance();
                    Operation.operationsByOpcode.put(op.getOpcode(), cls);
                    return (Operation) cls.newInstance();
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    // pass
                }
            }

            return null;
        }


        /**
         * Get an operation from its opcode
         *
         * Note: This only works when the operation has already been loaded via `Operation.operationForInstruction`
         * */
        static Operation operationForOpcode(int opcode) {
            if (opcode == 0) {
                return new OP_NOP();
            }

            try {
                return (Operation) Operation.operationsByOpcode.get(opcode).newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }




    /**
     * NOP operation
     *
     * Doesn't do anything. Any immediates passed are ignored
     * */
    @SuppressWarnings("unused")
    static class OP_NOP extends Operation {
        @Override
        int getOpcode() {
            return 0;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) { }
    }


    /**
     * ADD operation
     *
     * Pops two values from the stack, adds them and pushes the result back onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_ADD extends Operation {
        @Override
        int getOpcode() {
            return 1;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            program.stack.push(a + b);
        }
    }


    /**
     * SUB operation
     *
     * Pops two values from the stack, subtracts them and pushes the result back onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_SUB extends Operation {
        @Override
        int getOpcode() {
            return 2;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            program.stack.push(a - b);
        }
    }


    /**
     * MUL operation
     *
     * Pops two values from the stack, multiplies them and pushes the result back onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_MUL extends Operation {
        @Override
        int getOpcode() {
            return 3;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            program.stack.push(a * b);
        }
    }


    /**
     * MOD operation
     *
     * Pops two values from the stack, calculates the modulo and pushes the result back onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_MOD extends Operation {
        @Override
        int getOpcode() {
            return 4;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            program.stack.push(a % b);
        }
    }


    /**
     * LDI operation
     *
     * Pushes the value in the immediate onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_LDI extends Operation {
        @Override
        int getOpcode() {
            return 5;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            immediate <<= 16;
            immediate >>= 16;
            program.stack.push(immediate);
        }
    }


    /**
     * LDS operation
     *
     * Copies the value in the frame at the index specified by the immediate onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_LDS extends Operation {
        @Override
        int getOpcode() {
            return 6;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int value = program.stack.getFrameElement(immediate);
            program.stack.push(value);
        }
    }


    /**
     * STS operation
     *
     * Pops a value from the stack and puts it into the frame, at the index specified by the immediate
     * */
    @SuppressWarnings("unused")
    static class OP_STS extends Operation {
        @Override
        int getOpcode() {
            return 7;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int value = program.stack.pop();
            program.stack.pushFrame(immediate, value);
        }
    }


    /**
     * JUMP operation
     *
     * Moves the instruction pointer to the address specified by the immediate
     * */
    @SuppressWarnings("unused")
    static class OP_JUMP extends Operation {
        @Override
        int getOpcode() {
            return 8;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            program.instructionPointer = immediate;
        }
    }


    /**
     * JE operation
     *
     * Pops two values from the stack, compares them and - if they are equal - jumps to the address specified by the immediate
     * */
    @SuppressWarnings("unused")
    static class OP_JE extends Operation {
        @Override
        int getOpcode() {
            return 9;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            if (a == b) {
                program.instructionPointer = immediate;
            }
        }
    }


    /**
     * JNE operation
     *
     * Pops two values from the stack, compares them and - if they are not equal - jumps to the address specified by the immediate
     * */
    @SuppressWarnings("unused")
    static class OP_JNE extends Operation {
        @Override
        int getOpcode() {
            return 10;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            if (a != b) {
                program.instructionPointer = immediate;
            }
        }
    }


    /**
     * JE operation
     *
     * Pops two values from the stack, compares them and - if a is smaller than b - jumps to the address specified by the immediate
     * */
    @SuppressWarnings("unused")
    static class OP_JLT extends Operation {
        @Override
        int getOpcode() {
            return 11;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int a = program.stack.pop();
            int b = program.stack.pop();

            if (a < b) {
                program.instructionPointer = immediate;
            }
        }
    }


    /**
     * IN operation
     *
     * Requests user input and pushes that value onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_IN extends Operation {
        @Override
        int getOpcode() {
            return 12;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            String prompt = "\"[IN] enter a number that will be pushed onto the stack:";
            //int value = Integer.parseInt(Utils.readSingleLine(prompt));
            int value = MiniJava.read(prompt);
            program.stack.push(value);
        }
    }


    /**
     * OUT operation
     *
     * Pops a value from the stack and prints it to stdout
     * */
    @SuppressWarnings("unused")
    static class OP_OUT extends Operation {
        @Override
        int getOpcode() {
            return 13;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int value = program.stack.pop();
            MiniJava.write(String.format("[OUT] %s\n", value));
            //System.out.format("[OUT] %s\n", value);
        }
    }


    /**
     * CALL operation
     *
     * 1. Pops the destination address from the stack
     * 2. Pops the arguments that are going to be passed to the called function from the stack (#arguments specified via the immediate)
     * 3. Pushes the current frame pointer onto the stack
     * 4. Pushes the current instruction pointer (advanced by 1) onto the stack (the return address)
     * 5. Pushes the arguments onto the stack
     * 6. Updates the instruction pointer to the address of the called function
     * 7. Updates the frame pointer to the current stack pointer
     * */
    @SuppressWarnings("unused")
    static class OP_CALL extends Operation {
        @Override
        int getOpcode() {
            return 14;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int destinationInstructionPointer = program.stack.pop();

            int[] args = new int[immediate];
            for (int i = 0; i < immediate; i++) {
                // fetch the arguments off the stack
                args[i] = program.stack.pop();
            }

            program.stack.push(program.stack.framePointer);
            program.stack.push(program.instructionPointer + 1);

            for (int arg : Utils.reversed(args)) {
                program.stack.push(arg);
            }

            program.stack.framePointer = program.stack.stackPointer;
            program.instructionPointer = destinationInstructionPointer;
        }
    }


    /**
     * RETURN operation
     *
     * 1. pops the return value from the stack
     * 2. pops the local variables from the stack (#variables set via the immediate)
     * 3. pops the previous instruction pointer from the stack and restores it
     * 4. pops the previous frame pointer from the stack and restores it
     * 5. pushes the return value back onto the stack
     * */
    @SuppressWarnings("unused")
    static class OP_RETURN extends Operation {
        @Override
        int getOpcode() {
            return 15;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            int retval = program.stack.pop();

            for (int i = 0; i < immediate; i++) {
                program.stack.pop();
            }

            program.instructionPointer = program.stack.pop();
            program.stack.framePointer = program.stack.pop();

            program.stack.push(retval);
        }
    }


    /**
     * HALT operation
     *
     * stops program execution by setting the instruction pointer to -1 (soft exit)
     * */
    @SuppressWarnings("unused")
    static class OP_HALT extends Operation {
        @Override
        int getOpcode() {
            return 16;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            program.instructionPointer = -1;
        }
    }


    /**
     * ALLOC operation
     *
     * Reserves space on the stack for local variables
     * */
    @SuppressWarnings("unused")
    static class OP_ALLOC extends Operation {
        @Override
        int getOpcode() {
            return 17;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            for (int i = 0; i < immediate; i++) {
                program.stack.push(0);
            }
        }
    }


    /**
     * DEBUG operation
     *
     * custom operation that prints the current contents of the stack (including stack- and framepointer) to stdout
     * */
    @SuppressWarnings("unused")
    static class OP_DEBUG extends Operation {
        @Override
        int getOpcode() {
            return 99;
        }

        @Override
        void invoke(int immediate, ExecutableProgram program) {
            System.out.format("[DEBUG] stack: %s\n", program.stack);
        }
    }


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
    static class Stack<T> {
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
            int index = Utils.firstWhere(this.backing, i -> i != this.initialValue, Utils.StartLocation.END);

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


    /**
     * Utils
     * */
    private static class Utils {

        // Reverse an integer array
        static int[] reversed(int[] input) {
            int[] reversed = new int[input.length];

            for (int i = 0; i < input.length; i++) {
                reversed[input.length - i - 1] = input[i];
            }

            return reversed;
        }


        // read a single line from stdin
        static String readSingleLine(String prompt) {
            System.out.print(prompt);
            return new Scanner(System.in).nextLine();
        }


        // Read multiple lines from stdin
        static List<String> readMultipleLines(String prompt, int numberOfEmptyLinesRequiredToReturn) {
            System.out.print(prompt);

            List<String> input = new ArrayList<>();
            Scanner scanner = new Scanner(System.in);

            int numberOfEmptyLines = 0;

            while (numberOfEmptyLines < numberOfEmptyLinesRequiredToReturn) {
                String nextLine = scanner.nextLine();
                input.add(nextLine);

                numberOfEmptyLines += nextLine.length() == 0 ? 1 : 0;
            }

            return input;
        }


        enum StartLocation { BEGINNING, END }

        // Get the index of the first element in the list where `predicate` evaluates to true
        static <T> int firstWhere(List<T> collection, Predicate<T> predicate, StartLocation startLocation) {
            int start = startLocation == StartLocation.BEGINNING ? 0 : collection.size() - 1;
            int end   = startLocation == StartLocation.BEGINNING ? collection.size() : 0;

            int i = start;
            while (i != end) {
                if (predicate.test(collection.get(i))) {
                    return i;
                }

                if (startLocation == StartLocation.BEGINNING) {
                    i++;
                } else {
                    i--;
                }
            }

            return -1;
        }
    }


    // log to stdout if the debug flag is set
    static void log(String format, Object... args) {
        if (DEBUG) {
            System.out.format(format, args);
        }
    }


    /**
     * Sole purpose of this class is to provide the interface expected by the unit test
     * */
    static class Interpreter {

        static int[] parse(String sourceCode) {
            List<String> instructions = Arrays.asList(sourceCode.split("\n"));

            ExecutableProgram program = new ExecutableProgram(instructions);

            try {
                program.compile();
            } catch (ExecutableProgram.CompileException e) {
                e.printStackTrace();
                System.exit(1);
            }

            return program.opcodes;
        }

        static int execute(int[] rawInstructions) {
            ExecutableProgram program = new ExecutableProgram(rawInstructions);

            return program.run();
        }
    }
}

package task_09.interpreter;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static task_09.Util.log;

/**
 * The `ExecutableProgram` class interprets a program written in our custom assembly language and evaluates it
 * */
public class ExecutableProgram {

    public static class CompileException extends Exception {
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
    protected Stack<Object> stack = new Stack<>(128);

    // The Heap used when running the program
    protected Heap heap = new Heap(128);

    // Boolean value indicating whether the program has already successfully compiled
    private boolean didCompile = false;

    // Boolean value indicating whether the program is currently running
    private boolean isRunning = false;

    // Instruction Pointer
    protected int instructionPointer = -1;


    /**
     * Create a new `ExecutableProgram` with the contents of the file at the path
     *
     * This throws an exception if we were unable to find the file
     * */
    public ExecutableProgram(String filepath) {
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
    public ExecutableProgram(List<String> code) {
        this.instructions.addAll(code);
    }


    /**
     * Create a new `ExecutableProgram` from an array of integers containing precompiled instructions
     * */
    public ExecutableProgram(int[] rawInstructions) {
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
     * Adjust the stack size
     *
     * Only call this method before running the program!
     * */
    public void setStackSize(int size) {
        this.stack = new Stack<>(size);
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
    public void compile() throws CompileException {
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
    public int run() {
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
            return (Integer)stack.pop();
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

    /**
     * Get the opcodes of the program
     * This requires the program already being compiled
     * */
    public int[] getOpcodes() {
        if (!didCompile) {
            throw new RuntimeException("Can't get opcodes of uncompiled program");
        }
        return opcodes;
    }

    /**
     * Get the program's heap
     * */
    public Heap getHeap() {
        return heap;
    }

    public String toString_stack() {
        return this.stack.toString();
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

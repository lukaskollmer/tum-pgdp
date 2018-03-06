package task_06.interpreter;

import java.util.*;
import java.lang.annotation.*;

/**
 * Operation is an abstract class that the implementations of the different operations (add, alloc, etc) subclass
 * Use the `Operation.operationForInstruction(_)` method to get the operation for a specific instruction
 * You should not use any of the OP_XXX subclasses directly
 * */
public abstract class Operation {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    protected @interface Opcode {
        int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    protected @interface HasImmediate {
        boolean value();
    }


    // Hash Map mapping the opcodes to their respective operation classes
    private static HashMap<Integer, Class> operationsByOpcode = new HashMap<>();

    // Returns an operation's opcode
    int getOpcode() {
        return this.getClass().getAnnotation(Opcode.class).value();
    }

    // Returns whether the operations expects an immediate
    boolean hasImmediate() {
        return this.getClass().getAnnotation(HasImmediate.class).value();
    }

    // Invoke the operation
    abstract void invoke(int immediate, ExecutableProgram program);

    String getOperationName() {
        return this.getClass().getName().split("_")[2];
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
                Class cls = Class.forName(String.format("task_06.interpreter.OP_%s", instructionName));
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

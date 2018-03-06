package task_06.interpreter;

import task_06.Util;

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
@Operation.Opcode(14)
@Operation.HasImmediate(true)
public class OP_CALL extends Operation {

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

        for (int arg : Util.reversed(args)) {
            program.stack.push(arg);
        }

        program.stack.framePointer = program.stack.stackPointer;
        program.instructionPointer = destinationInstructionPointer;
    }
}

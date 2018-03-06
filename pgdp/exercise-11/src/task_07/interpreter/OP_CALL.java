package task_07.interpreter;

import task_07.Util;
import task_07.arc.Trackable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        int destinationInstructionPointer = program.stack.pop().element1;

        List<StackElement> args = new ArrayList<>();

        for (int i = 0; i < immediate; i++) {
            args.add(program.stack.pop());
        }

        program.stack.push(StackElement.withElement1(program.stack.framePointer));
        program.stack.push(StackElement.withElement1(program.instructionPointer + 1));

        Collections.reverse(args);

        for (StackElement arg : args) {
            program.stack.push(arg);

            if (arg.element2 instanceof Trackable) {
                ((Trackable)arg.element2).retain();
            }
        }

        program.stack.framePointer = program.stack.stackPointer;
        program.instructionPointer = destinationInstructionPointer;
    }
}

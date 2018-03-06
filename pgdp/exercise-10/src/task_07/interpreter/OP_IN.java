package task_07.interpreter;

import task_07.Util;

/**
 * IN operation
 *
 * Requests user input and pushes that value onto the stack
 * */
@SuppressWarnings("unused")
@Operation.Opcode(12)
@Operation.HasImmediate(false)
public class OP_IN extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        String prompt = "[IN] enter a number that will be pushed onto the stack: ";
        int value = Integer.parseInt(Util.readSingleLine(prompt));
        program.stack.push(value);
    }
}

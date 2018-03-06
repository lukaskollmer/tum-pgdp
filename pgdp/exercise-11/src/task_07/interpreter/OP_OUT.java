package task_07.interpreter;

import task_07.Util;

/**
 * OUT operation
 *
 * Pops a value from the stack and prints it to stdout
 * */
@SuppressWarnings("unused")
@Operation.Opcode(13)
@Operation.HasImmediate(false)
public class OP_OUT extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) {
        StackElement element = program.stack.pop();
        Object value = Util.nullCoalescing(element.element1, element.element2);

        System.out.format("[OUT] %s\n", value);
    }
}

package task_06.interpreter;

/**
 * NOP operation
 *
 * Doesn't do anything. Any immediates passed are ignored
 * */
@SuppressWarnings("unused")
@Operation.Opcode(0)
@Operation.HasImmediate(false)
public class OP_NOP extends Operation {

    @Override
    void invoke(int immediate, ExecutableProgram program) { }
}

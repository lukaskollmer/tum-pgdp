
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.util.*;

public class InterpreterTest {

    @Test
    void asm_test_empty_instructions() {
        String[] code = {};

        Assertions.assertEquals(Integer.MIN_VALUE, run(code));
    }

    @Test
    void asm_test_disassemble() {
        String code = "" +
                "ldi 1\n" +
                "ldi 1\n" +
                "add\n";

        //int[] instructions = Main_Task_08.Interpreter.parse(code);

        run(code);
    }

    @Test
    void asm_test_dont_execute_comment() {
        String[] code = {
                "ldi 5",
                ";ldi 7"
        };

        Assertions.assertEquals(5, run(code));
    }

    @Test
    void asm_test_label_resolution() {
        String[] code = {
                "ldi 0",
                "dest:",
                "debug",
                "ldi 1",
                "add",
                "jump dest"
        };

        Main_Task_08.ExecutableProgram program = new Main_Task_08.ExecutableProgram(Arrays.asList(code));

        try {
            program.compile();
        } catch (Main_Task_08.ExecutableProgram.CompileException e) {
            Assertions.fail(e);
        }

        Assertions.assertEquals(1, program.getImmediate(5));
    }

    @Test
    void asm_test_illegal_access() {
        String[] code = {
                "ldi 5",
                "add"
        };

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> run(code));
    }


    @Test
    void asm_test_mixed_upper_and_lower_case() {
        String[] code = {
                "LDI 12",
                "ldi 15",
                "debug",
                "aDD"
        };

        Assertions.assertEquals(27, run(code)); // INT_MIN bc the stack should be empty
    }

    @Test
    void asm_test_addition() {
        String[] code = {
                "ldi 5",
                "ldi 10",
                "add"
        };

        Assertions.assertEquals(15, run(code));
    }

    @Test
    void asm_test_subtraction() {
        String[] code = {
                "ldi 7",
                "ldi 14",
                "sub"
        };

        Assertions.assertEquals(7, run(code));
    }

    @Test
    void asm_test_subtraction_negative() {
        String[] code = {
                "ldi 56",
                "ldi 14",
                "sub"
        };

        Assertions.assertEquals(-42, run(code));
    }

    @Test
    void asm_test_multiplication() {
        String[] code = {
                "ldi 7",
                "ldi 6",
                "mul"
        };

        Assertions.assertEquals(42, run(code));
    }

    @Test
    void asm_test_multiplication_negative() {
        String[] code = {
                "ldi -5",
                "ldi 100",
                "mul"
        };

        Assertions.assertEquals(-500, run(code));
    }

    @Test
    void asm_test_modulo() {
        String[] code = {
                "ldi 6",
                "ldi 10",
                "mod"
        };

        Assertions.assertEquals(4, run(code));
    }

    @Test
    void asm_test_multiple_operations_add_mul_mod() {
        // equals (7 * 6) % 25
        String[] code = {
                "ldi 25",
                "ldi 7",
                "ldi 6",
                "mul",
                "mod"
        };

        Assertions.assertEquals(17, run(code));
    }

    @Test
    void asm_test_jump() {
        String[] code = {
                "ldi 1",
                "jump end",
                "ldi 5",
                "mul",
                "end:"
        };

        Assertions.assertEquals(1, run(code));
    }

    @Test
    void asm_test_je() {
        String[] code = {
                "ldi 12",
                "ldi 6",
                "ldi 2",
                "mul",
                "je end",
                "ldi 1",
                "end:",
        };

        Assertions.assertEquals(Integer.MIN_VALUE, run(code)); // INT_MIN bc the stack should be empty
    }


    @Test
    void asm_test_jne() {
        String[] code = {
                "ldi 11",
                "ldi 6",
                "ldi 2",
                "mul",
                "jne end",
                "ldi 1",
                "end:",
        };

        Assertions.assertEquals(Integer.MIN_VALUE, run(code)); // INT_MIN bc the stack should be empty
    }

    @Test
    void asm_test_jlt() {
        String[] code = {
                "ldi 15",
                "ldi 6",
                "ldi 2",
                "mul",
                "jlt end",
                "ldi 1",
                "end:",
        };

        Assertions.assertEquals(Integer.MIN_VALUE, run(code)); // INT_MIN bc the stack should be empty
    }


    @Test
    void asm_test_halt() {
        String[] code = {
                "ldi 1",
                "halt",
                "ldi 2"
        };

        Assertions.assertEquals(1, run(code));
    }

    @Test
    void asm_test_call() {

        String template =
                "jump start\n" + // jump to the start

                "inc:\n" +       // entry point of the inc function
                "lds 0\n" +      // fetch the argument from the frame and copy it onto the stack
                "ldi 1\n" +      // push a 1 onto the stack
                "add\n" +        // add
                "return 1\n" +   // return one variable (the value on top the stack

                "start:\n" +
                "ldi INPUT\n" +  // this is the input
                "ldi inc\n" +    // push the address of the inc function onto the stack
                "call 1";        // call inc and pass 1 argument (the 7)

        for (int i = -10; i < 100; i++) {
            String code = template.replace("INPUT", String.format("%s", i));
            Assertions.assertEquals(i + 1, run(code));
        }

    }


    @Test
    void asm_test_too_large_immediate() {
        String[] code = {
                "ldi 5",
                "ldi 327670",
                "add"
        };

        Main_Task_08.ExecutableProgram program = new Main_Task_08.ExecutableProgram(Arrays.asList(code));

        Assertions.assertThrows(Main_Task_08.ExecutableProgram.CompileException.class, program::compile);

    }



    @Test
    public void asm_test_example_greatest_common_divisor() {
        String textProgram =
                "LDI 3528\n" +
                        "LDI 3780\n" +
                        "LDI ggt\n" +
                        "CALL 2\n" +
                        "HALT\n" +
                        "\n" +
                        "ggt:\n" + // a bei -1, b bei 0
                        "ALLOC 1\n" +
                        "LDS -1\n" +
                        "LDS 0\n" +
                        "JLT loop\n" +
                        "LDS 0\n" +
                        "LDS -1\n" +
                        "STS 0\n" +
                        "STS -1\n" +
                        "loop:\n" +
                        "LDS 0\n" +
                        "STS 1\n" +
                        "LDS 0\n" +
                        "LDS -1\n" +
                        "MOD\n" +
                        "STS 0\n" +
                        "LDS 1\n" +
                        "STS -1\n" +
                        "LDS 0\n" +
                        "LDI 0\n" +
                        "JNE loop\n" +
                        "LDS -1\n" +
                        "RETURN 3\n";
        int[] program = Main_Task_08.Interpreter.parse(textProgram);
        int retVal = Main_Task_08.Interpreter.execute(program);
        Assertions.assertEquals(252, retVal);
    }

    @Test
    public void asm_test_factorial() {
        String textProgram =
                "LDI 6\n" +
                        "LDI fak\n" +
                        "CALL 1\n" +
                        "HALT\n" +
                        "\n" +
                        "fak:\n" +
                        "ALLOC 1\n" +
                        "LDI 1\n" +
                        "STS 1\n" +
                        "LDS 0\n" +
                        "LDI 1\n" +
                        "JE end\n" +
                        "LDI 1\n" +
                        "LDS 0\n" +
                        "SUB\n" +
                        "LDI fak\n" +
                        "CALL 1\n" +
                        "LDS 0\n" +
                        "MUL\n" +
                        "STS 1\n" +
                        "end:\n" +
                        "LDS 1\n" +
                        "RETURN 2";
        int[] program = Main_Task_08.Interpreter.parse(textProgram);
        int retVal = Main_Task_08.Interpreter.execute(program);
        Assertions.assertEquals(720, retVal);
    }


    int run(String[] code) {
        Main_Task_08.ExecutableProgram program = new Main_Task_08.ExecutableProgram(Arrays.asList(code));

        try {
            program.compile();
        } catch (Main_Task_08.ExecutableProgram.CompileException e) {
            Assertions.fail(e);
        }

        return program.run();
    }


    int run(String code) {
        int[] program = Main_Task_08.Interpreter.parse(code);
        return Main_Task_08.Interpreter.execute(program);
    }

}

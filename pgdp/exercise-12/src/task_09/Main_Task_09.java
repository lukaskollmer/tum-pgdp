package task_09;

import task_09.ast.Program;
import task_09.ast.formatter.Formatter;
import task_09.compiler.Compiler;
import task_09.interpreter.ExecutableProgram;

public class Main_Task_09 {
    public static void main(String... args) throws Exception {
        //Util.DEBUG = true;
        //Parser.DEBUG = true;


        String filepath = System.getProperty("user.dir") + "/src/task_09/test.txt";
        String code = Util.readFile(filepath);


        //run_some_code(code);
        format_some_code(code);
    }



    static void format_some_code(String code) {

        //System.out.format("code: %s\n", code);



        System.out.format("\n\n===== formatted =====\n");

        Program program = new Compiler(code).parseToAST();

        System.out.format("%s", Formatter.format(program));
        System.out.format("===== formatted =====\n");
    }

    static void run_some_code(String code) throws Exception {

        Compiler compiler = new Compiler(code);
        ExecutableProgram executableProgram = compiler.compileToExecutableProgram();

        System.out.format("%s\n", executableProgram);

        //Util.DEBUG = true;

        System.out.format("RETVAL: %s\n", executableProgram.run());

        System.out.format("#heap: %s\n", executableProgram.getHeap().getNumberOfElements());
    }
}

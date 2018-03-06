package task_07.compiler;

import java.util.ArrayList;
import java.util.List;

import static task_07.Util.f;

public class Optimizer {

    /**
     * Optimize a program by turning tail rec calls into jumps to retain the previous stack frame
     * */
    public static List<String> optimizeInstructions(List<String> instructions) {

        List<String> newInstructions = new ArrayList<>();

        String currentFunction_name = null;
        int currentFunction_address = -1;
        int currentFunction_numberOfLocalVariables = 0;

        for (int i = 0; i < instructions.size(); i++) {
            String instruction = instructions.get(i);

            newInstructions.add(instruction);

            // this works bc function names aren't allowed to contain underscores
            boolean isAFunctionEntryPointLabel =
                    instruction.endsWith(":") &&
                            !instruction.contains("_if") &&
                            !instruction.contains("_while") &&
                            !instruction.equals("end:");

            if (isAFunctionEntryPointLabel) {
                currentFunction_name = instruction.replace(":", "");
                currentFunction_address = i;

                String nextInstruction = instructions.get(i + 1);
                if (nextInstruction.startsWith("alloc ")) {
                    currentFunction_numberOfLocalVariables = Integer.parseInt(nextInstruction.replace("alloc ", ""));
                }
            }

            if (instruction.startsWith("return ")) {

                boolean isTailRecursiveCall =
                        instructions.get(i - 1).startsWith("call ") &&
                                instructions.get(i - 2).equals(f("ldi %s", currentFunction_name));

                if (isTailRecursiveCall) {

                    // 0. if necessary: insert a secondary entry point and reset the local variables
                    if (currentFunction_numberOfLocalVariables > 0) {
                        newInstructions.add(currentFunction_address + 2, currentFunction_name + "_entry2:");

                        List<String> instructions_resetLocalVariables = new ArrayList<>();

                        for (int _i = 0; _i < currentFunction_numberOfLocalVariables; _i++) {
                            instructions_resetLocalVariables.add("ldi 0");
                            instructions_resetLocalVariables.add(f("sts %s", _i + 1));
                        }

                        int index = currentFunction_address + 2 + 1;
                        newInstructions.addAll(index, instructions_resetLocalVariables);

                    }

                    // 1. remove the previous last three instructions
                    //    the last three instructions are:
                    //    (1) ldi {FUNCTION_NAME}
                    //    (2) call {#ARGS}
                    //    (3) return {#STACK_FRAME_SIZE}
                    for (int _i = 0; _i < 3; _i++) {
                        newInstructions.remove(newInstructions.size() - 1);
                    }


                    // 2. override the old parameters w/ the new values

                    int numberOfArguments = Integer.parseInt(instructions.get(i - 1).replace("call ", ""));
                    for (int argNr = 0; argNr > -numberOfArguments; argNr--) {
                        newInstructions.add(f("sts %s", argNr));
                    }

                    // 3. add the recursive jump

                    newInstructions.add("ldi -1");
                    String jumpDestinatinon = currentFunction_numberOfLocalVariables > 0 ? currentFunction_name + "_entry2" : currentFunction_name;
                    newInstructions.add(f("jump %s", jumpDestinatinon));
                }
            }
        }

        return newInstructions;
    }
}

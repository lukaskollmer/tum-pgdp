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

        String currentFunction = null;

        for (int i = 0; i < instructions.size(); i++) {
            String instruction = instructions.get(i);

            newInstructions.add(instruction);

            // this works bc function names aren't allowed to contain underscores
            // this also catches the `end:` label but we can safely ignore that since the `end:` label
            // is guaranteed to always be the last instruction
            boolean isAFunctionEntryPointLabel =
                    instruction.endsWith(":") &&
                            !instruction.contains("_if") &&
                            !instruction.contains("_while");

            if (isAFunctionEntryPointLabel) {
                currentFunction = instruction.replace(":", "");
            }

            if (instruction.startsWith("return ")) {

                boolean isTailRecursiveCall =
                        instructions.get(i - 1).startsWith("call ") &&
                                instructions.get(i - 2).equals(f("ldi %s", currentFunction));

                if (isTailRecursiveCall) {
                    // we now need to override the old parameters w/ the new values

                    newInstructions.remove(newInstructions.size() - 1);
                    newInstructions.remove(newInstructions.size() - 1);
                    newInstructions.remove(newInstructions.size() - 1);


                    int numberOfArguments = Integer.parseInt(instructions.get(i - 1).replace("call ", ""));
                    //System.out.format("#args: %s\n", numberOfArguments);

                    for (int argNr = 0; argNr > -numberOfArguments; argNr--) {
                        newInstructions.add(f("sts %s", argNr));
                    }

                    newInstructions.add("ldi -1");
                    newInstructions.add(f("jump %s", currentFunction));
                }
            }
        }

        return newInstructions;
    }
}

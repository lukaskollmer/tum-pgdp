/*
 * exercise-04/task-07
 * String manipulation (2)
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-16
 * status   in-progress
 */

import utils.ArrayUtil;
import utils.Functional;
import utils.StringUtil;

import java.util.ArrayList;

public class Main_Task_07 extends MiniJava {

    // Check if `string` is valid input for this task (only lower or uppercase characters, nothing else)
    static boolean stringIsValid(String string) {
        for (int i = 0; i < string.length(); i++) {
            StringUtil.CharacterCase characterCase = StringUtil.getCase(string.charAt(i));

            if (characterCase != StringUtil.CharacterCase.LOWER && characterCase != StringUtil.CharacterCase.UPPER) {
                return false;
            }
        }

        return true;
    }

    public static void main(String... args) {
        // Ask the user to enter individual words
        // If they entered an invalid string, ask again
        // If they entered nothing, stop asking and move on

        ArrayList<String> input = new ArrayList<>();

        while (true) {
            String newInput = MiniJava.readString("Enter a string. Enter nothing to end input and continue");

            if (!stringIsValid(newInput)) {
                continue;
            }

            if (newInput.equals("")) {
                break;
            } else {
                input.add(newInput);
            }
        }

        if (input.size() == 0) {
            write("You didn't enter any words.");
            System.exit(0);
        }



        String startcased  = StringUtil.capitalizeFirstCharacter(ArrayUtil.join(Functional.map(input, StringUtil::makeLowerCase), ""));
        String uppercased  = ArrayUtil.join(Functional.map(input, StringUtil::makeUpperCase), "");
        String snakecased  = ArrayUtil.join(Functional.map(input, StringUtil::makeLowerCase), "_");
        String pascalcased = ArrayUtil.join(Functional.map(input, s -> StringUtil.capitalizeFirstCharacter(StringUtil.makeLowerCase(s))), "");


        String output = "";
        output += String.format("Startcase:  %s\n", startcased);
        output += String.format("UPPERCASE:  %s\n", uppercased);
        output += String.format("snake_case: %s\n", snakecased);
        output += String.format("PascalCase: %s",   pascalcased);

        write(output);
    }
}

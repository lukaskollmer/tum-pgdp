/*
 * exercise-04/task-06
 * String manipulation (2)
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-13
 * status   in-progress
 */



import utils.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Main_Task_06 extends MiniJava {

    public static void main(String[] args) {
        String input = MiniJava.readString("Enter some text you'd like to modify");

        String explanation = "Possible Actions:\n";
        explanation += "0: Letter Frequency\n";
        explanation += "1: Replace Letters\n";
        explanation += "2: Reverse Words\n";

        switch (readInt(explanation)) {
            case 0:
                letterFrequency(input);
                break;
            case 1:
                replaceLetters(input);
                break;
            case 2:
                reverseWords(input);
                break;
            default:
                write("Invalid option. Plz try again.");
        }
    }



    static void letterFrequency(String input) {

        //
        // Calculate the frequency of individual letters
        //


        // We store the frequency data in a HashMap, using the letter as the key
        // and setting its number of occurrences in `input` as the value
        HashMap<String, Integer> letterFrequencies = new HashMap<>();

        input = StringUtil.makeUpperCase(input);

        // Loop over `input`, fetch the individual characters and update the frequency HashMap
        for (int i = 0; i < input.length(); i++) {
            String character = Character.toString(input.charAt(i));
            StringUtil.CharacterCase characterCase = StringUtil.getCase(character);

            // Make sure the character is a letter (we ignore everything else)
            if (characterCase !=  StringUtil.CharacterCase.NUMBER && characterCase != StringUtil.CharacterCase.INVALID) {
                // Increment the count in the hash table by one, using 0 as the default value if the character isn't already stored
                letterFrequencies.put(character, letterFrequencies.getOrDefault(character, 0) + 1);
            }
        }

        // Get an array of all keys in the table and sort it alphabetically
        ArrayList<String> keysAlphabeticallySorted = new ArrayList<>(letterFrequencies.keySet());
        Collections.sort(keysAlphabeticallySorted, (a, b) -> a.charAt(0) - b.charAt(0));


        // Turn the table into a nice string containing the individual frequencies
        String output = "";
        for (int i = 0; i < keysAlphabeticallySorted.size(); i++) {
            String key    = keysAlphabeticallySorted.get(i);
            Integer value = letterFrequencies.get(key);

            boolean isLast = i == keysAlphabeticallySorted.size() - 1;
            output += String.format("%s: %s%s", key, value, isLast ? "" : ", ");
        }

        write(output);
    }


    static void replaceLetters(String input) {
        //
        // Replacing parts of the word
        //


        String x = MiniJava.readString("Enter the letter you'd like to replace");
        String replacement = MiniJava.readString(String.format("Enter the letter you'd like to replace '%s' with", x));

        // 1. Iterate over all occurrences of the letter we want to replace (ignoring whether they are upper- or lowercased)
        // 2. Fetch the actual letter at the range
        // 3. Check whether it's upper- or lowercased
        // 4. Update `input` by replacing the letter at the position we found in (1) by replacing it with the replacement character
        //    (we also update the replacement letter to reflect the character case of the one we are replacing)
        for (StringUtil.Range occurrence : StringUtil.find(StringUtil.makeLowerCase(input), StringUtil.makeLowerCase(x))) {
            String oldLetter = StringUtil.getSubstringAtRange(input, occurrence);
            StringUtil.CharacterCase characterCase = StringUtil.getCase(oldLetter);

            input = StringUtil.replacing(input, occurrence.location, StringUtil.makeCase(replacement, characterCase));

        }

        write(input);
    }

    static void reverseWords(String input) {
        //
        // Reverse all words in a string but keep the words in the same order
        //

        // This is an easy one:
        // 1. Split the string into individual words
        // 2. Reverse them
        // 3. Join it back together
        write(ArrayUtil.join(Functional.map(StringUtil.split(input, " "), StringUtil::reverse), " "));
    }
}

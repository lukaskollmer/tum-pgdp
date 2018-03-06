
import utils.Functional;
import java.util.ArrayList;

class Utils_Task_08 {

    // Lambda for evaluating a generic condition
    interface Predicate<T> {
        boolean evaluate(T obj);
    }


    // Ask the user to enter an integer. This is repeated until `condition` evaluates to true
    static int getInt(String message, Utils_Task_08.Predicate<Integer> condition) {
        Integer input;

        do {
            input = MiniJava.readInt(message);
        } while (!condition.evaluate(input));

        return input;
    }

    /**
     * Encode a char into an integer
     *
     * Some rules apply:
     * - lowercase letters (a...z) become 0 through 25
     * - uppercase letters (A...Z) become 26 through 51
     * - numbers           (0...9) become 52 through 61
     * - a space  (' ') becomes 62
     * - a period ('.') becomes 63
     *
     * Returns -1 if the character `c` was invalid (outside the defined range)
     */
    static Integer encode(char c) {
        if (c == ' ') {
            return 62;
        } else if (c == '.') {
            return 63;
        }

        /*StringUtil.CharacterCase characterCase = StringUtil.getCase(c);
        if (characterCase == StringUtil.CharacterCase.LOWER) {
            return (c % 'a');
        } else if (characterCase == StringUtil.CharacterCase.UPPER) {
            return encode('z') + 1 + (c % 'A');
        } else if (characterCase == StringUtil.CharacterCase.NUMBER) {
            return encode('Z') + 1 + (c - '0');
        }*/

        //StringUtil.CharacterCase characterCase = StringUtil.getCase(c);
        if ('a' <= c && c <= 'z') {
            return (c % 'a');
        } else if ('A' <= c && c <= 'Z') {
            return encode('z') + 1 + (c % 'A');
        } else if ('0' <= c && c <= '9') {
            return encode('Z') + 1 + (c - '0');
        }




        // Invalid character
        return -1;
    }

    /**
     * Decode an Integer into a char
     *
     * (see the `encode` function for a explanation of how the values between 0 and 63 are mapped to chars)
     */
    static char decode(Integer i) {
        if (i == 62) {
            return ' ';
        } else if (i == 63) {
            return '.';
        }

        if (0 <= i && i <= 25) {
            return (char) ('a' + i);
        }

        if (26 <= i && i <= 51) {
            return (char) ('A' + i - 26);
        } else if (52 <= i && i <= 61) {
            return (char) ('0' + i - 52);
        }

        // Invalid character. We should never reach here if only values from the `encode` function are passed to `decode`
        return '\0';
    }
}


public class Main_Task_08 extends MiniJava {
    public static void main(String... args) {
        Utils_Task_08.Predicate<Integer> predicate = n -> 0 <= n && n <= 63;

        Integer key    = Utils_Task_08.getInt("Enter key (0...63)", predicate);
        Integer vector = Utils_Task_08.getInt("Enter vector (0...63)", predicate);
        String input   = MiniJava.readString("Enter the text you'd like to encrypt");

        //
        // ENCRYPTION
        //

        // 1. Encode - turn the string into an array of integers
        ArrayList<Integer> encodedCharacters = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            Integer encoded = Utils_Task_08.encode(input.charAt(i));
            if (encoded == -1) {
                write(String.format("Error: Found invalid letter '%s' in input", input.charAt(i)));
                System.exit(0);
            }
            encodedCharacters.add(encoded);
        }

        // 2. Run the CBC to encrypt the individual letters
        for (int i = 0; i < encodedCharacters.size(); i++) {
            Integer object = encodedCharacters.get(i);
            if (i == 0) {
                encodedCharacters.set(i, object ^ vector ^ key);
            } else {
                encodedCharacters.set(i, object ^ encodedCharacters.get(i - 1) ^ key);
            }
        }

        // 3. Decode the encrypted data into a string
        String encrypted = Functional.reduce(encodedCharacters, "", (acc, val) -> acc += Utils_Task_08.decode(val));
        write(String.format("Encrypted text: %s", encrypted));


        //
        // DECRYPTION
        //


        // 4. Re-encode the encrypted string
        ArrayList<Integer> array2 = new ArrayList<>();
        for (int i = 0; i < encrypted.length(); i++) {
            array2.add(Utils_Task_08.encode(encrypted.charAt(i)));
        }

        // 5. Decrypt the individual characters (this is essentially the exact reverse of what we did in step 2)
        Integer lastElement = 0; // Since we modify the array while we're iterating over it, we have to keep track of the last element
        for (int i = 0; i < array2.size(); i++) {
            Integer element = array2.get(i);
            if (i == 0) {
                array2.set(i, element ^ key ^ vector);
            } else {
                array2.set(i, element ^ lastElement ^ key);
            }

            lastElement = element;
        }

        // 5. Decode the now decrypted data into a string
        String decrypted = Functional.reduce(array2, "", (acc, val) -> acc += Utils_Task_08.decode(val));
        write(String.format("decrypted: %s\n", decrypted));


    }
}

/*
 * exercise-06/task-07
 *
 * Multiplying numbers of an arbitrary base and shoving the entire thing into a nice latex table
 * */

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main_Task_07 {
    public static void main(String... args) {

        /*
         * Here are some example numbers, with the correct result:
         * (base 17) 3FC1 * 189 = 5F30E9
         * (base 19) 3FC1 * 189 = 5A0A29
         *
         * (base 10) 24601357 * 25574234 = 629160860635538
         * (base  8) 24601357 * 25574234 = 703163155650644
         *
         * (base 16) C0FF3E * 57AFF5 = 421B6541A856
         * (base 35) C0FF3E * 57AFF5 = 1RJUNAHW4LH0
         *
         * (base  2) 110 * 1001010101 = 110111111110
         *
         */


        int base = MiniJava.readInt("Enter the base");

        int[] a = readNumber(MiniJava.readString("Enter the first number"));
        int[] b = readNumber(MiniJava.readString("Enter the second number"));

        multiplyAndPrintLatex(a, b, base);


    }




    // Multiply two numbers (represented by int-arrays) of a specific base
    // and print the calculation and the result as a LaTeX table
    static void multiplyAndPrintLatex(int[] x, int[] y, int base) {
        // First, we calculate the width of the latex table
        // +2 for the plus signs at the left border and the multiplication sign in the header
        int rowLength = x.length + y.length + 2;

        // Generate the alignment flags for the latex table
        StringBuilder alignmentFlags = new StringBuilder();
        for (int i = 0; i < rowLength + 2; i++) {
            alignmentFlags.append("c");
        }

        // Turn the numbers we multiply into latex-friendly strings
        String leftOperandElements  = Utils.wrapInAndSigns(Utils.reversed(x), x.length);
        String rightOperandElements = Utils.wrapInAndSigns(Utils.reversed(y), y.length);

        // Print the header of the latex table
        System.out.format("\\begin{tabular}{%s}\n", alignmentFlags.toString());
        System.out.format("  & %s & $\\ast$ & %s & \\\\ \n", leftOperandElements, rightOperandElements);
        System.out.format("\\hline\n");

        // Multiply the numbers and print the individual rows of the latex table after each multiplication step
        int[] result = mul_fancy(x, y, base, step -> {
            System.out.format("+ %s\n", Utils.toLatexTableRow(Utils.reversed(step), rowLength));
        });

        // Print the footer of the latex table
        System.out.format("\\hline\n");
        System.out.format("= %s\n", Utils.toLatexTableRow(Utils.reversed(result), rowLength));
        System.out.format("\\end{tabular}\n");
    }


    // Turn a string containing a number into an array of integers representing the individual digits of the number
    // Rules:
    // - the digits 0...9 are represented by themselves (the digits 0...9)
    // - everything above that (letters) is represented by digits
    static int[] readNumber(String input) {

        input = new StringBuilder(input).reverse().toString();

        int[] number = new int[input.length()];

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= '0' && c <= '9') {
                // the character represents an actual number between 0...9
                number[i] = Math.abs('0' - c);
            } else {
                // the character represents a letter between a...z
                // we first make it uppercase, then calculate the offset from 9
                c = Utils.makeCharUppercase(c);
                number[i] = 10 + c - 'A';
            }
        }

        return number;
    }


    // 'convert' a single digit to a string in the format of 0...z
    static String toString(int digit) {
        if (digit >= 0 && digit <= 9) {
            // simply turn the int into a string
            return String.valueOf(digit);
        } else {
            // create a string from the character at that codepoint
            return String.valueOf((char) ('A' + digit - 10));
        }
    }

    // 'convert' an array of digits (a number) to a string
    static String toString(int[] number) {
        return Utils.primitiveToList(Utils.reversed(number))
                .stream()
                .map(Main_Task_07::toString)
                .collect(Collectors.joining());
    }


    // Add two numbers of the same base
    static int[] add(int[] a, int[] b, int base) {
        // List containing the result. We can't use an `int[]` since we don't know the length in advance
        List<Integer> result = new ArrayList<>();

        // Make sure that `a` is always the larger array (in terms of number of elements)
        if (b.length > a.length) {
            int[] temp = a;
            a = b;
            b = temp;
        }

        // Add the two numbers
        // Since the arrays `a` and `b` already contain the numbers in reverse order,
        // we can just loop over them from start to end
        // Important: if `b` and `b` are of differing sizes, `a` should always be the longer array

        // How does the actual addition work?
        // 1. We loop over the the larger array and fetch the value at the same location in `b` (0 id `b` doesn't have a value at that index)
        // 2. We create the sum of the two elements, plus the carry value (which might still be set from the previous element)
        // 3. if the result is larger than or equal to the base we're working with, we set the carry value and subtract the base from the result
        // 4. if the result is smaller than the base, we reset the carry value to 0
        // 5. save the result
        // 6. after adding all individual digits, we check the carry flag one last time and - if it is greater 0 - add that digit to the result


        int carry = 0; // The value we carry over to the next column
        for (int i = 0; i < a.length; i++) {
            int x = a[i];
            int y = (i <= b.length-1) ? b[i] : 0;

            int res = x + y + carry;

            if (res >= base) {
                carry = res / base;
                res = res % base;
            } else {
                carry = 0;
            }

            result.add(res);
        }

        if (carry > 0) {
            result.add(carry);
        }

        // `result` contains the digits of the added numbers, in reverse order
        return Utils.listToPrimitive(result);
    }


    // Multiply a number (represented by an array of `int`s by `digit`, then shift the result by `shift`.
    static int[] mulDigit(int[] a, int digit, int shift, int base) {
        // List containing the result. We can't use an `int[]` since we don't know the length in advance
        List<Integer> result = new ArrayList<>();

        // Multiplication basically works like addition (see above)

        int carry = 0;

        for (int value : a) {
            int res = value * digit + carry;

            if (res >= base) {
                carry = res / base;
                res = res % base;
            } else {
                carry = 0;
            }

            result.add(res);
        }

        if (carry > 0) {
            result.add(carry);
        }

        // Insert leading zeroes until we shifted the number as far as requested
        // We insert at index 0 bc the number is reversed
        for (int i = 1; i <= shift; i++) {
            result.add(0, 0);
        }

        return Utils.listToPrimitive(result);
    }


    // multiply two numbers of the specified base
    static int[] mul(int[] a, int[] b, int base) {
        return mul_fancy(a, b, base, n -> {});
    }


    interface CalculationStepHandler {
        void didProcessStep(int[] number);
    }

    // multiply two numbers of the specified base.
    // After each step of the multiplication process, `stepHandler` is called with the number at that step
    // This allows you to hook into the multiplication process and somehow process that information
    static int[] mul_fancy(int[] a, int[] b, int base, CalculationStepHandler stepHandler) {

        List<List<Integer>> steps = new ArrayList<>();

        // b is shadowed in the current scope. the parameters are not mutated!
        b = Utils.reversed(b);
        for (int i = 0; i < b.length; i++) {
            int[] partialResult = mulDigit(a, b[i], b.length - i - 1, base);
            steps.add(Utils.primitiveToList(partialResult));
            stepHandler.didProcessStep(partialResult);

        }

        int[] result = Utils.listToPrimitive(steps.get(0));

        for (int i = 1; i < steps.size(); i++) {
            int[] temp = Utils.listToPrimitive(steps.get(i));
            result = add(result, temp, base);
        }

        return result;
    }


    private static class Utils {

        // check whether the char `c` is uppercase
        static boolean charIsUppercase(char c) {
            return c >= 'A' && c <= 'Z';
        }


        // Make the character uppercase
        // this expects `c` to be a valid character (a...z|A...Z)
        static char makeCharUppercase(char c) {
            if (charIsUppercase(c)) {
                return c;
            } else {
                return (char)(c - 32);
            }
        }


        // Reverse an integer array
        static int[] reversed(int[] input) {
            int[] reversed = new int[input.length];

            for (int i = 0; i < input.length; i++) {
                reversed[input.length - i - 1] = input[i];
            }

            return reversed;
        }


        // Create an array of `int`s from a List<Integer>
        static int[] listToPrimitive(List<Integer> list) {
            int length = list.size();

            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = list.get(i);
            }

            return array;
        }


        // Create a List<Integer> from an array of `int`s
        static List<Integer> primitiveToList(int[] array) {
            List<Integer> list = new ArrayList<>();

            for (int i : array) {
                list.add(i);
            }

            return list;
        }


        // Create a LaTeX table row string from an array of numbers
        static String toLatexTableRow(int[] numbers, int length) {
            return String.format("%s\\\\", wrapInAndSigns(numbers, length));
        }


        // Wrap the individual digits of `number` with '&' signs, until `length` is reached
        static String wrapInAndSigns(int[] number, int length) {
            String rowString = Utils.primitiveToList(number)
                    .stream()
                    .map(Main_Task_07::toString)
                    .collect(Collectors.joining(" & "));


            // fill the string with trailing '&'s until we reach the requested length
            while (rowString.split("&").length < length) {
                rowString = "&   " + rowString;
            }

            return rowString;
        }

    }
}

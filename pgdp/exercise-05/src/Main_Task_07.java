/*
 * exercise-05/task-07
 * Solving a system of linear equations using Gaussian Elimination
 *
 * Here are some example equations, with the correct result (verified via WolframAlpha)
 *
 * {"1a+2b+3c=2", "1a+1b+1c=2", "3a+3b+1c=0"};  // expected result: [5, -6, 3]
 * {"6a+2b+3c=2", "4a+2b+2c=4", "3a+3b+1c=0"};  // expected result: [-10, 4, 18]
 * {"-1x+1y+1z=0", "1x-3y-2z=5", "5x+1y+4z=3"}; // expected result: [-1, -4, 3]
 * {"6w-1x+1y-12z=-5", "6w-2x+2y-8z=8", "3w+0x+2y-4z=5", "3w-1x+0y-4z=9"}; // expected result: [13, 6, -5, 6]
 *
 * */

import java.util.ArrayList; // Used for term entry
import java.util.Arrays;    // Arrays.toString
import java.util.regex.*;   // Pattern


public class Main_Task_07 extends MiniJava {

    // Number of equations entered. This is set after requesting them from the user
    static int numberOfEquations;

    public static void main(String... args) {
        boarding();

        int[] matrix = readMatrix(getTerms());
        int[] solved = solveSystem(matrix);

        write(String.format("Solution: %s\n", Arrays.toString(solved)));
    }


    // Tell the user how this works
    static void boarding() {
        String explanation = "" +
                "Gaussian elimination\n\n" +
                "There are a couple of rules:\n" +
                "1. You have to enter terms in the exact form 'a+b+c+...=y'.\n" +
                "2. Always enter a number, even if an element's factor is 1 (eg '1x' instead of 'x')\n" +
                "3. End term input by entering an empty string\n" +
                "4. All entered terms must have the exact same number of elements. Everything else will result in UB\n\n" +
                "See the source code for some example terms";
        write(explanation);
    }

    // Ask the user to enter terms
    // This ends when the user enters an empty string
    static ArrayList<String> getTerms() {
        ArrayList<String> terms = new ArrayList<>();

        while (true) {
            String term = MiniJava.readString("Please enter a term in the form of 'a+b+c+...=y'");
            if (term.length() == 0) {
                break;
            }
            terms.add(term);
        }

        return terms;
    }

    // Turn an array of term strings into a one-dimensional array
    static int[] readMatrix(ArrayList<String> terms) {
        numberOfEquations = terms.size();

        int numberOfElements = (numberOfEquations + 1) * numberOfEquations;

        int[] matrix = new int[numberOfElements];

        int equationIndex = 0;
        for (int i = 0; i < numberOfElements; i += (numberOfEquations + 1)) {
            // `i` is the index of the first element in the row of the current equation

            // fancy regex to match all digits in the term and store them in individual capturing groups
            // this also supports signed numbers
            Pattern pattern = Pattern.compile("([-+]?\\d+)");
            Matcher matcher = pattern.matcher(terms.get(equationIndex));

            for (int j = 0; matcher.find(); j++) {
                matrix[i + j] = Integer.parseInt(matcher.group(0));
            }
            equationIndex++;
        }

        return matrix;
    }

    // Print a matrix nicely formatted to stdout
    static void printMatrix(int[] matrix) {
        int equationLength = getEquationLength(matrix);

        for (int i = 0; i < matrix.length; i += (numberOfEquations + 1)) {
            for (int j = 0; j < equationLength; j++) {
                int value = matrix[i + j];
                boolean isNegative = value < 0;

                System.out.format("%s%s ", isNegative ? "" : " ", matrix[i + j]);
            }
            System.out.format("\n");
        }
    }

    // Get the value at the specified coordinates
    // both `line` and `column` are 0-based indices
    static int get(int[] matrix, int line, int column) {
        return matrix[getEquationLength(matrix) * line + column];
    }

    // Set the value at the specified coordinates
    // both `line` and `column` are 0-based indices
    // Returns the previous value
    static int set(int[] matrix, int line, int column, int newValue) {
        int previousValue = get(matrix, line, column);

        matrix[getEquationLength(matrix) * line + column] = newValue;

        return previousValue;
    }

    // Multiply a line in the matrix by `factor`
    static void multLine(int[] matrix, int line, int factor) {
        int equationLength = getEquationLength(matrix);

        for (int i = 0; i < equationLength; i++) {
            int currentValue = get(matrix, line, i);
            set(matrix, line, i, currentValue * factor);
        }
    }

    // Multiply `otherLine` by `factor` and add it to `line`
    // `otherLine` in the matrix will not be mutated
    static void multAddLine(int[] matrix, int line, int otherLine, int factor) {
        int[] matrix2 = matrix.clone();
        multLine(matrix2, otherLine, factor);

        for (int i = 0; i < getEquationLength(matrix); i++) {
            int currentValue = get(matrix, line, i);
            set(matrix, line, i, currentValue + get(matrix2, otherLine, i));
        }
    }

    // Swap the contents of two lines in the matrix
    static void swap(int[] matrix, int lineA, int lineB) {
        // we put the contents of lineB in a temporary array
        int[] tempStorage = getLine(matrix, lineB);

        // go through lineA and swap each element with the one at tempStorage[i]
        // this means that after this for loop, matrix[lineA] now contains the values of matrix[lineB]
        // and tempStorage contains the previous values of matrix[lineA]
        for (int i = 0; i < getEquationLength(matrix); i++) {
            int value = get(matrix, lineA, i);
            set(matrix, lineA, i, tempStorage[i]);
            tempStorage[i] = value;
        }

        // go through line B and put the elements of tempStorage (which now contains the previous
        // values of matrix[lineA]) into matrix[lineB]
        for (int i = 0; i < getEquationLength(matrix); i++) {
            set(matrix, lineB, i, tempStorage[i]);
        }
    }

    static void searchSwap(int[] matrix, int fromLine) {
        // assigning `row` and `column` to `fromLine` is not necessary,
        // but it makes the code below a bit easier to read (IMO)
        int row = fromLine;
        int column = fromLine;

        for (int nextRow = row+1; nextRow < numberOfEquations; nextRow++) {
            int value = get(matrix, row, column);
            int valueBelow = get(matrix, nextRow, column);

            if (value == 0) {
                swap(matrix, row, nextRow);
            } else if (valueBelow != 0) {
                int kgv = kgv(value, valueBelow);
                multLine(matrix, nextRow, kgv/valueBelow);
                multAddLine(matrix, nextRow, row, (-kgv)/value);
            }
        }
    }


    static int kgv(int a, int b) {
        // to get the least common multiple, we
        // 1) multiply a and b
        // 2) compare a and b and fetch the smaller one (this is important to make it work with negative numbers)
        // 3) check for each number between `smaller` and `multiplied` whether both a and b
        //    fit into that number without a remainder
        // 4) if they do, that's the lcm

        int multiplied = a * b;

        int smaller = a;
        if (b < a) { smaller = b; }

        for (int i = smaller; i < multiplied; i++) {
            if (i % a == 0 && i % b == 0) {
                return i;
            }
        }

        return multiplied;
    }


    // Calculate the solution of the system of linear equations represented by `matrix`
    static int[] rowEchelonToResult(int[] matrix) {
        final int equationLength = getEquationLength(matrix); // this includes the solution part

        // array containing the results of the
        int[] results = new int[equationLength - 1];


        // loop backwards over all equations
        for (int equationIndex = numberOfEquations-1; equationIndex >= 0; equationIndex--) {
            int[] equation = getLine(matrix, equationIndex);

            int equationResult = 0;
            int equationFactor = 1;
            int otherNumbers = 0;

            // loop backwards over the individual elements of the equation
            for (int i = equationLength - 1; i >= 0; i--) {
                int value = equation[i];
                if (i == equationLength - 1) {
                    equationResult = value;
                } else if (i == equationIndex) {
                    equationFactor = value;
                } else {
                    otherNumbers += value * results[i];
                }
            }

            // calculate the solution for the equation and save it to `results`
            results[equationIndex] = (equationResult - otherNumbers) / equationFactor;
        }

        return results;
    }



    // Apply the gaussian elimination algorithm and return the solution of the equations
    static int[] solveSystem(int[] matrix) {
        for (int row = 0; row < numberOfEquations - 1; row++) {
            searchSwap(matrix, row);
        }

        return rowEchelonToResult(matrix);
    }





    // Helper function to get a single line from the matrix
    // (a single line of course being one of the entered equations)
    static int[] getLine(int[] matrix, int line) {
        int equationLength = getEquationLength(matrix);

        int[] lineContents = new int[equationLength];
        for (int i = 0; i < equationLength; i++) {
            lineContents[i] = get(matrix, line, i);
        }

        return lineContents;
    }

    // Helper function to get the length of each equation
    // This assumes that all equations in `matrix` have the same length
    // In this case, length means the total number of elements in the equation, including the result
    // For example, the equation '1a+2b+3c=4' has the length 4
    static int getEquationLength(int[] matrix) {
        return matrix.length / numberOfEquations;
    }
}

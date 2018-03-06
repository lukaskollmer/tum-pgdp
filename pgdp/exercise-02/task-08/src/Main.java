/*
 * exercise-02/task-08
 * Check whether a number is a prime number
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-04
 */



public class Main extends MiniJava {

    public static void main(String[] args) {

        // Ask the user to enter a number. Repeat until the number is > 0
        int input = 0;
        while (input <= 0) {
            input = readInt("Enter a number > 0");
        }

        // Check if the supplied number is a prime number.
        // This is a bit more complicated than it needs to be since we aren't allowed to use a for loop
        // Instead, we create a temporary variable (`x`), check if `input` can be divided by `x` and increment `x` by 1     // as long as it's smaller than input
        // Since we know that 1 and 2 are prime numbers, `x` starts at 2
        // The while loop checks for each `x` between 2 and `input` whether `input` can be divided by that `x`
        // If `input` can be divided by `x`, we know that it's not a prime number (since `x` is always guaranteed to be smaller than `input`)
        // If `input` cannot be divided by `x`, we know that it is a prime number

        int x = 2;
        while (x < input) {
            if (input % x == 0) {
                write(String.format("%s is not a prime number :(", input));
                return;
            }
            x++;
        }
        write(String.format("%s is a prime number :)", input));
    }
}

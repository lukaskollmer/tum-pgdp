/*
 * exercise-02/task-07
 * Calculate the sum of all positive integers < n that can be divided by 3 or 7
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-04
 */


public class Main extends MiniJava {

    public static void main(String[] args) {
        int input = -1;

        while (input < 0) {
            input = readInt("Enter an integer > 0");
        }


        int sum = 0;

        while (input > 0) {
            if ((input % 3) == 0 || (input % 7) == 0) {
                sum += input;
            }
            input--;
        }


        write(String.format("Result: %s", sum));

    }
}

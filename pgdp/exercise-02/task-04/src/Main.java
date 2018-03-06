/*
 * exercise-02/task-04
 * Implementing the historic dice game "merry seven" ("Lustige Sieben")
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-02
 */



import java.util.Arrays;
import java.util.List;

public class Main extends MiniJava {

    static Integer rollDice() {
        return dice() + dice();
    }

    static boolean diceResultIsInSameRowAsField(Integer diceResult, Integer field) {
        List<Integer> leftFields = Arrays.asList(2, 3, 4, 5, 6);
        List<Integer> rightFields= Arrays.asList(8, 9, 10, 11, 12);

        return leftFields.contains(diceResult) && leftFields.contains(field) || rightFields.contains(diceResult) && rightFields.contains(field);
    }

    public static void main(String[] args) {

        boolean didEnd = false;
        Integer creditsLeft = 100;

        while (!didEnd && creditsLeft > 0) {
            // BAD: You have to enter both in order to exit the game
            int input = readInt("How many credits would you like to set. Enter 0 to exit");
            int field = readInt("Select a field (2...12). Enter 0 to exit");

            if (input <= 0 || field <= 0) {
                didEnd = true;
                System.out.println("The player chose to exit the game");
            } else if (input > creditsLeft) {
                System.out.println("Invalid input");
                System.exit(0);
            } else if (field > 12) {
                System.out.println("Invalid input. You tried to set more credits than you had left");
                System.exit(0);
            }

            // subtract the credits
            creditsLeft -= input;

            // Roll dice and check the results
            Integer diceResult = rollDice();

            if (diceResult == 7 && field == 7) {
                creditsLeft += input * 3;
            } else if (diceResult == field) {
                creditsLeft += input * 2;
            } else if (diceResultIsInSameRowAsField(diceResult, field)) {
                creditsLeft += input;
            }

            write(String.format("Current #credits: %s\n", creditsLeft));
        }

        write(String.format("Ended game with %s credits left\n", creditsLeft));
    }
}

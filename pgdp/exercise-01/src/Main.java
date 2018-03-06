/*
 * exercise-01
 * Displays the text 'Hello World! ♥', with an unicode penguin hidden at a random location
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-10-28
 */


import java.util.Random;

class Util {

    /**
     * Create a new String from `target` by inserting `text` at a random location
     */
    static String insertAtRandomLocation(String target, String text) {
        int lower = 0, upper = target.length();

        int random = new Random().nextInt(upper - lower) + lower;

        String firstPart = target.substring(0, random);
        String lastPart  = target.substring(random, upper);

        return String.join("", firstPart, text, lastPart);
    }
}


public class Main extends MiniJava {

    public static void main(String[] args) {
        String penguin = "\uD83D\uDC27";

        write(Util.insertAtRandomLocation("Hello World! ♥", penguin));
    }
}

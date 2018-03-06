package task_05;


/*
*
* exercise-09/task-05
*
* simulating a christmas tree
*
* USAGE:
* pass --tree to get a canvas that is pre-filled w/ a christmas tree similar to the one on the instructions pdf
*
* OTHER STUFF
*
* the instructions weren't always quite clear, so i took some liberties in how i implemented some things
* - new objects only appear when the last object either was moved off the canvas or locked
* - (this means that there only ever is one object under the full control of the player)
* - all new objects appear in the top left corner
*   it might look like a bug that new objects never appear in the top left corner, but always offset by a single tile.
*   this is intentional. the initial position of all new objects is 1x1 (top left corner), but since new objects only appear on
*   a move {down|left|right}, they always get moved in that direction, immediately after being added to the canvas
* - the player can change the phase (tree building or tree decorating) while an object is still falling down, but it won't go into effect
*   until the next new object appears (after the current one is either locked or destroyed)
*
* */

import java.util.Arrays;
import java.util.List;

public class Weihnachtsbaum extends BitteNichtAbgeben {


    public static void keyPressed(int key) {
        handleKey(Direction.values()[key]);
    }


    static void handleKey(Direction direction) {
        if (direction == Direction.UP) {
            GameManager.shared.switchPhase();
            return;
        }


        GameManager.shared.currentObject().move(direction);

        GameManager.shared.addNewIndependentDecoration(Weihnachtsobjekt.Kind.FOREGROUND_SNOWFLAKE, 0.25);
        GameManager.shared.moveAllIndependentObjects(direction);

        GameManager.shared.draw();
    }


    public static void main(String[] args) {

        List<String> argsAsList = Arrays.asList(args);

        if (argsAsList.contains("--tree")) {
            GameManager.shared.drawTree();
        } else {
            System.out.format("FYI: You can pass --tree to get a canvas that is pre-filled w/ a christmas tree similar to the one on the instructions pdf\n");
        }

        GameManager.shared.draw();

        handleUserInput();
    }

    private static void handleUserInput() {
        while(true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                /* Intentionally left blank */
            }
            int step = nextStep();
            if (step != NO_KEY) {
                // System.out.print(step+",");
                keyPressed(step);
            }
        }
    }
}

/*
 * exercise-02/task-03
 * Implementing the dice game Mia (https://de.wikipedia.org/wiki/Mäxchen)
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   2017-11-02
 */

import java.util.ArrayList;
import java.util.Collections;

class Turn {
    enum Player {
        COMPUTER, HUMAN
    }

    static Player switchPlayer(Player aPlayer) {
        return aPlayer == Player.COMPUTER ? Player.HUMAN : Player.COMPUTER;
    }


    Integer value;
    Player player;


    Turn(Integer value, Player player) {
        this.value = value;
        this.player = player;
    }

    boolean isMeier() {
        return value == 21;
    }

    boolean isPatsch() {
        Integer remainer = value % 10;
        return remainer * 11 == value;
    }

    Integer hash() {
        if (this.isMeier()) {
            return 1000;
        } else if (this.isPatsch()) {
            return 100 + this.value;
        } else {
            return value;
        }
    }

    boolean loosesComparedTo(Turn otherTurn) {
        return this.hash() < otherTurn.hash();
    }
}


public class Main extends MiniJava {

    static Integer rollDiceAndCalculateResult() {
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(dice());
        numbers.add(dice());

        Collections.sort(numbers);
        Collections.reverse(numbers);

        return (numbers.get(0) * 10) + numbers.get(1);
    }


    public static void main(String[] args) {

        boolean over = false;
        Turn lastTurn = new Turn(-1, Turn.Player.COMPUTER);

        Integer index = 0;

        while (!over) {
            Integer value = rollDiceAndCalculateResult();
            Turn turn = new Turn(value, Turn.switchPlayer(lastTurn.player));

            System.out.format("turn %s: value: %s, player: %s, isMeier: %s, isPatsch: %s, hash: %s\n", index, turn.value, turn.player, turn.isMeier(), turn.isPatsch(), turn.hash());


            if (turn.loosesComparedTo(lastTurn)) {
                over = true;
                System.out.println("GAME OVER");
                System.out.format("%s LOST %s to %s\n", turn.player, turn.value, lastTurn.value);
            }
            lastTurn = turn;
            index++;
        }
    }
}

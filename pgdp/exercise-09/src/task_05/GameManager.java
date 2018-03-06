package task_05;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;


public class GameManager {

    public enum Phase {
        TreeCreation,   // phase 1: we build the tree itself
        TreeDecoration  // phase 2: we decorate the tree
    }

    public static GameManager shared = new GameManager(30, 33);

    public Phase phase;

    public final int width;
    public final int height;


    private final int[][] canvas;

    private List<Weihnachtsobjekt> objects = new ArrayList<>();


    GameManager(int width, int height) {
        this.width  = width;
        this.height = height;
        this.phase  = Phase.TreeCreation;
        this.canvas = BitteNichtAbgeben.generateLandscape(width, height);
    }




    public OptionSet<Weihnachtsobjekt.Kind> kindsAtPosition(int x, int y) {
        if (x <= 0 || y <= 0 || x >= width - 1 || y >= height - 1) {
            return new OptionSet<>(Weihnachtsobjekt.Kind.BACKGROUND_SURROUNDING);
        }

        int value = canvas[x][y];

        if (value == Weihnachtsobjekt.Kind.BACKGROUND_SURROUNDING.rawValue) {
            return new OptionSet<>(Weihnachtsobjekt.Kind.BACKGROUND_SURROUNDING);
        }

        Weihnachtsobjekt.Kind background = Weihnachtsobjekt.Kind.values()[Util.bgShift(value)];
        Weihnachtsobjekt.Kind foreground = Weihnachtsobjekt.Kind.values()[Util.fgShift(value) + 8];


        return new OptionSet<>(background, foreground);

    }

    // mutate the value at the position
    // returns the previous value
    int mutateAtPosition(int x, int y, Function<Integer, Integer> mutationBlock) {
        int oldValue = canvas[x][y];
        canvas[x][y] = mutationBlock.apply(oldValue);
        return oldValue;
    }




    void switchPhase() {
        // only works as long as there are two phases!
        phase = Phase.values()[1 - phase.ordinal()];
    }



    void addObject(Weihnachtsobjekt object) {
        objects.add(object);

        updatePositionOfObject(object);
        draw();
    }


    void updatePositionOfObject(Weihnachtsobjekt object) {

        if (object instanceof SingleObject) {
            // add the object's kind to the tile's raw value or subtract it, depending on whether the object is marked for destruction
            int change = object.kind.rawValue * (object.markedForDeath ? -1 : 1);
            mutateAtPosition(object.getX(), object.getY(), val -> val + change);

        } else if (object instanceof MultiObject) {
            for (SingleObject singleObject : ((MultiObject) object).parts) {
                updatePositionOfObject(singleObject);
            }
        }

        if (object.markedForDeath) {
            this.objects.remove(object);
        }
    }


    public void draw() {
        BitteNichtAbgeben.draw(this.canvas);
    }




    void moveAllIndependentObjects(Direction direction) {
        OptionSet<Direction> leftRight = new OptionSet<>(Direction.LEFT, Direction.RIGHT);

        Iterator<Weihnachtsobjekt> it = objects.iterator();

        while (it.hasNext()) {
            Weihnachtsobjekt object = it.next();
            if (object.isIndependent) {

                if (object.canMove(Direction.DOWN)) {
                    object.move(Direction.DOWN);

                } else if (leftRight.contains(direction) && object.canMove(direction)) {
                    object.move(direction);
                }
            }
        }
    }



    // returns the currently falling object
    // if the last falling object was locked in its position or destroyed,
    // this method will create a new object, add it to the canvas and return a reference
    Weihnachtsobjekt currentObject() {
        Weihnachtsobjekt object;


        for (ListIterator<Weihnachtsobjekt> iterator = objects.listIterator(objects.size()); iterator.hasPrevious(); ) {
            object = iterator.previous();
            if (!object.isLocked && !object.isIndependent) {
                return object;
            }
        }

        // create a new object for the current phase
        object = RandomObjectFactory.createRandomObjectForPhase(phase);


        for (int i = 1; i < object.getRightmostXPosition(); i++) {

            if (kindsAtPosition(i, 1).intersection(object.supportedMoveDestinationKinds()).isEmpty()) {
                // no free tiles available
                // instead of returning a new object, we return the topmost one
                // (which is already locked, meaning that the game is now pretty much over)
                return objects.get(objects.size() - 1);
            }
        }

        if (this.canvas[1][1] != 0 || this.canvas[1][2] != 0) {
            // canvas is full
            object.isLocked = true;
        }

        addObject(object);

        return object;
    }



    // add an independent decoration node
    void addNewIndependentDecoration(Weihnachtsobjekt.Kind decorationKind, double probability) {
        if (!Util.random(probability)) {
            return;
        }

        DecorationObject decoration = new DecorationObject(decorationKind);
        decoration.isIndependent = true;

        // get a free random field where we can put the snowflake
        while (true) {
            int x = Util.random(1, width - 1);
            int y = Util.random(1, height - 1);

            OptionSet<Weihnachtsobjekt.Kind> kindsAtRandomPosition = kindsAtPosition(x, y);

            if (kindsAtRandomPosition.contains(Weihnachtsobjekt.Kind.FOREGROUND_EMPTY)) {
                decoration.setX(x);
                decoration.setY(y);
                this.addObject(decoration);
                return;
            }
        }
    }



    // draw a tree on the canvas, similar to the one on the instructions pdf
    void drawTree() {
        this.canvas[13][31] = Weihnachtsobjekt.Kind.BACKGROUND_TRUNK_LEFT.rawValue;
        this.canvas[16][31] = Weihnachtsobjekt.Kind.BACKGROUND_TRUNK_RIGHT.rawValue;

        int[][] trunk = {
                {14, 31}, {15, 31},
                {14, 30}, {15, 30},
                {14, 29}, {15, 29}
        };

        for (int[] trunkFields : trunk) {
            this.canvas[trunkFields[0]][trunkFields[1]] = Weihnachtsobjekt.Kind.BACKGROUND_TRUNK_MIDDLE.rawValue;
        }


        int[][] tree = {
                // for each row:
                // start, end, row number
                {14, 15,  9},
                {13, 16, 10},
                {12, 17, 11},
                {11, 18, 12},
                {10, 19, 13},

                {13, 16, 14},
                {12, 17, 15},
                {11, 18, 16},
                {10, 19, 17},
                {9,  20, 18},

                {12, 17, 19},
                {11, 18, 20},
                {10, 19, 21},
                {9,  20, 22},
                {8,  21, 23},

                {11, 18, 24},
                {10, 19, 25},
                {9,  20, 26},
                {8,  21, 27},
                {7,  22, 28},
        };

        for (int[] treeInfo : tree) {
            for (int i = treeInfo[0]; i <= treeInfo[1]; i++) {
                int val = Weihnachtsobjekt.Kind.BACKGROUND_GREEN_MIDDLE.rawValue;

                if (i == treeInfo[0]) {
                    val = Weihnachtsobjekt.Kind.BACKGROUND_GREEN_LEFT.rawValue;
                } else if (i == treeInfo[1]) {
                    val = Weihnachtsobjekt.Kind.BACKGROUND_GREEN_RIGHT.rawValue;
                }

                this.canvas[i][treeInfo[2]] = val;
            }
        }

        this.draw();
    }

}

package task_05;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.BiFunction;

public class Weihnachtsobjekt {


    enum Kind implements OptionSet.Enum {
        @ObjectType.Background
        BACKGROUND_EMPTY        (0),
        @ObjectType.Background
        BACKGROUND_TRUNK_MIDDLE (1 << 8),
        @ObjectType.Background
        BACKGROUND_TRUNK_LEFT   (2 << 8),
        @ObjectType.Background
        BACKGROUND_TRUNK_RIGHT  (3 << 8),
        @ObjectType.Background
        BACKGROUND_GREEN_MIDDLE (4 << 8),
        @ObjectType.Background
        BACKGROUND_GREEN_LEFT   (5 << 8),
        @ObjectType.Background
        BACKGROUND_GREEN_RIGHT  (6 << 8),
        BACKGROUND_SURROUNDING  (18 << 8),


        @ObjectType.Foreground
        FOREGROUND_EMPTY     (0),
        @ObjectType.Foreground
        FOREGROUND_SNOWFLAKE (1),
        @ObjectType.Foreground
        FOREGROUND_BAUBLE    (2),
        @ObjectType.Foreground
        FOREGROUND_PENGUIN   (3);

        final int rawValue;

        Kind(int rawValue) {
            this.rawValue = rawValue;
        }

        static Kind fromRawValue(int rawValue) {
            for (Kind kind : Kind.values()) {
                if (kind.rawValue == rawValue) {
                    return kind;
                }
            }
            return null;
        }

        @Override
        public int getRawValue() {
            return rawValue;
        }
    }


    // location of the object within its coordinate system
    protected int _x;
    protected int _y;

    protected Kind kind;

    // objects that are 'marked for death' will be removed from the canvas on the next redraw
    protected boolean markedForDeath = false;

    // locked objects are fixed in their current location
    protected boolean isLocked = false;

    // independent objects have their own moving behaviour and are unaffected by the precise user input
    protected boolean isIndependent = false;



    Weihnachtsobjekt() {
        setX(1);
        setY(1);
    }


    public int getLeftmostXPosition() {
        return getX();
    }

    public int getRightmostXPosition() {
        return getX();
    }



    void setX(int x) {
        this._x = x;
    }

    int getX() {
        return this._x;
    }

    void setY(int y) {
        this._y = y;
    }

    int getY() {
        return this._y;
    }




    //
    // MOVE
    //

    // Check whether an object can move in the specified direction
    // Returns no if moving the object in that direction would collide w/ another object or w/ a wall
    boolean canMove(Direction direction) {
        if (this.isLocked || direction == Direction.UP) {
            // moving up is not an allowed operation
            return false;
        }

        OptionSet<Kind> supported = this.supportedMoveDestinationKinds();


        BiFunction<Integer, Integer, Boolean> canMoveTo = (x, y) -> {
            // todo does this if clause actually do something or can we get rid of it
            if (x >= GameManager.shared.width || y >= GameManager.shared.height) {
                return false;
            }

            OptionSet<Kind> atPosition = GameManager.shared.kindsAtPosition(x, y);

            if (!isIndependent) {
                // background or foreground
                Class<? extends Annotation>[] annotClasses  = Arrays.stream(getClass().getAnnotations()).map(a -> a.annotationType()).toArray(Class[]::new);
                Class<? extends Annotation> self_annotation = Util.getAnyMatching(annotClasses, ObjectType.Foreground.class, ObjectType.Background.class);

                if (self_annotation != null) {
                    OptionSet<Kind> atPosition_filtered = atPosition.filtered(self_annotation);
                    return supported.containsAll(atPosition_filtered);
                }

            }

            return !Collections.disjoint(supported, atPosition);
        };

        if (direction == Direction.DOWN) {
            int leftmost  = this.getLeftmostXPosition();
            int rightmost = this.getRightmostXPosition();

            for (int i = leftmost; i <= rightmost; i++) {
                if (!canMoveTo.apply(i, getY() + 1)) {
                    return false;
                }

                //if (canMoveTo.apply(i, getY() + 1)) {
                //    return true;
                //}
            }

        } else if (direction == Direction.LEFT) {
            // todo we probably can simplify this if statement eventually
            if (!canMoveTo.apply(this.getLeftmostXPosition() - 1, getY())) {
                //if (true) throw new RuntimeException();
                return false;
            }

            //if (canMoveTo.apply(this.getLeftmostXPosition() - 1, getY())) {
            //    return true;
            //}

        } else if (direction == Direction.RIGHT) {
            // todo we probably can simplify this if statement eventually
            if (!canMoveTo.apply(getRightmostXPosition() + 1, getY())) {
                return false;
            }

            //if (canMoveTo.apply(getRightmostXPosition() + 1, getY())) {
            //    return true;
            //}
        }

        return true;
        //return false;
    }


    // the kinds of object this object can fall onto
    OptionSet<Kind> supportedMoveDestinationKinds() {
        return new OptionSet<>(Kind.BACKGROUND_EMPTY);
    }



    // Move the object by one in the specified direction
    // Override this in your subclass to support moving objects larger than 1x1
    // Call `commitMoveAndLockIfNecessary` with a block performing the actual moving
    // also: subclasses implementing `move` are responsible for checking whether the object is locked and aborting if necessary
    void move(Direction direction) {

        //System.out.format("will move %s\n", this);

        if (this.isLocked) {
            return;
        }

        if (detectCollisionAndMarkForDestructionIfNecessary(direction)) {
            //Util.logm("will abort move in direction %s (%s)\n", direction, this);
            return;
        }
        //Util.logm("will proceed move in direction %s (%s)\n", direction, this);

        commitMoveAndLockIfNecessary(() -> {
            switch (direction) {
                case DOWN:
                    setY(getY() + 1);
                    break;
                case LEFT:
                    setX(getX() - 1);
                    break;
                case RIGHT:
                    setX(getX() + 1);
                    break;
            }
        });
    }


    // return value indicates whether the object is about to collide and can't move
    boolean detectCollisionAndMarkForDestructionIfNecessary(Direction direction) {

        boolean canMove = canMove(direction);

        if (!canMove && (direction == Direction.RIGHT || direction == Direction.LEFT)) {
            int newX = getX() + (direction == Direction.RIGHT ? 1 : -1);

            // only mark for destruction if we were about to hit a wall
            if (GameManager.shared.kindsAtPosition(newX, getY()).contains(Kind.BACKGROUND_SURROUNDING)) {
                this.markForDeath();
                GameManager.shared.updatePositionOfObject(this);
                return true;
            }
        }

        return !canMove;
    }


    void commitMoveAndLockIfNecessary(Runnable moveHandler) {

        // 1. Remove the current object from the canvas
        for (int x = this.getLeftmostXPosition(); x <= this.getRightmostXPosition(); x++) {
            if (!(this instanceof SingleObject)) {
                continue;
            }

            Kind kind = this.kind;
            if (kind == null && this instanceof MultiObject) {
                int idx = x - this.getLeftmostXPosition();
                kind = ((MultiObject) this).parts.get(idx).kind;
            }

            final Kind __kind = kind;
            //System.out.format("remove from %s|%s\n", x, getY());
            GameManager.shared.mutateAtPosition(x, this.getY(), val -> val - __kind.rawValue);
        }

        // 2. call the move handler to actually update the coordinates of the current object
        moveHandler.run();

        //System.out.format("new: %s|%s\n", getX(), getY());

        // 3. update the position of the object on the canvas
        // this is a bit tricky bc we have to avoid updating the position
        // for single-node objects that are part of a multi-node object (eg parts of a branch)
        // but still have to update it for all other single-node objects (eg decoration)
        // to get around this at least somewhat elegant, we use annotations
        if (getClass().isAnnotationPresent(ObjectType.Tree.class) || this.getClass().isAnnotationPresent(ObjectType.Decoration.class)) {
            GameManager.shared.updatePositionOfObject(this);
        }

        // 4. re-draw the canvas
        GameManager.shared.draw();


        if (getY() == GameManager.shared.height - 2) {
            // last row
            this.lock();
        }

        //System.out.format("did move  %s\n", this);

        if (!canMove(Direction.DOWN)) {
            this.lock();
        }
    }

    protected void lock() {
        this.isLocked = true;
    }


    protected void markForDeath() {
        if (isIndependent) return;
        this.markedForDeath = true;
    }



    //
    // toString
    //

    String toString_kind() {
        return this.kind.toString();
    }


    @Override
    public String toString() {
        return String.format("<%s x=%s:%s y=%s isIndependent=%s kind=%s>", getClass().getName(), getLeftmostXPosition(), getRightmostXPosition(), getY(), isIndependent, toString_kind());
    }


}

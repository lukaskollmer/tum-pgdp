package task_05;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MultiObject extends Weihnachtsobjekt {

    protected final List<SingleObject> parts = new ArrayList<>();


    // actual width: 2 + 2*width
    // one left side, 2*width inner objects and 1 right side
    final int width;
    final int actualWidth;


    // Create a new instance of `MultiObject`
    // `width` specifies the width of the object (keep in mind that the actual width varies)
    // `kinds` specifies the types of the individual subnodes (either 1 or 3 types)
    MultiObject(int width, boolean repeatMiddleType, Weihnachtsobjekt.Kind... kinds) {
        super();
        if (kinds.length != 1 && kinds.length != 3) {
            throw new RuntimeException("You have to specify either 1 or 3 types");
        } else if (repeatMiddleType && kinds.length != 3) {
            throw new RuntimeException("you need 3 types `repeatMiddleType` is true");
        }

        this.width = width;
        this.actualWidth = 2 * width + 2;

        boolean isSingleType = kinds.length == 1;

        for (int i = 0; i < actualWidth; i++) {
            Weihnachtsobjekt.Kind kind;

            if (isSingleType) {
                kind = kinds[0];
            } else {
                if (repeatMiddleType) {
                    kind = kinds[1];
                } else if (i == 0) {
                    kind = kinds[0];
                } else if (i == actualWidth - 1) {
                    kind = kinds[2];
                } else {
                    kind = kinds[1];
                }
            }

            SingleObject singleObject = new SingleObject(kind);
            singleObject.setY(this.getY());
            singleObject.setX(this.getX() + i);

            parts.add(singleObject);
        }
    }


    @Override
    public int getRightmostXPosition() {
        return this.getX() + this.actualWidth - 1;
    }


    @Override
    void move(Direction direction) {
        if (this.isLocked) {
            return;
        }

        if (this.detectCollisionAndMarkForDestructionIfNecessary(direction)) {
            return;
        }


        Runnable action = () -> {

            // move the individual sub-nodes
            for (int i = 0; i < parts.size(); i++) {
                // iterate in reverse if we move right
                // this is important to prevent false collision detections
                int index = i;
                if (direction == Direction.RIGHT) {
                    index = parts.size() - i - 1;
                }
                parts.get(index).move(direction);
            }


            setX(parts.get(0).getX());
            setY(parts.get(0).getY());

        };

        commitMoveAndLockIfNecessary(action);
    }


    @Override
    protected void lock() {
        super.lock();

        for (SingleObject object : parts) {
            object.lock();
        }
    }

    @Override
    protected void markForDeath() {
        super.markForDeath();

        for (SingleObject object : parts) {
            object.markForDeath();
        }
    }

    @Override
    String toString_kind() {
        StringBuilder stringBuilder = new StringBuilder("[");

        for (int i = 0; i < parts.size(); i++) {
            stringBuilder.append(parts.get(i).kind.toString());
            if (i < parts.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }
}

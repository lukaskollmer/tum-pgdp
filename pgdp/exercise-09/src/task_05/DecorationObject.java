package task_05;


@ObjectType.Decoration
@ObjectType.Foreground
public class DecorationObject extends SingleObject {

    DecorationObject(Kind kind) {
        super(kind);
    }


    @Override
    OptionSet<Kind> supportedMoveDestinationKinds() {
        // in theory, decoration can go everywhere except these kinds
        return new OptionSet<>(
                Kind.FOREGROUND_BAUBLE,
                Kind.FOREGROUND_PENGUIN,
                Kind.FOREGROUND_SNOWFLAKE,
                Kind.BACKGROUND_SURROUNDING
        ).inverted(Kind.class);
    }

    @Override
    boolean canMove(Direction direction) {

        boolean whatDoesSuperSay = super.canMove(direction);

        if (!whatDoesSuperSay) {
            return false;
        }

        if (direction == Direction.DOWN) {
            // _in theory_, we can move down
            // however, there are some conditions we need to take into account
            // - locked object of same kind below
            // - currently on a branch, no branch anywhere below

            OptionSet<Kind> kindsInCurrentField = GameManager.shared.kindsAtPosition(getX(), getY());
            OptionSet<Kind> kindsInFieldBelow   = GameManager.shared.kindsAtPosition(getX(), getY() + 1);


            Kind[] decorationKinds = {Kind.FOREGROUND_SNOWFLAKE,  Kind.FOREGROUND_BAUBLE,       Kind.FOREGROUND_PENGUIN    };
            Kind[] treeBranchKinds = {Kind.BACKGROUND_GREEN_LEFT, Kind.BACKGROUND_GREEN_MIDDLE, Kind.BACKGROUND_GREEN_RIGHT};


            if (kindsInFieldBelow.containsAny(decorationKinds)) {
                return false;

            } else if (kindsInCurrentField.containsAny(treeBranchKinds)) {
                // check for all fields below whether they contain some sort of tree node

                boolean anyOfTheFieldsBelowContainsATreeBranch = false;

                for (int y = getY() + 1; y < GameManager.shared.height; y++) {
                    if (GameManager.shared.kindsAtPosition(getX(), y).containsAny(treeBranchKinds)) {
                        anyOfTheFieldsBelowContainsATreeBranch = true;
                        break;
                    }
                }

                return anyOfTheFieldsBelowContainsATreeBranch;
            }
        } else {
            // check whether we can move left or right
            int newX = getX();

            if (direction == Direction.LEFT) {
                newX -= 1;
            } else if (direction == Direction.RIGHT) {
                newX += 1;
            }

            OptionSet<Kind> kindsInNextField = GameManager.shared.kindsAtPosition(newX, getY());
            return kindsInNextField.contains(Kind.FOREGROUND_EMPTY);
        }

        return true;
    }


    @Override
    protected void lock() {
        if (!isIndependent) {
            super.lock();
            return;
        }

        if (getY() + 1 == GameManager.shared.height - 1) {
            super.lock();
        }
    }
}

package task_05;


@ObjectType.Tree
public class TreeTrunk extends MultiObject {

    TreeTrunk(int width, boolean repeatMiddle) {
        super(width, repeatMiddle, Kind.BACKGROUND_TRUNK_LEFT, Kind.BACKGROUND_TRUNK_MIDDLE, Kind.BACKGROUND_TRUNK_RIGHT);
    }


    // These are unused, but here bc they're required by the instructions

    static class TrunkLeft extends SingleObject {
        TrunkLeft() {
            super(Kind.BACKGROUND_TRUNK_LEFT);
        }
    }

    static class TrunkMiddle extends SingleObject {
        TrunkMiddle() {
            super(Kind.BACKGROUND_TRUNK_MIDDLE);
        }
    }

    static class TrunkRight extends SingleObject {
        TrunkRight() {
            super(Kind.BACKGROUND_TRUNK_RIGHT);
        }
    }
}

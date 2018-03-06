package task_05;

@ObjectType.Tree
public class TreeBranch extends MultiObject {

    TreeBranch(int width) {
        super(width, false, Kind.BACKGROUND_GREEN_LEFT, Kind.BACKGROUND_GREEN_MIDDLE, Kind.BACKGROUND_GREEN_RIGHT);
    }


    // These are unused, but here bc they're required by the instructions

    static class BranchLeft extends SingleObject {
        BranchLeft() {
            super(Kind.BACKGROUND_GREEN_LEFT);
        }
    }

    static class BranchMiddle extends SingleObject {
        BranchMiddle() {
            super(Kind.BACKGROUND_GREEN_MIDDLE);
        }
    }

    static class BranchRight extends SingleObject {
        BranchRight() {
            super(Kind.BACKGROUND_GREEN_RIGHT);
        }
    }
}

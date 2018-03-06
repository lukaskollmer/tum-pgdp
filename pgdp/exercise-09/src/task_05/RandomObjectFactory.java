package task_05;

public class RandomObjectFactory {

    public static Weihnachtsobjekt createRandomTreeObject() {
        // todo do we really need to create all these objects in advance? we surely can do better than this
        Weihnachtsobjekt[] objects = {
                new TreeTrunk(1, false),
                new TreeTrunk(0, true),
                new TreeBranch(7),
                new TreeBranch(6),
                new TreeBranch(5),
                new TreeBranch(4),
                new TreeBranch(3),
                new TreeBranch(2),
                new TreeBranch(1),
                new TreeBranch(0),
        };

        return Util.getRandom(objects);
    }

    public static Weihnachtsobjekt createRandomDecorationObject() {
        Weihnachtsobjekt[] objects = {
                new DecorationObject(Weihnachtsobjekt.Kind.FOREGROUND_BAUBLE),
                new DecorationObject(Weihnachtsobjekt.Kind.FOREGROUND_PENGUIN),
                new DecorationObject(Weihnachtsobjekt.Kind.FOREGROUND_SNOWFLAKE),
        };

        return Util.getRandom(objects);
    }


    public static Weihnachtsobjekt createRandomObjectForPhase(GameManager.Phase phase) {
        if (phase == GameManager.Phase.TreeCreation) {
            return createRandomTreeObject();
        } else if (phase == GameManager.Phase.TreeDecoration) {
            return createRandomDecorationObject();
        }

        return null;
    }

}
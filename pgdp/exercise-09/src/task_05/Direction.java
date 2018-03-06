package task_05;

public enum Direction implements OptionSet.Enum {
    LEFT, RIGHT, UP, DOWN;

    @Override
    public int getRawValue() {
        return this.ordinal();
    }
}
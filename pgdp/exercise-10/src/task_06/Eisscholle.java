package task_06;

public class Eisscholle {


    enum State {
        UNKNOWN,
        GUESS,
        KNOWN
    }

    private int distance;
    private final String name;
    private Eisscholle predecessor;
    private State state = State.UNKNOWN;

    public Eisscholle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public Eisscholle getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Eisscholle predecessor) {
        this.predecessor = predecessor;
    }


    public boolean equals(Eisscholle otherFloe) {
        return this.name.equals(otherFloe.name);
    }

    @Override
    public String toString() {
        return String.format("<Eisscholle name=%s state=%s distance=%s predecessor=%s >", name, state, distance, predecessor);
    }
}

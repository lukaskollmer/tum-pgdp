package task_06;

public class Seeweg {

    private int distance;
    private Eisscholle from;
    private Eisscholle to;


    public Seeweg(int distance, Eisscholle from, Eisscholle to) {
        this.distance = distance;
        this.from = from;
        this.to = to;
    }


    public int getDistance() {
        return distance;
    }

    public Eisscholle getFrom() {
        return from;
    }

    public Eisscholle getTo() {
        return to;
    }


    @Override
    public String toString() {
        return String.format("<Seeweg distance=%s from=%s to=%s>", distance, from, to);
    }
}

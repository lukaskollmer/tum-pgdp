
public class PriorityQueue<ComparableThing extends Comparable<ComparableThing>> {
  private final static int START_SIZE = 777;

  private int count = 0;
  private ComparableThing[] heap;

  public PriorityQueue() {
    this(PriorityQueue.START_SIZE);
  }

  public PriorityQueue(int startSize) {
    @SuppressWarnings("unchecked") // Generic array creation via raw types
    ComparableThing[] myHeap = (ComparableThing[]) new Comparable[startSize];
    this.heap = myHeap;
  }

  // Make the array great again
  private void trumpify() {
    resize(heap.length * 2 + 1);
  }

  // Make the array very small
  private void schulzify() {
    resize(heap.length / 2);
  }

  private void resize(int newArraySize) {
    @SuppressWarnings("unchecked") // Generic array creation via raw types
    ComparableThing[] newHeap = (ComparableThing[]) new Comparable[newArraySize];
    System.arraycopy(heap, 0, newHeap, 0, Math.min(heap.length, newHeap.length));
    heap = newHeap;
  }

  public int size() {
    // TODO
  }

  public void clear() {
    // TODO
  }

  public void add(ComparableThing newElement) {
    // TODO
  }

  public ComparableThing poll() {
    // TODO
  }

  public ComparableThing peek() {
    // TODO
  }


  private void up(int index) {
    // TODO
  }

  private void down(int index) {
    // TODO
  }


  public static void main(String[] args) {
    Test.main();
  }

}


class Penguin implements Comparable<Penguin> {

  private int cuddliness;

  public Penguin(int cuddliness) {
    this.cuddliness = cuddliness;
  }

  public int getCuddliness() {
    return this.cuddliness;
  }

  public int compareTo(Penguin other) {
    int oc = other.cuddliness;
    if (cuddliness < oc) return -1;
    if (cuddliness > oc) return 1;
    return 0;
  }
}


class Test {

  public static void main() {
    // Code zum Testen?
  }

}

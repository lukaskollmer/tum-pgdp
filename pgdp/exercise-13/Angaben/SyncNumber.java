
public class SyncNumber {
  private int c = 1;

  public synchronized int read() {
    return c;
  }

  public synchronized void write(int c) {
    this.c = c;
  }
}

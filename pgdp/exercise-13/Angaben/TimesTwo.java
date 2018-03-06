
public class TimesTwo implements Runnable {
  private final SyncNumber n;

  public TimesTwo(SyncNumber c) {
    n = c;
  }

  @Override
  public void run() {
    int i = n.read();
    n.write(i * 2);
    System.out.println("write " + i * 2);
  }

  public static void main(String[] args) {
    SyncNumber n = new SyncNumber();
    TimesTwo d1 = new TimesTwo(n);
    TimesTwo d2 = new TimesTwo(n);
    new Thread(d1).start(); // Thread 1
    new Thread(d2).start(); // Thread 2
  }

}

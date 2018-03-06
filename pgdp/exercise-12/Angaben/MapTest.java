import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapTest {
  private Fun<Integer, String> f;
  private Integer[] a;
  private String[] b;
  private String[] b_ok;

  @BeforeEach
  void init() {
    f = new IntToString();
    a = new Integer[42];
    for (int i = 0; i < a.length; ++i)
      a[i] = i;
    b = new String[a.length];
    b_ok = new String[a.length];
    for (int i = 0; i < b_ok.length; ++i)
      b_ok[i] = Integer.toString(i);
  }

  @Test
  void shouldThrow1() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Map.map(null, a, b, 2));
  }

  @Test
  void shouldThrow2() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Map.map(f, null, b, 2));
  }

  @Test
  void shouldThrow3() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Map.map(f, a, null, 2));
  }

  @Test
  void shouldThrow4() {
    String[] bb = new String[a.length - 1];
    Assertions.assertThrows(IllegalArgumentException.class, () -> Map.map(f, a, bb, 2));
  }

  @Test
  void shouldThrow5() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> Map.map(f, a, b, -1));
    Assertions.assertThrows(IllegalArgumentException.class, () -> Map.map(f, a, b, -123));
  }

  @Test
  void test() throws InterruptedException {
    for (int i = 1; i <= a.length; ++i) {
      init();
      Map.map(f, a, b, i);
      Assertions.assertArrayEquals(b, b_ok);
    }
  }

}

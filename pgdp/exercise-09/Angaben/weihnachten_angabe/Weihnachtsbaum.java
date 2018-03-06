import java.util.ArrayList;

/*
.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:.
    .     *                                       .
    .    /.\                                      .
    .   /..'\                                     .
    .   /'.'\                                     .
    .  /.''.'\                                    .
    .  /.'.'.\                                    .
    . /'.''.'.\                                   .
    . ^^^[_]^^^                                   .
    .                                             .
    .                                             .
    .jgs                                          .
    .:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:.
*/

public class Weihnachtsbaum extends BitteNichtAbgeben {

  private static final int[][] landscape = generateLandscape(30, 33);
  private static ArrayList<Weihnachtsobjekt> objekte = new ArrayList<>();
  private static boolean[][] staticObjects = new boolean[30][33];

  public static void keyPressed(int key) {
    draw(landscape);
    if (key != WeihnachtsElfen.NO_KEY) {
      System.out.print(key);
    }
  }

  /*********************************************/
  /* Ab hier soll nichts mehr geändert werden! */
  /*********************************************/

  public static void main(String[] args) {
    draw(landscape);
    handleUserInput();
  }

  private static void handleUserInput() {
    while(true) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ie) {
        /* Intentionally left blank */
      }
      int step = nextStep();
      if (step != NO_KEY) {
        // System.out.print(step+",");
        keyPressed(step);
      }
    }
  }
}

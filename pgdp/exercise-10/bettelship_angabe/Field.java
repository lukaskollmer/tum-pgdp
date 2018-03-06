import java.util.LinkedList;

public class Field {

   // Liste mit allen Schiffen
   private LinkedList<Ship> shipList;

   // Liste mit allen geratenen Feldern
   private LinkedList<Coordinate> guess;

   private void init() {

      // TODO Zufälliges Platzieren der Schiffe

   }

   public void play() {
      init();

      // TODO Spielen, bis ein Spieler gewonnen hat

   }

   // Erzeugt Zufallszahlen zwischen low und high-1 (jeweils inklusive).
   private static int myRandom(int low, int high) {
      return (int) (Math.random() * (high - low) + low);
   }



   // Aufruf mit true zeigt alle Schiffe an,
   // Aufruf mit false nur die schon geratenen Felder.
   public void printFields(boolean show) {
      System.out.println("\n  --------------------");
      for (int up = 9; up >= 0; up--) {
         for (int right = 1; right <= 24; right++) {
            int x=(right-3)/2;
            Coordinate current = new Coordinate(x,up);
            char next = ' ';
            if (right == 1) next = (""+up).charAt(0); else
            if (right == 2 || right == 22) next = '|';
            for (int shipNr = 0; shipNr < shipList.size(); shipNr++) {
               if (right%2==0) break;
               Ship ship = shipList.get(shipNr);
               if (show && ship.contains(current)) {
                  next='X'; break;
               }
               for (int i = 0; i < guess.size(); i++) {
                  if (!show && guess.get(i).equals(current)) {
                     next='W'; // Wasser
                  }
               }
               if (!show && ship.isHit(current)) {
                  next='T'; // Treffer
                  if (ship.isSunk()) next='V'; // Versenkt
                  break;
               }
            }
            System.out.print(next);
         }
         System.out.println();
      }
      System.out.println("  - - - - - - - - - -\n  0 1 2 3 4 5 6 7 8 9\n");
   }
}

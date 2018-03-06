public class Settest {

   private static boolean error;

   public static void main(String[] args) {
      error = false;
      immuSetTesten();
      if (error) {
         System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
         System.out.println("%%%%%%%%% !! FEHLER !! %%%%%%%%%%%%");
         System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
      } else {
         System.out.println("\n    Test Ended Successfully!\n");
      }
   }

   public static void immuSetTesten() {
      Main_Task_03.Set<String> menge1 = new Main_Task_03.Set<>();
      System.out.println("Menge 1: " + menge1.toString());
      Main_Task_03.Set<String> menge2 = menge1.add("Hallo");
      System.out.println("Menge 2: " + menge2.toString());
      Main_Task_03.Set<String> menge3 = menge2.add("Welt");
      System.out.println("Menge 3: " + menge3.toString());
      Main_Task_03.Set<String> menge4 = menge3.add("!");
      System.out.println("Menge 4: " + menge4.toString());
      Main_Task_03.Set<String> menge5 = menge4.add("Welt");
      System.out.println("Menge 5: " + menge5.toString());

      System.out.println("Menge 1: " + menge1.toString());
      System.out.println("Menge 2: " + menge2.toString());
      System.out.println("Menge 3: " + menge3.toString());
      System.out.println("Menge 4: " + menge4.toString());
      System.out.println("Menge 5: " + menge5.toString());
      try {
         if (menge1.isElement("irgendwas")) {
            fehler("...isElement(\"irgendwas\")...");
         }
      } catch (Exception e) {
         fehler(e.toString());
      }
      if (menge1.isEqual(menge5)) {
         fehler("--> Fehler: Menge 1 und 5 sollten unterschiedlich sein.");
      } else {
         System.out.println("Menge 1 und 5 sind unterschiedlich.");
      }
      if (!menge4.isEqual(menge5)) {
         fehler("--> Fehler: Menge 4 und 5 sollten gleich sein.");
      } else {
         System.out.println("Menge 4 und 5 sind gleich.");
      }

      Main_Task_03.Set<String> menge6 = menge2.add("!");
      System.out.println("Menge 6: " + menge6.toString());
      if (!menge6.superset(menge2)) {
         fehler("--> Fehler: Menge 6 sollte eine Obermenge von Menge 2 sein.");
      } else {
         System.out.println("Menge 6 ist Obermenge von Menge 2.");
      }
      if (menge6.superset(menge3)) {
         fehler("--> Fehler: Menge 6 sollte keine Obermenge von Menge 3 sein.");
      } else {
         System.out.println("Menge 6 ist keine Obermenge von Menge 3.");
      }
      if (menge3.superset(menge6)) {
         fehler("--> Fehler: Menge 3 sollte keine Obermenge von Menge 6 sein.");
      } else {
         System.out.println("Menge 3 ist keine Obermenge von Menge 6.");
      }
      Main_Task_03.Set<String> menge7 = menge6.add("Welt");
      System.out.println("Menge 7: " + menge7.toString());
      if (!menge7.isEqual(menge5)) {
         fehler("--> Fehler: Menge 5 und 7 sollten gleich sein.");
      } else {
         System.out.println("Menge 5 und 7 sind gleich.");
      }
   }

   public static void fehler(String meldung) {
      System.out.println("%%%%%%%%%%%% Fehler %%%%%%%%%%%%%%%");
      System.out.println(meldung);
      System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
      error = true;
   }
}


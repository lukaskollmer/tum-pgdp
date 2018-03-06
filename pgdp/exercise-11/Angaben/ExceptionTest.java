import java.io.*;
public class ExceptionTest {
   public static void main (String[] args) {
      try {
         if (args.length == 0)
            throw new EOFException();
         if (args.length == 1)
            throw new FileNotFoundException();
         if (args.length == 3)
            System.out.println(24/(3-args.length));
      }
      catch (EOFException e) {
         System.out.println("EOFException");
      }
      catch (IOException e) {
         System.out.println("IOException");
      }
      catch (Exception e) {
         System.out.println("Exception");
      }
      System.out.println("ENDE");
   }
}

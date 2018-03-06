/*
 * exercise/task-03
 * Request integer array from user, filter smallest & largest element
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   20XX-11-09
 */


public class Main extends MiniJava {

    public static void main(String[] args) {
        // Create the array
        int size = readInt("array size?");
        
        int[] array = new int[size];
        
        for (int i=0; i < size; i++) {
            array[i] = readInt(String.format("Enter the number for index #%s", i));
        }
        
        
        // Get the smallest and largest element
        int smallest = Integer.MAX_VALUE;
        int largest  = Integer.MIN_VALUE;
        
        for (int i = 0; i < size; i++) {
            int value = array[i];
            
            if (value < smallest) {
                smallest = value;
            } else if (value > largest) {
                largest = value;
            }
        }
        
        
        System.out.format("smallest: %s, largest: %s", smallest, largest);
        
    }
}

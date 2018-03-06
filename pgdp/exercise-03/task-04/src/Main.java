/*
 * exercise-03/task-04
 * DESCRIPTION
 *
 * @author  Lukas Kollmer <lukas.kollmer@gmail.com>
 * @version 1.0
 * @since   20XX-XX-XX
 */

import java.util.*;

public class Main extends MiniJava {

    public static void main(String[] args) {
        
        int numberOfRows = 5;
        
        ArrayList<ArrayList<Integer>> triangle = new ArrayList<>();
        
        
        for (int i = 0; i < numberOfRows; i++) {
            int numberOfElementsInRow = i + 1;
            
            ArrayList<Integer> numbersInRow = new ArrayList<>();
            
            for (int j = 0; j < numberOfElementsInRow; j++) {                
                boolean isFirstOrLast = j == 0 || j == numberOfElementsInRow - 1;
                int value = isFirstOrLast ? 1 : triangle.get(i - 1).get(j);
                
                if (!isFirstOrLast) {
                    value += triangle.get(i - 1).get(j - 1);
                }
                numbersInRow.add(value);
            }
            triangle.add(numbersInRow);
        }
        
        
        for (int i = 0; i < triangle.size(); i++) {
            System.out.format("%s: %s\n", i, triangle.get(i));
        }
        
        
    }
}

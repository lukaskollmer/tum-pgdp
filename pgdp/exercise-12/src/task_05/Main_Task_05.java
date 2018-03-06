package task_05;

import java.util.Arrays;

public class Main_Task_05 {
    public static void main(String... args) {

        BinaryTree<Integer> tree = new BinaryTree<>();

        /*tree.insert(2);
        tree.insert(1);
        tree.insert(7);
        tree.insert(4);
        tree.insert(3);
        tree.insert(5);
        tree.insert(8);
        tree.insert(2);*/

        Arrays.asList(33, 5, 77, 9, 76, 85, 17, 73, 84, 39).forEach(tree::insert);


        System.out.format("=====\n%s\n=====\n", tree.toString());


        tree.remove(73);


        System.out.format("\n\n\n\n");
        System.out.format("=====\n%s\n=====\n", tree.toString());
    }
}

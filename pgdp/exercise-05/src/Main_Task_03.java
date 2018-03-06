import java.util.Arrays;

public class Main_Task_03 {
    public static void main(String... args) {
        int[] input = {1, 2, 3, 4, 6};
        print(input);
        System.out.println(Arrays.toString(invert(input)));
        System.out.println(Arrays.toString(cut(input, 15)));
        System.out.println(Arrays.toString(linearize(new int[][] {{1, 3}, {25}, {7, 4, 6, 9}})));
    }


    static void print(int[] array) {
        if (array.length == 0) {
            System.out.println("{}");
            return;
        }

        System.out.print("{");

        for (int i = 0; i < array.length; i++) {
            boolean isLast = i == array.length - 1;
            System.out.format("%s%s", array[i], isLast ? "" : ", ");
        }
        System.out.print("}\n");
    }

    static int[] invert(int[] array) {
        int[] result = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            result[array.length - 1 - i] = array[i];
        }
        return result;
    }

    static int[] cut(int[] array, int length) {
        int[] result = new int[length];

        for (int i = 0; i < length && i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    static int[] linearize(int[][] array) {
        int length = 0;

        for (int[] subarray : array) {
            length += subarray.length;
        }

        int[] result = new int[length];


        int index = 0;
        for (int[] subarray : array) {
            for (int element : subarray) {
                result[index] = element;
                index++;
            }
        }

        return result;
    }
}

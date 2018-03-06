import java.util.Arrays;

public class Main_Task_04 {
    public static void main(String... args) {
        System.out.println(vecVecMul(new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4}));

        //System.out.println(Arrays.toString(matVecMul(...)));
    }


    static int vecVecMul(int[] a, int[] b) {
        int result = 0;

        for (int i = 0; i < a.length && i < b.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    static int[] matVecMul(int[][] a, int[] b) {
        int[] result = new int[a.length];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i] = a[i][j] * b[j];
            }
        }

        return result;
    }
}

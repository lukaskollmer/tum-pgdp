


public class Main_Task_03 {
    public static void main(String... args) {
        System.out.println(fn(1000));
    }


    static double fn(double n) {
        if (n == 0) { return 4; }

        if (n % 2 == 0) { // even
            return 4 / (2 * n + 1) + fn(n - 1);
        } else { // odd
            return (-4) / (2 * n + 1) + fn(n - 1);
        }
    }
}

public class Main_Task_05 {
    public static void main(String... args) {
        System.out.format("frec: %s\n", frec(50));
    }

    static int frec(int x) {
        return g(x, 0);
    }


    static int ftailrec(int x) {
        return -1;
    }


    static int g(int x, int y) {
        if (x < 10) {
            return (int) Math.pow(x, y);
        }

        return (int) (Math.pow(x % 10, y) + g(x/10, y + 1));
    }
}

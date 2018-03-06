public class Main_Task_04 {
    public static void main(String... args) {
        System.out.format("fak_rec      : %s\n", fak_rec(5));
        System.out.format("fak_tail_rec : %s\n", fak_tail_rec(5));
    }

    static int fak_rec(int value) {
        if (value == 1) {
            return 1;
        }
        return value * fak_rec(value - 1);
    }

    static int fak_tail_rec(int value) {

        return fak_tail_rec(value - 1);
    }

    static int fak_tail_rec_hepler(int value, int temp) {
        return 0; // todo implement
    }
}

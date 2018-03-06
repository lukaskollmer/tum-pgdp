public class Main_Task_02 {

    public static void main(String[] args) {

        System.out.println(sqrt(4.0, 0.000001));

    }

    static double sqrt(double input, double epsilon) {
        return sqrt_imp(input, input, epsilon);
    }

    static double sqrt_imp(double input, double x, double epsilon) {
        if (x * x - input > epsilon) {
            return sqrt_imp(input, (x + input / x) * 0.5, epsilon);
        }

        return x;
    }

    static double sqrt_old(double x, double epsilon) {
        double tmp = x;

        while (tmp * tmp - x > epsilon) {
            tmp = (tmp + x / tmp) * 0.5;
        }

        return tmp;
    }
}

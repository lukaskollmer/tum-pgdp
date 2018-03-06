/*
 * exercise-06/task-06
 *
 * Solving third-degree polynomials
 * */


import java.util.Arrays;

public class Main_Task_06 {
    public static void main(String... args) {

        /*
         * Example formulas:
         * - 1x^3   + 4x^2 - 11x   - 30   (roots: -5, -2, 3)
         * - 0.5x^3 + 5x^2 - 33.5x - 308  (roots: 8, -7, -11)
         * - 0x^3   + 2x^2 + 0x^1  - 10   (roots -2, 2)
         * */

        double[] coefficients = new double[4];

        for (int i = 0; i < 4; i++) {
            coefficients[i] = MiniJava.readDouble(String.format("?x^%s", 3 - i));
        }
        Formula f = new Formula(coefficients);

        System.out.format("formula: %s\n", f);

        MiniJava.write(String.format("roots: %s", Arrays.toString(f.findAllRoots())));
    }





    // Determine whether two values have the same sign (-/+)
    static boolean haveSameSign(double a, double b) {
        return a == 0 && b == 0 || a > 0 && b > 0 || a < 0 && b < 0;
    }



    // A formula w/ either three or four coefficients (including the last one w/out an x)
    private static class Formula {
        double[] coefficients;

        Formula(double... coefficients) {
            // we check whether a_0 is 0
            // if it is, we can treat the entire formula as a second grade polynomial
            if (coefficients[0] == 0) {
                this.coefficients = new double[coefficients.length - 1];

                for (int i = 1; i < coefficients.length; i++) {
                    this.coefficients[i-1] = coefficients[i];
                }
            } else {
                this.coefficients = coefficients;
            }
        }

        // Calculate the function and return the result
        double invoke(int x) {
            return calculateY(this.coefficients, x);
        }


        // Simplify a formula w/ 4 coefficients into a formula w/ only 3 coefficients
        Formula simplify(int knownRoot) {
            if (this.coefficients.length != 4) {
                return this;
            }

            return new Formula(hornerSchema(this.coefficients, knownRoot));
        }

        int findRoot() {
            int[] rootInterval = findIntervalRecursive(this.coefficients, -2, 2, 10);

            return findRootRecursive(this.coefficients, rootInterval[0], rootInterval[1]);
        }

        int[] findAllRoots() {

            int[] roots;

            if (this.coefficients.length == 4) {
                int firstRoot = findRoot();
                int[] otherRoots = quadraticFormula(simplify(firstRoot).coefficients);

                roots = new int[otherRoots.length + 1];
                roots[0] = firstRoot;

                for (int i = 0; i < otherRoots.length; i++) {
                    roots[i + 1] = otherRoots[i];
                }
            } else {
                roots = quadraticFormula(this.coefficients);
            }

            return roots;
        }


        @Override
        public String toString() {
            StringBuilder description = new StringBuilder();
            for (int i = 0; i < this.coefficients.length; i++) {
                double value = this.coefficients[i];
                description.append(value < 0 ? " " : i == 0 ? "" :  " + ");
                description.append(String.format("%sx^%s", value, this.coefficients.length - i - 1));
            }
            return String.format("<Formula %s>", description.toString());
        }
    }





    //
    // Required functions from the instructions
    //


    static double[] hornerSchema (double[] coefficients, int x0) {
        double[] ret = new double[3];

        ret[0] = coefficients[0];

        // we start at 1 bc the first element has already been set
        for (int i = 1; i < 3; i++) {
            ret[i] = coefficients[i] + ret[i-1] * x0;
        }

        return ret;
    }


    static int[] findIntervalRecursive(double[] coefficients, int a, int b, int factor) {
        Formula f = new Formula(coefficients);

        if (!haveSameSign(f.invoke(a), f.invoke(b))) {
            return new int[]{a, b};
        } else {
            return findIntervalRecursive(coefficients, a * factor, b * factor, factor);
        }
    }


    static int findRootRecursive(double[] coefficients, int a, int b) {
        Formula f = new Formula(coefficients);
        int m = (a+b)/2;

        double a_res = f.invoke(a);
        double b_res = f.invoke(b);
        double m_res = f.invoke(m);

        if (a_res == 0) return a;
        if (b_res == 0) return b;
        if (m_res == 0) return m;

        if (!haveSameSign(a_res, m_res)) {
            return findRootRecursive(coefficients, a, m);
        } else if (!haveSameSign(m_res, b_res)) {
            return findRootRecursive(coefficients, m, b);
        }

        // should never reach here
        return -1;
    }


    // Mitternachtsformel
    static int[] quadraticFormula(double[] formula) {
        double a = formula[0];
        double b = formula[1];
        double c = formula[2];

        double x1 = (-b + (Math.sqrt(Math.pow(b, 2) - 4 * a * c))) / (2 * a);
        double x2 = (-b - (Math.sqrt(Math.pow(b, 2) - 4 * a * c))) / (2 * a);

        return new int[]{(int)x1, (int)x2};
    }

    static double calculateY(double[] coefficients, int x) {
        double result = 0;
        for (int i = 0; i < coefficients.length; i++) {
            double value = coefficients[i];
            double pow   = coefficients.length - i - 1;

            if (i == coefficients.length - 1) {
                // If this is the last coefficient (the one w/out an x, which basically is x^0), just add that to the result
                result += value;
            } else {
                result += (value / Math.abs(value)) * (Math.abs(value) * Math.pow(x, pow));
                /*        < --------- (a) ------- >   < ------------- (b) -------------- >  */
                // What's going on above?
                // (a) account for the fact that the coefficient might be negative (`n / abs(n)` is 1 if n is positive and -1 if n is negative)
                // (b) make the coefficient positive and multiply it w/ the n-th power of x

            }
        }
        return result;
    }
}

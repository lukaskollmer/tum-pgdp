import java.util.Arrays;

public class Main_Task_03 {
    public static void main(String... args) {

        System.out.format("evenSum: %s\n", evenSum(8));
        System.out.format("mul: %s\n", mul(7, 6));

        int[] array = {1, 2, 3, 4, 5};
        reverse(array);
        System.out.format("reverse: %s\n", Arrays.toString(array));

        System.out.format("n_odd: %s\n", n_odd(array));

        System.out.format("filter_oddd: %s\n", Arrays.toString(filterOdd(array)));
    }


    static int evenSum(int n) {
        if (n == 0) {
            return 0;
        }

        boolean isNegative = n < 0;

        if (isNegative) {
            n = n * -1;
        }

        int result = evenSum(n, 0);

        if (isNegative) {
            return result * -1;
        } else {
            return result;
        }
    }

    static int evenSum(int n, int acc) {
        int val = n;
        if (n % 2 != 0) {
            val = 0;
        }

        if (n == 0) {
            return acc;
        }

        return evenSum(n - 1, acc + val);
    }



    static int mul(int x, int y) {
        return mul(x, y, 0);
    }

    static int mul(int inc, int idx, int acc) {
        if (idx == 0) {
            return acc;
        }

        return mul(inc, idx - 1, acc + inc);
    }



    static void reverse(int[] m) {
        reverse(m, 0);
    }

    static void reverse(int[] array, int index) {
        if (index >= array.length / 2) {
            return;
        }
        int reverseIndex = array.length - 1 - index;

        int temp = array[index];
        array[index] = array[reverseIndex];
        array[reverseIndex] = temp;

        reverse(array, index + 1);
    }



    static int n_odd(int[] array) {
        return n_odd(array, 0, 0);
    }

    static int n_odd(int[] array, int idx, int acc) {
        if (idx == array.length) {
            return acc;
        }

        if (array[idx] % 2 == 0) {
            acc++;
        }

        return n_odd(array, idx + 1, acc);
    }



    static int[] filterOdd(int[] array) {
        return filterOdd(array, 0, new int[]{});
    }


    static int[] filterOdd(int[] input, int idx, int[] acc) {
        if (idx == input.length) {
            return acc;
        }

        int value = input[idx];

        if (value % 2 != 0) {
            int[] newAcc = new int[acc.length + 1];

            copy_array(newAcc, acc);
            newAcc[newAcc.length - 1] = value;

            acc = newAcc;
        }

        return filterOdd(input, idx + 1, acc);
    }


    static void copy_array(int[] a, int[] b) {
        copy_array(a, b, 0);
    }


    static void copy_array(int[] a, int[] b, int idx) {
        int size = a.length;
        if (b.length < size) {
            size = b.length;
        }

        if (idx >= size) {
            return;
        }
        a[idx] = b[idx];

        copy_array(a, b, idx + 1);
    }
}

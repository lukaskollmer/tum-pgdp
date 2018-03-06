

import utils.*;

public class Main_Task_02 {

    public static void main(String... args) {
        String input = "Hello Students! .aAbBcC? >wWxXyYzZ<";

        int shift = 3;

        for (int i = 0; i < input.length(); i++) {
            StringUtil.CharacterCase characterCase = StringUtil.getCase(input.charAt(i));
            if (characterCase == StringUtil.CharacterCase.INVALID) {
                continue;
            }

            char shifted = (char) (input.charAt(i) + shift);

            boolean isOutOfBounds = (characterCase == StringUtil.CharacterCase.LOWER && shifted > 'z')
                    || (characterCase == StringUtil.CharacterCase.UPPER && shifted > 'Z');

            if (isOutOfBounds) {
                shifted -= 26;
            }

            input = StringUtil.replacing(input, i, String.valueOf(shifted));
        }

        System.out.println(input);
        System.out.println(input.equals("Khoor Vwxghqwv! .dDeEfF? >zZaAbBcC<"));

    }
}

import utils.StringUtil;

public class Main_Task_04 {
    public static void main(String... args) {
        String input = "Hello Students!";


        String result = "";

        for (int i = 0; i < input.length(); i++) {
            Character c = input.charAt(i);
            StringUtil.CharacterCase oldCase = StringUtil.getCase(c);
            StringUtil.CharacterCase newCase = oldCase == StringUtil.CharacterCase.LOWER ? StringUtil.CharacterCase.UPPER : StringUtil.CharacterCase.LOWER;
            result += StringUtil.makeCase(String.valueOf(c), newCase);
        }

        System.out.format("result: %s\n", result);
    }
}

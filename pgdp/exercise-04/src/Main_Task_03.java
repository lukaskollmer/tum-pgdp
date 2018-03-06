import utils.StringUtil;
import java.util.ArrayList;

public class Main_Task_03 {
    public static void main(String... args) {
        ArrayList<Character> vocals = new ArrayList<>();
        vocals.add('a');
        vocals.add('e');
        vocals.add('i');
        vocals.add('o');
        vocals.add('u');


        String input = "Hat der alte Hexenmeister\n" +
                "sich doch einmal wegbegeben!\n" +
                "Und nun sollen seine Geister\n" +
                "auch nach meinem Willen leben.\n" +
                "Seine Wort und Werke\n" +
                "merkt ich und den Brauch,\n" +
                "und mit Geistesstärke\n" +
                "tu ich Wunder auch.\n" +
                "Walle! walle\n" +
                "Manche Strecke,\n" +
                "daß, zum Zwecke,\n" +
                "Wasser fließe\n" +
                "und mit reichem, vollem Schwalle\n" +
                "zu dem Bade sich ergieße.";

        for (int i = 0; i < input.length(); i++) {
            if (vocals.contains(StringUtil.makeLowerCase(input).charAt(i))) {
                String replacement = StringUtil.getCase(input.charAt(i)) == StringUtil.CharacterCase.LOWER ? "o" : "O";
                input = StringUtil.replacing(input, i, replacement);
            }
        }

        System.out.println(input);
    }
}

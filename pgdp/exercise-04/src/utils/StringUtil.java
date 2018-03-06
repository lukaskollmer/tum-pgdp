package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

    //
    // Characters, lower/upper case
    //

    // Enum indicating whether a character is lowercase or uppercase
    public enum CharacterCase {
        LOWER, UPPER, NUMBER, INVALID
    }

    // Get the case of the single-character String `string`
    public static CharacterCase getCase(String string) {
        if (string.length() != 1) {
            return CharacterCase.INVALID;
        }
        return getCase(string.charAt(0));
    }

    // Get the case of the char `character`
    public static CharacterCase getCase(char character) {

        List<Character> lowercaseUmlaute = Arrays.asList('ä', 'ö', 'ü');
        List<Character> uppercaseUmlaute = Arrays.asList('Ä', 'Ö', 'Ü');

        if ('a' <= character && character <= 'z' || lowercaseUmlaute.contains(character)) {
            return CharacterCase.LOWER;
        } else if ('A' <= character && character <= 'Z' || uppercaseUmlaute.contains(character)) {
            return CharacterCase.UPPER;
        } else if ('0' <= character && character <= '9') {
            return CharacterCase.NUMBER;
        }

        return CharacterCase.INVALID;
    }

    // Transform `string` into the specified character case
    public static String makeCase(String string, CharacterCase characterCase) {
        switch (characterCase) {
            case LOWER:
                return makeLowerCase(string);
            case UPPER:
                return makeUpperCase(string);
            default:
                return string;
        }
    }

    // Turn all lower case characters in the string into upper case
    public static String makeUpperCase(String string) {
        return mutateCharacterAsciiCodes(string, c -> c - 32, CharacterCase.LOWER);
    }


    // Turn all upper case characters in the string into lower case
    public static String makeLowerCase(String string) {
        return mutateCharacterAsciiCodes(string, c -> c + 32, CharacterCase.UPPER);
    }



    private interface CharacterMutationBlock {
        int mutate(int input);
    }

    // Create a new String from `string` by applying `mutationBlock` to each of the string's characters, if the chraracter's case is `applicableCharacterCase`
    static private String mutateCharacterAsciiCodes(String string, CharacterMutationBlock mutationBlock, CharacterCase applicableCharacterCase) {
        String newString = "";
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);

            if (StringUtil.getCase(character) == applicableCharacterCase) {
                newString += Character.toString((char) mutationBlock.mutate(character));
            } else {
                newString += Character.toString(character);
            }
        }
        return newString;
    }


    public static String capitalizeFirstCharacter(String string) {
        String firstCharacter = StringUtil.getCharacterAtIndex(string, 0);
        return StringUtil.replacing(string, 0, StringUtil.makeCase(firstCharacter, StringUtil.CharacterCase.UPPER));
    }



    //
    // Find + Replace
    //

    // Class representing a Range
    public static class Range {
        public int location;
        public int length;

        public Range(int location, int length) {
            this.location = location;
            this.length = length;
        }

        public String toString() {
            return String.format("<Range location: %s, length: %s>", location, length);
        }
    }

    // Find all occurrences of `substring` in `string`
    // Returns the ranges of the individual substrings
    public static ArrayList<Range> find(String string, String substring) {
        ArrayList<Range> ranges = new ArrayList<>();

        for (int i = 0; i < string.length(); i++) {
            Range range = null;
            for (int j = 0; j < substring.length(); j++) {

                if ((i + substring.length()) >= string.length()) {
                    break;
                }

                if (string.charAt(i+j) == substring.charAt(j)) {
                    if (range == null) {
                        range = new Range(i, 0);
                    }
                } else {
                    break;
                }
            }

            if (range != null) {
                range.length = substring.length();
                ranges.add(range);
            }
        }
        return ranges;
    }


    // Check whether a `Range` is within the bounds of `string`
    static boolean rangeIsWithinBoundsOfString(String string, Range range) {
        return range.location + range.length <= string.length();
    }


    // Get the substring within the specified range. Returns null if the range exceeds the bounds of the string
    public static String getSubstringAtRange(String string, Range range) {
        if (!rangeIsWithinBoundsOfString(string, range)) {
            return null; // TODO does this make a problem in our code?
        }

        ArrayList<Character> characters = new ArrayList<>();
        for (int i = 0; i < range.length; i++) {
            characters.add(string.charAt(range.location + i));
        }

        return Functional.reduce(characters, "", (acc, c) -> acc += Character.toString(c));
    }

    // Get the character at `index`. Returns null if `index` exceeds the bounds of the string
    static String getCharacterAtIndex(String string, int index) {
        return getSubstringAtRange(string, new Range(index, 1));
    }


    // Split a String into an Array of substrings
    public static ArrayList<String> split(String string, String separator) {
        ArrayList<String> elements = new ArrayList<>();
        ArrayList<Range> ranges = StringUtil.find(string, separator);

        if (ranges.size() == 0) {
            elements.add(string);
            return elements;
        }

        int lastLocation = 0;
        int index = 0;

        // skip the first element if `string` starts w/ `separator`
        Range firstRange = ranges.get(0);
        if (firstRange.location == 0) {
            index = 1;
            lastLocation = firstRange.length;
        }

        for (; index < ranges.size(); index++ ) {
            Range range = ranges.get(index);
            elements.add(getSubstringAtRange(string, new Range(lastLocation, range.location - lastLocation)));
            lastLocation = range.location + range.length;
        }

        // Add the substring between the end of the last separator and the end of the string
        Range lastRange = ranges.get(ranges.size() - 1);
        elements.add(getSubstringAtRange(string, new Range(lastRange.location + lastRange.length, string.length() - 1 - lastRange.location - separator.length())));


        return elements;
    }


    // Create a new String by replacing the character at index with the character `replacement`
    // `replacement` should be a single-character string
    public static String replacing(String string, Integer index, String replacement) {
        return replacing(string, index, replacement.charAt(0));
    }

    // Create a new String by replacing the character at index with the character `replacement`
    static String replacing(String string, Integer index, Character character) {
        String newString = "";

        for (int i = 0; i < string.length(); i++) {
            newString += (i == index) ? character : string.charAt(i);
        }

        return newString;
    }


    // Reverse `string`
    public static String reverse(String string) {
        String newString = "";

        int size = string.length();
        for (int i = 0; i < size; i++) {
            int reverseIndex = size - 1 - i;
            newString += StringUtil.getCharacterAtIndex(string, reverseIndex);
        }

        return newString;
    }
}

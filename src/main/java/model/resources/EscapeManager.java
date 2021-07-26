package model.resources;

/**
 * Created by tmn7 on 12/27/17.
 */
public class EscapeManager {

    private static final Character[] CHARS_THAT_NEED_ESCAPING = {'|','.','\\','?','*','+','&',':','{','}','[',']','(',')','^','$'};


    public static String escapeChar(Character charToEscape, Character prevChar) {

        String result = charToEscape.toString();
        String previous = prevChar.toString();

        for (int i = 0; i < CHARS_THAT_NEED_ESCAPING.length; i++) {

            if (CHARS_THAT_NEED_ESCAPING[i].equals(charToEscape)) {

                if (prevChar == Character.MIN_VALUE) {
                    result = "\\" + result;
                    break;
                } else {
                    if (!previous.equals("\\")) {
                        result = "\\" + result;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static String escapeString(final String stringToEscape) {
        StringBuilder sb = new StringBuilder();

        Character prevChar = Character.MIN_VALUE;
        for (Character c : stringToEscape.toCharArray()) {
            sb.append(escapeChar(c, prevChar));
            prevChar = c;
        }

        return sb.toString();
    }

    public static String removeBackSlashes(final String str) {
        return str.replace("\\","");
    }
}

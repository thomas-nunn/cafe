package controller.file;

import model.resources.EscapeManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tmn7 on 7/4/18.
 */
public class DelimitedCntrl {

    FileCntrl fileCntrl;
    private List<Character> delimiters;


    public DelimitedCntrl(FileCntrl fileCntrl) {

        this.fileCntrl = fileCntrl;
        setDelimiters();
    }

    /**
     * Loads delimiters with the most common flatfile delimiters.
     */
    private void setDelimiters() {

        delimiters = new ArrayList<>();
        delimiters.add(',');
        delimiters.add(';');
        delimiters.add('|');
        delimiters.add('\t');
    }

    /**
     * Reads the first 10 lines in textArea to try to determine what the
     * delimiter is. The Character in delimiters with the most occurrences in
     * those 10 lines will be returned as the flatfile delimiter.
     *
     * @param textArea The JTextArea holding a file's text
     * @return The delimiter from delimiters or Character.MIN_VALUE
     */
    public Character getFlatfileDelimiter(final JTextArea textArea) {

        Character delimiter = Character.MIN_VALUE;
        int maxArraySize = 0;
        int linesWithDelim = 0;
        List<String> linesToTest = new ArrayList<>();
        Pattern commentLinePattern = Pattern.compile("^[^\\w].*"); // regex for first char of line is a non-word char.


        // load linesToTest with up to 10 non-comment lines
        for (String line : textArea.getText().split("\\n")) {

            Matcher matcher = commentLinePattern.matcher(line);

            if (!matcher.matches()) {
                linesToTest.add(line);
            }

            if (linesToTest.size() >= 10){
                break;
            }
        }

        // determine the delimiter by selecting the value from delimiters with the most occurrences
        for (String ln : linesToTest) {
            for (Character delim : delimiters) {

                String[] splitArray = ln.split(EscapeManager.escapeChar(delim, Character.MIN_VALUE));
                int currentArraySize = splitArray.length;

                if (currentArraySize > maxArraySize) {
                    maxArraySize = currentArraySize;
                    delimiter = delim;
                }
            }
        }

        if (delimiter != Character.MIN_VALUE) {

            // count lines that have the delimiter
            for (String ln : linesToTest) {
                if (ln.contains(delimiter.toString())) {
                    linesWithDelim++;
                }
            }

            if (linesWithDelim < 2) delimiter = Character.MIN_VALUE;
        }

        return delimiter;
    }

    /**
     * Parses a flatfile line and adds each element to an ArrayList.
     * Delimiters that are surrounded by double quotes are ignored.
     *
     * @param str The String to parse
     * @return A List<String>
     */
    public List<String> parseString(final String str, final Character myDelimiter) {

        List<String> result = new ArrayList<>();
        int start = 0;
        boolean inQuotes = false;

        for (int i = 0; i < str.length(); i++) {

            boolean atLastChar = (i == str.length() - 1);

            if (str.charAt(i) == '\"') {
                inQuotes = !inQuotes; // toggle state
            }

            if (atLastChar) {

                result.add(str.substring(start, i));
                if (str.charAt(i) == myDelimiter)
                    result.add("");

            } else if (str.charAt(i) == myDelimiter && !inQuotes) {
                result.add(str.substring(start, i));
                start = i + 1;
            }
        }

        if (result.isEmpty()) {
            result.add(str);
        }

        return result;
    }
}
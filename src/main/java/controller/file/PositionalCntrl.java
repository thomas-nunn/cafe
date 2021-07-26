package controller.file;

import model.file.PositionalSchemaData;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmn7 on 9/16/18.
 */
public class PositionalCntrl {

    private final FileCntrl myFileCntrl;
    private static final Integer DUMMY_Y_VALUE = -99;


    public PositionalCntrl(FileCntrl fileCntrl) {
        myFileCntrl = fileCntrl;
    }

    /**
     * Splits a String based on schema and stores
     * in a List.
     *
     * @param lineOfFile The String line of a File
     * @return The List of positional entry fields
     */
    public List<String> getPositionalRowAsList(final String lineOfFile, final List<Point> fieldDims) {

        List<String> positionalList = new ArrayList<>();
        int lineLength = lineOfFile.length();
        int prevEndIndex = 0;

        for (Point p : fieldDims) {

            if (p.y == DUMMY_Y_VALUE) { // p.x will be the width of the column

                int endIndex = prevEndIndex + p.x;

                if (lineLength >= endIndex) {
                    positionalList.add(lineOfFile.substring(prevEndIndex,endIndex));
                } else {

                    if (lineLength > prevEndIndex) {
                        positionalList.add(lineOfFile.substring(prevEndIndex,lineLength));
                    } else {
                        positionalList.add("");
                    }
                }
                prevEndIndex = endIndex;

            } else { // p.x is starting index and p.y is ending index

                if (lineLength >= p.y) {
                    positionalList.add(lineOfFile.substring(p.x, p.y));
                } else {

                    if (lineLength > p.x) {
                        positionalList.add(lineOfFile.substring(p.x, lineLength));
                    } else {
                        positionalList.add("");
                    }
                }
            }
        }

        return positionalList;
    }

    /**
     * Schemas must be in one of two forms.
     * 1st: columnName,start(inclusive),end(exclusive)
     * 2nd: columnName,width
     *
     * @return true if schema conforms.
     */
    public boolean validateSchema(final File schemaFile) {

        boolean result = true;
        String line;
        int lineCount = 0;
        int columnCount = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(schemaFile))) {

            while ((line = br.readLine()) != null) {

                String[] elems = line.split(",");
                int elemsLength = elems.length;

                if (lineCount == 0) { // header row

                    if (elemsLength == 2) {
                        columnCount = 2;
                        if (!elems[0].equals("COLUMN") || !elems[1].equals("WIDTH")) {
                            result = false;
                            break;
                        }
                    } else if (elemsLength == 3) {
                        columnCount = 3;
                        if (!elems[0].equals("COLUMN") || !elems[1].equals("START") || !elems[2].equals("END")) {
                            result = false;
                            break;
                        }
                    } else {
                        result = false;
                        break;
                    }

                } else {

                    if (elemsLength != columnCount) {
                        result = false;
                        break;
                    }
                }

                lineCount++;
            }

        } catch (Exception ex2) {
            ex2.printStackTrace();
        }

        return result;
    }

    /**
     *
     * @param schemaFile
     * @return
     */
    public PositionalSchemaData getSchemaData(final File schemaFile) {

        List<Point> fieldPositions = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        String line;
        int lineCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(schemaFile))) {

            while ((line = br.readLine()) != null) {

                String[] elems = line.split(",");
                int elemsLength = elems.length;

                if (lineCount > 0) { // skip header row at line 0

                    columnNames.add(elems[0]);

                    if (elemsLength == 2) {
                        fieldPositions.add(new Point(Integer.parseInt(elems[1]), DUMMY_Y_VALUE));
                    }
                    if (elemsLength == 3) {
                        fieldPositions.add(new Point(Integer.parseInt(elems[1]), Integer.parseInt(elems[2])));
                    }
                }

                lineCount++;
            }

        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
        return new PositionalSchemaData(fieldPositions,columnNames, DUMMY_Y_VALUE);
    }

    public static Integer getDummyYValue() { return DUMMY_Y_VALUE; }
}

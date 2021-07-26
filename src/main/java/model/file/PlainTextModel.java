package model.file;

import javax.swing.*;
import java.io.*;

/**
 * Created by tmn7 on 6/4/18.
 */
public class PlainTextModel {

    private File myFile;
    private JTextArea textArea;


    public PlainTextModel(File file) {

        myFile = file;
        textArea = new JTextArea();
    }

    /**
     * Populates textArea with the contents of myFile.
     *
     * @return true if myFile was loaded into textArea
     */
    public boolean readFile() {

        boolean result = true;

        try (BufferedReader br = new BufferedReader(new FileReader(myFile))) {

            textArea.read(br,myFile);

        } catch (Exception ex2) {
            ex2.printStackTrace();
            result = false;
        }

        return result;
    }

    public boolean save() {

        String os = System.getProperty("os.name").toLowerCase();
        String windowsEncoding = "windows-1252";
        String utf8Encoding = "utf-8";
        String encoding = (os.contains("windows")) ? windowsEncoding : utf8Encoding;

        return saveFile(encoding);
    }

    public boolean saveAs(final String absPath) {

        myFile = new File(absPath);
        return save();
    }

    public boolean saveFile(final String encoding) {

        boolean result = true;

        try {
            textArea.write(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFile.getAbsolutePath()))));
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public String getAbsolutePath() { return myFile.exists() ? myFile.getAbsolutePath() : myFile.getName(); }
    public String getFileName() { return myFile.getName(); }
    public JTextArea getTextArea() {
        return textArea;
    }
}

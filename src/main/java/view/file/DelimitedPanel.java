package view.file;


import model.persistence.AppProperties;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmn7 on 7/20/18.
 */
public class DelimitedPanel extends DisplayPanel {

    private final Character myDelimiter;
    private final TextFieldPanel columnCountPanel;
    private final TextFieldPanel headerPanel;
    private final TextFieldPanel valuesPanel;
    private List<String> fakeHeaderFields;
    private boolean fakeHeader = false;


    /**
     * Constructor.
     *
     * @param delimiter The delimiter for this flatfile.
     * @param appProperties The AppProperties for setting colors and font.
     */
    public DelimitedPanel(final Character delimiter, final AppProperties appProperties) {
        myDelimiter = delimiter;
        columnCountPanel = new TextFieldPanel("#", false, appProperties);
        headerPanel = new TextFieldPanel("COLUMN", false, appProperties);
        valuesPanel = new TextFieldPanel("VALUE", false, appProperties);
        fakeHeaderFields = new ArrayList<>();
        buildPanel();
    }

    /**
     * Adds components to this DelimitedPanel for the initial set up.
     */
    private void buildPanel() {

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(3,3,3,-1); // top,left,bottom,right
        gbc.weighty = .5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(columnCountPanel,gbc);

        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(3,-1,3,-1); // top,left,bottom,right
        gbc.gridx = 1;
        add(headerPanel,gbc);

        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = .5;
        gbc.insets = new Insets(3,-1,3,3);
        gbc.gridx = 2;
        add(valuesPanel,gbc);
    }

    /**
     * Sets the headerPanel with values from the headerFile passed in.
     * Trips headerIsFake to true, preventing updating of the contents of headerPanel
     * when user selects the first row of the file.
     *
     * @param headerFields The List of header values for a delimited file.
     */
    public void setFakeHeader(final List<String> headerFields) {

        fakeHeaderFields = headerFields;
        int headerSize = fakeHeaderFields.size();

        if (headerSize > 0) {
            fakeHeader = true;

            updateMyTextFieldCount(columnCountPanel, headerSize);
            updateMyTextFieldCount(headerPanel, headerSize);

            for (int i = 0; i < headerSize; i++) {

                String field = fakeHeaderFields.get(i);
                MyTextField myTextField;

                myTextField = columnCountPanel.getMyTextFields().get(i);
                myTextField.setText(Integer.toString(i+1));
                myTextField = headerPanel.getMyTextFields().get(i);
                myTextField.setText(field);
            }
            revalidate();
        }
    }

    /**
     * Sets the text on the display components from a specified
     * line of a JTextArea to the end. The line is delimited and put
     * into the List<String> passed in.
     *
     * @param rowFields A row of a JTextArea as a List of Strings.
     * @param textAreaIndex The index of rowFields in the JTextArea.
     */
    @Override
    public void setTextOnDisplayComponents(final List<String> rowFields, final int textAreaIndex) {

        int rowFieldsCount = rowFields.size();

        if (textAreaIndex == 0 && !fakeHeader) {
            updateMyTextFieldCount(columnCountPanel, rowFieldsCount);
            updateMyTextFieldCount(headerPanel, rowFieldsCount);
        }

        updateMyTextFieldCount(valuesPanel, rowFieldsCount);

        for (int i = 0; i < rowFieldsCount; i++) {

            String field = rowFields.get(i);
            MyTextField myTextField;

            if (textAreaIndex == 0 && !fakeHeader) {
                myTextField = columnCountPanel.getMyTextFields().get(i);
                myTextField.setText(Integer.toString(i+1));
                myTextField = headerPanel.getMyTextFields().get(i);
                myTextField.setText(field);
            }

            myTextField = valuesPanel.getMyTextFields().get(i);
            myTextField.setText(field);
        }
    }

    /**
     * Calls updateMyUI for each of the 3 panels.
     */
    @Override
    public void updateMyUI() {
        columnCountPanel.updateMyUI();
        headerPanel.updateMyUI();
        valuesPanel.updateMyUI();
    }

    /////// Getters ///////
    public Character getMyDelimiter() {return myDelimiter;}
    public boolean isFakeHeader() { return fakeHeader; }
    public List<String> getFakeHeaderFields() { return fakeHeaderFields; }

}

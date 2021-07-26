package view.file;

import model.file.PositionalSchemaData;
import model.persistence.AppProperties;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PositionalEntryDialog extends EntryDialog {

    private final PositionalSchemaData myPositionalSchemaData;


    /**
     * Constructor.
     *
     * @param theEntry The line of a file as a List of Strings.
     * @param appProperties The AppProperties for setting colors and font.
     * @param positionalSchemaData The schema associated with the positional flatfile.
     */
    public PositionalEntryDialog(final List<String> theEntry, final AppProperties appProperties, final PositionalSchemaData positionalSchemaData) {
        super();
        myColumnHeaders = positionalSchemaData.getMyColumnNames();
        myEntry = theEntry;
        myAppProperties = appProperties;
        myPositionalSchemaData = positionalSchemaData;
        valuesPanelAppProperties = getValuePanelAppProps();
        columnCountPanel = new TextFieldPanel("#", false, myAppProperties);
        headerPanel = new TextFieldPanel("COLUMN", false, myAppProperties);
        valuesPanel = new TextFieldPanel("VALUE", true, valuesPanelAppProperties);
        copyButton = new JButton();
        writeButton = new JButton();
        resultTextArea = new JTextArea();
        configResultTextArea();
        buildDialog();
        addTextFields();
        configValueTextFields();
        setResultPanelString();
        configButtonPanel();
    }

    /**
     * Adds JTextFields to all panels based on the count of column headers
     * in myColumnHeaders. Each JTextField's text will be set accordingly.
     */
    @Override
    protected void addTextFields() {

        for (int i = 0; i < myColumnHeaders.size(); i++) {

            columnCountPanel.addTextField(String.valueOf(i+1));
            headerPanel.addTextField(myColumnHeaders.get(i));
            valuesPanel.addTextField( (myEntry.size() > i) ? myEntry.get(i) : "" );
        }
    }

    /**
     * Sets the text of resultTextArea from the values of JTextFields in
     * valuesPanel. The width of the positional field is enforced via
     * appending white space or truncating.
     */
    @Override
    protected void setResultPanelString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < valuesPanel.getMyTextFields().size(); i++) {

            JTextField jTextField = valuesPanel.getMyTextFields().get(i);
            Point p = myPositionalSchemaData.getMyFieldPositions().get(i);
            int fieldWidth = (p.y == myPositionalSchemaData.getDUMMY_Y_VALUE()) ? p.x : p.y - p.x;

            String textFieldValue = jTextField.getText();
            String valueToAppend = "";

            if (textFieldValue.length() < fieldWidth) {
                valueToAppend = String.format("%-"+fieldWidth+"s", textFieldValue);
            } else if (textFieldValue.length() == fieldWidth) {
                valueToAppend = textFieldValue;
            } else {
                valueToAppend = textFieldValue.substring(0, fieldWidth);
            }

            sb.append(valueToAppend);
        }

        resultTextArea.setText(sb.toString());
    }
}

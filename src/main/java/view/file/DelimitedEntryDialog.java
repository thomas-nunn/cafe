package view.file;

import model.persistence.AppProperties;

import javax.swing.*;
import java.util.List;

public class DelimitedEntryDialog extends EntryDialog {

    private final String myDelimiter;


    /**
     * Constructor.
     *
     * @param theColumnHeaders A String list of headers.
     * @param theEntry A String list of the fields of a flatfile entry.
     * @param appProperties The AppProperties for setting colors and font.
     * @param delimiter The delimiter for the flatfile that this entry is from.
     */
    public DelimitedEntryDialog(final List<String> theColumnHeaders, final List<String> theEntry, final AppProperties appProperties, final String delimiter) {
        super();
        myColumnHeaders = theColumnHeaders;
        myEntry = theEntry;
        myAppProperties = appProperties;
        valuesPanelAppProperties = getValuePanelAppProps();
        columnCountPanel = new TextFieldPanel("#", false, myAppProperties);
        headerPanel = new TextFieldPanel("COLUMN", false, myAppProperties);
        valuesPanel = new TextFieldPanel("VALUE", true, valuesPanelAppProperties);
        copyButton = new JButton();
        writeButton = new JButton();
        resultTextArea = new JTextArea();
        myDelimiter = delimiter;
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
     * Sets resultTextArea's text based on the text of each JTextField in valuesPanel.
     * The delimiter is appended after each JTextField String except the last.
     */
    @Override
    protected void setResultPanelString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < valuesPanel.getMyTextFields().size(); i++) {
            JTextField jTextField = valuesPanel.getMyTextFields().get(i);
            if (i < valuesPanel.getMyTextFields().size() - 1) {
                sb.append(jTextField.getText()).append(myDelimiter);
            } else {
                sb.append(jTextField.getText());
            }
        }

        resultTextArea.setText(sb.toString());
    }
}

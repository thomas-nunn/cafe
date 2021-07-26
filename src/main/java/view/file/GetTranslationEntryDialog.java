package view.file;

import model.persistence.AppProperties;

import javax.swing.*;
import java.util.List;

public class GetTranslationEntryDialog extends EntryDialog {

    private JButton addGenericServiceButton;
    private JButton addCustomServiceButton;
    private int genericTextFieldCount = 1;
    private int customTextFieldCount = 1;

    /**
     * Constructor.
     *
     * @param theColumnHeaders A String list of headers.
     * @param theEntry A String list of the fields of a flatfile entry.
     * @param appProperties The AppProperties for setting colors and font.
     */
    public GetTranslationEntryDialog(final List<String> theColumnHeaders, final List<String> theEntry, final AppProperties appProperties) {
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
        addGenericServiceButton = new JButton();
        addCustomServiceButton = new JButton();
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
     * If there are more than one generic or custom service then extra JTextFields
     * are added for each extra service. The extra services don't have values for
     * columnCountPanel and headerPanel.
     */
    @Override
    protected void addTextFields() {

        for (int i = 0; i < myColumnHeaders.size(); i++) {

            String header = myColumnHeaders.get(i);

            if (header.equals("GENERIC_SERVICE") || header.equals("CUSTOM_SERVICE")) {

                String[] services = myEntry.get(i).split("~");

                for (int j = 0; j < services.length; j++) {

                    if (j == 0) {
                        columnCountPanel.addTextField(String.valueOf(i + 1));
                        headerPanel.addTextField(myColumnHeaders.get(i));
                    } else {
                        columnCountPanel.addTextField("");

                        if (header.equals("GENERIC_SERVICE")) {
                            genericTextFieldCount++;
                            headerPanel.addTextField("  " + header + " " + genericTextFieldCount);
                        } else {
                            customTextFieldCount++;
                            headerPanel.addTextField("  " + header + " " + customTextFieldCount);
                        }
                    }
                    valuesPanel.addTextField(services[j]);
                }

            } else {
                columnCountPanel.addTextField(String.valueOf(i + 1));
                headerPanel.addTextField(myColumnHeaders.get(i));
                valuesPanel.addTextField((myEntry.size() > i) ? myEntry.get(i) : "");
            }
        }
    }

    /**
     * Sets resultTextArea's text based on the text of each JTextField in valuesPanel.
     * Multiple GENERIC_SERVICE or CUSTOM_SERVICE will be delimited by '~' but only if
     * the field isn't blank.
     * The ',' delimiter is appended before each new field except the first field.
     */
    @Override
    protected void setResultPanelString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < headerPanel.getMyTextFields().size(); i++) {

            if (valuesPanel.getMyTextFields().size() > i) {

                String headerText = headerPanel.getMyTextFields().get(i).getText();
                String valueText = valuesPanel.getMyTextFields().get(i).getText();

                if (headerText.endsWith("_SERVICE")) {  // GENERIC_SERVICE or CUSTOM_SERVICE
                    sb.append(",").append(valueText);
                } else if (headerText.matches(".*_SERVICE \\d+.*")) {   // Assuming this is the 2nd, 3rd, etc of either service
                    if (valueText.matches("\\S+")) {
                        sb.append("~").append(valueText);
                    }
                } else {
                    if (i == 0) {
                        sb.append(valueText);
                    } else {
                        sb.append(",").append(valueText);
                    }
                }
            }
        }

        resultTextArea.setText(sb.toString());
    }

    /**
     * Adds blank text fields to each of the 3 panels for the purpose of adding
     * a GENERIC_SERVICE or CUSTOM_SERVICE to an entry in get_translation_details.cdf.
     *
     * @param isGenericService true if GENERIC_SERVICE or false if CUSTOM_SERVICE
     */
    public void addServiceTextFields(boolean isGenericService) {

        int indexForNewTextField = getIndexForNewTextField(isGenericService);

        columnCountPanel.insertTextField(indexForNewTextField, "");

        if (isGenericService) {
            genericTextFieldCount++;
            headerPanel.insertTextField(indexForNewTextField, "  GENERIC_SERVICE" + " " + genericTextFieldCount);
        } else {
            customTextFieldCount++;
            headerPanel.insertTextField(indexForNewTextField, "  CUSTOM_SERVICE" + " " + customTextFieldCount);
        }

        valuesPanel.insertTextField(indexForNewTextField, "");
        configValueTextField(indexForNewTextField);
    }

    /**
     * Determines the index for adding a new Generic or Custom service
     * text field to this Dialog. This method assumes that GENERIC_SERVICE is
     * followed by CUSTOM_SERVICE which is followed by SCHEMA in the order of
     * headers in get_translation_details.cdf.
     *
     * @param isGenericeService Indicates Generic or Custom Service
     * @return The index for a new text field.
     */
    private int getIndexForNewTextField(boolean isGenericeService) {

        int indexForNewTextField = 0;

        for (int i = 0; i < headerPanel.getMyTextFields().size(); i++) {

            String headerText = headerPanel.getMyTextFields().get(i).getText();

            if (isGenericeService) {
                if (headerText.equals("CUSTOM_SERVICE")) {
                    indexForNewTextField = i;
                    break;
                }
            } else {
                if (headerText.equals("SCHEMA")) {
                    indexForNewTextField = i;
                    break;
                }
            }
        }
        return indexForNewTextField;
    }

    /**
     * Add button text and an Action to addGenericServiceButton. The text must be
     * added after the Action. Add addGenericServiceButton to buttonPanel.
     *
     * @param buttonText The text to display on the button
     * @param actionToAdd The Action to take when the button is clicked
     */
    public void configGenericButton(final String buttonText, final AbstractAction actionToAdd) {
        addGenericServiceButton.setAction(actionToAdd);
        addGenericServiceButton.setText(buttonText);
        buttonPanel.add(addGenericServiceButton);
    }

    /**
     * Add button text and an Action to addCustomServiceButton. The text must be
     * added after the Action. Add addCustomServiceButton to buttonPanel.
     *
     * @param buttonText The text to display on the button
     * @param actionToAdd The Action to take when the button is clicked
     */
    public void configCustomButton(final String buttonText, final AbstractAction actionToAdd) {
        addCustomServiceButton.setAction(actionToAdd);
        addCustomServiceButton.setText(buttonText);
        buttonPanel.add(addCustomServiceButton);
    }

    public int getGenericTextFieldCount() {
        return genericTextFieldCount;
    }

    public int getCustomTextFieldCount() {
        return customTextFieldCount;
    }
}

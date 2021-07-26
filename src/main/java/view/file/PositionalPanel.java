package view.file;

import model.file.PositionalSchemaData;
import model.persistence.AppProperties;

import java.awt.*;
import java.util.List;

/**
 * Created by tmn7 on 9/16/18.
 */
public class PositionalPanel extends DisplayPanel {

    private PositionalSchemaData myPositionalSchemaData;
    private final TextFieldPanel columnCountPanel;
    private final TextFieldPanel headerPanel;
    private final TextFieldPanel valuesPanel;


    /**
     * Constructor.
     *
     * @param positionalSchemaData The PositionalSchemaData for a File.
     * @param appProperties The AppProperties for color and font.
     */
    public PositionalPanel(final PositionalSchemaData positionalSchemaData, final AppProperties appProperties) {
        super();
        myPositionalSchemaData = positionalSchemaData;
        columnCountPanel = new TextFieldPanel("#", false , appProperties);
        headerPanel = new TextFieldPanel("COLUMN", false , appProperties);
        valuesPanel = new TextFieldPanel("VALUE", false , appProperties);
        buildPanel();
    }

    /**
     * Adds components to this TabPanel for the initial set up.
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
     * Sets the text on columnCountPanel and headerPanel.
     */
    public void setColumnAndHeaderText() {

        for (int i = 0; i < myPositionalSchemaData.getMyColumnNames().size(); i++) {
            columnCountPanel.addTextField(Integer.toString(i+1));
            headerPanel.addTextField(myPositionalSchemaData.getMyColumnNames().get(i));
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
    public void setTextOnDisplayComponents(List<String> rowFields, int textAreaIndex) {

        updateMyTextFieldCount(valuesPanel, rowFields.size());

        for (int i = 0; i < rowFields.size(); i++) {
            valuesPanel.getMyTextFields().get(i).setText(rowFields.get(i));
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
    public PositionalSchemaData getPositionalSchemaData() {return myPositionalSchemaData;}
}

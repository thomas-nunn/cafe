package view.file;

import model.persistence.AppProperties;
import model.persistence.DefaultProperties;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class EntryDialog extends JDialog {

    protected AppProperties myAppProperties;
    protected AppProperties valuesPanelAppProperties;
    protected List<String> myColumnHeaders;
    protected List<String> myEntry;
    protected TextFieldPanel columnCountPanel;
    protected TextFieldPanel headerPanel;
    protected TextFieldPanel valuesPanel;
    protected JPanel buttonPanel;
    protected JButton copyButton;
    protected JButton writeButton;
    protected JTextArea resultTextArea;


    /**
     * Default constructor.
     */
    protected EntryDialog() {
        
        super(new JFrame(), "Entry Editor");
        myColumnHeaders = new ArrayList<>();
        myEntry = new ArrayList<>();
        myAppProperties = new AppProperties();
        valuesPanelAppProperties = new AppProperties();
        columnCountPanel = new TextFieldPanel("#", false, myAppProperties);
        headerPanel = new TextFieldPanel("COLUMN", false, myAppProperties);
        valuesPanel = new TextFieldPanel("VALUE", true, valuesPanelAppProperties);
        buttonPanel = new JPanel();
        copyButton = new JButton();
        writeButton = new JButton();
        resultTextArea = new JTextArea();
    }

    /**
     *
     */
    protected void buildDialog() {

        JPanel parentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3,3,1,3); // top,left,bottom,right
        gbc.weightx = .5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentPanel.add(getResultPanel(),gbc);

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1,3,3,3); // top,left,bottom,right
        gbc.weighty = .5;
        gbc.gridx = 0;
        gbc.gridy = 1;
        parentPanel.add(new JScrollPane(getTextFieldParentPanel()),gbc);

        getContentPane().add(parentPanel);
    }

    /**
     * Configures a JPanel for holding the TextFieldPanels.
     *
     * @return The JPanel
     */
    protected JPanel getTextFieldParentPanel() {

        JPanel tfParentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints tfGbc = new GridBagConstraints();

        tfGbc.anchor = GridBagConstraints.FIRST_LINE_START;
        tfGbc.fill = GridBagConstraints.VERTICAL;
        tfGbc.gridwidth = 1;
        tfGbc.gridx = 0;
        tfGbc.gridy = 0;
        tfParentPanel.add(columnCountPanel,tfGbc);

        tfGbc.anchor = GridBagConstraints.FIRST_LINE_START;
        tfGbc.fill = GridBagConstraints.VERTICAL;
        tfGbc.weighty = .5;
        tfGbc.gridx = 1;
        tfParentPanel.add(headerPanel,tfGbc);

        tfGbc.anchor = GridBagConstraints.PAGE_END;
        tfGbc.fill = GridBagConstraints.BOTH;
        tfGbc.weightx = .5;
        tfGbc.weighty = .1;
        tfGbc.gridx = 2;
        tfParentPanel.add(valuesPanel,tfGbc);

        return tfParentPanel;
    }

    /**
     * Configures a JPanel for holding buttonPanel and resultTextArea.
     *
     * @return The JPanel
     */
    protected JPanel getResultPanel() {

        JPanel resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setBackground(myAppProperties.getBackGroundColor());
        GridBagConstraints resultGbc = new GridBagConstraints();

        resultGbc.anchor = GridBagConstraints.CENTER;
        resultGbc.fill = GridBagConstraints.BOTH;
        resultGbc.insets = new Insets(3,1,0,1);
        resultGbc.weighty = .6;
        resultGbc.weightx = .6;
        resultGbc.gridx = 0;
        resultGbc.gridy = 0;
        resultPanel.add(buttonPanel,resultGbc);

        resultGbc.anchor = GridBagConstraints.LINE_START;
        resultGbc.fill = GridBagConstraints.BOTH;
        resultGbc.insets = new Insets(0,1,3,1); // top,left,bottom,right
        resultGbc.gridwidth = 2;
        resultGbc.weighty = .5;
        resultGbc.weightx = .5;
        resultGbc.gridx = 0;
        resultGbc.gridy = 1;
        resultPanel.add(resultTextArea,resultGbc);

        return resultPanel;
    }

    /**
     * Configures the JPanel that houses the buttons.
     */
    protected void configButtonPanel() {

        buttonPanel.setBackground(myAppProperties.getBackGroundColor());
    }

    /**
     * Add listeners for the JTextField in valuesPanel specified by textFieldIndex.
     * A DocumentListener for updating the resultPanel JTextArea and a FocusListener for
     * changing the background color if a JTextField has focus.
     *
     * @param textFieldIndex The index of the JTextField
     */
    protected void configValueTextField(int textFieldIndex) {

        MyTextField myTextField = valuesPanel.getMyTextFields().get(textFieldIndex);

        myTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                setResultPanelString();
            }
            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                setResultPanelString();
            }
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                setResultPanelString();
            }
        });

        myTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                myTextField.setBackground(DefaultProperties.caretLineHighlightColor);
            }
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                myTextField.setBackground(valuesPanelAppProperties.getBackGroundColor());
            }
        });

    }

    /**
     * Add listeners for each JTextField in valuesPanel. A DocumentListener for updating
     * the resultPanel JTextArea and a FocusListener for changing the background color if a
     * JTextField has focus.
     */
    protected void configValueTextFields() {

        for (JTextField jTextField : valuesPanel.getMyTextFields()) {

            jTextField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    setResultPanelString();
                }
                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    setResultPanelString();
                }
                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    setResultPanelString();
                }
            });

            jTextField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    super.focusGained(e);
                    jTextField.setBackground(DefaultProperties.caretLineHighlightColor);
                }
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    jTextField.setBackground(valuesPanelAppProperties.getBackGroundColor());
                }
            });
        }
    }

    /**
     * Configures the JTextArea used for displaying the entry.
     */
    protected void configResultTextArea() {

        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setBackground(myAppProperties.getBackGroundColor());
        resultTextArea.setForeground(myAppProperties.getForeGroundColor());
        resultTextArea.setFont(myAppProperties.getFont());
        resultTextArea.setBorder(MyBorders.getBeveledTitleBorder("Entry"));
    }

    /**
     * Creates a new AppProperties for use in valuesPanel.
     * The only difference regarding colors and font will be
     * the background color which will be lighter than myAppProperties
     * background color in order to indicate that the text fields in
     * valuesPanel are editable.
     *
     * @return The AppProperties
     */
    protected AppProperties getValuePanelAppProps() {

        AppProperties valuePanelAppProps = new AppProperties();

        float[] hsbVals = Color.RGBtoHSB(myAppProperties.getBackGroundColor().getRed(),
                myAppProperties.getBackGroundColor().getGreen(),
                myAppProperties.getBackGroundColor().getBlue(), null);
        Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], 0.5f * ( 1f + hsbVals[2]));

        valuePanelAppProps.setBackGroundColor(highlight);
        valuePanelAppProps.setForeGroundColor(myAppProperties.getForeGroundColor());
        valuePanelAppProps.setFont(myAppProperties.getFont());

        return valuePanelAppProps;
    }

    /**
     * Add button text and an Action to copyButton. The text must be
     * added after the Action.
     *
     * @param buttonText The text to display on the button
     * @param actionToAdd The Action to take when the button is clicked
     */
    public void configCopyButton(final String buttonText, final AbstractAction actionToAdd) {
        copyButton.setAction(actionToAdd);
        copyButton.setText(buttonText);
        buttonPanel.add(copyButton);
    }

    /**
     * Add button text and an Action to writeButton. The text must be
     * added after the Action.
     *
     * @param buttonText The text to display on the button
     * @param actionToAdd The Action to take when the button is clicked
     */
    public void configWriteButton(final String buttonText, final AbstractAction actionToAdd) {
        writeButton.setAction(actionToAdd);
        writeButton.setText(buttonText);
        buttonPanel.add(writeButton);
    }

    /**
     * Sets this Dialog visible.
     * After calling pack(), this Dialog is invalidated in order
     * to account for a bug in Swing when a Component contains a
     * JTextArea with lineWrap = true. Calling pack() after invalidation will cause
     * the Dialog to be drawn correctly with all Components' height and width correct.
     */
    public void displayDialog() {

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        pack();
        invalidate();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    ///////////////// Getters /////////////////

    public String getResultText() {
        return resultTextArea.getText();
    }
    public TextFieldPanel getColumnCountPanel() {
        return columnCountPanel;
    }
    public TextFieldPanel getHeaderPanel() {
        return headerPanel;
    }
    public TextFieldPanel getValuesPanel() {
        return valuesPanel;
    }

    ///////////////// Abstract Methods /////////////////

    /**
     *
     */
    protected abstract void addTextFields();

    /**
     *
     */
    protected abstract void setResultPanelString();

}

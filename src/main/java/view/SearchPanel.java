package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tmn7 on 1/6/18.
 */
public class SearchPanel extends JPanel {

    private static final Dimension COMBO_BOX_DIMS = new Dimension(225,25);
    private boolean isFindAndReplace;
    private JButton closeButton;
    private JButton forwardFindButton;
    private JButton backwardFindButton;
    private JButton replaceButton;
    private JCheckBox caseCB;
    private JCheckBox regexCB;
    private JCheckBox entireWordCB;
    private EditorComboBox<String> findComboBox;
    private JTextField replaceTextField;
    private JLabel resultLabel;
    private Insets insets;


    public SearchPanel(final ComboBoxModel<String> comboBoxModel, final boolean hasReplace) {
        super(new GridBagLayout());
        isFindAndReplace = hasReplace;
        initFields(comboBoxModel);
        configFields();
        buildPanel();
    }

    /**
     * Initializes the fields of this Class.
     *
     * @param comboBoxModel The ComboBoxModel for findComboBox.
     */
    private void initFields(final ComboBoxModel<String> comboBoxModel) {

        closeButton = new JButton("Close");
        forwardFindButton = new JButton(">>");
        backwardFindButton = new JButton("<<");
        replaceButton = new JButton("Replace All");
        caseCB = new JCheckBox("Case");
        regexCB = new JCheckBox("Regex");
        entireWordCB = new JCheckBox("Word");
        findComboBox = new EditorComboBox<>(comboBoxModel,forwardFindButton);
        replaceTextField = new JTextField();
        resultLabel = new JLabel();
        insets = new Insets(3,2,1,2); // top,left,bottom,right
    }

    /**
     * Configures fields of this Class.
     */
    private void configFields() {

        findComboBox.setPreferredSize(COMBO_BOX_DIMS);
        findComboBox.setEditable(true);
        replaceTextField.setPreferredSize(COMBO_BOX_DIMS);
        replaceTextField.setEditable(true);
        caseCB.setToolTipText("Match Case");
        caseCB.setRolloverEnabled(false);
        entireWordCB.setToolTipText("Match Entire Word");
        entireWordCB.setRolloverEnabled(false);
        regexCB.setToolTipText("Regex Match");
        regexCB.setRolloverEnabled(false);
        resultLabel.setFont(new Font(findComboBox.getFont().getFontName(), Font.PLAIN, 15));
    }

    /**
     * Builds this JPanel.
     */
    private void buildPanel() {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.weightx = .1;
        gbc.weighty = .1;
        gbc.insets = insets;
        add(getOptionPanel(), gbc);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = insets;
        add((isFindAndReplace) ? getFindAndReplacePanel() : getFindPanel() , gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = insets;
        add(getCntrlPanel(), gbc);
    }

    /**
     * @return A JPanel with JCheckBox search options.
     */
    private JPanel getOptionPanel() {

        JPanel optionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints optsGbc = new GridBagConstraints();

        optsGbc.anchor = GridBagConstraints.NORTHWEST;
        optsGbc.gridx = 0;
        optsGbc.gridy = 0;
        optsGbc.insets = insets;
        optionPanel.add(caseCB, optsGbc);

        optsGbc.anchor = GridBagConstraints.NORTHWEST;
        optsGbc.gridx = 0;
        optsGbc.gridy = 1;
        optsGbc.insets = insets;
        optionPanel.add(entireWordCB, optsGbc);

        optsGbc.anchor = GridBagConstraints.NORTHWEST;
        optsGbc.gridx = 0;
        optsGbc.gridy = 2;
        optsGbc.weightx = .1;
        optsGbc.weighty = .1;
        optsGbc.insets = insets;
        optionPanel.add(regexCB, optsGbc);

        return optionPanel;
    }

    /**
     * @return A JPanel with combo box and search buttons.
     */
    private JPanel getFindPanel() {

        JPanel comboBoxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints fpGbc = new GridBagConstraints();

        fpGbc.anchor = GridBagConstraints.NORTH;
        fpGbc.gridx = 0;
        fpGbc.gridy = 0;
        fpGbc.weightx = .1;
        fpGbc.weighty = .1;
        fpGbc.insets = insets;
        comboBoxPanel.add(findComboBox, fpGbc);

        fpGbc.anchor = GridBagConstraints.NORTH;
        fpGbc.gridx = 1;
        fpGbc.gridy = 0;
        fpGbc.insets = insets;
        comboBoxPanel.add(backwardFindButton, fpGbc);

        fpGbc.anchor = GridBagConstraints.NORTH;
        fpGbc.gridx = 2;
        fpGbc.gridy = 0;
        fpGbc.insets = insets;
        comboBoxPanel.add(forwardFindButton, fpGbc);

        return comboBoxPanel;
    }

    /**
     * Builds a JPanel for use as a "Find and Replace" panel.
     * This panel contains standard search components but also
     * has search and replace components.
     *
     * @return The JPanel.
     */
    private JPanel getFindAndReplacePanel() {

        JPanel comboBoxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints frGbc = new GridBagConstraints();

        frGbc.anchor = GridBagConstraints.NORTH;
        frGbc.gridx = 0;
        frGbc.gridy = 0;
        frGbc.weightx = .1;
        frGbc.weighty = .1;
        frGbc.insets = insets;
        comboBoxPanel.add(findComboBox, frGbc);

        frGbc.anchor = GridBagConstraints.NORTH;
        frGbc.gridx = 1;
        frGbc.gridy = 0;
        frGbc.insets = insets;
        comboBoxPanel.add(backwardFindButton, frGbc);

        frGbc.anchor = GridBagConstraints.NORTH;
        frGbc.gridx = 2;
        frGbc.gridy = 0;
        frGbc.insets = insets;
        comboBoxPanel.add(forwardFindButton, frGbc);

        frGbc.anchor = GridBagConstraints.NORTH;
        frGbc.gridx = 0;
        frGbc.gridy = 1;
        frGbc.insets = insets;
        comboBoxPanel.add(replaceTextField, frGbc);

        frGbc.anchor = GridBagConstraints.NORTH;
        frGbc.fill = GridBagConstraints.HORIZONTAL;
        frGbc.gridx = 1;
        frGbc.gridy = 1;
        frGbc.gridwidth = 2;
        frGbc.insets = insets;
        comboBoxPanel.add(replaceButton, frGbc);

        return comboBoxPanel;
    }

    /**
     * @return A JPanel with results JLabel and a "close" button.
     */
    private JPanel getCntrlPanel() {

        JPanel cntrlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cpGbc = new GridBagConstraints();

        cpGbc.anchor = GridBagConstraints.EAST;
        cpGbc.gridx = 0;
        cpGbc.gridy = 1;
        cpGbc.insets = insets;
        cntrlPanel.add(resultLabel, cpGbc);

        cpGbc.anchor = GridBagConstraints.EAST;
        cpGbc.gridx = 1;
        cpGbc.gridy = 1;
        cpGbc.weightx = .1;
        cpGbc.insets = insets;
        cntrlPanel.add(closeButton, cpGbc);

        return cntrlPanel;
    }

    // Getters
    public JButton getCloseButton() {
        return closeButton;
    }
    public JButton getForwardFindButton() {
        return forwardFindButton;
    }
    public JButton getBackwardFindButton() {
        return backwardFindButton;
    }
    public JButton getReplaceButton() {
        return replaceButton;
    }
    public JCheckBox getCaseCB() {
        return caseCB;
    }
    public JCheckBox getRegexCB() {
        return regexCB;
    }
    public JCheckBox getEntireWordCB() {
        return entireWordCB;
    }
    public JComboBox<String> getFindComboBox() {
        return findComboBox;
    }
    public JTextField getReplaceTextField() {
        return replaceTextField;
    }
    public JLabel getResultLabel() {
        return resultLabel;
    }
    public boolean isFindAndReplace() { return isFindAndReplace; }
}

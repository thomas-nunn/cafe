package view.favorite;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmn7 on 6/10/17.
 */
public class AddFavPanel extends JPanel {

    private JPanel addFavTextFieldPanel;
    private JButton addFilePathButton;
    private JComboBox<String> groupBox;
    private JTextField titleTextField;
    private JLabel groupLabel;
    private JLabel titleLabel;
    private Insets insets; // top,left,bottom,right
    private Dimension TEXT_FIELD_DIMS;
    private Integer textFieldIndex;
    private List<JTextField> jtfList;
    private List<String> groups;
    private String[] groupsArr;

    public AddFavPanel(List<String> groups, int favPanelNumber) {
        super();
        this.groups = groups;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder(favPanelNumber + ". New Favorite"));
        initFields();
        setUpGroupSelectionBox();
        buildAddFavPanel();
    }

    private void initFields() {

        addFavTextFieldPanel = new JPanel(new GridBagLayout());
        addFilePathButton = new JButton("Add File Path");
        titleTextField = new JTextField();
        groupLabel = new JLabel("Group Title", JLabel.RIGHT);
        titleLabel = new JLabel("Favorite Title", JLabel.RIGHT);
        insets = new Insets(2,2,2,2); // top,left,bottom,right
        TEXT_FIELD_DIMS = new Dimension(450,23);
        textFieldIndex = 0;
        jtfList = new ArrayList<>();
    }

    private void setUpGroupSelectionBox() {

        groupsArr = new String[groups.size()];
        groupsArr = groups.toArray(groupsArr);
        groupBox = new JComboBox<>(groupsArr);
        groupBox.insertItemAt("",0);
        groupBox.setSelectedIndex(0);
        groupBox.setEditable(true);
        groupBox.setPreferredSize(TEXT_FIELD_DIMS);
        groupLabel.setPreferredSize(titleLabel.getPreferredSize());
    }

    private JPanel getGroupPanel() {

        JPanel groupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        groupPanel.add(groupLabel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        groupPanel.add(groupBox, gbc);

        return groupPanel;
    }

    private void buildAddFavPanel() {

        GridBagConstraints gbc = new GridBagConstraints();
        Insets afpInsets = new Insets(6,2,4,2);

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.insets = afpInsets;
        add(getGroupPanel(), gbc);

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = afpInsets;
        add(buildAddFavTitlePanel(), gbc);

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = afpInsets;
        add(addFavTextFieldPanel, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 3;
        gbc.insets = new Insets(2,0,6,4);
        add(addFilePathButton, gbc);
    }

    private JPanel buildAddFavTitlePanel() {

        JPanel titlePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        titlePanel.add(titleLabel, gbc);

        titleTextField.setPreferredSize(TEXT_FIELD_DIMS);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        titlePanel.add(titleTextField, gbc);

        return titlePanel;
    }

    public JTextField addTextField() {

        JTextField jTextField = new JTextField();
        jTextField.setPreferredSize(TEXT_FIELD_DIMS);
        jtfList.add(jTextField);

        String jtfLabelTitle = "File Path " + (textFieldIndex + 1);
        JLabel filePathLabel = new JLabel(jtfLabelTitle, JLabel.RIGHT);
        filePathLabel.setPreferredSize(titleLabel.getPreferredSize());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = textFieldIndex;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        addFavTextFieldPanel.add(filePathLabel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = textFieldIndex;
        gbc.weightx = .1;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        addFavTextFieldPanel.add(jTextField, gbc);

        textFieldIndex++;

        return jTextField;
    }

    public JButton getAddFilePathButton() {
        return addFilePathButton;
    }

    public List<JTextField> getJtfList() {
        return jtfList;
    }

    public JComboBox<String> getGroupBox() {
        return groupBox;
    }

    public JTextField getFavNameTextField() {
        return titleTextField;
    }
}
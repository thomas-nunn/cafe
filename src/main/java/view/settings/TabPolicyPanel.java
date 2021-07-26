package view.settings;

import view.file.MyBorders;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tmn7 on 9/2/17.
 */
public class TabPolicyPanel extends JPanel {

    private JRadioButton scrollPolicyButton;
    private JRadioButton wrapPolicyButton;
    private Insets insets;
    private Integer currentPolicy;
    private Dimension labelDims;

    /**
     *
     * @param thePolicy
     */
    public TabPolicyPanel(Integer thePolicy) {
        super();
        currentPolicy = thePolicy;
        initFields();
        buildPanel();
    }

    private void initFields() {
        scrollPolicyButton = new JRadioButton("Scroll Policy");
        wrapPolicyButton = new JRadioButton("Wrap Policy");
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(scrollPolicyButton);
        buttonGroup.add(wrapPolicyButton);
        insets = new Insets(3,3,3,3); // top,left,bottom,right
        labelDims = new Dimension(375,75);
    }

    private void buildPanel() {

        setLayout(new GridBagLayout());
        setBorder(MyBorders.getEmptyLineBorder());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = insets;
        add(getInfoPanel(), gbc);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = insets;
        add(getWrapPanel(), gbc);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = .01;
        gbc.weighty = .01;
        gbc.insets = insets;
        add(getScrollPanel(), gbc);

        if (currentPolicy == 0) {
            wrapPolicyButton.setSelected(true);
        } else {
            scrollPolicyButton.setSelected(true);
        }
    }


    private JPanel getInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints infoGBC = new GridBagConstraints();

        JLabel infoArea = new JLabel("The Tab Layout Policy dictates how tabs are displayed.", JLabel.CENTER);
        infoArea.setFont(new Font(Font.DIALOG, Font.PLAIN,15));

        infoGBC.fill = GridBagConstraints.BOTH;
        infoGBC.gridx = 0;
        infoGBC.gridy = 0;
        infoGBC.weightx = .01;
        infoGBC.insets = new Insets(10,3,10,3); // top,left,bottom,right
        infoPanel.add(infoArea,infoGBC);

        return infoPanel;
    }

    private JPanel getWrapPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonGBC = new GridBagConstraints();

        JTextArea wrapLabel = new JTextArea("Tab layout policy for wrapping tabs in multiple runs when all tabs will not fit within a single run.");
        wrapLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        wrapLabel.setPreferredSize(labelDims);
        wrapLabel.setLineWrap(true);
        wrapLabel.setWrapStyleWord(true);

        buttonGBC.anchor = GridBagConstraints.WEST;
        buttonGBC.gridx = 0;
        buttonGBC.gridy = 0;
        buttonGBC.weightx = .01;
        buttonGBC.insets = insets; // top,left,bottom,right
        buttonPanel.add(wrapPolicyButton, buttonGBC);

        buttonGBC.gridx = 1;
        buttonGBC.gridy = 0;
        buttonGBC.insets = insets;
        buttonPanel.add(wrapLabel, buttonGBC);

        return buttonPanel;
    }

    private JPanel getScrollPanel() {
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonGBC = new GridBagConstraints();

        JTextArea scrollLabel = new JTextArea("Tab layout policy for providing a subset of available tabs when all the tabs will not fit within a single run." +
                " If all the tabs do not fit within a single run the look and feel will provide a way to navigate to hidden tabs.");
        scrollLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollLabel.setPreferredSize(labelDims);
        scrollLabel.setLineWrap(true);
        scrollLabel.setWrapStyleWord(true);

        buttonGBC.anchor = GridBagConstraints.WEST;
        buttonGBC.gridx = 0;
        buttonGBC.gridy = 0;
        buttonGBC.weightx = .01;
        buttonGBC.insets = insets; // top,left,bottom,right
        buttonPanel.add(scrollPolicyButton, buttonGBC);

        buttonGBC.gridx = 1;
        buttonGBC.gridy = 0;
        buttonGBC.insets = insets;
        buttonPanel.add(scrollLabel, buttonGBC);

        return buttonPanel;
    }

    public Integer getCurrentPolicy() {
        if (wrapPolicyButton.isSelected()) {
            currentPolicy = 0;
        } else {
            currentPolicy = 1;
        }
        return currentPolicy;
    }
}

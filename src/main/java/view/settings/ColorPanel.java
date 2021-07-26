package view.settings;

import view.file.MyBorders;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tmn7 on 9/2/17.
 */
public class ColorPanel extends JPanel {

    private Dimension bgButtonDims;
    private Color bgColor, fgColor, newBgColor, newFgColor;
    private JButton bgButton, fgButton, selectedBgButton, selectedFgButton, defaultsButton;
    private Insets insets;

    public ColorPanel(Color bgColor, Color fgColor) {
        super();
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        setLayout(new GridBagLayout());
        initFields();
        buildPanel();
    }

    private void initFields() {

        bgButton = new JButton("Choose Background");
        fgButton = new JButton("Choose Foreground");
        selectedBgButton = new JButton();
        selectedFgButton = new JButton();
        defaultsButton = new JButton("Restore Defaults");
        newBgColor = null;
        newFgColor = null;

        insets = new Insets(3,3,3,3); // top,left,bottom,right
        bgButtonDims = new Dimension(bgButton.getPreferredSize());
    }

    private void buildPanel() {

        setBorder(MyBorders.getEmptyLineBorder());
        GridBagConstraints gbc = new GridBagConstraints();

        JButton currBg = new JButton();
        JButton currFg = new JButton();

        currBg.setBackground(bgColor);
        currFg.setBackground(fgColor);

        currBg.setPreferredSize(bgButtonDims);
        currFg.setPreferredSize(bgButtonDims);
        selectedBgButton.setPreferredSize(bgButtonDims);
        selectedFgButton.setPreferredSize(bgButtonDims);

        currBg.setEnabled(false);
        currFg.setEnabled(false);
        selectedBgButton.setEnabled(false);
        selectedFgButton.setEnabled(false);
        selectedBgButton.setBackground(null);
        selectedFgButton.setBackground(null);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = insets; // top,left,bottom,right
        add(new JLabel("Background"), gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = insets;
        add(new JLabel("Foreground"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = insets; // top,left,bottom,right
        add(new JLabel("Current"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = insets;
        add(currBg, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = insets;
        add(currFg, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = insets; // top,left,bottom,right
        add(new JLabel("New"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = insets;
        add(selectedBgButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.insets = insets;
        add(selectedFgButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(7,2,2,2); // top,left,bottom,right
        add(bgButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.weightx = .1;
        gbc.insets = new Insets(7,2,2,2); // top,left,bottom,right
        add(fgButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.insets = new Insets(15,3,3,3); // top,left,bottom,right
        add(defaultsButton, gbc);

    }

    public JButton getBgButton() {
        return bgButton;
    }
    public JButton getFgButton() {
        return fgButton;
    }
    public JButton getSelectedBgButton() {
        return selectedBgButton;
    }
    public JButton getSelectedFgButton() {
        return selectedFgButton;
    }
    public Color getBgColor() {
        return bgColor;
    }
    public Color getFgColor() {
        return fgColor;
    }
    public JButton getDefaultsButton() {
        return defaultsButton;
    }
    public Color getNewBgColor() { return newBgColor; }
    public void setNewBgColor(Color bgColor) {
        newBgColor = bgColor;
    }
    public Color getNewFgColor() { return newFgColor; }
    public void setNewFgColor(Color fgColor) {
        newFgColor = fgColor;
    }
}

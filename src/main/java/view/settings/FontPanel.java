package view.settings;

import view.file.MyBorders;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tmn7 on 9/2/17.
 */
public class FontPanel extends JPanel {

    private Font currentFont;
    private Font selectedFont;
    private GraphicsEnvironment graphicsEnvironment;
    private JComboBox<String> fontNameBox;
    private JComboBox<Integer> fontSizeBox;
    private JComboBox<String> fontStyleBox;
    private JLabel newDisplay;
    private JLabel currentDisplay;
    private JTextArea currentFontDisplay;
    private JTextArea selectedFontDisplay;
    private Map<String, Integer> styleMap;
    private Insets insets;
    private Integer minFontSize;
    private Integer maxFontSize;
    private boolean userSelectedSomething;


    /**
     *
     * @param font
     */
    public FontPanel(final Font font) {
        super(new GridBagLayout());
        initFields(font);
        setBorder(MyBorders.getEmptyLineBorder());
        setUpFontNameBox();
        setUpFontStyleBox();
        setUpFontSizeBox();
        setUpTextAreas();
        buildPanel();
    }

    private void initFields(final Font font) {

        currentFont = font;
        selectedFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize());
        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        currentDisplay = new JLabel("This is an example of the current Font.");
        newDisplay = new JLabel("This is an example of the new Font.");
        currentFontDisplay = new JTextArea();
        selectedFontDisplay = new JTextArea();
        insets = new Insets(2,2,2,2); // top,left,bottom,right

        styleMap = new HashMap<>();
        styleMap.put("Plain", Font.PLAIN);
        styleMap.put("Bold", Font.BOLD);
        styleMap.put("Italic", Font.ITALIC);

        minFontSize = 10;
        maxFontSize = 36;
        userSelectedSomething = false;
    }

    private void buildPanel() {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.insets = insets;
        add(getCurrentPanel(), gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = insets;
        add(getNewPanel(), gbc);
    }

    private JPanel getCurrentPanel() {

        JPanel currentPanel = new JPanel(new GridBagLayout());
        currentPanel.setBorder(BorderFactory.createTitledBorder("Current Font"));
        GridBagConstraints currentGbc = new GridBagConstraints();

        String styleName = "";
        for (String str : styleMap.keySet()) {
            if (styleMap.get(str) == currentFont.getStyle()) {
                styleName = str;
            }
        }

        currentDisplay.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        currentDisplay.setOpaque(true);
        currentDisplay.setBackground(Color.WHITE);
        currentDisplay.setFont(currentFont);

        currentGbc.anchor = GridBagConstraints.WEST;
        currentGbc.gridx = 0;
        currentGbc.gridy = 0;
        currentGbc.insets = insets;
        currentPanel.add(new JLabel(currentFont.getFontName() + ", " +
                styleName + ", " + Integer.toString(currentFont.getSize())), currentGbc);

        currentGbc.fill = GridBagConstraints.BOTH;
        currentGbc.gridx = 0;
        currentGbc.gridy = 1;
        currentGbc.weightx = .3;
        currentGbc.insets = insets;
        currentPanel.add(currentFontDisplay, currentGbc);

        return currentPanel;
    }

    private JPanel getNewPanel() {

        JPanel newPanel = new JPanel(new GridBagLayout());
        newPanel.setBorder(BorderFactory.createTitledBorder("New Font"));
        GridBagConstraints newGbc = new GridBagConstraints();

        newDisplay.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        newDisplay.setOpaque(true);
        newDisplay.setBackground(Color.WHITE);

        newGbc.anchor = GridBagConstraints.WEST;
        newGbc.gridx = 0;
        newGbc.gridy = 0;
        newGbc.weightx = .1;
        newGbc.insets = insets;
        newPanel.add(fontNameBox, newGbc);

        newGbc.gridx = 1;
        newGbc.gridy = 0;
        newGbc.insets = insets;
        newPanel.add(fontStyleBox, newGbc);

        newGbc.gridx = 2;
        newGbc.gridy = 0;
        newGbc.insets = insets;
        newPanel.add(fontSizeBox, newGbc);

        newGbc.fill = GridBagConstraints.BOTH;
        newGbc.gridx = 0;
        newGbc.gridy = 1;
        newGbc.weightx = .2;
        newGbc.gridwidth = 3;
        newGbc.insets = insets;
        newPanel.add(selectedFontDisplay, newGbc);

        return newPanel;
    }

    private void setUpTextAreas() {

        currentFontDisplay.setFont(currentFont);
        currentFontDisplay.setLineWrap(true);
        currentFontDisplay.setWrapStyleWord(true);
        currentFontDisplay.setPreferredSize(new Dimension(350,100));
        currentFontDisplay.setBackground(Color.WHITE);
        currentFontDisplay.setEditable(false);
        currentFontDisplay.setText("This is an example of the current Font.");

        selectedFontDisplay.setFont(selectedFont);
        selectedFontDisplay.setLineWrap(true);
        selectedFontDisplay.setWrapStyleWord(true);
        selectedFontDisplay.setPreferredSize(new Dimension(350,100));
        selectedFontDisplay.setBackground(Color.WHITE);
        selectedFontDisplay.setEditable(false);
        selectedFontDisplay.setText("This is an example of the new Font.");

    }

    private void setUpFontNameBox() {

        fontNameBox = new JComboBox<>();

        for (String fontName : graphicsEnvironment.getAvailableFontFamilyNames()) {
            fontNameBox.addItem(fontName);

        }
        fontNameBox.setSelectedItem(selectedFont.getFontName());
    }

    private void setUpFontStyleBox() {

        fontStyleBox = new JComboBox<>();

        fontStyleBox.addItem("Plain");
        fontStyleBox.addItem("Bold");
        fontStyleBox.addItem("Italic");

        if (currentFont.getStyle() == Font.PLAIN) {
            fontStyleBox.setSelectedItem("Plain");
        } else if (currentFont.getStyle() == Font.BOLD) {
            fontStyleBox.setSelectedItem("Bold");
        } else {
            fontStyleBox.setSelectedItem("Italic");
        }
    }

    private void setUpFontSizeBox() {

        fontSizeBox = new JComboBox<>();

        for (int i = minFontSize; i <= maxFontSize; i++) {
            fontSizeBox.addItem(i);
        }

        fontSizeBox.setSelectedItem(currentFont.getSize());
    }

    public JComboBox<String> getFontNameBox() {
        return fontNameBox;
    }

    public JComboBox<Integer> getFontSizeBox() {
        return fontSizeBox;
    }

    public JComboBox<String> getFontStyleBox() {
        return fontStyleBox;
    }

    public void setSelectedFont(Font selectedFont) {
        this.selectedFont = selectedFont;
    }

    public Font getCurrentFont() {
        return currentFont;
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    public JTextArea getCurrentFontDisplay() {
        return currentFontDisplay;
    }

    public JTextArea getSelectedFontDisplay() {
        return selectedFontDisplay;
    }

    public Map<String, Integer> getStyleMap() {
        return styleMap;
    }

    public boolean isUserSelectedSomething() {
        return userSelectedSomething;
    }

    public void setUserSelectedSomething(boolean userSelectedSomething) {
        this.userSelectedSomething = userSelectedSomething;
    }
}

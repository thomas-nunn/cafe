package view.file;

import model.persistence.AppProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tmn7 on 7/1/18.
 */
public class PlainTextPanel extends JPanel {

    private JTextArea textArea;
    private JScrollPane scrollPane;
    private TextLineNumber textLineNumber;
    private final AppProperties myAppProperties;


    /**
     * Constructor.
     *
     * @param theTextArea The JTextArea that hold a file's text.
     * @param absPath The file's absolute path for displaying to the user.
     * @param appProperties The AppProperties for setting colors and font.
     */
    public PlainTextPanel(final JTextArea theTextArea, final String absPath, final AppProperties appProperties) {
        super(new GridBagLayout());
        textArea = theTextArea;
        myAppProperties = appProperties;
        init();
        buildPanel(absPath);
    }

    /**
     * Initializes and configures fields of this panel.
     */
    private void init() {

        textArea.setEditable(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        textArea.setBackground(myAppProperties.getBackGroundColor());
        textArea.setForeground(myAppProperties.getForeGroundColor());
        textArea.setFont(myAppProperties.getFont());
        textLineNumber = new TextLineNumber(textArea,1);
        textLineNumber.setUpdateFont(true);
        scrollPane = new JScrollPane(textArea);
    }

    /**
     * Adds components to this panel.
     *
     * @param absPath The file's absolute path for displaying to the user.
     */
    private void buildPanel(String absPath) {

        scrollPane.setBorder(MyBorders.getTitledBorder(absPath));
        scrollPane.setRowHeaderView(textLineNumber);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .1;
        gbc.weighty = .1;
        gbc.insets = new Insets(3,3,3,3); // top,left,bottom,right

        add(scrollPane,gbc);
    }

    /**
     * Updates this panel's colors and font.
     */
    public void updateMyUI() {
        textArea.setBackground(myAppProperties.getBackGroundColor());
        textArea.setForeground(myAppProperties.getForeGroundColor());
        textArea.setFont(myAppProperties.getFont());
    }

}

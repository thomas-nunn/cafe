package view.file;

import javax.swing.*;

/**
 * Created by tmn7 on 6/30/18.
 */
public class ControlPanel extends JPanel {

    private JButton delimitedButton, positionalButton;

    public ControlPanel() {

        super();
        init();
        buildPanel();
    }

    private void init() {
        delimitedButton = new JButton("Delimited Flatfile");
        positionalButton = new JButton("Positional Flatfile");
    }

    private void buildPanel() {
        add(delimitedButton);
        add(positionalButton);
    }

    public JButton getDelimitedButton() {return delimitedButton;}
    public JButton getPositionalButton() {return positionalButton;}
}

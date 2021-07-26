package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tmn7 on 10/8/17.
 */
public class SearchDialog extends JDialog {

    JTabbedPane tabbedPane;
    GridBagConstraints gbc;
    private Insets insets;

    public SearchDialog(JFrame frame, final String title) {
        super(frame, title);
        setModal(false);
        setLocationRelativeTo(frame);
        initFields();
        buildDialog();
    }

    private void initFields() {

        tabbedPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        insets = new Insets(2,2,2,2); // top,left,bottom,right
    }

    private void buildDialog() {

        getContentPane().add(tabbedPane);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}

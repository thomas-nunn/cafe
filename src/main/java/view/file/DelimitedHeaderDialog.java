package view.file;

import model.resources.PathManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by tmn7 on 2/3/18.
 */
public class DelimitedHeaderDialog extends JDialog {

    private JButton submitButton;
    private JButton cancelButton;
    private JList<String> jList;
    private DefaultListModel listModel;


    public DelimitedHeaderDialog(JFrame frame) {
        super(frame, "Select Header", true);
        init();
        setUpDialog();
        setResizable(false);
    }

    @SuppressWarnings("unchecked")
    private void init() {

        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");
        listModel = new DefaultListModel();
        jList = new JList<>(listModel);

        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                if (mouseEvent.getClickCount() == 2) {
                    submitButton.doClick();
                }
            }
        });
    }

    /**
     * Creates and sorts a list of header file names from header files which are stored
     * at a location determined in PathManager.
     * @return The List of sorted header file names.
     */
    private List<String> getSortedHeaderFileNames() {

        List<String> headerFileNames = new ArrayList<>();
        File folder = new File(PathManager.getDelimitedHeaderPath());
        File[] headerFiles = folder.listFiles();

        if (headerFiles != null) {

            for (File f : headerFiles) headerFileNames.add(f.getName());

            headerFileNames.sort(new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    return s.compareToIgnoreCase(t1);
                }
            });
        }

        return headerFileNames;
    }

    /**
     *
     */
    private void setUpDialog() {

        final JPanel dialogPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        final List<String> headerFileNames = getSortedHeaderFileNames();

        if (!headerFileNames.isEmpty()) {

            jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            jList.setLayoutOrientation(JList.VERTICAL);
            JScrollPane scrollPane = new JScrollPane(jList,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            for (int i = 0; i < headerFileNames.size(); i++) {
                listModel.add(i, headerFileNames.get(i));
            }

            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(3,3,3,3);
            gbc.weighty = .1;
            dialogPanel.add(scrollPane, gbc);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(submitButton);
            buttonPanel.add(cancelButton);

            gbc.weightx = .5;
            gbc.anchor = GridBagConstraints.SOUTH;
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.insets = new Insets(3,3,3,3);
            dialogPanel.add(buttonPanel, gbc);

        } else {
            dialogPanel.add(new JLabel("No Delimited Headers Exist!"));
        }

        getContentPane().add(dialogPanel);
    }

    public JButton getSubmitButton() { return submitButton; }
    public JButton getCancelButton() { return cancelButton; }
    public JList<String> getjList() { return jList; }
}

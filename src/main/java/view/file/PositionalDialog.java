package view.file;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by tmn7 on 1/20/18.
 */
public class PositionalDialog extends JDialog {

    private JScrollPane scrollPane;
    private JButton submitButton;
    private JButton cancelButton;
    private DefaultListModel<String> listModel;
    private JList<String> jList;
    private String pathToSchemas;


    public PositionalDialog(JFrame frame, String pathToPositionalSchemas) {
        super(frame, "Select Schema", true);
        pathToSchemas = pathToPositionalSchemas;
        init();
        buildDialog();
        setResizable(false);
    }

    /**
     * Initializes and configures fields of this class.
     */
    private void init() {

        listModel = new DefaultListModel<>();
        jList = new JList<>(listModel);
        submitButton = new JButton("Submit");
        cancelButton = new JButton("Cancel");
        scrollPane = new JScrollPane(jList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        configureJList();
        addSchemaFileNamesToListModel(new File(pathToSchemas).listFiles());
    }


    /**
     * Creates a JPanel and adds necessary components. This JPanel
     * is added to the content pane of this Dialog.
     */
    private void buildDialog() {

        final JPanel dialogPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();

        if (listModel.isEmpty()) {

            dialogPanel.add(new JLabel("No Positional Schemas Exist!"), gbc);

        } else {

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(submitButton);
            buttonPanel.add(cancelButton);

            gbc.anchor = GridBagConstraints.NORTH;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(3,3,3,3);
            gbc.weighty = .1;
            dialogPanel.add(scrollPane, gbc);

            gbc.weightx = .5;
            gbc.anchor = GridBagConstraints.SOUTH;
            gbc.gridy = 1;
            dialogPanel.add(buttonPanel, gbc);
        }

        getContentPane().add(dialogPanel);
    }

    /**
     * Extracts file names from an Array of Files into an List.
     * Sorts the file name List and adds them to listModel.
     *
     * @param schemaFiles The Array of schema files.
     */
    private void addSchemaFileNamesToListModel(final File[] schemaFiles) {

        List<String> schemaFileNames = new ArrayList<>();

        for (File f : schemaFiles) {
            schemaFileNames.add(f.getName());
        }

        Collections.sort(schemaFileNames, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareToIgnoreCase(t1);
            }
        });

        for (int i = 0; i < schemaFileNames.size(); i++) {
            listModel.add(i, schemaFileNames.get(i));
        }
    }

    /**
     * Configures the JList by setting layout and selection mode.
     * Attaches a mouse listener to allow for double-clicking instead
     * of clicking the "Submit" button.
     */
    private void configureJList() {

        jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);

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

    //// Getters ////
    public JButton getSubmitButton() { return submitButton; }
    public JButton getCancelButton() { return cancelButton; }
    public JList<String> getjList() { return jList; }
}

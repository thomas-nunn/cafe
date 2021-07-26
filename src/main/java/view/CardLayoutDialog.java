package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


/**
 * Created by tmn7 on 8/9/18.
 */
public class CardLayoutDialog extends JDialog {

    /**
     * The main panel that hold all the components
     */
    private JPanel mainPanel;

    /**
     * The panel that holds JButtons for selecting components of the cardLayout.
     */
    private JPanel cardSelectionPanel;

    /**
     * The panel that holds action buttons for implementing changes.
     */
    private JPanel controlPanel;

    /**
     * The panel that holds user selected panels.
     */
    private JPanel selectedPanel;

    private CardLayout cardLayout;
    private Insets insets;
    private Dimension selectButtonMaxDims;
    private java.util.List<JButton> selectButtonList;
    private java.util.List<JButton> controlButtonList;


    public CardLayoutDialog(final JFrame frame, final String title) {
        super(frame, title);
        setModal(true);
        setLocationRelativeTo(frame);
        initFields();
        buildDialog();
    }

    private void initFields() {

        cardLayout = new CardLayout();
        mainPanel = new JPanel(new GridBagLayout());
        cardSelectionPanel = new JPanel(new GridBagLayout());
        controlPanel = new JPanel(new GridBagLayout());
        selectedPanel = new JPanel(cardLayout);

        insets = new Insets(2,2,2,2); // top,left,bottom,right
        selectButtonMaxDims = new Dimension(0,0);
        selectButtonList = new ArrayList<>();
        controlButtonList = new ArrayList<>();
    }

    private void buildDialog() {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.insets = insets;
        mainPanel.add(cardSelectionPanel, gbc);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = insets;
        mainPanel.add(selectedPanel, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = insets;
        mainPanel.add(controlPanel, gbc);

        this.getContentPane().add(mainPanel);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Adds a new JPanel to the cardLayout and creates the
     * JButton that will select it.
     * @param jPanel The new panel.
     * @param panelName The name of the new panel.
     */
    public void addPanelToCardLayout(final JPanel jPanel, final String panelName) {

        cardLayout.addLayoutComponent(jPanel, panelName);
        selectedPanel.add(jPanel, panelName);

        final JButton panelSelectButton = new JButton(panelName);
        addSelectionButton(panelSelectButton);

        panelSelectButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardLayout.show(selectedPanel, panelName);
                selectedPanel.revalidate();
            }
        });
    }

    /**
     * Adds a new panel selection button. Organizes all buttons so that
     * they start at the top of the cardSelectionPanel and any extra space
     * follows the last button.
     * @param selectionButton The JButton to add.
     */
    private void addSelectionButton(JButton selectionButton) {

        // Remove all components to guarantee organization of buttons
        cardSelectionPanel.removeAll();

        // resize all buttons to match largest
        Dimension sbDims = selectionButton.getPreferredSize();
        selectButtonMaxDims = (sbDims.getWidth() > selectButtonMaxDims.getWidth()) ? sbDims : selectButtonMaxDims;
        selectionButton.setPreferredSize(selectButtonMaxDims);

        GridBagConstraints gbc = new GridBagConstraints();

        // add back all existing buttons
        for (int i = 0; i < selectButtonList.size(); i++) {

            JButton jb = selectButtonList.get(i);
            jb.setPreferredSize(selectButtonMaxDims);

            gbc.anchor = GridBagConstraints.NORTH;
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.insets = insets;

            cardSelectionPanel.add(jb,gbc);
        }

        cardSelectionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // add new button last with weights to make it take up remainder of container
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = 0;
        gbc.gridy = selectButtonList.size();
        gbc.weightx = .01;
        gbc.weighty = .01;
        gbc.insets = insets;

        cardSelectionPanel.add(selectionButton,gbc);
        selectButtonList.add(selectionButton);
    }

    public void addControlButton(JButton controlButton) {

        // Remove all components to guarantee organization of buttons
        controlPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        Insets cbInsets = new Insets(7,2,4,2); // top,left,bottom,right

        // add back all existing buttons in descending order
        for (int i = controlButtonList.size() - 1; i >= 0; i--) {

            JButton jb = controlButtonList.get(i);

            gbc.anchor = GridBagConstraints.EAST;
            gbc.gridx = i+1;
            gbc.gridy = 0;
            gbc.insets = cbInsets;
            controlPanel.add(jb,gbc);
        }

        // add new button last with weights to make it take up remainder of container
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = .01;
        gbc.weighty = .01;
        gbc.insets = cbInsets;
        controlPanel.add(controlButton,gbc);

        controlButtonList.add(controlButton);
        controlPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public JPanel getCardSelectionPanel() {
        return cardSelectionPanel;
    }

    public java.util.List<JButton> getSelectButtonList() {
        return selectButtonList;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getSelectedPanel() {
        return selectedPanel;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

}

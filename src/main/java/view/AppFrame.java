package view;

import view.favorite.FavMenuPanel;
import view.file.TabPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AppFrame extends JFrame {

    Map<String,Integer> frameDims;
    private final JTabbedPane jTabbedPane = new JTabbedPane();
    private final JMenuBar menuBar = new JMenuBar();
    private final JMenu fileMenu = new JMenu("File");
    private final JMenu editMenu = new JMenu("Edit");
    private final JMenu viewMenu = new JMenu("View");
    private final JMenu optsMenu = new JMenu("Options");
    private final JMenu favsMenu = new JMenu("Favorites");
    private final JMenu helpMenu = new JMenu("Help");
    private final JMenu searchMenu = new JMenu("Search");
    private ButtonGroup textAreaOrientationButtonGroup = new ButtonGroup();
    private ButtonGroup textAreaPositionButtonGroup = new ButtonGroup();
    private ButtonGroup jTreeOrientationButtonGroup = new ButtonGroup();
    private ButtonGroup jTreePositionButtonGroup = new ButtonGroup();
    private FavMenuPanel favMenuPanel;


    /**
     * Constructor.
     *
     * @param myFrameDims A Map holding the dimensions for this frame.
     */
    public AppFrame(final Map<String,Integer> myFrameDims) {
        super("CAFE");
        frameDims = myFrameDims;
        buildUI(frameDims.get("x"), frameDims.get("y"), frameDims.get("width"), frameDims.get("height"));
    }

    /**
     * Builds and configures this JFrame.
     *
     * @param x X coordinate of the top left corner of this MasterFrame.
     * @param y Y coordinate of the top left corner of this MasterFrame.
     * @param w The width this MasterFrame should be.
     * @param h The height this MasterFrame should be.
     */
    public void buildUI(int x, int y, int w, int h) {

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(favsMenu);
        menuBar.add(viewMenu);
        menuBar.add(searchMenu);
        menuBar.add(optsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
        add(jTabbedPane);

        setResizable(true);
        setBounds(x, y, w, h);
        setPreferredSize(new Dimension(w, h));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        pack();
    }

    public void addItemToFileMenu(JMenuItem jMenuItem) {
        fileMenu.add(jMenuItem);
    }
    public void addItemtoSearchMenu(JMenuItem searchMenuItem) {
        searchMenu.add(searchMenuItem);
    }
    public void addItemToHelpMenu(JMenuItem jMenuItem) {
        helpMenu.add(jMenuItem);
    }
    public void addItemToEditMenu(JMenuItem jMenuItem) {
        editMenu.add(jMenuItem);
    }
    public void addItemToOptsMenu(JMenuItem jMenuItem) {
        optsMenu.add(jMenuItem);
    }

    /**
     * Adds a JRadioButton to viewMenu and the appropriate button group.
     *
     * @param jRadioButton The radio button to add.
     * @param buttonGroup The button group that the radio button belongs to.
     */
    public void addRadioButtontoViewMenu(final JRadioButton jRadioButton, final String buttonGroup) {

        viewMenu.add(jRadioButton);

        switch (buttonGroup) {

            case "text-area-orientation":
                textAreaOrientationButtonGroup.add(jRadioButton);
                break;
            case "text-area-position":
                textAreaPositionButtonGroup.add(jRadioButton);
                break;
            case "jTree-orientation":
                jTreeOrientationButtonGroup.add(jRadioButton);
                break;
            case "jTree-position":
                jTreePositionButtonGroup.add(jRadioButton);
        }
    }

    /**
     * Adds an item to the view menu.
     *
     * @param jMenuItem The JMenuItem to add.
     * @param addSeparator True if a separator should be added.
     * @param separatorBeforeItem True if separator should be added before the JMenuItem.
     */
    public void addItemtoViewMenu(JMenuItem jMenuItem, boolean addSeparator, boolean separatorBeforeItem) {

        if (addSeparator) {
            if (separatorBeforeItem) {
                viewMenu.addSeparator();
                viewMenu.add(jMenuItem);
            } else {
                viewMenu.add(jMenuItem);
                viewMenu.addSeparator();
            }
        } else {
            viewMenu.add(jMenuItem);
        }
    }

    public void addItemToFavsMenu(Component component, boolean addSeparator) {
        favsMenu.add(component);
        if (addSeparator) favsMenu.addSeparator();
    }

    public void addFavMenuPanelToFavsMenu(FavMenuPanel fmp) {

        if (favMenuPanel != null) {
            favsMenu.remove(favMenuPanel);
        }

        favMenuPanel = fmp;
        addItemToFavsMenu(fmp,false);
    }

    /**
     * Adds TabPanels to the JTabbedPane.
     * If a tab already exists with the same absolute path then
     * its TabPanel is replaced with the new one. Replacing existing
     * TabPanels doesn't fire a changeEvent on the JTabbedPane, so the
     * last TabPanel added is passed back from this method for use in
     * updating the currently selected TabPanel
     *
     * @param tabPanelMap A map of absolute file paths to TabPanels.
     * @return The last TabPanel added.
     */
    public TabPanel addTabPanelsToTabbedPane(Map<String,TabPanel> tabPanelMap) {

        TabPanel lastTabPanelAdded = null;

        for (String absPath : tabPanelMap.keySet()) {

            TabPanel tabPanel = tabPanelMap.get(absPath);
            lastTabPanelAdded = tabPanel;

            int indexOfExistingTab = jTabbedPane.indexOfTab(absPath);
            int indexOfNewTab;

            if (indexOfExistingTab >= 0) {
                jTabbedPane.setComponentAt(indexOfExistingTab, tabPanel);
                indexOfNewTab = indexOfExistingTab;
            } else {
                jTabbedPane.addTab(absPath, tabPanel);
                indexOfNewTab = jTabbedPane.indexOfTab(absPath);
            }

            jTabbedPane.setTabComponentAt(indexOfNewTab, tabPanel.getTabComponentPanel());
            jTabbedPane.setSelectedIndex(indexOfNewTab);
        }
        return lastTabPanelAdded;
    }

    /**
     * Closes a tab of this Class' JTabbedPane.
     *
     * @param tabName The tab's name.
     * @return true if the tab was successfully closed.
     */
    public boolean closeTab(String tabName) {

        boolean result = false;
        int index = jTabbedPane.indexOfTab(tabName);

        if (index >= 0) {
            TabPanel tabPanel = (TabPanel) jTabbedPane.getComponentAt(index);
            JButton closeButton = tabPanel.getTabComponentPanel().getCloseButton();
            closeButton.removeActionListener(closeButton.getActionListeners()[0]);
            jTabbedPane.removeTabAt(index);
            result = true;
        }

        return result;
    }

    public void setEnabledForCurrentTab(TabPanel tabPanel) {

        for (Component component : optsMenu.getMenuComponents()) {

            if (component instanceof JMenuItem) {
                JMenuItem jMenuItem = (JMenuItem) component;

                if (jMenuItem.getActionCommand().equals("Unstream File")) {
                    // TODO set enabled. Maybe a TabPanel should hold the boolean determining unstreamability.
                } else if (jMenuItem.getActionCommand().equals("Insert Delimited Header")) {
                    // TODO set enabled. Maybe a TabPanel should hold the boolean determining if a header can be inserted.
                }
            }
        }
    }

    /**
     * Updates settings (color, font, etc) for each TabPanel.
     */
    public void updateUI() {
        for (Component component : jTabbedPane.getComponents()) {
            if (component instanceof TabPanel) {
                TabPanel tempTabPanel = (TabPanel) component;
                tempTabPanel.updateMyUI();
            }
        }
    }

    /**
     * Set the tabbed pane's layout policy for tabs. Either tabs will
     * scroll on one line or wrap.
     *
     * @param thePolicy The Integer indicating scroll or wrap.
     */
    public void setTabPaneLayoutPolicy(Integer thePolicy) {
        jTabbedPane.setTabLayoutPolicy(thePolicy);
    }

    public JTabbedPane getjTabbedPane() {
        return jTabbedPane;
    }

}

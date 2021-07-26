package controller;

/**
 * Created by tmn7 on 7/29/18.
 */

import model.resources.PathManager;
import view.AppFrame;
import view.file.*;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmn7 on 9/1/17.
 */
public class MenuCntrl {

    private final App myApp;
    private final FavCntrl favController;
    private final SettingsCntrl settingsCntrl;
    private JMenuItem undoMenuItem = new JMenuItem();
    private JMenuItem redoMenuItem = new JMenuItem();
    private Integer newFileCount;


    /**
     * Constructor.
     *
     * @param app The Application's main controller Class.
     */
    MenuCntrl(final App app) {

        myApp = app;
        favController = myApp.getFavController();
        settingsCntrl = myApp.getSettingsCntrl();
        newFileCount = 0;
    }

    /**
     * Calls methods for creating all JMenuItems that will be
     * added to the AppFrame's JMenuBar.
     */
    void createAllMenuItems() {

        AppFrame myAppFrame = myApp.getAppFrame();

        createFileMenuItems(myAppFrame);
        createEditMenuItems(myAppFrame);
        createFavoriteMenuItems(myAppFrame);
        createViewMenuItems(myAppFrame);
        createSearchMenuItems(myAppFrame);
        createOptionsMenuItems(myAppFrame);
        createHelpMenuItems(myAppFrame);

        myAppFrame.pack();
    }

    /**
     * Creates and adds menu items to the File menu.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createFileMenuItems(AppFrame myAppFrame) {

        myAppFrame.addItemToFileMenu(createMenuItem("New", getNewFileAction(), null));
        myAppFrame.addItemToFileMenu(createMenuItem("Open", getOpenFileAction(), null));
        myAppFrame.addItemToFileMenu(createMenuItem("Save", getSaveAction(), KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)));
        myAppFrame.addItemToFileMenu(createMenuItem("Save As", getSaveAsAction(), null));
        myAppFrame.addItemToFileMenu(createMenuItem("Close", getCloseAction(), null));
        myAppFrame.addItemToFileMenu(createMenuItem("Close All", getCloseAllAction(), null));
        myAppFrame.addItemToFileMenu(createMenuItem("Exit", getExitAction(), null));
    }

    /**
     * Creates and adds menu items to the Edit menu.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createEditMenuItems(AppFrame myAppFrame) {

        myAppFrame.addItemToEditMenu(createMenuItem("Cut", new DefaultEditorKit.CutAction(), KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK)));
        myAppFrame.addItemToEditMenu(createMenuItem("Copy", new DefaultEditorKit.CopyAction(), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK)));
        myAppFrame.addItemToEditMenu(createMenuItem("Paste", new DefaultEditorKit.PasteAction(), KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK)));
        undoMenuItem = createMenuItem("Undo", myApp.getUndoRedoCntrl().getUndoAction(), KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        redoMenuItem = createMenuItem("Redo", myApp.getUndoRedoCntrl().getRedoAction(), KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        myAppFrame.addItemToEditMenu(undoMenuItem);
        myAppFrame.addItemToEditMenu(redoMenuItem);
    }

    /**
     * Creates and adds menu items to the Favorites menu.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createFavoriteMenuItems(AppFrame myAppFrame) {
        myAppFrame.addItemToFavsMenu(createMenuItem("Manage Favorites", favController.getFavMgrAction(),null), true);
    }

    /**
     * Creates and adds menu items to the View menu.
     * Radio buttons are selected based on values stored in the ViewValues.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createViewMenuItems(AppFrame myAppFrame) {

        boolean setTextAreaHorizontal = (myApp.getPropsControl().getMyAppProperties().getTextAreaOrientation() == 1); // 1 = JSplitPane.HORIZONTAL_SPLIT, 0 = JSplitPane.VERTICAL_SPLIT
        boolean setTextAreaTopLeft = (myApp.getPropsControl().getMyAppProperties().getTextAreaPosition() == 0); // 0 = left/top, 1 = bottom/right

        myAppFrame.addItemtoViewMenu(createMenuItem("Settings", settingsCntrl.getSettingsDialogAction(), null),true,false);

        myAppFrame.addItemtoViewMenu(createMenuItem("Text Area Orientation", null, null), false, false);
        myAppFrame.addRadioButtontoViewMenu(createViewRadioButton("Horizontal", getOrientationAction("Text_Area"), setTextAreaHorizontal),"text-area-orientation");
        myAppFrame.addRadioButtontoViewMenu(createViewRadioButton("Vertical", getOrientationAction("Text_Area"), !setTextAreaHorizontal),"text-area-orientation");

        myAppFrame.addItemtoViewMenu(createMenuItem("Text Area Position", null, null), true, true);
        myAppFrame.addRadioButtontoViewMenu(createViewRadioButton("Top/Left", getPositionAction("Text_Area"), setTextAreaTopLeft),"text-area-position");
        myAppFrame.addRadioButtontoViewMenu(createViewRadioButton("Bottom/Right", getPositionAction("Text_Area"), !setTextAreaTopLeft),"text-area-position");
    }

    /**
     * Creates and adds menu items to the Search menu.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createSearchMenuItems(AppFrame myAppFrame) {
        myAppFrame.addItemtoSearchMenu(createMenuItem("Find", getSearchMenuItemAction(), KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK)));
        myApp.getSearchCntrl().attachListeners();
    }

    /**
     * Creates and adds menu items to the Options menu.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createOptionsMenuItems(AppFrame myAppFrame) {
        myAppFrame.addItemToOptsMenu(createMenuItem("Explore File Location", getExploreAction(), null));
        myAppFrame.addItemToOptsMenu(createMenuItem("Insert Delimited Header", getInsertHeaderAction(), null));
        myAppFrame.addItemToOptsMenu(createMenuItem("SVN Update Current Tab's File", getSvnButtonAction(), null));
    }

    /**
     * Creates and adds menu items to the Help menu.
     * @param myAppFrame The AppFrame who's JMenuBar the menu items will be added to.
     */
    private void createHelpMenuItems(AppFrame myAppFrame) {
        myAppFrame.addItemToHelpMenu(createMenuItem("Help", getHelpAction(), null));
    }

    /**
     * Creates a JMenuItem with Action and Accelerator.
     *
     * @param action The action listener for the item.
     * @return The JMenuItem
     */
    JMenuItem createMenuItem(final String text, final Action action, KeyStroke keyStroke) {

        final JMenuItem jMenuItem = new JMenuItem(action);
        jMenuItem.setText(text);

        if (keyStroke != null) {
            jMenuItem.setAccelerator(keyStroke);
        }

        return jMenuItem;
    }

    /**
     * Creates and configures a JRadioButton.
     * @param text The Action Command and also the text to display next to the button.
     * @param action The Action associated with the button.
     * @return The JRadioButton.
     */
    private JRadioButton createViewRadioButton(final String text, final Action action, final boolean setSelected) {

        final JRadioButton jRadioButton = new JRadioButton(text);
        jRadioButton.setActionCommand(text);
        jRadioButton.addActionListener(action);
        jRadioButton.setSelected(setSelected);
        return jRadioButton;
    }

    /**
     * Create a JCheckBoxMenuItem and add it to the JMenu.
     *
     * @param action The action listener for the item.
     */
    public JCheckBoxMenuItem createCheckBoxItem(final Action action) {
        final JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(action);
        jCheckBoxMenuItem.setSelected(true);
        return jCheckBoxMenuItem;
    }


    /**
     * Switches the layout of each tab's JSplitPane if it has one.
     *
     * @return The Action.
     */
    private AbstractAction getOrientationAction(final String componentType) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int orientation = (actionEvent.getActionCommand().equals("Horizontal")) ? JSplitPane.HORIZONTAL_SPLIT : JSplitPane.VERTICAL_SPLIT;
                myApp.getPropsControl().getMyAppProperties().setTextAreaOrientation(orientation);

                for (TabPanel tabPanel : myApp.getTabPanelMap().values()) {
                    tabPanel.updateMyUI();
                }
            }
        };
    }

    private AbstractAction getPositionAction(final String componentType) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Integer position = (actionEvent.getActionCommand().equals("Top/Left")) ? 0 : 1;
                myApp.getPropsControl().getMyAppProperties().setTextAreaPosition(position);

                for (TabPanel tabPanel : myApp.getTabPanelMap().values()) {
                    tabPanel.updateMyUI();
                }
            }
        };
    }

    private AbstractAction getInsertHeaderAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                TabPanel tabPanel = myApp.getCurrentTabPanel();

                if (tabPanel.getDisplayPanel() instanceof DelimitedPanel) {

                    DelimitedPanel delimitedPanel = (DelimitedPanel) tabPanel.getDisplayPanel();
                    DelimitedHeaderDialog delimitedHeaderDialog = new DelimitedHeaderDialog(myApp.getAppFrame());

                    delimitedHeaderDialog.getSubmitButton().addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {

                            String selectedHeader = delimitedHeaderDialog.getjList().getSelectedValue();

                            if (selectedHeader != null && !selectedHeader.equals("")) {
                                List<String> headerFields = getHeaderFromFile(new File(PathManager.getDelimitedHeaderPath() + selectedHeader));
                                delimitedPanel.setFakeHeader(headerFields);
                                delimitedHeaderDialog.dispose();
                            }
                        }
                    });

                    delimitedHeaderDialog.getCancelButton().addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            delimitedHeaderDialog.dispose();
                        }
                    });

                    delimitedHeaderDialog.pack();
                    delimitedHeaderDialog.setLocationRelativeTo(myApp.getAppFrame());
                    delimitedHeaderDialog.setVisible(true);
                }
            }
        };
    }

    /**
     * Action for creating a new file in this myApp.
     * @return The Action
     */
    private AbstractAction getNewFileAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newFileCount++;
                myApp.createTabForNewFile("NEW FILE " +
                        "" + newFileCount);
            }
        };
    }

    /**
     * Opens a file chooser.
     * If the file exists and isn't a directory, a new tab is created.
     * @return The Action.
     */
    private AbstractAction getOpenFileAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                JFileChooser jFileChooser = getJFileChooser();
                int userOption = jFileChooser.showOpenDialog(myApp.getAppFrame());

                if (userOption == JFileChooser.APPROVE_OPTION) {
                    openFile(jFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        };
    }

    /**
     * Reads a header file and populates a List of String.
     *
     * @param headerFile The file to read.
     * @return The List of column headers.
     */
    private List<String> getHeaderFromFile(File headerFile) {
        List<String> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(headerFile))) {

            String line;

            while ((line = br.readLine()) != null) {
                result.add(line);
            }

        } catch (Exception ex2) {
            ex2.printStackTrace();
        }

        return result;
    }

    /**
     * Opens a file in this myApp and sets lastDirForFileChooser.
     *
     * @param absPath The absolute path of the file to open.
     */
    private void openFile(String absPath) {

        File file = new File(absPath);

        if (file.exists() && !file.isDirectory()) {

            List<File> fileList = new ArrayList<>();
            fileList.add(file);
            myApp.getPropsControl().getMyAppProperties().setLastDirForFileChooser(file.getParent());
            myApp.createTabPanels(fileList);
        }
    }

    public AbstractAction getHelpAction() {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {

            }
        };
    }

    /**
     * Action for opening a file explorer window based on
     * the currently selected TabPanel.
     * @return The Action
     */
    private AbstractAction getExploreAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                TabPanel tabPanel = myApp.getCurrentTabPanel();
                if (tabPanel != null) {
                    openFileLocation(tabPanel.getPlainTextModel().getAbsolutePath());
                }
            }
        };
    }

    private AbstractAction getSvnButtonAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                try {
                    //Runtime.getRuntime().exec("cmd /c echo \"Hello\"");
                    //Runtime.getRuntime().exec("cmd /c C:\\perl_scripts\\AAA_complete\\svn_commit.pl");

                    // TODO add check to make sure current tab's file is SVN versioned before running the SVN update command
                    final String absPath = myApp.getCurrentTabPanel().getPlainTextModel().getAbsolutePath();
                    myApp.getAppFrame().closeTab(absPath);

                    Process p = Runtime.getRuntime().exec("cmd /c svn update " + absPath);
                    p.waitFor();

                    List<File> fileList = new ArrayList<>();
                    fileList.add(new File(absPath));
                    myApp.createTabPanels(fileList);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };
    }

    /**
     * Exits the myApp if the user chooses yes.
     * @return The Action.
     */
    private AbstractAction getExitAction() {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                final int x = JOptionPane.showConfirmDialog(myApp.getAppFrame(), "Are you sure you want to exit?");
                if (x == 0) {
                    myApp.getPropsControl().saveMyAppProperties();
                    System.exit(0);
                }
            }
        };
    }

    /**
     * Saves the currently selected file. If file doesn't exist yet, then
     * a new file is created. Otherwise, the file is overwritten with data from this myApp.
     *
     * @return The Action.
     */
    private AbstractAction getSaveAction() {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {

                TabPanel tabPanel = myApp.getCurrentTabPanel();

                if (new File(tabPanel.getPlainTextModel().getAbsolutePath()).exists()) {
                    tabPanel.updateTabComponent(tabPanel.getPlainTextModel().save());
                } else {
                    saveAs();
                }
            }
        };
    }

    /**
     * Calls the saveAs() helper method when this Action occurs.
     *
     * @return The Action
     */
    private AbstractAction getSaveAsAction() {
        return new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                saveAs();
            }
        };
    }


    /**
     * Saves the currently selected tab as a new file.
     * If successful, the current tab is closed and a new tab created.
     */
    private void saveAs() {

        JTabbedPane tabbedPane = myApp.getAppFrame().getjTabbedPane();
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        TabPanel tabPanel = myApp.getCurrentTabPanel();

        JFileChooser jFileChooser = getJFileChooser();
        int userOption = jFileChooser.showSaveDialog(myApp.getAppFrame());

        if (userOption == JFileChooser.APPROVE_OPTION) {

            File file = jFileChooser.getSelectedFile();

            if (tabPanel.getPlainTextModel().saveAs(file.getAbsolutePath())) {

                myApp.getTabPanelMap().remove(tabPanel.getPlainTextModel().getAbsolutePath());
                tabbedPane.removeTabAt(selectedTabIndex);

                if (file.exists() && !file.isDirectory()) {
                    List<File> fileList = new ArrayList<>();
                    fileList.add(file);
                    myApp.createTabPanels(fileList);
                    myApp.getPropsControl().getMyAppProperties().setLastDirForFileChooser(file.getParent());
                }

            } else {

                final String message = "Saving " + file.getName() + " has failed!";
                JOptionPane.showMessageDialog(myApp.getAppFrame(), message, "Action Failed Message", 0);
            }
        }
    }

    /**
     * Opens a directory using the operating system's desktop.
     *
     * @param absPath The absolute path of the file whose directory is to be opened.
     */
    protected void openFileLocation(final String absPath) {

        File file = new File(absPath);

        if (file.exists()) {

            File dir = new File(file.getParent());
            try {
                Desktop.getDesktop().open(dir);
            } catch (IOException ioe) {
                System.out.println("openFileLocation IOException: " + ioe);
            }
        }
    }

    private AbstractAction getCloseAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                myApp.closeCurrentTab();
            }
        };
    }

    private AbstractAction getCloseAllAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                myApp.closeAllTabs();
            }
        };
    }

    private JFileChooser getJFileChooser() {
        File lastDirectory = new File(myApp.getPropsControl().getMyAppProperties().getLastDirForFileChooser());
        return (lastDirectory.isDirectory()) ? new JFileChooser(lastDirectory.getAbsolutePath()) : new JFileChooser();
    }

    private AbstractAction getSearchMenuItemAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                myApp.getSearchCntrl().displaySearchBox();
            }
        };
    }

    void removeUndoAction(final Action action) {
        undoMenuItem.removeActionListener(action);
    }
    void removeRedoAction(final Action action) {
        redoMenuItem.removeActionListener(action);
    }
    void setUndoMenuItemAction(final Action action) {
        undoMenuItem.setAction(action);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
    }
    void setRedoMenuItemAction(final Action action) {
        redoMenuItem.setAction(action);
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
    }

    // Getters
    FavCntrl getFavController() {return favController;}
    JMenuItem getUndoMenuItem() {return undoMenuItem;}
    JMenuItem getRedoMenuItem() {return redoMenuItem;}
}


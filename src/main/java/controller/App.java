package controller;

import controller.file.FileCntrl;
import controller.file.UndoRedoCntrl;
import model.resources.PathManager;
import view.AppFrame;
import view.file.TabPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class App {

    private final PropsCntrl propsControl;
    private final AppFrame appFrame;
    private final MenuCntrl menuCntrl;
    private final FavCntrl favController;
    private final FileCntrl fileCntrl;
    private final SearchCntrl searchCntrl;
    private final SettingsCntrl settingsCntrl;
    private final Map<String, TabPanel> tabPanelMap;
    private UndoRedoCntrl undoRedoCntrl;
    private TabPanel currentTabPanel;
    private UndoRedoCntrl.UndoListener currentUndoListener;


    /**
     * Constructor.
     */
    public App() {

        propsControl = new PropsCntrl(PathManager.getPropertiesFilePath());
        appFrame = new AppFrame(propsControl.getMyAppProperties().getFrameDims());
        favController = new FavCntrl(this);
        settingsCntrl = new SettingsCntrl(this);
        menuCntrl = new MenuCntrl(this);
        fileCntrl = new FileCntrl(this);
        searchCntrl = new SearchCntrl(this);
        tabPanelMap = new HashMap<>();
    }

    /**
     * Starting point for this application.
     * Configures the Frame and opens files previously left open.
     */
    void startApp() {

        setDropTarget(appFrame);
        appFrame.getjTabbedPane().addChangeListener(getChangeListenerForTabbedPane());
        appFrame.addWindowListener(getWindowCloseAction());
        appFrame.setTabPaneLayoutPolicy(propsControl.getMyAppProperties().getTabPolicy());

        List<File> filesLeftOpen = propsControl.getMyAppProperties().getFilesLeftOpen();
        if (filesLeftOpen.size() == 0) {
            createTabForNewFile("New File 1");
        } else {
            createTabPanels(filesLeftOpen);
        }
        menuCntrl.createAllMenuItems();
        menuCntrl.getFavController().addFavoritesToMenu();
    }

    /**
     * Handles creating a new file in the app.
     *
     * @param fileName The initial file name to be used.
     */
    void createTabForNewFile(String fileName) {

        Map<String,TabPanel> tabPanels = new HashMap<>();
        tabPanels.put(fileName, fileCntrl.createTabForNewFile(fileName));
        configureTabPanels(tabPanels);
        tabPanelMap.putAll(tabPanels);
        currentTabPanel = appFrame.addTabPanelsToTabbedPane(tabPanels);
    }

    /**
     * Creates a new tab for a JTabbedPane, one per file.
     * The currentTabPanel is set to the last added TabPanel.
     * This handles re-opening existing files, which won't fire
     * the JTabbedPane's changeEvent.
     *
     * @param fileList The list of files to create tabs for.
     */
    public void createTabPanels(List<File> fileList) {

        final List<File> plainTextFiles = new ArrayList<>();

        for (final File file : fileList) {

            if (file.exists()) {
                plainTextFiles.add(file);
            }
        }

        if (plainTextFiles.size() > 0) {

            Map<String,TabPanel> tabPanels = fileCntrl.createTabPanels(plainTextFiles);
            configureTabPanels(tabPanels);
            tabPanelMap.putAll(tabPanels);
            currentTabPanel = appFrame.addTabPanelsToTabbedPane(tabPanels);
        }
    }

    /**
     *
     * @param tabPanels
     */
    private void configureTabPanels(Map<String,TabPanel> tabPanels) {

        for (TabPanel tp : tabPanels.values()) {

            setDropTarget(tp.getjSplitPane());
            setDropTarget(tp.getPlainTextModel().getTextArea());
        }
    }

    /**
     * Enables drag and drop on a component.
     * Opens file(s) in the app that are dropped on the component.
     *
     * @param component The component to enable drag and drop on.
     */
    @SuppressWarnings("unchecked")
    public void setDropTarget(final Component component) {

        new DropTarget(component, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {}

            @Override
            public void dragOver(DropTargetDragEvent dropTargetDragEvent) {}

            @Override
            public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {}

            @Override
            public void dragExit(DropTargetEvent dropTargetEvent) {}

            @Override
            public void drop(DropTargetDropEvent dropTargetDropEvent) {

                try {
                    // Accept the drop first, important!
                    dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY);
                    ArrayList<File> fileList = new ArrayList<>((java.util.List) dropTargetDropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                    createTabPanels(fileList);

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Listener for a JTabbedPane. Listens for focus changing from one tab to another.
     * Sets currentTabPanel to the newly selected TabPanel and handles undo/redo updating.
     * @return A new ChangeListener.
     */
    private ChangeListener getChangeListenerForTabbedPane() {
        return new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {

                JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                Component selectedComp = sourceTabbedPane.getSelectedComponent();

                if (selectedComp instanceof TabPanel) {
                    if (undoRedoCntrl != null) removeCurrentUndoRedoListeners(); // remove currentTabPanel listeners before reassigning to newly selected TabPanel
                    currentTabPanel = (TabPanel) selectedComp;
                    searchCntrl.setTabPanel(currentTabPanel);
                    addUndoRedoToCurrentTabPanel();
                }
            }
        };
    }

    /**
     * Removes undo/redo listeners for current TabPanel.
     */
    private void removeCurrentUndoRedoListeners() {

        if (currentTabPanel != null) {
            currentTabPanel.getTextArea().getDocument().removeUndoableEditListener(currentUndoListener);
        }

        if (menuCntrl.getUndoMenuItem().getActionListeners().length > 0) {
            menuCntrl.removeUndoAction(undoRedoCntrl.getUndoAction());
        }

        if (menuCntrl.getRedoMenuItem().getActionListeners().length > 0) {
            menuCntrl.removeRedoAction(undoRedoCntrl.getRedoAction());
        }
    }

    /**
     * Configures undo/redo for currentTabPanel.
     */
    private void addUndoRedoToCurrentTabPanel() {

        if (currentTabPanel != null) {

            undoRedoCntrl = new UndoRedoCntrl();
            UndoRedoCntrl.UndoListener undoListener = undoRedoCntrl.getUndoListener();
            currentTabPanel.getTextArea().getDocument().addUndoableEditListener(undoListener);
            currentUndoListener = undoListener;
            menuCntrl.setUndoMenuItemAction(undoRedoCntrl.getUndoAction());
            menuCntrl.setRedoMenuItemAction(undoRedoCntrl.getRedoAction());
        }
    }

    /**
     * Handles closing a tab of a JTabbedPane.
     * @param tabName The absolute path of the file for the tab.
     * @return The Action.
     */
    public AbstractAction getCloseAction(final String tabName) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeTab(tabName);
            }
        };
    }

    /**
     * Closes the currently selected TabPanel.
     */
    public void closeCurrentTab() {
        closeTab(currentTabPanel.getPlainTextModel().getAbsolutePath());
    }

    /**
     * Calls closeTab method on all TabPanels in tabPanelMap.
     * To avoid ConcurrentModificationException, tabPanelMap isn't
     * iterated over directly.
     */
    public void closeAllTabs() {

        Set<String> tabNameList = new HashSet<>(tabPanelMap.keySet());

        for (String tabName : tabNameList) {
            closeTab(tabName);
        }
    }

    /**
     * Closes a tab of the Frame's JTabbedPane.
     *
     * @param tabName The name of the tab to close.
     */
    private void closeTab(final String tabName) {

        if (appFrame.closeTab(tabName)) {
            tabPanelMap.remove(tabName);
        }
    }

    /**
     * Handles the closing of the app window.
     * @return The WindowListener
     */
    private WindowListener getWindowCloseAction() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                propsControl.getMyAppProperties().setFilesLeftOpen(getOpenFiles(getOpenFilePaths()));
                propsControl.getMyAppProperties().setFrameDims(getFrameDims());
                propsControl.saveMyAppProperties();
                System.out.println(propsControl.getMyAppProperties().toString());
            }
        };
    }

    /**
     * @return A List of the currently open files' absolute paths.
     */
    private List<String> getOpenFilePaths() {

        List<String> openFilePaths = new ArrayList<>();
        for (int i = 0; i < appFrame.getjTabbedPane().getTabCount(); i++) {
            openFilePaths.add(appFrame.getjTabbedPane().getTitleAt(i));
        }
        return openFilePaths;
    }

    /**
     * @param openFilePaths A List of File absolute paths
     * @return A List of File Objects representing the Files currently open in the app.
     */
    private List<File> getOpenFiles(List<String> openFilePaths) {

        List<File> openFiles = new ArrayList<>();
        for (String path : openFilePaths) {
            File file = new File(path);
            if (file.exists()) {
                openFiles.add(file);
            }
        }
        return openFiles;
    }

    /**
     * @return A Map containing the Frame's position and size.
     */
    private Map<String,Integer> getFrameDims() {

        Map<String,Integer> frameDims = new HashMap<>();
        Rectangle rec = appFrame.getBounds();
        frameDims.put("x", rec.x);
        frameDims.put("y", rec.y);
        frameDims.put("width", rec.width);
        frameDims.put("height", rec.height);
        return frameDims;
    }

    // Getters
    public UndoRedoCntrl getUndoRedoCntrl() {return undoRedoCntrl;}
    public MenuCntrl getMenuCntrl() {return menuCntrl;}
    public SearchCntrl getSearchCntrl() {return searchCntrl;}
    public PropsCntrl getPropsControl() { return propsControl; }
    public FavCntrl getFavController() { return favController; }
    public SettingsCntrl getSettingsCntrl() { return settingsCntrl; }
    public Map<String, TabPanel> getTabPanelMap() {return tabPanelMap;}
    public TabPanel getCurrentTabPanel() {return currentTabPanel;}
    public AppFrame getAppFrame() { return appFrame; }
}

package controller;

import view.CardLayoutDialog;
import view.favorite.AddFavPanel;
import view.favorite.FavMenuPanel;
import view.favorite.FavSelectionAddPanel;
import view.favorite.FavSelectionDeletePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.*;

/**
 * Created by tmn7 on 7/29/18.
 */
public class FavCntrl {

    private final App myApp;


    public FavCntrl(final App app) {
        myApp = app;
    }

    /**
     *
     */
    void addFavoritesToMenu() {

        final FavMenuPanel favMenuPanel = new FavMenuPanel();

        Map<String, Map<String, List<String>>> groups = getSortedFavorites(myApp.getPropsControl().getMyAppProperties().getFavorites());

        for (String groupTitle : groups.keySet()) {
            for (String favTitle : groups.get(groupTitle).keySet()) {
                favMenuPanel.addFavToGroup(groupTitle, favTitle, groups.get(groupTitle).get(favTitle));
            }
        }

        myApp.getAppFrame().addFavMenuPanelToFavsMenu(favMenuPanel);
        favMenuPanel.setScrollPaneSize(); // Important!
        final JTree jTree = favMenuPanel.getjTree();
        jTree.addMouseListener(getJTreeMouseListener(favMenuPanel,jTree));
    }

    /**
     *
     * @param favMenuPanel
     * @param jTree
     * @return
     */
    private MouseListener getJTreeMouseListener(FavMenuPanel favMenuPanel, JTree jTree) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int selRow = jTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());

                if(selRow != -1) {
                    if (mouseEvent.getClickCount() == 2) {

                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

                        if (node.isLeaf()) {

                            DefaultMutableTreeNode selectedParent = (DefaultMutableTreeNode) node.getParent();
                            List<File> filesToOpen = new ArrayList<>();
                            for (String fileName : favMenuPanel.getFavMasterMap().get(selectedParent.toString()).get(node.toString())) {
                                filesToOpen.add(new File(fileName));
                            }
                            if (!filesToOpen.isEmpty()) {
                                myApp.createTabPanels(filesToOpen);
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     *
     * @param unsortedFavs
     * @return
     */
    private Map<String, Map<String, List<String>>> getSortedFavorites(Map<String, Map<String, List<String>>> unsortedFavs) {

        Map<String, Map<String, List<String>>> sortedFavs = new TreeMap<>(); // sort by group name

        for (String group : unsortedFavs.keySet()) {

            Map<String, List<String>> favorite = new TreeMap<>(unsortedFavs.get(group)); // sort Favorites in the group by name

            for (String favName : favorite.keySet()) {
                Collections.sort(favorite.get(favName)); // sort the Favorite's List of Files by absolute path
            }
            sortedFavs.put(group,favorite); // add group to sortedFavs...sorting is automatic because it's a TreeMap
        }

        return sortedFavs;
    }

    /**
     * @return An Action for displaying the Favorites Manager.
     */
    AbstractAction getFavMgrAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayFavMgr();
            }
        };
    }

    /**
     * Creates and configures a CardLayoutDialog for use as a Favorites Manager.
     */
    private void displayFavMgr() {

        final CardLayoutDialog favMgr = new CardLayoutDialog(myApp.getAppFrame(), "Manage Favorites");
        final Map<String, Map<String, List<String>>> favorites = getSortedFavorites(myApp.getPropsControl().getMyAppProperties().getFavorites());
        final FavSelectionAddPanel fsAddPanel = new FavSelectionAddPanel(favorites);
        final FavSelectionDeletePanel fsDeletePanel = new FavSelectionDeletePanel(favorites);

        favMgr.addPanelToCardLayout(fsAddPanel, "Add Favorites");
        favMgr.addPanelToCardLayout(fsDeletePanel, "Delete Favorites");
        favMgr.addControlButton(getFavMgrSaveButton(favMgr,fsAddPanel,fsDeletePanel));
        favMgr.addControlButton(getFavMgrCancelButton(favMgr));

        setUpAddFavSelectionPanel(favMgr,fsAddPanel);

        favMgr.pack();
        favMgr.setLocationRelativeTo(myApp.getAppFrame());
        favMgr.setVisible(true);
    }

    /**
     * Creates a JButton for saving changes to Favorites.
     *
     * @param favMgr The Favorites Manager that this JButton is for.
     * @param fsap FavSelectionAddPanel for the Favorites Manager.
     * @param fsdp FavSelectionDeletePanel for the Favorites Manager.
     * @return The JButton for saving changes to Favorites.
     */
    private JButton getFavMgrSaveButton(CardLayoutDialog favMgr, FavSelectionAddPanel fsap, FavSelectionDeletePanel fsdp) {
        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(getSaveFavAction(favMgr,fsap,fsdp));
        return saveButton;
    }

    /**
     * Creates a JButton for cancelling and closing the Favorites Manager.
     *
     * @param favMgr The Favorites Manager that this JButton is for.
     * @return The JButton for cancelling and closing the Favorites Manager.
     */
    private JButton getFavMgrCancelButton(CardLayoutDialog favMgr) {
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(getCancelAction(favMgr));
        return cancelButton;
    }

    /**
     * Sets up a FavSelectionAddPanel by attaching listeners to appropriate
     * components and making some components dropTargets.
     * @param favSelectionAddPanel The FavSelectionAddPanel to set up.
     */
    private void setUpAddFavSelectionPanel(CardLayoutDialog favMgr, FavSelectionAddPanel favSelectionAddPanel) {

        final AddFavPanel favPanel1 = favSelectionAddPanel.getAddFavPanelList().get(0);
        getDropTarget(favMgr, favPanel1, favPanel1);
        JTextField jTextField = favPanel1.addTextField();
        getDropTarget(favMgr, jTextField, favPanel1);

        favPanel1.getAddFilePathButton().addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                final JTextField jTextField1 = favPanel1.addTextField();
                getDropTarget(favMgr, jTextField1, favPanel1);
                favMgr.pack();
            }
        });

        favSelectionAddPanel.getAddAnotherFavButton().addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                final AddFavPanel favPanel2 = favSelectionAddPanel.addFavoritePanel();
                getDropTarget(favMgr, favPanel2, favPanel2);
                JTextField jTextField2 = favPanel2.addTextField();
                getDropTarget(favMgr, jTextField2, favPanel2);
                favMgr.pack();

                favPanel2.getAddFilePathButton().addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        final JTextField jTextField3 = favPanel2.addTextField();
                        getDropTarget(favMgr, jTextField3, favPanel2);
                        favMgr.pack();
                    }
                });

            }
        });

        favSelectionAddPanel.getExploreButton().addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                myApp.getMenuCntrl().openFileLocation(myApp.getPropsControl().getMyAppProperties().getLastDirForFileChooser());
            }
        });
    }

    /**
     * Enables drag and drop on a Component.
     * Dragging and dropping equates to opening a file.
     */
    @SuppressWarnings("unchecked")
    private DropTarget getDropTarget(final CardLayoutDialog favMgr, final Component component, final AddFavPanel addFavPanel) {

        return new DropTarget(component, new DropTargetListener() {

            public void dragEnter(DropTargetDragEvent e) {}
            public void dragExit(DropTargetEvent e) {}
            public void dragOver(DropTargetDragEvent e) {}
            public void dropActionChanged(DropTargetDragEvent e) {}

            public void drop(DropTargetDropEvent e) {

                try {
                    // Accept the drop first, important!
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    ArrayList<File> fileList = new ArrayList<>((java.util.List) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
                    addFavs(favMgr, addFavPanel, fileList);

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Handles user cancelling the Favorites Manager by disposing of it (the CardLayoutDialog).
     *
     * @param favMgr The Favorites Manager that this Action is for.
     * @return The Action.
     */
    private AbstractAction getCancelAction(CardLayoutDialog favMgr) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                favMgr.dispose();
            }
        };
    }

    private StringBuilder getFormattedString(Map<String, Map<String, List<String>>> theMap) {
        StringBuilder sb = new StringBuilder();

        int groupCount = 1;

        for (String group : theMap.keySet()) {

            sb.append("  " + groupCount + ". Group: " + group + "\n");
            groupCount++;
            int favCount = 1;

            for (String fav : theMap.get(group).keySet()) {

                sb.append("    " + favCount + ". Favorite: " + fav + "\n");
                favCount++;
                int fileCount = 1;

                for (String file : theMap.get(group).get(fav)) {

                    sb.append("      " + fileCount + ". File: " + file + "\n");
                    fileCount++;
                }
            }
        }
        sb.append("\n\n");
        return sb;
    }

    private AbstractAction getSaveFavAction(CardLayoutDialog favMgr, FavSelectionAddPanel favSelectionAddPanel, FavSelectionDeletePanel favSelectionDeletePanel) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Map<String, Map<String, List<String>>> favsToCreate = getFavoritesToCreate(favSelectionAddPanel);
                Map<String, Map<String, List<String>>> favsToDelete = getFavoritesToDelete(favSelectionDeletePanel);

                StringBuilder sb = new StringBuilder();

                if (!favsToCreate.isEmpty()) {
                    sb.append("Create these Favorites?\n");
                    sb.append(getFormattedString(favsToCreate));
                }

                if (!favsToDelete.isEmpty()) {
                    sb.append("Delete these Favorites?\n");
                    sb.append(getFormattedString(favsToDelete));
                }


                if (favsToCreate.size() > 0 || favsToDelete.size() > 0) {

                    int userOption = JOptionPane.showConfirmDialog(myApp.getAppFrame(), sb);

                    if (userOption == JOptionPane.YES_OPTION) {

                        if (favsToCreate.size() > 0) {
                            createFavorites(favsToCreate);
                        }

                        if (favsToDelete.size() > 0) {
                            deleteFavorites(favsToDelete);
                        }
                    }
                }
                favMgr.dispose();
            }
        };
    }

    private Map<String, Map<String, List<String>>> getFavoritesToCreate(FavSelectionAddPanel favSelectionAddPanel) {

        Map<String, Map<String, List<String>>> favsToCreateMap = new HashMap<>();

        for (AddFavPanel addFavPanel : favSelectionAddPanel.getAddFavPanelList()) {

            List<String> filePaths = new ArrayList<>();

            String group = (String) addFavPanel.getGroupBox().getSelectedItem();
            String title = addFavPanel.getFavNameTextField().getText();
            List<JTextField> jtfList = addFavPanel.getJtfList();

            for (JTextField jtf : jtfList) {

                String filePath = jtf.getText();
                File file = new File(filePath);
                if (file.exists()) {
                    filePaths.add(filePath);
                }
            }
            if (!group.equals("") && !title.equals("") && !filePaths.isEmpty()) {

                if (favsToCreateMap.containsKey(group)) {
                    Map<String, List<String>> groupFavs = favsToCreateMap.get(group);
                    groupFavs.put(title,filePaths);
                } else {
                    Map<String, List<String>> groupFavs = new HashMap<>();
                    groupFavs.put(title,filePaths);
                    favsToCreateMap.put(group, groupFavs);
                }
            }
        }

        return favsToCreateMap;
    }

    /**
     * Creates new favorites and adds them to the properties file and FavMenuPanel.
     * @return A map of groups with their fav titles.
     */
    private void createFavorites(Map<String, Map<String, List<String>>> favsToCreateMap) {

        for (String groupTitle : favsToCreateMap.keySet()) {

            for (String favTitle : favsToCreateMap.get(groupTitle).keySet()) {

                List<String> filePaths = favsToCreateMap.get(groupTitle).get(favTitle);
                myApp.getPropsControl().addFavorite(groupTitle, favTitle, filePaths);
            }
        }

        addFavoritesToMenu();
    }


    /**
     *
     * Creates a Map of Favorites to delete by examining all
     * the JCheckBoxes in a FavSelectionDeletePanel.
     *
     * @return The Map of Favorites to delete.
     */
    private Map<String, Map<String, List<String>>> getFavoritesToDelete(FavSelectionDeletePanel favSelectionDeletePanel) {

        Map<String, Map<String, List<String>>> favsToDelete = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareToIgnoreCase(t1);
            }
        });

        List<JCheckBox> groupCheckBoxes = favSelectionDeletePanel.getGroupCheckBoxList();
        Map<String, List<JCheckBox>> favCheckBoxMap = favSelectionDeletePanel.getFavsToDeleteMap();
        Map<String, Map<String, List<JCheckBox>>> fileCheckBoxMap = favSelectionDeletePanel.getFavFilesToDeleteMap();

        for (JCheckBox groupCheckBox : groupCheckBoxes) {

            String group = groupCheckBox.getText();
            if (groupCheckBox.isSelected()) {

                favsToDelete.put(group, new HashMap<String, List<String>>());

            } else {

                Map<String, List<String>> favMap = new HashMap<>();

                for (JCheckBox favCheckBox : favCheckBoxMap.get(group)) {

                    String favTitle = favCheckBox.getText();
                    if (favCheckBox.isSelected()) {

                        favMap.put(favTitle, new ArrayList<String>());
                        favsToDelete.put(group, favMap);

                    } else {

                        List<String> fileList = new ArrayList<>();

                        for (JCheckBox fileCheckBox : fileCheckBoxMap.get(group).get(favTitle)) {

                            String fileName = fileCheckBox.getText();
                            if (fileCheckBox.isSelected()) {
                                fileList.add(fileName);
                            }
                        }

                        if (!fileList.isEmpty()) {

                            favMap.put(favTitle, fileList);
                            favsToDelete.put(group, favMap);
                        }
                    }
                }
            }
        }

        return favsToDelete;
    }

    /**
     *
     * Deletes any combination of Groups, Favorites, Files from the
     * properties file and the FavMenuPanel.
     *
     * @param favsToDelete The Map of Favorites to delete.
     */
    private void deleteFavorites(Map<String, Map<String, List<String>>> favsToDelete) {

        List<String> groupsToDelete = new ArrayList<>();

        for (String group : favsToDelete.keySet()) {

            if (favsToDelete.get(group).isEmpty()) {

                groupsToDelete.add(group);

            } else {

                List<String> groupFavs = new ArrayList<>();

                for (String favTitle : favsToDelete.get(group).keySet()) {

                    if (favsToDelete.get(group).get(favTitle).isEmpty()) {

                        groupFavs.add(favTitle);

                    } else {

                        Map<String, Map<String, List<String>>> filesToDeleteMap = new HashMap<>();
                        List<String> favFilesToDelete = new ArrayList<>();

                        for (String fileName : favsToDelete.get(group).get(favTitle)) {
                            favFilesToDelete.add(fileName);
                        }

                        if (!favFilesToDelete.isEmpty()) {
                            Map<String, List<String>> favAndFilesMap = new HashMap<>();
                            favAndFilesMap.put(favTitle,favFilesToDelete);
                            filesToDeleteMap.put(group,favAndFilesMap);
                            myApp.getPropsControl().deleteFavoriteFiles(filesToDeleteMap);
                        }
                    }
                }

                if (!groupFavs.isEmpty()) {
                    myApp.getPropsControl().deleteFavorites(group, groupFavs);
                }
            }
        }

        if (!groupsToDelete.isEmpty()) {
            myApp.getPropsControl().deleteGroups(groupsToDelete);
        }

        addFavoritesToMenu();
    }

    /**
     * Adds the absolute path of each file in fileList
     * to a JTextField in an AddFavPanel of the FavSelectionAddPanel.
     * @param fileList The List of Files
     */
    private void addFavs(CardLayoutDialog favMgr, AddFavPanel favPanel, ArrayList<File> fileList) {

        List<File> tempFileList = new ArrayList<>(fileList);
        List<JTextField> jtfList = favPanel.getJtfList();
        int jtfCount = jtfList.size();

        for (int i = 0; i < jtfCount; i++) {

            JTextField currentJTF = jtfList.get(i);

            if (currentJTF.getText().isEmpty()) {
                if (tempFileList.size() >= 1) {
                    currentJTF.setText(tempFileList.get(0).getAbsolutePath());
                    tempFileList.remove(0);
                }
            }
        }

        for (File f : tempFileList) {
            JTextField jTextField = favPanel.addTextField();
            jTextField.setText(f.getAbsolutePath());
            getDropTarget(favMgr, jTextField, favPanel);
        }
    }
}

package view.favorite;

import model.resources.ImageManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmn7 on 7/29/18.
 */
public class FavMenuPanel extends JPanel {

    private List<String> groups;
    private Map<String, Map<String, List<String>>> favMasterMap;
    private JTree jTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode root;
    private JScrollPane jScrollPane;
    private String longestFavTitle;


    public FavMenuPanel() {
        super();
        buildMenuItem();
        this.add(jScrollPane);
    }

    private void  buildMenuItem() {

        root = new DefaultMutableTreeNode("root");
        root.setAllowsChildren(true);

        treeModel = new DefaultTreeModel(root);

        jTree = getFavJTree(treeModel);
        jTree.setCellRenderer(new MyCellRenderer());
        jTree.setRootVisible(false);
        jTree.setBackground(this.getBackground());

        // allows ToolTips for jTree
        ToolTipManager.sharedInstance().registerComponent(jTree);

        jScrollPane = new JScrollPane(jTree);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());

        favMasterMap = new HashMap<>();
        groups = new ArrayList<>();
        longestFavTitle = "";
    }

    public boolean addFavToGroup(final String group, final String title, final List<String> filePaths) {

        boolean result = false;
        Map<String, List<String>> groupFavs;
        DefaultMutableTreeNode favorite;

        longestFavTitle = (title.length() > longestFavTitle.length()) ? title : longestFavTitle;

        if (favMasterMap.containsKey(group)) {

            groupFavs = favMasterMap.get(group);

            if (groupFavs.containsKey(title)) {

                List<String> favFiles = groupFavs.get(title);
                favFiles.addAll(filePaths);

            } else {

                favorite = new DefaultMutableTreeNode(title);

                for (int i = 0; i < root.getChildCount(); i++) {

                    DefaultMutableTreeNode current = (DefaultMutableTreeNode) treeModel.getChild(root, i);

                    if (current.toString().equals(group)) {

                        treeModel.insertNodeInto(favorite, current, current.getChildCount());
                        groupFavs.put(title, filePaths);
                        break;
                    }
                }
            }

        } else {

            DefaultMutableTreeNode parent = new DefaultMutableTreeNode(group);
            favorite = new DefaultMutableTreeNode(title);

            parent.setAllowsChildren(true);
            parent.add(favorite);

            treeModel.insertNodeInto(parent, root, root.getChildCount());
            groups.add(group);
            groupFavs = new HashMap<>();
            groupFavs.put(title, filePaths);
            favMasterMap.put(group, groupFavs);
        }

        return result;
    }

    /**
     * Removes group nodes from the JTree.
     *
     * @param groupsToRemove The List of groups to be removed
     * @return true if groups were removed
     */
    public boolean removeGroups(final List<String> groupsToRemove) {
        boolean result = false;

        for (String group : groupsToRemove) {

            if (groups.contains(group)) {

                for (int i = 0; i < root.getChildCount(); i++) {

                    DefaultMutableTreeNode current = (DefaultMutableTreeNode) treeModel.getChild(root, i);
                    String userObject = (String) current.getUserObject();

                    if (userObject.equals(group)) {

                        result = true;
                        favMasterMap.remove(group);
                        groups.remove(group);

                        treeModel.removeNodeFromParent(current);
                        treeModel.nodeStructureChanged(root);

                        //setScrollPaneSize();
                        //this.revalidate();
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Removes child nodes from group nodes in the JTree.
     *
     * @param group The group to remove favorites from
     * @param favTitles The List of favorites to be removed
     * @return true if favorites were removed
     */
    public boolean removeFavFromGroup(String group, List<String> favTitles) {
        boolean result = false;

        Map<String, List<String>> groupFavs;

        if (groups.contains(group)) {

            groupFavs = favMasterMap.get(group);

            for (int i = 0; i < root.getChildCount(); i++) {

                DefaultMutableTreeNode current = (DefaultMutableTreeNode) treeModel.getChild(root, i);

                if (current.toString().equals(group)) {

                    for (int j = 0; j < current.getChildCount(); j++) {

                        DefaultMutableTreeNode favorite = (DefaultMutableTreeNode) treeModel.getChild(current, j);

                        if (favTitles.contains(favorite.toString())) {

                            treeModel.removeNodeFromParent(favorite);
                            groupFavs.remove(favorite.toString());
                            result = true;
                        }
                    }

                    if (groupFavs.isEmpty()) {
                        treeModel.removeNodeFromParent(current);
                        favMasterMap.remove(group);
                        break;
                    }
                }
            }
        }

        return result;
    }


    public boolean removeFilesFromAFavorite(String group, String favorite, List<String> fileNames) {
        boolean result = false;

        if (favMasterMap.containsKey(group) && !fileNames.isEmpty()) {
            if (favMasterMap.get(group).containsKey(favorite)) {
                for (String fName : fileNames) {
                    favMasterMap.get(group).get(favorite).remove(fName);
                }
                result = true;
            }
        }

        return result;
    }

    /**
     * Returns a new JTree that supports ToolTips for
     * Leaf nodes only. ToolTips display the list
     * of files that a favorite will open.
     *
     * @param treeModel The DefaultTreeModel for this JTree
     * @return The JTree
     */
    private JTree getFavJTree(DefaultTreeModel treeModel) {

        return new JTree(treeModel) {

            @Override
            public String getToolTipText(MouseEvent ev) {

                StringBuilder sb = new StringBuilder();

                if (ev != null) {

                    TreePath path = jTree.getPathForLocation(ev.getX(), ev.getY());

                    if (path != null) {

                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                        if (node.isLeaf()) {

                            String favName = (String) node.getUserObject();
                            String groupName = "";

                            for (String tempGroupName : favMasterMap.keySet()) {
                                if (favMasterMap.get(tempGroupName).containsKey(favName)) {
                                    groupName = tempGroupName;
                                    break;
                                }
                            }

                            if (favMasterMap.containsKey(groupName)) {

                                int count = 0;
                                sb.append("<html>Files:<br>");

                                for (String fileName : favMasterMap.get(groupName).get(favName)) {
                                    count++;
                                    File file = new File(fileName);
                                    sb.append(count);
                                    sb.append(". ");
                                    sb.append(file.getName());
                                    sb.append("<br>");
                                }
                                sb.append("</html>");
                            }
                        }
                    }
                }

                return sb.toString();
            }
        };
    }

    /**
     * Sets the dimension of the JTree's JScrollPane container
     * based on the preferred scrollable viewport size when all
     * nodes of the JTree are expanded.
     */
    public void setScrollPaneSize() {

        jTree.expandPath(new TreePath(root.getPath()));
        expandAllNodes(jTree, 0, jTree.getRowCount());
        jScrollPane.setPreferredSize(jTree.getPreferredScrollableViewportSize());
        collapseAllNodes(jTree, 0, jTree.getRowCount());

    }

    /**
     * Recursively expands all nodes of a JTree.
     * @param tree The JTree to expand nodes for.
     * @param startingIndex The index of the node to start the expansion process.
     * @param rowCount The number of starting rows to expand.
     */
    public void expandAllNodes(JTree tree, int startingIndex, int rowCount) {

        for (int i = startingIndex; i < rowCount; i++) {
            tree.expandRow(i);
        }

        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    /**
     * Collapses nodes of a JTree starting from the bottom up.
     * Will only collapse nodes from the starting index for rowCount.
     * @param tree The JTree to collapse nodes for.
     * @param startingIndex The index of the node to start the collapse process.
     * @param rowCount The number of nodes to collapse.
     */
    private void collapseAllNodes(JTree tree, int startingIndex, int rowCount) {

        for (int i = rowCount - 1; i >= startingIndex; i--) {
            tree.collapseRow(i);
        }
    }

    public JTree getjTree() {
        return jTree;
    }
    public Map<String, Map<String, List<String>>> getFavMasterMap() {
        return favMasterMap;
    }

    /**
     * Custom TreeCellRenderer to change how a JTree is displayed.
     */
    class MyCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Color getBackgroundNonSelectionColor() {
            return (null);
        }
        @Override
        public Color getBackgroundSelectionColor() {
            return new Color(226,255,250, 255);
        }
        @Override
        public Color getBackground() {
            return (null);
        }
        @Override
        public Font getFont() {
            return new Font("Dialog", Font.BOLD, 12);
        }
        @Override
        public Icon getClosedIcon() { return ImageManager.getJTreeClosedIcon(); }
        @Override
        public Icon getOpenIcon() { return ImageManager.getJTreeOpenIcon(); }
        @Override
        public Icon getLeafIcon() { return ImageManager.getJTreeLeafIcon(); }
    }

}

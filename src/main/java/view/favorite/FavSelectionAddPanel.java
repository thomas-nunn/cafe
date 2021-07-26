package view.favorite;

import view.file.MyBorders;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tmn7 on 7/7/17.
 */
public class FavSelectionAddPanel extends JPanel {

    private Map<String, Map<String, List<String>>> groups;
    private List<AddFavPanel> addFavPanelList;
    private JPanel addFavParentPanel;
    private Insets insets;
    private JButton addAnotherFavButton;
    private JButton exploreButton;

    public FavSelectionAddPanel(Map<String, Map<String, List<String>>> theGroups) {
        super();
        groups = theGroups;
        setLayout(new GridBagLayout());
        setBorder(MyBorders.getTitledBorder("Add Favorites"));
        initFields();
        buildPanel();
    }

    private void initFields() {

        addFavPanelList = new ArrayList<>();
        addFavParentPanel = new JPanel(new GridBagLayout());
        insets = new Insets(2,2,2,2); // top,left,bottom,right
        addAnotherFavButton = new JButton("Add Another Favorite");
        exploreButton = new JButton("Explore");
    }

    private void buildPanel() {

        addFavoritePanel();
        GridBagConstraints gbc = new GridBagConstraints();
        JScrollPane addFavScrollPane = new JScrollPane(addFavParentPanel);
        addFavScrollPane.setPreferredSize(new Dimension(700,400));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = insets;
        add(addFavScrollPane, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = insets;
        add(addAnotherFavButton, gbc);

        // add exploreButton button last with weights to make it take up remainder of container
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = .2;
        gbc.insets = insets;
        add(exploreButton, gbc);
    }

    public AddFavPanel addFavoritePanel() {

        GridBagConstraints gbc = new GridBagConstraints();
        AddFavPanel newFavPanel = new AddFavPanel(new ArrayList<>(groups.keySet()), addFavPanelList.size() + 1);
        addFavPanelList.add(newFavPanel);

        gbc.gridx = 0;
        gbc.gridy = addFavPanelList.size();
        gbc.insets = insets;
        gbc.weightx = .1;
        addFavParentPanel.add(newFavPanel, gbc);

        return newFavPanel;
    }

    ////////////// Getters //////////////
    public JButton getAddAnotherFavButton() {
        return addAnotherFavButton;
    }
    public JButton getExploreButton() { return exploreButton; }
    public List<AddFavPanel> getAddFavPanelList() {
        return addFavPanelList;
    }
}
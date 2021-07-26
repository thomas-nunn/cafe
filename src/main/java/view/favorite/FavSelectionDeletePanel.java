package view.favorite;

import view.file.MyBorders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmn7 on 7/7/17.
 */
public class FavSelectionDeletePanel extends JPanel {

    private Map<String, Map<String, List<String>>> groups;
    private List<JCheckBox> groupCheckBoxList;
    private Map<String, List<JCheckBox>> favsToDeleteMap;
    private Map<String, Map<String, List<JCheckBox>>> favFilesToDeleteMap;

    public FavSelectionDeletePanel(Map<String, Map<String, List<String>>> groups) {
        super();
        this.groups = groups;
        setLayout(new GridBagLayout());
        setBorder(MyBorders.getTitledBorder("Delete Favorites"));
        initFields();
        buildPanel();
    }

    private void initFields() {

        groupCheckBoxList = new ArrayList<>();
        favsToDeleteMap = new HashMap<>();
        favFilesToDeleteMap = new HashMap<>();

    }

    private void buildPanel() {

        JPanel panelForScroll = new JPanel(new GridBagLayout());
        GridBagConstraints pfsGBC = new GridBagConstraints();
        GridBagConstraints gbc = new GridBagConstraints();
        Insets groupInsets = new Insets(2,2,2,2); // top,left,bottom,right
        Insets favInsets = new Insets(2,28,2,2); // top,left,bottom,right
        Insets fileInsets = new Insets(1,58,1,2); // top,left,bottom,right

        int pfsGBCGridY = 0;

        for (String group : groups.keySet()) {

            Map<String, List<String>> groupTitles = groups.get(group);
            JPanel groupPanel = new JPanel(new GridBagLayout());
            groupPanel.setBorder(BorderFactory.createLineBorder(new Color(155, 172, 201)));
            JCheckBox groupCheckBox = new JCheckBox(group);
            groupCheckBoxList.add(groupCheckBox);
            int gbcGridY = 0;

            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridwidth = 1;
            gbc.weightx = .1;
            gbc.insets = groupInsets;
            gbc.gridy = gbcGridY;
            groupPanel.add(groupCheckBox,gbc);
            gbcGridY++;

            //  create List of Favorite check boxes per Group
            final List<JCheckBox> favCheckBoxList = new ArrayList<>();

            for (String title : groupTitles.keySet()) {

                JCheckBox favCheckBox = new JCheckBox(title);
                favCheckBoxList.add(favCheckBox);

                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridwidth = 1;
                gbc.insets = favInsets;
                gbc.gridy = gbcGridY;
                groupPanel.add(favCheckBox,gbc);
                gbcGridY++;

                // create List of File check boxes per Favorite
                final List<JCheckBox> favFileCheckBoxList = new ArrayList<>();

                for (String fileName : groupTitles.get(title)) {

                    JCheckBox cdfCheckBox = new JCheckBox(fileName);
                    favFileCheckBoxList.add(cdfCheckBox);

                    gbc.fill = GridBagConstraints.BOTH;
                    gbc.gridwidth = 1;
                    gbc.insets = fileInsets;
                    gbc.gridy = gbcGridY;
                    groupPanel.add(cdfCheckBox,gbc);
                    gbcGridY++;
                }

                if (favFilesToDeleteMap.containsKey(group)) {

                    Map<String, List<JCheckBox>> favMap = favFilesToDeleteMap.get(group);
                    favMap.put(title,favFileCheckBoxList);

                } else {

                    Map<String, List<JCheckBox>> favMap = new HashMap<>();
                    favMap.put(title,favFileCheckBoxList);
                    favFilesToDeleteMap.put(group,favMap);
                }

                favCheckBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent itemEvent) {

                        for (JCheckBox jcb : favFileCheckBoxList) {
                            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                                jcb.setSelected(true);
                            } else {
                                jcb.setSelected(false);
                            }
                        }
                    }
                });
            }

            favsToDeleteMap.put(group, favCheckBoxList);

            pfsGBC.anchor = GridBagConstraints.WEST;
            pfsGBC.fill = GridBagConstraints.HORIZONTAL;
            pfsGBC.gridx = 0;
            pfsGBC.gridy = pfsGBCGridY;
            pfsGBC.insets = groupInsets;
            panelForScroll.add(groupPanel,pfsGBC);
            pfsGBCGridY++;

            groupCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent itemEvent) {

                    for (JCheckBox jcb : favCheckBoxList) {
                        if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                            jcb.setSelected(true);
                        } else {
                            jcb.setSelected(false);
                        }
                    }
                }
            });
        }

        if (groups.size() == 0) {
            gbc.insets = new Insets(5,5,5, 5);
            this.add(new JLabel("No Favorites to Delete"), gbc);
        } else {

            JScrollPane deleteScrollPane = new JScrollPane(panelForScroll);
            deleteScrollPane.setPreferredSize(new Dimension(700,400));
            GridBagConstraints fsdpGBC = new GridBagConstraints();
            fsdpGBC.insets = groupInsets;
            fsdpGBC.fill = GridBagConstraints.BOTH;
            fsdpGBC.gridx = 0;
            fsdpGBC.gridy = 0;
            this.add(deleteScrollPane, fsdpGBC);
        }
    }


    public List<JCheckBox> getGroupCheckBoxList() {
        return groupCheckBoxList;
    }

    public Map<String, List<JCheckBox>> getFavsToDeleteMap() {
        return favsToDeleteMap;
    }

    public Map<String, Map<String, List<JCheckBox>>> getFavFilesToDeleteMap() {
        return favFilesToDeleteMap;
    }
}
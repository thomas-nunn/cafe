package view.file;

import model.resources.ImageManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by tmn7 on 9/7/18.
 */
public class TabComponentPanel extends JPanel {

    private JLabel titleLabel;
    private JButton dirtyFlagButton;
    private JButton closeButton;

    /**
     * Constructor.
     * @param absPath The absolute path of a file.
     */
    TabComponentPanel(final String absPath) {
        super(new GridBagLayout());
        initComps(absPath);
        buildPanel();
    }

    /**
     * Initializes fields of this class that are needed upon instantiation.
     */
    private void initComps(final String title) {
        titleLabel = new JLabel(title);
        dirtyFlagButton = createDirtyFlagButton();
        closeButton = createCloseButton();
    }

    /**
     * Adds components to this TabComponentPanel for the initial set up.
     */
    private void buildPanel() {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(4, -1, 2, 5); // top,left,bottom,right
        add(dirtyFlagButton, gbc);

        gbc.gridx++;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        add(titleLabel, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 6, 2, -1); // top,left,bottom,right
        add(closeButton, gbc);

        setOpaque(false);
    }

    /**
     * Creates a JButton for use as a "dirty flag" that indicates
     * changes have been made to a file but not yet saved.
     * @return The JButton.
     */
    private JButton createDirtyFlagButton() {

        JButton dirtyFlagButton = new JButton(ImageManager.getJTreeLeafIcon());

        dirtyFlagButton.setEnabled(false);
        dirtyFlagButton.setMargin(new Insets(0,0,0,0));
        dirtyFlagButton.setPreferredSize(new Dimension(15,15));
        dirtyFlagButton.setBorderPainted(false);
        dirtyFlagButton.setContentAreaFilled(false);

        dirtyFlagButton.setDisabledIcon(ImageManager.getJTreeLeafIcon());

        return dirtyFlagButton;
    }

    /**
     * Creates a JButton for closing a tab panel in a JTabbedPane.
     * @return The JButton.
     */
    private JButton createCloseButton() {

        final JButton closeButton = new JButton(ImageManager.getXBlackIcon());

        closeButton.setMargin(new Insets(0,0,0,0));
        closeButton.setPreferredSize(new Dimension(15,15));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);

        ImageIcon rolloverIcon = ImageManager.getXRedIcon();
        closeButton.setRolloverEnabled(true);
        closeButton.setRolloverIcon(rolloverIcon);

        return closeButton;
    }

    // getters
    JButton getDirtyFlagButton() {return dirtyFlagButton;}
    public JButton getCloseButton() {return closeButton;}
}

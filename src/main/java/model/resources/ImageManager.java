package model.resources;

import javax.swing.*;

/**
 * Created by tmn7 on 6/8/18.
 */
public class ImageManager {

    private static final Integer ICON_SCALE = 7;


//	private static final ImageIcon openIcon = new ImageIcon("C:\\INNOVATION\\editor_app\\images\\sm-green-tri.png");
//	private static final ImageIcon closedIcon = new ImageIcon("C:\\INNOVATION\\editor_app\\images\\sm-red-tri.png");
//	private static final ImageIcon leafIcon = new ImageIcon("C:\\INNOVATION\\editor_app\\images\\blank_icon.png");


    private static final ImageIcon openIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/sm-green-tri.png");
    private static final ImageIcon closedIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/sm-red-tri.png");
    private static final ImageIcon leafIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/blank_icon.png");
    private static final ImageIcon xBlackIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/round_x.png");
    private static final ImageIcon xRedIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/round_red_x.png");
    private static final ImageIcon asteriskIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/asterisk.png");
    private static final ImageIcon closedFolderIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/close_folder.png");
    private static final ImageIcon openFolderIcon = new ImageIcon("/home/tmn7/Documents/editor_app_files/zzz_jar_test/images/open_folder.png");

    private static final ImageIcon scaledOpenIcon = new ImageIcon(openIcon.getImage().getScaledInstance( ICON_SCALE, ICON_SCALE, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledClosedIcon = new ImageIcon(closedIcon.getImage().getScaledInstance( ICON_SCALE, ICON_SCALE, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledLeafIcon = new ImageIcon(leafIcon.getImage().getScaledInstance( ICON_SCALE, ICON_SCALE, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledXBlackIcon = new ImageIcon(xBlackIcon.getImage().getScaledInstance( ICON_SCALE * 2, ICON_SCALE * 2, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledXRedIcon = new ImageIcon(xRedIcon.getImage().getScaledInstance( ICON_SCALE * 2, ICON_SCALE * 2, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledAsteriskIcon = new ImageIcon(asteriskIcon.getImage().getScaledInstance( ICON_SCALE * 2, ICON_SCALE * 2, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledClosedFolderIcon = new ImageIcon(closedFolderIcon.getImage().getScaledInstance( ICON_SCALE * 2, ICON_SCALE * 2, java.awt.Image.SCALE_SMOOTH ));
    private static final ImageIcon scaledOpenFolderIcon = new ImageIcon(openFolderIcon.getImage().getScaledInstance( ICON_SCALE * 5, ICON_SCALE * 5, java.awt.Image.SCALE_SMOOTH ));

    public static ImageIcon getJTreeOpenIcon() { return scaledOpenIcon; }
    public static ImageIcon getJTreeClosedIcon() { return scaledClosedIcon; }
    public static ImageIcon getJTreeLeafIcon() { return scaledLeafIcon; }
    public static ImageIcon getXBlackIcon() { return scaledXBlackIcon; }
    public static ImageIcon getXRedIcon() { return scaledXRedIcon; }
    public static ImageIcon getAsteriskIcon() { return scaledAsteriskIcon; }
    public static ImageIcon getClosedFolderIcon() { return scaledClosedFolderIcon; }
    public static ImageIcon getOpenFolderIcon() { return scaledOpenFolderIcon; }

}

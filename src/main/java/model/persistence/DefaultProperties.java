package model.persistence;

import javax.swing.*;
import java.awt.*;

public class DefaultProperties {

    public static final Color defaultBackgroundColor = UIManager.getColor("TextField.background");
    public static final Color defaultForegroundColor = UIManager.getColor("TextField.foreground");
    public static final Color caretLineHighlightColor = new Color(123, 207, 94);
    public static final Color selectedHighlightColor = new Color(255, 255, 1);
    public static final Color nonSelectedHighlightColor = new Color(255, 229, 204);
    public static final Color uiManagerSelectionBackgroundColor = UIManager.getColor("TextArea.selectionBackground");
    public static final Font defaultFont = new Font("Dialog",0, 13);
    public static final Integer frameX = 0;
    public static final Integer frameY = 0;
    public static final Integer frameWidth = 1200;
    public static final Integer frameHeight = 1024;
}

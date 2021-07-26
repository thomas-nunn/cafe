package view.file;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by tmn7 on 7/27/18.
 */
public class MyBorders {

    public static Border getTitledBorder(final String title) {

        Color borderColor = new Color(144, 173, 219);
        Color titleColor = new Color(97, 124, 165);
        Border lineBorder = BorderFactory.createLineBorder(borderColor, 1);
        Font titleFont = new Font("Times New Roman", Font.BOLD, 12);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, title,
                TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,2,4,2), titledBorder); // top,left,bottom,right
    }

    public static Border getRecessedTitleBorder(final String title) {

        Color borderColor = new Color(144, 173, 219);
        Color titleColor = new Color(97, 124, 165);
        Border loweredBevelBorder = BorderFactory.createLoweredBevelBorder();
        Font titleFont = new Font("Times New Roman", Font.BOLD, 12);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(loweredBevelBorder, title,
                TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,2,4,2), titledBorder); // top,left,bottom,right
    }

    public static Border getBeveledTitleBorder(final String title) {

        Color borderColor = new Color(144, 173, 219);
        Color titleColor = new Color(97, 124, 165);
        Border loweredBevelBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED, titleColor, borderColor);
        Font titleFont = new Font("Times New Roman", Font.BOLD, 12);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(loweredBevelBorder, title,
                TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, titleFont, titleColor);
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4,2,4,2), titledBorder); // top,left,bottom,right
    }

    public static final Border getEmptyLineBorder() {

        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(10,10,10,10));
    }
}

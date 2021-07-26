package view.file;

import javax.swing.*;

/**
 * Created by tmn7 on 7/22/18.
 */
public class MyTextField extends JTextField {

    private final int PAD_X;
    private final int PAD_Y;
    private final boolean isEditable;

    public MyTextField(final int padX, final int padY, boolean isEditable) {
        super();
        PAD_X = padX;
        PAD_Y = padY;
        this.isEditable = isEditable;
        init();
    }

    private void init() {
        setEditable(isEditable);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder
                        (PAD_Y, PAD_X, PAD_Y, PAD_X))); // top, left, bottom, right
    }
}

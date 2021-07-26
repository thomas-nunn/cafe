package view.file;

import javax.swing.*;
import java.util.List;

/**
 * Created by tmn7 on 7/21/18.
 */
public abstract class DisplayPanel extends JPanel {


    /**
     * Sets the text on the display components from a specified
     * line of a JTextArea to the end. The line is delimited and put
     * into the List<String> passed in.
     *
     * @param rowFields A row of a JTextArea as a List of Strings.
     * @param textAreaIndex The index of rowFields in the JTextArea.
     */
    public abstract void setTextOnDisplayComponents(final List<String> rowFields, final int textAreaIndex);

    /**
     * Updates the UI based on changes to settings.
     * For example, if the user changed the background color for the App
     * then this method would be called to ensure that all Components
     * received the changes.
     */
    public abstract void updateMyUI();

    /**
     * Adds or removes text fields from a TextFieldPanel when the count
     * of text fields changes.
     *
     * @param textFieldPanel The TextFieldPanel to update
     * @param currentTextFieldCount The number of text fields required for textFieldPanel
     */
    protected void updateMyTextFieldCount(final TextFieldPanel textFieldPanel, final int currentTextFieldCount) {

        int oldTextFieldCount = textFieldPanel.getMyTextFields().size();

        if (currentTextFieldCount > oldTextFieldCount) {

            for (int i = oldTextFieldCount; i < currentTextFieldCount; i++) {
                textFieldPanel.addTextField();
            }

        } else if (currentTextFieldCount < oldTextFieldCount) {

            for (int z = oldTextFieldCount-1; z >= currentTextFieldCount; z--) {
                textFieldPanel.removeLastTextField();
            }
        }
    }
}

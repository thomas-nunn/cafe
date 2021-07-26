package view.file;

import model.persistence.AppProperties;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A JPanel Class for displaying MyTextFields in a vertical fashion.
 */
public class TextFieldPanel extends JPanel {

    private GridBagConstraints gbc = new GridBagConstraints();
    private List<MyTextField> myTextFields = new ArrayList<>();
    private JPanel fillerPanel = new JPanel();
    private boolean editable;
    private final AppProperties myAppProperties;


    /**
     * Constructor.
     *
     * @param title The title for the border of this panel.
     * @param fieldsAreEditable A boolean for indicating whether the MyTextFields of this panel should be editable.
     * @param appProperties The AppProperties for setting colors and font.
     */
    TextFieldPanel(String title, boolean fieldsAreEditable, final AppProperties appProperties) {
        super(new GridBagLayout());
        editable = fieldsAreEditable;
        myAppProperties = appProperties;
        setBorder(MyBorders.getTitledBorder(title));
        addFillerPanel();
    }

    /**
     * Adds a new text field without text to this panel.
     * The new MyTextField is added to the end of myTextFields list.
     */
    void addTextField() {

        myTextFields.add(getNewTextField());

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,1,1,0); // top,left,bottom,right
        gbc.gridx = 0;
        gbc.gridy = myTextFields.size()-1;
        add(myTextFields.get(myTextFields.size()-1),gbc);
    }

    /**
     * Adds a new MyTextField with text to this panel.
     * The new MyTextField is added to the end of myTextFields list.
     */
    void addTextField(String text) {

        MyTextField myTextField = getNewTextField();
        myTextField.setText(text);
        myTextFields.add(myTextField);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,1,1,0); // top,left,bottom,right
        gbc.gridx = 0;
        gbc.gridy = myTextFields.size()-1;
        add(myTextField,gbc);
    }

    /**
     * Inserts a new MyTextField at the specified index into this panel.
     * The new MyTextField is added to myTextFields list at the appropriate index.
     * All components are removed from this JPanel and then
     * added back in order.
     *
     * @param index The index to insert at.
     * @param text The text to set on the new text field.
     */
    void insertTextField(int index, String text) {

        MyTextField myTextField = getNewTextField();
        myTextField.setText(text);
        myTextFields.add(index, myTextField);

        removeAll();    // remove all components from this JPanel

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0,1,1,0); // top,left,bottom,right
        gbc.gridx = 0;

        for (int i = 0; i < myTextFields.size(); i++) {
            gbc.gridy = i;
            add(myTextFields.get(i),gbc);
        }
        addFillerPanel();
    }

    /**
     * Removes the last TextField from this panel and myTextFields list
     */
    void removeLastTextField() {

        MyTextField lastMyTextField = myTextFields.get(myTextFields.size()-1);
        remove(lastMyTextField);
        myTextFields.remove(lastMyTextField);
    }

    /**
     * Adds the filler JPanel to this panel
     */
    private void addFillerPanel() {

        fillerPanel.setBackground(myAppProperties.getBackGroundColor());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = .5;
        gbc.weighty = .5;
        gbc.gridx = 0;
        gbc.gridy = 10000;
        // 10000 is a magic number to make sure the filler is always last

        add(fillerPanel,gbc);
    }

    /**
     * Updates this panel's colors and font.
     */
    public void updateMyUI() {

        for (MyTextField mtf : myTextFields) {
            mtf.setBackground(myAppProperties.getBackGroundColor());
            mtf.setForeground(myAppProperties.getForeGroundColor());
            mtf.setFont(myAppProperties.getFont());
        }

        fillerPanel.setBackground(myAppProperties.getBackGroundColor());
    }

    /**
     * @return A new MyTextField with font and colors set.
     */
    private MyTextField getNewTextField() {

        MyTextField myTextField = new MyTextField(2,1,editable);
        myTextField.setFont(myAppProperties.getFont());
        myTextField.setBackground(myAppProperties.getBackGroundColor());
        myTextField.setForeground(myAppProperties.getForeGroundColor());
        return myTextField;
    }

    /////// Getters ///////
    List<MyTextField> getMyTextFields() { return myTextFields; }

}

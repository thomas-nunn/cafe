package view.file;

import model.file.PlainTextModel;
import model.file.PositionalSchemaData;
import model.persistence.AppProperties;
import model.resources.ImageManager;

import javax.swing.*;
import javax.swing.text.Highlighter;
import java.awt.*;


/**
 * Created by tmn7 on 6/29/18.
 *
 * A specialized JPanel to serve as the main component
 * for a JTabbedPane tab.
 */
public class TabPanel extends JPanel {

    private TabComponentPanel tabComponentPanel;
    private PlainTextModel plainTextModel;
    private PlainTextPanel plainTextPanel;
    private JSplitPane jSplitPane;
    private ControlPanel controlPanel;
    private Integer previousLine;
    private Integer currentLine;
    /**
     * The Highlight Object used in a JTextArea for highlighting the currently selected line.
     */
    private Highlighter.Highlight textAreaHighLight;
    private DisplayPanel displayPanel;
    private final AppProperties myAppProperties;


    /**
     * Constructs a new instance of this class.
     * A TabPanel displays a text file.
     *
     * @param thePlainTextModel The PlainTextModel for this class.
     */
    public TabPanel(final PlainTextModel thePlainTextModel, final AppProperties appProperties) {
        super(new GridBagLayout());
        plainTextModel = thePlainTextModel;
        myAppProperties = appProperties;
        init();
        buildPanel();
    }

    /**
     * Initializes fields of this class that are needed upon instantiation.
     */
    private void init() {
        tabComponentPanel = new TabComponentPanel(plainTextModel.getFileName());
        plainTextPanel = new PlainTextPanel(plainTextModel.getTextArea(), plainTextModel.getAbsolutePath(), myAppProperties);
        controlPanel = new ControlPanel();
        previousLine = 0;
        currentLine = 0;
    }

    /**
     * Adds components to this TabPanel for the initial set up
     * using just the PlainTextPanel and a control panel for
     * selecting different file types.
     */
    private void buildPanel() {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(3,3,0,3); // top,left,bottom,right

        add(controlPanel, gbc);

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = .1;
        gbc.weighty = .1;
        gbc.insets = new Insets(0,3,3,3); // top,left,bottom,right

        add(plainTextPanel,gbc);
    }

    /**
     * Sets the DisplayPanel to an instance of DelimitedPanel.
     *
     * @param delimiter The delimiter for a delimited flatfile.
     */
    public void setUpDelimitedPanel(final Character delimiter) {

        displayPanel = new DelimitedPanel(delimiter, myAppProperties);
        convertToFileTypeDisplay(new JScrollPane(displayPanel));
    }

    /**
     * Sets the DisplayPanel to an instance of PositionalPanel.
     *
     * @param positionalSchemaData The PositionalSchemaData for a positional flatfile.
     */
    public void setUpPositionalPanel(final PositionalSchemaData positionalSchemaData) {

        PositionalPanel positionalPanel = new PositionalPanel(positionalSchemaData, myAppProperties);
        positionalPanel.setColumnAndHeaderText();
        displayPanel = positionalPanel;
        convertToFileTypeDisplay(new JScrollPane(displayPanel));
    }

    /**
     * Removes the ControlPanel and PlainTextPanel from this TabPanel
     * and adds the JSplitPane, configured with the Component passed in and
     * the PlainTextPanel.
     *
     * @param component The Component for displaying a file type.
     */
    private void convertToFileTypeDisplay(Component component) {

        remove(controlPanel);
        remove(plainTextPanel);

        if (myAppProperties.getTextAreaPosition() == 0) { // 0 = left/top, 1 = bottom/right
            jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, plainTextPanel, component);
        } else {
            jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, component, plainTextPanel);
        }
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = .1;
        gbc.weighty = .1;
        gbc.insets = new Insets(0,3,3,3); // top,left,bottom,right

        add(jSplitPane,gbc);

        updateDisplayPanelSettings();
    }

    public void updateMyUI() {
        plainTextPanel.updateMyUI();
        if (displayPanel != null) {
            displayPanel.updateMyUI();
            updateDisplayPanelSettings();
        }
    }

    /**
     * Sets the "dirty flag" for this tab panel.
     * If changes have been made to the file but
     * not saved, then an '*' is shown.
     *
     * @param changesSaved true if user saved changes.
     */
    public void updateTabComponent(boolean changesSaved) {

        if (changesSaved) {
            tabComponentPanel.getDirtyFlagButton().setIcon(ImageManager.getJTreeLeafIcon());
            tabComponentPanel.getDirtyFlagButton().setDisabledIcon(ImageManager.getJTreeLeafIcon());
        } else {
            tabComponentPanel.getDirtyFlagButton().setIcon(ImageManager.getAsteriskIcon());
            tabComponentPanel.getDirtyFlagButton().setDisabledIcon(ImageManager.getAsteriskIcon());
        }
    }

    /**
     * Changes the DisplayPanel based on ViewValues.
     */
    private void updateDisplayPanelSettings() {

        setSplitPanePosition(jSplitPane, plainTextPanel, myAppProperties.getTextAreaPosition());
        setSplitPaneOrientation(jSplitPane, myAppProperties.getTextAreaOrientation());
        revalidate();
    }

    /**
     * Sets the position of a JSplitPane based on the Component and position passed in.
     * If position = 0, then componentToBasePositionsOn should be on the left/top.
     * If position = 1, then componentToBasePositionsOn should be on the bottom/right.
     * No change occurs unless the current position differs from what it should be.
     *
     * @param splitPane The JSplitPane whose Components' position's will be set.
     * @param componentToBasePositionsOn The Component to base positions on.
     * @param position The position component should be in.
     */
    private void setSplitPanePosition(JSplitPane splitPane, Component componentToBasePositionsOn, Integer position) {

        Component leftComponent = splitPane.getLeftComponent();
        Component rightComponent = splitPane.getRightComponent();

        // component currently on left but should be on right
        boolean swapReason1 = (leftComponent.equals(componentToBasePositionsOn)) && (position == 1);

        // component currently on right but should be on left
        boolean swapReason2 = (rightComponent.equals(componentToBasePositionsOn)) && (position == 0);

        if (swapReason1 || swapReason2) {
            splitPane.remove(leftComponent);
            splitPane.remove(rightComponent);
            splitPane.setLeftComponent(rightComponent);
            splitPane.setRightComponent(leftComponent);
        }
    }

    /**
     * Sets the orientation of a JSplitPane. Sets resize weight to .5
     * and revalidates the JSplitPane.
     *
     * @param splitPane JSplitPane whose orientation is to be set.
     * @param orientation The orientation to set the JSplitPane to.
     */
    private void setSplitPaneOrientation(JSplitPane splitPane, Integer orientation) {
        splitPane.setOrientation(orientation);
        splitPane.setResizeWeight(.5);
        splitPane.revalidate();
    }

    //// Getters ////
    public JTextArea getTextArea() {return plainTextModel.getTextArea();}
    public PlainTextModel getPlainTextModel() {return plainTextModel;}
    public ControlPanel getControlPanel() {return controlPanel;}
    public JSplitPane getjSplitPane() {return jSplitPane;}
    public DisplayPanel getDisplayPanel() {return displayPanel;}
    public Integer getPreviousLine() { return previousLine; }
    public Integer getCurrentLine() {return currentLine;}
    public TabComponentPanel getTabComponentPanel() {return tabComponentPanel;}
    public PlainTextPanel getPlainTextPanel() {return plainTextPanel;}
    public Highlighter.Highlight getTextAreaHighLight() {return textAreaHighLight;}

    //// Setters ////
    public void setPreviousLine(Integer previousLine) { this.previousLine = previousLine; }
    public void setCurrentLine(Integer currentLine) {
        this.currentLine = currentLine;
    }
    public void setTextAreaHighLight(Object highLight) {textAreaHighLight = (Highlighter.Highlight) highLight;}
}

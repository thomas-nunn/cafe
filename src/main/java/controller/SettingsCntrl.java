package controller;

import model.persistence.AppProperties;
import model.persistence.DefaultProperties;
import view.CardLayoutDialog;
import view.settings.ColorPanel;
import view.settings.FontPanel;
import view.settings.TabPolicyPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * Created by tmn7 on 7/29/18.
 */
public class SettingsCntrl {

    private final App myApp;
    private CardLayoutDialog cardLayoutDialog;
    private ColorPanel colorPanel;
    private FontPanel fontPanel;
    private TabPolicyPanel tabPolicyPanel;
    private GraphicsEnvironment graphicsEnvironment;


    SettingsCntrl(final App app) {
        myApp = app;
        graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

    }

    AbstractAction getSettingsDialogAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displaySettingsDialog();
            }
        };
    }

    private void displaySettingsDialog() {

        cardLayoutDialog = new CardLayoutDialog(myApp.getAppFrame(), "Settings Manager");
        colorPanel = new ColorPanel(myApp.getPropsControl().getMyAppProperties().getBackGroundColor(), myApp.getPropsControl().getMyAppProperties().getForeGroundColor());
        fontPanel = new FontPanel(myApp.getPropsControl().getMyAppProperties().getFont());
        tabPolicyPanel = new TabPolicyPanel(myApp.getPropsControl().getMyAppProperties().getTabPolicy());

        final JButton cancelButton = new JButton("Cancel");
        final JButton saveButton = new JButton("Save");

        colorPanel.getBgButton().addActionListener(getBGColorAction(cardLayoutDialog));
        colorPanel.getFgButton().addActionListener(getFGColorAction(cardLayoutDialog));
        colorPanel.getDefaultsButton().addActionListener(getDefaultColorsAction());
        addListenersToFontBoxes();
        saveButton.addActionListener(getSaveSettingsAction());
        cancelButton.addActionListener(getCancelAction());

        cardLayoutDialog.addPanelToCardLayout(colorPanel, "Colors");
        cardLayoutDialog.addControlButton(saveButton);
        cardLayoutDialog.addControlButton(cancelButton);
        cardLayoutDialog.addPanelToCardLayout(fontPanel, "Font");
        cardLayoutDialog.addPanelToCardLayout(tabPolicyPanel, "Tab Policy");
        cardLayoutDialog.pack();
        cardLayoutDialog.setLocationRelativeTo(myApp.getAppFrame());
        cardLayoutDialog.setVisible(true);
    }

    private void addListenersToFontBoxes() {

        final JComboBox fontNameBox = fontPanel.getFontNameBox();
        final JComboBox fontStyleBox = fontPanel.getFontStyleBox();
        final JComboBox fontSizeBox = fontPanel.getFontSizeBox();

        fontNameBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                Font updatedFont = new Font((String)fontNameBox.getSelectedItem(),
                        fontPanel.getStyleMap().get((String) fontStyleBox.getSelectedItem()),
                        (Integer)fontSizeBox.getSelectedItem());
                fontPanel.setSelectedFont(updatedFont);
                fontPanel.getSelectedFontDisplay().setFont(updatedFont);
                fontPanel.setUserSelectedSomething(true);
                cardLayoutDialog.pack();
            }
        });

        fontStyleBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                Font selectedFont = fontPanel.getSelectedFont();
                String style = (String) fontStyleBox.getSelectedItem();
                String fontName = (String)fontNameBox.getSelectedItem();

                if (selectedFont.isBold()) {
                    if (style.equals("Plain") || style.equals("Italic")) {
                        for (String str : graphicsEnvironment.getAvailableFontFamilyNames()) {
                            if (fontName.contains(str)) {
                                fontName = str;
                                break;
                            }
                        }
                    }
                }

                if (selectedFont.isItalic()) {
                    if (style.equals("Plain") || style.equals("Bold")) {
                        for (String str : graphicsEnvironment.getAvailableFontFamilyNames()) {
                            if (fontName.contains(str)) {
                                fontName = str;
                                break;
                            }
                        }
                    }
                }

                Font updatedFont = new Font(fontName, fontPanel.getStyleMap().get(style), (Integer)fontSizeBox.getSelectedItem());
                fontPanel.setSelectedFont(updatedFont);
                fontPanel.getSelectedFontDisplay().setFont(updatedFont);
                fontPanel.setUserSelectedSomething(true);
                cardLayoutDialog.pack();
            }
        });

        fontSizeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                Font updatedFont = new Font((String)fontNameBox.getSelectedItem(),
                        fontPanel.getStyleMap().get((String) fontStyleBox.getSelectedItem()),
                        (Integer)fontSizeBox.getSelectedItem());
                fontPanel.setSelectedFont(updatedFont);
                fontPanel.getSelectedFontDisplay().setFont(updatedFont);
                fontPanel.setUserSelectedSomething(true);
                cardLayoutDialog.pack();
            }
        });
    }

    private AbstractAction getDefaultColorsAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                colorPanel.getSelectedBgButton().setBackground(DefaultProperties.defaultBackgroundColor);
                colorPanel.getSelectedFgButton().setBackground(DefaultProperties.defaultForegroundColor);
                colorPanel.setNewBgColor(DefaultProperties.defaultBackgroundColor);
                colorPanel.setNewFgColor(DefaultProperties.defaultForegroundColor);
            }
        };
    }

    private AbstractAction getBGColorAction(final CardLayoutDialog settingsDialog) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Color c = JColorChooser.showDialog(settingsDialog,"Choose a Color", Color.WHITE);
                if (c != null) {
                    colorPanel.getSelectedBgButton().setBackground(c);
                    colorPanel.setNewBgColor(c);
                }
            }
        };
    }

    private AbstractAction getFGColorAction(final CardLayoutDialog settingsDialog) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Color c = JColorChooser.showDialog(settingsDialog,"Choose a Color", Color.WHITE);
                if (c != null) {
                    colorPanel.getSelectedFgButton().setBackground(c);
                    colorPanel.setNewFgColor(c);
                }

            }
        };
    }

    /**
     *
     * @return The Action
     */
    private AbstractAction getSaveSettingsAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                final AppProperties appProperties = myApp.getPropsControl().getMyAppProperties();

                if (colorPanel.getNewBgColor() != null || colorPanel.getNewFgColor() != null) {

                    // if user doesn't select a new color but hits save, then use the current colors
                    Color newBgColor = (colorPanel.getNewBgColor() == null) ? colorPanel.getBgColor() : colorPanel.getSelectedBgButton().getBackground();
                    Color newFgColor = (colorPanel.getNewFgColor() == null) ? colorPanel.getFgColor() : colorPanel.getSelectedFgButton().getBackground();

                    appProperties.setBackGroundColor(newBgColor);
                    appProperties.setForeGroundColor(newFgColor);
                }

                if (fontPanel.isUserSelectedSomething() && !fontPanel.getCurrentFont().equals(fontPanel.getSelectedFont())) {
                    appProperties.setFont(fontPanel.getSelectedFont());
                }

                appProperties.setTabPolicy(tabPolicyPanel.getCurrentPolicy());
                myApp.getSearchCntrl().setHighlightPainters();
                myApp.getAppFrame().setTabPaneLayoutPolicy(tabPolicyPanel.getCurrentPolicy());
                myApp.getAppFrame().updateUI();
                myApp.getAppFrame().pack();
                cardLayoutDialog.dispose();
            }
        };
    }

    /**
     * Cancels the cardLayoutDialog.
     *
     * @return The action for cancelling the cardLayoutDialog.
     */
    private AbstractAction getCancelAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                cardLayoutDialog.dispose();
                myApp.getAppFrame().pack();
            }
        };
    }


}

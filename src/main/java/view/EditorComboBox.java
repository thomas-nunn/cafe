package view;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by tmn7 on 3/3/18.
 *
 * A Custom JComboBox class that allows the user to trigger
 * a button with the Enter key.
 */
class EditorComboBox<T> extends JComboBox<T> {

    private final JButton enterButton;
    private boolean popUpOpen;
    private boolean clickTheButton;

    /**
     * Constructor.
     *
     * @param comboBoxModel The ComboBoxModel
     * @param enterActionButton The JButton to be triggered by the Enter key
     */
    @SuppressWarnings("unchecked")
    EditorComboBox(ComboBoxModel comboBoxModel, JButton enterActionButton) {
        super(comboBoxModel);
        enterButton = enterActionButton;
        popUpOpen = false;
        clickTheButton = false;
        setupComboBoxListeners();
    }

    /**
     * Attaches listeners to this EditorComboBox.
     * These listeners set boolean flags to achieve
     * the following behavior:
     * when the user hits the enter button
     * on the keyboard then the JButton called "enterButton"
     * will be triggered in all cases EXCEPT when the
     * ComboBox's pop up menu is open.
     *
     * This logic relies on the fact that these events
     * occur in a guaranteed order. That order is this:
     * 1. popupMenuWillBecomeVisible
     * 2. keyPressed
     * 3. popupMenuWillBecomeInvisible
     * 4. keyTyped
     * 5. keyReleased
     */
    private void setupComboBoxListeners() {

        this.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {
                popUpOpen = true;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                popUpOpen = false;
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
            }
        });

        this.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                super.keyPressed(keyEvent);

                if (keyEvent.paramString().contains("keyChar=Enter")) {

                    if (!popUpOpen) {
                        clickTheButton = true;
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent keyEvent) {
                super.keyTyped(keyEvent);

                if (keyEvent.paramString().contains("keyChar=Enter")) {

                    if (clickTheButton) {
                        enterButton.doClick();
                    }
                    clickTheButton = false;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                super.keyReleased(keyEvent);

                if (keyEvent.paramString().contains("keyChar=Enter")) {
                    // not needed
                }
            }
        });
    }
}

package controller.file;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tmn7 on 7/1/18.
 *
 * A Class for handling undo/redo on a JTextArea. This Class has a workaround to solve
 * the problem caused by calling JTextArea.replace().
 * The problem with JTextArea.replace() is that it uses two
 * actions, delete and addition. Those two actions are separate UndoableEdits, meaning
 * that undoing JTextArea.replace() requires two actions by the user. This works fine but
 * is awkward because the first undo (addition) results in an empty JTextArea.
 * A better solution to this workaround would be to write custom Undo/Redo classes that handle
 * this scenario. This may involve a custom JTextArea class or custom listeners in order to
 * capture and combine the two edits into one. See Class UndoableEditSupport, which may be used
 * to combine delete and addition for replace actions.
 * Note: this same problem occurs when highlighting text in a JTextArea and then typing over the highlight.
 * Other text editors don't have this problem.
 */
public class UndoRedoCntrl {

    private UndoManager undoManager = new UndoManager();
    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();
    private UndoableEdit searchReplaceEdit;

    /**
     * A flag to indicate that the user is doing a "replace" action using
     * the "Replace All" button on the search dialog.
     */
    private boolean goingToReplace = false;

    private List<Boolean> undoNextAction = new ArrayList<>();
    private List<Boolean> redoNextAction = new ArrayList<>();

    /**
     * Constructor
     */
    public UndoRedoCntrl() {}

    public void setGoingToReplace(boolean goingToReplace) {this.goingToReplace = goingToReplace;}
    public UndoListener getUndoListener() {return new UndoListener();}
    public UndoAction getUndoAction() {return undoAction;}
    public RedoAction getRedoAction() {return redoAction;}

    /**
     *
     */
    public class UndoListener implements UndoableEditListener {

        public void undoableEditHappened(UndoableEditEvent e) {

            if (goingToReplace) {
                if (searchReplaceEdit == null) {
                    undoNextAction.add(false);
                    searchReplaceEdit = e.getEdit();
                } else {
                    undoNextAction.add(true);
                    searchReplaceEdit = null;
                }
            } else {
                undoNextAction.add(false);
            }

            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    /**
     *
     */
    class UndoAction extends AbstractAction {
        UndoAction() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {

            if (this.isEnabled()) {

                undoManager.undo();
                undoAction.update();
                redoAction.update();

                if (undoNextAction.get(undoNextAction.size()-1)) {
                    undoManager.undo();
                    undoAction.update();
                    redoAction.update();
                    undoNextAction.remove(undoNextAction.size()-1);
                    redoNextAction.add(false);
                    redoNextAction.add(true);
                } else {
                    redoNextAction.add(false);
                }
                undoNextAction.remove(undoNextAction.size()-1);

            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getUndoPresentationName());
            this.setEnabled(undoManager.canUndo());
        }
    }

    /**
     *
     */
    class RedoAction extends AbstractAction {
        RedoAction() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {

            if (this.isEnabled()) {

                undoManager.redo();
                undoAction.update();
                redoAction.update();

                if (redoNextAction.get(redoNextAction.size()-1)) {

                    undoManager.redo();
                    undoAction.update();
                    redoAction.update();
                    redoNextAction.remove(redoNextAction.size()-1);
                    undoNextAction.add(false);
                    undoNextAction.add(true);
                } else {
                    undoNextAction.add(false);
                }

                redoNextAction.remove(redoNextAction.size()-1);
            }
        }

        public void update() {
            this.putValue(Action.NAME, undoManager.getRedoPresentationName());
            this.setEnabled(undoManager.canRedo());
        }
    }

}

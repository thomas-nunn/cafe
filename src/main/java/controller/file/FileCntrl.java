package controller.file;

import controller.App;
import model.file.PlainTextModel;
import model.persistence.DefaultProperties;
import model.resources.PathManager;
import view.file.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tmn7 on 6/29/18.
 */
public class FileCntrl {

    private final App myApp;
    private final DelimitedCntrl delimitedCntrl;
    private final PositionalCntrl positionalCntrl;


    public FileCntrl(App app) {
        
        myApp = app;
        delimitedCntrl = new DelimitedCntrl(this);
        positionalCntrl = new PositionalCntrl(this);
    }

    /**
     * Creates a TabPanel for a new file with no data.
     *
     * @param fileName The name of the new file.
     * @return The TabPanel.
     */
    public TabPanel createTabForNewFile(String fileName) {

        TabPanel tabPanel = new TabPanel(new PlainTextModel(new File(fileName)), myApp.getPropsControl().getMyAppProperties());
        attachListenersToTabPanel(tabPanel);
        return tabPanel;
    }

    /**
     * Creates TabPanel Objects for use in a JTabbedPane.
     * One TabPanel is created per File in fileList.
     * If a file is a CDF then the TabPanel is automatically set up
     * as a Delimited panel with a comma delimiter.
     *
     * @param fileList The list of files to create TabPanels for.
     * @return A Map of File absolute paths to TabPanels.
     */
    public Map<String, TabPanel> createTabPanels(List<File> fileList) {

        Map<String, TabPanel> resultMap = new HashMap<>();

        for (File file : fileList) {

            String absPath = file.getAbsolutePath();
            PlainTextModel plainTextModel = new PlainTextModel(file);
            plainTextModel.readFile();
            TabPanel tabPanel = new TabPanel(plainTextModel, myApp.getPropsControl().getMyAppProperties());
            attachListenersToTabPanel(tabPanel);

            resultMap.put(absPath, tabPanel);

            if (absPath.endsWith(".cdf")) {
                tabPanel.setUpDelimitedPanel(',');
                updateDisplayComponents(tabPanel, 0, false);
            }
        }

        return resultMap;
    }

    /**
     * Attaches listeners to components of a TabPanel.
     * Listeners are attached to the TabPanel's JTextArea, buttons
     * used for displaying file types, and components for closing the tab.
     *
     * @param tabPanel The TabPanel to receive listeners.
     */
    private void attachListenersToTabPanel(final TabPanel tabPanel) {

        tabPanel.getTextArea().getDocument().addDocumentListener(getTextAreaDocListener(tabPanel));
        tabPanel.getTextArea().addCaretListener(getCaretListener(tabPanel));
        tabPanel.getControlPanel().getDelimitedButton().addActionListener(getDelimitedButtonAction(tabPanel));
        tabPanel.getControlPanel().getPositionalButton().addActionListener(getPositionalButtonAction(tabPanel));
        tabPanel.getTabComponentPanel().getCloseButton().addActionListener(myApp.getCloseAction(tabPanel.getPlainTextModel().getAbsolutePath()));
        tabPanel.getTextArea().addMouseListener(getTextAreaMouseListener(tabPanel));
        tabPanel.getTextArea().addMouseListener(getTripleClickMouseListener(tabPanel));
    }

    /**
     * Returns a MouseListener for use with a JTextArea. This listener is
     * only used for implementing right mouse click in order to open a
     * pop up menu. The pop up menu contains a JMenuItem for opening the
     * EntryDialog which allows the user to easily edit or copy an entry.
     *
     * @param tabPanel The TabPanel holding the JTextArea
     * @return The MouseListener
     */
    private MouseListener getTextAreaMouseListener(final TabPanel tabPanel) {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);

                if (SwingUtilities.isRightMouseButton(mouseEvent)) {

                    final JPopupMenu popupMenu = new JPopupMenu();
                    final JMenuItem editMenuItem = new JMenuItem("Edit Entry");
                    popupMenu.add(editMenuItem);

                    DisplayPanel displayPanel = tabPanel.getDisplayPanel();
                    int currentLine = tabPanel.getCurrentLine();

                    if (displayPanel instanceof DelimitedPanel) {

                        editMenuItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                getDelimitedEntryDialog(tabPanel, currentLine).displayDialog();
                            }
                        });

                    } else if (displayPanel instanceof PositionalPanel) {

                        editMenuItem.addActionListener(new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                getPositionalEntryDialog(tabPanel, currentLine).displayDialog();
                            }
                        });
                    }

                    popupMenu.show(tabPanel.getTextArea(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        };
    }

    /**
     * An Action for displaying a file as a delimited flatfile.
     *
     * @param tabPanel The selected TabPanel
     * @return The Action
     */
    private AbstractAction getDelimitedButtonAction(final TabPanel tabPanel) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                Character delimiter = delimitedCntrl.getFlatfileDelimiter(tabPanel.getTextArea());

                if (delimiter != Character.MIN_VALUE) {

                    tabPanel.setUpDelimitedPanel(delimiter);
                    updateDisplayComponents(tabPanel, 0, false);

                } else {

                    String userDelimiter = JOptionPane.showInputDialog(myApp.getAppFrame(), "Please enter the delimiter");
                    if (userDelimiter != null && userDelimiter.length() == 1) {
                        tabPanel.setUpDelimitedPanel(userDelimiter.charAt(0));
                        updateDisplayComponents(tabPanel, 0, false);
                    }
                }
            }
        };
    }

    /**
     * An Action for displaying a file as an positional flatfile.
     *
     * @param tabPanel The selected TabPanel
     * @return The Action
     */
    private AbstractAction getPositionalButtonAction(final TabPanel tabPanel) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                final PositionalDialog positionalDialog = new PositionalDialog(myApp.getAppFrame(), PathManager.getPositionalSchemaPath());

                positionalDialog.getSubmitButton().addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {

                        String selectedSchema = positionalDialog.getjList().getSelectedValue();

                        if (selectedSchema != null && !selectedSchema.equals("")) {

                            File schemaFile = new File(PathManager.getPositionalSchemaPath() + selectedSchema);
                            if (positionalCntrl.validateSchema(schemaFile)) {
                                tabPanel.setUpPositionalPanel(positionalCntrl.getSchemaData(schemaFile));
                                updateDisplayComponents(tabPanel, 0,false);
                                positionalDialog.dispose();
                            }
                        }
                    }
                });

                positionalDialog.getCancelButton().addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        positionalDialog.dispose();
                    }
                });

                positionalDialog.pack();
                positionalDialog.setLocationRelativeTo(myApp.getAppFrame());
                positionalDialog.setVisible(true);
            }
        };
    }

    /**
     * Configures and returns a CaretListener for use with a JTextArea.
     * If the caret moves to a new line then display components will be updated.
     *
     * @param tabPanel The TabPanel containing the text area that this listener is for.
     * @return A CaretListener.
     */
    private CaretListener getCaretListener(final TabPanel tabPanel) {
        return new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent caretEvent) {

                JTextArea jTextArea = tabPanel.getTextArea();

                //  Get the line the caret is positioned on
                Integer caretPosition = jTextArea.getCaretPosition();
                Element root = jTextArea.getDocument().getDefaultRootElement();
                Integer currentLine = root.getElementIndex(caretPosition);

                //  Update display components
                if (tabPanel.getCurrentLine() != currentLine) {
                    tabPanel.setPreviousLine(tabPanel.getCurrentLine());
                    tabPanel.setCurrentLine(currentLine);
                    updateDisplayComponents(tabPanel, currentLine,false);
                }
            }
        };
    }

    /**
     * Returns a listener for changes to the text of a JTextArea's Document.
     * Display components will be updated if the insertUpdate or removeUpdate is called.
     * The file's "dirty flag" will be updated.
     *
     * @param tabPanel The TabPanel containing the JTextArea Document that this listener is for.
     * @return A DocumentListener.
     */
    private DocumentListener getTextAreaDocListener(final TabPanel tabPanel) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) { changeText(documentEvent); }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) { changeText(documentEvent); }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {}

            private void changeText(DocumentEvent de) {

                try {
                    updateDisplayComponents(tabPanel, tabPanel.getTextArea().getLineOfOffset(de.getOffset()),true);
                    tabPanel.updateTabComponent(false); // update "dirty" flag for the Tab

                } catch (BadLocationException gtadlBadLocExcptn) {
                    System.err.println("Text Area Doc Listener threw BadLocationException: " + gtadlBadLocExcptn);
                }
            }
        };
    }

    /**
     * Returns a MouseListener for handling triple left button clicks.
     * While JTextArea has default selection and highlighting for triple
     * clicks, this App overrides that behavior by highlighting an entire line
     * with just one click. This listener is needed to distinguish between clicking
     * on a line and triple clicking (selecting the entire line).
     * @param tabPanel
     * @return
     */
    private MouseListener getTripleClickMouseListener(final TabPanel tabPanel) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 3) {
                    highlightForTripleClick(tabPanel);
                } else if (e.getClickCount() < 3) {
                    updateDisplayComponents(tabPanel, tabPanel.getCurrentLine(), false);
                }
            }
        };
    }

    /**
     * Updates the contents of display components for the TabPanel passed in.
     * This method should be called when one of these actions occur:
     *   1. The user elects to display a file using one of the file types
     *   2. A different line of the text area is selected
     *   3. The contents of the TabPanel's text area change (user edits file, undo/redo called, etc.)
     *
     * @param tabPanel The TabPanel whose contents should be updated.
     * @param textAreaIndex The index of the caret in the TabPanel's text area
     * @param textAreaChanged boolean indication whether the content of the TabPanel's text area changed.
     */
    private void updateDisplayComponents(final TabPanel tabPanel, final int textAreaIndex, final boolean textAreaChanged) {

        if (textAreaChanged) myApp.getSearchCntrl().resetSearch();

        if (tabPanel.getTextArea().getLineCount() >= textAreaIndex) {

            DisplayPanel displayPanel = tabPanel.getDisplayPanel();
            String textAreaRow = getRowString(tabPanel.getTextArea(), textAreaIndex);

            if (displayPanel instanceof DelimitedPanel) {
                DelimitedPanel delimitedPanel = (DelimitedPanel) displayPanel;
                List<String> rowFields = delimitedCntrl.parseString(textAreaRow, delimitedPanel.getMyDelimiter());
                delimitedPanel.setTextOnDisplayComponents(rowFields, textAreaIndex);
                tabPanel.getjSplitPane().setDividerLocation(tabPanel.getjSplitPane().getDividerLocation());
            }

            if (displayPanel instanceof PositionalPanel) {
                PositionalPanel positionalPanel = (PositionalPanel) displayPanel;
                List<String> rowFields = positionalCntrl.getPositionalRowAsList(textAreaRow, positionalPanel.getPositionalSchemaData().getMyFieldPositions());
                positionalPanel.setTextOnDisplayComponents(rowFields, textAreaIndex);
            }

            highlightCurrentLine(tabPanel, tabPanel.getPreviousLine(), textAreaIndex);
        }
    }

    /**
     * Highlights the selected line of text in the TabPanel's JTextArea using
     * the UIManager's color for "TextArea.selectionBackground".
     * This method assumes that the line in question is already highlighted
     * via a single click highlight, which should be a different color.
     * The TabPanel's highlight will be updated with the new Highlight.
     *
     * @param tabPanel The TabPanel whose JTextArea is to be highlighted.
     */
    private void highlightForTripleClick(final TabPanel tabPanel) {

        Highlighter.Highlight highlight = tabPanel.getTextAreaHighLight();

        if (highlight != null) {

            Highlighter highlighter = tabPanel.getTextArea().getHighlighter();
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(DefaultProperties.uiManagerSelectionBackgroundColor);
            int start = highlight.getStartOffset();
            int end = highlight.getEndOffset();
            highlighter.removeHighlight(highlight);

            try {
                tabPanel.setTextAreaHighLight(highlighter.addHighlight(start, end, painter));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Highlights the TabPanel's JTextArea's line of text where the caret is currently located.
     * This is to aid the user in quickly identifying the location of the caret.
     *
     * @param tabPanel The TabPanel whose JTextArea is to be highlighted.
     * @param previousLine The line of the JTextArea that the caret was previously located on.
     * @param currentLine The line of the JTextArea that the caret is located on.
     */
    private void highlightCurrentLine(final TabPanel tabPanel, int previousLine, int currentLine) {

        if (!myApp.getSearchCntrl().isUserSearching()) {
            if (currentLine != previousLine) {

                try {
                    int rowStartOffset = tabPanel.getTextArea().getLineStartOffset(currentLine);
                    int rowEndOffset = tabPanel.getTextArea().getLineEndOffset(currentLine);

                    Highlighter highlighter = tabPanel.getTextArea().getHighlighter();
                    Highlighter.HighlightPainter selectedPainter = new DefaultHighlighter.DefaultHighlightPainter(DefaultProperties.caretLineHighlightColor);

                    if (tabPanel.getTextAreaHighLight() != null)
                        highlighter.removeHighlight(tabPanel.getTextAreaHighLight());

                    tabPanel.setTextAreaHighLight(highlighter.addHighlight(rowStartOffset, rowEndOffset, selectedPainter));

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns a List of Strings parsed from a row of a file.
     *
     * @param tabPanel The TabPanel holding the JTextArea
     * @param textArea The JTextArea
     * @param textAreaIndex The index of the row of the JTextArea
     * @return
     */
    public List<String> getRowAsList(final TabPanel tabPanel, final JTextArea textArea, final int textAreaIndex) {
        List<String> rowAsList = new ArrayList<>();

        DisplayPanel displayPanel = tabPanel.getDisplayPanel();

        if (displayPanel instanceof DelimitedPanel) {
            DelimitedPanel delimitedPanel = (DelimitedPanel) displayPanel;
            rowAsList = delimitedCntrl.parseString(getRowString(textArea,textAreaIndex), delimitedPanel.getMyDelimiter());
        }

        if (displayPanel instanceof PositionalPanel) {
            rowAsList = new ArrayList<>();
            // TODO implement for positional file
        }
        return rowAsList;
    }

    /**
     * Returns a row as a String from a JTextArea.
     *
     * @param textArea The JTextArea
     * @param textAreaIndex The index of the row of the JTextArea
     * @return The row's String
     */
    public String getRowString(final JTextArea textArea, final int textAreaIndex) {

        String rowString = "";

        try {
            int lnStart = textArea.getLineStartOffset(textAreaIndex);
            int lnEnd = textArea.getLineEndOffset(textAreaIndex);
            rowString = textArea.getText(lnStart, lnEnd-lnStart);

        } catch (BadLocationException ble) {
            System.out.println("The line start and end are messed up!!!");
        }

        return rowString;
    }

    /**
     * Populates and returns a List of Strings representing all the
     * text of a JTextArea.
     *
     * @param textArea The JTextArea
     * @return A List of Strings representing rows of a file.
     */
    private List<String> getAllRowStrings(final JTextArea textArea) {

        List<String> result = new ArrayList<>();

        for (int i = 0; i < textArea.getLineCount(); i++) {

            String rowString = "";

            try {
                int lnStart = textArea.getLineStartOffset(i);
                int lnEnd = textArea.getLineEndOffset(i);
                rowString = textArea.getText(lnStart, lnEnd-lnStart);

            } catch (BadLocationException ble) {
                System.out.println("The line start and end are messed up!!!");
            }
            result.add(rowString);
        }
        return result;
    }

    /**
     *
     * @param tabPanel
     * @param currentLine
     * @return
     */
    private DelimitedEntryDialog getDelimitedEntryDialog(final TabPanel tabPanel, int currentLine) {

        final DelimitedPanel delimitedPanel = (DelimitedPanel) tabPanel.getDisplayPanel();
        final JTextArea jTextArea = tabPanel.getTextArea();

        final DelimitedEntryDialog delimitedEntryDialog = new DelimitedEntryDialog(
                delimitedPanel.isFakeHeader() ? delimitedPanel.getFakeHeaderFields() : getRowAsList(tabPanel, jTextArea, 0),
                getRowAsList(tabPanel, jTextArea, currentLine),
                myApp.getPropsControl().getMyAppProperties(),
                delimitedPanel.getMyDelimiter().toString()
        );

        delimitedEntryDialog.configCopyButton("Copy Entry", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringSelection stringSelection = new StringSelection(delimitedEntryDialog.getResultText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });
        delimitedEntryDialog.configWriteButton("Write Entry to File", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int lineStart = jTextArea.getLineStartOffset(currentLine);
                    int lineEnd = jTextArea.getLineEndOffset(currentLine);
                    jTextArea.replaceRange(delimitedEntryDialog.getResultText() + System.lineSeparator(), lineStart, lineEnd);
                    delimitedEntryDialog.dispose();
                    jTextArea.setCaretPosition(lineStart);

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });

        return delimitedEntryDialog;
    }

    /**
     *
     * @param tabPanel
     * @param currentLine
     * @return
     */
    private PositionalEntryDialog getPositionalEntryDialog(final TabPanel tabPanel, int currentLine) {

        final PositionalPanel positionalPanel = (PositionalPanel) tabPanel.getDisplayPanel();
        final JTextArea jTextArea = tabPanel.getTextArea();
        final PositionalEntryDialog positionalEntryDialog = new PositionalEntryDialog(
                positionalCntrl.getPositionalRowAsList(getRowString(tabPanel.getTextArea(), currentLine), positionalPanel.getPositionalSchemaData().getMyFieldPositions()),
                myApp.getPropsControl().getMyAppProperties(),
                positionalPanel.getPositionalSchemaData()
        );

        positionalEntryDialog.configCopyButton("Copy Entry", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringSelection stringSelection = new StringSelection(positionalEntryDialog.getResultText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        });

        positionalEntryDialog.configWriteButton("Write Entry to File", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    int lineStart = jTextArea.getLineStartOffset(currentLine);
                    int lineEnd = jTextArea.getLineEndOffset(currentLine);
                    jTextArea.replaceRange(positionalEntryDialog.getResultText() + System.lineSeparator(), lineStart, lineEnd);
                    positionalEntryDialog.dispose();
                    jTextArea.setCaretPosition(lineStart);

                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });

        return positionalEntryDialog;
    }
}

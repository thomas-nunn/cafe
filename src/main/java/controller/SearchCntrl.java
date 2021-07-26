package controller;

import model.SearchResult;
import model.persistence.DefaultProperties;
import view.AppFrame;
import view.SearchDialog;
import view.SearchPanel;
import view.file.TabPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tmn7 on 7/29/18.
 */
public class SearchCntrl {

    private final App myApp;
    private final AppFrame appFrame;
    private TabPanel tabPanel;
    private JTextArea textArea;
    private SearchDialog searchDialog;
    private List<String> lastNSearchStrings;
    private List<SearchResult> searchResultIndices;
    private List<List<SearchResult>> indexedSearchList;
    private int currentResultIndex;
    private int previousResultIndex = -1;;
    private int displayedResultCounter;
    private String currentSearchTerm;
    private SearchPanel findPanel;
    private SearchPanel findAndReplacePanel;
    private Highlighter highlighter;
    private Highlighter.HighlightPainter selectedPainter;
    private Highlighter.HighlightPainter nonSelectedPainter;
    private static final Integer MAX_SEARCH_STRINGS = 4;
    private boolean userSearching = false;


    SearchCntrl(App app) {

        myApp = app;
        appFrame = app.getAppFrame();
        searchDialog = new SearchDialog(appFrame,"Search");
        lastNSearchStrings = new ArrayList<>();
        searchResultIndices = new ArrayList<>();
        indexedSearchList = new ArrayList<>();
        currentResultIndex = 0;
        currentSearchTerm = "";
        setHighlightPainters();
        setUpDialog();
    }

    private void setUpDialog() {

        final ComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        findPanel = getSearchPanel(comboBoxModel,false);
        findAndReplacePanel = getSearchPanel(comboBoxModel,true);

        searchDialog.getTabbedPane().addTab("Find", findPanel);
        searchDialog.getTabbedPane().addTab("Find And Replace", findAndReplacePanel);
        searchDialog.pack();
        searchDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                super.windowClosed(windowEvent);
                closeSearchDialog();
            }
        });
    }

    void displaySearchBox() {

        userSearching = true;
        searchDialog.setVisible(true);
        findPanel.getFindComboBox().requestFocus();
    }

    void attachListeners() {

        appFrame.addItemtoSearchMenu(myApp.getMenuCntrl().
                createMenuItem("Next Result", getFindAction(findPanel, true), KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK)));
    }

    /**
     * Creates and configures a new SearchPanel.
     *
     * @param comboBoxModel The ComboBoxModel for the SearchPanel.
     * @param isFindAndReplace Determines if the SearchPanel has "find and replace" capabilities.
     * @return A new SearchPanel with listeners attached.
     */
    private SearchPanel getSearchPanel(final ComboBoxModel<String> comboBoxModel, final boolean isFindAndReplace) {

        final SearchPanel findPanel = new SearchPanel(comboBoxModel, isFindAndReplace);

        findPanel.getCloseButton().addActionListener(getCloseAction());
        findPanel.getForwardFindButton().addActionListener(getFindAction(findPanel, true));
        findPanel.getBackwardFindButton().addActionListener(getFindAction(findPanel, false));

        findPanel.getCaseCB().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                resetSearch();
            }
        });
        findPanel.getEntireWordCB().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                resetSearch();
            }
        });
        findPanel.getRegexCB().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {

                resetSearch();
            }
        });

        if (isFindAndReplace) findPanel.getReplaceButton().addActionListener(getFindAndReplaceAction(findPanel));

        return findPanel;
    }

    /**
     * Action for a "Replace All" button.
     * Prompts user to confirm the replacement action.
     * Calls appropriate file type method for replacing and
     * updates the SearchPanel with the results.
     *
     * @param findPanel The SearchPanel containing user input
     * @return The AbstractAction
     */
    private AbstractAction getFindAndReplaceAction(final SearchPanel findPanel) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String searchTerm = (String) findPanel.getFindComboBox().getSelectedItem();
                String replaceTerm = findPanel.getReplaceTextField().getText();

                if (searchTerm != null && !searchTerm.equals("")) {

                    int reply = JOptionPane.showConfirmDialog(searchDialog, "Replace \"" + searchTerm + "\" with \"" + replaceTerm + "\"?");

                    if (reply == JOptionPane.OK_OPTION) {

                        resetSearch();
                        int replaceCount = replaceAll(searchTerm, replaceTerm);
                        findPanel.getResultLabel().setText("Replaced " + replaceCount + " Instances");
                    }
                }
            }
        };
    }

    /**
     * Resets fields of this Class for starting a new search.
     */
    public void resetSearch() {
        highlighter.removeAllHighlights();
        searchResultIndices.clear();
        indexedSearchList.clear();
        currentResultIndex = 0;
        previousResultIndex = -1;
        displayedResultCounter = 0;
    }

    /**
     * Adds new search terms to the combo box. If the max number of
     * search terms allowed is exceeded, then the oldest search term
     * will be removed to make room for the new search term.
     *
     * @param searchTerm The search term.
     * @param comboBox The JComboBox containing search terms.
     */
    private void updateSearchTerms(String searchTerm, JComboBox<String> comboBox) {

        if (!searchTerm.equals(currentSearchTerm)) {

            resetSearch();

            if (!lastNSearchStrings.contains(searchTerm)) {

                if (lastNSearchStrings.size() == MAX_SEARCH_STRINGS) {
                    lastNSearchStrings.remove(0);
                    comboBox.removeItemAt(comboBox.getItemCount()-1);
                }
                lastNSearchStrings.add(searchTerm);
                comboBox.insertItemAt(searchTerm, 0);
            }
            currentSearchTerm = searchTerm;
        }
    }

    /**
     * Determines the next forward highlightTextAreaResults result by evaluating the currently selected row.
     * This method accounts for the user selecting a new starting point to continue the highlightTextAreaResults.
     *
     * @param selectedIndex The index of the currently selected row of a JTextArea
     * @return The index of the next SearchResult in searchResultIndices
     */
    private Integer getNextForwardResult(final int selectedIndex) {

        Integer nextResult;

        if (selectedIndex == searchResultIndices.get(currentResultIndex).getIndex()) {
            nextResult = (currentResultIndex == searchResultIndices.size()-1) ? 0 : currentResultIndex + 1;
        } else {
            nextResult = getNextResultFromNewPosition(selectedIndex, true);
        }

        return nextResult;
    }

    /**
     * Determines the next backward highlightTextAreaResults result by evaluating the currently selected row
     * This method accounts for the user selecting a new starting point to continue the highlightTextAreaResults.
     *
     * @param selectedIndex The index of the currently selected row of a JTextArea
     * @return The index of the next SearchResult in searchResultIndices
     */
    private Integer getNextBackwardResult(final int selectedIndex) {

        Integer nextResult;

        if (selectedIndex == searchResultIndices.get(currentResultIndex).getIndex()) {
            nextResult = (currentResultIndex == 0) ? searchResultIndices.size()-1 : currentResultIndex - 1;
        } else {
            nextResult = getNextResultFromNewPosition(selectedIndex, false);
        }

        return nextResult;
    }

    /**
     * Gets the index of the next search result when a user selects a new
     * starting point in the JTextArea.
     *
     * @param selectedIndex The currently selected row of the JTextArea.
     * @param isForward The direction to search in indexedSearchList.
     * @return The index of the next result in searchResultIndices.
     */
    private Integer getNextResultFromNewPosition(int selectedIndex, boolean isForward) {

        Integer nextSearchResult = 0;
        SearchResult sr = null;

        if (isForward) {
            for (int i = selectedIndex; i < indexedSearchList.size(); i++) { // get the correct SearchResult

                if (i > selectedIndex && !indexedSearchList.get(i).isEmpty()) {
                    sr = indexedSearchList.get(i).get(0);
                    break;
                }
            }
        } else {
            for (int i = selectedIndex; i >= 0; i--) { // get the correct SearchResult

                if (i < selectedIndex && !indexedSearchList.get(i).isEmpty()) {
                    sr = indexedSearchList.get(i).get(indexedSearchList.get(i).size()-1);
                    break;
                }
            }
        }

        if (sr != null) {

            for (int i = 0; i < searchResultIndices.size(); i++) { // get the index of the correct SearchResult
                if (sr.equals(searchResultIndices.get(i))) {
                    nextSearchResult = i;
                    break;
                }
            }
        }

        return nextSearchResult;
    }


    private AbstractAction getFindAction(final SearchPanel findPanel, final boolean isForwardSearch) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String searchTerm = (String) findPanel.getFindComboBox().getSelectedItem();

                if (searchDialog.isVisible() && searchTerm != null) {

                    updateSearchTerms(searchTerm, findPanel.getFindComboBox());

                    boolean updateElapsedTime = searchResultIndices.isEmpty();
                    long startTime = System.nanoTime();

                    if (searchResultIndices.isEmpty()) {
                        searchTextArea(searchTerm);
                        highlightTextAreaResults();
                    }
                    highlightCurrentResult(isForwardSearch);

                    String resultCounterMsg = displayedResultCounter + " of " + searchResultIndices.size();
                    findPanel.getResultLabel().setText(resultCounterMsg);
                    findAndReplacePanel.getResultLabel().setText(resultCounterMsg);

                    if (updateElapsedTime) {
                        long stopTime = System.nanoTime();
                        String timeResult = ((stopTime - startTime) / 1000000000) + "." + ((stopTime - startTime) % 1000000000);
                        DecimalFormat df = new DecimalFormat("#.##");

                        //System.out.println("Search Elapsed Time: " + df.format(Double.valueOf(timeResult)) + " seconds");
                    }
                }
            }
        };
    }

    /**
     * Searches a JTextArea for occurrences of searchTerm.
     * All matches are added to indexedSearchList and searchResultIndices.
     * This method should only be called for a new search (searchResultIndices is empty).
     *
     * @param searchTerm The user entered highlightTextAreaResults term
     */
    private void searchTextArea(String searchTerm) {

        Pattern pattern = getSearchPattern(searchTerm);
        int counter = 0;

        for (String s : textArea.getText().split("\\n")) {

            Matcher matcher = pattern.matcher(s);

            try {
                // get the offset for the start of the current row
                int rowStartOffset = textArea.getLineStartOffset(counter);
                List<SearchResult> tempSearchResults = new ArrayList<>();

                while (matcher.find()) {

                    int matchStart = matcher.start();
                    int matchEnd = matcher.end();

                    if (indexedSearchList.size() > counter) {
                        tempSearchResults.addAll(indexedSearchList.get(counter));
                        indexedSearchList.remove(counter);
                    }

                    // get the offsets for the entire document, not just the row
                    int hlStart = rowStartOffset + matchStart;
                    int hlEnd = rowStartOffset + matchEnd;

                    SearchResult searchResult = new SearchResult(counter, hlStart, hlEnd);
                    searchResult.setLineStart(matchStart);
                    searchResult.setLineEnd(matchEnd);
                    searchResultIndices.add(searchResult);
                    tempSearchResults.add(searchResult);

                }
                counter++;
                indexedSearchList.add(tempSearchResults);

            } catch (BadLocationException ble) {
                System.out.println("searchTextArea BadLocationException: " + ble);
            }
        }
    }

    /**
     * Removes existing highlights and then highlights each SearchResult with nonSelectedPainter.
     * Sets the Highlighter.Highlight Object for each SearchResult.
     * This method should only be called for a new search.
     */
    private void highlightTextAreaResults() {

        if (!searchResultIndices.isEmpty()) {

            try {
                highlighter.removeAllHighlights();

                for (SearchResult searchResult : searchResultIndices) {
                    searchResult.setTextAreaHighLight(highlighter.addHighlight(searchResult.getStart(), searchResult.getEnd(), nonSelectedPainter));
                }

            } catch (BadLocationException ble) {
                System.out.println("highlightTextAreaResults BadLocationException: " + ble);
            }
        }
    }

    /**
     * Highlights textArea and tabPanel's DisplayPanel for the current SearchResult.
     * Current SearchResult will have a different highlight color than all other SearchResults.
     *
     * @param isForward Indicates search direction.
     */
    private void highlightCurrentResult(boolean isForward) {

        if (!searchResultIndices.isEmpty()) {

            try {
                int selectedRow = textArea.getLineOfOffset(textArea.getCaretPosition());
                currentResultIndex = (isForward) ? getNextForwardResult(selectedRow) : getNextBackwardResult(selectedRow);
                highLightTextAreaCurrentResult();
                previousResultIndex = currentResultIndex;

            } catch (BadLocationException ble) {
                System.out.println("highlightCurrentResult BadLocationException: " + ble);
            }
        }
    }

    /**
     * For textArea, highlights current SearchResult with selectedPainter and previous
     * current SearchResult, if it exists, with nonSelectedPainter.
     * When highlighting text that already has a highlight, the existing highlight must be removed first.
     */
    private void highLightTextAreaCurrentResult() {

        SearchResult currentSR = searchResultIndices.get(currentResultIndex);
        textArea.setCaretPosition(currentSR.getStart());

        try {
            if (previousResultIndex >= 0) {
                SearchResult previousSR = searchResultIndices.get(previousResultIndex);
                highlighter.removeHighlight(previousSR.getTextAreaHighLight());
                previousSR.setTextAreaHighLight(highlighter.addHighlight(previousSR.getStart(), previousSR.getEnd(), nonSelectedPainter));
            }

            highlighter.removeHighlight(currentSR.getTextAreaHighLight());
            currentSR.setTextAreaHighLight(highlighter.addHighlight(currentSR.getStart(), currentSR.getEnd(), selectedPainter));

            displayedResultCounter = currentResultIndex + 1;
            Rectangle rec = textArea.modelToView(textArea.getCaretPosition());
            scrollToCurrentResult(currentSR, rec);

        } catch (BadLocationException ble) {
            System.out.println("highLightTextAreaCurrentResult BadLocationException: " + ble);
        }
    }

    /**
     * Scrolls the JTextArea's JScrollPane to allow viewing the current search result in full.
     *
     * @param searchResult The current SearchResult.
     * @param rectangle The
     */
    private void scrollToCurrentResult(SearchResult searchResult, Rectangle rectangle) {

        try {

            textArea.setCaretPosition(searchResult.getStart());

            String term = textArea.getText(searchResult.getStart(), searchResult.getEnd()-searchResult.getStart());
            int termWidth = textArea.getFontMetrics(textArea.getFont()).stringWidth(term);
            Rectangle recToScrollTo = new Rectangle(rectangle.x - termWidth*4, rectangle.y, rectangle.width + termWidth*8, rectangle.height);

            textArea.scrollRectToVisible(recToScrollTo);

         } catch (BadLocationException ble) {
            System.out.println("scrollToCurrentResult BadLocationException: " + ble);
         }
    }

    /**
     * Creates and returns a Pattern Object for use
     * in 'find' and 'find and replace' actions.
     *
     * @param searchTerm Search String for the Pattern.
     * @return The Pattern Object for matching
     */
    private Pattern getSearchPattern(String searchTerm) {

        Pattern pattern;
        SearchPanel searchPanel = (SearchPanel) searchDialog.getTabbedPane().getSelectedComponent();
        boolean matchCaseFlag;
        boolean matchEntireWordFlag;
        boolean isRegexSearch;

        if (searchPanel.isFindAndReplace()) {
            matchCaseFlag = findAndReplacePanel.getCaseCB().isSelected();
            matchEntireWordFlag = findAndReplacePanel.getEntireWordCB().isSelected();
            isRegexSearch = findAndReplacePanel.getRegexCB().isSelected();
        } else {
            matchCaseFlag = findPanel.getCaseCB().isSelected();
            matchEntireWordFlag = findPanel.getEntireWordCB().isSelected();
            isRegexSearch = findPanel.getRegexCB().isSelected();
        }

        try {
            if (isRegexSearch) {
                pattern = Pattern.compile(searchTerm);
            } else if (matchCaseFlag) {
                pattern = Pattern.compile(Pattern.quote(searchTerm));
            } else if (matchEntireWordFlag) {
                pattern = Pattern.compile("\\b" + Pattern.quote(searchTerm) + "\\b", Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE);
            }
        } catch (java.util.regex.PatternSyntaxException e) {
            JOptionPane.showMessageDialog(searchDialog, "INVALID REGEX !!!");
            pattern = Pattern.compile("ZZZZZZAZZZZZZ");
        }

        return pattern;
    }

    /**
     * Replaces all occurrences of searchTerm with replaceTerm in tabPanel's JTextArea.
     * This method calls the JTextArea's setText method, which removes all text first and
     * then inserts the new text. This dual operation may cause confusion if the user
     * calls "Undo" on the replace action, as the UndoManager stores the removal as an
     * action. This results in needing 2 "Undo" actions.
     *
     * @param searchTerm The String to search for.
     * @param replaceTerm The replacement String.
     * @return The number of Strings replaced.
     */
    private Integer replaceAll(String searchTerm, String replaceTerm) {

        int matchCount = 0;

        Pattern p = getSearchPattern(searchTerm);
        Matcher matcher = p.matcher(tabPanel.getTextArea().getText());

        while (matcher.find()) {
            matchCount++;
        }

        if (matchCount > 0) {
            myApp.getUndoRedoCntrl().setGoingToReplace(true);
            tabPanel.getTextArea().setText(matcher.replaceAll(replaceTerm));
            myApp.getUndoRedoCntrl().setGoingToReplace(false);
        }

        return matchCount;
    }


    private AbstractAction getCloseAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeSearchDialog();
            }
        };
    }

    private void closeSearchDialog() {

        userSearching = false;
        resetSearch();
        searchDialog.setVisible(false);

        for (TabPanel tabPanel : myApp.getTabPanelMap().values()) {
            tabPanel.getTextArea().getHighlighter().removeAllHighlights();
            tabPanel.getTextArea().setCaretPosition(tabPanel.getTextArea().getCaretPosition());
        }

        tabPanel.revalidate();
    }

    /**
     * Sets tabPanel field to a new TabPanel and resets highlightTextAreaResults data.
     *
     * @param currentTabPanel The new TabPanel.
     */
    void setTabPanel(TabPanel currentTabPanel) {
        tabPanel = currentTabPanel;
        textArea = tabPanel.getTextArea();
        highlighter = tabPanel.getTextArea().getHighlighter();
        selectedPainter = new DefaultHighlighter.DefaultHighlightPainter(myApp.getPropsControl().getMyAppProperties().getSelectedHighlight());
        nonSelectedPainter = new DefaultHighlighter.DefaultHighlightPainter(myApp.getPropsControl().getMyAppProperties().getNonSelectedHighlight());
        resetSearch();
    }

    public boolean isUserSearching() {
        return userSearching;
    }


    public void setHighlightPainters() {

        Color selectedHLColor = myApp.getPropsControl().getSelectedHighlightForSearch(myApp.getPropsControl().getMyAppProperties().getForeGroundColor());
        setSelectedColor(selectedHLColor);
        setNonSelectedColor(myApp.getPropsControl().getNonSelectedHighlightForSearch(selectedHLColor));
    }

    public void setSelectedColor(Color selectedColor) {
        selectedPainter = new DefaultHighlighter.DefaultHighlightPainter(selectedColor);
    }

    public void setNonSelectedColor(Color nonSelectedColor) {
        nonSelectedPainter = new DefaultHighlighter.DefaultHighlightPainter(nonSelectedColor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("currentSearchTerm: " + currentSearchTerm + "\n");
        sb.append("displayedResultCounter: " + displayedResultCounter + "\n");
        sb.append("searchResultIndices: " + searchResultIndices + "\n");
        sb.append("lastNSearchStrings: " + lastNSearchStrings + "\n");

        return sb.toString();
    }
}

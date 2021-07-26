package model;

import javax.swing.text.Highlighter;

/**
 * Created by tmn7 on 10/15/17.
 *
 * A Class for storing data about a match found when searching a JTextArea.
 */
public class SearchResult {

    /**
     * The 0-based index of the line of the text area that this SearchResult is on.
     */
    private Integer index;

    /**
     * The offsets, for the entire text area, of this SearchResult.
     */
    private Integer start,end;

    /**
     * The offsets, for the line of the text area, of this SearchResult.
     */
    private Integer lineStart,lineEnd;

    /**
     * The Highlight Object used in a JTextArea for this SearchResult.
     */
    private Highlighter.Highlight textAreaHighLight;

    /**
     * Constructs a new SearchResult with basic data.
     *
     * @param index The index of the text area.
     * @param startingOffset The starting offset.
     * @param endingOffset The ending offset.
     */
    public SearchResult(Integer index, Integer startingOffset, Integer endingOffset) {
        this.index = index;
        start = startingOffset;
        end = endingOffset;
    }

    /// Setters ///
    public void setLineStart(Integer lineStart) {this.lineStart = lineStart;}
    public void setLineEnd(Integer lineEnd) {this.lineEnd = lineEnd;}
    public void setTextAreaHighLight(Object highLight) {textAreaHighLight = (Highlighter.Highlight) highLight;}

    /// Getters ///
    public Integer getIndex() {return index;}
    public Integer getStart() {return start;}
    public Integer getEnd() {return end;}
    public Integer getLineStart() {return lineStart;}
    public Integer getLineEnd() {return lineEnd;}
    public Highlighter.Highlight getTextAreaHighLight() {return textAreaHighLight;}

    /**
     * @return The String representation of this SearchResult.
     */
    @Override
    public String toString() {
        return "Index: " + index + ", Start: " + start + ", End " + end + ", Line Start " + lineStart + ", Line End " + lineEnd;
    }
}

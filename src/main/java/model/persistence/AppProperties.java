package model.persistence;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Map;

public class AppProperties implements Serializable {

    private static final long serialversionUID = 17937249878L;
    private Color backGroundColor;
    private Color foreGroundColor;
    private Color selectedHighlight;
    private Color nonSelectedHighlight;
    private Font font;
    private Integer textAreaOrientation;
    private Integer textAreaPosition;
    private Integer tabPolicy;
    private String lastDirForFileChooser;
    private List<File> filesLeftOpen;
    private Map<String, Integer> frameDims;
    private Map<String, Map<String, List<String>>> favorites;


    public Color getBackGroundColor() { return backGroundColor; }
    public void setBackGroundColor(Color backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public Color getForeGroundColor() {
        return foreGroundColor;
    }
    public void setForeGroundColor(Color foreGroundColor) {
        this.foreGroundColor = foreGroundColor;
    }

    public Color getSelectedHighlight() {
        return selectedHighlight;
    }
    public void setSelectedHighlight(Color selectedHighlight) {
        this.selectedHighlight = selectedHighlight;
    }

    public Color getNonSelectedHighlight() {
        return nonSelectedHighlight;
    }
    public void setNonSelectedHighlight(Color nonSelectedHighlight) { this.nonSelectedHighlight = nonSelectedHighlight; }

    public Font getFont() {
        return font;
    }
    public void setFont(Font font) {
        this.font = font;
    }

    public Integer getTextAreaOrientation() {
        return textAreaOrientation;
    }
    public void setTextAreaOrientation(Integer textAreaOrientation) {
        this.textAreaOrientation = textAreaOrientation;
    }

    public Integer getTextAreaPosition() {
        return textAreaPosition;
    }
    public void setTextAreaPosition(Integer textAreaPosition) { this.textAreaPosition = textAreaPosition; }

    public Integer getTabPolicy() {
        return tabPolicy;
    }
    public void setTabPolicy(Integer tabPolicy) {
        this.tabPolicy = tabPolicy;
    }

    public List<File> getFilesLeftOpen() { return filesLeftOpen; }
    public void setFilesLeftOpen(List<File> filesLeftOpen) { this.filesLeftOpen = filesLeftOpen; }

    public Map<String, Integer> getFrameDims() {
        return frameDims;
    }
    public void setFrameDims(Map<String, Integer> frameDims) {
        this.frameDims = frameDims;
    }

    public String getLastDirForFileChooser() {
        return lastDirForFileChooser;
    }
    public void setLastDirForFileChooser(String lastDirForFileChooser) { this.lastDirForFileChooser = lastDirForFileChooser; }

    public Map<String, Map<String, List<String>>> getFavorites() {
        return favorites;
    }
    public void setFavorites(Map<String, Map<String, List<String>>> favorites) {
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Background Color: ").append(backGroundColor).append("\n")
                .append("Foreground Color: ").append(foreGroundColor).append("\n")
                .append("Font: ").append(font).append("\n")
                .append("Selected Highlight Color: ").append(selectedHighlight).append("\n")
                .append("Non-selected Highlight Color: ").append(nonSelectedHighlight).append("\n")
                .append("Text Area Orientation: ").append(textAreaOrientation).append("\n")
                .append("Text Area Position: ").append(textAreaPosition).append("\n")
                .append("Tab Policy: ").append(tabPolicy).append("\n")
                .append("Last Directory For File Chooser: ").append(lastDirForFileChooser).append("\n")
                .append("Files Left Open: ").append(filesLeftOpen).append("\n")
                .append("Frame Dims: ").append(frameDims).append("\n")
                .append("Favorites: ").append(favorites).append("\n");

        return sb.toString();
    }
}

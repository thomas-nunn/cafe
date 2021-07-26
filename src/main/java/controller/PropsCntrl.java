package controller;

import model.persistence.AppProperties;
import model.persistence.DefaultProperties;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropsCntrl {

    private final String myPropsAbsPath;
    private AppProperties myAppProperties;

    /**
     * Constructor
     * @param propsAbsPath The path to the properties file
     */
    public PropsCntrl(final String propsAbsPath) {
        myPropsAbsPath = propsAbsPath;
        setMyAppProperties();
        setHighlightColors();
    }

    /**
     * Initializes myAppProperties. If the properties file exists then
     * the Serialized Object is used. Otherwise a new AppProperties is
     * created with default values. If an IO Exception is thrown when
     * trying to get the Serialized Object then the default AppProperties
     * will be used.
     */
    private void setMyAppProperties() {

        File propsFile = new File(myPropsAbsPath);

        if (propsFile.exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(myPropsAbsPath);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                myAppProperties = (AppProperties) in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
                setMyAppPropsToDefault();
            } catch (ClassNotFoundException c) {
                System.out.println("AppProperties class not found");
                c.printStackTrace();
            }
        } else {
            setMyAppPropsToDefault();
        }
    }

    /**
     * Saves the Serialized AppProperties by writing to the properties file.
     */
    public void saveMyAppProperties() {

        try {
            FileOutputStream fos = new FileOutputStream(myPropsAbsPath);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(myAppProperties);
            out.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMyAppPropsToDefault() {

        if (myAppProperties == null) myAppProperties = new AppProperties();
        myAppProperties.setBackGroundColor(DefaultProperties.defaultBackgroundColor);
        myAppProperties.setForeGroundColor(DefaultProperties.defaultForegroundColor);
        myAppProperties.setFont(DefaultProperties.defaultFont);
        myAppProperties.setTabPolicy(0);
        myAppProperties.setLastDirForFileChooser("");
        myAppProperties.setFavorites(new HashMap<>());
        myAppProperties.setFilesLeftOpen(new ArrayList<File>());
        myAppProperties.setTextAreaOrientation(0);
        myAppProperties.setTextAreaPosition(0);

        Map<String,Integer> frameDims = new HashMap<>();
        frameDims.put("x", DefaultProperties.frameX);
        frameDims.put("y", DefaultProperties.frameY);
        frameDims.put("width", DefaultProperties.frameWidth);
        frameDims.put("height", DefaultProperties.frameHeight);
        myAppProperties.setFrameDims(frameDims);
    }

    private void setHighlightColors() {

        myAppProperties.setSelectedHighlight(getSelectedHighlightForSearch(myAppProperties.getForeGroundColor()));
        myAppProperties.setNonSelectedHighlight(myAppProperties.getSelectedHighlight());
    }

    public boolean addFavorite(final String groupTitle, final String favTitle, final java.util.List<String> filePaths) {

        if (myAppProperties.getFavorites().containsKey(groupTitle)) {

            if (myAppProperties.getFavorites().get(groupTitle).containsKey(favTitle)) {
                myAppProperties.getFavorites().get(groupTitle).get(favTitle).addAll(filePaths);
            } else {
                myAppProperties.getFavorites().get(groupTitle).put(favTitle,filePaths);
            }

        } else {
            Map<String, java.util.List<String>> groupFav = new HashMap<>();
            groupFav.put(favTitle,filePaths);
            myAppProperties.getFavorites().put(groupTitle,groupFav);
        }

        return myAppProperties.getFavorites().containsKey(groupTitle) && myAppProperties.getFavorites().get(groupTitle).containsKey(favTitle);
    }

    public boolean deleteGroups(List<String> groupsToDelete) {
        boolean result = true;

        for (String group : groupsToDelete) {
            myAppProperties.getFavorites().remove(group);
        }

        return result;
    }

    public boolean deleteFavorites(final String group, final java.util.List<String> titlesToDelete) {
        boolean result = true;

        for (String title : titlesToDelete) {
            myAppProperties.getFavorites().get(group).remove(title);
        }

        return result;
    }

    public boolean deleteFavoriteFiles(Map<String, Map<String, List<String>>> fileToDeleteMap) {
        boolean result = false;

        for (String group : fileToDeleteMap.keySet()) {

            for (String title : fileToDeleteMap.get(group).keySet()) {

                if (myAppProperties.getFavorites().get(group).containsKey(title)) {

                    List<String> favAndFiles = myAppProperties.getFavorites().get(group).get(title);

                    for (String filePath : fileToDeleteMap.get(group).get(title)) {

                        if (favAndFiles.contains(filePath)) {
                            favAndFiles.remove(filePath);
                            result = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Determines if fgColor is light or dark and returns a highlight color
     * that is suitable for highlighting the current search result.
     *
     * @param fgColor The Color to base the highlight on.
     * @return A new Color for highlighting.
     */
    protected Color getSelectedHighlightForSearch(Color fgColor) {

        Color adjustedFGColor;
        double Y = 0.2126 * fgColor.getRed() + 0.7152 * fgColor.getGreen() + 0.0722 * fgColor.getBlue();

        if (Y < 128) { // the foreground color is dark
            adjustedFGColor = Color.PINK;
        } else {
            adjustedFGColor = Color.RED;
        }

        return getMixedWithYellowColor(adjustedFGColor);
    }

    /**
     * Returns a different shade of selectedHighlightColor.
     *
     * @param selectedHighlightColor The selected hightlight Color.
     * @return A new Color for highlighting all other search results besides the current one.
     */
    protected Color getNonSelectedHighlightForSearch(Color selectedHighlightColor) {

        float[] hsbVals = Color.RGBtoHSB(selectedHighlightColor.getRed(), selectedHighlightColor.getGreen(), selectedHighlightColor.getBlue(), null);
        return Color.getHSBColor(hsbVals[0], hsbVals[1], 0.3f * ( 1f + hsbVals[2]));
    }

    /**
     * @param theColor The Color to be mixed with yellow.
     * @return A new Color that is a mix of theColor and yellow.
     */
    private Color getMixedWithYellowColor(Color theColor) {

        Color yellow = Color.YELLOW;

        double totalAlpha = theColor.getAlpha() + yellow.getAlpha();
        double weight0 = theColor.getAlpha() / totalAlpha;
        double weight1 = yellow.getAlpha() / totalAlpha;

        double r = weight0 * theColor.getRed() + weight1 * yellow.getRed();
        double g = weight0 * theColor.getGreen() + weight1 * yellow.getGreen();
        double b = weight0 * theColor.getBlue() + weight1 * yellow.getBlue();
        double a = Math.max(theColor.getAlpha(), yellow.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    /**
     * @return myAppProperties
     */
    public AppProperties getMyAppProperties() { return myAppProperties; }
}

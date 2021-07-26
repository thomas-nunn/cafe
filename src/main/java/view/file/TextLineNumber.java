package view.file;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 *  This class will display line numbers for a related text component. The text
 *  component must use the same line height for each line. TextLineNumber
 *  supports wrapped lines and will highlight the line number of the current
 *  line in the text component.
 *
 *  This class was designed to be used as a component added to the row header
 *  of a JScrollPane.
 */
public class TextLineNumber extends JPanel
        implements CaretListener, DocumentListener, PropertyChangeListener {

    public final static float LEFT = 0.0f;
    public final static float CENTER = 0.5f;
    public final static float RIGHT = 1.0f;

    private final static Border OUTER = new MatteBorder(0, 0, 0, 2, Color.GRAY);

    private final static int HEIGHT = Integer.MAX_VALUE - 1000000;

    //  Text component this TextTextLineNumber component is in sync with

    private JTextComponent component;

    //  Properties that can be changed

    private boolean updateFont;
    private int borderGap;
    private Color currentLineForeground;
    private float digitAlignment;
    private int minimumDisplayDigits;

    //  Keep history information to reduce the number of times the component
    //  needs to be repainted

    private int lastDigits;
    private int lastHeight;
    private int lastLine;

    private Map<String, FontMetrics> fonts;
    private Map<Integer, Integer> yToLineMap;

    /**
     *	Create a line number component for a text component. This minimum
     *  display width will be based on 3 digits.
     *
     *  @param component  the related text component
     */
    public TextLineNumber(JTextComponent component) {
        this(component, 3);
    }

    /**
     *	Create a line number component for a text component.
     *
     *  @param component  the related text component
     *  @param minimumDisplayDigits  the number of digits used to calculate
     *                               the minimum width of the component
     */
    public TextLineNumber(JTextComponent component, int minimumDisplayDigits) {
        this.component = component;

        setFont( component.getFont() );
        setBackground(Color.GRAY);
        setForeground(component.getForeground());

        setBorderGap(5);
        setCurrentLineForeground(component.getBackground());
        setDigitAlignment( RIGHT );
        setMinimumDisplayDigits( minimumDisplayDigits );

        component.getDocument().addDocumentListener(this);
        component.addCaretListener( this );
        component.addPropertyChangeListener("font", this);
        addMouseListener(getMouseListener());
        yToLineMap = new HashMap<>();
    }

    /**
     *  Gets the update font property
     *
     *  @return the update font property
     */
    public boolean getUpdateFont() {
        return updateFont;
    }

    /**
     *  Set the update font property. Indicates whether this Font should be
     *  updated automatically when the Font of the related text component
     *  is changed.
     *
     *  @param updateFont  when true update the Font and repaint the line
     *                     numbers, otherwise just repaint the line numbers.
     */
    public void setUpdateFont(boolean updateFont) {
        this.updateFont = updateFont;
    }

    /**
     *  Gets the border gap
     *
     *  @return the border gap in pixels
     */
    public int getBorderGap() {
        return borderGap;
    }

    /**
     *  The border gap is used in calculating the left and right insets of the
     *  border. Default value is 5.
     *
     *  @param borderGap  the gap in pixels
     */
    public void setBorderGap(int borderGap) {
        this.borderGap = borderGap;
        Border inner = new EmptyBorder(0, borderGap, 0, borderGap);
        setBorder( new CompoundBorder(OUTER, inner) );
        lastDigits = 0;
        setPreferredWidth();
    }

    /**
     *  Gets the current line rendering Color
     *
     *  @return the Color used to render the current line number
     */
    public Color getCurrentLineForeground() {
        return currentLineForeground == null ? getForeground() : currentLineForeground;
    }

    /**
     *  The Color used to render the current line digits. Default is Coolor.RED.
     *
     *  @param currentLineForeground  the Color used to render the current line
     */
    public void setCurrentLineForeground(Color currentLineForeground) {
        this.currentLineForeground = currentLineForeground;
    }

    /**
     *  Gets the digit alignment
     *
     *  @return the alignment of the painted digits
     */
    public float getDigitAlignment() {
        return digitAlignment;
    }

    /**
     *  Specify the horizontal alignment of the digits within the component.
     *  Common values would be:
     *  <ul>
     *  <li>TextLineNumber.LEFT
     *  <li>TextLineNumber.CENTER
     *  <li>TextLineNumber.RIGHT (default)
     *	</ul>
     *  @param digitAlignment
     */
    public void setDigitAlignment(float digitAlignment) {
        this.digitAlignment =
                digitAlignment > 1.0f ? 1.0f : digitAlignment < 0.0f ? -1.0f : digitAlignment;
    }

    /**
     *  Gets the minimum display digits
     *
     *  @return the minimum display digits
     */
    public int getMinimumDisplayDigits() {
        return minimumDisplayDigits;
    }

    /**
     *  Specify the mimimum number of digits used to calculate the preferred
     *  width of the component. Default is 3.
     *
     *  @param minimumDisplayDigits  the number digits used in the preferred
     *                               width calculation
     */
    public void setMinimumDisplayDigits(int minimumDisplayDigits) {
        this.minimumDisplayDigits = minimumDisplayDigits;
        setPreferredWidth();
    }

    /**
     *  Calculate the width needed to display the maximum line number
     */
    private void setPreferredWidth() {

        Element root = component.getDocument().getDefaultRootElement();
        int lines = root.getElementCount();
        int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);

        //  Update sizes when number of digits in the line number changes

        if (lastDigits != digits) {

            lastDigits = digits;
            FontMetrics fontMetrics = getFontMetrics( getFont() );
            int width = fontMetrics.charWidth( '0' ) * digits;
            Insets insets = getInsets();
            int preferredWidth = insets.left + insets.right + width;

            Dimension d = getPreferredSize();
            d.setSize(preferredWidth, HEIGHT);
            setPreferredSize( d );
            setSize( d );
        }
    }

    /**
     *  Draw the line numbers
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //	Determine the width of the space available to draw the line number
        FontMetrics fontMetrics = component.getFontMetrics( component.getFont() );
        Insets insets = getInsets();
        int availableWidth = getSize().width - insets.left - insets.right;

        //  Determine the rows to draw within the clipped bounds.
        Rectangle clip = g.getClipBounds();
        int rowStartOffset = component.viewToModel( new Point(0, clip.y) );
        int endOffset = component.viewToModel( new Point(0, clip.y + clip.height) );

        while (rowStartOffset <= endOffset) {
            try {
                if (isCurrentLine(rowStartOffset) || isLineSelected(rowStartOffset)) {
                    g.setColor(getCurrentLineForeground());
                } else { g.setColor(getForeground()); }

                //  Get the line number as a string and then determine the
                //  "X" and "Y" offsets for drawing the string.
                String lineNumber = getTextLineNumber(rowStartOffset);
                int stringWidth = fontMetrics.stringWidth( lineNumber );
                int x = getOffsetX(availableWidth, stringWidth) + insets.left;
                int y = getOffsetY(rowStartOffset, fontMetrics);
                g.drawString(lineNumber, x, y);

                yToLineMap.put(y, Integer.parseInt(lineNumber)+1);

                //  Move to the next row
                rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
            }
            catch(Exception e) {break;}
        }
    }

    /*
     *  We need to know if the caret is currently positioned on the line we
     *  are about to paint so the line number can be highlighted.
     */
    private boolean isCurrentLine(int rowStartOffset) {
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();
        return (root.getElementIndex( rowStartOffset ) == root.getElementIndex(caretPosition));
    }

    private boolean isLineSelected(int rowStartOffset) {

        Highlighter hl = component.getHighlighter();
        boolean boo = false;

        for (Highlighter.Highlight hLight : hl.getHighlights()) {
            if (!boo) {
                boo = rowStartOffset >= hLight.getStartOffset() && rowStartOffset < hLight.getEndOffset();
            }
        }

        return boo;
    }

    /*
     *	Get the line number to be drawn. The empty string will be returned
     *  when a line of text has wrapped.
     */
    protected String getTextLineNumber(int rowStartOffset) {
        Element root = component.getDocument().getDefaultRootElement();
        int index = root.getElementIndex( rowStartOffset );
        Element line = root.getElement( index );

        return (line.getStartOffset() == rowStartOffset) ? String.valueOf(index + 1) : "";
    }

    /*
     *  Determine the X offset to properly align the line number when drawn
     */
    private int getOffsetX(int availableWidth, int stringWidth) {
        return (int)((availableWidth - stringWidth) * digitAlignment);
    }

    /*
     *  Determine the Y offset for the current row
     */
    private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics)
            throws BadLocationException {
        //  Get the bounding rectangle of the row

        Rectangle r = component.modelToView( rowStartOffset );
        int lineHeight = fontMetrics.getHeight();
        int y = r.y + r.height;
        int descent = 0;

        //  The text needs to be positioned above the bottom of the bounding
        //  rectangle based on the descent of the font(s) contained on the row.

        if (r.height == lineHeight) { // default font is being used
            descent = fontMetrics.getDescent();
        }
        else { // We need to check all the attributes for font changes

            if (fonts == null)
                fonts = new HashMap<String, FontMetrics>();

            Element root = component.getDocument().getDefaultRootElement();
            int index = root.getElementIndex( rowStartOffset );
            Element line = root.getElement( index );

            for (int i = 0; i < line.getElementCount(); i++) {
                Element child = line.getElement(i);
                AttributeSet as = child.getAttributes();
                String fontFamily = (String)as.getAttribute(StyleConstants.FontFamily);
                Integer fontSize = (Integer)as.getAttribute(StyleConstants.FontSize);
                String key = fontFamily + fontSize;

                FontMetrics fm = fonts.get( key );

                if (fm == null) {
                    Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                    fm = component.getFontMetrics( font );
                    fonts.put(key, fm);
                }

                descent = Math.max(descent, fm.getDescent());
            }
        }

        return y - descent;
    }

    //
//  Implement CaretListener interface
//
    @Override
    public void caretUpdate(CaretEvent e) {

        //  Get the line the caret is positioned on
        int caretPosition = component.getCaretPosition();
        Element root = component.getDocument().getDefaultRootElement();
        int currentLine = root.getElementIndex( caretPosition );

        //  Need to repaint so the correct line number can be highlighted
        if (lastLine != currentLine) {
            repaint();
            lastLine = currentLine;
        }
    }

    //
//  Implement DocumentListener interface
//
    @Override
    public void changedUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentChanged();
    }

    /*
     *  A document change may affect the number of displayed lines of text.
     *  Therefore the lines numbers will also change.
     */
    private void documentChanged() {
        //  View of the component has not been updated at the time
        //  the DocumentEvent is fired

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    int endPos = component.getDocument().getLength();
                    Rectangle rect = component.modelToView(endPos);

                    if (rect != null && rect.y != lastHeight) {
                        setPreferredWidth();
                        repaint();
                        lastHeight = rect.y;
                    }
                }
                catch (BadLocationException ex) { /* nothing to do */ }
            }
        });
    }

    //
    //  Implement PropertyChangeListener interface
    //
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() instanceof Font) {
            if (updateFont) {
                Font newFont = (Font) evt.getNewValue();
                setFont(newFont);
                lastDigits = 0;
                setPreferredWidth();
                yToLineMap.clear();
            }
            repaint();
        }
    }

    /**
     * @return The MouseListener
     */
    private MouseListener getMouseListener() {

        return new MouseAdapter() {

            /**
             * Gets the y coordinate of the mouse click and highlights
             * the corresponding row of the Component.
             * The correct row is stored in yToLineMap and the y
             * coordinate value is decremented until a match is
             * found in yToLineMap.
             *
             * @param mouseEvent The mouseClicked MouseEvent
             */
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                int y = mouseEvent.getY();

                while (!yToLineMap.containsKey(y)) {
                    y--;
                    if (y < 0) break;
                }

                Element root = component.getDocument().getDefaultRootElement();
                int line = (yToLineMap.containsKey(y)) ? yToLineMap.get(y) : 1;
                line = Math.max(line, 1);
                line = Math.min(line, root.getElementCount());
                int startOfLineOffset = root.getElement(line - 1).getStartOffset();

                // must set caret before calling triple click //
                component.setCaretPosition(startOfLineOffset);
                ////////////////////////////////////////////////

                try {
                    Rectangle rectangle = component.modelToView(startOfLineOffset);
                    Point p = rectangle.getLocation();
                    tripleClick(p.x, p.y);
                } catch (BadLocationException ble) {}
            }
        };
    }

    /**
     * Simulates the user triple clicking the left mouse button on a line.
     * This behavior results in the entire line being selected.
     * @param x x coordinate for clicking.
     * @param y y coordinate for clicking.
     */
    private void tripleClick(int x, int y) {

        Point point = new Point(x, y);
        SwingUtilities.convertPointToScreen(point, component);
        component.requestFocus();
        component.dispatchEvent(new MouseEvent(component,
                MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y,
                point.x, point.y,3,false, MouseEvent.BUTTON1));
    }

}

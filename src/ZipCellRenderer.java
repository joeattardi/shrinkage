import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/*
 * The cell renderer for the ZIP table. This is necessary because
 * the Filename column is aligned left, while the other columns
 * are aligned right.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class ZipCellRenderer extends JLabel implements TableCellRenderer
{
    
    /**
     * Creates the StringCellRenderer.
     */
    public ZipCellRenderer()
    {
        super();
    }
    
    /**
     * Renders a particular long cell. If the column is 1, it will be
     * a left-aligned JLabel, otherwise it'll be a right-aligned JLabel.
     *
     * @param jTable The table
     * @param value The value of the cell.
     * @param isSelected Whether or not the cell is selected.
     * @param hasFocus Whether or not the cell has focus
     * @param nRow The row of the currently selected cell.
     * @param nColumn The column of the currently selected cell.
     *
     * @return The component that the cell will be rendered with.
     */
    public Component getTableCellRendererComponent(JTable jTable, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int nRow, int nColumn)
    {                             
        if(nColumn == 0)            
            setHorizontalAlignment(SwingConstants.LEFT);                
        else
            setHorizontalAlignment(SwingConstants.RIGHT);
        
        setText(value.toString());
        
        return this;
    }    
    
}

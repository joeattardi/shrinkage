import javax.swing.table.*;
import java.util.*;

/*
 * Table model for the table that displays ZIP file entries.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attardi <jattardi@cs.uml.edu>
 */
public class ShrinkageTableModel extends AbstractTableModel
{
    /** The column names. */
    private static final String[] columnNames = {"Name", "Size", "Compressed", "Ratio", "Path"};

    /** Vector for holding the entry row data. */
    private Vector vecRows;

    /**
     * Creates a new ShrinkageTableModel.
     */
    public ShrinkageTableModel()
    {
        vecRows = new Vector();
    }

    /**
     * Gets the number of columns.
     * @return The number of columns.
     */
    public int getColumnCount()
    {
        return columnNames.length;
    }

    /**
     * Gets the number of rows.
     * @return The number of rows.
     */
    public int getRowCount()
    {
        return vecRows.size();
    }

    /**
     * Gets the name of a column.
     * @param nColumn The column to get.
     * @return The name of this column.
     */
    public String getColumnName(int nColumn)
    {
        return columnNames[nColumn];
    }

    /**
     * Deletes a row in the table.
     * @param nRow The row to delete.
     */
    public void deleteRow(int nRow)
    {
        vecRows.removeElementAt(nRow);
        fireTableDataChanged();
    }

    /**
     * Retrieves the data at a given location in the table.
     * @param nRow The row to look in.
     * @param nColumn The column to look in.
     * @return The object containd in that location.
     */
   public Object getValueAt(int nRow, int nColumn)
   {
       Object value = ((Vector)vecRows.get(nRow)).get(nColumn);

       if(value.getClass().equals(String.class))
           return (String)value;
       else if(value.getClass().equals(Long.class))
           return (Long)value;
       else
           return value;
   }

   public Class getColumnClass(int nColumn)
   {
        return getValueAt(0, nColumn).getClass();
   }

   /**
    * Sets the data at a given location in the table.
    * @param value The value to set.
    * @param nRow The row to set at.
    * @param nColumn The column to set at.
    */
   public void setValueAt(Object value, int nRow, int nColumn)
   {
        if(getValueAt(nRow, nColumn) instanceof String)
            ((Vector)vecRows.get(nRow)).setElementAt((String)value, nColumn);
        else if(getValueAt(nRow, nColumn) instanceof Long)
            ((Vector)vecRows.get(nRow)).setElementAt((Long)value, nColumn);
        else
            ((Vector)vecRows.get(nRow)).setElementAt(value, nColumn);

        fireTableCellUpdated(nRow, nColumn);
   }

   /**
    * Adds an entry to this table.
    *
    * @param strName The filename.
    * @param lSize The size.
    * @param lCompressed The compressed size.
    * @param strPath The full path of the filename.
    */
   public void addEntry(String strName, long lSize, long lCompressed, String strPath)
   {
       int nPercent = 0;

       Vector vecNewRow = new Vector();
       vecNewRow.add(strName);
       vecNewRow.add(new Long(lSize));
       vecNewRow.add(new Long(lCompressed));

       try{nPercent = (int)(((float)lCompressed/(float)lSize)*100);}
       catch(ArithmeticException ae) {nPercent = 0;}
       vecNewRow.add(nPercent + "%");

       vecNewRow.add(strPath);
       vecRows.add(vecNewRow);

       fireTableDataChanged();
   }

   /**
    * Clears the table.
    */
   public void clearTable( )
   {
        vecRows = new Vector( );
        fireTableDataChanged();
   }
}

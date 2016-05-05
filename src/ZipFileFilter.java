import java.io.*;

/**
 * A file filter used in the file chooser to ensure that only ZIP archives appear
 * in the file chooser.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joe Attardi <jattardi@cs.uml.edu>
 */
public class ZipFileFilter extends javax.swing.filechooser.FileFilter
{
    /**
     * Determines whether or not to accept a file under this filter.
     * @param file A File object representing the file to filter.
     * @return true if the filename is included in the filter, false otherwise.
     */
    public boolean accept( File file )
    {
       return (file.getName().toLowerCase().endsWith(".zip") || file.isDirectory());

    }

    /**
     * Gets the name of this filter.
     * @return The name of the filter.
     */
    public String getDescription()
    {
        return "ZIP archives (*.zip)";
    }
}

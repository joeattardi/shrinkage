import java.io.*;
import java.util.zip.*;
import java.util.*;

/**
 * Handles all operations with ZIP Archive files.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class ZipHandler
{
    /**
     * Creates a ZipHandler. Note that a ZIP file is not opened at this point.
     */
    public ZipHandler () {}

    /**
     * Extracts the contents of a ZIP file in its own thread.
     * This method will launch an ExtractThread and an ExtractingWindow
     * to handle the extraction process in the background.
     * @param fileArchive A File object representing the ZIP file.
     * @param strBaseDir The base directory to extract to.
     * @param frm The parent frame of the ExtractingWindow dialog that will be shown.
     * @throws IOException if a file I/O error occurs
     * @throws ZipException if a ZIP error occurs
     */
    public void extractArchive(File fileArchive, String strBaseDir, MainFrame frm)
        throws IOException, ZipException
    {
        long lTotalSize = getTotalSize(fileArchive);

        ExtractThread et = new ExtractThread(frm, fileArchive, strBaseDir, lTotalSize);
        et.start();
    }

    /**
     * Gets the total uncompressed size of a ZIP file.
     * This works by stepping through each ZIP entry and adding up each
     * uncompressed size.
     * @param fileArchive A File object representing the ZIP file.
     * @return The total combined size.
     * @throws IOException is a file I/O error occurs
     * @throws ZipException if a ZIP error occurs
     */
    private long getTotalSize(File fileArchive) throws IOException, ZipException
    {
        ZipFile zfArchive = null;
        ZipEntry entryCurr = null;
        long lTotalSize = 0;

        // Throw an exception if the file doesn't exist.
        if (!fileArchive.exists())
            throw new FileNotFoundException("File not found");

        // Otherwise, go through each entry and add up each uncompressed
        // size to the total.
        try
        {
            zfArchive = new ZipFile(fileArchive);
            Enumeration enumEntries = zfArchive.entries();

            while (enumEntries.hasMoreElements())
            {
                entryCurr = (ZipEntry)enumEntries.nextElement();
                lTotalSize += entryCurr.getSize();
            }
        }
        catch(ZipException ze) { lTotalSize = 0; }

        return lTotalSize;
    }

    /**
     * Adds a file or files to a ZIP file.
     * @param frm The parent frame for the dialog that will appear.
     * @param fileArchive A File object representing the ZIP file.
     * @param filesToAdd An array of File objects representing the files to add.
     * @param bUpdate Whether or not the archive contains files that must be updated.
     * @throws IOException if an I/O error occurs
     * @throws ZipException if a ZIP error occurs
     */
    public void add(MainFrame frm, File fileArchive, File[] filesToAdd, boolean bUpdate)
        throws IOException, ZipException
    {
        long lCurrentSize = 0;
        long lSizeToAdd = 0;

        if (bUpdate)
            lCurrentSize = getTotalSize(fileArchive);

        if (frm.isEmpty())
            fileArchive.delete();

        // Step through each file and add the size to the size to add.
        for (int n = 0; n < filesToAdd.length; n++)
        {
            // If the file doesn't exist, throw an exception.
            if (!filesToAdd[n].exists())
                throw new FileNotFoundException("File not found: " + filesToAdd[n].getAbsolutePath());
            else
                lSizeToAdd += filesToAdd[n].length();
        }

        long lNewSize = lCurrentSize + lSizeToAdd;

        // Create the AddThread. The AddThread will create the UpdateProgress dialog
        // within it.
        AddThread at = new AddThread(frm, fileArchive, filesToAdd, lNewSize, bUpdate);
        at.start();
    }

    /**
     * Removes an entry from a ZIP file.
     * The only way I found to do this is to just rewrite the entire ZIP file,
     * leaving out the entry to be removed.
     * @param frm The parent frame of the dialog that will be displayed.
     * @param fileArchive A File object representing the ZIP file.
     * @param strEntryName The name of the ZIP entry to remove.
     * @throws IOException if an I/O error occurs
     * @throws ZipException if a ZIP error occurs
     */
    public void remove(MainFrame frm, File fileArchive, String strEntryName)
        throws IOException, ZipException
    {
        long lCurrentSize = getTotalSize(fileArchive);
        ZipFile zf = new ZipFile(fileArchive);
        ZipEntry entryToRemove;
        long lSizeToRemove = 0;

        entryToRemove = zf.getEntry(strEntryName);
        lSizeToRemove = entryToRemove.getSize();

        long lNewSize = lCurrentSize - lSizeToRemove;

        // Create the RemoveThread. The RemoveThread will create the UpdateProgress dialog
        // within it.
        RemoveThread rt = new RemoveThread(frm, fileArchive, strEntryName, lNewSize);
        rt.start();
    }

    /**
     * Gets a list of files stored in a ZIP file.
     * @param fileArchive A File object representing the ZIP file.
     * @return an Enumeration containing ZipEntry objects for each entry.
     * @throws IOException if an I/O error occurs
     * @throws ZipException if a ZIP error occurs
     */
     public Enumeration getZipContents (File fileArchive)
        throws IOException, ZipException
    {
        // Throw an exception if the file doesn't exist.
        if (!fileArchive.exists())
            throw new FileNotFoundException("File not found");

        // Otherwise, open the file and return its contents.
        ZipFile zfArchive = new ZipFile(fileArchive);
        return zfArchive.entries();
    }

    /**
     * Gets an input stream to a particular ZIP entry in a ZIP file.
     * @param fileArchive A File object representing the ZIP file.
     * @param strEntryName The name of the ZIP entry we want.
     * @return an InputStream to the requested entry.
     * @throws IOException if an I/O error occurs
     * @throws ZipException if a ZIP error occurs
     */
    public InputStream getEntryInputStream(File fileArchive, String strEntryName)
        throws IOException, ZipException
    {
        // Throw an exception if the file doesn't exist.
        if (!fileArchive.exists())
            throw new FileNotFoundException("File not found");

        // Otherwise, open the ZIP file and get the right entry.
        ZipFile zfArchive = new ZipFile(fileArchive);
        ZipEntry entryToView = zfArchive.getEntry(strEntryName);

        // If the entry we want doesn't exist, throw an exception.
        if (entryToView == null)
            throw new ZipException("That file does not exist in the archive.");

        return zfArchive.getInputStream(entryToView);
    }
}

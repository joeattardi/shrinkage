import java.awt.*;
import javax.swing.*;
import java.util.zip.*;
import java.util.*;
import java.io.*;

/**
 * Rebuilds the ZIP file, removing a certain entry, in a separate thread.
 * This allows the GUI to update a progress bar to track the progress of the task.<br><br>
 *
 * Why is a separate thread needed for this task? If we run this task in the
 * default thread (the event-dispatching thread), all the updates to the progress bar
 * will be placed on the event-dispatching queue <i>after</i> the update operation.
 * In this case, the dialog would display no progress bar at all until the operation
 * was complete, after which the progress bar would shoot to 100% then the dialog would close.<br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu><br>
 */
public class RemoveThread extends Thread
{
    /** The archive file to remove from. */
    private File fileArchive;

    /** The name of the ZIP entry to remove. */
    private String strEntryToRemove;

    /** The UpdateProgress dialog to control. */
    private UpdateProgress up;

    /** Reference to the main frame. Used to fire an update of the table once the
     * task is complete. */
    private MainFrame frm;

    /**
     * Creates the RemoveThread.
     * @param frm The parent frame for the dialog to show.
     * @param fileArchive The archive file to remove from.
     * @param strEntryToRemove The name of the ZIP entry to remove.
     * @param lTotalSize The total size to write.
     */
    public RemoveThread(MainFrame frm, File fileArchive, String strEntryToRemove, long lTotalSize)
    {
        this.fileArchive = fileArchive;
        this.strEntryToRemove = strEntryToRemove;
        this.frm = frm;

        up = new UpdateProgress(frm, "Removing from archive", lTotalSize);
        up.show();
    }

    /**
     * Starts the thread.
     */
    public void run()
    {
        up.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frm.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try
        {
            boolean bInclude = true;
            byte[] buf = new byte[1024];
            int nLen = 0;
            ZipEntry currEntry;
            ZipEntry newEntry;
            InputStream isCurrEntry;

            File fileTempArchive = new File(fileArchive.getAbsolutePath() + ".tmp");

            // Throw an exception if the file or the archive file doesn't exist.
            if (!fileArchive.exists())
                throw new FileNotFoundException("File not found");

            // Open an output stream to the ZIP file, and create a ZIP entry
            // for this file.
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileTempArchive));

            // Get all old data from the ZIP file
            ZipFile currentZip = new ZipFile(fileArchive);
            Enumeration currentEntries = currentZip.entries();

            while (currentEntries.hasMoreElements())
            {
                // Reset the "include" flag. It will be set false if we've hit
                // one of the entries to remove.
                bInclude = true;

                currEntry = (ZipEntry)currentEntries.nextElement();
                isCurrEntry = currentZip.getInputStream(currEntry);

                // Check and see if this is the entry to remove.
                if (!currEntry.getName().equals(strEntryToRemove))
                {
                    newEntry = new ZipEntry(currEntry.getName());

                    zos.putNextEntry(newEntry);
                    while( (nLen = isCurrEntry.read(buf)) > 0)
                    {
                        zos.write(buf, 0, nLen);
                        up.updateProgress(nLen);
                    }
                    isCurrEntry.close();
                    zos.flush();
                    zos.closeEntry();
                }
            }
            currentZip.close();

            // Close the streams.
            //zos.closeEntry();
            zos.close();

            // NOTE: This is the most serious bug with the program.
            // Almost randomly, the File.delete() will not work. It simply returns
            // false, and doesn't delete the file. Many posts have appeared on the
            // Java Developer Forums about this issue, so it's apparently a common one.
            // Since this program is for demonstration only, and not an actualy release
            // product, there is a trick that fixes this problem *some* of the time.
            // Once in a while, an add or remove operation might fail, but this
            // "trick" decreases the likelihood.
            // The trick is to manually call the garbage collector. This should never
            // be done under normal circumstances - it is a temporary fix for this version!
            if (!fileArchive.delete())
            {
                // We would NEVER do this in a professional release product!
                // It could drastically reduce performance.
                System.gc();

                // Wait the the garbage collector to clean up.
                try{ Thread.sleep(1000); }
                catch (Exception e) {}
                fileArchive.delete();
            }
            fileTempArchive.renameTo(fileArchive);

            // Close the UpdateProgress dialog.
            up.dispose();

            // Update the ZIP entry table in the main frame.
            frm.rebuildTable();
        }
        catch(ZipException ze)
        {
            JOptionPane.showMessageDialog(up, "An error occurred while trying to remove one or more entries:\n" + ze.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            up.dispose();
            frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        catch(IOException ioe)
        {
            JOptionPane.showMessageDialog(up, "An error occurred while trying to open \"" + fileArchive.getAbsolutePath() + "\":\n" + ioe.getMessage(), "Error opening file", JOptionPane.ERROR_MESSAGE);
            up.dispose();
            frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        up.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}

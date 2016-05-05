import java.awt.*;
import javax.swing.*;
import java.util.zip.*;
import java.util.*;
import java.io.*;

/**
 *
 * Rebuilds the ZIP file, adding one or more entries, in a separate thread.<br>
 * This allows the GUI to update a progress bar to track the progress of the task.
 *
 * Why is a separate thread needed for this task? If we run this task in the
 * default thread (the event-dispatching thread), all the updates to the progress bar
 * will be placed on the event-dispatching queue <i>after</i> the update operation.
 * In this case, the dialog would display no progress bar at all until the operation
 * was complete, after which the progress bar would shoot to 100% then the dialog would close.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class AddThread extends Thread
{
    /** The archive file. */
    private File fileArchive;

    /** An array of File objects - the file(s) to add to the archive. */
    private File[] filesToAdd;

    /** The UpdateProgress dialog to control. */
    private UpdateProgress up;

    /** Reference to the main frame. Used to fire an update of the table once the
     * task is complete. */
    private MainFrame frm;

    /** Whether or not we have to update old files first (false if this is a new archive). */
    private boolean bUpdate;

    /**
     * Creates the AddThread.
     * @param frm The parent frame of the update progress dialog.
     * @param fileArchive The archive file to add to.
     * @param filesToAdd The files to add.
     * @param lTotalSize The total number of bytes to write.
     * @param bUpdate Whether or not we have to update old files first.
     */
    public AddThread(MainFrame frm, File fileArchive, File[] filesToAdd, long lTotalSize, boolean bUpdate)
    {
        this.fileArchive = fileArchive;
        this.filesToAdd = filesToAdd;
        this.frm = frm;
        this.bUpdate = bUpdate;

        up = new UpdateProgress(frm, "Adding file(s) to archive", lTotalSize);
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
            byte[] buf = new byte[1024];
            int nLen = 0;
            ZipEntry currEntry;
            ZipEntry newEntry;
            InputStream isCurrEntry;

            File fileToAdd;
            FileInputStream fisNewEntry;

            ZipFile currentZip;

            File fileTempArchive = new File(fileArchive.getAbsolutePath() + ".tmp");

            // Check all the files to add. If any of them don't exist, throw an exception.
            for (int n = 0; n < filesToAdd.length; n++)
                if(!filesToAdd[n].exists())
                    throw new FileNotFoundException("File to add not found - " + filesToAdd[n].getName());

            // Throw an exception if the archive file doesn't exist.
            //if (!fileArchive.exists())
                //throw new FileNotFoundException("Archive file not found");

            // Open an output stream to the ZIP file, and create a ZIP entry
            // for this file.
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileTempArchive));

            if (bUpdate)
            {
                // Try to read the entries from the existing ZIP file. I
                try
                {
                    // Get all old data from the ZIP file
                    currentZip = new ZipFile(fileArchive);
                    Enumeration currentEntries = currentZip.entries();

                    while (currentEntries.hasMoreElements())
                    {
                        currEntry = (ZipEntry)currentEntries.nextElement();
                        isCurrEntry = currentZip.getInputStream(currEntry);

                        newEntry = new ZipEntry(currEntry.getName());

                        zos.putNextEntry(newEntry);
                        while( (nLen = isCurrEntry.read(buf)) > 0)
                        {
                            zos.write(buf, 0, nLen);
                            up.updateProgress(nLen);
                        }

                        zos.flush();
                        zos.closeEntry();
                        isCurrEntry.close();
                    }

                     currentZip.close();
                }
                catch(Exception e) {e.printStackTrace();}
            }

            // Now go through the array of new files, and add each one to the
            // ZIP file.
            for (int n = 0; n < filesToAdd.length; n++)
            {
                fileToAdd = filesToAdd[n];
                fisNewEntry = new FileInputStream(fileToAdd);

                newEntry = new ZipEntry(fileToAdd.getName());
                zos.putNextEntry(newEntry);

                while( (nLen = fisNewEntry.read(buf)) > 0)
                {
                    zos.write(buf, 0, nLen);
                    up.updateProgress(nLen);
                }

                zos.closeEntry();
                fisNewEntry.close();
            }

            zos.close();
            up.dispose();

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

            frm.rebuildTable();
        }
        catch(ZipException ze)
        {
            JOptionPane.showMessageDialog(null, "An error occurred while trying to add file(s) to the archive:\n" + ze.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
            up.dispose();
            frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        catch(IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "An error occurred while trying to open \"" +
                                          fileArchive.getAbsolutePath() + "\".", "Error opening file",
                                          JOptionPane.ERROR_MESSAGE);
            up.dispose();
            frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        up.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        frm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    }
}

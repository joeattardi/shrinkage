import java.util.*;
import java.io.*;
import java.util.zip.*;
import javax.swing.*;

/** 
 * Extracts the ZIP in a separate thread in order to show status in a window
 * with a progress bar.
 *
 * For 91.461 GUI Programming II Semester Project
 * "Shrinkage" Archiving Program
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class ExtractThread extends Thread
{
    /** The ZIP archive to extract. */
    private File fileArchive;
    
    /** The base directory to extract to. */
    private String strBaseDir;
    
    /** The ExtractingWindow to control. */
    private ExtractingWindow ew;
    
    /** A flag to indicate state of pause. */
    private boolean bPause = false;

    /** A flag to indicate cancellation of the operation. */
    private boolean bCancel = false;

    /**
     * Creates a new ExtractThread.
     * @param frm The parent frame for the dialog to display.
     * @param fileArchive The archive file to extract.
     * @param strBaseDir The base directory.
     * @param lTotalSize The total number of bytes to write.
     */
    public ExtractThread(MainFrame frm, File fileArchive, String strBaseDir, long lTotalSize)
    {
        this.fileArchive = fileArchive;
        this.strBaseDir = strBaseDir;   
        
	// Create the ExtractingWindow.
	ew = new ExtractingWindow(frm, lTotalSize, this);
	ew.show();
    }
    
    /**
     * Starts the thread extracting the zip file.
     */
    public void run()
    {
        File fileBaseDir;           // the base dir to extract to
        File fileEntryOut;          // the output file of a ZIP entry
        FileOutputStream fosOut;    // output stream for writing a ZIP entry
        ZipEntry entryCurr;         // the current ZIP entry
        InputStream isEntry;        // input stream for the current ZIP entry
        ZipFile zfArchive;          // the archive file
        Enumeration enumEntries;    // enumeration to hold ZIP entries
      
        byte[] buf;                 // buffer to hold data - used to control rate
        int nFileOffset;            // offset used for reading from the input buffer
        int nNumRead;               // the number of bytes read on a read
        
        try
        {
            // Make buf a 1024-byte buffer. This allows us to increment the status
            // bar every 1024 bytes, making the progression of the status bar fairly
            // uniform.
            buf = new byte[1024];        
            
            // Throw an exception if the file doesn't exist.
            if(!fileArchive.exists())
                throw new FileNotFoundException("File not found");
            
            // If the path to write to doesn't exist, create it.
            fileBaseDir = new File(strBaseDir);
            if(!fileBaseDir.exists())
                fileBaseDir.createNewFile();
                    
            // Open the ZIP file.
            zfArchive = new ZipFile(fileArchive);        
                           
            // Step through the ZIP entries, and write each one to disk from the base
            // directory.
            enumEntries = zfArchive.entries();                       
            while (enumEntries.hasMoreElements() && !bCancel)
            { 
		// Wait here if there's a pause state.
		while (bPause);
		
                entryCurr = (ZipEntry)enumEntries.nextElement();           
                isEntry = zfArchive.getInputStream(entryCurr);                        
                
                fileEntryOut = new File(strBaseDir, entryCurr.getName());                 
                ew.setFile(fileEntryOut);                
                
                // Create the directory that contains this file if it doesn't already exist.
                if (!fileEntryOut.getParentFile().exists())                           
                    fileEntryOut.getParentFile().mkdirs();            

		// If this is a directory entry, create the directory.
		// Note: isDirectory() only returns true if the ZIP entry for the
		// directory name ends with a /. It doesn't always. But, a directory
		// entry can have a size of 0 too, so we'll check for that as well.
		if (entryCurr.isDirectory() || entryCurr.getSize() == 0)		
		    fileEntryOut.mkdirs();
		else
		{

		    fileEntryOut.createNewFile();                        
		    fosOut = new FileOutputStream(fileEntryOut);
                
		    nFileOffset = 0;
                
		    while( (nNumRead = isEntry.read(buf, nFileOffset, 1024)) >= 0)
		    {                                           
			fosOut.write(buf);                   
			ew.updateProgress(nNumRead);
		    }
                
		    // Close file streams.
		    isEntry.close();
		    fosOut.close();
		}			      
            }  
            
            ew.dispose();
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "An error occurred while extracting: " + ioe.getMessage(),
                                          "Extract error", JOptionPane.ERROR_MESSAGE);    
	    ioe.printStackTrace();
        }
    }

    /**
     * Sets or unsets the pause state.
     * @param bPause The state to set the pause flag to.
     */
    public void setPause(boolean bPause)
    {
	this.bPause = bPause;
    }

    /**
     * Cancels the archive operation.
     * This sets the cancel flag to true, and the operation will abort
     * after the current file is done.
     */
    public void cancel()
    {
	bCancel = true;
    }
}

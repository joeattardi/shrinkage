import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/**
 * Shows the status of an extract operation with a progress bar.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class ExtractingWindow extends JDialog implements ActionListener
{
    /** The content pane. */
    private Container contentPane;

    /** JLabel for the current filename. */
    private JLabel lblCurrentFile;

    /** The current filename. */
    private String strCurrentFilename;

    /** The progress bar. */
    private JProgressBar prgBar;

    /** The "Cancel" button. */
    private JButton btCancel;

    /** The bytes copied so far. */
    private int nBytesSoFar;

    /** The parent window. */
    private JFrame frmParent;

    /** A reference to the extract thread (in case we need to pause or cancel). */
    private ExtractThread et;

    /**
     * Creates the ExtractingWindow and initializes its GUI.
     *
     * @param frmParent The parent JFrame.
     * @param lTotalSize The total file size.
     * @param et The ExtractThread.
     */
    public ExtractingWindow(JFrame frmParent, long lTotalSize, ExtractThread et)
    {
        super(frmParent);

        this.et = et;
        setTitle("Extracting files, please wait...");
        setSize(300,120);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(frmParent);

        contentPane = getContentPane();
        contentPane.setLayout(null);

        lblCurrentFile = new JLabel("Extracting");
        lblCurrentFile.setBounds(10, 10, 200, 20);
        contentPane.add(lblCurrentFile);

        prgBar = new JProgressBar(0, (int)lTotalSize);
        prgBar.setBounds(10, 30, 250, 20);
        prgBar.setStringPainted(true);
        contentPane.add(prgBar);

        btCancel = new JButton("Cancel");
        btCancel.setActionCommand("cancel");
        btCancel.addActionListener(this);
        btCancel.setBounds(85, 60, 100, 20);
        contentPane.add(btCancel);

        nBytesSoFar = 0;
        this.et = et;
    }

    /**
     * Handles an ActionEvent (when the button is clicked).
     * @param ae The ActionEvent object supplied by the system.
     */
    public void actionPerformed(ActionEvent ae)
    {
        String strCmd = ae.getActionCommand();

        if (strCmd.equals("cancel"))
        {
            et.setPause(true);
            int nResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel the operation?",
                                                        "Confirm Cancel", JOptionPane.YES_NO_OPTION);

            if (nResult == 1)
                et.setPause(false);
            else
            {
                et.cancel();
                dispose();
            }
        }
    }

    /**
     * Updates the filename, ensuring it's done in the event-dispatching thread.
     * @param fileCurrent The current File being extracted..
     */
    public void setFile(File fileCurrent)
    {
        strCurrentFilename = fileCurrent.getName();

        // Use the invokeLater method of SwingUtilities to make sure that
        // this update is performed on the event-dispatching thread.
        SwingUtilities.invokeLater(
            new Runnable()
            {
                public void run()
                {
                    lblCurrentFile.setText(strCurrentFilename);
                }
            });
    }

    /**
     * Updates the progress bar.
     * @param nBytes The number of bytes that have been written.
     */
    public void updateProgress(int nBytes)
    {
        nBytesSoFar += nBytes;

        // Ensure that the progress bar is updated on the event-dispatching thread.
        // We do this by putting the code to execute in a Runnable object and passing that
        // to SwingUtilities.invokeLater().
        SwingUtilities.invokeLater(
            new Runnable()
            {
                public void run()
                {
                    prgBar.setValue(nBytesSoFar);
                    int nPercent = (int)(prgBar.getPercentComplete() * 100);
                    prgBar.setString(nPercent + "%");
                }
            });
    }

    /**
     * Sets the thread to be monitored.
     * This would normally be done in the constructor, but in this case we can't.
     * The ExtractThread expects the ExtractWindow as an argument to its constructor,
     * so it must already be created before we create this. Therefore, when the ExtractThread
     * is created, the reference to the ExtractingWindow is still null. That's why we have to set
     * this later on.
     * @param et The ExtractThread to monitor.
     */
    public void setExtractThread(ExtractThread et)
    {
        this.et = et;
    }
}

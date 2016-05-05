import java.awt.*;
import javax.swing.*;

/**
 * Shows a progress bar while updating the archive file.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br><br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class UpdateProgress extends JDialog
{
    /** The content pane. */
    private Container contentPane;

    /** Title JLabel. */
    private JLabel lblTitle;

    /** The progress bar. */
    private JProgressBar prgBar;

    /** The number of bytes written so far. */
    private int nBytesSoFar = 0;

    /**
     * Creates the UpdateProgress dialog.
     * @param frm The parent frame.
     * @param strTitle The title to use.
     * @param lTotalSize The total number of bytes to be written.
     */
    public UpdateProgress(MainFrame frm, String strTitle, long lTotalSize)
    {
        super(frm);
        setSize(310,100);
        setTitle(strTitle);
        setLocationRelativeTo(frm);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        contentPane = getContentPane();
        contentPane.setLayout(null);

        lblTitle = new JLabel("Please wait while the archive is updated.");
        lblTitle.setBounds(10, 5, 300, 20);
        contentPane.add(lblTitle);

        prgBar = new JProgressBar(0, (int)lTotalSize);
        prgBar.setStringPainted(true);
        prgBar.setBounds(10, 30, 250, 20);
        contentPane.add(prgBar);
    }

    /**
     * Updates the progress bar.
     * @param nBytesAdded The number of bytes added.
     */
    public void updateProgress(int nBytesAdded)
    {
        nBytesSoFar += nBytesAdded;

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
}

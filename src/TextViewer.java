import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.*;

/*
 * TextViewer.java
 *
 * Views the contents of a text file inside
 * an archive.
 *
 * For 91.461 GUI Programming II Semester Project
 * "Shrinkage" Archiving Program
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class TextViewer extends JFrame implements ActionListener
{
    /** Reference to the main frame. */
    private MainFrame frm;

    /** The content pane. */
    private Container contentPane;

    /** Input stream from the ZIP entry. */
    private InputStream isData;

    /** Text area to display the text file. */
    private JTextArea txtFile;

    /** Scroll pane to display the text area in. */
    private JScrollPane scrFile;

    /** Toolbar with buttons to manipulate the file. */
    private JToolBar tbTools;

    /** Toolbar button to copy to the clipboard. */
    private JButton btCopy;

    /** Toolbar button to close the viewer. */
    private JButton btClose;

    /** Toolbar button to search the file. */
    private JButton btFind;

    /** A status bar at the bottom of the screen. */
    private JLabel lblStatus;

    /**
     * Creates the TextViewer window.
     * @param frm Reference to the main window.
     * @param isData Input stream from the ZIP entry.
     * @param strFilename The name of the filename we're viewing.
     */
    public TextViewer(MainFrame frm, InputStream isData, String strFilename)
    {
      this.frm = frm;
      this.isData = isData;

      setTitle( "Text Viewer (" + strFilename + ")");
      setSize(600,400);
      setLocationRelativeTo(frm);

      contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

      txtFile = new JTextArea(80, 80);
      txtFile.setEditable(false);
      scrFile = new JScrollPane(txtFile);
      contentPane.add(scrFile, BorderLayout.CENTER);

      createToolBar();

      try
      {
          int nByteCount = isData.available();
          int nLineCount = loadTextFile();
          txtFile.setSelectionStart(0);
          txtFile.setSelectionEnd(0);

          lblStatus = new JLabel(strFilename + " - " + nLineCount + " lines, " +
                                 nByteCount + " bytes");
          contentPane.add(lblStatus, BorderLayout.SOUTH);
      }
      catch (IOException ioe)
      {
          JOptionPane.showMessageDialog(this, "An error occurred while trying to open the text file (" + ioe.getMessage() + ")",
                                        "File error", JOptionPane.ERROR_MESSAGE);
          dispose();
      }
    }

    /**
     * Builds the toolbar.
     */
     private void createToolBar()
     {
        tbTools = new JToolBar("Tools");

        btCopy = new JButton("Copy", new ImageIcon("images/Copy16.gif"));
        btCopy.setToolTipText("Copy");
        btCopy.setActionCommand("copy");
        btCopy.addActionListener(this);
        tbTools.add(btCopy);

        btFind = new JButton("Find", new ImageIcon("images/Find16.gif"));
        btFind.setToolTipText("Find");
        btFind.setActionCommand("find");
        btFind.addActionListener(this);
        tbTools.add(btFind);

        btClose = new JButton("Close", new ImageIcon("images/Stop16.gif"));
        btClose.setToolTipText("Close");
        btClose.setActionCommand("close");
        btClose.addActionListener(this);
        tbTools.add(btClose);

        contentPane.add(tbTools, BorderLayout.NORTH);
     }

    /**
     * Reads all character data from the buffered reader.
     * @return The number of lines read.
     * @throws IOException if an error occurs while reading.
     */
    private int loadTextFile() throws IOException
    {
        int nNumLines = 0;
        String strCurrentLine = "";
        BufferedReader brLineReader = new BufferedReader(new InputStreamReader(isData));

        while ((strCurrentLine = brLineReader.readLine()) != null)
        {
            nNumLines++;
            txtFile.append(strCurrentLine + "\n");
        }

        brLineReader.close();
        isData.close();

        return nNumLines;
    }

    /**
     * Handles button clicks on this page.
     * This method is called by the event system.
     * @param ae The ActionEvent supplied by the system.
     */
    public void actionPerformed(ActionEvent ae)
    {
        String strCmd = ae.getActionCommand();

        if (strCmd.equals("close"))
            dispose();
        else if (strCmd.equals("copy"))
            doCopy();
        else if (strCmd.equals("find"))
            new FindDialog(this).show();
    }

    /**
     * Copies the currently selected text to the system clipboard.
     */
    private void doCopy()
    {
        Clipboard sysClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(txtFile.getSelectedText());
        sysClipboard.setContents(selection, selection);
    }

    /**
     * Gets the current position of the cursor (or the end of the current selection).
     * @return The position in the file of the cursor.
     */
    public int getCursorPosition()
    {
        return txtFile.getSelectionEnd();
    }

    /**
     * Gets the contents of the text area.
     * @return The text in the text area.
     */
    public String getText()
    {
        return txtFile.getText();
    }

    /**
     * Sets the selection in the text area.
     * @param nSelectionStart
     * @param nSelectionEnd
     */
    public void setSelection(int nSelectionStart, int nSelectionEnd)
    {
        txtFile.setSelectionStart(nSelectionStart);
        txtFile.setSelectionEnd(nSelectionEnd);
        txtFile.requestFocus();
    }
}

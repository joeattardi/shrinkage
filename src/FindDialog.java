import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * FindDialog.java
 *
 * Dialog box that pops up when the user clicks the 'Find'
 * button in the Text Viewer, enabling them to search for
 * text in the text file.
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu>
 */
public class FindDialog extends JDialog implements ActionListener
{
    /** The content pane. */
    private Container contentPane;

    /** Reference to the text viewer window. */
    private TextViewer txtViewer;

    /** Label for the search terms text field. */
    private JLabel lblSearch;

    /** Text field to enter the search terms. */
    private JTextField txtSearch;

    /** The "Find" button. */
    private JButton btFind;

    /** The "Close" button. */
    private JButton btClose;

    /** Check box for selecting case sensitivity */
    private JCheckBox cbCase;

    /** Check box for selecting whole word matching */
    private JCheckBox cbWholeWord;

    /**
     * Creates the FindDialog.
     * @param txtViewer Reference to the Text Viewer that spawned this dialog.
     */
    public FindDialog(TextViewer txtViewer)
    {
        super(txtViewer, "Find");
        this.txtViewer = txtViewer;

        setSize(250, 120);
        setLocationRelativeTo(txtViewer);

        contentPane = getContentPane();
        contentPane.setLayout(null);

        lblSearch = new JLabel("Find:");
        lblSearch.setBounds(5, 5, 40, 20);
        contentPane.add(lblSearch);

        txtSearch = new JTextField(30);
        txtSearch.setBounds(45, 5, 160, 20);
        contentPane.add(txtSearch);

        cbCase = new JCheckBox("Case sensitive");
        cbCase.setMnemonic(KeyEvent.VK_C);
        cbCase.setBounds(35, 25, 150, 20);
        contentPane.add(cbCase);

        cbWholeWord = new JCheckBox("Match whole word only");
        cbWholeWord.setMnemonic(KeyEvent.VK_W);
        cbWholeWord.setBounds(35, 45, 170, 20);
        contentPane.add(cbWholeWord);

        btFind = new JButton("Find Next");
        btFind.setActionCommand("find");
        btFind.addActionListener(this);
        btFind.setBounds(5, 65, 100, 20);
        contentPane.add(btFind);
        getRootPane().setDefaultButton(btFind);

        btClose = new JButton("Close");
        btClose.setActionCommand("close");
        btClose.addActionListener(this);
        btClose.setBounds(115, 65, 100, 20);
        contentPane.add(btClose);
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
        else if (strCmd.equals("find"))
            doFind();
    }

    /**
     * Finds the next occurrence of the search term in the text file.
     * The search begins at the cursor's current position.
     */
    private void doFind()
    {
        int nSearchTermPos = 0;
        int nStart = txtViewer.getCursorPosition();
        String strSearchTerm = txtSearch.getText();
        if (strSearchTerm.equals(""))
        {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", "No search term entered",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If the user wants to match whole words only, add whitespace before and after
        if (cbWholeWord.isSelected())
            strSearchTerm = " " + strSearchTerm + " ";

        // Check the case setting. If the user wants to match case, just call indexOf as-is.
        if (cbCase.isSelected())
            nSearchTermPos = txtViewer.getText().indexOf(strSearchTerm, nStart);
        // However, if the user wants case-insensitivity, convert both the search string
        // and the text to search to lowercase for case-insensitivity.
        else
            nSearchTermPos = txtViewer.getText().toLowerCase().indexOf(strSearchTerm.toLowerCase(), nStart);

        if (nSearchTermPos < 0)
            JOptionPane.showMessageDialog(this, "No text matched your search.",
                                          "Found nothing", JOptionPane.INFORMATION_MESSAGE);
        else
        {
            // If we were selecting the whole word, remove the extra space now when we select it.
            if (cbWholeWord.isSelected())
                txtViewer.setSelection(nSearchTermPos + 1, (nSearchTermPos + strSearchTerm.length() - 1));
            else
                txtViewer.setSelection(nSearchTermPos, (nSearchTermPos + strSearchTerm.length()));
        }
    }
}



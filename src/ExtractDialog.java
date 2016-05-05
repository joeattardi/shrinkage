import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
/**
 * A dialog that will appear for the user to specify where to extract an archive.
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joe Attardi <jattardi@cs.uml.edu>
 */

public class ExtractDialog extends JDialog implements ActionListener
{
    /** Reference to the configuration manager. */
    private ConfigManager configMgr;

    /** OK button - starts the operation */
    private JButton btOK;

    /** The content pane for this dialog. */
    private Container contentPane;

    /** Cancel button - aborts the operation */
    private JButton btCancel;

    /** Browse button - to choose a directory */
    private JButton btBrowse;

    /** Text field for directory selection */
    private JTextField txtDirectory;

    /** Label for the directory field. */
    private JLabel lblDirectory;

    /** Reference to the parent MainFrame. */
    private MainFrame parent;

    /**
     * Creates a new instance of ExtractDialog.
     * @param parent The parent to this dialog.
     */
    public ExtractDialog(MainFrame parent, ConfigManager configMgr)
    {
        super(parent, true);
        this.parent = parent;
        this.configMgr = configMgr;
        setTitle("Extract Archive Contents");
        setSize(500,100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane = getContentPane();
        contentPane.setLayout(null);

        lblDirectory = new JLabel("Directory to extract to:");
        lblDirectory.setBounds(10, 10, 160, 20);
        contentPane.add(lblDirectory);

        txtDirectory = new JTextField(20);
        txtDirectory.setBounds(170, 10, 200, 20);
        contentPane.add(txtDirectory);

        btBrowse = new JButton("Browse...");
        btBrowse.setMnemonic(KeyEvent.VK_B);
        btBrowse.setBounds(370, 10, 100, 20);
        btBrowse.setActionCommand("browse");
        btBrowse.addActionListener(this);
        contentPane.add(btBrowse);

        btOK = new JButton("Extract");
        btOK.setMnemonic(KeyEvent.VK_X);
        btOK.setBounds(150, 40, 100, 20);
        btOK.setActionCommand("extract-files");
        btOK.addActionListener(this);
        getRootPane().setDefaultButton(btOK);
        contentPane.add(btOK);

        btCancel = new JButton("Cancel");
        btCancel.setMnemonic(KeyEvent.VK_C);
        btCancel.setBounds(250, 40, 100, 20);
        btCancel.setToolTipText("Cancel the operation.");
        btCancel.setActionCommand("cancel");
        btCancel.addActionListener(this);
        contentPane.add(btCancel);

    }

    /**
     * Called when an event occurs.
     * @param ae The ActionEvent object supplied by the system.
     */
    public void actionPerformed(ActionEvent ae)
    {
        String strActionCmd = ae.getActionCommand();

        if (strActionCmd.equals("extract-files"))
        {
            if(txtDirectory.getText().equals(""))
            {
                JOptionPane.showMessageDialog(this, "Please select a directory to extract the archive to.",
                                              "No directory selected", JOptionPane.INFORMATION_MESSAGE);
                txtDirectory.requestFocus();
            }
            else if(!new File(txtDirectory.getText()).exists())
            {
                int nOption = JOptionPane.showConfirmDialog(this, "\"" + txtDirectory.getText() + "\" does not exist.\n" +
                                                            "Do you want to create the new directory?",
                                                            "Directory not found",
                                                            JOptionPane.YES_NO_OPTION);
                if(nOption == 0)
                {
                    new File(txtDirectory.getText()).mkdirs();

                    parent.doExtract(txtDirectory.getText());
                    dispose();
                }
            }
            else
            {
                parent.doExtract(txtDirectory.getText());
                dispose();
            }
        }

        else if (strActionCmd.equals("browse"))
        {
            JFileChooser jfChooser = new JFileChooser(configMgr.getProperty(ConfigManager.EXTRACT_DIR));
            jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfChooser.setDialogTitle("Select Directory");

            int nReturnValue = jfChooser.showOpenDialog(this);
            if (nReturnValue == JFileChooser.APPROVE_OPTION)
            {
                // Update the text box with the chosen directory.
                txtDirectory.setText(jfChooser.getSelectedFile().getAbsolutePath());
            }
        }

        else if (strActionCmd.equals("cancel"))
            dispose();
    }


}

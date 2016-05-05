import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
 * Dialog for setting program options for Shrinkage. The options are kept in a file
 * named <code>shrinkage.properties</code>.<br>
 *
 * The Options dialog interacts with the ConfigManager class to get and set properties
 * from the configuration file. The controls in the dialog are populated from the data
 * stored in the configuration file.<br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu> <br>
 */
public class OptionsDialog extends JDialog implements ActionListener
{
    /** The content pane. */
    private Container contentPane;

    /** Reference to the main frame. */
    private MainFrame frm;

    /** Reference to the config manager. */
    private ConfigManager configMgr;

    /** JLabel for the default open location. */
    private JLabel lblDefaultOpen;

    /** JTextField for the default open location. */
    private JTextField txtDefaultOpen;

    /** A "Browse" button for the default open location. */
    private JButton btBrowseDefaultOpen;

    /** JLabel for the default extract location. */
    private JLabel lblDefaultExtract;

    /** JTextField for the default extract location. */
    private JTextField txtDefaultExtract;

    /** A "Browse" button for the default extract location. */
    private JButton btBrowseDefaultExtract;

    /** JLabel for the default new location. */
    private JLabel lblDefaultNew;

    /** JTextField for the default new location. */
    private JTextField txtDefaultNew;

    /** A "Browse" button for the default new location. */
    private JButton btBrowseDefaultNew;

    /** The "OK" button (saves changes). */
    private JButton btOK;

    /** The "Cancel" button. (throws out changes) */
    private JButton btCancel;

    /**
     * Creates the OptionsDialog.
     * @param frm Reference to the main frame.
     * @param configMgr Reference to the configuration manager.
     */
    public OptionsDialog(MainFrame frm, ConfigManager configMgr)
    {
        super(frm, true);

        this.frm = frm;
        this.configMgr = configMgr;

        setTitle("Shrinkage Options");
        setSize(550,150);
        setLocationRelativeTo(frm);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        contentPane = getContentPane();
        contentPane.setLayout(null);

        lblDefaultOpen = new JLabel("Default location for opening files:");
        lblDefaultOpen.setBounds(0, 10, 220, 20);
        contentPane.add(lblDefaultOpen);

        txtDefaultOpen = new JTextField(20);
        txtDefaultOpen.setBounds(220, 10, 200, 20);
        txtDefaultOpen.setText(configMgr.getProperty(ConfigManager.OPEN_DIR));
        contentPane.add(txtDefaultOpen);

        btBrowseDefaultOpen = new JButton("Browse...");
        btBrowseDefaultOpen.setBounds(420, 10, 100, 20);
        btBrowseDefaultOpen.setActionCommand("browse-open");
        btBrowseDefaultOpen.addActionListener(this);
        contentPane.add(btBrowseDefaultOpen);

        lblDefaultExtract = new JLabel("Default target for extracting files:");
        lblDefaultExtract.setBounds(0, 30, 220, 20);
        contentPane.add(lblDefaultExtract);

        txtDefaultExtract = new JTextField(20);
        txtDefaultExtract.setBounds(220, 30, 200, 20);
        txtDefaultExtract.setText(configMgr.getProperty(ConfigManager.EXTRACT_DIR));
        contentPane.add(txtDefaultExtract);

        btBrowseDefaultExtract = new JButton("Browse...");
        btBrowseDefaultExtract.setBounds(420, 30, 100, 20);
        btBrowseDefaultExtract.setActionCommand("browse-extract");
        btBrowseDefaultExtract.addActionListener(this);
        contentPane.add(btBrowseDefaultExtract);

        lblDefaultNew = new JLabel("Default location for new files:");
        lblDefaultNew.setBounds(0, 50, 220, 20);
        contentPane.add(lblDefaultNew);

        txtDefaultNew = new JTextField(20);
        txtDefaultNew.setBounds(220, 50, 200, 20);
        txtDefaultNew.setText(configMgr.getProperty(ConfigManager.NEW_DIR));
        contentPane.add(txtDefaultNew);

        btBrowseDefaultNew = new JButton("Browse...");
        btBrowseDefaultNew.setBounds(420, 50, 100, 20);
        btBrowseDefaultNew.setActionCommand("browse-new");
        btBrowseDefaultNew.addActionListener(this);
        contentPane.add(btBrowseDefaultNew);

        btOK = new JButton("OK");
        btOK.setBounds(100, 100, 100, 20);
        getRootPane().setDefaultButton(btOK);
        btOK.setActionCommand("ok");
        btOK.addActionListener(this);
        contentPane.add(btOK);

        btCancel = new JButton("Cancel");
        btCancel.setBounds(300, 100, 100, 20);
        btCancel.setActionCommand("cancel");
        btCancel.addActionListener(this);
        contentPane.add(btCancel);
    }

    /**
     * Handles an event (in this case, a button click).
     * @param ae The ActionEvent object supplied by the system.
     */
    public void actionPerformed(ActionEvent ae)
    {
        String strCmd = ae.getActionCommand();

        if (strCmd.equals("ok"))
        {
            syncConfig();
            dispose();
        }
        else if (strCmd.equals("cancel"))
            dispose();
        else if (strCmd.equals("browse-open"))
            browseProperty(txtDefaultOpen);
        else if (strCmd.equals("browse-extract"))
            browseProperty(txtDefaultExtract);
        else if (strCmd.equals("browse-new"))
            browseProperty(txtDefaultNew);
    }

    /**
     * Synchronizes the current configuration back to the
     * configuration file.
     */
    private void syncConfig()
    {
        configMgr.setProperty(ConfigManager.OPEN_DIR, txtDefaultOpen.getText());
        configMgr.setProperty(ConfigManager.EXTRACT_DIR, txtDefaultExtract.getText());
        configMgr.setProperty(ConfigManager.NEW_DIR, txtDefaultNew.getText());
    }

    /**
     * Sets a given property by bringing up a file chooser and allowing the user to choose a file.
     * @param txtFieldToSet The text field to set.
     */
    private void browseProperty(JTextField txtFieldToSet)
    {
        JFileChooser jfChooser = new JFileChooser();

        jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfChooser.setDialogTitle("Select Directory");

        int nReturnValue = jfChooser.showOpenDialog(this);
        if (nReturnValue == JFileChooser.APPROVE_OPTION)
        {
            // Update the text box with the chosen directory.
            txtFieldToSet.setText(jfChooser.getSelectedFile().getAbsolutePath());
        }
    }
}

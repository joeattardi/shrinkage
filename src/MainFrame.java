import javax.help.*;
import javax.swing.border.BevelBorder;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;

/**
 * Main window of the Shrinkage program. Displays the contents of the current
 * zip file, and has menus and a toolbar to manipulate the ZIP file.<br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu><br>
 */
public class MainFrame extends JFrame
{
    /** The icon image to use for the application (displayed in the taskbar) */
    private Image imgApplicationIcon;

    /** A reference to the currently open ZIP file. */
    private File fileCurrentArchive;

    /** Handler for ZIP files. */
    private ZipHandler handler;

    /** The event listener for this frame. */
    private MainWindowListener mwListener;

    /** JPanel to use as the content pane. */
    private JPanel contentPane;

    /** JLabel that acts as the status bar. */
    private JLabel lblStatus;

    /** The table containing the file names. */
    private JTable tblZipTable;

    /** Table model for the ZIP table. */
    private ShrinkageTableModel stModel;

    /** A scroll pane to hold the table. */
    private JScrollPane scrZipTable;

    /** The "File" menu. */
    private JMenu menuFile;

    /** Menu item to create a new archive */
    private JMenuItem mitemNew;

    /** Toolbar button to create a new archive */
    private JButton btNew;

    /** Menu item to open an existing archive. */
    private JMenuItem mitemOpen;

    /** Toolbar button to open an existing archive. */
    private JButton btOpen;

    /** Menu item to close an open archive. */
    private JMenuItem mitemClose;

    /** Toolbar button to close an open archive. */
    private JButton btClose;

    /** Menu item to quit the program. */
    private JMenuItem mitemQuit;

    /** Toolbar button to quit the program. */
    private JButton btQuit;

    /** The "Actions" menu. */
    private JMenu menuActions;

    /** The "Tools" menu. */
    private JMenu menuTools;

    /** Menu item to bring up the options dialog. */
    private JMenuItem mitemOptions;

    /** Toolbar button to bring up the options dialog. */
    private JButton btOptions;

    /** Menu item to extract an archive. */
    private JMenuItem mitemExtract;

    /** Toolbar button to extract an archive. */
    private JButton btExtract;

    /** Menu item to add a file to an archive. */
    private JMenuItem mitemAdd;

    /** Toolbar button to add a file to an archive. */
    private JButton btAdd;

    /** Menu item to remove a file from an archive. */
    private JMenuItem mitemRemove;

    /** Toolbar button to remove a file from an archive. */
    private JButton btRemove;

    /** Menu item to view a file in an archive. */
    private JMenuItem mitemView;

    /** Toolbar button to view a file in the archive. */
    private JButton btView;

    /** Checkbox menu item to enable/disable the toolbar. */
    private JCheckBoxMenuItem mitemToolbar;

    /** The "Help" menu. */
    private JMenu menuHelp;

    /** The "Shrinkage Help" menu item. */
    private JMenuItem mitemHelp;

    /** Toolbar button for the help. */
    private JButton btHelp;

    /** The toolbar. */
    private JToolBar tbTools;

    /** A configuration manager to store settings. */
    private ConfigManager configMgr;

    /** The URL of the HelpSet file. */
    private java.net.URL hsURL;

    /** The actual HelpSet. */
    private HelpSet hs;

    /** The HelpBroker that opens the help system. */
    private HelpBroker hb;

    /**
     * Creates the main frame, and adds all components to it.
     *
     * For clarity, large operations like creating menus are broken
     * down into separate methods called by the constructor.
     */
    public MainFrame()
    {
        imgApplicationIcon = Toolkit.getDefaultToolkit().getImage("images/shrinkage-icon.gif");
        setIconImage(imgApplicationIcon);

        // Set the Windows look and feel. If an error occurs, don't do anything
        // and it will default to the Metal look and feel.
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (Exception e) {}

        setTitle(Shrinkage.TITLE + " " + Shrinkage.VERSION + " - No archive loaded");

        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // Disable automatic closing of the window when the "X" is clicked,
        // so the window listener class can handle it instead.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        handler = new ZipHandler();
        configMgr = new ConfigManager();
        mwListener = new MainWindowListener(this, configMgr);
        addWindowListener(mwListener);

        // Initialize the help resources.
        try
        {
            hsURL = HelpSet.findHelpSet(null, "help/helpset.hs");
            hs = new HelpSet(null, hsURL);
            hb = hs.createHelpBroker();
            hb.setSize(new Dimension(800, 500));
        }
        catch (Exception e)
        {
            showErrorDialog("An error occurred while initializing the help files.", "Help error");
        }

        createMenuBar();
        createToolBar();

        lblStatus = new JLabel("No archive loaded");
        lblStatus.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        contentPane.add(lblStatus, BorderLayout.SOUTH);

        stModel = new ShrinkageTableModel();
        tblZipTable = new JTable(stModel);
        tblZipTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblZipTable.setEnabled(false);
        tblZipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblZipTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblZipTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblZipTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblZipTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblZipTable.getColumnModel().getColumn(4).setPreferredWidth(250);

        scrZipTable = new JScrollPane(tblZipTable);
        scrZipTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                                                               "Archive Contents"));

        contentPane.add(scrZipTable, BorderLayout.CENTER);

        setSize(710,480);
        show();
    }

    /**
     * Creates the menu system.
     */
    private void createMenuBar()
    {
        JMenuBar menuBar;

        menuBar = new JMenuBar();

        menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        menuActions = new JMenu("Actions");
        menuActions.setMnemonic(KeyEvent.VK_A);

        menuTools = new JMenu("Tools");
        menuTools.setMnemonic(KeyEvent.VK_T);

        menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);

        mitemNew = new JMenuItem("New Archive...", new ImageIcon("images/New16.gif"));
        mitemNew.setToolTipText("Create a new, empty ZIP archive.");
        mitemNew.setMnemonic(KeyEvent.VK_N);
        mitemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        mitemNew.setActionCommand("new");
        mitemNew.addActionListener(mwListener);
        menuFile.add(mitemNew);

        mitemOpen = new JMenuItem("Open Archive...", new ImageIcon("images/Open16.gif"));
        mitemOpen.setToolTipText("Open an existing ZIP archive.");
        mitemOpen.setMnemonic(KeyEvent.VK_O);
        mitemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        mitemOpen.setActionCommand("open");
        mitemOpen.addActionListener(mwListener);
        menuFile.add(mitemOpen);

        mitemClose = new JMenuItem("Close Archive", new ImageIcon("images/Import16.gif"));
        mitemClose.setToolTipText("Close the currently opened ZIP archive.");
        mitemClose.setMnemonic(KeyEvent.VK_C);
        mitemClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        mitemClose.setActionCommand("close");
        mitemClose.addActionListener(mwListener);
        mitemClose.setEnabled(false);
        menuFile.add(mitemClose);

        menuFile.addSeparator();

        mitemQuit = new JMenuItem("Quit", new ImageIcon("images/Stop16.gif"));
        mitemQuit.setToolTipText("Quit the program.");
        mitemQuit.setMnemonic(KeyEvent.VK_Q);
        mitemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        mitemQuit.setActionCommand("quit");
        mitemQuit.addActionListener(mwListener);
        menuFile.add(mitemQuit);

        mitemAdd = new JMenuItem("Add file(s) to archive...", new ImageIcon("images/Add16.gif"));
        mitemAdd.setToolTipText("Add a file to the currently opened archive.");
        mitemAdd.setMnemonic(KeyEvent.VK_A);
        mitemAdd.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        mitemAdd.setActionCommand("add");
        mitemAdd.addActionListener(mwListener);
        mitemAdd.setEnabled(false);
        menuActions.add(mitemAdd);

        mitemRemove = new JMenuItem("Remove selected file(s)", new ImageIcon("images/Delete16.gif"));
        mitemRemove.setToolTipText("Remove the selected file from the currently opened archive.");
        mitemRemove.setMnemonic(KeyEvent.VK_R);
        mitemRemove.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        mitemRemove.setActionCommand("remove");
        mitemRemove.addActionListener(mwListener);
        mitemRemove.setEnabled(false);
        menuActions.add(mitemRemove);

        mitemExtract = new JMenuItem("Extract archive...", new ImageIcon("images/Export16.gif"));
        mitemExtract.setToolTipText("Extract the contents of the currently opened archive.");
        mitemExtract.setMnemonic(KeyEvent.VK_X);
        mitemExtract.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        mitemExtract.setActionCommand("extract");
        mitemExtract.addActionListener(mwListener);
        mitemExtract.setEnabled(false);
        menuActions.add(mitemExtract);

        mitemView = new JMenuItem("View selected file", new ImageIcon("images/View16.gif"));
        mitemView.setToolTipText("View the currently selected text file.");
        mitemView.setMnemonic(KeyEvent.VK_V);
        mitemView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        mitemView.setActionCommand("view");
        mitemView.addActionListener(mwListener);
        mitemView.setEnabled(false);
        menuActions.add(mitemView);

        mitemOptions = new JMenuItem("Options...", new ImageIcon("images/Preferences16.gif"));
        mitemOptions.setToolTipText("Set the program options.");
        mitemOptions.setMnemonic(KeyEvent.VK_P);
        mitemOptions.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        mitemOptions.setActionCommand("options");
        mitemOptions.addActionListener(mwListener);
        menuTools.add(mitemOptions);

        mitemToolbar = new JCheckBoxMenuItem("Show Toolbar");
        mitemToolbar.setToolTipText("Toggle the display of the toolbar.");
        mitemToolbar.setMnemonic(KeyEvent.VK_T);
        mitemToolbar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        mitemToolbar.setActionCommand("toolbar");
        mitemToolbar.addActionListener(mwListener);
        mitemToolbar.setSelected(configMgr.getProperty(ConfigManager.TOOLBAR).equals("enabled"));

        menuTools.add(mitemToolbar);

        mitemHelp = new JMenuItem("Shrinkage Help", new ImageIcon("images/Help16.gif"));
        mitemHelp.setToolTipText("View the help files.");
        mitemHelp.setMnemonic(KeyEvent.VK_P);
        mitemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        // Special ActionListener needed here to launch the help system.
        mitemHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
        menuHelp.add(mitemHelp);

        menuBar.add(menuFile);
        menuBar.add(menuActions);
        menuBar.add(menuTools);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    /**
     * Creates the toolbar.
     */
    private void createToolBar()
    {
        tbTools = new JToolBar("Tools");

        btNew = new JButton(new ImageIcon("images/New24.gif"));
        btNew.setToolTipText("Create a new, empty ZIP archive.");
        btNew.setActionCommand("new");
        btNew.addActionListener(mwListener);
        tbTools.add(btNew);

        btOpen = new JButton(new ImageIcon("images/Open24.gif"));
        btOpen.setToolTipText("Open an existing ZIP archive.");
        btOpen.setActionCommand("open");
        btOpen.addActionListener(mwListener);
        tbTools.add(btOpen);

        btClose = new JButton(new ImageIcon("images/Import24.gif"));
        btClose.setToolTipText("Close the currently opened ZIP archive.");
        btClose.setActionCommand("close");
        btClose.addActionListener(mwListener);
        btClose.setEnabled(false);
        tbTools.add(btClose);

        tbTools.addSeparator();

        btAdd = new JButton(new ImageIcon("images/Add24.gif"));
        btAdd.setToolTipText("Add a file to the currently opened archive.");
        btAdd.setActionCommand("add");
        btAdd.addActionListener(mwListener);
        btAdd.setEnabled(false);
        tbTools.add(btAdd);

        btRemove = new JButton(new ImageIcon("images/Delete24.gif"));
        btRemove.setToolTipText("Remove the selected file from the currently opened archive.");
        btRemove.setActionCommand("remove");
        btRemove.addActionListener(mwListener);
        btRemove.setEnabled(false);
        tbTools.add(btRemove);

        btExtract = new JButton(new ImageIcon("images/Export24.gif"));
        btExtract.setToolTipText("Extract the contents of the currently opened archive.");
        btExtract.setActionCommand("extract");
        btExtract.addActionListener(mwListener);
        btExtract.setEnabled(false);
        tbTools.add(btExtract);

        btView = new JButton(new ImageIcon("images/View24.gif"));
        btView.setToolTipText("View the currently selected text file.");
        btView.setActionCommand("view");
        btView.addActionListener(mwListener);
        btView.setEnabled(false);
        tbTools.add(btView);

        tbTools.addSeparator();

        btOptions = new JButton(new ImageIcon("images/Preferences24.gif"));
        btOptions.setToolTipText("Set the program options.");
        btOptions.setActionCommand("options");
        btOptions.addActionListener(mwListener);
        tbTools.add(btOptions);

        btQuit = new JButton(new ImageIcon("images/Stop24.gif"));
        btQuit.setToolTipText("Quit the program.");
        btQuit.setActionCommand("quit");
        btQuit.addActionListener(mwListener);
        tbTools.add(btQuit);

        tbTools.addSeparator();

        btHelp = new JButton(new ImageIcon("images/Help24.gif"));
        btHelp.setToolTipText("View the help files.");
        // Special ActionListener needed here to launch the help system.
        btHelp.addActionListener(new CSH.DisplayHelpFromSource(hb));
        tbTools.add(btHelp);

        contentPane.add(tbTools, BorderLayout.NORTH);

        tbTools.setVisible(configMgr.getProperty(ConfigManager.TOOLBAR).equals("enabled"));
    }

   /**
    * Closes a currently open zip file, and clears the zip file name table.
    */
    public void closeArchive()
    {
        // Clear the ZIP entry table.
        stModel.clearTable( );
        tblZipTable.setEnabled(false);

        // Reset the status bar and title text.
        lblStatus.setText("No archive loaded");
        setTitle(Shrinkage.TITLE + " " + Shrinkage.VERSION + " - No archive loaded");

        // Disable the menu items that don't apply when no archive is opened.
        mitemAdd.setEnabled(false);
        mitemRemove.setEnabled(false);
        mitemClose.setEnabled(false);
        mitemExtract.setEnabled(false);
        mitemView.setEnabled(false);

        // Disable the buttons that don't apply when no archive is opened.
        btAdd.setEnabled(false);
        btRemove.setEnabled(false);
        btClose.setEnabled(false);
        btExtract.setEnabled(false);
        btView.setEnabled(false);

        fileCurrentArchive = null;
    }

    /**
     * Creates a new, empty archive file.
     */
    public void doNew()
    {
        JFileChooser jfChooser = new JFileChooser(configMgr.getProperty(ConfigManager.NEW_DIR));
        jfChooser.setDialogTitle("Save New Archive File");
        jfChooser.addChoosableFileFilter(new ZipFileFilter());
        int nReturnValue = jfChooser.showSaveDialog(this);

        if (nReturnValue == JFileChooser.APPROVE_OPTION)
        {
            fileCurrentArchive = jfChooser.getSelectedFile();
            if (!fileCurrentArchive.getName().toLowerCase().endsWith(".zip"))
                fileCurrentArchive = new File(fileCurrentArchive.getAbsolutePath() + ".zip");
            //try
            //{
                if (fileCurrentArchive.exists())
                {
                    if (JOptionPane.showConfirmDialog(this, "\"" + fileCurrentArchive.getAbsolutePath() +
                                                      "\" already exists. Do you wish to overwrite?",
                                                      "File Exists",
                                                      JOptionPane.YES_NO_OPTION) == 1)
                    {
                        return;
                    }

                }

                //fileCurrentArchive.createNewFile();

                // Update the GUI components.
                tblZipTable.setEnabled(true);

                // Set the status bar and title text to reflect the opened filename.
                setTitle(Shrinkage.TITLE + " " + Shrinkage.VERSION + " - " + fileCurrentArchive.getName());
                lblStatus.setText("Opened " + fileCurrentArchive.getName());

                // Enable the menu items that operate on open files.
                mitemClose.setEnabled(true);
                mitemExtract.setEnabled(true);
                mitemAdd.setEnabled(true);
                mitemRemove.setEnabled(true);
                mitemView.setEnabled(true);

                // Enable the toolbar buttons that operate on open files.
                btClose.setEnabled(true);
                btExtract.setEnabled(true);
                btAdd.setEnabled(true);
                btRemove.setEnabled(true);
                btView.setEnabled(true);
            //}
            /*
            catch (IOException ioe)
            {
                showErrorDialog("An error occurred while trying to open \"" +
                                fileCurrentArchive.getAbsolutePath() + "\".",
                                "Error opening file");
                ioe.printStackTrace();
                // Clear the ZIP entry table.
                stModel.clearTable( );
                tblZipTable.setEnabled(false);

                // Reset the status bar and title text.
                lblStatus.setText("No archive loaded");
                setTitle(Shrinkage.TITLE + " " + Shrinkage.VERSION + " - No archive loaded");

                // Disable the menu items that don't apply when no archive is opened.
                mitemAdd.setEnabled(false);
                mitemRemove.setEnabled(false);
                mitemClose.setEnabled(false);
                mitemExtract.setEnabled(false);
                mitemView.setEnabled(false);

                // Disable the buttons that don't apply when no archive is opened.
                btAdd.setEnabled(false);
                btRemove.setEnabled(false);
                btClose.setEnabled(false);
                btExtract.setEnabled(false);
                btView.setEnabled(false);

                fileCurrentArchive = null;
            }*/
        }
    }

    /**
     * Selects a file to open, then displays its contents in the zip table.
     */
    public void openArchive()
    {
        Enumeration enZipEntries;
        JFileChooser jfChooser = new JFileChooser(configMgr.getProperty(ConfigManager.OPEN_DIR));
        jfChooser.addChoosableFileFilter(new ZipFileFilter());

        int nReturnValue = jfChooser.showOpenDialog(this);

        if (nReturnValue == JFileChooser.APPROVE_OPTION)
        {
            fileCurrentArchive = jfChooser.getSelectedFile();

            try {enZipEntries = handler.getZipContents(fileCurrentArchive);}

            catch(FileNotFoundException fnfe)
            {
                showErrorDialog("\"" + fileCurrentArchive.getAbsolutePath() +
                                "\" was not found.\nCheck the path and filename and try again.",
                                "File not found");
                fileCurrentArchive = null;
                return;
            }
            catch(ZipException ze)
            {
                showErrorDialog("\"" + fileCurrentArchive.getAbsolutePath() +
                                "\" is not a valid ZIP archive.",
                                "Invalid file format");
                fileCurrentArchive = null;
                return;
            }
            catch(IOException ioe)
            {
                showErrorDialog("An error occurred while trying to open \"" +
                                fileCurrentArchive.getAbsolutePath() + "\".",
                                "Error opening file");
                fileCurrentArchive = null;
                return;
            }

            while (enZipEntries.hasMoreElements())
            {
                ZipEntry ze = (ZipEntry)enZipEntries.nextElement();

                if(ze.isDirectory())
                    continue;

                stModel.addEntry(ze.getName().substring(ze.getName().lastIndexOf("/") + 1),
                                 ze.getSize(),
                                 ze.getCompressedSize(),
                                 ze.getName());
            }


            // Update the GUI components.
            tblZipTable.setEnabled(true);

            // Set the status bar and title text to reflect the opened filename.
            setTitle(Shrinkage.TITLE + " " + Shrinkage.VERSION + " - " + fileCurrentArchive.getName());
            lblStatus.setText("Opened " + fileCurrentArchive.getName());

            // Enable the menu items that operate on open files.
            mitemClose.setEnabled(true);
            mitemExtract.setEnabled(true);
            mitemAdd.setEnabled(true);
            mitemRemove.setEnabled(true);
            mitemView.setEnabled(true);

            // Enable the buttons that operate on open files.
            btClose.setEnabled(true);
            btExtract.setEnabled(true);
            btAdd.setEnabled(true);
            btRemove.setEnabled(true);
            btView.setEnabled(true);
        }
    }

    /**
     * Starts the operation of extracting an archive.
     * @param strDirectory The directory to extract the archive into.
     */
    public void doExtract(String strDirectory)
    {
        // We need the total size, to pass to the progress bar. But we can't just take
        // the size of the ZIP file - this represents the compressed size. We need to
        // quickly step through the ZIP file and add up the uncompressed size of each
        // entry.
        long lTotalSize = 0;

        try{ handler.extractArchive(fileCurrentArchive, strDirectory, this); }
        catch (FileNotFoundException fnfe)
        {
            showErrorDialog("\"" + fileCurrentArchive.getAbsolutePath() +
                                "\" was not found.\nCheck the path and filename and try again.",
                                "File not found");
            fileCurrentArchive = null;
            return;
        }
        catch(ZipException ze)
        {
            showErrorDialog("\"" + fileCurrentArchive.getAbsolutePath() +
                            "\" is not a valid ZIP archive.",
                            "Invalid file format");
            fileCurrentArchive = null;
            return;
        }
        catch(IOException ioe)
        {
            showErrorDialog("An error occurred while trying to open \"" + fileCurrentArchive.getAbsolutePath() + "\".",
                            "Error opening file");
            fileCurrentArchive = null;
            return;
        }
    }

    /**
     * Adds a file to the currently opened archive.
     */
    public void doAdd()
    {
        // Create the file chooser, and enable multiple selections.
        JFileChooser jfChooser = new JFileChooser(configMgr.getProperty(ConfigManager.OPEN_DIR));
        jfChooser.setMultiSelectionEnabled(true);
        int nResult = jfChooser.showOpenDialog(this);

        if (nResult == JFileChooser.APPROVE_OPTION)
        {
            File[] filesToAdd = jfChooser.getSelectedFiles();

            try
            {
                // Add the file(s) to the archive.
                if(isEmpty())
                    handler.add(this,fileCurrentArchive, filesToAdd, false);
                else if (fileCurrentArchive.exists())
                    handler.add(this, fileCurrentArchive, filesToAdd, true);
                else
                    handler.add(this, fileCurrentArchive, filesToAdd, false);
            }
            catch (FileNotFoundException fnfe)
            {
                showErrorDialog("\"" + fileCurrentArchive.getAbsolutePath() +
                                "\" was not found.\nCheck the path and filename and try again.",
                                "File not found");
                fnfe.printStackTrace();

                return;
            }
            catch(ZipException ze)
            {
                showErrorDialog("An error occurred while trying to add file(s) to \"" +
                                fileCurrentArchive.getAbsolutePath() + "\".",
                                "Error");
                ze.printStackTrace();
                return;
            }
            catch(IOException ioe)
            {
                showErrorDialog("An error occurred while trying to open \"" +
                                fileCurrentArchive.getAbsolutePath() + "\".",
                                "Error opening file");
                ioe.printStackTrace();
                return;
            }
        }
    }

    /**
     * Clears and re-populates the ZIP table with the entries of the currently
     * opened ZIP file.
     */
    public void rebuildTable()
    {
        Enumeration enZipEntries;

        // First, clear the list.
        stModel.clearTable();

        try
        {
            // Re-populate the list.
            enZipEntries = handler.getZipContents(fileCurrentArchive);

            while (enZipEntries.hasMoreElements())
            {
                ZipEntry ze = (ZipEntry)enZipEntries.nextElement();

                if(ze.isDirectory())
                    continue;

                stModel.addEntry(ze.getName().substring(ze.getName().lastIndexOf("/") + 1),
                                 ze.getSize(),
                                 ze.getCompressedSize(),
                                 ze.getName());
            }

        }
        catch (FileNotFoundException fnfe)
        {
            showErrorDialog("\"" + fileCurrentArchive.getAbsolutePath() +
                            "\" was not found.\nCheck the path and filename and try again.",
                            "File not found");
            fnfe.printStackTrace();

            return;
        }
        catch(ZipException ze)
        {
            showErrorDialog("An error occurred while accessing\"" +
                            fileCurrentArchive.getAbsolutePath() + "\".", "Error");
            return;
        }
        catch(IOException ioe)
        {
            showErrorDialog("An error occurred while trying to open \"" +
                            fileCurrentArchive.getAbsolutePath() + "\".", "Error opening file");
            ioe.printStackTrace();
            return;
        }
    }

    /**
     * Removes the currently selected entry from the ZIP file.
     */
    public void doRemove()
    {
        // Get the name of the selected entry.
        int nCurrRow = tblZipTable.getSelectedRow();

        if (nCurrRow < 0)
            showErrorDialog("You must select an entry to delete.", "No entry selected");
        String strEntryName = (String)stModel.getValueAt(nCurrRow, 4);

        if (JOptionPane.showConfirmDialog(this, "Are you sure you wish to remove \"" + strEntryName +
                                           "\" from the archive?", "Confirm Remove", JOptionPane.YES_NO_OPTION) == 1)
        {
            return;
        }

        try{handler.remove(this, fileCurrentArchive, strEntryName); }
        catch(ZipException ze)
        {
            showErrorDialog("An error occurred while trying to remove \"" + strEntryName + "\".", "Error");
        }
        catch(IOException ioe)
        {
            showErrorDialog("An error occurred while trying to open \"" +
                            fileCurrentArchive.getAbsolutePath() + "\".", "Error opening file");
        }
    }

    /**
     * Views the currently selected file in the Text Viewer.
     */
     public void doView()
     {
        int nCurrRow = tblZipTable.getSelectedRow();
        String strEntryName = (String)stModel.getValueAt(nCurrRow, 4);

        try
        {
            // Get an input stream to the file we want to view.
            InputStream isEntryData = handler.getEntryInputStream(fileCurrentArchive, strEntryName);
            new TextViewer(this, isEntryData, strEntryName).show();
        }
        catch (ZipException ze)
        {
            showErrorDialog(ze.getMessage(), "ZIP error");
        }
        catch (IOException ioe)
        {
            showErrorDialog("An error occurred while accessing the ZIP file.", "I/O error");
        }
     }

    /**
     * Causes the toolbar to be either visible or hidden. It looks at the state
     * of the checkbox menu item, and then toggles it.
     */
    public void toggleToolbar()
    {
        boolean bValue = mitemToolbar.isSelected();

        tbTools.setVisible(bValue);
        configMgr.setProperty(ConfigManager.TOOLBAR, bValue ? "enabled":"disabled");
    }

    /**
     * Determines whether or not the archive file table is empty.
     * @return True if the table is empty, false if not.
     */
    public boolean isEmpty()
    {
        return (tblZipTable.getRowCount() == 0);
    }

    /**
     * Shows an error dialog box.
     * @param strTitle The title of the dialog box.
     */
     private void showErrorDialog(String strMessage, String strTitle)
     {
        JOptionPane.showMessageDialog(this, strMessage, strTitle, JOptionPane.ERROR_MESSAGE);
     }
}

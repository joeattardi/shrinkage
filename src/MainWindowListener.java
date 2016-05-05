import java.awt.event.*;
import javax.help.*;

/*
 * Provides all event listening on the main window.<br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joseph Attard <jattardi@cs.uml.edu><br>
 */
public class MainWindowListener implements WindowListener,ActionListener
{
    /** A reference to the main frame. */
    private MainFrame frame;

    /** Reference to the configuration manager. */
    private ConfigManager configMgr;

    /**
     * Creates a new MainWindowListener.
     *
     * @param frame Reference to the main frame.
     * @param configMgr Reference to the configuration manager.
     */
    public MainWindowListener(MainFrame frame, ConfigManager configMgr)
    {
        this.frame = frame;
        this.configMgr = configMgr;
    }

    /* Required by the WindowListener interface, but not used in this class. */
    public void windowActivated(WindowEvent we) {}
    /* Required by the WindowListener interface, but not used in this class. */
    public void windowClosed(WindowEvent we) {}
    /* Required by the WindowListener interface, but not used in this class. */
    public void windowDeactivated(WindowEvent we) {}
    /* Required by the WindowListener interface, but not used in this class. */
    public void windowDeiconified(WindowEvent we) {}
    /* Required by the WindowListener interface, but not used in this class. */
    public void windowIconified(WindowEvent we) {}
    /* Required by the WindowListener interface, but not used in this class. */
    public void windowOpened(WindowEvent we) {}

    /**
     * Called by the system as the window is about to be closed.
     *
     * @param we The WindowEvent supplied by the system.
     */
    public void windowClosing(WindowEvent we)
    {
        configMgr.writeSettings();
        System.exit(0);
    }

    /**
     * Called by the system when an action is performed on a component
     * that has an ActionListener attached to it.
     *
     * @param ae The ActionEvent supplied by the system.
     */
    public void actionPerformed(ActionEvent ae)
    {
        String strActionCmd = ae.getActionCommand();

        if (strActionCmd.equals("quit"))
            windowClosing(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        else if (strActionCmd.equals("open"))
            frame.openArchive();
        else if (strActionCmd.equals("close"))
            frame.closeArchive();
        else if (strActionCmd.equals("extract"))
            new ExtractDialog(frame, configMgr).show();
        else if (strActionCmd.equals("add"))
            frame.doAdd();
        else if (strActionCmd.equals("remove"))
            frame.doRemove();
        else if (strActionCmd.equals("new"))
            frame.doNew();
        else if (strActionCmd.equals("options"))
            new OptionsDialog(frame, configMgr).show();
        else if (strActionCmd.equals("view"))
            frame.doView();
        else if (strActionCmd.equals("toolbar"))
            frame.toggleToolbar();
    }

}

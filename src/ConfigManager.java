import java.util.*;
import java.io.*;
import javax.swing.*;

/**
 * Manages configuration settings in the properties file.<br><br>
 *
 * For 91.461 GUI Programming II Semester Project<br>
 * "Shrinkage" Archiving Program<br>
 *
 * @author Joe Attardi <jattardi@cs.uml.edu>
 */
public class ConfigManager
{
    /** The default directory to open files from. */
    public static final String OPEN_DIR = "open_dir";

    /** The default directory to extract files to. */
    public static final String EXTRACT_DIR = "extract_dir";

    /** The default directory to create new files in. */
    public static final String NEW_DIR = "new_dir";

    /** Whether or not to show the toolbar. */
    public static final String TOOLBAR = "toolbar";

    /** The Properties object that manages settings. */
    private Properties pSettings;

    /**
     * Creates the ConfigManager, loading initial settings
     * from disk.
     *
     * @param configMgr Reference to the configuration manager.
     */
    public ConfigManager()
    {
        FileInputStream fisInFile;

        pSettings = new Properties();

        try
        {
            fisInFile = new FileInputStream("shrinkage.properties");
            pSettings.load(fisInFile);
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "Error opening configuration file: " + ioe.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gets the value of a property.
     * @param strProperty The property name.
     * @return The value of the given property, or null if it's not found.
     */
    public String getProperty(String strProperty)
    {
        return pSettings.getProperty(strProperty);
    }

    /**
     * Sets a new value for a property.
     * @param strProperty The property name.
     * @param strValue The value of the property.
     */
    public void setProperty(String strProperty, String strValue)
    {
        // Update the property in memory.
        pSettings.setProperty(strProperty, strValue);

        // Update the configuration file.
        writeSettings();
    }

    /**
     * Writes the current configuration settings to disk.
     */
    public void writeSettings()
    {
        FileOutputStream fosOutFile;

        try
        {
            fosOutFile = new FileOutputStream("shrinkage.properties");
            pSettings.store(fosOutFile,"Shrinkage Configuration");
        }
        catch(IOException ioe)
        {
            JOptionPane.showMessageDialog(null, "Error writing to configuration file: " + ioe.getMessage(),
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

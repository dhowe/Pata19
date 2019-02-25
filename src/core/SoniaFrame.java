package core;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import pitaru.sonia.LiveInput;
import pitaru.sonia.Sonia;
import processing.core.PApplet;

public class SoniaFrame extends JFrame implements SoniaConstants, ActionListener
{
  static boolean OSX = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

  static File sampleDir, projDir;

  static protected JButton savePrefsButton;
  static protected JDialog aboutBox, prefsBox;

  static protected JMenu fileMenu, helpMenu, globalMenu, sampleMenu, bankMenu, quantizeMenu;
  static protected JMenuItem openMI, optionsMI, quitMI, saveMI;
  static protected JMenuItem docsMI, supportMI, aboutMI;

  static JSpinner microDataSpinner, microPadSpinner, microProbSpinner;
  static JComboBox modeList;

  static private PApplet p;

  static String[] sampleMenuNames = { OPEN, CUT, COPY, PASTE, REVERT, REVERSE, DOUBLE, DECLICK };
  static String[] sampleCbMenuNames = { SOLO, MUTE, SWEEP, BOUNCE, };
  static String[][] nestedMenus = { { SHIFT, "-12", "-7", "-5", "-2", "  0", "  2", "  3", "  5", "  12", "  0" } };
  static String[] bankMenuNames = { SOLO, MUTE, CLEAR, DOUBLE, SWEEP, BOUNCE, REVERT, REVERSE, DOUBLE, DECLICK };
  static ButtonGroup qGroup;

  private boolean saving;

  //private float[] bg;
  //private boolean isExiting;

  public SoniaFrame(PApplet sketch, int w, int h, int inputDevId, int outputDevId)
  {
    if (OSX) System.setProperty("apple.laf.useScreenMenuBar", "true");

    //startSonia(p, inputDevId, outputDevId);
    
    p = sketch;
    try {
      
      startSonia(p, inputDevId, outputDevId);
      
    } catch (Exception e) {
      System.err.println("\nFATAL: Unable to use in:"+inputDevId+", out:"+outputDevId+" as device-ids\n");
      throw e;
    }

    sampleDir = new File(DATA_DIR);
    projDir = new File(PROJ_DIR);

    addMenus();

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e)
      {
        quit();
      }
    });

    JPanel panel = createSketchPanel(sketch, w, h);

    aboutBox = createAbout();
    prefsBox = createPrefs();

    registerForMacOSXEvents();

    sketch.init(); // start the sketch

    doLayout(w, h);

    panel.requestFocus();
  }
  
  void startSonia(PApplet p, int inputDeviceId, int outputDeviceId)
  {    
    Sonia.setInputDevice(inputDeviceId);
    Sonia.setOutputDevice(outputDeviceId);

    Sonia.start(p);
    
    LiveInput.start(SPECTRUM_LENGTH);
    LiveInput.useEqualizer(false);
  }


  private JPanel createSketchPanel(PApplet sketch, int w, int h)
  {
    JPanel panel = new javax.swing.JPanel();
    Color purple = new Color(40);
    //this.bg = new float[] { purple.getRed(), purple.getGreen(), purple.getBlue() };
    panel.setBackground(purple);
    panel.setBounds(20, 0, w, h + 40);
    panel.add(sketch);
    this.add(panel);
    return panel;
  }

  private JDialog createPrefs()
  {
    JDialog prefsDialog = new JDialog(this, "Preferences");
    prefsDialog.setModal(true);
    prefsDialog.addWindowListener(new WindowAdapter() {
      public void windowOpened(WindowEvent e)
      {
        System.out.println("prefsDialog.windowOpened...");
        refreshPrefsData();
      }
    });

    JPanel prefsPanel = new JPanel();
    prefsPanel.setLayout(null);
    prefsPanel.setSize(500, 400);
    Insets is = prefsPanel.getInsets();

    return prefsDialog;
  }


  // Generic registration with the Mac OS X application menu
  public void registerForMacOSXEvents()
  {
    if (OSX) {
      try {
        // Generate and register the OSXAdapter, passing the methods we wish to
        // use as delegates for various com.apple.awt.ApplicationListener
        // methods
        OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[]) null));
        OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
        OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[]) null));
        // OSXAdapter.setFileHandler(this,
        // getClass().getDeclaredMethod("loadImageFile", new Class[] {
        // String.class }));
      } catch (Exception e)
      {
        System.err.println("Error while loading OSXAdapter code...");
        e.printStackTrace();
      }
    }
  }

  private JDialog createAbout()
  {
    JDialog ab = new JDialog(this, "About");
    ab.getContentPane().setLayout(new BorderLayout());
    ab.getContentPane().add(new JLabel("SamplerFi[" + VERSION + "]", JLabel.CENTER));
    ab.getContentPane().add(new JLabel("\u00A920010 Daniel Howe", JLabel.CENTER), BorderLayout.SOUTH);
    ab.setSize(160, 120);
    ab.setResizable(false);
    return ab;
  }

  private void doLayout(int w, int h)
  {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int yoff = (screenSize.height - h) / 2; // the window Dimensions
    setBounds((screenSize.width - w) / 2, yoff, w, h + 40);
    setUndecorated(yoff < 20);
    setVisible(true);
  }

  public void addMenus()
  {
    JMenu fileMenu = new JMenu("File");

    JMenuBar mainMenuBar = new JMenuBar();

    // FILE_MENU ----------------------
    mainMenuBar.add(fileMenu = new JMenu("File"));
    setShortcut(fileMenu, KeyEvent.VK_F);

    fileMenu.add(openMI = new JMenuItem("Open..."));
    setShortcut(openMI, KeyEvent.VK_O);
    openMI.addActionListener(this);

    fileMenu.addSeparator();

    fileMenu.add(saveMI = new JMenuItem("Save"));
    setShortcut(saveMI, KeyEvent.VK_S);
    saveMI.addActionListener(this);

    // Quit/prefs menu items are provided on Mac OS X; only add your own on
    // other platforms
    if (!OSX)
    {
      fileMenu.addSeparator();
      fileMenu.add(optionsMI = new JMenuItem("Options"));
      optionsMI.addActionListener(this);

      fileMenu.addSeparator();

      fileMenu.add(quitMI = new JMenuItem("Quit"));
      setShortcut(quitMI, KeyEvent.VK_Q);
      quitMI.addActionListener(this);
    }

    // GLOBAL_MENU ---------------------
    if (SHOW_GLOBAL_MENU) {

      mainMenuBar.add(globalMenu = new JMenu("Global"));
      setShortcut(fileMenu, KeyEvent.VK_G);

      JMenuItem nextMI = null;
      for (int i = 0; i < Switch.ACTIVE.length; i++) {
        globalMenu.add(nextMI = new JCheckBoxMenuItem(Switch.ACTIVE[i].name));
        nextMI.setSelected(Switch.ACTIVE[i].on);
        setShortcut(nextMI, Switch.ACTIVE[i].key);
        nextMI.addActionListener(this);
      }
    }

    // BANK_MENU ---------------------
    if (SHOW_BANK_MENU) {

      mainMenuBar.add(bankMenu = new JMenu("Bank"));
      setShortcut(bankMenu, KeyEvent.VK_B);

      JMenuItem jmi = null;
      for (int i = 0; i < bankMenuNames.length; i++)
      {
        if (bankMenuNames[i].equals(SEP)) bankMenu.addSeparator();
        else {
          bankMenu.add(jmi = new JCheckBoxMenuItem(bankMenuNames[i]));
          jmi.addActionListener(this);
        }
      }
    }

    // SAMPLE_MENU ---------------------
    mainMenuBar.add(sampleMenu = new JMenu("Sample"));

    JMenuItem jmi = null;
    for (int i = 0; i < sampleMenuNames.length; i++)
    {
      sampleMenu.add(jmi = new JMenuItem(sampleMenuNames[i]));
      jmi.addActionListener(this);
      // keyboard shortcuts
      if (sampleMenuNames[i].equals(PASTE)) {
        setShortcut(jmi, KeyEvent.VK_X);
      }
      else
        if (sampleMenuNames[i].equals(CUT))
        // jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_MASK));
        setShortcut(jmi, KeyEvent.VK_X);
        else
          if (sampleMenuNames[i].equals(COPY))
            // jmi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
            // MENU_MASK));
            setShortcut(jmi, KeyEvent.VK_C);
    }

    // QUANTIZE_MENU ---------------------
    int[] accelerators = { KeyEvent.VK_G, KeyEvent.VK_K, KeyEvent.VK_M, KeyEvent.VK_N };
    mainMenuBar.add(quantizeMenu = new JMenu("Quantize"));
    qGroup = new ButtonGroup();
    for (int i = 0; i < QUANTIZE_MODES.length; i++)
    {
      JRadioButtonMenuItem rmi = new JRadioButtonMenuItem(QUANTIZE_MODES[i]);
      // rmi.setAccelerator(KeyStroke.getKeyStroke(accelerators[i], MENU_MASK));
      setShortcut(rmi, accelerators[i]);
      rmi.addActionListener(this);
      rmi.setSelected(i == 0);
      quantizeMenu.add(rmi);
      qGroup.add(rmi);
    }

    if (false) { // modal menus
      for (int i = 0; i < sampleCbMenuNames.length; i++)
      {
        if (i == 0) sampleMenu.addSeparator();
        sampleMenu.add(jmi = new JCheckBoxMenuItem(sampleCbMenuNames[i]));
        jmi.addActionListener(this);
      }
    }

    // help & about menus ---------------------

    mainMenuBar.add(helpMenu = new JMenu("Help"));
    helpMenu.add(docsMI = new JMenuItem("Online Documentation"));
    helpMenu.addSeparator();
    helpMenu.add(supportMI = new JMenuItem("Technical Support"));

    // About menu item is provided on Mac OS X; only add your own on other
    // platforms
    if (!OSX)
    {
      helpMenu.addSeparator();
      helpMenu.add(aboutMI = new JMenuItem("About SamplerFi"));
      aboutMI.addActionListener(this);
    }

    setJMenuBar(mainMenuBar);
  }

  private void setShortcut(JMenuItem jmi, int keyEvent)
  {
    if (!(jmi instanceof JMenu))
      jmi.setAccelerator(KeyStroke.getKeyStroke(keyEvent, KeyEvent.META_DOWN_MASK));
    jmi.setMnemonic(keyEvent);
  }

  public void actionPerformed(ActionEvent e)
  {
    Object src = e.getSource();
    String cmd = e.getActionCommand();

    // System.out.println("ApplicationFrame.actionPerformed("+src.getClass().getName()+","+cmd+")");

    // global-switch menu
    for (int i = 0; i < Switch.ACTIVE.length; i++)
    {
      if (Switch.ACTIVE[i].name.equals(cmd)) {
        Switch.ACTIVE[i].toggle();
        return;
      }
    }

    // File, Help, Global menus -----------------------------

    if (src == quitMI)
    {
      quit();
    }
    else
      if (src == optionsMI)
      {
        preferences();
      }
      else
        if (src == aboutMI)
        {
          about();
        }
        else
          if (src == saveMI)
          {
            saveProject();
          }
          else
            if (src == savePrefsButton)
            {
              savePrefs();
            }
            else
              if (src == openMI) // open proj config file
              {
                loadProject();
              }
  }

  private void savePrefs()
  {

  }

  private void loadProject()
  {

  }

  public void about()
  {
    aboutBox.setLocation((int) this.getLocation().getX() + 22, (int) this.getLocation().getY() + 22);
    aboutBox.setVisible(true);
  }

  public void preferences()
  {
    prefsBox.setLocation(getWidth() / 2 - 150, getHeight() / 2 - 150);
    prefsBox.setVisible(true);
  }

  public void saveProject()
  {
  }

  public boolean quit()
  {
    int option = JOptionPane.YES_OPTION;

    if (CONFIRM_ON_QUIT) {
      option = JOptionPane.showConfirmDialog(this,
          "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
    }

    Sonia.stop();

    if (option == JOptionPane.YES_OPTION) {

      //this.isExiting = true;

      System.out.print("[INFO] Exiting...");

      for (int i = 0; this.isSaving(); i++) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e1) {
          System.err.println("[WARN] " + e1.getMessage());
        }
        System.out.print(".");
        if (i % 50 == 49)
          System.out.println();
      }
      System.out.println("OK");

      System.exit(1);
    }

    return (option == JOptionPane.YES_OPTION);
  }

  private boolean isSaving()
  {
    return saving;
  }

  private void refreshPrefsData()
  {
  }

  public static JMenuItem getSampleMenuItem(String text)
  {
    Component[] c = sampleMenu.getMenuComponents();
    for (int i = 0; i < c.length; i++)
    {
      JMenuItem jmi = (JMenuItem) c[i];
      if (jmi.getActionCommand().equals(text))
        return jmi;
    }
    return null;
  }

}
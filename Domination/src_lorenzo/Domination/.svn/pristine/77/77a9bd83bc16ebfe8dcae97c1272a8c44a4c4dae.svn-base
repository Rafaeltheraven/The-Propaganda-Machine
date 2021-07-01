// Yura Mamyrin, Group D

package net.yura.domination.ui.swinggui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskAdapter;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.core.StatType;
import net.yura.swing.GraphicsUtil;
import net.yura.swing.ImageIcon;
import net.yura.domination.guishared.PicturePanel;
import net.yura.domination.guishared.RiskFileFilter;
import net.yura.domination.guishared.StatsPanel;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.tools.mapeditor.MapEditor;

/**
 * <p> Swing GUI Main Frame </p>
 * @author Yura Mamyrin
 */
public class SwingGUIPanel extends JPanel implements ActionListener{

	public final static String version = "2";
	public final static String product = "Swing GUI for " + RiskUtil.GAME_NAME;

	private ResourceBundle resbundle = TranslationBundle.getBundle();

	private JTabbedPane tabbedpane;
	private JToolBar currentToolbar;
	private JMenuBar gMenuBar;

	private GameTab gameTab;
	private ConsoleTab consoleTab;
	private StatisticsTab statisticsTab;
	private DebugTab debugTab;
	private MapEditor editorTab;

	Risk myrisk;
	int gameState;
	PicturePanel pp;
	JButton lobby;

	/**
	 * Creates a new SwingGUI
	 * @param r The Risk object for this GUI
	 */
	public SwingGUIPanel(Risk r) {
		myrisk= r;

		//c1Id = -1;
		gameState=-1; // (-1 means no game)

		pp = new PicturePanel(myrisk);

		setLayout(new java.awt.BorderLayout());

		// set the border of the window
		//setDefaultLookAndFeelDecorated(true);
		//setUndecorated(true);
		//getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

		// add menu bar
		gMenuBar = new JMenuBar();

		tabbedpane = new JTabbedPane();

                lobby = new JButton( resbundle.getString("lobby.run") );
		lobby.setActionCommand("lobby");
		lobby.addActionListener( this );
		lobby.setBackground( Color.RED );
		lobby.setVisible(false);
                
		gameTab = new GameTab(this);
		consoleTab = new ConsoleTab();
		editorTab = new MapEditor(myrisk,this);

		addTab(gameTab);
		addTab( new FX3DPanel(pp) );
                try {
                    if (RiskUIUtil.checkForNoSandbox()) {
                        addTab( new LobbyTab(myrisk) );
                    }
                }
                catch (Throwable th) {
                    RiskUtil.printStackTrace(th); // midletrunner.jar could be missing
                }
		addTab(consoleTab);

                try {
                    statisticsTab = new StatisticsTab();
                    addTab(statisticsTab);
                }
                catch (Throwable th) {
                    RiskUtil.printStackTrace(th); // jfreechart could be missing
                }

                try {
                    debugTab = new DebugTab();
                    addTab(debugTab);
                }
                catch (Throwable th) {
                    RiskUtil.printStackTrace(th); // Grasshopper.jar could be missing
                }
		addTab( new TestPanel(myrisk,pp) );
		addTab(editorTab);
                try {
                    if (RiskUIUtil.checkForNoSandbox()) {
                        addTab( new TranslationToolPanel() );
                    }
                }
                catch (Throwable th) {
                    RiskUtil.printStackTrace(th); // TranslationTool.jar could be missing
                }

                addTab(new BugsPanel(this));

		add(tabbedpane, java.awt.BorderLayout.CENTER );


		ChangeListener changeMenu = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				SwingGUITab sgt = (SwingGUITab)tabbedpane.getSelectedComponent();

				if (currentToolbar!=null) { remove(currentToolbar); }
				currentToolbar = sgt.getToolBar();

                                if (currentToolbar!=null) {
                                    currentToolbar.setOrientation( javax.swing.JToolBar.HORIZONTAL );
                                    add( currentToolbar, java.awt.BorderLayout.NORTH );
                                }

                                revalidate();
				repaint();
			}
		};






		tabbedpane.addChangeListener( changeMenu );

		//oddpanelbug.add(toolbarGUI, java.awt.BorderLayout.NORTH );
		changeMenu.stateChanged(null);






		// create Help menu item
		JMenu gHelp = new JMenu(resbundle.getString("swing.menu.help"));
		gHelp.setMnemonic('H');

		JMenuItem gmManual = new JMenuItem(resbundle.getString("swing.menu.manual"));
		gmManual.setMnemonic('M');
		gmManual.setActionCommand("manual");
		gmManual.addActionListener( this );
		gHelp.add(gmManual);

		JMenuItem cmCommands = new JMenuItem(resbundle.getString("swing.menu.console.commands"));
		cmCommands.setMnemonic('C');
		cmCommands.setActionCommand("commands");
		cmCommands.addActionListener( this );
		gHelp.add(cmCommands);

		JMenuItem gmAbout = new JMenuItem(resbundle.getString("swing.menu.about"));
		gmAbout.setMnemonic('A');
		gmAbout.setActionCommand("about");
		gmAbout.addActionListener( this );
		gHelp.add(gmAbout);

		gMenuBar.add(gHelp);

                new PLAF(this);

		//add(gMenuBar, java.awt.BorderLayout.NORTH );
		// sets menu bar
		//setJMenuBar(gMenuBar);
		//setBounds(new java.awt.Rectangle(0,0,905,629));


		// now gui is setup u can listen
		myrisk.addRiskListener( new SwingRiskAdapter() );

                if (debugTab!=null) {
                    debugTab.start();
                }

                net.yura.domination.engine.ai.AIManager.setWait(5);
	}

        public JMenuBar getJMenuBar() {
            return gMenuBar;
        }
        
        public JTabbedPane getJTabbedPane() {
            return tabbedpane;
        }

        public void firePropertyChange(String string, Object o, Object o1) {
            super.firePropertyChange(string, o, o1);
        }

	public void checkForUpdates() {
                RiskUIUtil.checkForUpdates(myrisk);
		if (RiskUIUtil.getAddLobby()) {
			lobby.setVisible(true);
                        revalidate();
                        repaint();
		}
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getActionCommand().equals("manual")) {
			try {
				RiskUtil.openDocs( resbundle.getString("helpfiles.swing") );
			}
			catch(Exception e) {
				showError("Unable to open manual: "+e.getMessage() );
			}
		}
		else if (a.getActionCommand().equals("about")) {
			openAbout();
		}
		else if (a.getActionCommand().equals("quit")) {
			System.exit(0);
		}
		else if (a.getActionCommand().equals("commands")) {
			Commands();
		}
		else {
			System.out.print("command \""+a.getActionCommand()+"\" is not implemented yet\n");
		}
	}

	public void addTab(SwingGUITab a) {
		//tabbedpane.addTab(a.getName(),(Component)a);
		tabbedpane.add((Component)a);
		JMenu menu = a.getMenu();
		if (menu!=null) {
			gMenuBar.add(menu);
		}
	}

        public void setSelectedTab(Class tab) {
            for (int i = 0; i < tabbedpane.getTabCount(); i++) {
                Component c = tabbedpane.getComponentAt(i);
                if (tab.isInstance(c)) {
                    tabbedpane.setSelectedIndex(i);
                    return;
                }
            }
            throw new IllegalArgumentException("tab not found " + tab);
	}

	/**
	 * Submits input to parser if neccessary
	 * @param input The string that is checked
	 */
	public void go(String input) {

		pp.setHighLight(PicturePanel.NO_COUNTRY);
		// Testing.append("Submitted: \""+input+"\"\n");

		if (gameState != RiskGame.STATE_PLACE_ARMIES || !myrisk.getGame().getSetupDone() ) { blockInput(); }

		myrisk.parser(input);

		// Console.setCaretPosition(Console.getDocument().getLength());
	}

	/**
	 * Blocks the game panel
	 */
	public void blockInput() {
		gameState= -1;
                gameTab.showPanel("nothing");
		gameTab.blockInput();
		consoleTab.blockInput();
	}

	public void pprepaintCountries() {

		String tmp = gameTab.getSelectedMapView();
		int newview = -1;

		if (tmp.equals(resbundle.getString("game.tabs.continents")))           { newview=PicturePanel.VIEW_CONTINENTS; }
		else if (tmp.equals(resbundle.getString("game.tabs.ownership")))       { newview=PicturePanel.VIEW_OWNERSHIP; }
		else if (tmp.equals(resbundle.getString("game.tabs.borderthreat")))    { newview=PicturePanel.VIEW_BORDER_THREAT; }
		else if (tmp.equals(resbundle.getString("game.tabs.cardownership")))   { newview=PicturePanel.VIEW_CARD_OWNERSHIP; }
		else if (tmp.equals(resbundle.getString("game.tabs.troopstrength")))   { newview=PicturePanel.VIEW_TROOP_STRENGTH; }
		else if (tmp.equals(resbundle.getString("game.tabs.connectedempire"))) { newview=PicturePanel.VIEW_CONNECTED_EMPIRE; }

		pp.repaintCountries( newview );
	}

        /**
         * called by the map editor to show a preview of the map we are making/testing
         */
        public void showMapImage(Icon p) {
            gameTab.guiSetup.showMapImage(p, "loaded from memory");
        }

	/**
	 * This reads in a file for the commands
	 */
	public void Commands() {

		String commands="";

		try {

			BufferedReader bufferin=new BufferedReader(new InputStreamReader( RiskUtil.openStream("commands.txt") ));

			String input = bufferin.readLine();
			while(input != null) {
				if (commands.equals("")) { commands = input; }
				else { commands = commands + "\n" + input; }
				input = bufferin.readLine();
			}
			bufferin.close();
			// Testing.append("Commands Box opened\n");
			JOptionPane.showMessageDialog( RiskUIUtil.findParentFrame(this) , commands, resbundle.getString("swing.message.commands"), JOptionPane.PLAIN_MESSAGE);
		}
		catch (Exception e) {
			showError("error with commands.txt file: "+e.getMessage() );
		}
	}

	/**
	 * This opens the about dialog box
	 */
	public void openAbout() {
		RiskUIUtil.openAbout( RiskUIUtil.findParentFrame(this) ,product, version);
	}

	public void showError(String error) {
		JOptionPane.showMessageDialog(this, resbundle.getString("swing.message.error") + " " + error, resbundle.getString("swing.title.error"), JOptionPane.ERROR_MESSAGE);
	}

        File getNewLogFile() {
                JFileChooser fc = new JFileChooser();
                RiskFileFilter filter = new RiskFileFilter(RiskFileFilter.RISK_LOG_FILES);
                fc.setFileFilter(filter);

                int returnVal = fc.showSaveDialog(RiskUIUtil.findParentFrame(this));
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        String fileName = file.getName();
                        if (!(fileName.endsWith("." + RiskFileFilter.RISK_LOG_FILES))) {
                                file = new File(file.getParentFile(), fileName + "." + RiskFileFilter.RISK_LOG_FILES);
                        }
                        return file;
                }
                return null;
        }

	void saveLog(JTextArea textArea) {

                File logFile = getNewLogFile();
                if (logFile != null) {
                        try {
                                FileWriter fileout = new FileWriter(logFile);
                                BufferedWriter buffer = new BufferedWriter(fileout);
                                PrintWriter printer = new PrintWriter(buffer);
                                printer.write(textArea.getText());
                                printer.close();
                        }
                        catch(Exception error) {
                                showError( error.getMessage() );
                        }
                }
	}

	public void submitBug(String to, String from, String subjectIn, String messageFromUser, String cause) {

            String subject = RiskUtil.GAME_NAME +" "+RiskUtil.RISK_VERSION+" SwingGUI "+ TranslationBundle.getBundle().getLocale().toString()+" "+subjectIn;

            try {
                if (RiskUIUtil.checkForNoSandbox()) {
                    Map map = new HashMap();
                    RiskGame game = myrisk.getGame();
                    if (game != null) {
                        map.put("gameLog", new net.yura.grasshopper.LogList(game.getCommands()));
                    }
                    if (messageFromUser != null) {
                        map.put("messageFromUser" , messageFromUser);
                    }
                    map.put("lobbyID", net.yura.lobby.mini.MiniLobbyClient.getMyUUID());
                    map.put("debugText", debugTab.getDebugText());
                    map.put("errText", debugTab.getErrText());
                    if (to != null) {
                        // TODO: the current server (TF_MAIL) ignores this, so all emails end up going to bugs@
                        map.put("recipient", to);
                    }

                    net.yura.grasshopper.BugSubmitter.submitBug(map, from, subject, cause, RiskUtil.GAME_NAME,
                            RiskUtil.RISK_VERSION+" (save: " + RiskGame.SAVE_VERSION + " network: "+RiskGame.NETWORK_VERSION+")",
                            TranslationBundle.getBundle().getLocale().toString()
                        );
                    JOptionPane.showMessageDialog(this, "SENT!");
                    // everything went well sending through grasshopper, we return
                    return;
                }
            }
            catch (Throwable th) { } // maybe Grasshopper.jar is missing

            // if for some reason we can not send with grasshopper, we fall back to client email
            try {
                String text = (messageFromUser != null ? messageFromUser : "") + "\n\n\n" +
                        debugTab.getDebugText() + "\n" +
                        debugTab.getErrText() + "\n\n" +
                        "OS: " + RiskUIUtil.getOSString() + "\n" +
                        "ID: " + net.yura.lobby.mini.MiniLobbyClient.getMyUUID();

                // for some reason + does not get decoded, so we set it back to a space
                URL url = new URL("mailto:yura@yura.net"+
                        "?subject="+URLEncoder.encode(subject, "UTF-8").replace('+', ' ')+
                        "&body="+URLEncoder.encode(text, "UTF-8").replace('+', ' '));

                RiskUtil.openURL(url);
            }
            catch (Throwable th) {
                JOptionPane.showMessageDialog(this, "Error opening native email: "+th);
            }
        }

	//############################################################################################################

// this get all the commands from the game and does what needs to be done
	class SwingRiskAdapter extends RiskAdapter {

		/**
		 * Checks if redrawing or repainting is needed
		 * @param output
		 * @param redrawNeeded If frame needs to be redrawn
		 * @param repaintNeeded If frame needs to be repainted
		 */
		public void sendMessage(String output, boolean redrawNeeded, boolean repaintNeeded) {
			// Testing.append("Returned: \""+output+"\"\n");
			consoleTab.addOutput(output);

			if (redrawNeeded) {
				pprepaintCountries();
			}
			if (repaintNeeded) {
				repaint();
			}
		}

		public void sendDebug(String a) {
                    if (debugTab!=null) {
			debugTab.sendDebug(a);
                    }
		}

		public void showMessageDialog(String a) {
			showError(a);
		}

		/**
		 * Blocks the game panel
		 */
		public void noInput() {
			blockInput();
		}

		/**
		 * checks if the the frame needs input
		 * @param s determines what needs input
		 */
		public void needInput(int s) {
			gameState=s;
                        gameTab.getInput(s);
			consoleTab.getInput();
			repaint();
		}

		/**
		 * Displays a message
		 * @param state The message that is needed to be displayed
		 */
		public void setGameStatus(String state) {
			gameTab.setGameStatus(state);
		}

		public void showMapPic(RiskGame p) {
                    ImageIcon i=null;
                    try {
                        i = new ImageIcon( PicturePanel.getImage(p) );
                    }
                    catch(Throwable e) { }
                    gameTab.guiSetup.showMapImage(i, p.getMapFile());
		}

		public void showCardsFile(String c, boolean m) {
                    gameTab.guiSetup.showCardsFile(c,m);
		}

		public void newGame(boolean t) { // t==true: this is a local game
			gameTab.setLocalGame(t);
			gameTab.newGame();
		}

		public void startGame(boolean localGame) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			gameTab.setLocalGame(localGame);
			// check maybe we should load from memory
			if (myrisk.getGame().getMapFile() == null && myrisk.getGame().getCardsFile() == null) {
				pp.memoryLoad(editorTab.getImageMap(),editorTab.getImagePic());
			}
			else {
				try {
					pp.load();
				}
				catch(IOException e) {
                                        RiskUtil.printStackTrace(e);
				}
                                catch(OutOfMemoryError e) {
                                        RiskUtil.printStackTrace(e);
                                }
			}

                        blockInput();

			// YURA: not sure why this needs to be here, used to work without it
			pprepaintCountries();
			gameTab.startGame();
			statisticsTab.startGame();

			SwingGUIPanel.this.setCursor(null); // Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)

                        if (!localGame && tabbedpane.getSelectedComponent() instanceof LobbyTab) {
                            // SortingFocusTraversalPolicy can throw a null pointer if set tab called from non UI thread
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    setSelectedTab(GameTab.class);
                                }
                            });
                        }
		}

		/**
		 * Closes the game
		 */
		public void closeGame() {
                    // we are removing lots of UI, better do it in UI thread or things will crash
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gameTab.closeGame();
                            statisticsTab.closeGame();
                        }
                    });
		}

		public void showDiceResults(int[] att, int[] def) {

			String output=resbundle.getString("core.dice.results");

			output = output + " " + resbundle.getString("core.dice.attacker");
			for (int c=0; c< att.length ; c++) {
				output = output + " " + (att[c]+1);
			}

			output = output + " " + resbundle.getString("core.dice.defender");
			for (int c=0; c< def.length ; c++) {
				output = output + " " + (def[c]+1);
			}

			gameTab.resultsLabel.setText(output);

			gameTab.showPanel("results");
		}

		public void closeBattle() {
			blockInput();
		}

		public void serverState(boolean s) {
			gameTab.serverState(s);
		}

		public void addPlayer(int t, String name, int color, String ip) {
			gameTab.guiSetup.addPlayer(t, name, color, ip);
		}

		public void delPlayer(String name) {
			gameTab.guiSetup.delPlayer(name);
		}

/*  // other things that need to be done

public void openBattle(int c1num, int c2num) {}
public void setNODAttacker(int n) {}
public void setNODDefender(int n) {}

*/
	}

class ConsoleTab extends JPanel implements SwingGUITab, ActionListener {

	private String temptext;
	private List history;
	private int pointer;

	JMenu cConsole;
	JToolBar toolbarCon;

	private JTextArea Console;
	private JTextField Command;
	private JButton Submit;

	private JLabel statusBar;
	private JScrollPane Con;


	public ConsoleTab() {
		setName( resbundle.getString("swing.tab.console") );

		// ################### CONSOLE #######################

		history = new java.util.Vector();
		pointer=-1;

		statusBar = new JLabel(resbundle.getString("swing.status.loading"));
		Console = new JTextArea();
		Con = new JScrollPane(Console);

		Dimension conSize = GraphicsUtil.newDimension(PicturePanel.PP_X, PicturePanel.PP_Y + 60);

		Con.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Con.setPreferredSize(conSize);
		Con.setMinimumSize(conSize);

		// Console.setBackground(Color.white); // not needed with swing
		Console.setEditable(false);

		Command = new JTextField("");
		// Command.setColumns(75); // dont use because it goes odd in linux
		Dimension CommandSize = GraphicsUtil.newDimension(PicturePanel.PP_X - 50, 20);
		//c.ipadx = 600; // width
		//c.ipadx = 0; // width
		Command.setPreferredSize(CommandSize);
		Command.setMinimumSize(CommandSize);
		Command.setMaximumSize(CommandSize);

		Submit = new JButton(resbundle.getString("swing.button.submit"));


		Submit.setActionCommand("read command");
		Submit.addActionListener( this );
		Command.setActionCommand("read command");
		Command.addActionListener( this );




		// make tool bar

		toolbarCon = new JToolBar();
		toolbarCon.setRollover(true);
		toolbarCon.setFloatable(false);

		JButton cRunScript	= new JButton(resbundle.getString("swing.menu.console.runscript"));
		JButton cSaveConsole	= new JButton(resbundle.getString("swing.menu.console.save"));
		JButton cClearConsole	= new JButton(resbundle.getString("swing.menu.console.clear"));
		JButton cClearHistory	= new JButton(resbundle.getString("swing.menu.console.histclear"));

		JButton cCommands	= new JButton(resbundle.getString("swing.menu.console.commands"));
		JButton cManual		= new JButton(resbundle.getString("swing.menu.manual"));
		JButton cAbout		= new JButton(resbundle.getString("swing.menu.about"));
		JButton cQuit		= new JButton(resbundle.getString("swing.menu.quit"));

		cRunScript.setActionCommand("run script");
		cRunScript.addActionListener( this );
		cSaveConsole.setActionCommand("save console");
		cSaveConsole.addActionListener( this );
		cClearConsole.setActionCommand("clear console");
		cClearConsole.addActionListener( this );
		cClearHistory.setActionCommand("clear history");
		cClearHistory.addActionListener( this );

		cCommands.setActionCommand("commands");
		cCommands.addActionListener( this );
		cManual.setActionCommand("manual");
		cManual.addActionListener( this );
		cAbout.setActionCommand("about");
		cAbout.addActionListener( this );
		cQuit.setActionCommand("quit");
		cQuit.addActionListener( this );

		toolbarCon.add(cRunScript);
		toolbarCon.add(cSaveConsole);
		toolbarCon.add(cClearConsole);
		toolbarCon.add(cClearHistory);
		toolbarCon.addSeparator();
		toolbarCon.add(cCommands);
		toolbarCon.add(cManual);
		toolbarCon.add(cAbout);
		if (RiskUIUtil.checkForNoSandbox()) { toolbarCon.add(cQuit); }




		// create Console menu item
		cConsole = new JMenu(resbundle.getString("swing.menu.console"));
		cConsole.setMnemonic('C');

		JMenuItem cmRunScript = new JMenuItem(resbundle.getString("swing.menu.console.runscript"));
		cmRunScript.setMnemonic('R');
		cmRunScript.setActionCommand("run script");
		cmRunScript.addActionListener( this );
		cConsole.add(cmRunScript);

		JMenuItem cmSaveConsole = new JMenuItem(resbundle.getString("swing.menu.console.save"));
		cmSaveConsole.setMnemonic('S');
		cmSaveConsole.setActionCommand("save console");
		cmSaveConsole.addActionListener( this );
		cConsole.add(cmSaveConsole);

		JMenuItem cmClearConsole = new JMenuItem(resbundle.getString("swing.menu.console.clear"));
		cmClearConsole.setMnemonic('C');
		cmClearConsole.setActionCommand("clear console");
		cmClearConsole.addActionListener( this );
		cConsole.add(cmClearConsole);

		JMenuItem cmClearHistory = new JMenuItem(resbundle.getString("swing.menu.console.histclear"));
		cmClearHistory.setMnemonic('H');
		cmClearHistory.setActionCommand("clear history");
		cmClearHistory.addActionListener( this );
		cConsole.add(cmClearHistory);





		GridBagConstraints c = new GridBagConstraints();
		c.insets = new java.awt.Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.BOTH;

		setLayout(new java.awt.GridBagLayout());

		c.gridx = 0; // col
		c.gridy = 0; // row
		c.gridwidth = 2; // width
		c.gridheight = 1; // height
                c.weightx = 1d;
                c.weighty = 1d;
		add(Con, c);

		c.gridx = 0; // col
		c.gridy = 1; // row
		c.gridwidth = 1; // width
		c.gridheight = 1; // height
                c.weightx = 1d;
                c.weighty = 0d;
		add(Command, c);

		c.gridx = 1; // col
		c.gridy = 1; // row
		c.gridwidth = 1; // width
		c.gridheight = 1; // height
                c.weightx = 0d;
                c.weighty = 0d;
		add(Submit, c);

		c.gridx = 0; // col
		c.gridy = 2; // row
		c.gridwidth = 2; // width
		c.gridheight = 1; // height
                c.weightx = 1d;
                c.weighty = 0d;
		add(statusBar, c);




		Command.addKeyListener( new KeyAdapter() {

			public void keyPressed(KeyEvent key) {


				if (key.getKeyCode() == 38) {
					// Testing.append("up key (history)\n");

					if (pointer < 0) {
						Toolkit.getDefaultToolkit().beep();
					}
					else {
						if (pointer == history.size()-1) { temptext=Command.getText(); }
						Command.setText( (String)history.get(pointer) );
						pointer--;
					}
				}
				else if(key.getKeyCode() == 40) {
					// Testing.append("down key (history)\n");


					if (pointer > history.size()-2 ) {
						Toolkit.getDefaultToolkit().beep();
					}
					else if (pointer == history.size()-2 ) {
						Command.setText(temptext);
						pointer++;
					}
					else {
						pointer=pointer+2;
						Command.setText( (String)history.get(pointer) );
						pointer--;
					}

				}
				else {
					pointer = history.size()-1;
				}

			}
		});

		setOpaque(false);

		statusBar.setText(resbundle.getString( "swing.status.ready"));
	}

	private void cgo(String input) {

		if (input.equals("exit") ) {
			// Testing.append("Exit.\n");
			System.exit(0);
		}
		else if (input.equals("help") ) {
			Commands();
		}
		else if (input.equals("about") ) {
			openAbout();
		}
		else if (input.equals("clear") ) {
			// Testing.append("Console cleared\n");
			Console.setText("");
		}
		else if (input.equals("manual") ) {

			try {
				RiskUtil.openDocs( resbundle.getString("helpfiles.swing") );
			}
			catch(Exception e) {
				addOutput("Unable to open manual: "+e.getMessage() );
			}
		}
		else {
			go(input);
		}
	}

	public void addOutput(String output) {
		Console.append(output + System.getProperty("line.separator") );
		Console.setCaretPosition(Console.getDocument().getLength());
	}

	public void blockInput() {
		statusBar.setText(resbundle.getString("swing.status.working"));
		Submit.setEnabled(false);
		Command.setEnabled(false);
	}

	public void getInput() {
		Submit.setEnabled(true);
		Command.setEnabled(true);
		Command.requestFocus();
		statusBar.setText(resbundle.getString("swing.status.doneready"));
	}

	public void setVisible(boolean v) {
		super.setVisible(v);
		Command.requestFocus(); // does not work too well
	}

	public JToolBar getToolBar() {
		return toolbarCon;
	}

	public JMenu getMenu() {
		return cConsole;
	}

	public void actionPerformed(ActionEvent a) {

		if (a.getActionCommand().equals("read command")) {

			String input = Command.getText();
			Command.setText("");

			history.add(input);
			pointer = history.size()-1;
			cgo(input);
		}
		else if (a.getActionCommand().equals("run script")) {

			JFileChooser fc = new JFileChooser();
			RiskFileFilter filter = new RiskFileFilter(RiskFileFilter.RISK_SCRIPT_FILES);
			fc.setFileFilter(filter);

			int returnVal = fc.showOpenDialog( RiskUIUtil.findParentFrame(this) );
			if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
				java.io.File file = fc.getSelectedFile();
				// Write your code here what to do with selected file

				try {
					// Testing.append("Opening file: "+ file.getPath() +"\n");
					// Testing.append("Running Script...\n");

					FileReader filein = new FileReader(file);
					BufferedReader bufferin = new BufferedReader(filein);

					String input = bufferin.readLine();
					while(input != null) {

						go(input);
						input = bufferin.readLine();

					}
					bufferin.close();
					// Testing.append("Script end\n");

				}
				catch(Exception error) {
					// Testing.append("Error: "+error.getMessage() + "\n");
				}

			} else {
				// Write your code here what to do if user has canceled Open dialog
			}
		}
		else if (a.getActionCommand().equals("save console")) {
			saveLog(Console);
		}
		else if (a.getActionCommand().equals("clear console")) {
			Console.setText("");
		}
		else if (a.getActionCommand().equals("clear history")) {
			history.clear();
			pointer = -1;
		}
		else {
			SwingGUIPanel.this.actionPerformed(a);
		}
	}
}

class DebugTab extends JSplitPane implements SwingGUITab,ActionListener {

	private JTextArea debugText;
	private JTextArea errText;

	private JToolBar toolbarDebug;
	private JMenu mDebug;

        String cause;

	public JToolBar getToolBar() {
		return toolbarDebug;
	}

	public JMenu getMenu() {
		return mDebug;
	}

	public void sendDebug(String a) {
		debugText.append(a + System.getProperty("line.separator") );
		debugText.setCaretPosition(debugText.getDocument().getLength());
	}

	public DebugTab() {
		super(JSplitPane.HORIZONTAL_SPLIT);

		setName("Debug");

		//##################### Debug ####################


		toolbarDebug = new JToolBar();
		toolbarDebug.setRollover(true);

		JButton tdSaveDebug  = new JButton("Save Debug Log");
		JButton tdPlayDebug  = new JButton("Play Debug Log");
		JButton tdClearDebug = new JButton("Clear Debug Log");
		JButton tdSaveError = new JButton("Save Error Log");
		JButton sendError = new JButton("Send Error Log");

		tdSaveDebug.setActionCommand("save debug");
		tdPlayDebug.setActionCommand("play debug");
		tdClearDebug.setActionCommand("clear debug");
		tdSaveError.setActionCommand("save error");
		sendError.setActionCommand("send error");

		tdSaveDebug.addActionListener(this);
		tdPlayDebug.addActionListener(this);
		tdClearDebug.addActionListener(this);
		tdSaveError.addActionListener(this);
		sendError.addActionListener(this);

                JButton cr = new JButton("Clear Error");
		cr.setActionCommand("clear error");
		cr.addActionListener(this);

                JButton gc = new JButton("GC");
		gc.setActionCommand("gc");
		gc.addActionListener(this);



		toolbarDebug.add(tdSaveDebug);
		toolbarDebug.add(tdPlayDebug);
		toolbarDebug.add(tdClearDebug);
		toolbarDebug.addSeparator();
		toolbarDebug.add(tdSaveError);
		toolbarDebug.add(sendError);
                toolbarDebug.add(cr);
                toolbarDebug.addSeparator();
                toolbarDebug.add(gc);

		toolbarDebug.setFloatable(false);





		mDebug = new JMenu("Debug");
		mDebug.setMnemonic('D');

		JMenuItem dSave = new JMenuItem("Save Debug Log");
		dSave.setMnemonic('S');
		dSave.setActionCommand("save debug");
		dSave.addActionListener( this );
		mDebug.add(dSave);

		JMenuItem dPlay = new JMenuItem("Play Debug Log");
		dPlay.setMnemonic('P');
		dPlay.setActionCommand("play debug");
		dPlay.addActionListener( this );
		mDebug.add(dPlay);

		JMenuItem dClear = new JMenuItem("Clear Debug Log");
		dClear.setMnemonic('C');
		dClear.setActionCommand("clear debug");
		dClear.addActionListener( this );
		mDebug.add(dClear);

		mDebug.addSeparator();

		JMenuItem dSaveErr = new JMenuItem("Save Error Log");
		dSaveErr.setMnemonic('E');
		dSaveErr.setActionCommand("save error");
		dSaveErr.addActionListener( this );
		mDebug.add(dSaveErr);

		JMenuItem send = new JMenuItem("Send Error Log");
		send.setMnemonic('S');
		send.setActionCommand("send error");
		send.addActionListener( this );
		mDebug.add(send);

		//mDebug.addSeparator();

		//JMenuItem aiwait = new JMenuItem("Change AI wait");
		//aiwait.setMnemonic('A');
		//aiwait.setActionCommand("aiwait");
		//aiwait.addActionListener( this );
		//mDebug.add(aiwait);






		debugText = new JTextArea();
		debugText.setEditable(false);

		JScrollPane debugScroll = new JScrollPane(debugText);
		debugScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//debugScroll.setBorder( new javax.swing.border.TitledBorder( goodBorder, "Debug Log", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP ) );

		JPanel debugPanel = new JPanel( new BorderLayout() );
		debugPanel.add( new JLabel("  Debug Log"), BorderLayout.NORTH );
		debugPanel.add(debugScroll);

		// ####### err

		errText = new JTextArea();
		errText.setEditable(false);

		JScrollPane errScroll = new JScrollPane( errText );
		//errScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//errScroll.setBorder( BorderFactory.createTitledBorder( BorderFactory.createLoweredBevelBorder() , "Error Log", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP ) );

		JPanel errPanel = new JPanel( new BorderLayout() );
		errPanel.add( new JLabel("  Error Log"), BorderLayout.NORTH );
		errPanel.add(errScroll);

		// ######## split

		debugPanel.setOpaque(false);
		errPanel.setOpaque(false);

		setLeftComponent(debugPanel);
		setRightComponent(errPanel);

		setContinuousLayout(true);
		setOneTouchExpandable(true);
		setDividerLocation(GraphicsUtil.scale(400));
		setBorder( BorderFactory.createEmptyBorder() );

		setOpaque(false);
	}

        public void start() {
		int size = 16;
		Image img = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.RED );
		g.fillOval(0,0,size,size);
		g.dispose();
		final Icon icon = new ImageIcon(img);

                if (RiskUIUtil.checkForNoSandbox()) {

                    try {
                        // Could not open/create prefs root node Software\JavaSoft\Prefs at root 0x80000002. Windows RegCreateKeyEx(...) returned error code 5.
                        // HACK this will print any problems loading the Preferences before we start grasshopper
                        java.util.prefs.Preferences.userRoot(); // returns java.util.prefs.WindowsPreferences
                    }
                    catch (Throwable th) { }

                    net.yura.grasshopper.BugManager.interceptAndAlert(new Writer() {
                        public void write(char[] cbuf, int off, int len) {
                            // TODO this will throw Error if current thread is interrupted
                            errText.append(String.valueOf(cbuf, off, len));
                        }
                        public void flush() { }
                        public void close() { }
                    }, new net.yura.grasshopper.BugManager() {
                        public void action(String thecause) {
                                cause = thecause;
                                int nom = tabbedpane.indexOfComponent(DebugTab.this);

                                if (tabbedpane.getIconAt(nom)==null) {
                                    tabbedpane.setIconAt(nom,icon);
                                }
                        }
                    });

                    try {
                        net.yura.swingme.core.CoreUtil.setupLogging();
                    }
                    catch (Throwable th) {
                        RiskUtil.printStackTrace(th);
                    }
		}
        }

	public void actionPerformed(ActionEvent a) {

		if (a.getActionCommand().equals("play debug")) {

			JFileChooser fc = new JFileChooser();
			RiskFileFilter filter = new RiskFileFilter(RiskFileFilter.RISK_LOG_FILES);
			fc.setFileFilter(filter);

			int returnVal = fc.showOpenDialog( RiskUIUtil.findParentFrame(this) );
			if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
				java.io.File file = fc.getSelectedFile();
				String fileName = file.getAbsolutePath();

				go("newgame");
				go("play " + fileName);
			}
		}
		else if (a.getActionCommand().equals("save debug")) {

                        RiskGame game = myrisk.getGame();
                        if (game == null) {
                            saveLog(debugText);
                        }
                        else {
                            File logFile = getNewLogFile();
                            if (logFile != null) {
                                try {
                                    RiskUtil.saveGameLog(logFile, game);
                                }
                                catch(Exception error) {
                                    showError( error.getMessage() );
                                }
                            }
                        }
		}
		else if (a.getActionCommand().equals("clear debug")) {
			debugText.setText("");
		}
		else if (a.getActionCommand().equals("save error")) {
			saveLog(errText);
		}
		else if (a.getActionCommand().equals("send error")) {

                        String email = JOptionPane.showInputDialog(this,"tell me your e-mail please");

                        if (email == null) { email ="none"; }

                        submitBug(null, email, "Bug", null, cause);
		}
                else if (a.getActionCommand().equals("clear error")) {
                    errText.setText("");
                    tabbedpane.setIconAt(tabbedpane.indexOfComponent(this), null);
                }
                else if (a.getActionCommand().equals("gc")) {
                    System.gc();
                }
		else {
			throw new RuntimeException("command \""+a.getActionCommand()+"\" is not implemented yet\n");
		}
	}

        private String getDebugText() {
            return debugText.getText();
        }

        private String getErrText() {
            return errText.getText();
        }
}

class StatisticsTab extends JPanel implements SwingGUITab,ActionListener {

	private JToolBar toolbarStat;
	private JMenu sStatistics;
	private StatsPanel graph;

	private AbstractButton[] statbuttons;

	public JToolBar getToolBar() {
		return toolbarStat;
	}

	public JMenu getMenu() {
		return sStatistics;
	}

	public void actionPerformed(ActionEvent a) {
		graph.repaintStats(StatType.fromOrdinal(Integer.parseInt(a.getActionCommand())));
		graph.repaint();
	}

	public StatisticsTab() {

		setName( resbundle.getString("swing.tab.statistics") );

		//##################### graph ####################

		toolbarStat = new JToolBar();
		toolbarStat.setRollover(true);

		toolbarStat.setFloatable(false);

		graph = new StatsPanel(myrisk);
		graph.setBorder( BorderFactory.createLoweredBevelBorder() );

		// create Statistics menu item
		sStatistics = new JMenu( resbundle.getString("swing.tab.statistics") );
		sStatistics.setMnemonic('S');

                StatType[] stats = StatType.values();

		statbuttons = new AbstractButton[stats.length*2];
		for (int a=0; a<stats.length; a++) {
                        StatType stat = stats[a];
                        String text = resbundle.getString("swing.toolbar."+stat.getName() );

                        JButton button = new JButton(text);
			button.setActionCommand( String.valueOf(stat.ordinal()) );
			button.addActionListener(this);
			button.setEnabled(false);
                        toolbarStat.add(button);

                        JMenuItem menuItem = new JMenuItem(text);
			menuItem.setActionCommand( String.valueOf(stat.ordinal()) );
			menuItem.addActionListener(this);
			menuItem.setEnabled(false);
			sStatistics.add(menuItem);

                        statbuttons[a] = button;
                        statbuttons[a+stats.length] = menuItem;
		}

		setLayout( new BorderLayout() );
		add(graph);
	}

	public void startGame() {
            for (int a=0; a<statbuttons.length; a++) {
                statbuttons[a].setEnabled(true);
            }
	}

	public void closeGame() {
            for (int a=0; a<statbuttons.length; a++) {
                statbuttons[a].setEnabled(false);
            }
	}
}
}

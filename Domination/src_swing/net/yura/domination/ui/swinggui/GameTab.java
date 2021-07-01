package net.yura.domination.ui.swinggui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import net.yura.domination.engine.ColorUtil;
import net.yura.domination.engine.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.guishared.BadgeButton;
import net.yura.domination.engine.guishared.MapMouseListener;
import net.yura.domination.engine.guishared.PicturePanel;
import net.yura.domination.engine.guishared.RiskFileFilter;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.swing.GraphicsUtil;
import net.yura.swing.ImageIcon;
import net.yura.swing.JTable;

public class GameTab extends JPanel implements SwingGUITab, ActionListener {

    	private ResourceBundle resbundle = TranslationBundle.getBundle();
	private SwingGUIPanel swingGUIPanel;

	private JPanel Pix;
	private JPanel guiGame;

	private JLabel gameStatus;
	private JMenu gGame;
	private JToolBar toolbarGUI;

	private boolean serverOn;

	private JButton gNewGame;
	private JButton gLoadGame;
	private JButton gSaveGame;
	private JButton gCloseGame;

	private JButton gJoinGame;
	private JButton gStartServer;

	private JButton gOptions;
	private JMenuItem gmOptions;
	private JMenuItem gmReplay;

	private JMenuItem gmStartServer;
	private JMenuItem gmJoinGame;

	private JMenuItem gmNewGame;
	private JMenuItem gmLoadGame;
	private JMenuItem gmSaveGame;
	private JMenuItem gmCloseGame;
        
	private JButton showMission;
	private JButton showCards;
	private JButton Undo;

	private JButton autoplace;
	private JPanel gameOptions;
        private JLabel capitalLabel;

	private JTextField country1;
	private JTextField country2;

	private JSlider moveNumber;
	private boolean localGame;

	private JPanel inGameInput;
	private CardLayout inGameCards;
	private JComboBox mapViewComboBox;

	SetupPanel guiSetup;
	JLabel resultsLabel;

	JLabel attacker;
	JLabel armies;
        
	JSlider slider;
        
	JButton roll1;
	JButton roll2;
	JButton roll3;

	JPanel defend;
	JPanel roll;
	winnerPanel winner;
	tradeCardsPanel tradeCards;

	public GameTab(SwingGUIPanel swingGUI) {
		swingGUIPanel = swingGUI;

		setName( resbundle.getString("swing.tab.game") );


		serverOn=false;


		// ################### GUI #######################



		gameStatus = new JLabel("");

		Pix = new JPanel( new BorderLayout() );
		Pix.setOpaque(false);

		// cant use this as it goes even worse in xp
		//lobby.setContentAreaFilled(false);

		JLabel pixlogo = new JLabel("yura.net " + SwingGUIPanel.product + ", " + RiskUtil.GAME_NAME + " IDE", new ImageIcon(this.getClass().getResource("about.jpg")), JLabel.CENTER);
		pixlogo.setHorizontalTextPosition( JLabel.CENTER );
		pixlogo.setVerticalTextPosition( JLabel.TOP );

		Pix.add( swingGUIPanel.lobby , BorderLayout.NORTH );
		Pix.add( pixlogo );


                JButton donate = new JButton();
		donate.addActionListener( this );
		donate.setActionCommand("donate");
                donate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                URL donateNow = this.getClass().getResource("donate.png");
                if (donateNow!=null) {
                     donate.setIcon( new ImageIcon( donateNow ) );
                }
                else {
                    donate.setText("Donate");
                }
                Pix.add(donate, BorderLayout.SOUTH );


		setLayout(new java.awt.BorderLayout());

		add(Pix, java.awt.BorderLayout.CENTER );

		add(gameStatus, java.awt.BorderLayout.SOUTH);

		gameStatus.setBorder( BorderFactory.createLoweredBevelBorder() );

		Insets margin = new Insets(2,2,2,2);


		showMission = new JButton(resbundle.getString("swing.button.mission"));
		showMission.setToolTipText(resbundle.getString("game.button.mission"));
		showMission.addActionListener( this );
		showMission.setActionCommand("showmission");
		showMission.setMargin(margin);

		showCards = new JButton(resbundle.getString("swing.button.cards"));
		showCards.setToolTipText(resbundle.getString("game.button.cards"));
		showCards.addActionListener( this );
		showCards.setActionCommand("showcards");
		showCards.setMargin(margin);

		Undo = new JButton(resbundle.getString("swing.button.undo"));
		Undo.setToolTipText(resbundle.getString("game.button.undo"));
		Undo.addActionListener( this );
		Undo.setActionCommand("undo");
		Undo.setMargin(margin);

		JButton viewContinents = new JButton(resbundle.getString("swing.button.continents"));
		viewContinents.addActionListener( this );
		viewContinents.setActionCommand("continents");
		viewContinents.setMargin(margin);

		moveNumber = new JSlider();
		slider = new JSlider();

		armies = new JLabel();

		toolbarGUI = new JToolBar();
		toolbarGUI.setRollover(true);



		autoplace = new JButton(resbundle.getString("game.button.go.autoplace"));


		Dimension gameOptionsSize = GraphicsUtil.newDimension(PicturePanel.PP_X, 25);

		gameOptions = makeGameOptionsPanel();
		gameOptions.setPreferredSize(gameOptionsSize);
		gameOptions.setMinimumSize(gameOptionsSize);
		gameOptions.setMaximumSize(gameOptionsSize);


		gameOptions.add(viewContinents);


		guiSetup = new SetupPanel();
		guiGame = new GamePanel();

		guiSetup.setOpaque(false);
		guiGame.setOpaque(false);





		// make toolbar

		gNewGame		= new JButton(resbundle.getString("swing.menu.new"));
		gLoadGame		= new JButton(resbundle.getString("swing.menu.load"));
		gSaveGame		= new JButton(resbundle.getString("swing.menu.save"));
		gCloseGame		= new JButton(resbundle.getString("swing.menu.close"));

		gOptions		= new JButton(resbundle.getString("swing.menu.options"));

		gJoinGame		= new JButton(resbundle.getString("swing.menu.joingame"));
		gStartServer		= new JButton(resbundle.getString("swing.menu.startserver"));

		JButton gManual		= new JButton(resbundle.getString("swing.menu.manual"));
		JButton gAbout		= new JButton(resbundle.getString("swing.menu.about"));
		JButton gQuit		= new JButton(resbundle.getString("swing.menu.quit"));

		gNewGame.setActionCommand("new game");
		gNewGame.addActionListener( this );
		gLoadGame.setActionCommand("load game");
		gLoadGame.addActionListener( this );
		gSaveGame.setActionCommand("save game");
		gSaveGame.addActionListener( this );
		gCloseGame.setActionCommand("close game");
		gCloseGame.addActionListener( this );
		gOptions.setActionCommand("options");
		gOptions.addActionListener( this );
		gManual.setActionCommand("manual");
		gManual.addActionListener( this );
		gAbout.setActionCommand("about");
		gAbout.addActionListener( this );
		gQuit.setActionCommand("quit");
		gQuit.addActionListener( this );

		gStartServer.setActionCommand("start server");
		gStartServer.addActionListener( this );
		gJoinGame.setActionCommand("join game");
		gJoinGame.addActionListener( this );



		toolbarGUI.add(gNewGame);
		toolbarGUI.add(gLoadGame);
		toolbarGUI.add(gSaveGame);
		toolbarGUI.add(gCloseGame);
		toolbarGUI.addSeparator();
		toolbarGUI.add(gJoinGame);
		toolbarGUI.add(gStartServer);
		toolbarGUI.addSeparator();
		toolbarGUI.add(gOptions);
		toolbarGUI.add(gManual);
		toolbarGUI.add(gAbout);
		if (RiskUIUtil.checkForNoSandbox()) { toolbarGUI.add(gQuit); }

		toolbarGUI.setFloatable(false);






		// create Game menu item
		gGame = new JMenu(resbundle.getString("swing.menu.game"));
		gGame.setMnemonic('G');

		gmNewGame = new JMenuItem(resbundle.getString("swing.menu.new"));
		gmNewGame.setMnemonic('N');
		gmNewGame.setActionCommand("new game");
		gmNewGame.addActionListener( this );
		gGame.add(gmNewGame);

		gmLoadGame = new JMenuItem(resbundle.getString("swing.menu.load"));
		gmLoadGame.setMnemonic('L');
		gmLoadGame.setActionCommand("load game");
		gmLoadGame.addActionListener( this );
		gGame.add(gmLoadGame);

		gmSaveGame = new JMenuItem(resbundle.getString("swing.menu.save"));
		gmSaveGame.setMnemonic('S');
		gmSaveGame.setActionCommand("save game");
		gmSaveGame.addActionListener( this );
		gGame.add(gmSaveGame);

		gmCloseGame = new JMenuItem(resbundle.getString("swing.menu.close"));
		gmCloseGame.setMnemonic('C');
		gmCloseGame.setActionCommand("close game");
		gmCloseGame.addActionListener( this );
		gGame.add(gmCloseGame);

		gGame.addSeparator();

		gmJoinGame = new JMenuItem(resbundle.getString("swing.menu.joingame"));
		gmJoinGame.setMnemonic('J');
		gmJoinGame.setActionCommand("join game");
		gmJoinGame.addActionListener( this );
		gGame.add(gmJoinGame);

		gmStartServer = new JMenuItem(resbundle.getString("swing.menu.startserver"));
		gmStartServer.setMnemonic('V');
		gmStartServer.setActionCommand("start server");
		gmStartServer.addActionListener( this );
		gGame.add(gmStartServer);

		gGame.addSeparator();

		gmOptions = new JMenuItem(resbundle.getString("swing.menu.options"));
		gmOptions.setMnemonic('O');
		gmOptions.setActionCommand("options");
		gmOptions.addActionListener( this );
		gGame.add(gmOptions);

		gmReplay = new JMenuItem("Replay");
		gmReplay.setMnemonic('R');
		gmReplay.setActionCommand("replay");
		gmReplay.addActionListener( this );
		gGame.add(gmReplay);

		if (RiskUIUtil.checkForNoSandbox()) {

			gGame.addSeparator();

			JMenuItem gmQuit = new JMenuItem(resbundle.getString("swing.menu.quit"));
			gmQuit.setMnemonic('Q');
			gmQuit.setActionCommand("quit");
			gmQuit.addActionListener( this );
			gGame.add(gmQuit);
		}





		roll1 = new JButton(resbundle.getString("swing.dice.roll1"));
		roll2 = new JButton(resbundle.getString("swing.dice.roll2"));
		roll3 = new JButton(resbundle.getString("swing.dice.roll3"));

		roll1.setActionCommand("roll 1");
		roll2.setActionCommand("roll 2");
		roll3.setActionCommand("roll 3");

		roll1.addActionListener(this);
		roll2.addActionListener(this);
		roll3.addActionListener(this);


		gOptions.setEnabled(false);
		gmOptions.setEnabled(false);
		gmReplay.setEnabled(false);

		gSaveGame.setEnabled(false);
		gCloseGame.setEnabled(false);

		gmSaveGame.setEnabled(false);
		gmCloseGame.setEnabled(false);

		setOpaque(false);
	}

	public JMenu getMenu() {
		return gGame;
	}

	public JToolBar getToolBar() {
		return toolbarGUI;
	}

	public void actionPerformed(ActionEvent a) {

                String actionCommand = a.getActionCommand();

		if ("showmission".equals(actionCommand)) {
			showMission( swingGUIPanel.myrisk.getCurrentMission() );
		}
		else if ("showcards".equals(actionCommand)) {
			openCards();
		}
		else if ("undo".equals(actionCommand)) {

                        swingGUIPanel.pp.setC1(PicturePanel.NO_COUNTRY);
                        swingGUIPanel.pp.setC2(PicturePanel.NO_COUNTRY);

			swingGUIPanel.go("undo");
		}
		else if ("continents".equals(actionCommand)) {

			StringBuffer buffer = new StringBuffer();
			buffer.append("<html><table>");

			Continent[] continents = swingGUIPanel.myrisk.getGame().getContinents();

			for (int c=0;c<continents.length;c++) {

				Continent continent = continents[c];

				buffer.append("<tr style=\"background-color: ");
				buffer.append(ColorUtil.getHexForColor(continent.getColor()));
				buffer.append("; color:");
				buffer.append(ColorUtil.getHexForColor(ColorUtil.getTextColorFor(continent.getColor())));
				buffer.append("\"><td>");
				buffer.append(continent.getName());
				buffer.append("</td><td> - </td><td>");
				buffer.append(continent.getArmyValue());
				buffer.append("</td></tr>");

			}

			buffer.append("</table></html>");

			JOptionPane.showMessageDialog(this, buffer.toString(), resbundle.getString("swing.button.continents"), JOptionPane.PLAIN_MESSAGE );

		}
		else if ("roll 1".equals(actionCommand)) {
			swingGUIPanel.go("roll 1");
		}
		else if ("roll 2".equals(actionCommand)) {
			swingGUIPanel.go("roll 2");
		}
		else if ("roll 3".equals(actionCommand)) {
			swingGUIPanel.go("roll 3");
		}
		else if ("join game".equals(actionCommand)) {
			String result = JOptionPane.showInputDialog(RiskUIUtil.findParentFrame(this), "type the server name", swingGUIPanel.myrisk.getRiskConfig("default.host") );
			if (result!=null) { swingGUIPanel.go("join "+result); }
		}
		else if ("new game".equals(actionCommand)) {
			swingGUIPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			swingGUIPanel.go("newgame");
		}
		else if ("load game".equals(actionCommand)) {

			String name = RiskUIUtil.getLoadFileName(
				RiskUIUtil.findParentFrame(this)
				//RiskUtil.SAVES_DIR,
				//RiskFileFilter.RISK_SAVE_FILES
			);

			if (name!=null) {
				swingGUIPanel.go("loadgame " + name );
			}
		}
		else if ("save game".equals(actionCommand)) {

			String name = RiskUIUtil.getSaveFileName(
				RiskUIUtil.findParentFrame(this)
				//RiskUtil.SAVES_DIR,
				//RiskFileFilter.RISK_SAVE_FILES
			);

			if (name!=null) {
				swingGUIPanel.go("savegame " + name );
			}
		}
		else if ("close game".equals(actionCommand)) {
			//if (localGame) {
				swingGUIPanel.go("closegame");
			//}
			//else {
			//	go("leave");
			//}
		}
		else if ("options".equals(actionCommand)) {

			Object[] message = new Object[2];
			message[0] = new JCheckBox("Auto End Go");
			message[1] = new JCheckBox("Auto Defend");


			((JCheckBox)message[0]).setSelected( swingGUIPanel.myrisk.getAutoEndGo() );
			((JCheckBox)message[1]).setSelected( swingGUIPanel.myrisk.getAutoDefend() );

			String[] options = {
			    "OK",
			    "cancel"
			};

			int result = JOptionPane.showOptionDialog(
			    this,                             // the parent that the dialog blocks
			    message,                                    // the dialog message array
			    "Options", // the title of the dialog window
			    JOptionPane.OK_CANCEL_OPTION,                 // option type
			    JOptionPane.PLAIN_MESSAGE,            // message type
			    null,                                       // optional icon, use null to use the default icon
			    options,                                    // options string array, will be made into buttons
			    options[0]                                  // option that should be made into a default button
			);

			if (result == JOptionPane.OK_OPTION ) {

                                swingGUIPanel.myrisk.parser( "autodefend " + ((((JCheckBox)message[1]).isSelected()) ? "on" : "off") );
                                // "autoendgo on" may trigger the end of my go
				swingGUIPanel.myrisk.parser( "autoendgo " + ((((JCheckBox)message[0]).isSelected()) ? "on" : "off") );

			}
/*

			Frame frame = Risk.findParentFrame(this);

			OptionsDialog optionsDialog = new OptionsDialog( frame , true, myrisk);
			Dimension frameSize = frame.getSize();
			Dimension optionsSize = optionsDialog.getPreferredSize();
			int x = frame.getLocation().x + (frameSize.width - optionsSize.width) / 2;
			int y = frame.getLocation().y + (frameSize.height - optionsSize.height) / 2;
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			optionsDialog.setLocation(x, y);
			optionsDialog.setVisible(true);
*/
		}
		else if ("replay".equals(actionCommand)) {
			swingGUIPanel.go("replay");
		}
		else if ("start server".equals(actionCommand)) {
			if (serverOn) {
				swingGUIPanel.go("killserver");
			}
			else {
				swingGUIPanel.go("startserver");
			}
		}
		else if ("lobby".equals(actionCommand)) {
			RiskUIUtil.runLobby(swingGUIPanel.myrisk);
		}
                else if ("donate".equals(actionCommand)) {
                        RiskUIUtil.donate(this);
                }
		else {
			swingGUIPanel.actionPerformed(a);
		}
	}
        
	public void showMission(String mission) {
		JOptionPane.showMessageDialog(this, resbundle.getString("swing.message.mission") + " " + mission, resbundle.getString("swing.title.mission"), JOptionPane.INFORMATION_MESSAGE);
	}
        
	public void openCards() {

		Frame frame = RiskUIUtil.findParentFrame(this);

		CardsDialog cardsDialog = new CardsDialog(frame, swingGUIPanel.pp, true, swingGUIPanel.myrisk, (swingGUIPanel.gameState == 1));
		Dimension frameSize = frame.getSize();
		Dimension aboutSize = cardsDialog.getPreferredSize();
		int x = frame.getLocation().x + (frameSize.width - aboutSize.width) / 2;
		int y = frame.getLocation().y + (frameSize.height - aboutSize.height) / 2;
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		cardsDialog.setLocation(x, y);

		cardsDialog.populate(swingGUIPanel.myrisk.getCurrentCards());

		cardsDialog.setVisible(true);
	}

	public void blockInput() {

		gSaveGame.setEnabled(false);
		gmSaveGame.setEnabled(false);

		showMission.setEnabled(false);
		showCards.setEnabled(false);
		Undo.setEnabled(false);

		gOptions.setEnabled(false);
		gmOptions.setEnabled(false);
		gmReplay.setEnabled(false);

		// this is so close is not selected
		mapViewComboBox.grabFocus();

	}

	public void getInput() {

		if (localGame) {
			gSaveGame.setEnabled(true);
			gmSaveGame.setEnabled(true);
                        if (swingGUIPanel.myrisk.getGame().getState()!=RiskGame.STATE_DEFEND_YOURSELF) {
                            Undo.setEnabled(true);
                        }
			gmReplay.setEnabled(true);
		}

		showMission.setEnabled(true);
		showCards.setEnabled(true);

		gOptions.setEnabled(true);
		gmOptions.setEnabled(true);

	}

	public void newGame() {

            // if we do not use this here we get ClassCastException in java 1.7
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {

			gNewGame.setEnabled(false);
			gLoadGame.setEnabled(false);
			//gSaveGame.setEnabled(true);
			gCloseGame.setEnabled(true);

			gmNewGame.setEnabled(false);
			gmLoadGame.setEnabled(false);
			gmStartServer.setEnabled(false);
			gmJoinGame.setEnabled(false);

			gStartServer.setEnabled(false);
			gJoinGame.setEnabled(false);

			//gmSaveGame.setEnabled(true);
			gmCloseGame.setEnabled(true);

			remove(Pix);
			remove(guiGame);

			guiSetup.setupGame();

			add(guiSetup, java.awt.BorderLayout.CENTER );

			swingGUIPanel.setCursor(null); // Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                }
            } );
	}

	public void closeGame() {
			gNewGame.setEnabled(true);
			gLoadGame.setEnabled(true);
			gSaveGame.setEnabled(false);
			gCloseGame.setEnabled(false);

			gmNewGame.setEnabled(true);
			gmLoadGame.setEnabled(true);
			gmStartServer.setEnabled(true);
			gmJoinGame.setEnabled(true);

			gStartServer.setEnabled(true);
			gJoinGame.setEnabled(true);

			gmSaveGame.setEnabled(false);
			gmCloseGame.setEnabled(false);

			gOptions.setEnabled(false);
			gmOptions.setEnabled(false);
			gmReplay.setEnabled(false);

			remove(guiGame);
			remove(guiSetup);

			add(Pix, java.awt.BorderLayout.CENTER );

                        swingGUIPanel.pp.stopAni(); // stop anmations
	}

	public void startGame() {

            // if we do not use this here we get ClassCastException in java 1.7 when we load a saved game
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {

			gNewGame.setEnabled(false);
			gLoadGame.setEnabled(false);
			//gSaveGame.setEnabled(true);
			gCloseGame.setEnabled(true);

			gmNewGame.setEnabled(false);
			gmLoadGame.setEnabled(false);
			gmStartServer.setEnabled(false);
			gmJoinGame.setEnabled(false);

			gStartServer.setEnabled(false);
			gJoinGame.setEnabled(false);

			//gmSaveGame.setEnabled(true);
			gmCloseGame.setEnabled(true);

			remove(Pix);
			remove(guiSetup);

			add(guiGame, java.awt.BorderLayout.CENTER );
                }
            } );
	}

	public void serverState(boolean s) {
			serverOn=s;

			if (serverOn) {
				gmStartServer.setText(resbundle.getString("swing.menu.stopserver"));
				gStartServer.setText(resbundle.getString("swing.menu.stopserver"));
			}
			else {
				gmStartServer.setText(resbundle.getString("swing.menu.startserver"));
				gStartServer.setText(resbundle.getString("swing.menu.startserver"));
			}
	}

	public void setGameStatus(String state) {
		gameStatus.setText(" "+state);
	}
        
	public JPanel makeGameOptionsPanel() {

			JPanel gameOptionsPanel = new JPanel();

			gameOptionsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 0));

			gameOptionsPanel.setOpaque(false);

			JLabel mapLookLabel = new JLabel(resbundle.getString("game.tabs.mapview") + ":");

			mapViewComboBox = new JComboBox();

			Dimension mapViewSize = GraphicsUtil.newDimension(120, 20);

			mapViewComboBox.setPreferredSize(mapViewSize);
			mapViewComboBox.setMinimumSize(mapViewSize);
			mapViewComboBox.setMaximumSize(mapViewSize);

			mapViewComboBox.addItem(resbundle.getString("game.tabs.continents"));
			mapViewComboBox.addItem(resbundle.getString("game.tabs.ownership"));
			mapViewComboBox.addItem(resbundle.getString("game.tabs.borderthreat"));
			mapViewComboBox.addItem(resbundle.getString("game.tabs.cardownership"));
			mapViewComboBox.addItem(resbundle.getString("game.tabs.troopstrength"));
			mapViewComboBox.addItem(resbundle.getString("game.tabs.connectedempire"));

			mapViewComboBox.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.pprepaintCountries();
							swingGUIPanel.pp.repaint();
						}
					}
			);

			JLabel playersLabel = new JLabel(resbundle.getString("newgame.label.players"));

			Dimension playerPanelSize = GraphicsUtil.newDimension(120, 20);

			JPanel players = new playersPanel();

			players.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0),1));

			players.setPreferredSize(playerPanelSize);
			players.setMinimumSize(playerPanelSize);
			players.setMaximumSize(playerPanelSize);

			gameOptionsPanel.add(mapLookLabel);
			gameOptionsPanel.add(mapViewComboBox);
			gameOptionsPanel.add(playersLabel);
			gameOptionsPanel.add(players);
			gameOptionsPanel.add(showMission);
			gameOptionsPanel.add(showCards);
			gameOptionsPanel.add(Undo);

			return gameOptionsPanel;
	}

	void showPanel(String name) {
		inGameCards.show(inGameInput, name);
	}

	void setLocalGame(boolean lg) {
		localGame = lg;
	}

	String getSelectedMapView() {
		return (String)mapViewComboBox.getSelectedItem();
	}

	//############################################################################################################

	class playersPanel extends JPanel {

		public void paintComponent(Graphics g) {

                        int width = getWidth();
                        int height = getHeight();
			int[] colors = swingGUIPanel.myrisk.getPlayerColors();

                        if (colors.length==0) return;

			for (int c=0; c < colors.length ; c++) {
				g.setColor( new Color( colors[c] ) );
				g.fillRect(((width / colors.length) * c), 0, width / colors.length, height);
			}

			g.setColor( new Color( ColorUtil.getTextColorFor( colors[0] ) ) );

			g.drawRect( 2 , 2 , (width/colors.length)-5 , height - 5);

			g.setColor( Color.black );
			g.drawLine((width / colors.length) - 1, 0, (width / colors.length) - 1, height - 1);
		}
	}

	class GamePanel extends JPanel {

		public GamePanel() {

			resultsLabel = new JLabel("RESULTS");
                        resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);

			Dimension mapSize = GraphicsUtil.newDimension(PicturePanel.PP_X, PicturePanel.PP_Y);

			swingGUIPanel.pp.setPreferredSize(mapSize);
			swingGUIPanel.pp.setMinimumSize(mapSize);
			swingGUIPanel.pp.setMaximumSize(mapSize);

                        final MapMouseListener mml = new MapMouseListener(swingGUIPanel.myrisk,swingGUIPanel.pp);
                        MouseInputAdapter mapListener = new MouseInputAdapter() {
                            public void mouseExited(MouseEvent e) {
                                mml.mouseExited();
                            }
                            public void mouseReleased(MouseEvent e) {
                                int[] click = mml.mouseReleased(e.getX(),e.getY(),swingGUIPanel.gameState);
                                if (click!=null) {
                                    mapClick(click,e);
                                }
                            }
                            public void mouseMoved(MouseEvent e) {
                                mml.mouseMoved(e.getX(),e.getY(),swingGUIPanel.gameState);
                            }
                        };


			swingGUIPanel.pp.addMouseListener(mapListener);
			swingGUIPanel.pp.addMouseMotionListener(mapListener);

			Dimension d = GraphicsUtil.newDimension(PicturePanel.PP_X, 50);

			inGameCards = new CardLayout();

			inGameInput = new JPanel();
			inGameInput.setLayout( inGameCards );
			inGameInput.setPreferredSize(d);
			inGameInput.setMinimumSize(d);
			inGameInput.setMaximumSize(d);

			JLabel nothing = new JLabel(resbundle.getString("game.pleasewaitnetwork"));
                        nothing.setHorizontalAlignment(SwingConstants.CENTER);
			nothing.setPreferredSize(d);
			nothing.setMinimumSize(d);
			nothing.setMaximumSize(d);

			resultsLabel.setPreferredSize(d);
			resultsLabel.setMinimumSize(d);
			resultsLabel.setMaximumSize(d);

			JPanel placeArmies = new placeArmiesPanel();
			placeArmies.setPreferredSize(d);
			placeArmies.setMinimumSize(d);
			placeArmies.setMaximumSize(d);

			roll = new rollPanel();
			roll.setPreferredSize(d);
			roll.setMinimumSize(d);
			roll.setMaximumSize(d);

			JPanel move = new movePanel();
			move.setPreferredSize(d);
			move.setMinimumSize(d);
			move.setMaximumSize(d);

			JPanel attack = new attackPanel();
			attack.setPreferredSize(d);
			attack.setMinimumSize(d);
			attack.setMaximumSize(d);

			defend = new defendPanel();
			defend.setPreferredSize(d);
			defend.setMinimumSize(d);
			defend.setMaximumSize(d);

			JPanel tacMove = new tacMovePanel();
			tacMove.setPreferredSize(d);
			tacMove.setMinimumSize(d);
			tacMove.setMaximumSize(d);

			JPanel capital = new capitalPanel();
			capital.setPreferredSize(d);
			capital.setMinimumSize(d);
			capital.setMaximumSize(d);

			tradeCards = new tradeCardsPanel();
			tradeCards.setPreferredSize(d);
			tradeCards.setMinimumSize(d);
			tradeCards.setMaximumSize(d);

			winner = new winnerPanel();
			winner.setPreferredSize(d);
			winner.setMinimumSize(d);
			winner.setMaximumSize(d);

			JPanel endgo = new endgoPanel();
			endgo.setPreferredSize(d);
			endgo.setMinimumSize(d);
			endgo.setMaximumSize(d);

			inGameInput.setOpaque(false);

			placeArmies.setOpaque(false);
			roll.setOpaque(false);
			move.setOpaque(false);
			attack.setOpaque(false);
			defend.setOpaque(false);
			tacMove.setOpaque(false);
			capital.setOpaque(false);
			tradeCards.setOpaque(false);
			winner.setOpaque(false);
			endgo.setOpaque(false);

			inGameInput.add(nothing, "nothing");
			inGameInput.add(placeArmies, "placeArmies");
			inGameInput.add(roll, "roll");
			inGameInput.add(move, "move");
			inGameInput.add(attack, "attack");
			inGameInput.add(defend, "defend");
			inGameInput.add(tacMove, "tacMove");
			inGameInput.add(capital, "capital");
			inGameInput.add(tradeCards, "tradeCards");
			inGameInput.add(winner, "winner");
			inGameInput.add(endgo, "endgo");
			inGameInput.add(resultsLabel, "results");

			// ################### IN GAME #######################
/*

			this.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(gameOptions, c);

			c.gridx = 0; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(pp, c);

			c.gridx = 0; // col
			c.gridy = 2; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(inGameInput, c);

*/
			JPanel ppBorder = new JPanel( new BorderLayout() );
			ppBorder.setBorder(
				BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(5,10,5,10),
					BorderFactory.createLineBorder(Color.BLACK,1)
				)
			);
			ppBorder.setOpaque(false);
			ppBorder.add(swingGUIPanel.pp);

			setLayout(new BorderLayout());
			add(gameOptions, BorderLayout.NORTH);
			add(ppBorder);
			add(inGameInput, BorderLayout.SOUTH);

			setBorder( BorderFactory.createEmptyBorder(5,0,5,0) );
		}

		//**********************************************************************
		//                     MouseListener Interface
		//**********************************************************************

		public void mapClick(int[] countries,MouseEvent e) {

                    if (swingGUIPanel.gameState == RiskGame.STATE_PLACE_ARMIES) {
                        if (countries.length==1) {
                            if ( e.getModifiers() == java.awt.event.InputEvent.BUTTON1_MASK ) {
                                swingGUIPanel.go( "placearmies " + countries[0] + " 1" );
                            }
                            else {
                                swingGUIPanel.go( "placearmies " + countries[0] + " 10" );
                            }
                        }
                    }
                    else if (swingGUIPanel.gameState == RiskGame.STATE_ATTACKING) {
                            if (countries.length==0) {
                                attacker.setText(resbundle.getString("game.note.selectattacker"));
                            }
                            else if (countries.length == 1) {
                                attacker.setText(resbundle.getString("game.note.attackerisseldefender").replaceAll( "\\{0\\}", swingGUIPanel.myrisk.getCountryName( countries[0])));
                            }
                            else {
                                swingGUIPanel.go("attack " + countries[0] + " " + countries[1]);
                            }

                    }
                    else if (swingGUIPanel.gameState == RiskGame.STATE_FORTIFYING) {
                            if (countries.length==0) {
                                    country1.setText("");
                            }
                            else if (countries.length == 1) {
                                    country1.setText( swingGUIPanel.myrisk.getCountryName( countries[0]) );
                                    country2.setText("");
                            }
                            else if (countries.length == 2) {
                                    country2.setText( swingGUIPanel.myrisk.getCountryName( countries[1]) );
                            }
                    }
                    else if (swingGUIPanel.gameState == RiskGame.STATE_SELECT_CAPITAL) {
                            if (countries.length==0) {
                                capitalLabel.setText( resbundle.getString("core.help.selectcapital") );
                            }
                            else if (countries.length == 1) {
                                capitalLabel.setText( resbundle.getString("core.help.selectcapital")+": "+swingGUIPanel.myrisk.getCountryName( countries[0]) );
                            }
                    }
                }
        }

	class SetupPanel extends JPanel implements ActionListener {

		private JRadioButton domination;
		private JRadioButton capital;
		private JRadioButton mission;
		private JCheckBox AutoPlaceAll;
		private JCheckBox recycle;

		private JLabel mapPic;
		private JTextField cardsFile;

		private ButtonGroup CardTypeButtonGroup;
		private ButtonGroup GameTypeButtonGroup;
		private JTable players;
		private TableModel dataModel;
		private JButton defaultPlayers;
		private NamedColor[] namedColors;
                private PlayerType[] playerTypes;

		class PlayerType {
		    String displayString;
		    String type;
		    public PlayerType(String displayString, String type) {
			this.displayString = displayString;
			this.type = type;
		    }
		    String getType() {
			return type;
		    }
		    public String toString() {
			return displayString;
		    }
		}

                class NamedColor extends Color {
                    private String name;
                    private String realname;
                    public NamedColor(Color color, String rn, String n) {
                        super(color.getRGB());
                        realname = rn;
                        name = n;
                    }
                    public String getRealName() {
                        return realname;
                    }
                    public String toString() {
                        return name;
                    }
                }

		public SetupPanel() {

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;

		// ##########################################################################################################

			JPanel playerOptions = new JPanel();
			playerOptions.setLayout(new java.awt.GridBagLayout());
			playerOptions.setBorder(javax.swing.BorderFactory.createTitledBorder( resbundle.getString("newgame.label.players") ));

			namedColors = new NamedColor[] {
				new NamedColor(Color.PINK,       "pink",      resbundle.getString("color.pink")),
				new NamedColor(Color.RED,        "red",       resbundle.getString("color.red")),
				new NamedColor(Color.ORANGE,     "orange",    resbundle.getString("color.orange")),
				new NamedColor(Color.YELLOW,     "yellow",    resbundle.getString("color.yellow")),
				new NamedColor(Color.GREEN,      "green",     resbundle.getString("color.green")),
				new NamedColor(Color.CYAN,       "cyan",      resbundle.getString("color.cyan")),
				new NamedColor(Color.BLUE,       "blue",      resbundle.getString("color.blue")),
				new NamedColor(Color.MAGENTA,    "magenta",   resbundle.getString("color.magenta")),
				new NamedColor(Color.WHITE,      "white",     resbundle.getString("color.white")),
				new NamedColor(Color.LIGHT_GRAY, "lightgray", resbundle.getString("color.lightgray")),
				new NamedColor(Color.DARK_GRAY,  "darkgray",  resbundle.getString("color.darkgray")),
				new NamedColor(Color.BLACK,      "black",     resbundle.getString("color.black"))
			};

                        final String[] ais = swingGUIPanel.myrisk.getAICommands();
                        playerTypes = new PlayerType[ ais.length+1 ];
                        playerTypes[0] = new PlayerType(resbundle.getString("newgame.player.type.human"),"human");
                        for (int a=0;a<ais.length;a++) {
                            String displayString;
                            try {
                                displayString = resbundle.getString("newgame.player.type."+ais[a]+"ai");
                            }
                            catch (MissingResourceException ex) {
                                // fallback if missing
                                displayString = "ai " + ais[a];
                            }
                            playerTypes[a+1] = new PlayerType(displayString,"ai "+ais[a]);
                        }

			final String[] names = {
				resbundle.getString("newgame.label.name"),
				resbundle.getString("newgame.label.color"),
				resbundle.getString("newgame.label.type"),
			};

			//String name="appletuser";
			//if (Risk.applet == null) {
			//	name = System.getProperty("user.name");
			//}

			// Create the dummy data (a few rows of names)
/*
			data = new Object[][] {
				{ name , namedColors[4], resbundle.getString("newgame.player.type.human")},
				{"bob", namedColors[1], resbundle.getString("newgame.player.type.easyai")},
				{"fred", namedColors[9], resbundle.getString("newgame.player.type.easyai")},
				{"ted", namedColors[11], resbundle.getString("newgame.player.type.easyai")},
				{"yura", namedColors[6], resbundle.getString("newgame.player.type.hardai")},
				{"lala", namedColors[2], resbundle.getString("newgame.player.type.hardai")}
			};
*/

			dataModel = new DefaultTableModel(names,0) { // AbstractTableModel
				//public int getColumnCount() { return names.length; }
				//public int getRowCount() { return data.length;}
				//public Object getValueAt(int row, int col) {return data[row][col];}
				public String getColumnName(int column) {return names[column];}
				public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
				public boolean isCellEditable(int row, int col) { return localGame; }
				public void setValueAt(Object aValue, int row, int column) { if(column==0) { if( ((String)aValue).length() > 15 ) { aValue = ((String)aValue).substring(0,15); } } super.setValueAt(aValue,row,column); } // if(column==0 && ((String)aValue).indexOf('$')!=-1 ) { return; } else { }
			};

			players = new JTable( dataModel );
			players.setSelectionMode(0);
			players.getTableHeader().setReorderingAllowed(false);

			DefaultTableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
				public void setValue(Object value) {
					if (value instanceof NamedColor) {
						NamedColor c = (NamedColor) value;
						setBackground(c);
						setForeground( RiskUIUtil.getTextColorFor( c ) );
						setText(c.toString());
					} else {
						super.setValue(value);
					}
				}
			};

			final JComboBox colorComboBox = new JComboBox( namedColors );
			final JComboBox typeComboBox = new JComboBox( playerTypes );

			TableColumn colorColumn = players.getColumn(resbundle.getString("newgame.label.color"));
			colorColumn.setCellEditor(new DefaultCellEditor(colorComboBox));
			colorRenderer.setHorizontalAlignment(JLabel.CENTER);
			colorColumn.setCellRenderer(colorRenderer);

			TableColumn typeColumn = players.getColumn(resbundle.getString("newgame.label.type"));
			typeColumn.setCellEditor(new DefaultCellEditor(typeComboBox));


			JScrollPane scrollpane = new JScrollPane(players);
			Dimension psize = GraphicsUtil.newDimension(200, 140);

			scrollpane.setPreferredSize(psize);
			scrollpane.setMinimumSize(psize);
			scrollpane.setMaximumSize(psize);

			// re-useing the combo box created for the table
			colorComboBox.addActionListener(
			    new ActionListener() {
				public void actionPerformed(ActionEvent a) {

					Color c = (Color)colorComboBox.getSelectedItem();
					colorComboBox.setBackground( c );
					colorComboBox.setForeground( RiskUIUtil.getTextColorFor(c) );

				}
			    }
			);

			JButton newPlayer = new JButton(resbundle.getString("newgame.newplayer"));

			newPlayer.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent a) {

						if (localGame) {

							((DefaultTableModel)dataModel).addRow( new Object[] { resbundle.getString("newgame.newplayername") ,namedColors[0],playerTypes[0]} );

						}
						else {

                					colorComboBox.setSelectedIndex(0);
							typeComboBox.setSelectedIndex(0);

 							// Messages
							Object[] message = new Object[6];

							message[0] = resbundle.getString("newgame.label.name");
							message[1] = new JTextField( resbundle.getString("newgame.newplayername") );
							message[2] = resbundle.getString("newgame.label.color");
							message[3] = colorComboBox;
							message[4] = resbundle.getString("newgame.label.type");
							message[5] = typeComboBox;

							// Options
							String[] options = {
								"OK",
								"Cancel"
							};
							int result = JOptionPane.showOptionDialog(
								RiskUIUtil.findParentFrame(swingGUIPanel),			// the parent that the dialog blocks
								message,				// the dialog message array
								"create new player",			// the title of the dialog window
								JOptionPane.OK_CANCEL_OPTION,		// option type
								JOptionPane.QUESTION_MESSAGE,		// message type
								null,					// optional icon, use null to use the default icon
								options,				// options string array, will be made into buttons
								options[0]				// option that should be made into a default button
							);

							if (result==0) {

								String type=((PlayerType)typeComboBox.getSelectedItem()).getType();

								swingGUIPanel.go("newplayer "+type+" "+((NamedColor)colorComboBox.getSelectedItem()).getRealName()+" "+((JTextField)message[1]).getText());

							}
						}
					}
				}
			);


			JButton delPlayer = new JButton(resbundle.getString("newgame.removeplayer"));

			delPlayer.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent a) {

						if (players.getSelectedRow() == -1 ) { return; }

						if (localGame) {
							players.removeEditor();
							((DefaultTableModel)dataModel).removeRow( players.getSelectedRow() );
						}
						else {
							swingGUIPanel.go("delplayer "+ players.getValueAt( players.getSelectedRow() , 0));
						}
					}
				}
			);

			defaultPlayers = new JButton(resbundle.getString("newgame.resetplayers"));

			defaultPlayers.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent a) {
						players.removeEditor();
						resetPlayers(false);
					}
				}
			);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 3; // width
			c.gridheight = 1; // height
			playerOptions.add(scrollpane, c);

			c.gridx = 0; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			playerOptions.add(newPlayer, c);

			c.gridx = 1; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			playerOptions.add(delPlayer, c);

			c.gridx = 2; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			playerOptions.add(defaultPlayers, c);

		// ##########################################################################################################

			JPanel mapOptions = new JPanel();
			mapOptions.setLayout(new java.awt.GridBagLayout());
			mapOptions.setBorder(javax.swing.BorderFactory.createTitledBorder( resbundle.getString("newgame.label.map") ));

			JButton chooseMap = new BadgeButton(resbundle.getString("newgame.choosemap"));

			chooseMap.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {

							String name = RiskUIUtil.getNewMap( RiskUIUtil.findParentFrame(swingGUIPanel) );

							if (name != null) {
								swingGUIPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								swingGUIPanel.go("choosemap " + name );
							}
						}
					}
			);


			JButton defaultMap = new JButton(resbundle.getString("newgame.defaultmap"));

			defaultMap.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("choosemap "+RiskGame.getDefaultMap() );
						}
					}
			);



		mapPic = new JLabel();
		mapPic.setBorder( BorderFactory.createLoweredBevelBorder() );
		Dimension size = GraphicsUtil.newDimension(203, 127);
		mapPic.setPreferredSize(size);
		mapPic.setMinimumSize(size);
		mapPic.setMaximumSize(size);
                
                JPopupMenu mapPopup = new JPopupMenu();
                mapPopup.add("copy name").addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        StringSelection selection = new StringSelection(mapPic.getToolTipText());
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                });
                mapPic.setComponentPopupMenu(mapPopup);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 2; // width
			c.gridheight = 1; // height
			mapOptions.add(mapPic, c);

			c.gridx = 0; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			c.weightx = 1;
			mapOptions.add(chooseMap, c);

			c.gridx = 1; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			mapOptions.add(defaultMap, c);
			c.weightx = 0;


			JPanel cardOptions = new JPanel();
			cardOptions.setLayout(new java.awt.GridBagLayout());
			cardOptions.setBorder(javax.swing.BorderFactory.createTitledBorder( resbundle.getString("newgame.label.cards") ));


			cardsFile = new JTextField("");
			cardsFile.setEditable(false);
			cardsFile.setBackground( mapOptions.getBackground() ); // SystemColor.control

			size = GraphicsUtil.newDimension(200, 20);

			cardsFile.setPreferredSize(size);
			cardsFile.setMinimumSize(size);
			cardsFile.setMaximumSize(size);

			JButton chooseCards = new JButton(resbundle.getString("newgame.choosecards"));

			chooseCards.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {

							String name = RiskUIUtil.getNewFile( RiskUIUtil.findParentFrame(swingGUIPanel), RiskFileFilter.RISK_CARDS_FILES);

							if (name != null) {

								swingGUIPanel.go("choosecards " + name );
							}
						}
					}
			);


			JButton defaultCards = new JButton(resbundle.getString("newgame.defaultcards"));

			defaultCards.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {

							swingGUIPanel.go("choosecards "+RiskGame.getDefaultCards() );
						}
					}
			);


			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 2; // width
			c.gridheight = 1; // height
			cardOptions.add(cardsFile, c);

			c.gridx = 0; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			c.weightx = 1;
			cardOptions.add(chooseCards, c);

			c.gridx = 1; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			cardOptions.add(defaultCards, c);
			c.weightx = 0;


			JPanel GameTypeButtons = new JPanel();
			GameTypeButtons.setLayout(new javax.swing.BoxLayout(GameTypeButtons, javax.swing.BoxLayout.Y_AXIS));
			GameTypeButtons.setBorder(javax.swing.BorderFactory.createTitledBorder(resbundle.getString("newgame.label.gametype")));

			JPanel cardsOptions = new JPanel();
			cardsOptions.setLayout(new javax.swing.BoxLayout(cardsOptions, javax.swing.BoxLayout.Y_AXIS));
			cardsOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(resbundle.getString("newgame.label.cardsoptions")));


			GameTypeButtonGroup = new ButtonGroup();
			CardTypeButtonGroup = new ButtonGroup();

			domination = new JRadioButton(resbundle.getString("newgame.mode.domination"), true);
			capital = new JRadioButton(resbundle.getString("newgame.mode.capital"));
			mission = new JRadioButton(resbundle.getString("newgame.mode.mission"));

			domination.setOpaque(false);
			capital.setOpaque(false);
			mission.setOpaque(false);

			domination.addActionListener( this );
			capital.addActionListener( this );
			mission.addActionListener( this );

			final JRadioButton increasing = new JRadioButton(resbundle.getString("newgame.cardmode.increasing"), true);
			final JRadioButton fixed = new JRadioButton(resbundle.getString("newgame.cardmode.fixed"));
                        final JRadioButton italian = new JRadioButton(resbundle.getString("newgame.cardmode.italianlike"));

			increasing.setOpaque(false);
			fixed.setOpaque(false);
                        italian.setOpaque(false);

			GameTypeButtonGroup.add ( domination );
			GameTypeButtonGroup.add ( capital );
			GameTypeButtonGroup.add ( mission );

			CardTypeButtonGroup.add ( increasing );
			CardTypeButtonGroup.add ( fixed );
                        CardTypeButtonGroup.add ( italian );

			GameTypeButtons.add( domination );
			GameTypeButtons.add( capital );
			GameTypeButtons.add( mission );

			cardsOptions.add( increasing );
			cardsOptions.add( fixed );
                        cardsOptions.add( italian );

			JPanel GameOptionsButtons = new JPanel();
			GameOptionsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
			GameOptionsButtons.setBorder(javax.swing.BorderFactory.createTitledBorder(resbundle.getString("newgame.label.startgameoptions")));

			AutoPlaceAll = new JCheckBox(resbundle.getString("newgame.autoplace"));
			GameOptionsButtons.add( AutoPlaceAll );
			AutoPlaceAll.setOpaque(false);
                        AutoPlaceAll.setSelected( "true".equals(swingGUIPanel.myrisk.getRiskConfig("default.autoplaceall")) );

			recycle = new JCheckBox(resbundle.getString("newgame.recycle"));
			GameOptionsButtons.add( recycle );
			recycle.setOpaque(false);
                        recycle.setSelected( "true".equals(swingGUIPanel.myrisk.getRiskConfig("default.recyclecards")) );

			JButton startGame = new JButton(resbundle.getString("newgame.startgame"));

			startGame.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {

							boolean setupOK=true;
							String error="";

							if (players.getRowCount() >= 2 && players.getRowCount() <= RiskGame.MAX_PLAYERS) {

								for (int c=0; c < players.getRowCount(); c++) {
									for (int b=(c+1); b < players.getRowCount(); b++) {
										if ( ((String)players.getValueAt(c, 0)).equals( players.getValueAt(b, 0) ) ) {
											setupOK=false;
											error=resbundle.getString("newgame.error.samename");
										}
										if ( players.getValueAt(c, 1) == ( players.getValueAt(b, 1) ) ) {
											setupOK=false;
											error=resbundle.getString("newgame.error.samecolor");
										}
									}
								}

							}
							else {
								setupOK=false;
								error=resbundle.getString("newgame.error.2to6players");
							}

							//if ((players.getRowCount() == 2) && ( !(((String)players.getValueAt(0, 2)).equals("Human")) || !(((String)players.getValueAt(1, 2)).equals("Human")) || !(domination.isSelected()) ) ) {
							//	setupOK=false;
							//	error=resbundle.getString("newgame.error.2playerdominationonly");
							//}

							if (setupOK == true) {

								if (localGame) {

                                                                    List playerStrings = new ArrayList();

								    for (int c=0; c < players.getRowCount(); c++) {

                                                                        String name = (String)players.getValueAt(c, 0);
                                                                        String color = ((NamedColor)players.getValueAt(c, 1)).getRealName();
									String type = ((PlayerType)players.getValueAt(c, 2)).getType();

                                                                        playerStrings.add( new String[] {name,color,type} );

									swingGUIPanel.go("newplayer "+type+" "+color+" "+name );

								    }

                                                                    RiskUtil.savePlayers(playerStrings, SwingGUIPanel.class);
								}

								String type="";

								if (domination.isSelected()) { type = "domination"; }
								else if (capital.isSelected()) { type = "capital"; }
								else if (mission.isSelected()) { type = "mission"; }

								if (increasing.isSelected()) { type += " increasing"; }
								else if (fixed.isSelected()) { type += " fixed"; }
                                                                else { type += " italianlike"; }

								swingGUIPanel.go("startgame " + type + (( AutoPlaceAll.isSelected() )?(" autoplaceall"):("")) + (( recycle.isSelected() )?(" recycle"):("")) );

							}
							else {
								swingGUIPanel.showError(error);
							}

						}
					}
			);


			// ################### GAME SETUP #######################
			this.setLayout(new java.awt.GridBagLayout());

			playerOptions.setOpaque(false);
			GameTypeButtons.setOpaque(false);
			mapOptions.setOpaque(false);
			cardOptions.setOpaque(false);
			GameOptionsButtons.setOpaque(false);
			cardsOptions.setOpaque(false);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(playerOptions, c);

			c.gridx = 0; // col
			c.gridy = 2; // row
			c.gridwidth = 1; // width
			c.gridheight = 2; // height
			this.add(GameTypeButtons, c);


			c.gridx = 1; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(mapOptions, c);

			c.gridx = 0; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(cardsOptions, c);

			c.gridx = 1; // col
			c.gridy = 1; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(cardOptions, c);

			c.gridx = 1; // col
			c.gridy = 2; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(GameOptionsButtons, c);

			c.gridx = 1; // col
			c.gridy = 3; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(startGame, c);
		}

		public void showCardsFile(String c, boolean m) {
			cardsFile.setText(c);

			if ( m==false && mission.isSelected() ) { domination.setSelected(true); AutoPlaceAll.setEnabled(true); }

			mission.setEnabled(m);
		}

		public void showMapImage(Icon p, String tooltip) {
			mapPic.setIcon( p ); // SCALE_DEFAULT
                        mapPic.setToolTipText(tooltip);
			swingGUIPanel.setCursor(null); // Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
		}

		public void actionPerformed(ActionEvent e) {

			if (e.getSource()==mission) {
				AutoPlaceAll.setEnabled(false);
			}
			else if (e.getSource()==domination) {
				AutoPlaceAll.setEnabled(true);
			}
			else if (e.getSource()==capital) {
				AutoPlaceAll.setEnabled(true);
			}
		}

		public void resetPlayers(boolean lastUsed) {

			while (players.getRowCount() != 0) ((DefaultTableModel)dataModel).removeRow( 0 );

                        Properties settings;
                        if (lastUsed) {
                            settings = RiskUtil.getPlayerSettings(swingGUIPanel.myrisk, SwingGUIPanel.class);
                        }
                        else {
                            settings = new Properties() {
                                public String getProperty(String key) {
                                    return swingGUIPanel.myrisk.getRiskConfig(key);
                                }
                            };
                        }

			for (int c=1; c<=RiskGame.MAX_PLAYERS; c++) {
                            String name = settings.getProperty("default.player"+c+".name");
                            String color = settings.getProperty("default.player"+c+".color");
                            String type = settings.getProperty("default.player"+c+".type");
                            if (!"".equals(name)&&!"".equals(color)&&!"".equals(type)) {
                                try {
                                    ((DefaultTableModel)dataModel).addRow( new Object[] {name , findColor( ColorUtil.getColor( color ) ), findType( swingGUIPanel.myrisk.getType( type ) ) } );
                                }
                                catch (Exception ex) {
                                    System.err.println("unable to add player "+name+" "+color+" "+type);
                                    ex.printStackTrace();
                                }
                            }
			}
		}

		public void setupGame() {
			players.removeEditor();

			if (localGame) {

				resetPlayers(true);
				defaultPlayers.setEnabled(true);
			}
			else {

				// remove all players
				while (players.getRowCount() != 0) ((DefaultTableModel)dataModel).removeRow( 0 );
				defaultPlayers.setEnabled(false);
			}
		}

		public void addPlayer(int t, String name, int color, String ip) {

			if (!localGame) {

				NamedColor c=findColor(color);

				Object type=findType(t);

				((DefaultTableModel)dataModel).addRow( new Object[] {name, c ,type} );
			}
		}

		public NamedColor findColor(int color) {
				// go though array of colors and find correct NamedColor
				for (int a=0; a < namedColors.length; a++) {
					if (namedColors[a].getRGB()==color) { return namedColors[a]; }
				}
				return null;
		}
		public PlayerType findType(int t) {
                    String type = swingGUIPanel.myrisk.getType(t);
                    for (int a=0;a<playerTypes.length;a++) {
                        if (playerTypes[a].getType().equals(type)) {
                            return playerTypes[a];
                        }
                    }
                    return null;
		}

		public void delPlayer(String name) {

			if (!localGame) {

				for (int c=0; c < players.getRowCount(); c++) {

					if ( players.getValueAt(c,0).equals(name) ) {
						((DefaultTableModel)dataModel).removeRow( c );
						break;
					}
				}
			}
		}
	}

	class capitalPanel extends JPanel {

		public capitalPanel() {

			this.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;

			JButton endtrade = new JButton(resbundle.getString("about.okbutton"));

			endtrade.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
                                                        int c1Id = swingGUIPanel.pp.getC1();
							swingGUIPanel.pp.setC1(PicturePanel.NO_COUNTRY);
							swingGUIPanel.go("capital "+c1Id );
						}
					}
			);

			capitalLabel = new JLabel(resbundle.getString("core.help.selectcapital"));

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(capitalLabel, c);

			c.gridx = 1; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(endtrade, c);
		}
	}

	class tradeCardsPanel extends JPanel {

                JButton endtrade;

		public tradeCardsPanel() {

			endtrade = new JButton(resbundle.getString("game.button.go.endtrade"));

			endtrade.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("endtrade");
						}
					}
			);

			add( new JLabel(resbundle.getString("cards.totradeclick")) );
			add(endtrade);
		}
	}

	class endgoPanel extends JPanel {

		public endgoPanel() {

			this.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;

			JButton endturn = new JButton("End Go");

			endturn.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("endgo");
						}
					}
			);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(endturn, c);
		}
	}

	class placeArmiesPanel extends JPanel {

		public placeArmiesPanel() {

			this.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;

			autoplace.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("autoplace");
						}
					}
			);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(armies, c);

			c.gridx = 1; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(autoplace, c);
		}
	}

	class attackPanel extends JPanel {

		public attackPanel() {

			this.setLayout(new java.awt.GridBagLayout());

			Dimension size = GraphicsUtil.newDimension(300, 20);

			attacker = new JLabel(resbundle.getString("game.note.selectattacker"));

			attacker.setPreferredSize(size);
			attacker.setMinimumSize(size);
			attacker.setMaximumSize(size);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;


			JButton endAttackButton = new JButton(resbundle.getString("game.button.go.endattack"));

			endAttackButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("endattack");
							attacker.setText(resbundle.getString("game.note.selectattacker"));

							swingGUIPanel.pp.setC1(PicturePanel.NO_COUNTRY);
							swingGUIPanel.pp.setC2(PicturePanel.NO_COUNTRY);
							swingGUIPanel.pp.repaint();
						}
					}
			);


			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(attacker, c);

			c.gridx = 1; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(endAttackButton, c);
		}
	}

	class tacMovePanel extends JPanel {

		public tacMovePanel() {

			this.setLayout(new java.awt.GridBagLayout());

			Dimension size = GraphicsUtil.newDimension(200, 40);

                        Font titleFont = new Font("SansSerif", Font.PLAIN, getFont().getSize() - 2); // 13 - 2 = 11

			country1 = new JTextField("");
			country1.setEditable(false);
			//country1.setBackground(SystemColor.control);
			country1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED),"Source",javax.swing.border.TitledBorder.LEADING,javax.swing.border.TitledBorder.TOP,titleFont,new java.awt.Color(60,60,60)));

			country1.setPreferredSize(size);
			country1.setMinimumSize(size);
			country1.setMaximumSize(size);

			country2 = new JTextField("");
			country2.setEditable(false);
			//country2.setBackground(SystemColor.control);
			country2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED),"Target",javax.swing.border.TitledBorder.LEADING,javax.swing.border.TitledBorder.TOP,titleFont,new java.awt.Color(60,60,60)));

			country2.setPreferredSize(size);
			country2.setMinimumSize(size);
			country2.setMaximumSize(size);

			country1.setOpaque(false);
			country2.setOpaque(false);

			// s.setBorder(new TitledBorder("Number of Armies to move") );

			moveNumber.setMinimumSize(GraphicsUtil.newDimension(300, 50));

			moveNumber.putClientProperty("JSlider.isFilled", Boolean.TRUE );

			moveNumber.setPaintTicks(true);
			moveNumber.setMajorTickSpacing(1);
			// moveNumber.setMinorTickSpacing(1);

			moveNumber.setPaintLabels( true );
			moveNumber.setSnapToTicks( true );

			moveNumber.getLabelTable().put(new Integer(11), new JLabel(new Integer(11).toString(), JLabel.CENTER));
			moveNumber.setLabelTable( slider.getLabelTable() );

			moveNumber.getAccessibleContext().setAccessibleName("slider");
			moveNumber.getAccessibleContext().setAccessibleDescription("move armies slider");

			// moveNumber.addChangeListener(listener);


			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.anchor = GridBagConstraints.CENTER;

			JButton moveButton = new JButton(resbundle.getString("move.move"));

			moveButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {

							if ( !(country1.getText().equals("")) && !(country2.getText().equals("")) ) {

								//FIXME: get the id of the country, don't use the string name
								int nCountryId = swingGUIPanel.myrisk.getGame().getCountryInt(swingGUIPanel.pp.getC1()).getColor();
								//int nCountryId = myrisk.getGame().getCountry(country1.getText()).getColor();

								showQuestion( swingGUIPanel.myrisk.hasArmiesInt(nCountryId) - 1);
//			    showQuestion( myrisk.hasArmies(country1.getText() )-1 );

								country1.setText("");
								country2.setText("");
								swingGUIPanel.pp.setC1(PicturePanel.NO_COUNTRY);
								swingGUIPanel.pp.setC2(PicturePanel.NO_COUNTRY);
                                                                swingGUIPanel.pp.repaint();
							}
						}
					}
			);

			JButton noMoveButton = new JButton(resbundle.getString("game.button.go.nomove"));

			noMoveButton.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("nomove");
							country1.setText("");
							country2.setText("");
							swingGUIPanel.pp.setC1(PicturePanel.NO_COUNTRY);
							swingGUIPanel.pp.setC2(PicturePanel.NO_COUNTRY);
						}
					}
			);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(country1, c);

			c.gridx = 1; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(country2, c);

			c.gridx = 2; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(moveButton, c);

			c.gridx = 3; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(noMoveButton, c);
		}

		public void showQuestion(int n) {
			moveNumber.setMaximum(n);
			moveNumber.setMinimum(1);
			moveNumber.setValue(1);

			String[] options = {
				resbundle.getString("swing.move.move"),
				resbundle.getString("swing.move.moveall"),
				resbundle.getString("swing.move.cancel")
			};

			int a = JOptionPane.showOptionDialog(
				RiskUIUtil.findParentFrame(this),                            // the parent that the dialog blocks
				moveNumber,                      // the dialog message array
				resbundle.getString("swing.move.title"), // the title of the dialog window
				JOptionPane.DEFAULT_OPTION,      // option type
				JOptionPane.QUESTION_MESSAGE,    // message type
				null,                            // optional icon, use null to use the default icon
				options,                         // options string array, will be made into buttons
				options[0]                       // option that should be made into a default button
			);

			if (a==0) {
				swingGUIPanel.go("movearmies " + swingGUIPanel.pp.getC1() + " " + swingGUIPanel.pp.getC2() + " "+ moveNumber.getValue() );
			}
			if (a==1) {
				swingGUIPanel.go("movearmies " + swingGUIPanel.pp.getC1() + " " + swingGUIPanel.pp.getC2() + " "+ n );
			}
		}
	}

	class rollPanel extends JPanel {

		public rollPanel() {

			this.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;

			JButton retreat = new JButton(resbundle.getString("battle.retreat"));

			retreat.addActionListener(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent a) {
                                    swingGUIPanel.go("retreat");
                                }
                            }
			);

			c.gridx = 3; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(retreat, c);
		}
	}

	class defendPanel extends JPanel {
		public defendPanel() {
			this.setLayout(new java.awt.GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			c.fill = GridBagConstraints.BOTH;
		}
	}

	class movePanel extends JPanel {

		public movePanel() {

			this.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new java.awt.Insets(3, 3, 3, 3);
			//c.fill = GridBagConstraints.BOTH;


			// s.setBorder(new TitledBorder("Number of Armies to move") );

			slider.setMinimumSize(GraphicsUtil.newDimension(300, 50));

			slider.putClientProperty("JSlider.isFilled", Boolean.TRUE );

			slider.setPaintTicks(true);
			slider.setMajorTickSpacing(1);
			// slider.setMinorTickSpacing(1);

			slider.setPaintLabels( true );
			slider.setSnapToTicks( true );

			slider.getLabelTable().put(new Integer(11), new JLabel(new Integer(11).toString(), JLabel.CENTER));
			slider.setLabelTable( slider.getLabelTable() );

			slider.getAccessibleContext().setAccessibleName("slider");
			slider.getAccessibleContext().setAccessibleDescription("move armies slider");

			// slider.addChangeListener(listener);

			slider.setOpaque(false);

			JLabel label = new JLabel(resbundle.getString("move.numberofarmies"));

			JButton move1 = new JButton(resbundle.getString("move.move"));

			move1.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("move " + slider.getValue() );
						}
					}
			);

			JButton moveall = new JButton(resbundle.getString("move.moveall"));

			moveall.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent a) {
							swingGUIPanel.go("move all");
						}
					}
			);

			c.gridx = 0; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(label, c);

			c.gridx = 1; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(slider, c);

			c.gridx = 2; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(move1, c);

			c.gridx = 3; // col
			c.gridy = 0; // row
			c.gridwidth = 1; // width
			c.gridheight = 1; // height
			this.add(moveall, c);
		}
	}

	class winnerPanel extends JPanel {

                JButton continueButton;

		public winnerPanel() {

                        continueButton = new JButton(resbundle.getString("game.button.go.continue"));
			continueButton.addActionListener(
                            new ActionListener() {
                                public void actionPerformed(ActionEvent a) {
                                    swingGUIPanel.go("continue");
                                }
                            }
			);

                        add( new JLabel(resbundle.getString("game.over")) );
                        add(continueButton);
		}
	}
}

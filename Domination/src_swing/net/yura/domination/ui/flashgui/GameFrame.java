// Yura Mamyrin, Group D

package net.yura.domination.ui.flashgui;

import javax.swing.JFrame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;
import net.yura.domination.engine.ColorUtil;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.guishared.AboutDialog;
import net.yura.swing.GraphicsUtil;
import net.yura.swing.ImageIcon;
import net.yura.domination.engine.guishared.MapMouseListener;
import net.yura.domination.engine.guishared.PicturePanel;
import net.yura.domination.engine.translation.TranslationBundle;

/**
 * Game Frame for FlashGUI
 * @author Yura Mamyrin
 */
public class GameFrame extends JFrame implements KeyListener {

        public static final Color UI_COLOR = Color.RED;

	private BufferedImage gameImg;
	private Risk myrisk;
	private PicturePanel pp;
	private GameMenuPanel gm;
	private int mapView;
	private String gameStatus;
	private boolean localGame;
	private int gameState;
	private String note;

	private JButton cardsbutton;
	private JButton missionbutton;
	private JButton undobutton;
	private JButton menubutton;
	private JButton graphbutton;
	private JButton gobutton;

        private final Action closeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
		go("closegame");
            }
        };
        private Action extraAction;

	private java.util.ResourceBundle resb;
	private StatsDialog graphdialog;

	private boolean menuOn;
	private boolean graphOn;

	private int[] colors;
	private CardsDialog cardsDialog;

	//private int c1Id;
	private MoveDialog movedialog;


	private JButton savebutton;
	private JButton resumebutton;

	private JCheckBox AutoEndGo;
	private JCheckBox AutoDefend;

	private JButton helpbutton;
	private JButton closebutton;

        private MouseInputAdapter mapListener;

	public GameFrame(Risk r, PicturePanel p) {

		//setResizable(false);

		//setGameStatus(null); // not needed?

		myrisk=r;
		pp=p;

                final MapMouseListener mml = new MapMouseListener(myrisk,pp);
                mapListener = new MouseInputAdapter() {
                    public void mouseExited(MouseEvent e) {
                        mml.mouseExited();
                    }
                    public void mouseReleased(MouseEvent e) {
                        int[] click = mml.mouseReleased(e.getX(),e.getY(),gameState);
                        if (click!=null) {
                            mapClick(click,e);
                        }
                    }
                    public void mouseMoved(MouseEvent e) {
                        mml.mouseMoved(e.getX(),e.getY(),gameState);
                    }
                };

		menuOn=false;
		graphOn=false;

		gameImg = RiskUIUtil.getUIImage(this.getClass(),"game.jpg");

		initGUI();

		setIconImage(Toolkit.getDefaultToolkit().getImage( AboutDialog.class.getResource("icon.gif") ));

		pack();

		graphdialog = null;

		try {

			setMinimumSize( getPreferredSize() );

		}
		catch(NoSuchMethodError ex) {

			// must me java 1.4
			setResizable(false);

		}

	}

        public void setExtraAction(Action action) {
            extraAction = action;
        }

        @Override
        public void setVisible(boolean visible) {
            if (!visible) {
		if (graphOn) { displayGraph(); }
		if (menuOn) { displayMenu(); }
		extraAction = null;
            }
            super.setVisible(visible);
        }

	/**
	 * This method is called from within the constructor to initialize the form.
	 * Initialises the GUI
	 */
	private void initGUI() {

		resb = TranslationBundle.getBundle();

		// set title
		setTitle("yura.net " + RiskUtil.GAME_NAME ); // resb.getString("game.title")

		//JLayeredPane layeredPane = new JLayeredPane();
		//layeredPane.setPreferredSize(d);
		//layeredPane.setMinimumSize(d);
		//layeredPane.setMaximumSize(d);

		final JPanel fpLeft = new JPanel() {
		    public void paintComponent(Graphics g) {
			// getHeight() = 425
			g.drawImage(gameImg, 0, 0, getWidth(), getHeight(),    0, 0, 31, 425, this); // left
			g.setColor(Color.BLACK);
			g.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		    }
		};

		final JPanel fpRight = new JPanel() {
		    public void paintComponent(Graphics g) {
			// getHeight() = 425
			g.drawImage(gameImg, 0, 0, getWidth(), getHeight(),    31, 0, 63, 425, this); // right
			g.setColor(Color.BLACK);
			g.drawLine(0,0,0,getHeight());
		    }
		};
                
		int l=715,m=6;

		final BufferedImage topleft=gameImg.getSubimage(63,0,l,54);
		final BufferedImage topmiddle=gameImg.getSubimage(63+l,0,m,54);
		final BufferedImage topright=gameImg.getSubimage(63+l+m,0,740-(l+m),54);

		JPanel fp = new JPanel() {
		    public void paintComponent(Graphics g) {

			//		  destination		source
			//g.drawImage(game,0,0,740,54,     63,0,803,54,this); // top

			GraphicsUtil.drawImage(g, topleft, 0, 0, this);
			for (int c = GraphicsUtil.scale(topleft.getWidth()); c < getWidth() - GraphicsUtil.scale(topright.getWidth()); c = c + GraphicsUtil.scale(topmiddle.getWidth())) {
				g.drawImage(topmiddle, c, 0, GraphicsUtil.scale(topmiddle.getWidth()), GraphicsUtil.scale(topmiddle.getHeight()), this);
			}
			g.drawImage(topright, getWidth() - GraphicsUtil.scale(topright.getWidth()), 0, GraphicsUtil.scale(topright.getWidth()), GraphicsUtil.scale(topright.getHeight()), this);

			Graphics2D g2 = (Graphics2D)g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor( Color.BLACK );

			GraphicsUtil.drawStringCenteredAt(g, resb.getString("game.tabs.continents"), 81, 26);
			GraphicsUtil.drawStringCenteredAt(g, resb.getString("game.tabs.ownership"), 196, 26);
			GraphicsUtil.drawStringCenteredAt(g, resb.getString("game.tabs.borderthreat"), 311, 26);
			GraphicsUtil.drawStringCenteredAt(g, resb.getString("game.tabs.cardownership"), 426, 26);
			GraphicsUtil.drawStringCenteredAt(g, resb.getString("game.tabs.troopstrength"), 541, 26);
			GraphicsUtil.drawStringCenteredAt(g, resb.getString("game.tabs.connectedempire"), 656, 26);

			if (mapView==PicturePanel.VIEW_CONTINENTS) {
				GraphicsUtil.drawImage(g, gameImg, 24, 32, 139, 39,   64, 383, 179, 390,this);
			}
			else if (mapView==PicturePanel.VIEW_OWNERSHIP) {
				GraphicsUtil.drawImage(g, gameImg, 139, 32, 254, 39,     64, 390, 179, 397,this);
			}
			else if (mapView==PicturePanel.VIEW_BORDER_THREAT) {
				GraphicsUtil.drawImage(g, gameImg, 254, 32, 369, 39,     64, 397, 179, 404, this);
			}
			else if (mapView==PicturePanel.VIEW_CARD_OWNERSHIP) {
				GraphicsUtil.drawImage(g, gameImg, 369, 32, 484, 39,     64, 404, 179, 411, this);
			}
			else if (mapView==PicturePanel.VIEW_TROOP_STRENGTH) {
				GraphicsUtil.drawImage(g, gameImg, 484, 32, 599, 39,     64, 411, 179, 418, this);
			}
			else if (mapView==PicturePanel.VIEW_CONNECTED_EMPIRE) {
				GraphicsUtil.drawImage(g, gameImg, 599, 32, 714, 39,     64, 418, 179, 425, this);
			}

			g.drawLine(fpLeft.getWidth() - 1, getHeight() - 1, getWidth() - fpRight.getWidth(), getHeight() - 1);
		    }
		};
		//fp.setBounds(0,0, (int)d.getWidth() , (int)d.getHeight() );
		fp.addMouseListener( new MouseInputAdapter() {
                    public void mouseReleased(MouseEvent e) {
                	int click=insideButton(e.getX(),e.getY());
			if (click != -1) { // this means it was one of the view buttons
				if (mapView !=click) {
					setMapView(click);
				}
			}
                    }
                } );
		//fp.addMouseMotionListener(this);


		l=551;
		m=6;

		final BufferedImage bottomleft=gameImg.getSubimage(63,54,l,121);
		final BufferedImage bottommiddle=gameImg.getSubimage(63+l,54,m,121);
		final BufferedImage bottomright=gameImg.getSubimage(63+l+m,54,740-(l+m),121);

		JPanel fpBottom = new JPanel() {
		    public void paintComponent(Graphics g) {

			//g.drawImage(game,0,0,740,121,  63,54,803,175,this); // bottom

                        GraphicsUtil.drawImage(g, bottomleft, 0, 0, this);
			for (int c = GraphicsUtil.scale(bottomleft.getWidth()); c < getWidth() - GraphicsUtil.scale(bottomright.getWidth()); c = c + GraphicsUtil.scale(bottommiddle.getWidth())) {
				g.drawImage(bottommiddle, c, 0, GraphicsUtil.scale(bottommiddle.getWidth()), GraphicsUtil.scale(bottommiddle.getHeight()), this);
			}
			g.drawImage(bottomright, getWidth() - GraphicsUtil.scale(bottomright.getWidth()), 0, GraphicsUtil.scale(bottomright.getWidth()), GraphicsUtil.scale(bottomright.getHeight()), this);

			Graphics2D g2 = (Graphics2D)g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int[] cols = colors;

			for (int c=0; c<cols.length; c++) {

				Color col = new Color( cols[c] );

				g.setColor( new Color(col.getRed(),col.getGreen(),col.getBlue(), 100) );

				if (c==0) {
					GraphicsUtil.fillArc(g, 8, 89, 24 , 24, 90, 180);
					g.fillRect(GraphicsUtil.scale(20), GraphicsUtil.scale(89), (getWidth() - GraphicsUtil.scale(173)) - (GraphicsUtil.scale(24) * (cols.length - c)), GraphicsUtil.scale(24));
				}
				else {
					g.fillRect((getWidth() - GraphicsUtil.scale(177)) - (GraphicsUtil.scale(24) * (cols.length - c)), GraphicsUtil.scale(89), GraphicsUtil.scale(24), GraphicsUtil.scale(24));
				}

			}

			if (gameStatus!=null) {

				g.setColor( new Color( ColorUtil.getTextColorFor( cols[0] ) ) );
                                
                                // do not let font go bellow 10,
                                // on hi res windows, default fontsize is 10, and 10-2=8 looks tiny
                                int fontSize = Math.max(10, g.getFont().getSize() - 2); // 13 - 2 = 11

				g.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, fontSize));
				GraphicsUtil.drawString(g, gameStatus, 22, 105);
			}

			g2.setColor( Color.BLACK );

			TextLayout tl=null;
			FontRenderContext frc = g2.getFontRenderContext();
			Font font = g2.getFont();

			if ( gameState==RiskGame.STATE_NEW_GAME ) {
				tl = new TextLayout( resb.getString("game.pleasewait") , font, frc);
			}
			else if ( (gameState==RiskGame.STATE_TRADE_CARDS || gameState==RiskGame.STATE_PLACE_ARMIES || gameState==RiskGame.STATE_ATTACKING || gameState==RiskGame.STATE_SELECT_CAPITAL || gameState==RiskGame.STATE_FORTIFYING ) && !(note.equals("")) ) {
				tl = new TextLayout( note , font, frc);
			}

			if (tl!=null) {
				tl.draw(g2, (float) (getWidth() - GraphicsUtil.scale(84) - tl.getBounds().getWidth() / 2), (float) GraphicsUtil.scale(57));
			}

			g.drawLine(fpLeft.getWidth() - 1, 0, getWidth() - fpRight.getWidth(), 0);
		    }

		    public void setBounds(int x,int y,int w,int h) {
			super.setBounds(x,y,w,h);
			gobutton.setBounds(getWidth() - GraphicsUtil.scale(140), GraphicsUtil.scale(74), GraphicsUtil.scale(115) , GraphicsUtil.scale(31));
		    }
		};
		fpBottom.setLayout(null);

		int w=114;
		int h=66;
		// normal - pressed - rollover - disabled
		int x=63;
		int y=77;

		graphbutton = makeRiskButton(gameImg.getSubimage(x, y, w, h), gameImg.getSubimage(x, y+230, w, h), gameImg.getSubimage(x, y+164, w, h), gameImg.getSubimage(x, y+98, w, h) );
		GraphicsUtil.setBounds(graphbutton, x - 63, y - 54, w, h);
		graphbutton.addActionListener( buttonActionListener );
		graphbutton.setToolTipText( resb.getString("game.button.statistics") );

		x=x+w;

		cardsbutton = makeRiskButton(gameImg.getSubimage(x, y, w, h), gameImg.getSubimage(x, y+230, w, h), gameImg.getSubimage(x, y+164, w, h), gameImg.getSubimage(x, y+98, w, h) );
		GraphicsUtil.setBounds(cardsbutton, x - 63, y - 54, w, h);
		cardsbutton.addActionListener( buttonActionListener );
		cardsbutton.setToolTipText(resb.getString("game.button.cards"));

		x=x+w;

		missionbutton = makeRiskButton(gameImg.getSubimage(x, y, w, h), gameImg.getSubimage(x, y+230, w, h), gameImg.getSubimage(x, y+164, w, h), gameImg.getSubimage(x, y+98, w, h) );
		GraphicsUtil.setBounds(missionbutton, x - 63, y - 54, w, h);
		missionbutton.addActionListener( buttonActionListener );
		missionbutton.setToolTipText(resb.getString("game.button.mission"));

		x=x+w;

		undobutton = makeRiskButton(gameImg.getSubimage(x, y, w, h), gameImg.getSubimage(x, y+230, w, h), gameImg.getSubimage(x, y+164, w, h), gameImg.getSubimage(x, y+98, w, h) );
		GraphicsUtil.setBounds(undobutton, x - 63, y - 54, w, h);
		undobutton.addActionListener( buttonActionListener );
		undobutton.setToolTipText(resb.getString("game.button.undo"));

		x=x+w;

		menubutton = makeRiskButton(gameImg.getSubimage(x, y, w, h), gameImg.getSubimage(x, y+230, w, h), gameImg.getSubimage(x, y+164, w, h), gameImg.getSubimage(x, y+98, w, h) );
		GraphicsUtil.setBounds(menubutton, x - 63, y - 54, w, h);
		menubutton.addActionListener( buttonActionListener );
		menubutton.setToolTipText( resb.getString("game.button.menu") );



		w=115;
		h=31;
		gobutton = makeRiskButton(gameImg.getSubimage(663, 128, w, h), gameImg.getSubimage(412, 394, w, h), gameImg.getSubimage(296, 394, w, h), gameImg.getSubimage(180, 394, w, h) );
		gobutton.addActionListener( buttonActionListener );

		fpBottom.add(graphbutton);
		fpBottom.add(cardsbutton);
		fpBottom.add(missionbutton);
		fpBottom.add(undobutton);
		fpBottom.add(menubutton);
		fpBottom.add(gobutton);

		//pp.setBounds(31, 54, PicturePanel.PP_X , PicturePanel.PP_Y);
		//pp.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0,0,0),1));
		pp.addMouseListener(mapListener);
		pp.addMouseMotionListener(mapListener);
		pp.setBackground(Color.BLACK);

		gm = new GameMenuPanel();
		gm.setVisible(false);
		GraphicsUtil.setBounds(gm, 285, 141, 170, 250);
		//gm.addMouseListener(this);
		//gm.addMouseMotionListener(this);
		getRootPane().getLayeredPane().add(gm , JLayeredPane.MODAL_LAYER);

		//layeredPane.add(gm, 0);
		//layeredPane.add(pp, 1);
		//layeredPane.add(fp, 2);

		//getContentPane().add(layeredPane);

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(
			new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					closeAction.actionPerformed(null);
				}
			}
		);

		this			.addKeyListener( this);
		cardsbutton		.addKeyListener( this);
		missionbutton		.addKeyListener( this);
		undobutton		.addKeyListener( this);
		menubutton		.addKeyListener( this);
		graphbutton		.addKeyListener( this);
		gobutton		.addKeyListener( this);

		cardsDialog = new CardsDialog(GameFrame.this, true, myrisk, pp);

                RiskUIUtil.center(cardsDialog);

		movedialog = new MoveDialog(this, false);

		RiskUIUtil.center(movedialog);

		// #################################################################################
		// sort out size and add to main panel

		Dimension d = GraphicsUtil.newDimension(740, 54);
		fp.setPreferredSize(d);
		fp.setMinimumSize(d);
		fp.setMaximumSize(d);

		d = GraphicsUtil.newDimension(740, 121);
		fpBottom.setPreferredSize(d);
		fpBottom.setMinimumSize(d);
		fpBottom.setMaximumSize(d);

		d = GraphicsUtil.newDimension(31,425);
		fpLeft.setPreferredSize(d);
		fpLeft.setMinimumSize(d);
		fpLeft.setMaximumSize(d);

		d = GraphicsUtil.newDimension(32,425);
		fpRight.setPreferredSize(d);
		fpRight.setMinimumSize(d);
		fpRight.setMaximumSize(d);

		JPanel flashPanel = new JPanel( new BorderLayout() );
		flashPanel.add(fp,BorderLayout.NORTH);
		flashPanel.add(pp);
		flashPanel.add(fpBottom,BorderLayout.SOUTH);
		flashPanel.add(fpLeft,BorderLayout.WEST);
		flashPanel.add(fpRight,BorderLayout.EAST);

		getContentPane().add( flashPanel );
	}

	public void setup(boolean s) {

            	try {
			pp.load();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

		gameState=0; // -1 or 0 means no input needed
		mapView=PicturePanel.VIEW_CONTINENTS;

		//gameStatus="";
		setGameStatus(null);

		note="";
		//c1Id = -1;

		localGame = s;

		closeAction.putValue(Action.NAME, resb.getString(localGame ? "game.menu.close" : "game.menu.leave"));

		repaintCountries();

                // disable all buttons at the start of the game
                AbstractButton[] buttons = new AbstractButton[] {savebutton,AutoEndGo,AutoDefend,cardsbutton,missionbutton,undobutton,gobutton};
                for (int c=0;c<buttons.length;c++) {
                    buttons[c].setEnabled(false);
                }
	}

	ActionListener buttonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if (e.getSource()==cardsbutton) {
				displayCards();
			}
			else if (e.getSource()==missionbutton) {
				displayMission();
			}
			else if (e.getSource()==undobutton) {
				doUndo();
			}
			else if (e.getSource()==menubutton) {
				displayMenu();
			}
			else if (e.getSource()==graphbutton) {
				displayGraph();
			}
			else if (e.getSource()==gobutton) {
				goOn();
			}
			else if (e.getSource()==savebutton) {

				String name = RiskUIUtil.getSaveFileName(
					GameFrame.this
					//RiskUtil.SAVES_DIR,
					//RiskFileFilter.RISK_SAVE_FILES
				);

				if (name!=null) {
					go("savegame " + name );
				}
			}
			else if (e.getSource()==resumebutton) {
				displayMenu();
			}
			else if (e.getSource()==AutoEndGo) {

				if ( AutoEndGo.isSelected() ) {
					go("autoendgo on");
				}
				else {
					go("autoendgo off");
				}
			}
			else if (e.getSource()==AutoDefend) {

				if ( AutoDefend.isSelected() ) {
					go("autodefend on");
				}
				else {
					go("autodefend off");
				}
			}
			else if (e.getSource()==helpbutton) {

				try {
					RiskUtil.openDocs( resb.getString("helpfiles.flash") );
				}
				catch(Exception er) {
					JOptionPane.showMessageDialog(GameFrame.this,"Unable to open manual: "+er.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	public void repaintCountries() {

		pp.repaintCountries( mapView );
/*
		if (mapView==1) {
			pp.repaintCountries( PicturePanel.VIEW_CONTINENTS );
		}
		else if (mapView==2) {
			pp.repaintCountries( PicturePanel.VIEW_OWNERSHIP );
		}
		else if (mapView==3) {
			pp.repaintCountries( PicturePanel.VIEW_BORDER_THREAT );
		}
		else if (mapView==4) {
			pp.repaintCountries( PicturePanel.VIEW_CARD_OWNERSHIP );
		}
		else if (mapView==5) {
			pp.repaintCountries( PicturePanel.VIEW_TROOP_STRENGTH );
		}
		else if (mapView==6) {
			pp.repaintCountries( PicturePanel.VIEW_CONNECTED_EMPIRE );
		}
*/
	}

	public void setGameStatus(String state) {

                int[] cols = null;
		if (state!=null) {
			// the colors for the bottom display r collected here
			cols = myrisk.getPlayerColors();
                        if (cols.length == 0) { cols = null; }
		}

		if (cols==null) {
			cols = new int[] { ColorUtil.GRAY };
		}


		gameStatus=state;
                colors = cols;

		repaint();
	}

	public void needInput(int s) {
		gameState=s;


                // if for some strange reason this dialog is open and we need some other input, close it
                // this can happen if we timeout in a game during battle won move stage
                if (gameState!=RiskGame.STATE_BATTLE_WON && movedialog.isVisible()) {
                    movedialog.exitForm();
                }


		String goButtonText=null;

		switch (gameState) {

			case RiskGame.STATE_TRADE_CARDS: {

				// after wiping out someone if you go into trade mode
				pp.setC1(255);
				pp.setC2(255);

                                if (myrisk.getGame().canEndTrade()) {
                                    goButtonText = resb.getString("game.button.go.endtrade");
                                }
                                note = getArmiesLeftText();
				break;
			}
			case RiskGame.STATE_PLACE_ARMIES: {
				if ( !myrisk.getGame().NoEmptyCountries() ) {
					goButtonText = resb.getString("game.button.go.autoplace");
				}
                                note = getArmiesLeftText();
				break;
			}
			case RiskGame.STATE_ATTACKING: {

				pp.setC1(255);
				pp.setC2(255);

				note = resb.getString("game.note.selectattacker");

				goButtonText = resb.getString("game.button.go.endattack");

				break;
			}
			case RiskGame.STATE_FORTIFYING: {

				note = resb.getString("game.note.selectsource");

				goButtonText = resb.getString("game.button.go.nomove");

				break;
			}
			case RiskGame.STATE_END_TURN: {

				goButtonText = resb.getString("game.button.go.endgo");

				break;
			}
			case RiskGame.STATE_GAME_OVER: {
                                if (myrisk.getGame().canContinue()) {
                                    goButtonText = resb.getString("game.button.go.continue");
                                }
                                else {
                                    if (localGame) {
                                            goButtonText = resb.getString("game.button.go.closegame");
                                    }
                                    else {
                                            goButtonText = resb.getString("game.button.go.leavegame");
                                    }
                                }
				break;
			}
			case RiskGame.STATE_SELECT_CAPITAL: {

				note = resb.getString("game.note.happyok");

				goButtonText = resb.getString("game.button.go.ok");

				break;
			}
			case RiskGame.STATE_BATTLE_WON: {

                                RiskGame game = myrisk.getGame();
                                openMove(game.getMustMove(), game.getAttacker().getColor(), game.getDefender().getColor(), false);
				movedialog.setVisible(true);

				break;
			}
			// for gameState 4 look in FlashRiskAdapter.java
			// for gameState 10 look in FlashRiskAdapter.java
			default: break;
		}





		if (goButtonText!=null) {
			gobutton.setEnabled(true);
			gobutton.setText(goButtonText);
		}
		else {
			gobutton.setEnabled(false);
			gobutton.setText("");
		}

		// can not use this as it sometimes just does not work, no idea why, mainly a vista problem
		//if (!(gobutton.getText().equals(""))) {
		//	gobutton.setEnabled(true);
		//}
		//else {
		//	gobutton.setEnabled(false);
		//}

                cardsbutton.setEnabled(true);
                missionbutton.setEnabled(true);

                if (localGame) {

                    if (gameState!=RiskGame.STATE_DEFEND_YOURSELF) {
                        undobutton.setEnabled(true);
                    }
                    savebutton.setEnabled(true);

                }

                AutoEndGo.setEnabled(true);
                AutoEndGo.setBackground( Color.white );
                AutoEndGo.setSelected( myrisk.getAutoEndGo() );

                AutoDefend.setEnabled(true);
                AutoDefend.setBackground( Color.white );
                AutoDefend.setSelected( myrisk.getAutoDefend() );

		repaint(); // SwingGUI has this here, if here then not needed in set status
	}

        public String getArmiesLeftText() {
                int l = myrisk.getGame().getCurrentPlayer().getExtraArmies();
                return RiskUtil.replaceAll( resb.getString("game.note.armiesleft"),"{0}", String.valueOf(l));
        }

	/**
	 * checks if the coordinates are in one of the tabs at the top of the window
	 */
	public int insideButton(int x, int y) {

                int W = 115;
                int H = 23;

                if (GraphicsUtil.insideButton(x, y, 24, 9, W, H)) {
                        return PicturePanel.VIEW_CONTINENTS;
                }
                if (GraphicsUtil.insideButton(x, y, 139, 9, W, H)) {
                        return PicturePanel.VIEW_OWNERSHIP;
                }
                if (GraphicsUtil.insideButton(x, y, 254, 9, W, H)) {
                        return PicturePanel.VIEW_BORDER_THREAT;
                }
                if (GraphicsUtil.insideButton(x, y, 369, 9, W, H)) {
                        return PicturePanel.VIEW_CARD_OWNERSHIP;
                }
                if (GraphicsUtil.insideButton(x, y, 484, 9, W, H)) {
                        return PicturePanel.VIEW_TROOP_STRENGTH;
                }
                if (GraphicsUtil.insideButton(x, y, 599, 9, W, H)) {
                        return PicturePanel.VIEW_CONNECTED_EMPIRE;
                }
		return -1;
	}

	/** calls the parser with the command  */

	private BattleDialog battledialog;

	public void setBattleDialog(BattleDialog bd) {
		battledialog=bd;
	}

	/**
	 * calls the parser
	 * @param command sends the input command to the parser via a string
	 */
	public void go(String command) {

		blockInput();

		myrisk.parser(command);
	}

	public void blockInput() {

		pp.setHighLight(255);

		//c1Id = -1;

		if (gameState==RiskGame.STATE_ROLLING || gameState==RiskGame.STATE_DEFEND_YOURSELF) {

			//this does not close it, just resets its params
			battledialog.blockInput();
		}


		if (gameState==RiskGame.STATE_BATTLE_WON || gameState==RiskGame.STATE_FORTIFYING) {

			// this hides the dailog
			movedialog.exitForm();
		}

		if (gameState!=RiskGame.STATE_PLACE_ARMIES || !myrisk.getGame().getSetupDone() ) { noInput(); }

	}

	public void noInput() {

                // if we timeout on our turn, we need to close this dialog
                if(movedialog.isVisible()) {
                    movedialog.exitForm();
                }

		cardsbutton.setEnabled(false);
		missionbutton.setEnabled(false);
		undobutton.setEnabled(false);

		savebutton.setEnabled(false);

		AutoEndGo.setEnabled(false);
		AutoEndGo.setBackground( Color.lightGray );

		AutoDefend.setEnabled(false);
		AutoDefend.setBackground( Color.lightGray );

		gobutton.setText("");
		gobutton.setEnabled(false);

		note="";
		gameState=0;

	}

	/**
	 * Returns an image of a given country
	 * @param a Index position of country
	 */
	public BufferedImage getCountryImage(int a) {

		return pp.getCountryImage(a, true);

	}

	public void openMove(int min, int c1num, int c2num, boolean tacmove) {

		int src = myrisk.hasArmiesInt( c1num );
		int des = myrisk.hasArmiesInt( c2num );
		BufferedImage c1img = pp.getCountryImage(c1num ,true);
		BufferedImage c2img = pp.getCountryImage(c2num ,true);
		Country country1 = myrisk.getGame().getCountryInt( c1num);
		Country country2 = myrisk.getGame().getCountryInt( c2num);

		int color = myrisk.getCurrentPlayerColor();

		movedialog.setup(tacmove,       min,      src, des, c1img, c2img, country1, country2, new Color( color ) );

	}

        public void mapClick(final int[] countries,MouseEvent e) {

            Object oldnote = note;

            if (gameState == RiskGame.STATE_PLACE_ARMIES) {
                if (countries.length==1) {
                    if ( e.getModifiers() == java.awt.event.InputEvent.BUTTON1_MASK ) {
                        go( "placearmies " + countries[0] + " 1" );
                    }
                    else {
                        go( "placearmies " + countries[0] + " 10" );
                    }
                }
            }
            else if (gameState == RiskGame.STATE_ATTACKING) {

                if (countries.length==0) {
                    note=resb.getString("game.note.selectattacker");
                }
                else if (countries.length == 1) {
                    note=resb.getString("game.note.selectdefender");
                }
                else {
                    go("attack " + countries[0] + " " + countries[1]);
                    note=resb.getString("game.note.selectattacker");
                }

            }
            else if (gameState == RiskGame.STATE_FORTIFYING) {
                if (countries.length==0) {
                    note=resb.getString("game.note.selectsource");
                }
                else if (countries.length==1) {
                    note=resb.getString("game.note.selectdestination");
                }
                else {
                    note="";
                    repaint();

                    openMove(1,countries[0] , countries[1], true);

                    // this comes in on the mouse event thread
                    // we need to make this dialog blocking so the user
                    // can not click on the map while this dialog is up
                    movedialog.setModal(true);
                    movedialog.setVisible(true);
                    movedialog.setModal(false);
                    // now we set it back to a none-blocking dialog
                    // for use with the move of armies after a attack

                    // clean up
                    pp.setC1(255);
                    pp.setC2(255);
                    note=resb.getString("game.note.selectsource");

                }
            }
            else if (gameState == RiskGame.STATE_SELECT_CAPITAL) {
                // do nothing ??
            }

            if (oldnote!=note) {
                repaint();
            }
        }


	/**
	 * the map view has changed
	 * (normal, border threat, ...)
	 * @param click		The tab number the user has clicked on
	 */
	private void setMapView(int click) {
		mapView = click;
		repaintCountries();
		repaint();
	}//private void setMapView(int click)


	/**
	 * displays the cards dialog
	 */
	private void displayCards() {

		cardsDialog.setup( (gameState==RiskGame.STATE_TRADE_CARDS) );

		cardsDialog.setVisible(true);
	}



	/**
	 * displays the mission window
	 */
	private void displayMission() {
		MissionDialog missiondialog = new MissionDialog(GameFrame.this, true, myrisk);

		Dimension frameSize = getSize();
		Dimension aboutSize = missiondialog.getSize();
		int x = getLocation().x + (frameSize.width - aboutSize.width) / 2;
		int y = getLocation().y + (frameSize.height - aboutSize.height) / 2;
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		missiondialog.setLocation(x, y);

		missiondialog.setVisible(true);
	}//private void displayMission()



	/**
	 * does an "undo"
	 */
	private void doUndo() {

                pp.setC1(PicturePanel.NO_COUNTRY);
                pp.setC2(PicturePanel.NO_COUNTRY);

		go("undo");
	}//private void doUndo()



	/**
	 * displays the menu
	 */
	private void displayMenu() {

	    if(menuOn) {

                setMenuButtonAction(closebutton, null);
                
		gm.setVisible(false);
		pp.addMouseListener(mapListener);
		pp.addMouseMotionListener(mapListener);

		menuOn=false;
	    }
	    else {

                setMenuButtonAction(closebutton, extraAction != null && extraAction.isEnabled() ? extraAction : closeAction);

		pp.removeMouseListener(mapListener);
		pp.removeMouseMotionListener(mapListener);

		if (myrisk.getGame().getCurrentPlayer()!=null) {

			AutoEndGo.setSelected( myrisk.getAutoEndGo() );
			AutoDefend.setSelected( myrisk.getAutoDefend() );
		}

		gm.setVisible(true);
		menuOn=true;
	    }
	}

        static void setMenuButtonAction(AbstractButton button, Action action) {
                    Icon normal = button.getIcon();
                    Icon rollover = button.getRolloverIcon();
                    Icon selected = button.getSelectedIcon();
                    Icon rolloverSelected = button.getRolloverSelectedIcon();
                    Icon pressed = button.getPressedIcon();
                    Icon disabled = button.getDisabledIcon();

                    button.setAction(action);
                    
                    button.setIcon(normal);
                    button.setRolloverIcon(rollover);
                    button.setSelectedIcon(selected);
                    button.setRolloverSelectedIcon(rolloverSelected);
                    button.setPressedIcon(pressed);
                    button.setDisabledIcon(disabled);
        }
        
	public void displayGraph() {

		if (graphOn) {
			graphdialog.setVisible(false);
			graphOn=false;
		}
		else {

			if (graphdialog==null) {

				graphdialog = new StatsDialog(GameFrame.this, false, myrisk);

				Dimension frameSize = (GameFrame.this).getPreferredSize();
				Dimension graphSize = graphdialog.getSize();
				int a = (GameFrame.this).getLocation().x + (frameSize.width - graphSize.width) / 2;
				int b = (GameFrame.this).getLocation().y + (frameSize.height - graphSize.height) / 2;
				if (a < 0) a = 0;
				if (b < 0) b = 0;
				graphdialog.setLocation(a, b);

			}

			graphdialog.setVisible(true);
			graphOn=true;
		}

	}

	/**
	 * the user has clicked the "go" button
	 */
	private void goOn() {
		if (gameState==RiskGame.STATE_TRADE_CARDS) {
			go("endtrade");
		}
		else if (gameState==RiskGame.STATE_PLACE_ARMIES) {
			go("autoplace");
		}
		else if (gameState==RiskGame.STATE_ATTACKING) {
			pp.setC1(255);
			go("endattack");
		}
		else if (gameState==RiskGame.STATE_FORTIFYING) {
			pp.setC1(255);
			go("nomove");
		}
		else if (gameState==RiskGame.STATE_END_TURN) {
			go("endgo");
		}
		else if (gameState==RiskGame.STATE_GAME_OVER) {
                        RiskGame game = myrisk.getGame();
                        if (game!=null && game.canContinue()) {
                            go("continue");
                        }
                        else {
                            closeAction.actionPerformed(null);
                        }
		}
		else if (gameState == RiskGame.STATE_SELECT_CAPITAL) {
                        int c1Id = pp.getC1();
			pp.setC1(255);
			go("capital " + c1Id);
		}
	}//private void goOn()


	public static JButton makeRiskButton(Image normal, Image pressed, Image hover, Image disabled) {
		JButton button = new JButton();
		NewGameFrame.sortOutButton( button, normal, hover, pressed );
		button.setDisabledIcon( new ImageIcon( disabled ) );
		return button;
	}

	/**
	 * The user has released a key
	 */
	public void keyReleased( KeyEvent event ) {

		//if (event.isControlDown()) {

			//with CTRL down

			// when the ctrl+number bit was here is gave the "?" key when the number 6 was pressed

		//} else {

			switch (event.getKeyCode())
			{
				case KeyEvent.VK_1: this.setMapView(PicturePanel.VIEW_CONTINENTS); break;
				case KeyEvent.VK_2: this.setMapView(PicturePanel.VIEW_OWNERSHIP); break;
				case KeyEvent.VK_3: this.setMapView(PicturePanel.VIEW_BORDER_THREAT); break;
				case KeyEvent.VK_4: this.setMapView(PicturePanel.VIEW_CARD_OWNERSHIP); break;
				case KeyEvent.VK_5: this.setMapView(PicturePanel.VIEW_TROOP_STRENGTH); break;
				case KeyEvent.VK_6: this.setMapView(PicturePanel.VIEW_CONNECTED_EMPIRE); break;

					// can not use this as it may be not a int
					//Integer.parseInt( event.getKeyChar() + ""));

			}

			//no modifier button pressed
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_C:
					//cards
					if (gameState!=RiskGame.STATE_NEW_GAME) this.displayCards();
					break;
				case KeyEvent.VK_M:
					//mission
					if (gameState!=RiskGame.STATE_NEW_GAME) this.displayMission();
					break;
				case KeyEvent.VK_U:
					//undo
					if (gameState!=RiskGame.STATE_NEW_GAME && gameState!=RiskGame.STATE_DEFEND_YOURSELF) this.doUndo();
					break;
				case KeyEvent.VK_F10:
					this.displayMenu();
					//menu
					break;
				case KeyEvent.VK_G:
					//go button
					if (gameState!=RiskGame.STATE_NEW_GAME) this.goOn();
					break;
			}
		//}
	}//public void keyReleased( KeyEvent event )

	public void keyTyped( KeyEvent event ) {}
	public void keyPressed( KeyEvent event ) {}

	/**
	 * the game menu
	 * which you get when pressing the "Menu" button
	 */
	class GameMenuPanel extends JPanel {

		public GameMenuPanel() {

			setLayout(null);

			int w=100;

			savebutton = makeRiskButton(gameImg.getSubimage(480, 373, w, 21), gameImg.getSubimage(380, 373, w, 21), gameImg.getSubimage(280, 373, w, 21), gameImg.getSubimage(180, 373, w, 21) );
			savebutton.setText(resb.getString("game.menu.save"));
			GraphicsUtil.setBounds(savebutton, 35, 50, w, 20);
			savebutton.addActionListener( buttonActionListener );

                        // close button text and action set when menu is opened
			closebutton = makeRiskButton(gameImg.getSubimage(480, 373, w, 21), gameImg.getSubimage(380, 373, w, 21), gameImg.getSubimage(280, 373, w, 21), gameImg.getSubimage(180, 373, w, 21) );
			GraphicsUtil.setBounds(closebutton, 35, 80, w, 20);





			AutoEndGo = new JCheckBox(resb.getString("game.menu.autoendgo"));
			AutoEndGo.setToolTipText( resb.getString("game.menu.autoendgo"));

			AutoEndGo.setMargin(new Insets(0,0,0,0));
			AutoEndGo.setBorderPainted(false);
			AutoEndGo.setFocusPainted(false);

			GraphicsUtil.setBounds(AutoEndGo, 35, 110, w, 20);
			AutoEndGo.addActionListener( buttonActionListener );
			AutoEndGo.setBackground( Color.lightGray );






			AutoDefend = new JCheckBox(resb.getString("game.menu.autodefend"));
			AutoDefend.setToolTipText( resb.getString("game.menu.autodefend"));

			AutoDefend.setMargin(new Insets(0,0,0,0));
			AutoDefend.setBorderPainted(false);
			AutoDefend.setFocusPainted(false);

			GraphicsUtil.setBounds(AutoDefend, 35, 140, w, 20);
			AutoDefend.addActionListener( buttonActionListener );
			AutoDefend.setBackground( Color.lightGray );





			helpbutton = makeRiskButton(gameImg.getSubimage(480, 373, w, 21), gameImg.getSubimage(380, 373, w, 21), gameImg.getSubimage(280, 373, w, 21), gameImg.getSubimage(180, 373, w, 21) );
			helpbutton.setText(resb.getString("game.menu.manual"));
			GraphicsUtil.setBounds(helpbutton, 35, 170, w, 20);
			helpbutton.addActionListener( buttonActionListener );




			resumebutton = makeRiskButton(gameImg.getSubimage(480, 373, w, 21), gameImg.getSubimage(380, 373, w, 21), gameImg.getSubimage(280, 373, w, 21), gameImg.getSubimage(180, 373, w, 21) );
			resumebutton.setText(resb.getString("game.menu.closemenu"));
			GraphicsUtil.setBounds(resumebutton, 35, 200, w, 20);
			resumebutton.addActionListener( buttonActionListener );

			add(savebutton);
			add(AutoDefend);
			add(helpbutton);
			add(AutoEndGo);
			add(closebutton);
			add(resumebutton);

		}

		public void paintComponent(Graphics g) {

			Graphics2D g2 = (Graphics2D)g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
			g2.setComposite(ac);

//					  destination		source
			GraphicsUtil.drawImage(g2, gameImg, 0, 0, 170, 250,     633, 175, 803, 425, this); // top

			FontRenderContext frc = g2.getFontRenderContext();

			Font font = new java.awt.Font("Arial", java.awt.Font.BOLD, getFont().getSize() + 11);  // 13 + 11 = 24
			g2.setColor( Color.black );
			TextLayout tl;

			tl = new TextLayout( resb.getString("game.menu.title") , font, frc);
			tl.draw( g2, (float) (GraphicsUtil.scale(85) - tl.getBounds().getWidth()/2), (float) GraphicsUtil.scale(40));
		}
	}
}

// Yura Mamyrin, Group D

package net.yura.domination.ui.flashgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.yura.domination.engine.Risk;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.core.Country;
import net.yura.swing.GraphicsUtil;
import net.yura.domination.engine.translation.TranslationBundle;

/**
 * <p> Battle Dialog for FlashGUI </p>
 * @author Yura Mamyrin
 */
public class BattleDialog extends JDialog implements MouseListener {

	private GameFrame gui;
	private Risk myrisk;

	private BufferedImage c1img;
	private BufferedImage c2img;

	private int c1num;
	private int c2num;

	private BufferedImage Battle;
	private BufferedImage Back;

	private JButton button;
	private JButton retreat;
        private AbstractButton annihilate;

	private Country country1;
	private Country country2;

	private Color color1;
	private Color color2;

	private boolean canRetreat;

	private javax.swing.Timer timer;
	private int[] att;
	private int[] def;

	private int max; // indicates input is needed if > 0

	private int noda,nodd;
        private boolean spinA,spinD;

	private BufferedImage[] attackerSpins;
	private BufferedImage[] defenderSpins;

	private java.util.ResourceBundle resb;
	private JPanel battle;

	/**
	 * Creates a new BattleDialog
	 * @param parent decides the parent of the frame
	 * @param modal
	 * @param r the risk main program
	 */

	public BattleDialog(GameFrame parent, boolean modal, Risk r) {
		super(parent, modal);
		gui = parent;
		myrisk = r;

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		Battle = RiskUIUtil.getUIImage(this.getClass(),"battle.jpg");

		Back = Battle.getSubimage(0, 0, 480, 350);

                int x=580;
                int i=0;
		int w=29;
		int h=29;

		attackerSpins = new BufferedImage[6];

		attackerSpins[0] = Battle.getSubimage(x, h*i++, w, h);
		attackerSpins[1] = Battle.getSubimage(x, h*i++, w, h);
		attackerSpins[2] = Battle.getSubimage(x, h*i++, w, h);
		attackerSpins[3] = Battle.getSubimage(x, h*i++, w, h);
		attackerSpins[4] = Battle.getSubimage(x, h*i++, w, h);
		attackerSpins[5] = Battle.getSubimage(x, h*i++, w, h);

		defenderSpins = new BufferedImage[6];

		defenderSpins[0] = Battle.getSubimage(x, h*i++, w, h);
		defenderSpins[1] = Battle.getSubimage(x, h*i++, w, h);
		defenderSpins[2] = Battle.getSubimage(x, h*i++, w, h);
		defenderSpins[3] = Battle.getSubimage(x, h*i++, w, h);
		defenderSpins[4] = Battle.getSubimage(x, h*i++, w, h);
		defenderSpins[5] = Battle.getSubimage(x, h*i++, w, h);

		initGUI();
		pack();
	}

	/** This method is called from within the constructor to initialize the dialog. */
	private void initGUI() {
		resb = TranslationBundle.getBundle();

		setResizable(false);

		battle = new BattlePanel();
		battle.setLayout(null);
		battle.addMouseListener(this);

		Dimension bSize = GraphicsUtil.newDimension(480, 350);

		battle.setPreferredSize( bSize );
		battle.setMinimumSize( bSize );
		battle.setMaximumSize( bSize );

		int w=88;
		int h=31;

		button = GameFrame.makeRiskButton( Battle.getSubimage(196, 270, w, h), Battle.getSubimage(481, 242, w, h), Battle.getSubimage(481, 210, w, h), Battle.getSubimage(481, 274, w, h) );
		button.setText(resb.getString("battle.roll"));
		GraphicsUtil.setBounds(button, 196, 270, 88, 31);

		retreat = GameFrame.makeRiskButton( Battle.getSubimage(487, 110, w, h), Battle.getSubimage(481, 178, w, h), Battle.getSubimage(481, 146, w, h), Battle.getSubimage(487, 110, w, h) );
		retreat.setText(resb.getString("battle.retreat"));
		GraphicsUtil.setBounds(retreat, 342, 270, 88, 31);

                annihilate = new JToggleButton(resb.getString("battle.annihilate"));
                NewGameFrame.sortOutButton( annihilate, Battle.getSubimage(485, 5, w, h), Battle.getSubimage(481, 73, w, h), Battle.getSubimage(481, 41, w, h) );
		GraphicsUtil.setBounds(annihilate, 50, 270, 88, 31);

		button.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            gui.go( "roll " + (canRetreat?noda:nodd) );
                        }
                    }
		);

		retreat.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            gui.go( "retreat" );
                        }
                    }
		);

                annihilate.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (annihilate.isSelected()) {
                                gui.go( "roll " + (canRetreat?noda:nodd) );
                            }
                        }
                    }
		);

		battle.add(retreat);
		battle.add(button);
                battle.add(annihilate);

		getContentPane().add(battle);

		timer = new javax.swing.Timer(10, spinDiceAction());


		addWindowListener(
			new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					if (canRetreat) {
						gui.go( "retreat" );
					}
				}
			}
		);
	}

	/**
	 * Spins the images of the dice
	 * @return Action
	 */
	public Action spinDiceAction() {
		return new AbstractAction("spin dice action") {
			public void actionPerformed (ActionEvent e) {
				// repaint here slows the game down a lot!
				//repaint();
				drawDiceAnimated( battle.getGraphics() );
			}
		};
	}

	/*
	 * @param a the number of attacking armies
	 * @param b the number of defending armies
	 * @param ai the image of attacker
	 * @param bi the image of defender
	 * @param country1	attacking country
	 * @param country2	defending country
	 * @param c1 color of the attacker
	 * @param c2 color of the defender
	 */
	public void setup(int a, int b, BufferedImage ai, BufferedImage bi, Country country1, Country country2, Color c1, Color c2) {
		c1num=a;
		c2num=b;

		c1img = ai;
		c2img = bi;

		this.country1 = country1;
		this.country2 = country2;

		color1=c1;
		color2=c2;

		att=null;
		def=null;

		noda=0;
		nodd=0;

                spinA = false;
                spinD = false;

                annihilate.setSelected(false);
                
                blockInput();
        }

        public void blockInput() {
                button.setEnabled(false);
                retreat.setVisible(false);
                annihilate.setVisible(false);
		canRetreat=false;
		max=0;
		setTitle(resb.getString("battle.title"));
	}

	/**
	 * Sets number of attacking dice
	 * @param n number of dice
	 */
	public void setNODAttacker(int n) {
		att=null;
		def=null;
		noda = n;
                spinA = true;
		battle.repaint();
		timer.start();
	}

	/**
	 * Sets number of defending dice
	 * @param n number of dice
	 */
	public void setNODDefender(int n) {
		nodd = n;
                spinD = true;
                battle.repaint();
	}

	/**
	 * Shows the dice results
	 * @param atti the attacking results
	 * @param defi the defending results
	 */
	public void showDiceResults(int[] atti, int[] defi) {
		if( timer.isRunning() ) {
			timer.stop();
		}
		att=atti;
		def=defi;
                spinA = false;
                spinD = false;
		battle.repaint();
	}

	/**
	 * Checks to see if input is needed
	 * @param n Maximum number of dice
	 * @param c If you can retreat
	 */
	public void needInput(int n, boolean c) {
		max=n;
		canRetreat=c;

		if (canRetreat) {
                        if (noda==0 || noda > max) {
                            noda = max;
                        }
		}
		else {
                        if (nodd==0 || nodd > max) {
                            nodd = max;
                        }
		}

		att=null;
		def=null;

		button.setEnabled(true);
                setTitle(resb.getString(canRetreat?"battle.select.attack":"battle.select.defend"));
                retreat.setVisible(canRetreat);
                annihilate.setVisible(canRetreat);

		battle.repaint();

                if (canRetreat && annihilate.isSelected()) {
                    // dont want to "click" on kill as it will deselect the button
                    gui.go("roll "+ (canRetreat?noda:nodd) );
                }
	}

	private static Random r = new Random();

	class BattlePanel extends JPanel {

		/**
		 * Paints the frame
		 * @param g The graphics
		 */
		public void paintComponent(Graphics g) {
			//super.paintComponent(g);

			GraphicsUtil.drawImage(g, Back ,0 ,0 ,this);

			if (canRetreat) {
				GraphicsUtil.drawImage(g, Battle.getSubimage(481, 105, 98, 40), 336, 265, this); // retreat
                                GraphicsUtil.drawImage(g, Battle.getSubimage(481, 0, 98, 40), 46, 265, this); // annihilate
			}

                        MoveDialog.paintMove(g,
                                c1img, c2img,
                                color1, color2, 
                                country1.getName(), country2.getName(),
                                myrisk.hasArmiesInt(c1num),myrisk.hasArmiesInt(c2num) );

                        g.setColor( Color.BLACK );
                        GraphicsUtil.drawStringCenteredAt(g, resb.getString("battle.select.dice"), 240, 320);

                        drawDiceSelect(g);
			drawDiceAnimated(g);
			drawDiceResults(g);
		}
	}

        final static int MINI_DICE_X=481;
        final static int MINI_DICE_Y=306;
        final static int MINI_DICE_WIDTH=21;
        final static int MINI_DICE_HEIGHT=21;
        
        private void drawDiceSelect(Graphics g) {

            // this is the max defend dice allowed for this battle
            int deadDiceD = myrisk.hasArmiesInt(c2num);
            if (deadDiceD > myrisk.getGame().getMaxDefendDice()) {
                deadDiceD = myrisk.getGame().getMaxDefendDice();
            }

            BufferedImage liveA = Battle.getSubimage(MINI_DICE_X, MINI_DICE_Y, MINI_DICE_WIDTH, MINI_DICE_HEIGHT);
            BufferedImage liveD = Battle.getSubimage(MINI_DICE_X, MINI_DICE_Y+MINI_DICE_HEIGHT, MINI_DICE_WIDTH, MINI_DICE_HEIGHT);
            BufferedImage deadA = Battle.getSubimage(MINI_DICE_X+MINI_DICE_WIDTH, MINI_DICE_Y, MINI_DICE_WIDTH, MINI_DICE_HEIGHT);
            BufferedImage deadD = Battle.getSubimage(MINI_DICE_X+MINI_DICE_WIDTH, MINI_DICE_Y+MINI_DICE_HEIGHT, MINI_DICE_WIDTH, MINI_DICE_HEIGHT);

            // if we need input
            if (max != 0) {
                // selecting the number of attacking dice
                if (canRetreat) {
                    GraphicsUtil.drawImage(g, liveA, 120, DRAW_DICE_Y + 4, this);

                    if (noda > 1) {
                        GraphicsUtil.drawImage(g, liveA, 120, DRAW_DICE_Y + 35, this);
                    }
                    else if (max > 1) {
                        GraphicsUtil.drawImage(g, deadA, 120, DRAW_DICE_Y + 35, this);
                    }

                    if (noda > 2) {
                        GraphicsUtil.drawImage(g, liveA, 120, DRAW_DICE_Y + 66, this);
                    }
                    else if (max > 2) {
                        GraphicsUtil.drawImage(g, deadA, 120, DRAW_DICE_Y + 66, this);
                    }

                    // draw the dead dice
                    GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 4, this);
                    if (deadDiceD > 1) {
                        GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 35, this);
                    }
                    if (deadDiceD > 2) {
                        GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 66, this);
                    }
                }
                // selecting the number of dice to defend
                else {
                    GraphicsUtil.drawImage(g, liveD, 339, DRAW_DICE_Y + 4, this);

                    if (nodd > 1) {
                        GraphicsUtil.drawImage(g, liveD, 339, DRAW_DICE_Y + 35, this);
                    }
                    else if (max > 1) {
                        GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 35, this);
                    }

                    if (nodd > 2) {
                        GraphicsUtil.drawImage(g, liveD, 339, DRAW_DICE_Y + 66, this);
                    }
                    else if (max > 2) {
                        GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 66, this);
                    }
                }
            }
            // battle open and waiting for the attacker to select there number of dice
            else if (att == null && def == null && !spinD) {

                    // draw the dead dice
                    GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 4, this);
                    if (deadDiceD > 1) {
                        GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 35, this);
                    }
                    if (deadDiceD > 2) {
                        GraphicsUtil.drawImage(g, deadD, 339, DRAW_DICE_Y + 66, this);
                    }

                    if (!spinA) {

                            // draw dead dice for attacker
                            int deadDiceA = myrisk.hasArmiesInt(c1num)-1;
                            // we assume that the attacker can attack with max of 3 dice

                            GraphicsUtil.drawImage(g, deadA, 120, DRAW_DICE_Y + 4, this);
                            if (deadDiceA > 1) {
                                    GraphicsUtil.drawImage(g, deadA, 120, DRAW_DICE_Y + 35, this);
                            }
                            if (deadDiceA > 2) {
                                    GraphicsUtil.drawImage(g, deadA, 120, DRAW_DICE_Y + 66, this);
                            }
                    }
            }
        }

        final static int DRAW_DICE_Y = 170;
        
        public void drawDiceAnimated(Graphics g) {
            if (spinA) {
                    GraphicsUtil.drawImage(g, attackerSpins[ r.nextInt( 6 ) ] , 116, DRAW_DICE_Y, this);

                    if (noda > 1) {
                            GraphicsUtil.drawImage(g, attackerSpins[ r.nextInt( 6 ) ] , 116, DRAW_DICE_Y+31, this);
                    }
                    if (noda > 2) {
                            GraphicsUtil.drawImage(g, attackerSpins[ r.nextInt( 6 ) ] , 116, DRAW_DICE_Y+62, this);
                    }
                    //g.drawString("ROLLING ATTACKER " + noda +"    " + Math.random() , 50, 100);

                    if (spinD) {
                            GraphicsUtil.drawImage(g, defenderSpins[ r.nextInt( 6 ) ] , 335, DRAW_DICE_Y, this);

                            if (nodd > 1) {
                                    GraphicsUtil.drawImage(g, defenderSpins[ r.nextInt( 6 ) ] , 335, DRAW_DICE_Y+31, this);
                            }
                            if (nodd > 2) {
                                    GraphicsUtil.drawImage(g, defenderSpins[ r.nextInt( 6 ) ] , 335, DRAW_DICE_Y+62, this);
                            }
                            //g.drawString("ROLLING DEFENDER " + nodd +"    " + Math.random(), 300, 100);
                    }
            }
	}

        private void drawDiceResults(Graphics g2) {

            // just in case in the middle of the draw the att and def get set to null
            int[] atti=att;
            int[] defi=def;

            if (atti != null && defi != null ) {

                    if (defi[0] >= atti[0]) {
                            g2.setColor( Color.blue );
                            g2.fillPolygon(GraphicsUtil.newPolygon(
                                    new int[] {339, 339, 140}, 
                                    new int[] {DRAW_DICE_Y + 4, DRAW_DICE_Y + 24, DRAW_DICE_Y + 14}));
                    }
                    else {
                            g2.setColor( Color.red );
                            g2.fillPolygon(GraphicsUtil.newPolygon(
                                    new int[] {140, 140, 339}, 
                                    new int[] {DRAW_DICE_Y + 4, DRAW_DICE_Y + 24, DRAW_DICE_Y + 14}));
                    }

                    if (atti.length > 1 && defi.length > 1) {

                            if (defi[1] >= atti[1]) {
                                    g2.setColor( Color.blue );
                                    g2.fillPolygon(GraphicsUtil.newPolygon(
                                            new int[] {339, 339, 140}, 
                                            new int[] {DRAW_DICE_Y + 35, DRAW_DICE_Y + 55, DRAW_DICE_Y + 45}));
                            }
                            else {
                                    g2.setColor( Color.red );
                                    g2.fillPolygon(GraphicsUtil.newPolygon(
                                            new int[] {140, 140, 339}, 
                                            new int[] {DRAW_DICE_Y + 35, DRAW_DICE_Y + 55, DRAW_DICE_Y + 45}));
                            }
                    }

                    if (atti.length > 2 && defi.length > 2) {

                            if (defi[2] >= atti[2]) {
                                    g2.setColor( Color.blue );
                                    g2.fillPolygon(GraphicsUtil.newPolygon(
                                            new int[] {339, 339, 140}, 
                                            new int[] {DRAW_DICE_Y + 66, DRAW_DICE_Y + 86, DRAW_DICE_Y + 76}));
                            }
                            else {
                                    g2.setColor( Color.red );
                                    g2.fillPolygon(GraphicsUtil.newPolygon(
                                            new int[] {140, 140, 339}, 
                                            new int[] {DRAW_DICE_Y + 66, DRAW_DICE_Y + 86, DRAW_DICE_Y + 76}));
                            }
                    }


                    // draw attacker dice
                    drawDice(true, atti[0] , 120, DRAW_DICE_Y+4, g2 );

                    if (atti.length > 1) {
                            drawDice(true, atti[1] , 120, DRAW_DICE_Y+35, g2 );
                    }
                    if (atti.length > 2) {
                            drawDice(true, atti[2] , 120, DRAW_DICE_Y+66, g2 );
                    }

                    // draw defender dice
                    drawDice(false, defi[0] , 339, DRAW_DICE_Y+4, g2 );

                    if (defi.length > 1) {
                            drawDice(false, defi[1] , 339, DRAW_DICE_Y+35, g2 );
                    }

                    if (defi.length > 2) {
                            drawDice(false, defi[2] , 339, DRAW_DICE_Y+66, g2 );
                    }
            }
        }

	/**
	 * Gets the dice
	 * @param isAttacker if the dice is an attacker
	 * @param result Result of the dice
	 */
	public void drawDice(boolean isAttacker, int result,final int dx,final int dy,Graphics g) {

                g.translate(GraphicsUtil.scale(dx), GraphicsUtil.scale(dy));

		if (isAttacker) {
			GraphicsUtil.drawImage(g, Battle.getSubimage(MINI_DICE_X, MINI_DICE_Y, MINI_DICE_WIDTH, MINI_DICE_HEIGHT), 0, 0, this);
		}
		else {
			GraphicsUtil.drawImage(g, Battle.getSubimage(MINI_DICE_X, MINI_DICE_Y+MINI_DICE_HEIGHT, MINI_DICE_WIDTH, MINI_DICE_HEIGHT), 0, 0, this);
		}

		int size=3;

                g.setColor( new Color(255, 255, 255, 200) );

		if (result==0) {

			GraphicsUtil.fillOval(g, 9, 9, size, size);
		}
		else if (result==1) {

			GraphicsUtil.fillOval(g, 3, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 15, size, size);
		}
		else if (result==2) {

			GraphicsUtil.fillOval(g, 3, 3, size, size);
			GraphicsUtil.fillOval(g, 9, 9, size, size);
			GraphicsUtil.fillOval(g, 15, 15, size, size);
		}
		else if (result==3) {

			GraphicsUtil.fillOval(g, 3, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 15, size, size);
			GraphicsUtil.fillOval(g, 3, 15, size, size);
		}
		else if (result==4) {

			GraphicsUtil.fillOval(g, 3, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 15, size, size);
			GraphicsUtil.fillOval(g, 3, 15, size, size);
			GraphicsUtil.fillOval(g, 9, 9, size, size);
		}
		else if (result==5) {

			GraphicsUtil.fillOval(g, 3, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 3, size, size);
			GraphicsUtil.fillOval(g, 15, 15, size, size);
			GraphicsUtil.fillOval(g, 3, 15, size, size);
			GraphicsUtil.fillOval(g, 9, 3, size, size);
			GraphicsUtil.fillOval(g, 9, 15, size, size);
		}

		g.translate(-GraphicsUtil.scale(dx), -GraphicsUtil.scale(dy));
	}

	/**
	 * Checks where the mouse was clicked
	 * @param x x co-ordinate
	 * @param y y co-ordinate
	 * @return int type of button
	 */
	public int insideButton(int x, int y) {
                int ax=120;
                int dx=339;
		int W=21;
		int H=21;

		if (GraphicsUtil.insideButton(x, y, ax, DRAW_DICE_Y + 4, W, H)) {
			return 1;
		}
		if (GraphicsUtil.insideButton(x, y, ax, DRAW_DICE_Y + 35, W, H)) {
			return 2;
		}
		if (GraphicsUtil.insideButton(x, y, ax, DRAW_DICE_Y + 66, W, H)) {
			return 3;
		}
		if (GraphicsUtil.insideButton(x, y, dx, DRAW_DICE_Y + 4, W, H)) {
			return 4;
		}
		if (GraphicsUtil.insideButton(x, y, dx, DRAW_DICE_Y + 35, W, H)) {
			return 5;
		}
                if (GraphicsUtil.insideButton(x, y, dx, DRAW_DICE_Y + 66, W, H)) {
                        return 6;
                }

		return 0;
	}

	/**
	 * Works out where the mouse was clicked
	 * @param e A mouse event
	 */
	public void mouseClicked(MouseEvent e) {
		int click=insideButton(e.getX(),e.getY());
		if (max != 0) {
                    if (canRetreat) {
			if (click == 1) { noda=1; }
			if (click == 2 && max > 1) { noda=2; }
			if (click == 3 && max > 2) { noda=3; }
                    }
                    else {
			if (click == 4) { nodd=1; }
			if (click == 5 && max > 1) { nodd=2; }
			if (click == 6 && max > 2) { nodd=3; }
                    }
                    battle.repaint();
		}
	}

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }

	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

}

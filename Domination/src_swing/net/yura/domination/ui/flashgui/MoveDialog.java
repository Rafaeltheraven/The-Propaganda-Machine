// Yura Mamyrin, Group D

package net.yura.domination.ui.flashgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.yura.domination.engine.RiskUIUtil;
import net.yura.domination.engine.core.Country;
import net.yura.swing.GraphicsUtil;
import net.yura.domination.engine.translation.TranslationBundle;

/**
 * <p> Move Dialog for FlashGUI </p>
 * @author Yura Mamyrin
 */

public class MoveDialog extends JDialog {

	private GameFrame gui;
	private boolean tacmove;

	private BufferedImage Move;
	private BufferedImage MoveBack;

	private BufferedImage c1img;
	private BufferedImage c2img;
	private Country country1;
	private Country country2;

	private int move;
	private int csrc;
	private int cdes;

	private Color color;

	private JSlider slider;
	private java.util.ResourceBundle resb;
	private JButton cancel;
	private final static Polygon arrow;
	private movePanel movepanel;

        static {
		int x=110;
		int y=40;
		int xCoords[] = {x+60, x+130, x+130, x+200, x+130, x+130, x+60};
		int yCoords[] = {y+40,  y+40,  y+20,  y+60, y+100,  y+80, y+80};
		arrow = GraphicsUtil.newPolygon(xCoords, yCoords);
        }
        
	public MoveDialog(Frame parent, boolean modal) {

		super(parent, modal);

		gui = (GameFrame)parent;

		Move = RiskUIUtil.getUIImage(this.getClass(),"move.jpg");

		MoveBack = Move.getSubimage(0, 0, 480, 330);

		initGUI();

		setResizable(false);

		pack();
	}

	/**
         * This method is called from within the constructor to initialize the form.
	 * Initialises the GUI
	 */
	private void initGUI() {
		resb = TranslationBundle.getBundle();

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		Dimension d = GraphicsUtil.newDimension(480, 330);

		movepanel = new movePanel();
		movepanel.setPreferredSize(d);
		movepanel.setMinimumSize(d);
		movepanel.setMaximumSize(d);
		movepanel.setLayout(null);

		slider = new JSlider();
		movepanel.add(slider);

		int w=88;
		int h=31;

		cancel = new JButton(resb.getString("move.cancel"));
		NewGameFrame.sortOutButton( cancel, Move.getSubimage(484, 5, w, h), Move.getSubimage(480, 72, w, h) ,Move.getSubimage(480, 41, w, h) );
		GraphicsUtil.setBounds(cancel, 50, 250, w, h);
		cancel.setActionCommand("cancel");

		JButton moveall = new JButton(resb.getString("move.moveall"));
		NewGameFrame.sortOutButton( moveall, Move.getSubimage(196, 250, w, h) ,Move.getSubimage(480, 134, w, h), Move.getSubimage(480, 103, w, h) );
		GraphicsUtil.setBounds(moveall, 196, 250, w, h);
		moveall.setActionCommand("all");

		JButton button = new JButton(resb.getString("move.move"));
		NewGameFrame.sortOutButton( button, Move.getSubimage(342, 250, w, h), Move.getSubimage(480, 196, w, h), Move.getSubimage(480, 165, w, h) );
		GraphicsUtil.setBounds(button, 343, 250, w, h);
		button.setActionCommand("move");



		w=35;
		h=25;

		JButton b1 = new JButton(resb.getString("move.min"));
		NewGameFrame.sortOutButton( b1, Move.getSubimage(25, 192, w, h), Move.getSubimage(480, 252, w, h), Move.getSubimage(515, 252, w, h) );
		GraphicsUtil.setBounds(b1, 25, 192, w, h);
		b1.setActionCommand("b1");

		JButton b4 = new JButton(resb.getString("move.max"));
		NewGameFrame.sortOutButton( b4, Move.getSubimage(25, 192, w, h), Move.getSubimage(480, 252, w, h), Move.getSubimage(515, 252, w, h) );
		GraphicsUtil.setBounds(b4, 420, 192, w, h);
		b4.setActionCommand("b4");



		w=25;
		h=25;

		JButton b2 = new JButton(resb.getString("move.minus"));
		NewGameFrame.sortOutButton( b2, Move.getSubimage(60, 192, w, h), Move.getSubimage(480, 227, w, h), Move.getSubimage(505, 227, w, h) );
		GraphicsUtil.setBounds(b2, 60, 192, w, h);
		b2.setActionCommand("b2");

		JButton b3 = new JButton(resb.getString("move.plus"));
		NewGameFrame.sortOutButton( b3, Move.getSubimage(60, 192, w, h), Move.getSubimage(480, 227, w, h), Move.getSubimage(505, 227, w, h) );
		GraphicsUtil.setBounds(b3, 395, 192, w, h);
		b3.setActionCommand("b3");




		ActionListener al = new ActionListener() {

			/**
			 *  Assigns the correct command to the button pressed
			 * @param e ActionEvent object
			 */
			public void actionPerformed(ActionEvent e) {

				if (e.getActionCommand().equals("cancel")) {

					exitForm();

				}
				else if (e.getActionCommand().equals("all")) {

					if (tacmove) {
						gui.go("movearmies " +country1.getColor()+ " " +country2.getColor()+ " " + (csrc-1) );
					}
					else {
						gui.go("move " + (csrc-1) );
					}

					//exitForm();

				}
				else if (e.getActionCommand().equals("move")) {

					if (tacmove) {
						gui.go("movearmies " +country1.getColor()+ " " +country2.getColor()+ " " + move );
					}
					else {
						gui.go("move " + move);
					}

					//exitForm();

				}
				else if (e.getActionCommand().equals("b1")) {

					slider.setValue( slider.getMinimum() );

				}
				else if (e.getActionCommand().equals("b2")) {

					slider.setValue( move-1 );

				}
				else if (e.getActionCommand().equals("b3")) {

					slider.setValue( move+1 );

				}
				else if (e.getActionCommand().equals("b4")) {

					slider.setValue( slider.getMaximum() );

				}
			}
		};


		cancel.addActionListener( al );
		moveall.addActionListener( al );
		button.addActionListener( al );

		b1.addActionListener( al );
		b2.addActionListener( al );
		b3.addActionListener( al );
		b4.addActionListener( al );

		movepanel.add(b1);
		movepanel.add(b2);
		movepanel.add(b3);
		movepanel.add(b4);

		movepanel.add(cancel);
		movepanel.add(moveall);
		movepanel.add(button);

		getContentPane().add(movepanel);

		addWindowListener(
			new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					if (tacmove) {
						exitForm();
					}
				}
			}
		);
	}

	public void setup(boolean tm, int m, int a, int b, BufferedImage ai, BufferedImage bi, Country country1, Country country2, Color c) {

		tacmove=tm;

		c1img = ai;
		c2img = bi;

		this.country1 = country1;
		this.country2 = country2;

		move=m;

		csrc=a;
		cdes=b;

		color=c;


		// set title
		if (tacmove) {
			setTitle(resb.getString("move.title.tactical"));
			cancel.setVisible(true);
		}
		else {
			setTitle(resb.getString("move.title.captured"));
			cancel.setVisible(false);
		}


		// all this coz for some reason u cant reuse a JSlider, the labels start getting painted wrong
		movepanel.remove(slider);

		slider = new JSlider(move,csrc-1,move);


		int spacig = Math.round( (csrc-1)/10f );

		if (spacig==0) {
			slider.setMajorTickSpacing(1);
		}
		else {
			slider.setMajorTickSpacing( spacig );
			slider.setMinorTickSpacing(1);
		}

		slider.setPaintTicks( true );
		slider.setPaintLabels( true );
		slider.setSnapToTicks( true );
		slider.setOpaque( false );

		slider.addChangeListener(
			new ChangeListener() {
				public void stateChanged(ChangeEvent e) {

					move = slider.getValue();
					movepanel.repaint();
				}
			}
		);

		GraphicsUtil.setBounds(slider, 90, 180, 300, 50);

		movepanel.add(slider);
	}

	/**
         * Exit the Application
	 * Closes the GUI
	 */
	public void exitForm() {
		setVisible(false);
	}

	class movePanel extends JPanel {

		/**
		 * Paints the graphic
		 * @param g Graphics
		 */
		public void paintComponent(Graphics g) {

			GraphicsUtil.drawImage(g, MoveBack, 0, 0, this);

			if (tacmove) {
				GraphicsUtil.drawImage(g, Move.getSubimage(480, 0, 98, 41), 46, 245, this);
			}

			paintMove(g, 
                                c1img, c2img,
                                color, color,
                                country1.getName(), country2.getName(),
                                csrc-move,cdes+move );

                        g.setColor( RiskUIUtil.getTextColorFor(color) );
			GraphicsUtil.drawStringCenteredAt(g, java.lang.Integer.toString(move), 240, 104);
		}
	}

        static void paintMove(Graphics g,
                BufferedImage c1img,BufferedImage c2img,
                Color color1, Color color2,
                String name1,String name2,
                int noa1i, int noa2i
                ) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        drawCountry(g2, c1img, 130, 100);
                        drawCountry(g2, c2img, 350, 100);

			g2.setColor( Color.black );

                        GraphicsUtil.drawStringCenteredAt(g, name1, 130, 40);
                        GraphicsUtil.drawStringCenteredAt(g, name2, 350, 40);

			g2.setColor( color1 );
                        GraphicsUtil.fillOval(g, 120 , 90 , 20, 20);

			g2.setColor( color2 );
                        GraphicsUtil.fillOval(g, 340 , 90 , 20, 20);

			g2.setColor( new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 150) );

			g2.fillPolygon( arrow );

			g2.setColor( RiskUIUtil.getTextColorFor(color1) );
                        GraphicsUtil.drawStringCenteredAt(g, String.valueOf(noa1i), 130, 105);

			g2.setColor( RiskUIUtil.getTextColorFor(color2) );
                        GraphicsUtil.drawStringCenteredAt(g, String.valueOf(noa2i), 350, 105);
        }

        static void drawCountry(Graphics2D g, BufferedImage img, int x, int y) {

            int maxW = 200;
            int maxH = 140;

            int w = img.getWidth();
            int h = img.getHeight();

            if (w > maxW || h > maxH) {
                double scale = Math.min(maxW/(double)w,maxH/(double)h);
                w = (int)( scale * w );
                h = (int)( scale * h );
            }

            GraphicsUtil.drawImage(g, img, x - w/2, y - h/2, w, h, null);
        }
}

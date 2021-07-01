// Yura Mamyrin, Group D

package net.yura.domination.ui.flashgui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JDialog;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Shape;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.util.List;
import net.yura.domination.engine.Risk;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.swing.GraphicsUtil;
import net.yura.domination.guishared.PicturePanel;
import net.yura.domination.engine.translation.TranslationBundle;

/**
 * Cards Dialog for FlashGUI
 * @author Yura Mamyrin
 */
public class CardsDialog extends JDialog {

	private Risk myrisk;
	private Player human;

	private JPanel myCardsPanel;
	private JPanel TradePanel;
	private JScrollPane CardsPlane;

	private BufferedImage Cards;
	private BufferedImage Back;
	private PicturePanel pp;

	private BufferedImage Infantry;
	private BufferedImage Cavalry;
	private BufferedImage Artillery;
	private BufferedImage Wildcard;
	private JButton tradeButton;
	private boolean canTrade;

	private java.util.ResourceBundle resb;

	/**
	 * Creates a new CardsDialog
	 * @param parent decides the parent of the frame
	 * @param modal
	 * @param r the risk main program
	 */
	public CardsDialog(Frame parent, boolean modal, Risk r, PicturePanel p) {
		super(parent, modal);
		myrisk = r;
		pp=p;

		Cards = RiskUIUtil.getUIImage(this.getClass(),"cards.jpg");

		Back = Cards.getSubimage(0, 0, 630, 500);

		Wildcard = Cards.getSubimage(630, 0, 50, 145);
		Cavalry = Cards.getSubimage(630, 145, 50, 70);
		Infantry = Cards.getSubimage(630, 215, 50, 70);
		Artillery = Cards.getSubimage(630, 285, 70, 50);

		initGUI();

		pack();
	}

	public void setup(Player human, boolean ct) {
		this.human = human;
		canTrade=ct;

		Component[] oldcards = myCardsPanel.getComponents();
		for (int c=0; c< oldcards.length ; c++) {
			myCardsPanel.remove(oldcards[c]);
		}
		oldcards = TradePanel.getComponents();
		for (int c=0; c< oldcards.length ; c++) {
			TradePanel.remove(oldcards[c]);
		}

		List cards = this.human.getCards();
		for (int c=0; c < cards.size(); c++) {
			JPanel cp = new CardPanel( (Card)cards.get(c) );
			myCardsPanel.add(cp);
		}

		tradeButton.setEnabled(false);
	}

	/**
         * This method is called from within the constructor to initialize the dialog.
         */
	private void initGUI() {
		resb = TranslationBundle.getBundle();

		setTitle(resb.getString("cards.title"));
		setResizable(false);

		JPanel cardspanel = new CardsPanel();


		cardspanel.setLayout(null); // new java.awt.GridBagLayout()

		Dimension Size = GraphicsUtil.newDimension(630,500);

		cardspanel.setPreferredSize(Size);
		cardspanel.setMinimumSize(Size);
		cardspanel.setMaximumSize(Size);


		myCardsPanel = new JPanel();
		myCardsPanel.setLayout(GraphicsUtil.newFlowLayout(java.awt.FlowLayout.LEFT));
		myCardsPanel.setOpaque(false);


		TradePanel = new JPanel();
		TradePanel.setLayout(GraphicsUtil.newFlowLayout(java.awt.FlowLayout.LEFT));
		TradePanel.setOpaque(false);

		tradeButton = GameFrame.makeRiskButton(Cards.getSubimage(396, 420, 88, 31), Cards.getSubimage(630, 335, 88, 31), Cards.getSubimage(630, 366, 88, 31), Cards.getSubimage(630, 397, 88, 31));
		tradeButton.setText(resb.getString("cards.trade"));
		GraphicsUtil.setBounds(tradeButton, 396, 420, 88, 31);

		JTextArea note = new JTextArea(resb.getString("cards.note"));

		note.setLineWrap(true);
		note.setWrapStyleWord(true);

		//note.setForeground((new JLabel()).getForeground());
		note.setFont((new JLabel()).getFont());
		note.setEditable(false);
		GraphicsUtil.setBounds(note, 400, 260, 180, 150);
		note.setOpaque(false);
                
                // in nimbus theme we have to do this extra hack
                note.setBorder(null);
                note.setBackground(new Color(0, 0, 0, 0));

		Dimension noteSize = GraphicsUtil.newDimension(180, 120);

		note.setPreferredSize( noteSize );
		note.setMinimumSize( noteSize );
		note.setMaximumSize( noteSize );


		JButton okButton = GameFrame.makeRiskButton(Cards.getSubimage(500, 420, 88, 31), Cards.getSubimage(630, 428, 88, 31), Cards.getSubimage(630, 459, 88, 31), Cards.getSubimage(500, 420, 88, 31));
		okButton.setText(resb.getString("cards.done"));
		GraphicsUtil.setBounds(okButton, 500, 420, 88, 31);

		okButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						closeDialog();
					}
				}
		);

		tradeButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {

					    Component[] cards2 = TradePanel.getComponents();

					    if (cards2.length==3) {

						myrisk.parser("trade "+((CardPanel)cards2[0]).getCardName() + " " + ((CardPanel)cards2[1]).getCardName() + " " + ((CardPanel)cards2[2]).getCardName() );

						TradePanel.remove(TradePanel.getComponent(2));
						TradePanel.remove(TradePanel.getComponent(1));
						TradePanel.remove(TradePanel.getComponent(0));

						TradePanel.validate();

						tradeButton.setEnabled(false);

						repaint();
					    }

					}
				}
		);


		CardsPlane = new JScrollPane();
		CardsPlane.setOpaque(false);
		CardsPlane.getViewport().add(myCardsPanel);
		CardsPlane.getViewport().setOpaque(false);
                CardsPlane.setViewportBorder(null); // for nimbus
		CardsPlane.setBorder(null);
		//CardsPlane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
		GraphicsUtil.setBounds(CardsPlane, 49, 48, 532, 198);


		//TradePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));
		GraphicsUtil.setBounds(TradePanel, 49, 270, 322, 182);


		cardspanel.add(note);
		cardspanel.add(tradeButton);
		cardspanel.add(okButton);

		cardspanel.add(CardsPlane);
		cardspanel.add(TradePanel);

		getContentPane().add(cardspanel);

		addWindowListener(
				new java.awt.event.WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						closeDialog();
					}
				}
		);
	}

	/**
	 * Gets the image of the country
	 * @param a the country number
	 * @return Image the image of the country
	 */
	public Image getCountryImage(int a) {

		BufferedImage pictureB = pp.getCountryImage(a, isOwnedPlayer(a));

		int width = pictureB.getWidth();
		int height = pictureB.getHeight();

		if (width > 50) { width=50; }
		if (height > 50) { height=50; }

		return pictureB.getScaledInstance(width,height, java.awt.Image.SCALE_SMOOTH );
	}
        
        boolean isOwnedPlayer(int country) {
            Country c = myrisk.getGame().getCountryInt(country);
            return c != null && human == c.getOwner();
        }

	public String getNumArmies() {
            // return resb.getString("cards.nexttrade").replaceAll( "\\{0\\}", "" + resb.getString("cards.fixed"));
            if(myrisk.getGame().getCardMode()==RiskGame.CARD_FIXED_SET) {
		 return resb.getString("cards.fixed");
            }
            else if(myrisk.getGame().getCardMode()==RiskGame.CARD_ITALIANLIKE_SET) {
		 return resb.getString("cards.italianlike");
            }
            else {
		 return resb.getString("cards.nexttrade").replaceAll( "\\{0\\}", "" + myrisk.getNewCardState());
            }
	}

	class CardsPanel extends JPanel {

		/**
		 * Paints the panel
		 * @param g The graphics
		 */
		public void paintComponent(Graphics g) {

			super.paintComponent(g);

			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			GraphicsUtil.drawImage(g, Back, 0, 0, this);

			GraphicsUtil.drawString(g, resb.getString("cards.yourcards"), 60, 41);
			GraphicsUtil.drawString(g, resb.getString("cards.trade"), 60, 263);

			GraphicsUtil.drawString(g, getNumArmies() , 400, 410);
		}
	}

	class CardPanel extends JPanel implements MouseListener {

		private Card card;
		private BufferedImage grayImage;
		private BufferedImage highlightImage;
		private boolean select;

		/**
		 * Constructor of for the panel
		 * @param c The card
		 */
		public CardPanel (Card c) {
			card=c;

			this.addMouseListener(this);

			int cardWidth=100;
			int cardHeight=170;

			select=false;

			Dimension CardSize = GraphicsUtil.newDimension(cardWidth, cardHeight);
			this.setPreferredSize( CardSize );
			this.setMinimumSize( CardSize );
			this.setMaximumSize( CardSize );

			setOpaque(false);

			//this.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 1));

			double scale = GraphicsUtil.scale;

			grayImage = new BufferedImage((int)(CardSize.width * scale), (int)(CardSize.height * scale), java.awt.image.BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = grayImage.createGraphics();
			g2.setFont(getFont()); // needed for hi-res java 1.8
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g2.setColor( Color.lightGray );
			g2.fillRect(0, 0, grayImage.getWidth(), grayImage.getHeight());

			g2.scale(scale, scale);
                        
			if (!(card.getName().equals("wildcard"))) {

				String text = ((Country)card.getCountry()).getName(); // Display

                                g2.setColor( GameFrame.UI_COLOR );

                                GraphicsUtil.drawStringCenteredAt(g2, text, cardWidth / 2, 5, cardWidth - 10);

				Image i = getCountryImage( ((Country)card.getCountry()).getColor() );

				GraphicsUtil.drawImage(g2, i, 25 + (25 - (i.getWidth(this) / 2)), 35 + (25 - (i.getHeight(this) / 2)), null);

				if (card.getName().equals("Infantry")) {
					GraphicsUtil.drawImage(g2, Infantry, 25, 90, null);
				}
				else if (card.getName().equals("Cavalry")) {
					GraphicsUtil.drawImage(g2, Cavalry, 25, 90, null);
				}
				else if (card.getName().equals("Cannon")) {
					GraphicsUtil.drawImage(g2, Artillery, 15, 105, null);
				}

			}
			else {
				GraphicsUtil.drawImage(g2, Wildcard, 25, 10, null);
			}

			//g2.setColor( Color.black );
			//TextLayout tl = new TextLayout( card.getName() , font, frc);
			//tl.draw( g2, (float) (100/2-tl.getBounds().getWidth()/2), (float)160 );

			g2.setColor( GameFrame.UI_COLOR );

			Shape shape2 = GraphicsUtil.newRoundRectangle(2, 2, 95, 165, 20, 20);

			g2.draw(shape2);

			highlightImage = new BufferedImage(grayImage.getWidth(), grayImage.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);

			RescaleOp HighLight = new RescaleOp(1.5f, 1.0f, null);
			HighLight.filter( grayImage , highlightImage );

			g2.dispose();
		}

		/**
		 * Paints the panel
		 * @param g The graphics
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D)g;

			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
			g2.setComposite(ac);

			Shape shape = GraphicsUtil.newRoundRectangle(0, 0, 100, 170, 25, 25);

			g2.clip(shape);
                        
			if (select) { g2.drawImage(highlightImage, 0, 0, getWidth(), getHeight(), this); }
			else { g2.drawImage(grayImage, 0, 0 ,getWidth(), getHeight(), this); }
		}

		/**
		 * Gets the card name
		 * @return String The card name
		 */
		public String getCardName() {

			if (!(card.getName().equals( Card.WILDCARD ))) {
				return ((Country)card.getCountry()).getColor()+"";
			} else {
				return card.getName();
			}
		}

		//**********************************************************************
		//                     MouseListener Interface
		//**********************************************************************

		/**
		 * Works out what has been clicked
		 * @param e A mouse event
		 */
		public void mouseClicked(MouseEvent e) {

			if ( this.getParent() == myCardsPanel ) {
				if (TradePanel.getComponentCount() < 3) { myCardsPanel.remove(this); select=false; TradePanel.add(this); }
				if (TradePanel.getComponentCount() == 3 && canTrade && myrisk.canTrade( ((CardPanel)TradePanel.getComponent(0)).getCardName() , ((CardPanel)TradePanel.getComponent(1)).getCardName(), ((CardPanel)TradePanel.getComponent(2)).getCardName() ) ) { tradeButton.setEnabled(true); }
			}
			else if ( this.getParent() == TradePanel ) {
				TradePanel.remove(this); select=false; myCardsPanel.add(this);
				tradeButton.setEnabled(false);
			}

			myCardsPanel.validate();
			TradePanel.validate();

			CardsPlane.validate();

			myCardsPanel.repaint();
			TradePanel.repaint();
		}

		/**
		 * Tells the frame to repaint when the mouse has been entered
		 * @param e A mouse event
		 */
		public void mouseEntered(MouseEvent e) {
			select=true;
			this.repaint();
		}

		/**
		 * Tells the frame to repaint when the mouse has been exited
		 */
		public void mouseExited(MouseEvent e) {
			select=false;
			this.repaint();
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

	}

	private void closeDialog() {
		setVisible(false);
	}
}

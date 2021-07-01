package net.yura.domination.lobby.client;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.swing.GraphicsUtil;
import net.yura.swing.ImageIcon;

public class GameSidePanel extends JSplitPane {

    private final static String product = RiskUtil.GAME_NAME + " Lobby Client";
    private final static String version = "0.3";

    final ImageIcon backpic = new ImageIcon( GameSidePanel.class.getResource("back.jpg") );
    private JLabel nameLabel;

    public void setGameName(String name) {
	nameLabel.setText(name);
    }

    public GameSidePanel(JProgressBar timer, JButton startButton, JPanel playerListArea, JPanel chatBoxArea) {
        super(JSplitPane.VERTICAL_SPLIT, true);
        ResourceBundle resb = TranslationBundle.getBundle();

        //setReplay(false);

        final Box sidepanelTop = new Box(javax.swing.BoxLayout.Y_AXIS);
        final Box sidepanelBottom = new Box(javax.swing.BoxLayout.Y_AXIS);

        int smallPadding = GraphicsUtil.scale(5);
        int bigPadding = GraphicsUtil.scale(15);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel1.setBorder(BorderFactory.createEmptyBorder(0, 0, bigPadding, 0));
        panel1.setOpaque(false);
        nameLabel = new JLabel("Game");
        nameLabel.setForeground(Color.WHITE);
        panel1.add( nameLabel );
        sidepanelTop.add( panel1 );

        if (timer != null) {
            JPanel panel2 = new JPanel();
            panel2.setOpaque(false);
            panel2.setBorder(BorderFactory.createEmptyBorder(0, 0, bigPadding, 0));
            panel2.add( timer );
            sidepanelTop.add( panel2 );
        }

        if (startButton != null) {
            final JButton aboutButton = new JButton( resb.getString( "mainmenu.about") );

            aboutButton.addActionListener( new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                            RiskUIUtil.openAbout(RiskUIUtil.findParentFrame(GameSidePanel.this), product, version);
                    }
            });
            
            Insets insets = new Insets( startButton.getMargin().top ,0, startButton.getMargin().bottom ,0);
            startButton.setMargin(insets);
            aboutButton.setMargin(insets);
            
            JPanel panel3 = new JPanel( new GridLayout(1,2,smallPadding,smallPadding) );
            panel3.setOpaque(false);
            panel3.setBorder(BorderFactory.createEmptyBorder(smallPadding,smallPadding,bigPadding + smallPadding,smallPadding) );
            
            panel3.add( startButton );
            panel3.add( aboutButton );
            sidepanelBottom.add( panel3 );
        }

        sidepanelTop.add( playerListArea );
        sidepanelBottom.add( chatBoxArea );

        playerListArea.setPreferredSize(GraphicsUtil.newDimension(160, 120));

        setDividerSize(bigPadding);
        setTopComponent(sidepanelTop);
        setBottomComponent(sidepanelBottom);

        setBorder(BorderFactory.createEmptyBorder(bigPadding, bigPadding, bigPadding, bigPadding));
        setUI( new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					public void paint(Graphics g) { }
				};
			}
		} );

        setPreferredSize(GraphicsUtil.newDimension(200, 600));
    }
    
    public void paintComponent(Graphics g) {
        int w = backpic.getIconWidth();
        int h = backpic.getIconHeight();
        for (int i = 0; i < getWidth(); i += w) for (int j = 0; j < getHeight(); j += h) {
                g.drawImage(backpic.getImage(), i, j, this);
        }
    }
}

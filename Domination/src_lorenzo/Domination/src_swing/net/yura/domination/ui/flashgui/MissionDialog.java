// Yura Mamyrin, Group D

package net.yura.domination.ui.flashgui;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.swing.GraphicsUtil;

/**
 * Mission Dialog for FlashGUI
 * @author Yura Mamyrin
 */
public class MissionDialog extends JDialog implements MouseListener {

    private BufferedImage mission;
    private String text;

    /**
     * Creates a mission dialog
     * @param parent Frame
     * @param modal boolean
     */
    public MissionDialog(Frame parent, boolean modal) {
        super(parent, modal);
	mission = RiskUIUtil.getUIImage(this.getClass(),"mission.jpg");
        initGUI();
	setResizable(false);
        pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initGUI() {

        // set title
        setTitle("");

	Dimension d = GraphicsUtil.newDimension(150, 230);

	missionPanel missionpanel = new missionPanel();
	missionpanel.setPreferredSize(d);
	missionpanel.setMinimumSize(d);
	missionpanel.setMaximumSize(d);
	missionpanel.addMouseListener(this);

	getContentPane().add(missionpanel);

        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm();
                }
            }
	);
    }
    
    public void setMission(String text) {
        	this.text = text;
    }

    /**
     * Closes the Dialog
     */
    private void exitForm() {
	setVisible(false);
	dispose();
    }

    class missionPanel extends JPanel {

    /**
     * Paints graphic
     * @param g Graphics
     */
	public void paintComponent(Graphics g) {

	    GraphicsUtil.drawImage(g, mission, 0, 0, this);

	    Graphics2D g2 = (Graphics2D)g;

	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // do not let font go bellow 10,
            // on hi res windows, default fontsize is 10, and 10-2=8 looks tiny
            int fontSize = Math.max(10, g.getFont().getSize() - 2); // 13 - 2 = 11
            
	    Font font = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, fontSize);
            g.setFont(font);
            g2.setColor( GameFrame.UI_COLOR );

            GraphicsUtil.drawStringCenteredAt(g, text, 75, 70, 100);
	}
    }

	//**********************************************************************
	//                     MouseListener Interface
	//**********************************************************************

	public void mouseClicked(MouseEvent e) {
	    exitForm();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}

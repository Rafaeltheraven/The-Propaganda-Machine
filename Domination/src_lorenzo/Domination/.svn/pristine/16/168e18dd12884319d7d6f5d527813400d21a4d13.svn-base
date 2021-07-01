// Yura Mamyrin

package net.yura.domination.ui.flashgui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.yura.domination.engine.Risk;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.core.StatType;
import net.yura.swing.GraphicsUtil;
import net.yura.domination.guishared.StatsPanel;
import net.yura.domination.engine.translation.TranslationBundle;

/**
 * <p> Statistics Dialog for FlashGUI </p>
 * @author Yura Mamyrin
 */

public class StatsDialog extends JDialog implements ActionListener {

        // we can only have 12 stats with the current UI, there is a total of 14, so we skip a couple
        private static final StatType[] STAT_TYPES = {
            StatType.COUNTRIES,
            StatType.ARMIES,
            StatType.KILLS,
            StatType.CASUALTIES,
            StatType.REINFORCEMENTS,
            StatType.CONTINENTS,
            StatType.CONNECTED_EMPIRE,
            StatType.ATTACKS,
            // skip RETREATS
            StatType.COUNTRIES_WON,
            StatType.COUNTRIES_LOST,
            // skip ATTACKED
            StatType.CARDS,
            StatType.DICE};

        private BufferedImage Back;
	private Risk myrisk;
	private StatsPanel graph;
	private java.util.ResourceBundle resb;
        private ButtonGroup group;

	public StatsDialog(Frame parent, boolean modal, Risk r) {
		super(parent, modal);
		myrisk = r;
		Back = RiskUIUtil.getUIImage(this.getClass(),"graph.jpg");
		initGUI();
		setResizable(false);
		pack();
	}

	/** This method is called from within the constructor to initialize the form. */

	/**
	 * Initialises the GUI
	 */
	private void initGUI() {

		resb = TranslationBundle.getBundle();

		setTitle( resb.getString("swing.tab.statistics") );

		JPanel thisgraph = new JPanel();
                thisgraph.setBorder( new FlashBorder(
                        Back.getSubimage(100, 0, 740, 50),
                        Back.getSubimage(0, 0, 50, 400),
                        Back.getSubimage(100, 182, 740, 150),
                        Back.getSubimage(50, 0, 50, 400)
                        ) );

		Dimension d = GraphicsUtil.newDimension(740, 600);
		thisgraph.setPreferredSize(d);
		thisgraph.setMinimumSize(d);
		thisgraph.setMaximumSize(d);

		thisgraph.setLayout(null);

                group = new ButtonGroup();

		int x=49;
		int y=483;
		int w=107;
		int h=33;

                for (int c=0;c<STAT_TYPES.length;c++) {
                        StatType statType = STAT_TYPES[c];
                        thisgraph.add(makeButton(statType.getName(), x, y, w, h, statType.ordinal()));
                        x=x+w;

                        // when we have done half, move on to 2nd row
                        if (c == (STAT_TYPES.length/2)-1) {
                                x=49;
                                y=y+h;
                        }
                }

                ((AbstractButton)thisgraph.getComponent(0)).setSelected(true);

		graph = new StatsPanel(myrisk);
		GraphicsUtil.setBounds(graph, 50, 50, 640, 400);

		thisgraph.add(graph);

		getContentPane().add(thisgraph);

		addWindowListener(
                    new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                            exitForm();
                        }
                    }
		);

	}

	public void actionPerformed(ActionEvent a) {
            showGraph(StatType.fromOrdinal(Integer.parseInt(a.getActionCommand())));
	}

        public void setVisible(boolean b) {
            super.setVisible(b);
            if (b) {
                showGraph(StatType.fromOrdinal(Integer.parseInt(group.getSelection().getActionCommand())));
            }
        }

        public void showGraph(StatType statType) {
		graph.repaintStats( statType );
		graph.repaint();
        }

	/**
	 * Closes the GUI
	 */
	private void exitForm() {
		((GameFrame)getParent()).displayGraph();
	}

        private AbstractButton makeButton(String a, int x,int y,int w,int h,int s) {

                AbstractButton statbutton = new JToggleButton(resb.getString("swing.toolbar."+a));
                statbutton.setActionCommand(s+"");
                statbutton.addActionListener( this );
                GraphicsUtil.setBounds(statbutton, x, y, w, h);
                group.add(statbutton);

                NewGameFrame.sortOutButton( statbutton, Back.getSubimage(x+100,y-433+165,w,h), Back.getSubimage(x+100,y-433,w,h), Back.getSubimage(x+100,y-433+66,w,h) );

                return statbutton;
        }
}

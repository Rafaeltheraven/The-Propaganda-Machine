// Yura Mamyrin, Group D

package net.yura.domination.ui.swinggui;

import java.awt.Toolkit;
import javax.swing.JFrame;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUIUtil;
import net.yura.domination.engine.guishared.AboutDialog;

/**
 * <p> Swing GUI Main Frame </p>
 * @author Yura Mamyrin
 */
public class SwingGUIFrame {

	public static void main(String[] argv) {

		RiskUIUtil.parseArgs(argv);

                Risk r = new Risk();
		SwingGUIPanel sg = new SwingGUIPanel( r );

		JFrame gui = new JFrame();

		gui.setContentPane( sg );
                gui.setJMenuBar( sg.getJMenuBar() );

		gui.setTitle( SwingGUIPanel.product );
		gui.setIconImage(Toolkit.getDefaultToolkit().getImage( AboutDialog.class.getResource("icon.gif") ));

		gui.pack();

		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                RiskUIUtil.center(gui);

		try {
			gui.setMinimumSize( gui.getPreferredSize() );
		}
		catch(NoSuchMethodError ex) {
			// must me java 1.4
			gui.setResizable(false);
		}

		gui.setVisible(true);

                RiskUIUtil.openFile(argv,r);

		sg.checkForUpdates();
	}
}

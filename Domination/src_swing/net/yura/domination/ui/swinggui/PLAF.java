package net.yura.domination.ui.swinggui;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ServiceLoader;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.yura.mobile.gui.plaf.LookAndFeel;

public class PLAF {

    private static final ServiceLoader<LookAndFeel> LOOK_AND_FEEL_LOADER = ServiceLoader.load(LookAndFeel.class); 

    private SwingGUIPanel ui;
    private ButtonGroup lookAndFeelRadioGroup;
    private String lookAndFeel;

    Action setLookAndFeelAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            ButtonModel model = lookAndFeelRadioGroup.getSelection();
            String lookAndFeelName = model.getActionCommand();
            try {
                setLookAndFeel(lookAndFeelName);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                ui.showError("unable To Change LookAndFeel to " + lookAndFeelName);
            }
        }
    };
    
    public PLAF(SwingGUIPanel ui) {
        this.ui = ui;
        ui.getJMenuBar().add(createLookAndFeelMenu());
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("L&F");
        menu.setName("lookAndFeel");

        // Look for toolkit look and feels first
        UIManager.LookAndFeelInfo lookAndFeelInfos[] = UIManager.getInstalledLookAndFeels();
        lookAndFeel = UIManager.getLookAndFeel().getClass().getName();
        lookAndFeelRadioGroup = new ButtonGroup();
        for(UIManager.LookAndFeelInfo lafInfo: lookAndFeelInfos) {
            menu.add(createLookAndFeelItem(lafInfo.getName(), lafInfo.getClassName()));
        }  
        // Now load any look and feels defined externally as service via java.util.ServiceLoader
        LOOK_AND_FEEL_LOADER.iterator();
        for (LookAndFeel laf : LOOK_AND_FEEL_LOADER) {           
            menu.add(createLookAndFeelItem(laf.toString(), laf.getClass().getName()));
        }

        return menu;
    }

    private JRadioButtonMenuItem createLookAndFeelItem(String lafName, String lafClassName) {
        JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem();

        lafItem.setSelected(lafClassName.equals(lookAndFeel));
        lafItem.setHideActionText(true);
        lafItem.setAction(setLookAndFeelAction);
        lafItem.setText(lafName);
        lafItem.setActionCommand(lafClassName);
        lookAndFeelRadioGroup.add(lafItem);

        return lafItem;
    }

    private void setLookAndFeel(String lookAndFeel) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

        String oldLookAndFeel = this.lookAndFeel;

        if (oldLookAndFeel != lookAndFeel) {
            UIManager.setLookAndFeel(lookAndFeel);
            this.lookAndFeel = lookAndFeel;
            updateLookAndFeel();
            ui.firePropertyChange("lookAndFeel", oldLookAndFeel, lookAndFeel);
        }
    }

    private void updateLookAndFeel() {
        Window windows[] = Window.getWindows();

        for(Window window : windows) {
            SwingUtilities.updateComponentTreeUI(window);
            for(Component demoPanel : ui.getJTabbedPane().getComponents()) {
                SwingGUITab tab = (SwingGUITab)demoPanel;
                SwingUtilities.updateComponentTreeUI(tab.getToolBar());
            }
        }
    }
}

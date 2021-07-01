package net.yura.swingme.core;

import java.util.Arrays;
import java.util.List;
import javax.microedition.lcdui.Graphics;
import net.yura.domination.engine.RiskUtil;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.BoxLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.layout.Layout;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 */
public class ViewChooser extends Panel implements ActionListener {

    Option[] options;
    ActionListener actionListener;
    String actionCommand;
    boolean stretchCombo;
    
    public ViewChooser(Option[] pp) {
        options = pp;
        
        Button test = new Button("test");
        test.workoutPreferredSize();

        setPreferredSize(10, test.getHeightWithBorder()); // some small size, but we will strech

    }
    public void setStretchCombo(boolean stretch) {
        stretchCombo = stretch;
    }
    
    public void addActionListener(ActionListener al) {
        actionListener = al;
    }
    public void setActionCommand(String com) {
        actionCommand = com;
    }

    public void setSize(int width, int height) {

        Option currentOption = getSelectedItem();

        int buttonsWidth = 0;
        Button[] buttons = new Button[options.length];
        for (int c=0;c<buttons.length;c++) {
            buttons[c] = new Button( options[c].getValue() );

            if (c==0) {
                buttons[c].setName("SegmentedControlLeft");
            }
            else if (c== (buttons.length-1) ) {
                buttons[c].setName("SegmentedControlRight");
            }
            else {
                buttons[c].setName("SegmentedControlMiddle");
            }

            buttons[c].workoutPreferredSize();
            buttonsWidth = buttonsWidth + buttons[c].getWidthWithBorder();
        }

        if (buttonsWidth <= width) {
            setLayout( new FlowLayout(Graphics.HCENTER,0) );
            ButtonGroup group = new ButtonGroup();
            for (int c=0;c<buttons.length;c++) {
                Button b = buttons[c];
                b.setActionCommand( options[c].getKey() );
                if (currentOption == options[c]) {
                    b.setSelected(true);
                }
                group.add(b);
                b.addActionListener(this);
                add(b);
            }
            // remove the rest
            while (getComponentCount() > buttons.length) { remove(0); }
        }
        else {
            ComboBox combo = new ComboBox( RiskUtil.asVector( Arrays.asList( options ) ) );
            combo.setSelectedItem(currentOption);
            combo.workoutPreferredSize();
            combo.addActionListener(this);
            setLayout( stretchCombo?(Layout)new BorderLayout():new BoxLayout(Graphics.HCENTER) );
            insert(combo,0);
            // remove the rest
            while (getComponentCount() > 1) { remove(1); }
        }
        // we do the removing after we add the new component so that other threads
        // can still call getSelectedItem() while this is happening and get a value

        super.setSize(width, height);
    }

    public void actionPerformed(String ac) {
        actionListener.actionPerformed(actionCommand);
    }
    
    public Option getSelectedItem() {
        
        List components = getComponents();
        if (components.isEmpty()) {
            return options[0]; // default
        }
        else {
            Object one = components.get(0);
            if (one instanceof ComboBox) {
                return (Option) ((ComboBox)one).getSelectedItem();
            }
            else {
                ButtonGroup bg = ((Button)one).getGroup();
                String id = bg.getSelection().getActionCommand();
                for (int c=0;c<options.length;c++) {
                    if (id.equals( options[c].getKey() ) ) {
                        return options[c];
                    }
                }
                throw new RuntimeException("can not find option with id: "+id);
            }
        }
    }
    
    public void resetMapView() {
        List components = getComponents();
        if (components.isEmpty()) {
            // do nothing
        }
        else if (components.get(0) instanceof ComboBox) {
            ((ComboBox)components.get(0)).setSelectedIndex(0);
        }
        else {
            ((Button)components.get(0)).setSelected(true);
        }
    }
    
}

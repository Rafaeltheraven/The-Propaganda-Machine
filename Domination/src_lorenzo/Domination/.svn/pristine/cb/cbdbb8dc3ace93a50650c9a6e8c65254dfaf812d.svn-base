package net.yura.lobby.mini;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.microedition.lcdui.Graphics;
import net.yura.domination.engine.ColorUtil;
import net.yura.lobby.model.Game;
import net.yura.lobby.util.TimeoutUtil;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.TextArea;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.gui.plaf.Style;
import net.yura.swingme.core.AnalogClock;

public class GameRenderer extends DefaultListCellRenderer {

    AnalogClock clock = new AnalogClock();
    MiniLobbyClient lobby;
    ScaledIcon sicon;
    Game game;
    String line1,line2,part2;
    
    public GameRenderer(MiniLobbyClient l) {
        lobby = l;
        setName("ListRendererCollapsed"); // get rid of any padding
        sicon = new ScaledIcon( XULLoader.adjustSizeToDensity(75),XULLoader.adjustSizeToDensity(47) );
        //int size = (int) (getFont().getHeight()*1.5);
        int size = XULLoader.adjustSizeToDensity(25);
        clock.setSize( size, size );
        clock.setBackground(0x00FFFFFF);

        padding = XULLoader.adjustSizeToDensity(2);
        gap = XULLoader.adjustSizeToDensity(2);
    }

    public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, null, index, isSelected, cellHasFocus);

        game = (Game)value;

        sicon.setIcon( lobby.game.getIconForGame(game) );
        setIcon(sicon);

        long time = game.getTimeout()*1000L;
        Calendar _now = Calendar.getInstance();
        _now.setTimeZone( TimeZone.getTimeZone("GMT") );
        _now.setTime( new Date(time) );
        clock.setTime(_now);
        clock.setVisible( time!=0 );
        line1 = lobby.game.getGameDescription(game);
        if (time!=0) {
            line1 = TimeoutUtil.formatPeriod( time )+" "+ line1;
        }

        line2 = game.getPlayers() + " " + game.getName();

        setVerticalTextPosition( "".equals(line2) ? Graphics.VCENTER : Graphics.TOP);

        part2 = game.getNumOfPlayers()+"/"+game.getMaxPlayers();

        return c;
    }

    public int getFixedCellHeight() {
        return padding*2 + Math.max(sicon.getIconHeight(), getFont().getHeight()*2+gap+padding*2 );
    }

    public void paintComponent(Graphics2D g) {

        // draw icon
        super.paintComponent(g);

        int offsetx = padding+getIcon().getIconWidth()+gap;
        g.setFont( font );
        g.setColor( getForeground() );

        if (clock.isVisible()) {
            int offsety = padding;
            clock.setForeground( getForeground() );
            g.translate(offsetx, offsety);
            clock.paint(g);
            g.translate(-offsetx, -offsety);
            offsetx = offsetx + clock.getWidth()+padding;
        }

        // draw line1
        g.drawString(line1, offsetx, padding * 2);

        offsetx = padding+getIcon().getIconWidth()+gap;

        int state = getCurrentState();
        // if NOT focused or selected
        if ( (state&Style.FOCUSED)==0 && (state&Style.SELECTED)==0 ) {
            g.setColor( theme.getForeground(Style.DISABLED) );
        }

        // draw line2
        int offsety=getHeight()-font.getHeight()-padding;
        int space = getWidth()-offsetx-font.getWidth("10/10");            
        String drawString = font.getWidth(line2)>space?line2.substring(0, TextArea.searchStringCharOffset(line2, font, space))+extension:line2;
        g.drawString(drawString, offsetx, offsety);

        // draw part2 of line2
        g.drawString(part2, getWidth()-font.getWidth(part2)-padding, getHeight()-font.getHeight()-padding);

        // draw action button
        String action;
        int color;
        switch (game.getState( lobby.whoAmI() )) {
            case Game.STATE_CAN_JOIN: action = "Join"; color=ColorUtil.GREEN; break;
            case Game.STATE_CAN_LEAVE: action = "Leave"; color=ColorUtil.RED; break;
            case Game.STATE_CAN_PLAY: action = "Play"; color=ColorUtil.BLUE; break;
            case Game.STATE_CAN_WATCH: action = "Watch"; color=0xFFEEEEEE; break;
            default: action = null; color=0; break;
        }
        int actionx=getWidth();
        if (action!=null) {
            int w = font.getWidth(action);
            int h = font.getHeight();

            g.setColor(color);
            actionx = getWidth()-w-padding*3;
            g.fillRoundRect(actionx, padding, w+padding*2, h+padding*2, 5, 5);

            g.setColor( ColorUtil.getTextColorFor(color) );
            g.drawString(action, getWidth()-w-padding*2, padding*2);
        }

        // draw red 'my turn' indicator
        if (lobby.whoAmI().equals( game.getWhosTurn() )) {
            int wh = font.getHeight();
            g.setColor(0xFFFF0000);
            g.fillOval(actionx-wh-padding, (getHeight()-wh)/2, wh, wh);
        }
    }
    
    public static class ScaledIcon extends Icon {
        Icon icon;
        public ScaledIcon(int w,int h) {
            width = w;
            height = h;
        }
        public void setIcon(Icon i) {
            icon = i;
        }
        public void paintIcon(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            double sx=width/(double)icon.getIconWidth();
            double sy=height/(double)icon.getIconHeight();
            g.getGraphics().scale(sx, sy);
            icon.paintIcon(c, g, 0, 0);
            g.getGraphics().scale(1/sx,1/sy);
            g.translate(-x, -y);
        }
    }

}

package net.yura.domination.guishared;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Window;
import java.io.File;
import java.util.List;
import javax.swing.SwingUtilities;
import net.yura.domination.engine.OnlineUtil;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUtil;
import net.yura.swing.GraphicsUtil;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.lobby.mini.MiniLobbyRisk;
import net.yura.domination.mapstore.MapChooser;
import net.yura.lobby.client.ChatBox;
import net.yura.lobby.client.PlayerList;
import net.yura.lobby.mini.MiniLobbyClient;
import net.yura.lobby.model.Game;
import net.yura.lobby.model.GameType;
import net.yura.lobby.model.Player;
import net.yura.mobile.gui.ActionListener;
import net.yura.me4se.ME4SEPanel;

/**
 * @author Yura Mamyrin
 */
public class SwingMEWrapper {

    public static String showMapChooser(Frame parent, List files) {

       // TMP TMP TMP
       try {
           // clean up crap left by old version
            File rms = new File(".rms");
            if (rms.exists()) {
                File[] files1 = rms.listFiles();
                for (int c=0;c<files1.length;c++) {
                    files1[c].delete();
                }
                rms.delete();
            }
        }
        catch (Throwable th) { }
        // TMP TMP TMP
        
        
        final ME4SEPanel wrapper = new ME4SEPanel(); // this sets the theme to NimbusLookAndFeel
        wrapper.getApplicationManager().applet = RiskUIUtil.applet;

        MapChooser.loadThemeExtension(); // loads extra things needed for map chooser
        
        final MapChooser chooser = new MapChooser(new ActionListener() {
            public void actionPerformed(String actionCommand) {
                SwingUtilities.getWindowAncestor(wrapper).setVisible(false);
            }
        }, files, null);

        wrapper.add( chooser.getRoot() );

        wrapper.setPreferredSize(GraphicsUtil.newDimension(400,600));

        wrapper.showDialog(parent, TranslationBundle.getBundle().getString("newgame.choosemap") );
        
        // WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT
        // WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT WAIT
        
        String result = chooser.getSelectedMap();
        
        chooser.destroy();

        return result;
    }

    public static MiniLobbyClient makeMiniLobbyClient(String server, Risk risk,final Window window, final ChatBox chat, final PlayerList players, final String appName) {
        MapChooser.loadThemeExtension();
        MiniLobbyClient miniLobbyClient = new MiniLobbyClient(new MiniLobbyRisk(risk) {
            private net.yura.domination.lobby.client.GameSetupPanel gsp;
            public void openGameSetup(GameType gameType) {
                if (gsp==null) {
                    gsp = new net.yura.domination.lobby.client.GameSetupPanel();
                }
                Game result = gsp.showDialog( window , gameType.getOptions(), OnlineUtil.getDefaultOnlineGameName(lobby.whoAmI()) );
                if (result!=null) {
                    lobby.createNewGame(result);
                }
            }
            public String getAppName() {
                return appName + RiskUtil.GAME_NAME;
            }
            public String getAppVersion() {
                return RiskUtil.RISK_VERSION;
            }
            public void showMessage(String fromwho, String message) {
                if (chat != null) {
                    chat.incomingChat(fromwho, message);
                }
            }

            public void addPlayer(Player player) {
                if (players != null) {
                    players.addPlayer(player);
                }
            }
            public void removePlayer(final String player) {
                if (players != null) {
                    // this comes in on com thread, and we dont want to remove item while its painting
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            players.removePlayer(player);
                        }
                    });
                }
            }
            public void renamePlayer(String oldname, String newname, int newtype) {
                if (players != null) {
                    players.renamePlayer(oldname, newname, newtype);
                }
            }
        } );
        miniLobbyClient.connect(server);
        return miniLobbyClient;
    }

    public static net.yura.mobile.gui.Graphics2D getSwingMEGraphics(Graphics g) {
        javax.microedition.lcdui.Graphics j2meG = new javax.microedition.lcdui.Graphics(g);
        // on retina mac all our SwingME images are double size
        // but our Swing Graphics are not scaled, so we reverse scale the graphics
        double scale = javax.microedition.midlet.ApplicationManager.getScale();
        j2meG.scale(1 / scale, 1 / scale);
        return new net.yura.mobile.gui.Graphics2D(j2meG);
    }
}

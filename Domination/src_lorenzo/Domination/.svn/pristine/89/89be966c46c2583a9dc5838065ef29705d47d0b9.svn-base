package net.yura.domination.ui.flashgui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import net.yura.domination.engine.Risk;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.guishared.SwingMEWrapper;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.lobby.client.GameSidePanel;
import net.yura.lobby.client.ChatBox;
import net.yura.lobby.client.PlayerList;
import net.yura.lobby.mini.MiniLobbyClient;
import net.yura.lobby.model.Game;
import net.yura.me4se.ME4SEPanel;
import net.yura.swing.GraphicsUtil;

public class FlashMiniLobby {

    private final Risk myrisk;
    private final MiniLobbyClient mlc;
    private final ChatBox inGameChat;
    private final PlayerList playerList;

    public FlashMiniLobby(final FlashRiskAdapter fra, final Risk myrisk, RootPaneContainer root, Frame window) {
        this.myrisk = myrisk;

        final ME4SEPanel wrapper = new ME4SEPanel();
        wrapper.getApplicationManager().applet = RiskUIUtil.applet;

        inGameChat = new ChatBox();
        playerList = new PlayerList(null);

        mlc = SwingMEWrapper.makeMiniLobbyClient(MiniLobbyClient.LOBBY_SERVER, myrisk, window, inGameChat, playerList, "Flash");
        wrapper.add(mlc.getRoot());

        mlc.addCloseListener(new net.yura.mobile.gui.ActionListener() {
            public void actionPerformed(String actionCommand) {
                wrapper.destroy();
                fra.closeMiniLobby();
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(wrapper);

        BufferedImage img = RiskUIUtil.getUIImage(this.getClass(),"graph.jpg");
        panel.setBorder( new FlashBorder(
                    img.getSubimage(100, 0, 740, 50),
                    img.getSubimage(0, 0, 50, 400),
                    img.getSubimage(100, 350, 740, 50), //img.getSubimage(100, 332, 740, 68),
                    img.getSubimage(50, 0, 50, 400)
                ) );

        root.setContentPane(panel);
        window.setTitle( mlc.getTitle() );
        window.setResizable(true);
        window.setSize(GraphicsUtil.scale(500), GraphicsUtil.scale(600));
    }

    Action getOnlineAction() {
        return new AbstractAction(TranslationBundle.getBundle().getString("lobby.resign")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                mlc.resign();
                setEnabled(false); // we can only resign once
            }
            /**
             * TODO remove duplicate code from:
             * @see net.yura.domination.mobile.flashgui.MiniFlashRiskAdapter#amOnlinePlayer();
             */
            @Override
            public boolean isEnabled() {
                Player player = myrisk.getGame().getPlayer(mlc.whoAmI());
                return super.isEnabled() && player != null && player.isAlive();
            }
        };
    }

    Component getOnlinePanel() {
        Game game = mlc.getCurrentOpenGame();
        inGameChat.reset(mlc.mycom, null, game.getId());
        playerList.clearPlayerList();
        GameSidePanel sidePanel = new GameSidePanel(null, null, playerList, inGameChat);
        sidePanel.setGameName(game.getName());
        return sidePanel;
    }
}

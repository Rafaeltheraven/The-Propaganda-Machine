package net.yura.lobby.mini;

import net.yura.lobby.model.Game;
import net.yura.lobby.model.GameType;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.util.Properties;

/**
 * @author Yura Mamyrin
 */
public interface MiniLobbyGame {

    void addLobbyGameMoveListener(MiniLobbyClient lgl);

    Properties getProperties();

    boolean isMyGameType(GameType gametype);
    Icon getIconForGame(Game game);
    String getGameDescription(Game game);

    /**
     * callback mlc.createNewGame(Game)
     */
    void openGameSetup(GameType gameType);

    /**
     * this method must trigger mlc.mycom.playGame(game.getId()) at some point in the future
     */
    void prepareAndOpenGame(Game game);

    void objectForGame(Object object);
    void stringForGame(String message);

    void connected(String username);
    void disconnected();

    /**
     * button inside lobby was clicked that the user wants to join a private game
     */
    void joinPrivateGame();
    /**
     * a private game was started on the server
     */
    void gameStarted(int id);

    String getAppName();
    String getAppVersion();

    void lobbyShutdown();
}

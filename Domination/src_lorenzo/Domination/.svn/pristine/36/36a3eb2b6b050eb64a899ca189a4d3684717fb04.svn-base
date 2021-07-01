package net.yura.domination.lobby.mini;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import net.yura.domination.engine.OnlineRisk;
import net.yura.domination.engine.OnlineUtil;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.domination.mapstore.Map;
import net.yura.domination.mapstore.MapChooser;
import net.yura.domination.mapstore.MapServerClient;
import net.yura.domination.mapstore.MapServerListener;
import net.yura.domination.mapstore.MapUpdateService;
import net.yura.lobby.mini.MiniLobbyClient;
import net.yura.lobby.mini.MiniLobbyGame;
import net.yura.lobby.model.Game;
import net.yura.lobby.model.GameType;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.util.Properties;
import net.yura.swingme.core.CoreUtil;

/**
 * @author Yura Mamyrin
 * @see net.yura.domination.lobby.client.ClientGameRisk
 */
public abstract class MiniLobbyRisk implements MiniLobbyGame,OnlineRisk {

    private static final Logger logger = Logger.getLogger( MiniLobbyRisk.class.getName() );

    private Risk myrisk;
    protected MiniLobbyClient lobby;

    public MiniLobbyRisk(Risk risk) {
        myrisk = risk;
    }

    public void addLobbyGameMoveListener(MiniLobbyClient lgl) {
        lobby = lgl;
    }

    public Properties getProperties() {
        return CoreUtil.wrap( TranslationBundle.getBundle() );
    }

    public boolean isMyGameType(GameType gametype) {
        return RiskUtil.GAME_NAME.equals(gametype.getName());
    }

    @Override
    public void prepareAndOpenGame(final Game game) {
        final String mapUID = OnlineUtil.getMapNameFromLobbyStartGameOption(game.getOptions());

        // TODO check if we are already in the process of downloading this map

        // check if we have this map already & if we need to do a update for the map
        if (MapChooser.haveLocalMap(mapUID) && !MapUpdateService.getInstance().contains(mapUID)) {
            lobby.mycom.playGame(game.getId());
        }
        else {
            net.yura.domination.mapstore.GetMap.getMap(mapUID, new Observer() {
                @Override
                public void update(Observable observable, Object data) {
                    if (data == RiskUtil.SUCCESS) {
                        lobby.mycom.playGame(game.getId());
                    }
                    else {
                        lobby.error("map download failed " + mapUID);
                    }
                }
            });
        }
    }

    private boolean openGame;

    /**
     * @see net.yura.domination.lobby.client.ClientGameRisk#gameObject(java.lang.Object)
     */
    public void objectForGame(Object object) {
        if (object instanceof RiskGame) {
            RiskGame thegame = (RiskGame)object;
            Player player = thegame.getPlayer(lobby.whoAmI());
            String address = player==null?"_watch_":player.getAddress();
            myrisk.setOnlinePlay(this);
            myrisk.setAddress(address);
            myrisk.setGame(thegame);
            openGame = true;
        }
// TODO remove this legacy message system
        else if (object instanceof java.util.Map) {
            java.util.Map map = (java.util.Map)object;

            String command = (String)map.get("command");
            if ("game".equals(command)) {
                String address = (String)map.get("playerId");
                RiskGame thegame = (RiskGame)map.get("game");
                myrisk.setOnlinePlay(this);
                myrisk.setAddress(address);
                myrisk.setGame(thegame);
                openGame = true;
            }
            else {
                System.out.println("MiniLobbyRisk unknown command "+command+" "+map);
            }
        }
// END TODO
        else {
            System.out.println("MiniLobbyRisk unknown object "+object);
        }
    }

    /**
     * @see net.yura.domination.lobby.client.ClientGameRisk#gameString(java.lang.String)
     */
    public void stringForGame(String message) {
        if (openGame) {
            myrisk.parserFromNetwork(message);
        }
        else {
            logger.info("GAME NOT OPEN SO IGNORING: "+message);
        }
    }

    public void disconnected() {
        myrisk.disconnected();
    }

    public void connected(String username) {
    }
    public void joinPrivateGame() {
    }
    public void gameStarted(int id) {
    }



    WeakHashMap mapping = new WeakHashMap();

    public Icon getIconForGame(Game game) {
        String mapUID = OnlineUtil.getMapNameFromLobbyStartGameOption(game.getOptions());
        mapping.put(game, mapUID); // keep a strong ref to the mapUID as long as we have a strong ref to the game

        // there are 3 layers of WeakHashMap
        // for locale maps:
        //      Game -> MapUID {@link MiniLobbyRisk#mapping} (added here)
        //      MapUID -> Map {@link MapChooser#mapCache} (added in MapChooder.getLocalIconForMap -> MapChooser.getIconForMapOrCategory)
        //      Map -> LazyIcon {@link MapChooser#iconCache} (added in MapChooder.getLocalIconForMap -> MapChooser.getIconForMapOrCategory -> MapChooser.gotImg)
        // for remote maps
        //      Game -> MapUID {@link MiniLobbyRisk#mapping} (added here)
        //      MapUID -> LazyIcon  {@link MapChooser#iconCache} (MiniLobbyRisk.mapServerClient.gotResultMaps -> MapChooser.getRemoteImage -> MapChooser.gotImg)

        // if local map
        if (MapChooser.haveLocalMap(mapUID)) {
            return MapChooser.getLocalIconForMap(MapChooser.createMap(mapUID));
        }

        if (mapServerClient == null) {
            mapServerClient = new MapServerClient(new MapServerListener() {
                public void gotResultCategories(String url, List categories) { }
                public void gotResultMaps(String url, List maps) {
                    if (maps.size() != 1) {
                        logger.warning("no map found on MapServer for "+url);
                        return;
                    }
                    Map map = (Map) maps.get(0);
                    Object mapUIDkey = MapChooser.getFileUID(map.getMapUrl());
                    boolean fromCache = MapChooser.getRemoteImage(mapUIDkey, MapChooser.getURL(url, map.getPreviewUrl()), mapServerClient);

                    if (fromCache) {
                        publishImg(mapUIDkey);
                    }
                }
                public void onXMLError(String string) {
                    logger.info("ERROR "+string);
                }
                public void downloadFinished(String mapUID) { }
                public void onDownloadError(String string) { }
                /**
                 * key is mapUID in this case
                 */
                public void publishImg(Object key) {
                    lobby.getRoot().repaint();
                }

            });
            mapServerClient.start();
        }

        return MapChooser.getRemoteIconForMap(mapUID, mapServerClient);
    }

    MapServerClient mapServerClient;

    public void lobbyShutdown() {
        if (mapServerClient != null) {
            mapServerClient.kill();
            mapServerClient = null;
        }
    }

    public String getGameDescription(Game game) {
        return OnlineUtil.getGameDescriptionFromLobbyStartGameOption( game.getOptions() );
    }



    // WMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMW
    // WMWMWMWMWMWMWMWMWMWMWMWMWMW OnlineRisk MWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMW
    // WMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMW

    public void sendUserCommand(final String messagefromgui) {
        lobby.sendGameMessage(messagefromgui);
    }
    public void sendGameCommand(String mtemp) {
	// this happens for game commands on my go
        logger.info("ignore GameCommand "+mtemp );
    }
    public void closeGame() {
        openGame = false;
        lobby.closeGame();
    }

    public void playerRenamed(String oldName, String newName, String newAddress, int newType) {
        if (oldName.equals(lobby.whoAmI())) {
            myrisk.setAddress("_watch_");
        }
        if (newName.equals(lobby.whoAmI())) {
            myrisk.setAddress(newAddress);
        }
    }
}

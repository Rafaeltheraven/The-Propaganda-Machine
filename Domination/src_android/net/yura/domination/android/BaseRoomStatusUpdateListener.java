package net.yura.domination.android;

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseRoomStatusUpdateListener implements RoomStatusUpdateListener {

    private static final Logger logger = Logger.getLogger(BaseRoomStatusUpdateListener.class.getName());

    @Override
    public void onRoomConnecting(Room room) {
        logger.info("onRoomConnecting(" + room + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        logger.info("onRoomAutoMatching(" + room + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> strings) {
        logger.info("onPeerInvitedToRoom(" + room + ", " + strings + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> strings) {
        logger.info("onPeerDeclined(" + room + ", " + strings + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> strings) {
        logger.info("onPeerJoined(" + room + ", " + strings + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> strings) {
        logger.info("onPeerLeft(" + room + ", " + strings + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onConnectedToRoom(Room room) {
        logger.info("onConnectedToRoom(" + room + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        logger.info("onDisconnectedFromRoom(" + room + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> strings) {
        logger.info("onPeersConnected(" + room + ", " + strings + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> strings) {
        logger.info("onPeersDisconnected(" + room + ", " + strings + ")");
        onRoomUpdated(room);
    }

    @Override
    public void onP2PConnected(String s) {
        logger.info("onP2PConnected(" + s + ")");
    }

    @Override
    public void onP2PDisconnected(String s) {
        logger.info("onP2PDisconnected(" + s + ")");
    }

    public abstract void onRoomUpdated(Room room);
}

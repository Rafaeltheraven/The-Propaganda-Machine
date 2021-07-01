package net.yura.domination.android;

import java.util.logging.Logger;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;


class BaseRoomUpdateListener implements RoomUpdateListener {

    private static final Logger logger = Logger.getLogger(BaseRoomUpdateListener.class.getName());

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        logger.info("onRoomCreated(" + statusCode + ", " + room + ")");
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        logger.info("onJoinedRoom(" + statusCode + ", " + room + ")");
    }

    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        logger.info("onLeftRoom(" + statusCode + ", " + roomId + ")");
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        logger.info("onRoomConnected(" + statusCode + ", " + room + ")");
    }
}

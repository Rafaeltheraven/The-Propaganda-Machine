package net.yura.domination.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.yura.android.LoadingDialog;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.translation.TranslationBundle;
import net.yura.lobby.client.ProtoAccess;
import net.yura.lobby.model.Game;
import net.yura.lobby.model.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;

/**
 * if 2 people (B and Q) create games with 1 friend each (C and G) and 2 auto-match players each:
 *
 * B players=[Q, C, G, B] creator=B GameX
 * Q players=[Q, C, B, G] creator=Q GameY
 * C players=[Q, C, G, B] creator=B
 * G players=[Q, C, B, G] creator=Q
 *
 * C sends name to B
 * G sends name to Q
 * B sends to everyone he is the creator
 * Q sends to everyone he is the creator
 * as B is less then Q, Q sends its name and the name it received from G to B
 * if Q only got the name of G after it found out B is a creator it will now send G's name to B
 * now B has everyones name and starts the game.
 */
public class RealTimeMultiplayer extends InvitationCallback implements GoogleAccount.SignInListener {

    private static final int RC_SELECT_PLAYERS = 2;
    private static final int RC_CREATOR_WAITING_ROOM = 3;
    private static final int RC_JOINER_WAITING_ROOM = 4;

    private static final int GOOGLE_PLAY_GAME_MIN_OTHER_PLAYERS = 1;

    public static final String EXTRA_SHOW_AUTOMATCH = "com.google.android.gms.games.SHOW_AUTOMATCH";

    private static final Logger logger = Logger.getLogger(RealTimeMultiplayer.class.getName());

    // we want to be able to send Game objects, but we dont care about the GameTypes, as its always going to be the same game
    private ProtoAccess encodeDecoder = new ProtoAccess(new ProtoAccess.ObjectProvider() {
        public Object getObjectId(Object var1) {
            return -1;
        }
        public Object getObjetById(Object var1, Class var2) {
            return null;
        }
    });
    private Activity activity;
    private Lobby lobby;
    private boolean invitationsLoaded;
    private String gameCreator;
    private Room gameRoom;
    private String myParticipantId;
    private Game lobbyGame;

    interface Lobby {
        void createNewGame(Game game);
        void playGame(Game gameId);
        void getUsername();
    }

    private RoomStatusUpdateCallback roomStatusUpdateCallback = new RoomStatusUpdateCallback() {
        @Override
        public void onRoomConnecting(@Nullable Room room) {
            gameRoom = room;
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            gameRoom = room;
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
            gameRoom = room;
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
            gameRoom = room;
        }

        @Override
        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
            gameRoom = room;
        }

        @Override
        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
            gameRoom = room;
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            gameRoom = room;

            Games.getPlayersClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                    .getCurrentPlayerId().addOnSuccessListener(new GoogleAccount.SafeOnSuccessListener<String>() {
                @Override
                public void onSuccessSafe(String playerId) {
                    myParticipantId = gameRoom.getParticipantId(playerId);
                }
            });
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            gameRoom = room;
        }

        @Override
        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
            gameRoom = room;
        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
            gameRoom = room;
        }

        @Override
        public void onP2PConnected(@NonNull String s) {
        }

        @Override
        public void onP2PDisconnected(@NonNull String s) {
        }
    };

    public RealTimeMultiplayer(Activity activity, Lobby lobby) {
        this.activity = activity;
        this.lobby = lobby;
    }

    RealTimeMultiplayerClient getRealTimeMultiplayerClient() {
        return Games.getRealTimeMultiplayerClient(activity, GoogleSignIn.getLastSignedInAccount(activity));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                handlePlayersSelected(resultCode, data);
                break;
            case RC_CREATOR_WAITING_ROOM:
                handleReturnFromWaitingRoom(resultCode, true /* isCreator */);
                break;
            case RC_JOINER_WAITING_ROOM:
                handleReturnFromWaitingRoom(resultCode, false /* not creator */);
                break;
        }
    }

    @Override
    public void onSignInSucceeded() {
        // we clicked on google play games notification to launch the app, do not display dialog, go directly into the game
        Games.getGamesClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).getActivationHint().addOnSuccessListener(
                new GoogleAccount.SafeOnSuccessListener<Bundle>() {
                    @Override
                    public void onSuccessSafe(Bundle bundle) {
                        if (bundle != null) {
                            Invitation invitation = bundle.getParcelable(Multiplayer.EXTRA_INVITATION);
                            logger.info("invitationId: " + invitation);
                            if (invitation != null) {
                                acceptInvitation(invitation.getInvitationId());
                            }
                        }
                    }
                }
        );

        if (!invitationsLoaded) {
            // we may go into the app directly instead of clicking on the notification.
            Games.getInvitationsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).loadInvitations().addOnSuccessListener(
                    new GoogleAccount.SafeOnSuccessListener<AnnotatedData<InvitationBuffer>>() {
                        @Override
                        public void onSuccessSafe(AnnotatedData<InvitationBuffer> invitationBufferAnnotatedData) {
                            InvitationBuffer buffer = invitationBufferAnnotatedData.get();
                            logger.info("onInvitationsLoaded: " + buffer.getCount() + " " + buffer);
                            for (Invitation invitation : buffer) {
                                logger.info("onInvitationsLoaded invitation: " + invitation);
                                createAcceptDialog(invitation).show();
                            }
                            buffer.release();
                        }
                    }
            );

            Games.getInvitationsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).registerInvitationCallback(this);
            invitationsLoaded = true;
        }
    }

    public void signOut() {
        Games.getInvitationsClient(activity, GoogleSignIn.getLastSignedInAccount(activity)).unregisterInvitationCallback(this);
        invitationsLoaded = false;
    }

    @Override
    public void onInvitationReceived(@NonNull Invitation invitation) {
        logger.info("Invitation received from: " + invitation.getInviter());
        createAcceptDialog(invitation).show();
    }

    @Override
    public void onInvitationRemoved(@NonNull String s) {

    }

    @Override
    public void onSignInFailed() {
        // dont care
    }

    private Participant getMe(List<Participant> participants) {
        // this is sometimes null and sometimes simply does not match anyone in the game, fuck knows why?
        //String myPlayerId = GoogleSignIn.getLastSignedInAccount(activity).getId();
        for (Participant participant : participants) {
            if (myParticipantId != null && myParticipantId.equals(participant.getParticipantId())) {
                return participant;
            }
        }
        throw new RuntimeException(myParticipantId + " not found in " + participants);
    }

    public void startGameGooglePlay(Game game) {
        logger.info("starting player selection");
        lobbyGame = game;
        gameCreator = null;
        if (lobbyGame.getNumOfPlayers() != 1) {
            throw new RuntimeException("should only have creator "+game.getPlayers());
        }

        getRealTimeMultiplayerClient()
            .getSelectOpponentsIntent(GOOGLE_PLAY_GAME_MIN_OTHER_PLAYERS, game.getMaxPlayers() - 1)
                .addOnSuccessListener(new GoogleAccount.SafeOnSuccessListener<Intent>() {
                    @Override
                    public void onSuccessSafe(Intent intent) {
                        intent.putExtra(EXTRA_SHOW_AUTOMATCH, false);
                        activity.startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                }
        );
    }

    private void handlePlayersSelected(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            logger.info("Player selection failed. "+resultCode);
            return;
        }

        openLoadingDialog("mainmenu.googlePlayGame.waitRoom");

        ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        // get auto-match criteria
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

        logger.info("Players selected. Creating room. "+invitees+" "+minAutoMatchPlayers+" "+maxAutoMatchPlayers);

        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(
            new RoomUpdateCallback() {
                @Override
                public void onRoomCreated(int statusCode, Room room) {
                    closeLoadingDialog();
                    if (statusCode != GamesCallbackStatusCodes.OK) {
                        String error = "onRoomCreated failed. "+statusCode+" "+getErrorString(statusCode);
                        logger.warning(error);
                        toast(error);
                        return;
                    }
                    gameRoom = room;
                    logger.info("Starting waiting room activity.");
                    getRealTimeMultiplayerClient().getWaitingRoomIntent(room, 1).addOnSuccessListener(new GoogleAccount.SafeOnSuccessListener<Intent>() {
                        @Override
                        public void onSuccessSafe(Intent intent) {
                            activity.startActivityForResult(intent, RC_CREATOR_WAITING_ROOM);
                        }
                    });
                }

                @Override
                public void onJoinedRoom(int i, @Nullable Room room) {
                    gameRoom = room;
                }

                @Override
                public void onLeftRoom(int i, @NonNull String s) {

                }

                @Override
                public void onRoomConnected(int i, @Nullable Room room) {
                    gameRoom = room;
                }
            })
            .setRoomStatusUpdateCallback(roomStatusUpdateCallback)
            .setOnMessageReceivedListener(new OnRealTimeMessageReceivedListener() {
                @Override
                public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
                    onMessageReceived(realTimeMessage);
                }
            })
            .addPlayersToInvite(invitees);

        if (minAutoMatchPlayers > 0) {
            Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }

        // The variant has to be positive, or else it will throw an Exception.
        roomConfigBuilder.setVariant(lobbyGame.getOptions().hashCode() & 0x7FFFFFFF);

        getRealTimeMultiplayerClient().create(roomConfigBuilder.build());
        logger.info("Room created, waiting for it to be ready");
    }

    private void onMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] data = realTimeMessage.getMessageData();
        try {
            Message message = (Message)encodeDecoder.load(new ByteArrayInputStream(data), data.length);
            onMessageReceived(message, realTimeMessage.getSenderParticipantId());
        }
        catch (IOException ex) {
            logger.log(Level.WARNING, "can not decode", ex);
        }
    }

    private void onMessageReceived(Message message, String from) {
        logger.info("Room message received: " + message);
        String command = message.getCommand();
        if (ProtoAccess.REQUEST_JOIN_GAME.equals(command)) {
            String name = (String)message.getParam();

            if (lobbyGame != null) {
                lobbyGame.getPlayers().add(new net.yura.lobby.model.Player(name, 0));

                int joined = getParticipantStatusCount(Participant.STATUS_JOINED);
                logger.info("new player joined: "+name+" "+lobbyGame.getNumOfPlayers()+"/"+joined+"/"+gameRoom.getParticipantIds().size());
                if (lobbyGame.getNumOfPlayers() == joined) {
                    // TODO can we be not inside lobby???
                    // TODO can we be not logged in?
                    // in case we decided to start with less then the max number of human players
                    // we need to update the max number to the current number, so no one else can join
                    lobbyGame.setMaxPlayers(lobbyGame.getNumOfPlayers());
                    lobby.createNewGame(lobbyGame);
                }
            }
            else {
        	if (gameCreator == null) {
        	    throw new RuntimeException("someone sent me a lobby username, but i dont know what to do");
        	}
        	// we got a username but someone else is the real creator, forward the username to them.
        	sendLobbyUsername(name, gameCreator);
            }
        }
        else if (ProtoAccess.COMMAND_GAME_STARTED.equals(command)) {
            Object param = message.getParam();
            lobby.playGame((Game) param);
        }
        else if (ProtoAccess.REQUEST_HELLO.equals(command)) {
            String creator = (String) message.getParam();
            if (lobbyGame != null) {
        	String myPID = getMe(gameRoom.getParticipants()).getParticipantId();
        	if (creator.compareTo(myPID) == 0) {
        	    throw new RuntimeException("did we just say hello to ourselves?");
        	}
        	else if (creator.compareTo(myPID) < 0) {
        	    gameCreator = creator;
        	    Collection<net.yura.lobby.model.Player> players = lobbyGame.getPlayers();
        	    for (net.yura.lobby.model.Player player : players) {
        		sendLobbyUsername(player.getName(), gameCreator);
        	    }
        	    lobbyGame = null;
        	}
        	// we are the main creator, dont need to do anything
            }
            // we are not a creator, so dont care who is, as long as we send our name to one of the
            // creators it should be ok.
        }
        else {
            logger.warning("unknown command "+message);
        }
    }

    private int getParticipantStatusCount(int status) {
        int count=0;
        for (String id: gameRoom.getParticipantIds()) {
            if (gameRoom.getParticipantStatus(id) == status) {
                count++;
            }
        }
        return count;
    }

    private void handleReturnFromWaitingRoom(int resultCode, boolean isCreator) {
        logger.info("Returning from waiting room. isCreator="+isCreator);
        if (resultCode != Activity.RESULT_OK) {
            logger.info("Room was cancelled, result code = " + resultCode);
            return;
        }
        String myPID = getMe(gameRoom.getParticipants()).getParticipantId();
        logger.info("Room ready. me="+myPID+" "+gameRoom.getParticipantIds()+" creator="+gameRoom.getCreatorId()+" "+lobbyGame+" "+isCreator);
        openLoadingDialog("mainmenu.googlePlayGame.waitGame");
        if (isCreator) {
            if (gameRoom.getAutoMatchCriteria() != null) {
                // send a message to everyone that i think i am the creator.
                List<String> participants = gameRoom.getParticipantIds();
                Message message = new Message();
                message.setCommand(ProtoAccess.REQUEST_HELLO);
                message.setParam(myPID);
                for (String participant : participants) {
                    if (!participant.equals(myPID)) {
                        sendMessage(message, participant);
                    }
                }
            }
        }
        else {
            // send username to any of the game creator, they will know what to do with it.
            lobby.getUsername();
        }
    }

    public void setLobbyUsername(String username) {
        sendLobbyUsername(username, gameRoom.getCreatorId());
    }

    private void sendLobbyUsername(String username, String creator) {
        logger.info("Sending ID to creator. "+username+" "+creator);

        Message message = new Message();
        message.setCommand(ProtoAccess.REQUEST_JOIN_GAME);
        message.setParam(username);

        sendMessage(message, creator);
    }

    public void gameStarted(int id) {
        logger.info("lobby gameStarted " + id + " " + gameRoom + " " + lobbyGame);
        if (gameRoom != null) {
            Message message = new Message();
            message.setCommand(ProtoAccess.COMMAND_GAME_STARTED);
            lobbyGame.setId(id);
            message.setParam(lobbyGame);

            List<String> participants = gameRoom.getParticipantIds();
            for (String participant : participants) {
                sendMessage(message, participant);
            }
        }
    }

    void sendMessage(Message message, String recipientParticipantId) {
	
	// Play Games throws a error if i tell it to send a message to myself
	if (recipientParticipantId.equals(getMe(gameRoom.getParticipants()).getParticipantId())) {
	    onMessageReceived(message, recipientParticipantId);
	}
	else {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream( encodeDecoder.computeAnonymousObjectSize(message) );
            try {
                encodeDecoder.save(bytes, message);
            }
            catch (IOException ex) {
                throw new RuntimeException("can not encode", ex);
            }
            byte[] data = bytes.toByteArray();

            getRealTimeMultiplayerClient().sendReliableMessage(data, gameRoom.getRoomId(), recipientParticipantId, new RealTimeMultiplayerClient.ReliableMessageSentCallback() {
                @Override
                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientId) {
                            logger.info(String.format("Message %d sent (%d) to %s", tokenId, statusCode, recipientId));
                }
            });
	}
    }

    private AlertDialog createAcceptDialog(Invitation invitation) {
        ResourceBundle resb = TranslationBundle.getBundle();
        String title = resb.getString("mainmenu.googlePlayGame.acceptGame");
        String message = RiskUtil.replaceAll(resb.getString("mainmenu.googlePlayGame.invited"), "{0}", invitation.getInviter().getDisplayName());
        String accept = resb.getString("mainmenu.googlePlayGame.accept");
        String reject = resb.getString("mainmenu.googlePlayGame.reject");
        final String invitationId = invitation.getInvitationId();
        return new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(accept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        acceptInvitation(invitationId);
                    }
                })
                .setNegativeButton(reject, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getRealTimeMultiplayerClient().declineInvitation(invitationId);
                    }
                })
                .create();
    }

    private void acceptInvitation(String invitationId) {
        openLoadingDialog("mainmenu.googlePlayGame.waitRoom");
        lobbyGame = null;
        gameCreator = null;

        getRealTimeMultiplayerClient().join(RoomConfig.builder(
                new RoomUpdateCallback() {
                    @Override
                    public void onJoinedRoom(int statusCode, Room room) {
                        closeLoadingDialog();
                        if (statusCode != GamesCallbackStatusCodes.OK) {
                            String error = "onJoinedRoom failed. "+statusCode+" "+getErrorString(statusCode);
                            logger.warning(error);
                            toast(error);
                            return;
                        }
                        gameRoom = room;
                        logger.info("Starting waiting room activity as joiner.");
                        getRealTimeMultiplayerClient().getWaitingRoomIntent(room, 1).addOnSuccessListener(new GoogleAccount.SafeOnSuccessListener<Intent>() {
                            @Override
                            public void onSuccessSafe(Intent intent) {
                                activity.startActivityForResult(intent, RC_JOINER_WAITING_ROOM);
                            }
                        });
                    }

                    @Override
                    public void onRoomCreated(int i, @Nullable Room room) {
                        gameRoom = room;
                    }

                    @Override
                    public void onLeftRoom(int i, @NonNull String s) {

                    }

                    @Override
                    public void onRoomConnected(int i, @Nullable Room room) {
                        gameRoom = room;
                    }
                })
                .setRoomStatusUpdateCallback(roomStatusUpdateCallback)
                .setOnMessageReceivedListener(new OnRealTimeMessageReceivedListener() {
                    @Override
                    public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {
                        onMessageReceived(realTimeMessage);
                    }
                })
                .setInvitationIdToAccept(invitationId)
                .build());
    }

    private void closeLoadingDialog() {
        Intent intent = new Intent(activity, LoadingDialog.class);
        intent.putExtra(LoadingDialog.PARAM_COMMAND, "hide");
        activity.startActivity(intent);
    }

    private void openLoadingDialog(String messageName) {
        Intent intent = new Intent(activity, LoadingDialog.class);
        intent.putExtra(LoadingDialog.PARAM_MESSAGE, TranslationBundle.getBundle().getString(messageName));
        intent.putExtra(LoadingDialog.PARAM_CANCELLABLE, true);
        activity.startActivity(intent);
    }

    void toast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    static String getErrorString(int statusCode) {
        switch(statusCode) {
            case GamesCallbackStatusCodes.OK: return "OK"; // 0
            case GamesCallbackStatusCodes.INTERNAL_ERROR: return "INTERNAL_ERROR"; // 1
            case GamesCallbackStatusCodes.CLIENT_RECONNECT_REQUIRED: return "CLIENT_RECONNECT_REQUIRED"; // 2
            case 3: return "NETWORK_ERROR_STALE_DATA";
            case 4: return "NETWORK_ERROR_NO_DATA";
            case 5: return "NETWORK_ERROR_OPERATION_DEFERRED";
            case 6: return "NETWORK_ERROR_OPERATION_FAILED";
            case 7: return "LICENSE_CHECK_FAILED";
            case 8: return "APP_MISCONFIGURED";

            case 3000: return "ACHIEVEMENT_UNLOCK_FAILURE";
            case 3001: return "ACHIEVEMENT_UNKNOWN";
            case 3002: return "ACHIEVEMENT_NOT_INCREMENTAL";
            case 3003: return "ACHIEVEMENT_UNLOCKED";

            case 6000: return "MULTIPLAYER_ERROR_CREATION_NOT_ALLOWED";
            case 6001: return "MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER";
            case 6002: return "MULTIPLAYER_ERROR_INVALID_MULTIPLAYER_TYPE";
            case 6003: return "MULTIPLAYER_DISABLED";
            case 6004: return "MULTIPLAYER_ERROR_INVALID_OPERATION";

            case 6500: return "MATCH_ERROR_INVALID_PARTICIPANT_STATE";
            case 6501: return "MATCH_ERROR_INACTIVE_MATCH";
            case 6502: return "MATCH_ERROR_INVALID_MATCH_STATE";
            case 6503: return "MATCH_ERROR_OUT_OF_DATE_VERSION";
            case 6504: return "MATCH_ERROR_INVALID_MATCH_RESULTS";
            case 6505: return "MATCH_ERROR_ALREADY_REMATCHED";
            case 6506: return "MATCH_NOT_FOUND";
            case 6507: return "MATCH_ERROR_LOCALLY_MODIFIED";

            case GamesCallbackStatusCodes.REAL_TIME_CONNECTION_FAILED: return "REAL_TIME_CONNECTION_FAILED"; // 7000
            case GamesCallbackStatusCodes.REAL_TIME_MESSAGE_SEND_FAILED: return "REAL_TIME_MESSAGE_SEND_FAILED"; // 7001
            case 7002: return "INVALID_REAL_TIME_ROOM_ID";
            case 7003: return "PARTICIPANT_NOT_CONNECTED";
            case 7004: return "REAL_TIME_ROOM_NOT_JOINED";
            case 7005: return "REAL_TIME_INACTIVE_ROOM";
            case -1: return "REAL_TIME_MESSAGE_FAILED"; // it really is -1
            case 7007: return "OPERATION_IN_FLIGHT";
            default: return "unknown statusCode "+statusCode;
        }
    }
}

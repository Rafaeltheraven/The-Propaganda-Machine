package net.yura.domination.lobby.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import net.yura.domination.engine.RiskIO;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.lobby.server.LobbySession;
import net.yura.lobby.server.TurnBasedGame;
import net.yura.mobile.util.Url;

public class ServerGameRisk extends TurnBasedGame {

	private ServerRisk myrisk;

        static {
            final File gameDir = new File(RiskUtil.GAME_NAME);
            final File mapsDir = new File(gameDir, "maps");

            RiskUtil.streamOpener = new RiskIO() {
                public InputStream openStream(String name) throws IOException {
                    return new FileInputStream(new File(gameDir, name));
                }
                public InputStream openMapStream(String name) throws IOException {
                    return new FileInputStream(new File(mapsDir, name));
                }
                public ResourceBundle getResourceBundle(Class c, String n, Locale l) {
                    // TODO this should be different for different clients connected to this server
                    return ResourceBundle.getBundle(c.getPackage().getName()+"."+n, l );
                }
                public void openURL(URL url) throws Exception {

                }
                public void openDocs(String doc) throws Exception {

                }
                public void saveGameFile(String name, RiskGame obj) throws Exception {

                }
                public InputStream loadGameFile(String file) throws Exception {
                    return null;
                }
                public void getMap(String filename, Observer observer) {
                    observer.update(null, RiskUtil.ERROR);
                }
                public java.io.OutputStream saveMapFile(String fileName) throws Exception {
                    return RiskUtil.getOutputStream(mapsDir , fileName);
                }
                public void renameMapFile(String oldName, String newName) {
                    RiskUtil.rename(new File(mapsDir, oldName), new File(mapsDir, newName));
                }
                public boolean deleteMapFile(String mapName) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };

            try {
                GameSettings settings = new GameSettings(mapsDir);
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                mbs.registerMBean(settings, new ObjectName("net.yura.domination:type=GameSettings") );
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

	public ServerGameRisk() {
		myrisk = new ServerRisk(this);
		// POP UP DEBUG WINDOW
		//Increment1Frame gui = new Increment1Frame( myrisk );
		//RiskGUI gui = new RiskGUI( myrisk );
        	//gui.setVisible(true);
	}

        @Override
        public void setId(int id) {
            super.setId(id);

            myrisk.setName("Domination-Game-"+id);
        }

	public void startGame(String startGameOptions, String[] players) {

		// sort them so if player bob was green last time, they r again
		Arrays.sort(players);

		//System.out.println("\tNEW GAME STARTING FOR RISK: "+gameid);

		//myguid = gameid;

		myrisk.makeNewGame();


		String[] options = startGameOptions.split("\\n");

		int aiaverage = Integer.parseInt(options[0]);
		int aieasy = Integer.parseInt(options[1]);
		int aihard = Integer.parseInt(options[2]);

		if ((players.length+aiaverage+aieasy+aihard)>RiskGame.MAX_PLAYERS ) { throw new RuntimeException("player number missmatch for startgame"); }

		myrisk.addSetupCommandToInbox(options[3]); // set the map file to use

		List<String> colorString = new ArrayList<String>();
		colorString.add( myrisk.getRiskConfig("default.player1.color") );
		colorString.add( myrisk.getRiskConfig("default.player2.color") );
		colorString.add( myrisk.getRiskConfig("default.player3.color") );
		colorString.add( myrisk.getRiskConfig("default.player4.color") );
		colorString.add( myrisk.getRiskConfig("default.player5.color") );
		colorString.add( myrisk.getRiskConfig("default.player6.color") );
		Iterator<String> it = colorString.iterator();

		for (int c=0;c<players.length;c++) {
			it.hasNext();
			String color = it.next();
			String playerid = "player"+(c+1);
			myrisk.addSetupCommandToInbox(playerid,"newplayer human "+color + " " + players[c]);
		}

                // BeginnerBot, RookieBot, AmateurBot and ProBot.
                // Normal Medium Average Standard

		for (int c=0;c<aiaverage;c++) {
			it.hasNext();
			String color = it.next();
			myrisk.addSetupCommandToInbox("newplayer ai average "+color + " AverageBot" + (c+1));
		}

		for (int c=0;c<aieasy;c++) {
			it.hasNext();
			String color = it.next();
			myrisk.addSetupCommandToInbox("newplayer ai easy "+color + " EasyBot" + (c+1));
		}

		for (int c=0;c<aihard;c++) {
			it.hasNext();
			String color = it.next();
			myrisk.addSetupCommandToInbox("newplayer ai hard "+color + " HardBot" + (c+1));
		}

		myrisk.addSetupCommandToInbox(options[4]); // start the game

		// HACK: only return when the game is setup
		while(!myrisk.getWaiting()) {
			try { Thread.sleep(100); }
			catch(InterruptedException e){}
		}

                myrisk.setPaued(false);
	}

        public byte[] saveGameState() {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                myrisk.getGame().saveGame(bout); // TODO this is prob not very thread safe!!
                return bout.toByteArray();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

	public void loadGame(byte[] gameData) {
            try {
                ByteArrayInputStream in = new ByteArrayInputStream(gameData);
                ObjectInputStream oin = new ObjectInputStream(in);
                RiskGame riskGame = (RiskGame)oin.readObject();

                String address=myrisk.getAddress();
                List<Player> players = riskGame.getPlayers();
                for (Player player:players) {
                    if (player.getType()!=Player.PLAYER_HUMAN) {
                        player.setAddress(address);
                    }
                }
                myrisk.setGame(riskGame);
                myrisk.setPaued(false);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
	}

	public void destroyGame() {
		myrisk.setKillFlag();
	}

	public void clientHasJoined(String username) {
		sendObjectToClient(myrisk.getGame(), username);
	}

        /**
         * @return May return null, all methods calling this method must handle null!
         */
        private String getPlayerId(String username) {
            RiskGame game = myrisk.getGame();
            List<Player> players = game.getPlayers();
            for (Player player:players) {
                if (player.getType()==Player.PLAYER_HUMAN && player.getName().equals(username)) {
                    return player.getAddress();
                }
            }
            return null;
        }

	// get message from the user
	public void stringFromPlayer(String username, String message) {
		//System.out.print("\tGOTFROMCLIENT "+username+":"+message+"\n");
		String address = getPlayerId(username);
                Player player = myrisk.getGame().getCurrentPlayer();
		//if (game.getCurrentPlayer()!=null) { System.out.print( "\t"+game.getCurrentPlayer().getAddress()+" "+address ); }
		// game not started OR game IS started and it is there go
		if (message.trim().equals("closegame")) {
			throw new RuntimeException("closegame not allowed to be sent to core: "+username);
		}
                if (message.trim().equals("undo")) {
			throw new RuntimeException("undo not allowed to be sent to core: "+username);
		}
		if (player==null) {
                        throw new RuntimeException("currentPlayer is null");
		}
                if (!player.getAddress().equals( address )) {
                        throw new RuntimeException("got command but it is not our go: username="+username+" address="+address+" message=\""+message+"\" current player: "+player.getName() );
                }

		myrisk.addPlayerCommandToInbox(address , message);
	}


	public void doBasicGo(String username) {
		String playerid = getPlayerId(username);
		// this check is already done
		//if (myrisk.getGame().getCurrentPlayer().getAddress().equals(playerid)) {
			myrisk.addPlayerCommandToInbox(playerid+"-doBasicGo", myrisk.getBasicPassiveGo() );
		//}
		// else something is going very wrong!!!!
		// such as cheating
	}

        @Override
	public boolean playerResigns(String username) {
            boolean gameRemoved = false;

		String playerid = getPlayerId(username);

		//String currentAddress = myrisk.getGame().getCurrentPlayer().getAddress();

		if (playerid != null) {
                        List<Player> players = (List<Player>)myrisk.getGame().getPlayers();
			//myrisk.renamePlayer(username,newName,myrisk.getAddress(),Player.PLAYER_AI_CRAP);

                        String newName = username+"-Resigned";
                        Player thePlayer=null,oldPlayer=null;
                        int aliveHumans=0,aliveAIs=0;
                        for (Player player:players) {
                            String name = player.getName();
                            if (name.equals(username)) {
                                thePlayer = player;
                            }
                            else if (name.equals(newName)) {
                                oldPlayer = player;
                            }
                            // check they are alive
                            else if (player.isAlive()) {
                                if (player.getType()==Player.PLAYER_HUMAN) {
                                    aliveHumans++;
                                }
                                else if (player.getType()!=Player.PLAYER_AI_CRAP) {
                                    aliveAIs++;
                                }
                            }
                        }

                        if(thePlayer==null) { // this should never happen
                            throw new IllegalArgumentException("can not find player "+username);
                        }
                        if (oldPlayer!=null) { // this should never happen
                            throw new IllegalArgumentException("player with name already in game "+newName);
                        }

                        sendRename(username,newName,myrisk.getAddress(),Player.PLAYER_AI_CRAP,true);

                        if (aliveHumans==0) {
                            gameRemoved = gameFinished( whoHasMostPoints() );

                            // We have no humans and no ais, (we only have resigned human, crap/submissive ais)
                            // so we must pause the game or it will get stuck in a loop of placing armies and
                            // nothing else actually happening, and will never finish.

                            // We need this check as if someone is still watching the game, the game does not get
                            // destroyed and will take up more and more space in memory, as round after round goes
                            // by and nothing actually happens

                            // TODO in LobbyClientGUI it is possible to re-join a game at this stage, and the game
                            // will still be paused so it will not actually do anything, then again, if the TurnBasedGame.finished
                            // flag is set, the game will get destroyed when all human players are not watching it anyway
                            if (aliveAIs==0) {
                                myrisk.setPaued(true);
                            }
                        }
		}
                else {
                    throw new RuntimeException("can not resign player "+username+" they are not in this game");
                }

		// already handled by the turn based game
		// ----
		// if they have resigned on there own go then do a go for them
		//if (currentAddress.equals(playerid)) {
		//	myrisk.parser( AIPlayer.getOutput(myrisk.getGame(),AIPlayer.aicrap) );
		//}

                return gameRemoved;
	}

        private void sendRename(String oldName, String newName, String newAddress, int newType, boolean doLegacySend) {
            HashMap map = new HashMap();
            map.put("oldName", oldName);
            map.put("newName", newName);
            map.put("newAddress", newAddress);
            map.put("newType", newType);
            myrisk.addPlayerCommandToInbox("RENAME", Url.toQueryString(RiskUtil.asHashtable(map)) );

// TODO remove when no more <= 45 clients
            if (doLegacySend) {
                map.put("command", "rename");
                for (LobbySession session: getAllClients()) {
                    String username = session.getUsername();
                    String playerid;
                    if (oldName.equals(username) || newName.equals(username)) {
                        if (newType==Player.PLAYER_HUMAN) {
                            playerid = newAddress;
                        }
                        else {
                            playerid="_watch_";
                        }
                    }
                    else {
                        playerid = getPlayerId(username);
                        if (playerid==null) {
                            playerid="_watch_";
                        }
                    }
                    map.put("playerId", playerid);
                    System.out.println("LEGACY RENAME "+map);
                    sendObjectToClient(map,username);
                }
            }
// END TODO
        }

        @Override
        public void playerJoins(String newuser) {
            Player player = myrisk.findEmptySpot();
            if (player==null) {
                throw new RuntimeException("no AI CRAP found in game");
            }
            String playerId = "player"+( myrisk.getGame().getPlayers().indexOf(player) +1);
            String oldName = player.getName();
            sendRename(oldName,newuser,playerId,Player.PLAYER_HUMAN,true);
        }

        @Override
	public void renamePlayer(String oldUser, String newUser) {
            String playerId = getPlayerId(oldUser);
            if (playerId == null) {
                throw new IllegalArgumentException(oldUser + " not found in game player: " + myrisk.getGame().getPlayers());
            }
            sendRename(oldUser, newUser, playerId, Player.PLAYER_HUMAN, false);
	}


	public void getInputFromSomeone() {
            Player player = myrisk.getGame().getCurrentPlayer();
            // Player should never be null, but better not to crash here
            String username = (player != null && player.getType() == Player.PLAYER_HUMAN) ? player.getName() : null;
            getInputFromClient(username);
	}

        private String whoHasMostPoints() {
		RiskGame game = myrisk.getGame();
		if ( game.checkPlayerWon() ) {
			return game.getCurrentPlayer().getName();
		}
                String name="???";
                int best=-1;
                List<Player> players = game.getPlayers();
                for (int c=0;c<players.size();c++) {
                        Player player = players.get(c);
                        // player.getType() == Player.PLAYER_HUMAN &&
                        // if all resign then no humans left
                        if ( player.getNoTerritoriesOwned()>best) {
                                name = player.getName();
                                best = player.getNoTerritoriesOwned();
                        }
                }
                return name;
	}

}

package net.yura.domination.lobby.client;

import java.awt.Component;
import java.awt.Frame;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.swing.Icon;
import net.yura.domination.engine.OnlineRisk;
import net.yura.domination.engine.OnlineUtil;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskIO;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.ui.flashgui.FlashRiskAdapter;
import net.yura.domination.ui.flashgui.GameFrame;
import net.yura.lobby.client.LobbyClientGUI;
import net.yura.lobby.client.ResBundle;
import net.yura.lobby.client.TurnBasedAdapter;
import net.yura.lobby.model.Game;

public class ClientGameRisk extends TurnBasedAdapter implements OnlineRisk {

        private final static Logger logger = Logger.getLogger( ClientGameRisk.class.getName() );

	static {
                final String RISK_PATH = RiskUtil.GAME_NAME + "/";
                final String MAP_PATH = "maps/";

                RiskUtil.streamOpener = new RiskIO() {
                    public InputStream openStream(String name) throws IOException {
                            return LobbyClientGUI.openStream(RISK_PATH+name);
                    }
                    public InputStream openMapStream(String name) throws IOException {
                            return openStream(MAP_PATH+name);
                    }
                    public ResourceBundle getResourceBundle(Class a,String n,Locale l) {
                            return ResBundle.getBundle(a,n,l);
                    }
                    public void openURL(URL url) throws Exception {
                            LobbyClientGUI.openURL(url);
                    }
                    public void openDocs(String doc) throws Exception {
                            openURL( new URL( LobbyClientGUI.getCodeBase(), RISK_PATH+doc) );
                    }
                    public void saveGameFile(String name, RiskGame obj) throws Exception {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    public InputStream loadGameFile(String file) throws Exception {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    public void getMap(String filename, Observer observer) {
                        observer.update(null, RiskUtil.ERROR);
                    }
                    public OutputStream saveMapFile(String fileName) throws Exception {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    public void renameMapFile(String oldName, String newName) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    public boolean deleteMapFile(String mapName) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
            };
	}

	public ClientGameRisk() {

	}


	//##################################################################################
	// game setup
	//##################################################################################

	private GameSetupPanel gsp;

	public Game newGameDialog(Frame parent, String serveroptions,String myname) { // String serveroptions is a list of maps

            if (gsp==null) {
                gsp = new GameSetupPanel();
            }

            return gsp.showDialog(parent, serveroptions, OnlineUtil.getDefaultOnlineGameName(myname));
	}

        Map<String, String> optionsToMapUID = new WeakHashMap();

        public Icon getIcon(String options, Component comp) {
            String mapUID = OnlineUtil.getMapNameFromLobbyStartGameOption(options);
            // keep a strong ref to the mapUID while we have a string ref to the options
            optionsToMapUID.put(options, mapUID);
            return RiskMap.getMapIcon(mapUID).getIcon(32, 20, comp);
	}

        public String getGameDescription(String string) {
            return OnlineUtil.getGameDescriptionFromLobbyStartGameOption(string);
        }

	//##################################################################################
	// in game client stuff
	//##################################################################################


	private Risk myrisk;

	private GameFrame frame;
        
        private GameSidePanel sidepanel;

	public void startNewGame(String name) {
		if (frame==null) {
			myrisk = new Risk();
			makeNewGameFrame();
		}
                sidepanel.setGameName(name);
	}

	private void makeNewGameFrame() {
                sidepanel = new GameSidePanel(timer, startButton, playerListArea, chatBoxArea);

		FlashRiskAdapter riskadapter = new FlashRiskAdapter(myrisk) {

			public void addPlayer(int type, String name, java.awt.Color color, String ip) {}
			public void sendDebug(String a) {  } // System.out.println("\tRISK "+ a);

			public void noInput() {
				if (gameFrame!=null) {
					gameFrame.noInput();
				}
			}

			public void startGame(boolean s) {
				gameFrame.setup(s);

				gameFrame.setVisible(true);
				gameFrame.requestFocus();
			}

			public void closeGame() {
				gameFrame.setVisible(false);
			}

			//public void needInput(int s) {
			//	super.needInput(s);
			//}
		};

		frame = riskadapter.getGameFrame();

                frame.setSidePanel(sidepanel);
		frame.pack();

                RiskUIUtil.setMinimumSize(frame, frame.getPreferredSize());

		// amoung other things, the newplayer command needs to be passed with option that matches this
		// computers Risk.myAddress, or the Risk game would not know when to ask for input
	}

	// this NEEDS to call leaveGame();
	public void closegame() {
            // simulate a normal ui command into the game
            if (myrisk.getGame()!=null) {
		myrisk.parser("closegame");
            }
            else {
                // we are here coz the game failed to open
                leaveGame();
            }
        }

        /**
         * this is called when I resign from a game
         */
	public void blockInput() {
                myrisk.closeBattle();
		frame.blockInput();
	}

	public void gameString(String message) {

		//System.out.println("\tGOT: "+message);

		myrisk.parserFromNetwork(message);
	}

	public void gameObject(Object object) {
            if (object instanceof RiskGame) {
                RiskGame thegame = (RiskGame)object;
                Player player = thegame.getPlayer(lgml.whoAmI());
                String address = player==null?"_watch_":player.getAddress();
                myrisk.setOnlinePlay(this);
                myrisk.setAddress(address);
                myrisk.setGame(thegame);
                updateButtons();
            }
// TODO remove this legacy message system
            else if (object instanceof java.util.Map) {
		Map map = (Map)object;

                String command = (String)map.get("command");
                if ("game".equals(command)) {
                    String address = (String)map.get("playerId");
                    RiskGame thegame = (RiskGame)map.get("game");
                    myrisk.setOnlinePlay(this);
                    myrisk.setAddress(address);
                    myrisk.setGame(thegame);
                    updateButtons();
                }
                else {
                    System.out.println("ClientGameRisk unknown command "+command+" "+map);
                }
            }
// END TODO
            else {
                System.out.println("unknown object "+object);
            }
	}

        // TODO call this method when someone is killed in the game
        private void updateButtons() {
            Player player = myrisk.getGame().getPlayer(lgml.whoAmI());
            updateButton(player!=null && player.isAlive(), myrisk.findEmptySpot() != null );
        }

        // WMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMW
        // WMWMWMWMWMWMWMWMWMWMWMWMWMW OnlineRisk MWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMW
        // WMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMWMW

        public void sendUserCommand(String messagefromgui) {
            sendGameMessage(messagefromgui);
        }

        public void sendGameCommand(String mtemp) {
            // this happens for game commands on my go
            logger.info("ignore GameCommand "+mtemp );
        }

        public boolean isThisMe(String name) {
            return name.equals(lgml.whoAmI());
        }

        public void closeGame() {
            leaveGame();
        }

        public void playerRenamed(String oldName, String newName, String newAddress, int newType) {
            if (oldName.equals(lgml.whoAmI())) {
                myrisk.setAddress("_watch_");
            }
            if (newName.equals(lgml.whoAmI())) {
                myrisk.setAddress(newAddress);
            }

            updateButtons();
        }
}

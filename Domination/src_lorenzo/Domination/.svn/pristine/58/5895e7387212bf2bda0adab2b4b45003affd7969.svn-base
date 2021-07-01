package net.yura.domination.mobile.flashgui;

import javax.microedition.lcdui.Image;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskListener;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.lobby.mini.MiniLobbyRisk;
import net.yura.domination.mobile.flashgui.DominationMain.GooglePlayGameServices;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Menu;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.logging.Logger;
import net.yura.mobile.util.Url;
import net.yura.domination.engine.OnlineUtil;
import net.yura.lobby.mini.MiniLobbyClient;

public class MiniFlashRiskAdapter implements RiskListener {

    private Risk myRisk;
    private MainMenu mainmenu;
    private GameSetup gameSetup;
    private GameActivity gameFrame;

    public MiniLobbyClient lobby;

    public MiniFlashRiskAdapter(Risk risk) {
        myRisk = risk;
        risk.addRiskListener( this );

        email = DominationMain.getAccountsString();
    }

    public void openLobby() {
    	lobby = new net.yura.lobby.mini.MiniLobbyClient(new MiniLobbyRisk(myRisk) {
            @Override
            public void openGameSetup(net.yura.lobby.model.GameType gameType) {
                show("setup");
                String[] split = gameType.getOptions().split(",");
                String[] names = new String[split.length];
                for (int c = 0; c < names.length; c++) {
                    names[c] = Url.decode(split[c]);
                }
                gameSetup.openNewGame(false, names, OnlineUtil.getDefaultOnlineGameName(lobby.whoAmI()));
            }
            @Override
            public String getAppName() {
                return "AndroidDomination";
            }
            @Override
            public String getAppVersion() {
                return DominationMain.version;
            }
            @Override
            public void connected(String username) {
                GooglePlayGameServices play = DominationMain.getGooglePlayGameServices();
                if (play != null) {
                    play.setLobbyUsername(username);
                }
            }
            @Override
            public void joinPrivateGame() {
                DominationMain.getGooglePlayGameServices().beginUserInitiatedSignIn();
            };
            @Override
            public void gameStarted(int id) {
                DominationMain.getGooglePlayGameServices().gameStarted(id);
            }

            @Override
            public void showMessage(String fromwho, String message) {
                MiniLobbyClient.toast(fromwho != null ? fromwho + ": " + message : message);
            }

            @Override
            public void renamePlayer(String oldname, String newname, int newtype) {
                // if we had a player/spectator list we would rename the player there
            }
            @Override
            public void addPlayer(net.yura.lobby.model.Player player) { }
            @Override
            public void removePlayer(String player) { }
        } );

        updatePlayGamesInfo();
        lobby.connect(net.yura.lobby.mini.MiniLobbyClient.LOBBY_SERVER);

        Frame mapFrame = new Frame( lobby.getTitle() ) {
            public void setVisible(boolean b) {
        	super.setVisible(b);
        	if (!b) {
        	    lobby = null;
        	}
            };
        };
        mapFrame.setContentPane( lobby.getRoot() );
        mapFrame.setMaximum(true);
        mapFrame.setVisible(true);
    }

    void createLobbyGame(String name,String options,int numPlayers,int timeout, boolean privateGame) {
        net.yura.lobby.model.Game game = new net.yura.lobby.model.Game(name, options, numPlayers,timeout);
        if (privateGame) {
            game.getPlayers().add(new net.yura.lobby.model.Player(lobby.whoAmI(),0));
            DominationMain.getGooglePlayGameServices().startGameGooglePlay(game);
        }
        else {
            lobby.createNewGame(game);
        }
    }
    boolean shouldShowClosePrompt() {
        return myRisk.getLocalGame() || amOnlinePlayer();
    }
    void addExtraButtons(Menu menu) {
        if (amOnlinePlayer()) {
            Button button = new Button( GameActivity.resb.getString("lobby.resign"), new Icon("/ic_menu_exit.png") );
            button.addActionListener(new ActionListener() {
                public void actionPerformed(String actionCommand) {
                    lobby.resign();
                }
            });
            menu.add(button);
        }
        if (lobby != null) {
            Button button = new Button(GameActivity.resb.getString("lobby.chat"), new Icon("/ic_menu_chat.png"));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(String actionCommand) {
                    lobby.sendChatMessage();
                }
            });
            menu.add(button);
        }
//        else if ( myRisk.findEmptySpot() != null ) {
//            Button button = new Button("Join");
//            button.addActionListener(new ActionListener() {
//                public void actionPerformed(String actionCommand) {
//                    lobby.join();
//                }
//            });
//            menu.add(button);
//        }
    }

    private boolean amOnlinePlayer() {
        Player player = lobby==null?null:myRisk.getGame().getPlayer(lobby.whoAmI());
        return player!=null && player.isAlive();
    }

    // TODO ########### NOT THREAD SAFE!!! ############
    // gameFrame.setVisible(false) can throw a "this window is not visible" error
    // as we may be opening a game, and opening a gamesetup in another thread
    // (in lobby, quickly click on a game and newgame button right after)
    private void show(String what) {

        if (mainmenu!=null) {
            mainmenu.setVisible(false);
            mainmenu = null;
        }
        if (gameSetup!=null) {
            gameSetup.setVisible(false);
            gameSetup = null;
        }
        if (gameFrame!=null) {
            gameFrame.setVisible(false);
            gameFrame = null;
        }

        if ("menu".equals(what)) {
            mainmenu = new MainMenu(myRisk,this);
        }
        else if ("setup".equals(what)) {
            gameSetup = new GameSetup(myRisk,this);
        }
        else if ("game".equals(what)) {
            gameFrame = new GameActivity(myRisk,this);
        }
        else {
            throw new IllegalArgumentException("unknown "+what);
        }
    }

    public void openMainMenu() {
        show("menu");
        mainmenu.openMainMenu();
        updatePlayGamesInfo();
    }

    String googleAccountId,googleAccountIdToken,email;

    public void playGamesStateChanged(String id, String idToken, String email) {
        this.googleAccountId = id;
        this.googleAccountIdToken = idToken;
        this.email = email;
        updatePlayGamesInfo();
    }

    private void updatePlayGamesInfo() {
	GooglePlayGameServices nlc = DominationMain.getGooglePlayGameServices();
	if (nlc != null) {
            if (mainmenu != null) {
                mainmenu.setPlayGamesSingedIn(nlc.isSignedIn());
            }
            if (lobby != null) {
                lobby.setPlayGamesSingedIn(nlc.isSignedIn(), googleAccountId, googleAccountIdToken, email);
            }
	}
    }

    @Override
    public void newGame(boolean t) {
        show("setup");
        gameSetup.openNewGame(t,null,null);
    }

    @Override
    public void closeGame() {
        if (move!=null) {
            move.setVisible(false);
        }
        openMainMenu();
    }

    @Override
    public void sendMessage(String output, boolean redrawNeeded, boolean repaintNeeded) {
        Logger.debug("Game: "+output);
        if (gameFrame!=null) {
            gameFrame.mapRedrawRepaint(redrawNeeded,repaintNeeded);
        }
    }

    // ======================= game setup =============================

    @Override
    public void addPlayer(int type, String name, int color, String ip) {
        gameSetup.updatePlayers();
    }

    @Override
    public void delPlayer(String name) {
        gameSetup.updatePlayers();
    }

    @Override
    public void showMapPic(RiskGame p) {
        gameSetup.showMapPic(p.getMapFile());
    }

    @Override
    public void showCardsFile(String c, boolean m) {
        gameSetup.showCardsFile(c,m);
    }

    @Override
    public void startGame(boolean s) {
        show("game");
        gameFrame.startGame(s);
    }

    // ========================= in game ==============================

    MoveDialog move;
    @Override
    public void needInput(int s) {

        // if for some strange reason this dialog is open and we need some other input, close it
        // this can happen if we timeout in a game during battle won move stage
        if (s!=RiskGame.STATE_BATTLE_WON && move!=null && move.isVisible()) {
            move.setVisible(false);
        }

        if (gameFrame!=null) {
                gameFrame.needInput(s);

                switch(s) {
                    case RiskGame.STATE_ROLLING:
                        battle.needInput(myRisk.getGame().getNoAttackDice(), true);
                        break;
                    case RiskGame.STATE_DEFEND_YOURSELF:
                        battle.needInput(myRisk.getGame().getNoDefendDice(), false);
                        break;
                    case RiskGame.STATE_BATTLE_WON:
                        if (move==null) {
                            move = new MoveDialog(myRisk) {
                                @Override
                                public void setVisible(boolean b) {
                                    super.setVisible(b);
                                    if (!b) {
                                        move=null;
                                    }
                                }
                            };
                        }
                        RiskGame game = myRisk.getGame();
                        int min = game.getMustMove();
                        int c1num = game.getAttacker().getColor();
                        int c2num = game.getDefender().getColor();
                        Image c1img = gameFrame.pp.getCountryImage(c1num);
                        Image c2img = gameFrame.pp.getCountryImage(c2num);
                        move.setupMove(min, c1num, c2num, c1img, c2img, false);

                        // as needInput can get called any number of times, we only show if its not visible
                        if (!move.isVisible()) { move.setVisible(true); }
                        break;
                }
        }
    }

    @Override
    public void noInput() {
        if (gameFrame!=null) {
            gameFrame.noInput();
        }
    }

    BattleDialog battle;
    @Override
    public void openBattle(int c1num, int c2num) {

        if (battle == null) {
            battle = new BattleDialog(myRisk);
        }

        Image c1img = gameFrame.pp.getCountryImage(c1num);
        Image c2img = gameFrame.pp.getCountryImage(c2num);

        battle.setup(c1num, c2num,c1img,c2img);

        // TODO: move main map to centre on where battle is happening

        battle.setVisible(true);

    }

    @Override
    public void closeBattle() {
        battle.setVisible(false);
    }

    @Override
    public void sendDebug(String a) {

    }

    @Override
    public void serverState(boolean s) {

    }

    @Override
    public void setGameStatus(String state) {
        if (gameFrame!=null) {
            gameFrame.setGameStatus(state);
        }
    }

    /**
     * Sets number of attackers
     * @param n number of attackers
     */
    public void setNODAttacker(int n) {
            if (battle.isVisible() ) {
                    battle.setNODAttacker(n);
            }
    }

    /**
     * Sets number of defenders
     * @param n number of defenders
     */
    public void setNODDefender(int n) {
            if (battle.isVisible() ) {
                    battle.setNODDefender(n);
            }
    }

    @Override
    public void showDiceResults(int[] att, int[] def) {
        if (battle.isVisible() ) {
                battle.showDiceResults(att, def);
        }
    }

    @Override
    public void showMessageDialog(String a) {
        OptionPane.showMessageDialog(null, a, null, OptionPane.INFORMATION_MESSAGE);
    }
}

package net.yura.domination.mobile.flashgui;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import net.yura.domination.engine.ColorUtil;
import net.yura.domination.engine.OnlineUtil;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskUtil;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.mapstore.BadgeButton;
import net.yura.domination.mapstore.MapChooser;
import net.yura.domination.mapstore.MapUpdateService;
import net.yura.domination.mobile.MiniUtil;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.ButtonGroup;
import net.yura.mobile.gui.ChangeListener;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.ComboBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Frame;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.OptionPane;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.util.Option;
import net.yura.mobile.util.Properties;

public class GameSetup extends Frame implements ChangeListener,ActionListener {

    private final String[] compsNames;
    private final int[] compTypes;

    // shares res
    Properties resb = GameActivity.resb;
    public Risk myrisk;
    MiniFlashRiskAdapter controller;

    // new game res
    XULLoader newgame;
    Button autoplaceall;
    private boolean localgame;

    // online play
    private String[] allowedMaps;
    String lobbyMapName;
    // end online play

    public GameSetup(Risk risk,MiniFlashRiskAdapter controller) {
        myrisk = risk;
        this.controller = controller;
        setMaximum(true);

        setBorder(GameActivity.marble);
        setBackground( 0x00FFFFFF );

        String[] ais = myrisk.getAICommands();
        compsNames = new String[ais.length+1];
        compTypes = new int[ais.length+1];
        for (int c=0;c<ais.length;c++) {
            compsNames[c] = ais[c]+"AI";
            compTypes[c] = myrisk.getType("ai "+ais[c]);
        }
        compsNames[compsNames.length-1] = "human";
        compTypes[compTypes.length-1] = Player.PLAYER_HUMAN;
    }

    @Override
    public void actionPerformed(String actionCommand) {

            if ("closegame".equals(actionCommand)) { // user clicks back in game setup screen
        	if (localgame) {
        	    myrisk.parser("closegame");
        	}
        	else {
        	    controller.openMainMenu();
        	}
            }
            else if ("startgame".equals(actionCommand)) {
                ButtonGroup gameType = (ButtonGroup)newgame.getGroups().get("GameType");
                ButtonGroup cardType = (ButtonGroup)newgame.getGroups().get("CardType");

                //Button autoplaceall = (Button)newgame.find("autoplaceall");
                Button recycle = (Button)newgame.find("recycle");

                int numOfPlayers = getNoOfPlayers();

                if (numOfPlayers >= 2 && numOfPlayers <= RiskGame.MAX_PLAYERS ) {

                    if (localgame) {
                        RiskUtil.savePlayers(myrisk, getClass());

                        // do not allow the user to accidently tap the start button twice.
                        newgame.find("startButton").setFocusable(false);

                        String gameTypeCommand = gameType.getSelection().getActionCommand();
                        String cardTypeCommand = cardType.getSelection().getActionCommand();
                        boolean autoPlaceAllBoolean = autoplaceall != null && autoplaceall.isSelected();
                        boolean recycleCardsBoolean = recycle != null && recycle.isSelected();

                        DominationMain.saveGameSettings(gameTypeCommand, cardTypeCommand, autoPlaceAllBoolean, recycleCardsBoolean);

                        myrisk.parser("startgame " + gameTypeCommand + " " + cardTypeCommand +
                                (autoPlaceAllBoolean ? " autoplaceall" : "") +
                                (recycleCardsBoolean ? " recycle" : ""));
                    }
                    else {
                        String name = ((TextComponent)newgame.find("GameName")).getText().trim();
                        if ("".equals(name)) {
                            OptionPane.showMessageDialog(null, resb.getProperty("newgame.error.nogamename") , resb.getProperty("newgame.error.title"), OptionPane.ERROR_MESSAGE );
                        }
                        else {
                            boolean privateGame = ((Button) newgame.find("private")).isSelected();
                            int easyAI = getNoPlayers(Player.PLAYER_AI_EASY);
                            int averageAI = getNoPlayers(Player.PLAYER_AI_AVERAGE);
                            int hardAI = getNoPlayers(Player.PLAYER_AI_HARD);
                            controller.createLobbyGame(
                                    name,
                                    OnlineUtil.createGameString(
                                            easyAI,
                                            averageAI,
                                            hardAI,
                                            getStartGameOption(gameType.getSelection().getActionCommand()),
                                            getStartGameOption(cardType.getSelection().getActionCommand()),
                                            autoplaceall.isSelected(),
                                            recycle.isSelected(),
                                            lobbyMapName),
                                    privateGame ? RiskGame.MAX_PLAYERS - easyAI - averageAI - hardAI : getNoPlayers(Player.PLAYER_HUMAN),
                                    Integer.parseInt(((Option) ((ComboBox) newgame.find("TimeoutValue")).getSelectedItem()).getKey()),
                                    privateGame
                            );

                            controller.openMainMenu(); // close the game setup screen
                        }
                    }
                }
                else {
                        OptionPane.showMessageDialog(null, resb.getProperty("newgame.error.numberofplayers") , resb.getProperty("newgame.error.title"), OptionPane.ERROR_MESSAGE );
                }

            }
            else if ("choosemap".equals(actionCommand)) {
                MapListener al = new MapListener();

                MapChooser mapc = new MapChooser(al, MiniUtil.getFileList("map"), allowedMaps == null ? null : new HashSet(Arrays.asList(allowedMaps)));
                al.mapc = mapc;

                Frame mapFrame = new Frame( resb.getProperty("newgame.choosemap") );
                mapFrame.setContentPane( mapc.getRoot() );
                mapFrame.setMaximum(true);
                mapFrame.setVisible(true);
            }
            else if ("mission".equals(actionCommand)) {
                    autoplaceall.setFocusable(false);
            }
            else if ("domination".equals(actionCommand)) {
                    autoplaceall.setFocusable(true);
            }
            else if ("capital".equals(actionCommand)) {
                    autoplaceall.setFocusable(true);
            }
            else if ("increasing".equals(actionCommand) || "fixed".equals(actionCommand) || "italianlike".equals(actionCommand)) {
                // ignore these radio buttons
            }
            else if ("customPlayers".equals(actionCommand)) {
                // TODO
            }
            else {
                System.err.println("GameSetup unknown command: "+actionCommand);
            }
        }

    	int getNoOfPlayers() {
            if (localgame) {
                return myrisk.getGame().getPlayers().size();
            }
            else {
        	int count=0;
        	for (int c=0;c<compTypes.length;c++) {
        	    count = count + getNoPlayers(compTypes[c]);
        	}
        	return count;
            }
    	}
    	int getNoPlayers(int type) {
    	    for (int c=0;c<compTypes.length;c++) {
    		if (compTypes[c]==type) {
    		    Component comp = newgame.find(compsNames[c]);
    		    if (comp!=null) {
    			return ((Integer)((Spinner)comp).getValue()).intValue();
    		    }
    		    return 0;
    		}
    	    }
    	    throw new RuntimeException("invalid type "+type);
    	}
    	int getStartGameOption(String newOption) {
		if ( newOption.equals("domination") ) {
		    return RiskGame.MODE_DOMINATION;
		}
		if ( newOption.equals("capital") ) {
		    return RiskGame.MODE_CAPITAL;
		}
		if ( newOption.equals("mission") ) {
		    return RiskGame.MODE_SECRET_MISSION;
		}
		if ( newOption.equals("increasing") ) {
		    return RiskGame.CARD_INCREASING_SET;
		}
		if ( newOption.equals("fixed") ) {
		    return RiskGame.CARD_FIXED_SET;
		}
		if ( newOption.equals("italianlike") ) {
		    return RiskGame.CARD_ITALIANLIKE_SET;
		}
		throw new RuntimeException("unknown option "+newOption);
    	}
    
    class MapListener implements ActionListener {
        MapChooser mapc;
        public void actionPerformed(String arg0) {
            
            String name = mapc.getSelectedMap();
            if (name != null) {
        	
        	if (localgame) {
                    myrisk.parser("choosemap " + name );
        	}
        	else {
        	    setLobbyMap(name);
        	}
            }
            
            mapc.getRoot().getWindow().setVisible(false);
            
            mapc.destroy();
        }
    };

    void setLobbyMap(String name) {
	lobbyMapName = name;
	showMapPic(name);
        java.util.Map mapinfo = RiskUtil.loadInfo(name,false);
        String cardsFile = (String)mapinfo.get("crd");
        java.util.Map cardsinfo = RiskUtil.loadInfo(cardsFile,true);
        String[] missions = (String[])cardsinfo.get("missions");
        showCardsFile(cardsFile, missions.length > 0);
    }

    // ================================================ GAME SETUP
    public void openNewGame(boolean islocalgame,String[] allowedMaps,String gameName) {

        localgame = islocalgame;
        this.allowedMaps = allowedMaps;

        newgame = GameActivity.getPanel("/newgame.xml",this);

        if (gameName!=null) {
            TextComponent tc = (TextComponent)newgame.find("GameName");
            tc.setText( gameName );
            tc.setVisible(true);
            newgame.find("Timeout").setVisible(true);
            newgame.find("private").setVisible(DominationMain.getGooglePlayGameServices() != null);
        }

        MapUpdateService.getInstance().addObserver( (BadgeButton)newgame.find("MapImg") );


        ButtonGroup gameType = (ButtonGroup)newgame.getGroups().get("GameType");
        ButtonGroup cardType = (ButtonGroup)newgame.getGroups().get("CardType");
        autoplaceall = (Button)newgame.find("autoplaceall");
        Button recycle = (Button)newgame.find("recycle");

        setSelected(gameType, DominationMain.getString(DominationMain.DEFAULT_GAME_TYPE_KEY, gameType.getSelection().getActionCommand()));
        setSelected(cardType, DominationMain.getString(DominationMain.DEFAULT_CARD_TYPE_KEY, cardType.getSelection().getActionCommand()));

        if (autoplaceall != null) {
            autoplaceall.setSelected(DominationMain.getBoolean(DominationMain.DEFAULT_AUTO_PLACE_ALL_KEY, autoplaceall.isSelected()));
        }
        if (recycle != null) {
            recycle.setSelected(DominationMain.getBoolean(DominationMain.DEFAULT_RECYCLE_CARDS_KEY, recycle.isSelected()));
        }

        ((Spinner) newgame.find("human")).setMinimum(localgame ? ("true".equals(System.getProperty("debug")) ? 0 : 1)
                                                               : ("true".equals(System.getProperty("debug")) ? 1 : 2));


        for (int c=0;c<compsNames.length;c++) {
            addChangeListener(compsNames[c]);
        }

        PlayerList playerList = (PlayerList) newgame.find("playerList");

        if (localgame) {
            RiskUtil.loadPlayers( myrisk ,getClass());
            playerList.setGame(myrisk);
        }
        else {
            setLobbyMap( allowedMaps[0] );
            playerList.setVisible(false);
        }





        setTitle(resb.getProperty(localgame?"newgame.title.local":"newgame.title.network"));
        //resetplayers.setVisible(localgame?true:false);

        setContentPane((Panel) newgame.getRoot());
        revalidate();

        setVisible(true);
    }

    private static void setSelected(ButtonGroup group, String actionCommand) {
        Enumeration<Button> buttons = group.getElements();
        while (buttons.hasMoreElements()) {
            Button button = buttons.nextElement();
            if ((actionCommand == null) ? (button.getActionCommand() == null) : actionCommand.equals(button.getActionCommand())) {
                button.setSelected(true);
                return;
            }
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (!b && newgame!=null) {
            MapUpdateService.getInstance().deleteObserver( (BadgeButton)newgame.find("MapImg") );
        }
    }

    private void addChangeListener(String name) {
        Component comp = newgame.find(name);
        if (comp!=null && comp instanceof Spinner) {
            ((Spinner)comp).addChangeListener(this);
        }
    }
    private void removeChangeListener(String name) {
        Component comp = newgame.find(name);
        if (comp!=null && comp instanceof Spinner) {
            ((Spinner)comp).removeChangeListener(this);
        }
    }


    public void updatePlayers() {

        java.util.List players = myrisk.getGame().getPlayers();

        int[] count = new int[compTypes.length];

        for (int c=0;c<players.size();c++) {
            int type = ((Player)players.get(c)).getType();
            for (int a=0;a<compTypes.length;a++) {
                if (type == compTypes[a]) {
                    count[a]++;
                    break;
                }
            }
        }

        for (int c=0;c<compsNames.length;c++) {
            Component comp = newgame.find(compsNames[c]);
            if (comp!=null) {
                // we want to remove the listener first as this update is not user generated
                removeChangeListener(compsNames[c]);
                comp.setValue( new Integer(count[c]) );
                addChangeListener(compsNames[c]);
            }
        }

        // need to revalidate and repaint the playerList
        revalidate();
        repaint();
    }

    public void changeEvent(Component source, int num) {

        int type = -1;
        for (int c=0;c<compsNames.length;c++) {
            Component comp = newgame.find(compsNames[c]);
            if (source == comp) {
                type = compTypes[c];
                break;
            }
        }

        if (type==-1) {
            throw new RuntimeException("type for this Component can not be found "+source); // should also never happen
        }
        
        if (localgame) {
            List players = myrisk.getGame().getPlayers();
            int count=0;
            for (int c=0;c<players.size();c++) {
                int ptype = ((Player)players.get(c)).getType();
                if (ptype == type) {
                    count++;
                }
            }
            int newval = ((Integer)((Spinner)source).getValue()).intValue();

            if (newval<count) {
                for (int c=players.size()-1;c>=0;c--) {
                    Player p = (Player)players.get(c);
                    if (p.getType() == type) {
                        myrisk.parser("delplayer " + p.getName());
                        break;
                    }
                }
            }
            else if (newval>count) {
                if (players.size() == RiskGame.MAX_PLAYERS) {
                    for (int c=players.size()-1;c>=0;c--) {
                        Player p = (Player)players.get(c);
                        if (p.getType()!=type) {
                            p.setType( type );
                            updatePlayers();
                            return;
                        }
                    }
                }
                else {
                    String newname=null;
                    String newcolor=null;
                    for (int c=0;c<Risk.names.length;c++) {
                        boolean badname=false;
                        boolean badcolor=false;
                        for (int a=0;a<players.size();a++) {
                            if (Risk.names[c].equals(((Player)players.get(a)).getName())) {
                                badname = true;
                            }
                            if (ColorUtil.getColor(Risk.colors[c])==((Player)players.get(a)).getColor()) {
                                badcolor = true;
                            }
                            if (badname&&badcolor) {
                                break;
                            }
                        }
                        if (newname==null && !badname) {
                            newname = Risk.names[c];
                        }
                        if (newcolor==null && !badcolor) {
                            newcolor = Risk.colors[c];
                        }
                        if (newname!=null && newcolor!=null) {
                            break;
                        }
                    }

                    if (newname!=null&&newcolor!=null) {
                        myrisk.parser("newplayer " + myrisk.getType(type)+" "+ newcolor+" "+ newname );
                    }
                    else {
                        throw new RuntimeException("new name and color can not be found"); // this should never happen
                    }
                }
            }
        }
        // else network game not done yet

    }

    public void showMapPic(String mapFile) {
/*
        InputStream in=null;
        
        String prv = p.getPreviewPic();
        if (prv!=null) {
            in = MapChooser.getLocalePreviewImg("preview/"+prv);
        }
        if (in==null) {
            in = MapChooser.getLocalePreviewImg( p.getImagePic() );
        }

        Image img=null;
        if (in!=null) {
            try {
                img = MapChooser.createImage(in);
            }
            catch (Exception ex) {
                Logger.warn(ex);
            }
        }
        
        Label label = (Label)newgame.find("MapImg");
        
        if (img!=null) {
            LazyIcon icon = new LazyIcon( adjustSizeToDensityFromMdpi(150) , adjustSizeToDensityFromMdpi(94) ); // 150x94
            icon.setImage( img );
            label.setIcon( icon );
        }
        else {
            label.setIcon( null );
        }
*/
        // crazy 1 liner
        ((Label)newgame.find("MapImg")).setIcon( MapChooser.getLocalIconForMap( MapChooser.createMap( mapFile ) ) );
        
        revalidate();
        repaint();
    }

    public void showCardsFile(String c, boolean hasMission) {
        //cardsFile.setText(c);
        
        Button mission = (Button)newgame.find("mission");
        Button domination = (Button)newgame.find("domination");
        
        if ( !hasMission && mission.isSelected() ) {
            domination.setSelected(true);
            autoplaceall.setFocusable(true);
        }
        mission.setFocusable(hasMission);
    }
}

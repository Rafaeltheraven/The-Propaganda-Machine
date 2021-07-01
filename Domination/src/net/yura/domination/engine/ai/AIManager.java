package net.yura.domination.engine.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.core.RiskGame;
import net.yura.util.Service;

public class AIManager {

    private static int wait=500;
    public static int getWait() {
            return wait;
    }
    public static void setWait(int w) {
            wait = w;
    }



    private final Map<Integer,AI> ais = new HashMap();

    public AIManager() {
        Iterator<Class<AI>> providers = Service.providerClasses(AIManager.class);
        while (providers.hasNext()) {
            try {
                // each AIManager has its own instances of AI players so the state does not leak
                AI ai = providers.next().newInstance();
                int type = ai.getType();
                if ( ais.get( type ) !=null ) {
                    throw new RuntimeException("more then 1 ai with same type");
                }
                ais.put( type , ai );
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void play(Risk risk) {
            RiskGame game = risk.getGame();
            String output = getOutput(game, game.getCurrentPlayer().getType() );
            try { Thread.sleep(wait); }
            catch(InterruptedException e) {}
            risk.parser(output);
    }

    public String getOutput(RiskGame game,int type) {

            AI usethisAI=ais.get(type);

            if (usethisAI==null) {
                throw new IllegalArgumentException("can not find ai for type "+type);
            }

            usethisAI.setGame(game);

            String output=null;

            switch ( game.getState() ) {
                    case RiskGame.STATE_TRADE_CARDS:	output = usethisAI.getTrade(); break;
                    case RiskGame.STATE_PLACE_ARMIES:	output = usethisAI.getPlaceArmies(); break;
                    case RiskGame.STATE_ATTACKING:	output = usethisAI.getAttack(); break;
                    case RiskGame.STATE_ROLLING:	output = usethisAI.getRoll(); break;
                    case RiskGame.STATE_BATTLE_WON:	output = usethisAI.getBattleWon(); break;
                    case RiskGame.STATE_FORTIFYING:	output = usethisAI.getTacMove(); break;
                    case RiskGame.STATE_SELECT_CAPITAL:	output = usethisAI.getCapital(); break;
                    case RiskGame.STATE_DEFEND_YOURSELF:output = usethisAI.getAutoDefendString(); break;
                    case RiskGame.STATE_END_TURN:	output = "endgo"; break;

                    case RiskGame.STATE_GAME_OVER: throw new IllegalStateException("AI error: game is over");
                    default: throw new IllegalStateException("AI error: unknown state "+ game.getState() );
            }

            if (output==null) { throw new NullPointerException("AI ERROR!"); }

            return output;
    }

    public int getTypeFromCommand(String command) {
        for (AI ai:ais.values()) {
            if (ai.getCommand().equals(command)) {
                return ai.getType();
            }
        }
        throw new IllegalArgumentException("unknown command "+command);
    }

    public String getCommandFromType(int type) {
        for (AI ai:ais.values()) {
            if (ai.getType() == type) {
                return ai.getCommand();
            }
        }
        throw new IllegalArgumentException("unknown type "+type);
    }

    public String[] getAICommands() {
        String[] commands = new String[ais.size()];
        int c=0;
        for (AI ai:ais.values()) {
            commands[c++] = ai.getCommand();
        }
        return commands;
    }
}

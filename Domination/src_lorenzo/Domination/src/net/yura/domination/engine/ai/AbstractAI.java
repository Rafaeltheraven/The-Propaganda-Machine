// Yura Mamyrin

package net.yura.domination.engine.ai;

import net.yura.domination.engine.ai.logic.AICapital;
import net.yura.domination.engine.ai.logic.AIDomination;
import net.yura.domination.engine.ai.logic.AIMission;
import net.yura.domination.engine.core.RiskGame;

public abstract class AbstractAI implements AI {
	
    private final AI domination;
    private final AI mission;
    private final AI capital;

    private AI current;
    
    public AbstractAI() {
        int type = getType();
        domination = new AIDomination(type);
        mission = new AIMission(type);
        capital = new AICapital(type);
    }

    public void setGame(RiskGame game) {
        int mode = game.getGameMode();

        if (mode==RiskGame.MODE_CAPITAL) {
            current = capital;
        }
        else if (mode==RiskGame.MODE_SECRET_MISSION) {
            current = mission;
        }
        else {
            current = domination;
        }
        
        current.setGame(game);
    }

    public String getBattleWon() {
        return current.getBattleWon();
    }
    public String getTacMove() {
        return current.getTacMove();
    }
    public String getTrade() {
        return current.getTrade();
    }
    public String getPlaceArmies() {
        return current.getPlaceArmies();
    }
    public String getAttack() {
        return current.getAttack();
    }
    public String getRoll() {
        return current.getRoll();
    }
    public String getCapital() {
        return current.getCapital();
    }
    public String getAutoDefendString() {
        return current.getAutoDefendString();
    }

}

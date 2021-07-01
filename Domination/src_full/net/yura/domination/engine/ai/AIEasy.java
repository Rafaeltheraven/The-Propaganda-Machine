// Yura Mamyrin

package net.yura.domination.engine.ai;

import net.yura.domination.engine.ai.logic.AIDomination;

/**
 * @author Steven Hawkins
 */
public class AIEasy extends AbstractAI {
    
    public int getType() {
        return AIDomination.PLAYER_AI_EASY;
    }

    public String getCommand() {
        return "easy";
    }
    
}

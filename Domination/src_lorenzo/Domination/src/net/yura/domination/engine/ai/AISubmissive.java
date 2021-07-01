// Yura Mamyrin

package net.yura.domination.engine.ai;

import java.util.List;
import java.util.Random;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

/**
 * THIS IS NOT A REAL AI, THIS IS WHAT A HUMAN PLAYER THAT HAS RESIGNED FROM A GAME BECOMES
 * SO THAT OTHER PLAYERS CAN CARRY ON PLAYING, THIS AI NEVER ATTACKS ANYONE, JUST FOLLOWS RULES
 * @author Yura Mamyrin
 */
public class AISubmissive implements AI {

    public int getType() {
        return Player.PLAYER_AI_CRAP;
    }

    public String getCommand() {
        return "crap";
    }

    public void setGame(RiskGame game) {
        this.game = game;
        player = game.getCurrentPlayer();
    }

    protected Random r = new Random(); // this was always static

    protected RiskGame game;
    protected Player player;

    public String getBattleWon() {
	return "move all";
    }

    public String getTacMove() {
	return "nomove";
    }

    public String getTrade() {

            List<Card> cards = player.getCards();

            if (cards.size() < 3) {
                    return "endtrade";
            }

            Card[] result = new Card[3];

            if (game.getBestTrade(cards, result) > 0) {
                    return getTrade(result);
            }

            return "endtrade";
    }

	protected String getTrade(Card[] result) {
		String output = "trade ";
		output = getCardName(result[0], output);
		output = output + " ";
		output = getCardName(result[1], output);
		output = output + " ";
		output = getCardName(result[2], output);
		return output;
	}

    private String getCardName(Card card1, String output) {
            if (card1.getName().equals("wildcard")) {
                    output = output + card1.getName();
            } else {
                    output = output + card1.getCountry().getColor();
            }
            return output;
    }

    public String getPlaceArmies() {
		if ( game.NoEmptyCountries()==false ) {
		    return "autoplace";
		}
		return getPlaceCommand(randomCountry(player.getTerritoriesOwned()), player.getExtraArmies()/3 + player.getExtraArmies()%3);
    }

    public String getAttack() {
	return "endattack";
    }

    public String getRoll() {
	return "retreat";
    }

    public String getCapital() {
	    return "capital " + randomCountry(player.getTerritoriesOwned()).getColor();
    }

    public Country randomCountry(List<Country> countries) {
    	if (countries.isEmpty()) {
    		return null;
    	}
    	return countries.get( r.nextInt(countries.size()) );
    }

    public String getAutoDefendString() {
        int n=game.getDefender().getArmies();
        return "roll "+Math.min(game.getMaxDefendDice(), n);
    }

	protected String getPlaceCommand(Country country, int armies) {
		return "placearmies " + country.getColor() + " " + (!game.getSetupDone()?1:Math.max(1, Math.min(player.getExtraArmies(), armies)));
	}

}

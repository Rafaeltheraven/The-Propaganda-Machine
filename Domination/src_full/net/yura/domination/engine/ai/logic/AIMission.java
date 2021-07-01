//  Group D

package net.yura.domination.engine.ai.logic;

import java.util.List;
import java.util.Map;

import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;

/**
 * @author Steven Hawkins
 *
 * TODO infer the other missions
 * TODO modify isTooWeak and calculate the probable number of countries to take
 */
public class AIMission extends AIDomination {

        public AIMission(int type) {
            super(type);
        }

	@Override
	protected String fortify(GameState gs, List<Country> attackable,
		boolean minimal, List<Country> borders) {
		if (player.getMission().getNoofarmies() > 1 
				&& (player.getTerritoriesOwned().size() - 3 >= player.getMission().getNoofcountries())) {
			String result = super.fortify(gs, attackable, true, player.getTerritoriesOwned());
			if (result != null) {
				return result;
			}
		}
		return super.fortify(gs, attackable, minimal, borders);
	}
	
	protected boolean shouldEndAttack(GameState gameState) {
		boolean result = super.shouldEndAttack(gameState);
		if (result && isCloseToTerritoryTarget()) {
			return false;
		}
		return result;
	}
	
	protected int scoreCountry(Country country) {
		int result = super.scoreCountry(country);
		if (player.getMission().getPlayer() != null && !isTargetMoot()) {
			List<Country> n = country.getNeighbours();
			for (int i = 0; i < n.size(); i++) {
				Country nc = n.get(i);
				if (nc.getOwner() == player.getMission().getPlayer()) {
					result--;
				}
			}
		}
		return result;
	}
	
	private boolean isCloseToTerritoryTarget() {
		return player.getMission().getNoofcountries() > 0 
		&& (player.getMission().getPlayer() == null || isTargetMoot())
		&& player.getTerritoriesOwned().size() - 3 >= player.getMission().getNoofcountries();
	}
	
	protected boolean pressAttack(GameState gameState) {
		boolean result = super.pressAttack(gameState);
		if (!result && isCloseToTerritoryTarget()) {
			return true;
		}
		return result;
	}
	
	protected int getMinPlacement() {
		return Math.max(1, player.getMission().getNoofarmies());
	}
	
	@Override
	public GameState getGameState(Player p, boolean excludeCards) {
		GameState g = super.getGameState(p, excludeCards);
		if (player.getMission().getPlayer() != null && !isTargetMoot() && player.getMission().getPlayer() != g.orderedPlayers.get(0).p) {
			g.targetPlayers.add(0, player.getMission().getPlayer());
		}
		if (g.commonThreat == null) {
			for (PlayerState ps : g.orderedPlayers) {
				if (ps.owned.size() > 1) {
					g.breakOnlyTargets = true;
					g.targetPlayers.add(0, ps.p);
					break;
				}
			}
		}
		return g;
	}
	
	private boolean isTargetMoot() {
		return player.getMission().getPlayer() != null && (player.getMission().getPlayer() == player || player.getMission().getPlayer().getTerritoriesOwned().isEmpty());
	}
	
	protected double getContinentValue(Continent co) {
		double result = super.getContinentValue(co);
		if (isTargetContinent(co)) {
			result *= 4;
		}
		return result;
	}

	private boolean isTargetContinent(Continent co) {
		return player.getMission().getContinent1() == co
				|| player.getMission().getContinent2() == co
				|| player.getMission().getContinent3() == co;
	}
	
	@Override
	protected boolean shouldProactivelyFortify(Continent co, boolean attack,
		List<Country> attackable, GameState gameState,
		Map<Country, AttackTarget> targets, boolean pressAttack,
		List<EliminationTarget> continents) {
		boolean result = super.shouldProactivelyFortify(co, attack, attackable, gameState, targets,
			pressAttack, continents);
		if (result && isTargetContinent(co) && gameState.me.owned.size() > 0) {
			return false;
		}
		return result;
	}
	
}

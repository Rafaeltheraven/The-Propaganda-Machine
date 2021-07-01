//  Group D

package net.yura.domination.engine.ai.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import net.yura.domination.engine.ai.AISubmissive;
import net.yura.domination.engine.core.Card;
import net.yura.domination.engine.core.Continent;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;
import net.yura.domination.engine.core.StatType;
import net.yura.domination.engine.core.Statistic;

/**
 * @author Steven Hawkins
 *
 * TODO:
 * fear reprisals
 */
public class AIDomination extends AISubmissive {

	static final int MAX_AI_TURNS = 300;
	public static final int PLAYER_AI_AVERAGE = 4;
	public final static int PLAYER_AI_HARD = 2;
	public final static int PLAYER_AI_EASY = 1;

    protected final int type;
	private boolean eliminating;
	private Continent breaking;

    public AIDomination(int type) {
        this.type = type;
    }

	/**
	 * Contains quick information about the player
	 */
	static class PlayerState implements Comparable<PlayerState> {
		Player p;
		double attackValue;
		int defenseValue;
		int attackOrder;
		double playerValue;
		Set<Continent> owned;
		int armies;
		boolean strategic;

		public int compareTo(PlayerState ps) {
			if (playerValue != ps.playerValue) {
				return (int)Math.signum(playerValue - ps.playerValue);
			}
			return p.getCards().size() - ps.p.getCards().size();
		}

		public String toString() {
			return p.toString();
		}
	}

	/**
	 * Overview of the Game
	 */
	static class GameState {
		PlayerState me;
		Player[] owned;
		List<PlayerState> orderedPlayers;
		List<Player> targetPlayers = new ArrayList<Player>(3);
		Set<Country> capitals;
		PlayerState commonThreat;
		boolean breakOnlyTargets;
	}

	/**
	 * A single target for attack that may contain may possible attack routes
	 */
	static class AttackTarget implements Comparable<AttackTarget>, Cloneable {
		int remaining = Integer.MIN_VALUE;
		int[] routeRemaining;
		int[] eliminationScore;
		Country[] attackPath;
		Country targetCountry;
		int depth;

		public AttackTarget(int fromCountries, Country targetCountry) {
			routeRemaining = new int[fromCountries];
			Arrays.fill(routeRemaining, Integer.MIN_VALUE);
			attackPath = new Country[fromCountries];
			this.targetCountry = targetCountry;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(targetCountry).append(" ").append(remaining).append(":(");
			for (int i = 0; i < attackPath.length; i ++) {
				if (attackPath[i] == null) {
					continue;
				}
				sb.append(attackPath[i]).append(" ").append(routeRemaining[i]).append(" ");
			}
			sb.append(")");
			return sb.toString();
		}

		public int compareTo(AttackTarget obj) {
			int diff = remaining - obj.remaining;
			if (diff != 0) {
				return diff;
			}
			return targetCountry.getColor() - obj.targetCountry.getColor();
		}

		public AttackTarget clone() {
			try {
				return (AttackTarget) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * A target to eliminate
	 */
	static class EliminationTarget implements Comparable<EliminationTarget> {
		List<AttackTarget> attackTargets = new ArrayList<AttackTarget>();
		PlayerState ps;
		boolean target;
		boolean allOrNone;
		Continent co;

		public int compareTo(EliminationTarget other) {
			if (this.target) {
				return -1;
			}
			if (other.target) {
				return 1;
			}
			int diff = other.ps.p.getCards().size() - ps.p.getCards().size();
			if (diff != 0) {
				return diff;
			}
			return ps.defenseValue - other.ps.defenseValue;
		}

		public String toString() {
			return "Eliminate " + (co != null?co:ps.p);
		}
	}

	public String getPlaceArmies() {
		if (((this.type == AIDomination.PLAYER_AI_EASY && game.NoEmptyCountries() && r.nextInt(6) != 0) //mostly random placement
				|| (game.getSetupDone() && this.type == AIDomination.PLAYER_AI_AVERAGE && r.nextBoolean()))) { //use a random placement half of the time to make the player less aggressive
			return simplePlacement();
	    }
		if ( game.NoEmptyCountries() ) {
			return plan(false);
		}
		return findEmptyCountry();
    }

	private String simplePlacement() {
		if ( !game.NoEmptyCountries()) {
		    return "autoplace";
		}
		List<Country> t = player.getTerritoriesOwned();
		List<Country> n = findAttackableTerritories(player, false);
		List<Country> copy = new ArrayList<Country>(n);
		Country c = null;
		if (n.isEmpty() || t.size() == 1) {
			c = t.get(0);
		    return getPlaceCommand(c, player.getExtraArmies());
		}
		if (n.size() == 1) {
			c = n.get(0);
			return getPlaceCommand(c, player.getExtraArmies());
		}
		HashSet<Country> toTake = new HashSet<Country>();
		Country fallback = null;
		Country overload = null;
		int additional = 1;
		while (!n.isEmpty()) {
			c = n.remove( r.nextInt(n.size()) );
			List<Country> cn = c.getNeighbours();
			for (int i = 0; i < cn.size(); i++) {
				Country other = cn.get(i);
				if (other.getOwner() == player || toTake.contains(other)) {
					continue;
				}
				int diff = 0;
				if (game.getMaxDefendDice() == 2) {
					diff = c.getArmies() - 2 - (3*other.getArmies()/2 + other.getArmies()%2);
				} else {
					diff = c.getArmies() - 2 - 2*other.getArmies();
				}

				if (diff >= 0) {
					if (diff < other.getArmies()*3) {
						//we have enough, but try to overload to be safe
						overload = c;
						additional = other.getArmies()*3 - diff;
					}
					toTake.add(other);
					continue;
				}
				if (-diff <= player.getExtraArmies()) {
					return getPlaceCommand(c, -diff);
				}
				if (fallback == null) {
					fallback = c;
					additional = Math.max(1, -diff);
				}
			}
		}
		if (fallback == null) {
			if (overload != null) {
				return getPlaceCommand(overload, additional);
			}
			//we're fully overloaded, just place the rest
			return getPlaceCommand(randomCountry(copy), player.getExtraArmies());
		}
		return getPlaceCommand(fallback, additional);
	}

    /**
     * ai looks at all the continents and tries to see which one it should place on
     * first it simply looks at the troops on each continent
     * then it looks at each player's potential moves.
	 */
	private String findEmptyCountry() {
		Continent[] cont = game.getContinents();

		double check = -Double.MAX_VALUE;
		Country toPlace = null;
		Map<Player, Integer> players = new HashMap<Player, Integer>();
		for (int i = 0; i < this.game.getPlayers().size(); i++) {
			players.put((Player) this.game.getPlayers().get(i), Integer.valueOf(i));
		}

		List<Continent> conts = new ArrayList<Continent>(Arrays.asList(cont));
		Collections.sort(conts, new Comparator<Continent>() {

			@Override
			public int compare(Continent arg0, Continent arg1) {
				return (int)Math.signum(getContinentValue(arg1) - getContinentValue(arg0));
			}
		});

		outer: for (int i = 0; i < conts.size(); i++) {
			Continent co = conts.get(i);

			List<Country> ct = co.getTerritoriesContained();
			int bestCountryScore = 0;

			boolean hasFree = false;
			Country preferedCountry = null;
			int[] troops = new int[game.getPlayers().size()];

			boolean hasPlacement = false;
			Player otherOwner = null;
			for (int j = 0; j < ct.size(); j++) {
				Country country = ct.get(j);
				if (country.getOwner() == null) {
					hasFree = true;
					int countryScore = scoreCountry(country);
					if (preferedCountry == null || countryScore < bestCountryScore || (countryScore == bestCountryScore && r.nextBoolean())) {
						bestCountryScore = countryScore;
						preferedCountry = country;
					}
				} else {
					Integer index = players.get(country.getOwner());
					troops[index.intValue()]++;
					if (country.getOwner() == player) {
						hasPlacement = true;
					} else if (otherOwner == null) {
						otherOwner = country.getOwner();
					} else if (otherOwner != country.getOwner() && r.nextBoolean()) {
						hasPlacement = true; //this is contested
					}
				}
			}

			if (!hasFree) {
				continue;
			}

			if (type == PLAYER_AI_HARD && !hasPlacement) {
				return getPlaceCommand(preferedCountry, 1);
			}

			/* Calculate the base value of that continent */
			double continentValue = getContinentValue(co);

			for (int j = 0; j < troops.length; j++) {
				int numberofEnemyUnits = 0;
				int territorynum = 1;
				int numberOfEnemies = 0;
				for (int k = 0; k < troops.length; k++) {
					if (j == k) {
						territorynum += troops[k];
					} else {
						numberofEnemyUnits += troops[k];
						if (troops[k] > 0) {
							numberOfEnemies++;
						}
					}
				}

				double score = territorynum / Math.max(1d, (numberofEnemyUnits * numberOfEnemies));
				score *= continentValue;
				score /= bestCountryScore;

				Player p = (Player)game.getPlayers().get(j);

				if (p != this.player) {
					//always block
					if (territorynum == ct.size()) {
						toPlace = preferedCountry;
						break outer;
					}
				}

				if (check <= score) {
					check = score;
					toPlace = preferedCountry;
				} else if (toPlace == null) {
					toPlace = preferedCountry;
				}
			}
		}

		if (toPlace == null) {
			return "autoplace";
		}
		return getPlaceCommand(toPlace, 1);
	}

	/**
	 * Gives a score (lower is better) to a country
	 */
	protected int scoreCountry(Country country) {
		final int n = country.getIncomingNeighbours().size();
		int countryScore = n + 6; //normalize so that 1 is the best score for an empty country
		if (country.getArmies() > 0) {
			countryScore += n;
			countryScore -= country.getArmies();
		}
		if (n < 3) {
			countryScore -= 2;
		}
		if (game.getSetupDone() && country.getCrossContinentNeighbours().size() == 1) {
			countryScore -= 3;
		}
		int neighborBonus = 0;
		int neighbors = 0;
		//defense
		for (int k = 0; k < n; k++) {
			Country cn = country.getIncomingNeighbours().get(k);
			if (cn.getOwner() == player) {
				neighborBonus-=cn.getArmies();
				neighbors++;
			} else if (cn.getOwner() != null) {
				countryScore+=(cn.getArmies()/2 + cn.getArmies()%2);
			}
		}
		int n1 = country.getNeighbours().size();
		//attack
		for (int k = 0; k < n1; k++) {
			Country cn = (Country) country.getNeighbours().get(k);
			if (cn.getOwner() == player) {
				neighborBonus-=cn.getArmies();
				neighbors++;
			} else if (cn.getOwner() == null && cn.getContinent() != country.getContinent()) {
				countryScore--;
			}
		}

		neighbors = neighbors/2 + neighbors%2;
		countryScore += neighborBonus/4 + neighborBonus%2;

		if (!game.getSetupDone() || neighbors > 1) {
			countryScore -= Math.pow(neighbors, 2);
			if (!game.getSetupDone()) {
				countryScore = Math.max(1, countryScore);
			}
		}
		return countryScore;
	}

	/**
	 * General planning method for both attack and placement
	 * TODO should save placement planning state over the whole planning phase (requires refactoring of the aiplayer)
	 *      and should consider all placement moves waited by a utility/probability function and possibly combined
	 *      using an approximation algorithm - currently the logic is greedy and will miss easy lower priority opportunities
	 * @param attack
	 * @return
	 */
	private String plan(boolean attack) {
		List<Country> attackable = findAttackableTerritories(player, attack);
		if (attack && attackable.isEmpty()) {
			return "endattack";
		}
		GameState gameState = getGameState(player, false);

		//kill switch
		if (attack && (game.getCurrentPlayer().getStatistics().size() > MAX_AI_TURNS && (gameState.me.playerValue < gameState.orderedPlayers.get(gameState.orderedPlayers.size() - 1).playerValue || r.nextBoolean()))) {
			boolean keepPlaying = false;
			for (int i = 0; i < game.getPlayers().size(); i++) {
				Player p = (Player)game.getPlayers().get(i);
				if (p.getType() == Player.PLAYER_HUMAN && !p.getTerritoriesOwned().isEmpty()) {
					keepPlaying = true;
					break;
				}
			}
			if (!keepPlaying) {
				Country attackFrom = attackable.get(r.nextInt(attackable.size()));
				for (Country c : (List<Country>)attackFrom.getNeighbours()) {
					if (c.getOwner() != player) {
						return "attack " + attackFrom.getColor() + " " + c.getColor();
					}
				}
			}
		}

		HashMap<Country, AttackTarget> targets = searchAllTargets(attack, attackable, gameState);

		//easy seems to be too hard based upon player feedback, so this dumbs down the play with a greedy attack
		if (attack && player.getType() == PLAYER_AI_EASY && game.getMaxDefendDice() == 2 && game.isCapturedCountry() && r.nextBoolean()) {
			ArrayList<AttackTarget> targetList = new ArrayList<AIDomination.AttackTarget>(targets.values());
			Collections.sort(targetList, Collections.reverseOrder());
			for (AttackTarget at : targetList) {
				if (at.remaining < 1) {
					break;
				}
				int route = findBestRoute(attackable, gameState, attack, null, at, gameState.targetPlayers.get(0), targets);
				Country start = attackable.get(route);
				return getAttack(targets, at, route, start);
			}
		}

		return plan(attack, attackable, gameState, targets);
	}

	private HashMap<Country, AttackTarget> searchAllTargets(Boolean attack, List<Country> attackable, GameState gameState) {
		HashMap<Country, AttackTarget> targets = new HashMap<Country, AttackTarget>();
		for (int i = 0; i < attackable.size(); i++) {
			Country c = attackable.get(i);
			int attackForce = c.getArmies();
			searchTargets(targets, c, attackForce, i, attackable.size(), game.getSetupDone()?player.getExtraArmies():(player.getExtraArmies()/2+player.getExtraArmies()%2), attack, gameState);
		}
		return targets;
	}

	protected String plan(boolean attack, List<Country> attackable, GameState gameState,
			Map<Country, AttackTarget> targets) {
		boolean shouldEndAttack = false;
		boolean pressAttack = false;
		int extra = player.getExtraArmies();
		Set<Country> allCountriesTaken = new HashSet<Country>();
		List<EliminationTarget> continents = findTargetContinents(gameState, targets, attack, true);
		List<Country> v = getBorder(gameState);
		boolean isTooWeak = false;

		//special case planning
		if (game.getSetupDone()) {
			pressAttack = pressAttack(gameState);
			shouldEndAttack = shouldEndAttack(gameState);
			isTooWeak = isTooWeak(gameState);
			//eliminate
			List<EliminationTarget> toEliminate = findEliminationTargets(targets, gameState, attack, extra);
			if (!toEliminate.isEmpty()) {
				Collections.sort(toEliminate);
				for (int i = 0; i < toEliminate.size(); i++) {
					EliminationTarget et = toEliminate.get(i);
					//don't pursue eliminations that will weaken us too much
					int totalCards = player.getCards().size() + et.ps.p.getCards().size();
					if (type == PLAYER_AI_HARD
							&& gameState.orderedPlayers.size() > 1
							&& gameState.me.playerValue < gameState.orderedPlayers.get(0).playerValue
							&& shouldEndAttack
							&& et.ps.armies > gameState.me.armies*.4
						    && et.ps.armies - getCardEstimate(et.ps.p.getCards().size()) > (totalCards>RiskGame.MAX_CARDS?1:(gameState.me.playerValue/gameState.orderedPlayers.get(0).playerValue)) * getCardEstimate(player.getCards().size() + et.ps.p.getCards().size())) {
						toEliminate.remove(i--);
						continue;
					}
					if ((et.ps.p.getCards().isEmpty() && gameState.orderedPlayers.get(0).playerValue > .7*gameState.me.playerValue)
							|| (et.ps.p.getCards().size() > 2 && player.getCards().size() + et.ps.p.getCards().size() <= RiskGame.MAX_CARDS)) {
						//don't consider in a second pass
						toEliminate.remove(i--);
					}
					String result = eliminate(attackable, targets, gameState, attack, extra, allCountriesTaken, et, shouldEndAttack, false);
					if (result != null) {
						eliminating = true;
						return result;
					}
				}
			}

			String objective = planObjective(attack, attackable, gameState, targets, allCountriesTaken, pressAttack, shouldEndAttack, true);
			if (objective != null) {
				return objective;
			}

			if (type == PLAYER_AI_HARD && gameState.orderedPlayers.size() > 1
					&& (isIncreasingSet() || gameState.me.playerValue > gameState.orderedPlayers.get(0).playerValue)) {
				//consider low probability eliminations
				if (!toEliminate.isEmpty()) {
					if (!attack) {
						//redo the target search using low probability
						HashMap<Country, AttackTarget> newTargets = searchAllTargets(true, attackable, gameState);
						outer: for (int i = 0; i < toEliminate.size(); i++) {
							EliminationTarget et = toEliminate.get(i);
							//reset the old targets - the new ones contain the new remaining estimates
							for (int j = 0; j < et.attackTargets.size(); j++) {
								AttackTarget newTarget = newTargets.get(et.attackTargets.get(j).targetCountry);
								if (newTarget == null) {
									//TODO: I don't believe this should be happening
									//throw new AssertionError(et.attackTargets.get(j).targetCountry + " no longer reachable");
									continue outer;
								}
								et.attackTargets.set(j, newTarget);
							}
							String result = eliminate(attackable, newTargets, gameState, attack, extra, allCountriesTaken, et, shouldEndAttack, true);
							if (result != null) {
								eliminating = true;
								return result;
							}
						}
					} else if (isIncreasingSet()){
						//try to pursue the weakest player
						EliminationTarget et = toEliminate.get(0);
						et.allOrNone = false;
						String result = eliminate(attackable, targets, gameState, attack, extra, allCountriesTaken, et, shouldEndAttack, true);
						if (result != null) {
							return result;
						}
					}
				}
				//just try to stay in the game
				if (isIncreasingSet() && gameState.me.defenseValue < gameState.orderedPlayers.get(0).attackValue) {
					shouldEndAttack = true;
				}
			}

			if (!attack && allCountriesTaken.isEmpty() && shouldEndAttack && !pressAttack && !game.getCards().isEmpty()) {
				String result = ensureRiskCard(attackable, gameState, targets, pressAttack,
						continents);
				if (result != null) {
					return result;
				}
			}

			//attack the common threat
			if ((gameState.commonThreat != null && !gameState.commonThreat.owned.isEmpty()) || (gameState.breakOnlyTargets && !isTooWeak)) {
				String result = breakContinent(attackable, targets, gameState, attack, pressAttack, v);
				if (result != null) {
					return result;
				}
			}

			if (!attack && (gameState.orderedPlayers.size() > 1 || player.getCapital() != null || player.getMission() != null || game.getMaxDefendDice() > 2)) {
				String result = fortify(gameState, attackable, true, v);
				if (result != null) {
					//prefer attack to fortification
					if (!continents.isEmpty() && pressAttack && player.getCapital() == null) {
						String toAttack = eliminate(attackable, targets, gameState, attack, extra, allCountriesTaken, continents.get(0), false, false);
						if (toAttack != null) {
							return toAttack;
						}
					}
					return result;
				}
			}

			//free a continent, but only plan to do so if in a good position
			//TODO: this does not consider countries already committed
			if (pressAttack || (type != PLAYER_AI_HARD && attack) || (type == PLAYER_AI_HARD && !isTooWeak
					&& (player.getMission() != null || !gameState.me.owned.isEmpty() || continents.isEmpty() || attack))) {
				String result = breakContinent(attackable, targets, gameState, attack, pressAttack, v);
				if (result != null) {
					return result;
				}
			}
		} else if (!attack) {
			String result = fortify(gameState, attackable, game.getMaxDefendDice() == 2, v);
			if (result != null) {
				return result;
			}
		}

		String objective = planObjective(attack, attackable, gameState, targets, allCountriesTaken, pressAttack, shouldEndAttack, false);
		if (objective != null) {
			return objective;
		}

		//take over a continent
		if (!continents.isEmpty() && (!shouldEndAttack
				|| (!game.isCapturedCountry() && !game.getCards().isEmpty())
				|| !attack
				|| gameState.commonThreat != null
				|| (!isTooWeak && (gameState.breakOnlyTargets || gameState.me.defenseValue > gameState.orderedPlayers.get(0).attackValue)))) {
			int toConsider = continents.size();
			if (attack && isTooWeak) {
				toConsider = 1;
			}
			for (int i = 0; i < toConsider; i++) {
				String result = eliminate(attackable, targets, gameState, attack, extra, allCountriesTaken, continents.get(i), shouldEndAttack, false);
				if (result != null) {
					eliminating = true;
					for (Country c : (List<Country>)continents.get(i).co.getTerritoriesContained()) {
						if (c.getOwner() != player && !allCountriesTaken.contains(c)) {
							eliminating = false;
							break;
						}
					}
					if (shouldProactivelyFortify(continents.get(i).co,
							attack, attackable, gameState,
							targets, pressAttack, continents)) {
						//fortify proactively
						List<Country> border = new ArrayList<Country>();
						for (Country c : (List<Country>)continents.get(i).co.getBorderCountries()) {
							if (c.getOwner() == player) {
								border.add(c);
							}
						}
						String placement = fortify(gameState, attackable, false, border);
						if (placement != null) {
							return placement;
						}
					}
					return result;
				}
			}
			if (!attack) {
				AttackTarget min = null;
				for (int i = 0; i < toConsider; i++) {
					EliminationTarget et = continents.get(i);
					for (int k = 0; k < et.attackTargets.size(); k++) {
						AttackTarget at = et.attackTargets.get(k);
						if (min == null || (!allCountriesTaken.contains(at.targetCountry) && at.remaining < min.remaining)) {
							min = at;
						}
					}
				}
				if (min != null) {
					int route = findBestRoute(attackable, gameState, attack, min.targetCountry.getContinent(), min, game.getSetupDone()?(Player)gameState.targetPlayers.get(0):null, targets);
					if (route != -1) {
						int toPlace = -min.routeRemaining[route] + 2;
						if (toPlace < 0) {
							toPlace = player.getExtraArmies()/3;
						}
						return getPlaceCommand(attackable.get(route), toPlace);
					}
				}
			}
		}

		if (attack) {
			return lastAttacks(attack, attackable, gameState, targets, shouldEndAttack, v);
		}

		String result = fortify(gameState, attackable, false, v);
		if (result != null) {
			return result;
		}

		//fail-safe - TODO: should probably just pile onto the max
		return super.getPlaceArmies();
	}

	protected String planObjective(boolean attack, List<Country> attackable,
			GameState gameState, Map<Country, AttackTarget> targets,
			Set<Country> allCountriesTaken, boolean pressAttack, boolean shouldEndAttack, boolean highProbability) {
		return null;
	}

	protected boolean shouldProactivelyFortify(Continent c, boolean attack,
			List<Country> attackable, GameState gameState,
			Map<Country, AttackTarget> targets, boolean pressAttack,
			List<EliminationTarget> continents) {
		return type == PLAYER_AI_HARD && !isIncreasingSet() && eliminating
				&& gameState.commonThreat == null && !attack && ensureRiskCard(attackable, gameState, targets, pressAttack, continents)==null;
	}

	protected boolean isIncreasingSet() {
		return game.getCardMode() == RiskGame.CARD_INCREASING_SET && (type != PLAYER_AI_HARD || game.getNewCardState() > 12) && (!game.getCards().isEmpty() || game.isRecycleCards());
	}

	private String ensureRiskCard(List<Country> attackable, GameState gameState,
			Map<Country, AttackTarget> targets, boolean pressAttack, List<EliminationTarget> continents) {
		if (this.type == AIDomination.PLAYER_AI_EASY) {
			return null;
		}
		List<AttackTarget> attacks = new ArrayList<AttackTarget>(targets.values());
		Collections.sort(attacks);
		AttackTarget target = null;
		boolean found = false;
		int bestRoute = 0;
		for (int i = attacks.size() - 1; i >= 0; i--) {
			AttackTarget at = attacks.get(i);
			if (target != null && at.remaining < target.remaining) {
				break;
			}
			if (found) {
				continue;
			}
			if (at.remaining > 0) {
				target = null;
				break;
			}
			if (continents.size() > 0 && at.targetCountry.getContinent() == continents.get(0).co) {
				bestRoute = findBestRoute(attackable, gameState, pressAttack, null, at, game.getSetupDone()?(Player) gameState.targetPlayers.get(0):null, targets);
				target = at;
				found = true;
			} else {
				int route = findBestRoute(attackable, gameState, pressAttack, null, at, game.getSetupDone()?(Player) gameState.targetPlayers.get(0):null, targets);
				if (target == null || gameState.targetPlayers.contains(at.targetCountry.getOwner()) || r.nextBoolean()) {
					bestRoute = route;
					target = at;
				}
			}
		}
		if (target != null) {
			return getPlaceCommand(attackable.get(bestRoute), -target.remaining + 1);
		}
		return null;
	}

	/**
	 * one last pass looking to get a risk card or reduce forces
	 */
	private String lastAttacks(boolean attack, List<Country> attackable,
		GameState gameState, Map<Country, AttackTarget> targets, boolean shouldEndAttack, List<Country> border) {
		boolean isTooWeak = isTooWeak(gameState) && gameState.me.defenseValue < .5*gameState.orderedPlayers.get(0).defenseValue;
		boolean forceReduction = game.isCapturedCountry() || game.getCards().isEmpty() || gameState.me.playerValue > 1.5*gameState.orderedPlayers.get(0).playerValue;
		List<AttackTarget> sorted = new ArrayList<AttackTarget>(targets.values());
		Collections.sort(sorted);
		for (int i = sorted.size() - 1; i >= 0; i--) {
			AttackTarget target = sorted.get(i);
			if (target.depth > 1) {
				break; //we don't want to bother considering anything beyond an initial attack
			}
			int bestRoute = findBestRoute(attackable, gameState, attack, null, target, gameState.targetPlayers.get(0), targets);
			if (bestRoute == -1) {
				continue; //shouldn't happen
			}
			Country attackFrom = attackable.get(bestRoute);
			Country initialAttack = getCountryToAttack(targets, target, bestRoute, attackFrom);
			if (forceReduction) {
				//peephole break continent
				if ((attackFrom.getCrossContinentNeighbours().size() == 1 || !border.contains(attackFrom))
						&& attackFrom.getCrossContinentNeighbours().contains(initialAttack)
						&& ((gameState.commonThreat != null && gameState.commonThreat.p == initialAttack.getOwner()) || gameState.targetPlayers.contains(initialAttack.getOwner()) || (gameState.commonThreat == null && !gameState.breakOnlyTargets))
						&& initialAttack.getContinent().getOwner() != null
						&& (!border.contains(attackFrom) || initialAttack.getArmies() == 1 || attackFrom.getArmies() > 3)
						&& target.remaining >= -(attackFrom.getArmies()/2 + attackFrom.getArmies()%2)) {
					return getAttack(targets, target, bestRoute, attackFrom);
				}
				if (gameState.commonThreat != null && gameState.commonThreat.p == initialAttack.getContinent().getOwner() && target.remaining >= -(attackFrom.getArmies()/2 + attackFrom.getArmies()%2)) {
					return getAttack(targets, target, bestRoute, attackFrom);
				}
			} else if (target.remaining >= -(attackFrom.getArmies()/2 + attackFrom.getArmies()%2)) {
				if (gameState.commonThreat != null && !isIncreasingSet() && gameState.commonThreat.p != initialAttack.getOwner() && !gameState.targetPlayers.contains(initialAttack.getOwner()) && initialAttack.getContinent().getOwner() != null) {
					//don't break a continent if there is a common threat
					continue;
				}
				if (type != PLAYER_AI_EASY && attackFrom.getArmies() - target.remaining > getCardEstimate(player.getCards().size()<4?4:5) + initialAttack.getArmies()/2) {
					//don't attack ourselves into the ground and let the force reduction logic kick in
					continue;
				}
				if (isTooWeak && target.remaining < 0 && isIncreasingSet() && player.getCards().isEmpty()) {
					continue;
				}
				return getAttack(targets, target, bestRoute, attackFrom);
			}
		}
		if (!isTooWeak && type != PLAYER_AI_EASY) {
			for (int i = 0; i < sorted.size(); i++) {
				AttackTarget target = sorted.get(i);
				if (target.depth > 1) {
					continue; //we don't want to bother considering anything beyond an initial attack
				}
				int bestRoute = findBestRoute(attackable, gameState, attack, null, target, gameState.targetPlayers.get(0), targets);
				if (bestRoute == -1) {
					continue; //shouldn't happen
				}
				Country attackFrom = attackable.get(bestRoute);
				Country initialAttack = getCountryToAttack(targets, target, bestRoute, attackFrom);
				if (border.contains(attackFrom) && initialAttack.getArmies() < 5) {
					//don't weaken the border for little gain
					continue;
				}
				if (attackFrom.getArmies() < 3 || 
						(game.getMaxDefendDice() > 2 && initialAttack.getArmies() > 2 && (gameState.me.playerValue < 1.5*gameState.orderedPlayers.get(0).playerValue  || game.isCapturedCountry())) ||
						(attackFrom.getArmies() < 4 && attackFrom.getArmies() - 1 <= initialAttack.getArmies())) {
					//don't make an attack where the odds are against us
					continue;
				}
				if (gameState.commonThreat != null) {
					if (gameState.commonThreat.p == initialAttack.getOwner()) {
						return getAttack(targets, target, bestRoute, attackFrom);
					}
				} else {
					if (ownsNeighbours(initialAttack) && target.remaining > -(attackFrom.getArmies()/2 + attackFrom.getArmies()%2)) {
						if ((isIncreasingSet() || gameState.me.playerValue < .8*gameState.orderedPlayers.get(0).playerValue) && !isGoodIdea(gameState, targets, bestRoute, target, attackFrom, null, shouldEndAttack)) {
				        	continue;
				        }
						return getAttack(targets, target, bestRoute, attackFrom);
					}
			        List<Country> neighbours = attackFrom.getIncomingNeighbours();
			        int count = 0;
			        for (int j=0; j<neighbours.size(); j++) {
			           if ( neighbours.get(j).getOwner() != player) {
			        	   count++;
			           }
			        }
			        if (shouldEndAttack && (target.routeRemaining[bestRoute] > 0 && count > 1)) {
			        	//this is just a regular attack, so filter it out
			        	continue;
			        }
			        if (gameState.orderedPlayers.get(0).playerValue > gameState.me.playerValue) {
			        	for (int j = 0; j < gameState.orderedPlayers.size(); j++) {
							PlayerState ps = gameState.orderedPlayers.get(j);
							if (ps.p == initialAttack.getOwner() && (!gameState.breakOnlyTargets || gameState.targetPlayers.contains(ps.p)) &&
									(ps.attackOrder == 1 || gameState.orderedPlayers.size() == 1 || ps.defenseValue > gameState.me.defenseValue*1.2 || (!shouldEndAttack&&isGoodIdea(gameState, targets, bestRoute, target, attackFrom, null, shouldEndAttack)))) {
								return getAttack(targets, target, bestRoute, attackFrom);
							}
						}
			        	continue;
			        }
			        if ((isIncreasingSet() || (game.getCardMode() == RiskGame.CARD_ITALIANLIKE_SET && !game.getCards().isEmpty()) || gameState.me.playerValue < .8*gameState.orderedPlayers.get(0).playerValue) && !isGoodIdea(gameState, targets, bestRoute, target, attackFrom, null, shouldEndAttack)) {
			        	//don't push toward elimination
			        	continue;
			        }
		        	return getAttack(targets, target, bestRoute, attackFrom);
				}
			}
		}
		return "endattack";
	}

	/**
	 * Quick check to see if we're significantly weaker than the strongest player
	 */
	protected boolean isTooWeak(GameState gameState) {
		boolean result = (gameState.orderedPlayers.size() > 1 || player.getMission() != null || player.getCapital() != null) && gameState.me.defenseValue < gameState.orderedPlayers.get(0).attackValue / Math.max(2, gameState.orderedPlayers.size() - 1);
		//early in the game the weakness assessment is too generous as a lot can happen in between turns
		if (!result && type == PLAYER_AI_HARD
				&& gameState.orderedPlayers.size() > 2
				&& (gameState.me.defenseValue < 1.2*gameState.orderedPlayers.get(gameState.orderedPlayers.size() - 1).defenseValue
						|| ((gameState.commonThreat != null || player.getStatistics().size() < 4 || player.getCards().size() < 2) && gameState.me.defenseValue < (game.getMaxDefendDice()==2?1.2:1)*gameState.orderedPlayers.get(0).attackValue))
				&& shouldEndAttack(gameState)) {
			return true;
		}
		return result;
	}

	/**
	 * Stops non-priority attacks if there is too much pressure
	 * @param gameState
	 * @return
	 */
	protected boolean shouldEndAttack(GameState gameState) {
		if (gameState.orderedPlayers.size() < 2 || type == PLAYER_AI_EASY) {
			return false;
		}
		int defense = gameState.me.defenseValue;
		double sum = 0;
		for (int i = 0; i < gameState.orderedPlayers.size(); i++) {
			sum += gameState.orderedPlayers.get(i).attackValue;
		}
		if (defense > sum) {
			return false;
		}
		double ratio = defense/sum;
		if (ratio < .5) {
			return true;
		}
		//be slightly probabilistic about this decision
		return r.nextDouble() > (ratio-.5)*2;
	}

	/**
	 * If the ai should be more aggressive
	 * @param gameState
	 * @return
	 */
	protected boolean pressAttack(GameState gameState) {
		if (this.type == AIDomination.PLAYER_AI_EASY) {
			return r.nextBoolean();
		}
		if (gameState.orderedPlayers.size() < 2) {
			return true;
		}
		int defense = gameState.me.defenseValue;
		double sum = 0;
		for (int i = 0; i < gameState.orderedPlayers.size(); i++) {
			sum += gameState.orderedPlayers.get(i).attackValue;
		}
		return defense > sum;
	}

	/**
	 * Find the continents that we're interested in competing for.
	 * This is based upon how much we control the continent and weighted for its value.
	 */
	private List<EliminationTarget> findTargetContinents(GameState gameState, Map<Country, AttackTarget> targets, boolean attack, boolean filterNoAttacks) {
		Continent[] c = game.getContinents();
		int targetContinents = Math.max(1, c.length - gameState.orderedPlayers.size());
		//step 1 examine continents
		List<Double> vals = new ArrayList<Double>();
		List<EliminationTarget> result = new ArrayList<EliminationTarget>();
		HashSet<Country> seen = new HashSet<Country>();
		for (int i = 0; i < c.length; i++) {
			Continent co = c[i];
			if (gameState.owned[i] != null && (gameState.owned[i] == player || (gameState.commonThreat != null && gameState.commonThreat.p != gameState.owned[i]))) {
				continue;
			}
			List<Country> ct = co.getTerritoriesContained();
			List<AttackTarget> at = new ArrayList<AttackTarget>();
			int territories = 0;
			int troops = 0;
			int enemyTerritories = 0;
		    int enemyTroops = 0;
		    seen.clear();
		    //look at each country to see who owns it
			for (int j = 0; j < ct.size(); j++) {
				Country country = ct.get(j);
				if (country.getOwner() == player) {
					territories++;
					troops += country.getArmies();
				} else {
					AttackTarget t = targets.get(country);
					if (t != null) {
						at.add(t);
					}
					enemyTerritories++;
					int toAttack = 0;
					if (gameState.commonThreat == null || gameState.commonThreat.p != country.getOwner()) {
						toAttack += country.getArmies();
					} else {
						//this will draw the attack toward continents mostly controlled by the common threat
						toAttack += country.getArmies()/2;
					}
					if (toAttack >= game.getMaxDefendDice() && (t == null || t.remaining <= 0)) {
						if (game.getMaxDefendDice() == 2) {
							toAttack = 3*toAttack/2;
						} else {
							toAttack *= 2;
						}
					}
					enemyTroops += toAttack;
				}
				//account for the immediate neighbours
				if (!country.getCrossContinentNeighbours().isEmpty()) {
					for (int k = 0; k < country.getCrossContinentNeighbours().size(); k++) {
						Country ccn = country.getCrossContinentNeighbours().get(k);
						if (seen.add(ccn)) { //prevent counting the same neighbor multiple times
							if (ccn.getOwner() == player) {
								if (country.getOwner() != player) {
									troops += ccn.getArmies()-1;
								}
							} else if (gameState.commonThreat == null) {
								enemyTroops += ccn.getArmies()*.8;
							}
						}
					}
				}
			}
			if (at.isEmpty() && filterNoAttacks) {
				continue; //nothing to attack this turn
			}
			int needed = enemyTroops + enemyTerritories + territories - troops + (attack?game.getMaxDefendDice()*co.getBorderCountries().size():0);
			if (attack && game.isCapturedCountry() && (needed*.8 > troops)) {
				continue; //should build up, rather than attack
			}
			double ratio = Math.max(1, territories + 2d*troops + player.getExtraArmies()/(game.getSetupDone()?2:3))/(enemyTerritories + 2*enemyTroops);
			int pow = 2;
			if (!game.getSetupDone()) {
				pow = 3;
			}
			if (ratio < .5) {
				if (gameState.commonThreat != null) {
					continue;
				}
				//when we have a low ratio, further discourage using a divisor
				ratio/=Math.pow(Math.max(1, enemyTroops-enemyTerritories), pow);
			} else {
				targetContinents++;
			}
			if (gameState.commonThreat == null) {
				//lessen the affect of the value modifier as you control more continents
				ratio *= Math.pow(getContinentValue(co), 1d/(gameState.me.owned.size() + 1));
			}
			Double key = Double.valueOf(-ratio);
			int index = Collections.binarySearch(vals, key);
			if (index < 0) {
				index = -index-1;
			}
			vals.add(index, key);
			EliminationTarget et = new EliminationTarget();
			et.allOrNone = false;
			et.attackTargets = at;
			et.co = co;
			et.ps = gameState.orderedPlayers.get(0);
			result.add(index, et);
		}
		if (result.size() > targetContinents) {
			result = result.subList(0, targetContinents);
		}
		return result;
	}

	/**
	 * Find the best route (the index in attackable) for the given target selection
	 */
	protected int findBestRoute(List<Country> attackable, GameState gameState,
			boolean attack, Continent targetCo, AttackTarget selection, Player targetPlayer, Map<Country, AttackTarget> targets) {
		int bestRoute = 0;
		Set<Country> bestPath = null;
		for (int i = 1; i < selection.routeRemaining.length; i++) {
			if (selection.routeRemaining[i] == Integer.MIN_VALUE) {
				continue;
			}
			int diff = selection.routeRemaining[bestRoute] - selection.routeRemaining[i];
			Country start = attackable.get(i);

			if (selection.routeRemaining[bestRoute] == Integer.MIN_VALUE) {
				bestRoute = i;
				continue;
			}

			//short sighted check to see if we're cutting off an attack line
			if (attack && selection.routeRemaining[i] >= 0 && diff != 0 && selection.routeRemaining[bestRoute] >= 0) {
				HashSet<Country> path = getPath(selection, targets, i, start);
				if (bestPath == null) {
					bestPath = getPath(selection, targets, bestRoute, attackable.get(bestRoute));
				}
				HashSet<Country> path1 = new HashSet<Country>(path);
				for (Iterator<Country> iter = path1.iterator(); iter.hasNext();) {
					Country attacked = iter.next();
					if (!bestPath.contains(attacked) || attacked.getArmies() > 4) {
						iter.remove();
					}
				}
				if (diff < 0 && !path1.isEmpty()) {
			    	HashMap<Country, AttackTarget> specificTargets = new HashMap<Country, AttackTarget>();
			    	searchTargets(specificTargets, start, start.getArmies(), 0, 1, player.getExtraArmies(), attack, Collections.EMPTY_SET, path1, gameState);
			    	int forwardMin = getMinRemaining(specificTargets, start.getArmies(), false, gameState);
			    	if (forwardMin > -diff) {
			    		bestRoute = i;
						bestPath = path;
			    	}
				} else if (diff > 0 && path1.isEmpty() && start.getArmies() >= 3) {
					bestRoute = i;
					bestPath = path;
				}
				continue;
			}

			if (diff == 0 && attack) {
				//range planning during attack is probably too greedy, we try to counter that here
				Country start1 = attackable.get(bestRoute);
				int adjustedCost1 = start1.getArmies() - selection.routeRemaining[bestRoute];
				int adjustedCost2 = start.getArmies() - selection.routeRemaining[i];
				if (adjustedCost1 < adjustedCost2) {
					continue;
				}
				if (adjustedCost2 < adjustedCost1) {
					bestRoute = i;
					continue;
				}
			}

			if ((diff < 0 && (!attack || selection.routeRemaining[bestRoute] < 0))
					|| (diff == 0
							&& ((selection.attackPath[i] != null && selection.attackPath[i].getOwner() == targetPlayer)
									|| (targetPlayer == null || selection.attackPath[bestRoute].getOwner() != targetPlayer) && start.getContinent() == targetCo))) {
				bestRoute = i;
			}
		}
		if (selection.routeRemaining[bestRoute] == Integer.MIN_VALUE) {
			return -1;
		}
		return bestRoute;
	}

	/**
	 * Get a set of the path from start (exclusive) to the given target
	 */
	private HashSet<Country> getPath(AttackTarget at, Map<Country, AttackTarget> targets, int i,
			Country start) {
		HashSet<Country> path = new HashSet<Country>();
		Country toAttack = at.targetCountry;
		path.add(toAttack);
		while (!start.isNeighbours(toAttack)) {
			at = targets.get(at.attackPath[i]);
			toAttack = at.targetCountry;
			path.add(toAttack);
		}
		return path;
	}

	/**
	 * Return the attack string for the given selection
	 */
	protected String getAttack(Map<Country, AttackTarget> targets, AttackTarget selection, int best,
			Country start) {
		Country toAttack = getCountryToAttack(targets, selection, best, start);
		return "attack " + start.getColor() + " " + toAttack.getColor();
	}

	/**
	 * Gets the initial country to attack given the final selection
	 */
	private Country getCountryToAttack(Map<Country, AttackTarget> targets, AttackTarget selection,
			int best, Country start) {
		Country toAttack = selection.targetCountry;
		while (!start.isNeighbours(toAttack)) {
			selection = targets.get(selection.attackPath[best]);
			toAttack = selection.targetCountry;
		}
		return toAttack;
	}

	/**
	 * Simplistic fortification
	 * TODO: should be based upon pressure/continent value
	 */
	protected String fortify(GameState gs, List<Country> attackable, boolean minimal, List<Country> borders) {
		int min = Math.max(game.getMaxDefendDice(), getMinPlacement());
		//at least put 2, which increases defensive odds
		for (int i = 0; i < borders.size(); i++) {
			Country c = borders.get(i);
			if (c.getArmies() < min) {
				return getPlaceCommand(c, min - c.getArmies());
			}
		}
		if (minimal && (!game.getSetupDone() || (isIncreasingSet() && player.getCards().size() > 1))) {
			return null;
		}
		for (int i = 0; i < borders.size(); i++) {
			Country c = borders.get(i);
			//this is a hotspot, at least match the immediate troop level
			int diff = additionalTroopsNeeded(c, gs);
			if (diff > 0) {
				return getPlaceCommand(c, Math.min(player.getExtraArmies(), diff));
			}
			if (!minimal && -diff < c.getArmies() + 2) {
				return getPlaceCommand(c, Math.min(player.getExtraArmies(), c.getArmies() + 2 + diff));
			}
		}
		return null;
	}

	/**
	 * Simplistic (immediate) guess at the additional troops needed.
	 */
	protected int additionalTroopsNeeded(Country c, GameState gs) {
		int needed = 0;
		boolean minimal = !gs.capitals.contains(c);
		List<Country> v = c.getIncomingNeighbours();
		for (int j = 0; j < v.size(); j++) {
			Country n = v.get(j);
			if (n.getOwner() != player) {
				if (minimal) {
					needed = Math.max(needed, n.getArmies());
				} else {
					needed += (n.getArmies() -1);
				}
			}
		}
		if (!isIncreasingSet() && type != PLAYER_AI_EASY && gs.commonThreat == null && gs.me.playerValue < gs.orderedPlayers.get(0).playerValue) {
			for (Country cont : c.getCrossContinentNeighbours()) {
				if (!gs.me.owned.contains(c.getContinent()) && cont.getArmies() < cont.getContinent().getArmyValue()) {
					if (gs.me.owned.contains(cont.getContinent()) && needed > 0) {
						needed += cont.getContinent().getArmyValue();
					} else {
						needed = Math.max(needed, cont.getContinent().getArmyValue()/2);
					}
					break;
				}
			}
		}
		int diff = needed - c.getArmies();
		return diff;
	}

	protected int getMinPlacement() {
		return 1;
	}

	/**
	 * Get the border of my continents, starting with actual borders then the front
	 */
	protected List<Country> getBorder(GameState gs) {
		List<Country> borders = new ArrayList<Country>();
		if (gs.me.owned.isEmpty()) {
			//TODO: could look to build a front
			return borders;
		}
		Set<Country> front = new HashSet<Country>();
		Set<Country> visited = new HashSet<Country>();
		for (Iterator<Continent> i = gs.me.owned.iterator(); i.hasNext();) {
			Continent myCont = i.next();
			List<Country> v = myCont.getBorderCountries();
			for (int j = 0; j < v.size(); j++) {
				Country border = v.get(j);
				if (!ownsNeighbours(border) || isAttackable(border)) {
					borders.add(border);
				} else {
					if (border.getCrossContinentNeighbours().size() == 1) {
						Country country = border.getCrossContinentNeighbours().get(0);
						if (country.getOwner() != player) {
							borders.add(country);
							continue;
						}
					}
					List<Country> n = border.getCrossContinentNeighbours();
					findFront(gs, front, myCont, visited, n);
				}
			}
		}
		borders.addAll(front); //secure borders first, then the front
		return borders;
	}

	private boolean ownsNeighbours(Country c) {
		return ownsNeighbours(player, c);
	}

	/**
	 * return true if the country can be attacked
	 */
	private boolean isAttackable(Country c) {
		for (Country country : c.getIncomingNeighbours()) {
			if (country.getOwner() != player) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Search for the front of my continent
	 */
	private void findFront(GameState gs, Set<Country> front, Continent myCont,
			Set<Country> visited, List<Country> n) {
		Stack<Country> c = new Stack<Country>();
		c.addAll(n);
		while (!c.isEmpty()) {
			Country b = c.pop();
			if (!visited.add(b)) {
				continue;
			}
			if (b.getOwner() == player && b.getContinent() != myCont) {
				if (gs.me.owned.contains(b.getContinent())) {
					continue;
				}
				if (isAttackable(b)) {
					front.add(b);
				} else {
					c.addAll(b.getNeighbours());
				}
			}
		}
	}

	/**
	 * Estimates a baseline value for a continent
	 * @param co
	 * @return
	 */
	protected double getContinentValue(Continent co) {
		int players = 0;
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (!((Player)game.getPlayers().get(i)).getTerritoriesOwned().isEmpty()) {
				players++;
			}
		}
		int freeContinents = game.getContinents().length - players;
		double continentValue = co.getArmyValue() + co.getTerritoriesContained().size()/3;
		int neighbors = 0;
		for (int i = 0; i < co.getBorderCountries().size(); i++) {
			//TODO: update for 1-way
			neighbors += ((Country)co.getBorderCountries().get(i)).getCrossContinentNeighbours().size();
		}
		continentValue /= Math.pow(2*neighbors - co.getBorderCountries().size(), 2);
		if (freeContinents > co.getBorderCountries().size()) {
			continentValue *= co.getBorderCountries().size();
		}
		return continentValue;
	}

	/**
	 * Break continents starting with the strongest player
	 */
	private String breakContinent(List<Country> attackable, Map<Country, AttackTarget> targets, GameState gameState, boolean attack, boolean press, List<Country> borders) {
		List<Continent> toBreak = getContinentsToBreak(gameState);
		if (!attack && type == PLAYER_AI_EASY) {
			return null;
		}
		outer: for (int i = 0; i < toBreak.size(); i++) {
			Continent c = toBreak.get(i);
			Player tp = ((Country)c.getTerritoriesContained().get(0)).getOwner();
			PlayerState ps = null;
			for (int j = 0; j < gameState.orderedPlayers.size(); j++) {
				ps = gameState.orderedPlayers.get(j);
				if (ps.p == tp) {
					break;
				}
			}
			//next level check to see if breaking is a good idea
			if ((!press || !attack) && !gameState.targetPlayers.contains(tp)) {
				if (gameState.commonThreat != null || gameState.breakOnlyTargets) {
					continue outer;
				}
				if (ps.attackOrder != 1 && ps.playerValue < gameState.me.playerValue) {
					continue outer;
				}
			}
			//find the best territory to attack
			List<Country> t = c.getTerritoriesContained();
			int best = Integer.MAX_VALUE;
			AttackTarget selection = null;
			int bestRoute = 0;
			for (int j = 0; j < t.size(); j++) {
				Country target = t.get(j);
				AttackTarget attackTarget = targets.get(target);
				if (attackTarget == null
						|| attackTarget.remaining == Integer.MIN_VALUE
						|| (attackTarget.remaining + player.getExtraArmies() < 1
								&& (!game.getCards().isEmpty() || !press))) {
					continue;
				}
				int route = findBestRoute(attackable, gameState, attack, null, attackTarget, gameState.orderedPlayers.get(0).p, targets);
				Country attackFrom = attackable.get(route);
				if (gameState.commonThreat == null && !gameState.breakOnlyTargets && gameState.me.owned.isEmpty() && attackTarget.routeRemaining[route] + player.getExtraArmies() < 1) {
					continue;
				}
				int cost = attackFrom.getArmies() - attackTarget.routeRemaining[route];
				if (borders.contains(attackFrom)) {
					cost += game.getMaxDefendDice();
				}
				if (cost < best || (cost == best && r.nextBoolean())) {
					best = cost;
					bestRoute = route;
					selection = attackTarget;
				}
			}
			if (selection != null) {
				Country attackFrom = attackable.get(bestRoute);
				if (best > (3*c.getArmyValue() + 2*selection.targetCountry.getArmies()) && game.getMaxDefendDice() == 2) {
					//ensure that breaking doesn't do too much collateral damage
					int value = 3*c.getArmyValue();
					int collateral = 0;
					Set<Country> path = getPath(selection, targets, bestRoute, attackFrom);
					for (Iterator<Country> j = path.iterator(); j.hasNext();) {
						Country attacked = j.next();
						value++;
						if (attacked.getOwner() == selection.targetCountry.getOwner() || gameState.targetPlayers.contains(attacked.getOwner())) {
							if (game.getMaxDefendDice() == 2 || attacked.getArmies() < 3) {
								value += 3*attacked.getArmies()/2 + attacked.getArmies()%2;
							} else {
								value += 2*attacked.getArmies();
							}
						} else {
							if (game.getMaxDefendDice() == 2 || attacked.getArmies() < 3) {
								collateral += 3*attacked.getArmies()/2 + attacked.getArmies()%2;
							} else {
								collateral += 2*attacked.getArmies();
							}
						}
					}
					if (value < best && (!attack || r.nextInt(best - value) != 0) && (gameState.commonThreat == null || !gameState.breakOnlyTargets || value/ps.attackOrder < collateral)) {
						continue outer;
					}
				}
				String result = getMove(targets, attack, selection, bestRoute, attackFrom);
				if (result == null) {
					continue outer;
				}
				breaking = c;
				return result;
			}
		}
		return null;
	}

	/**
	 * Get a list of continents to break in priority order
	 */
	protected List<Continent> getContinentsToBreak(GameState gs) {
		List<Continent> result = new ArrayList<Continent>();
		List<Double> vals = new ArrayList<Double>();
		for (int i = 0; i < gs.owned.length; i++) {
			if (gs.owned[i] != null && gs.owned[i] != player) {
				Continent co = game.getContinents()[i];
				Double val = Double.valueOf(-getContinentValue(co) * game.getContinents()[i].getArmyValue());
				int index = Collections.binarySearch(vals, val);
				if (index < 0) {
					index = -index-1;
				}
				vals.add(index, val);
				result.add(index, co);
			}
		}
		return result;
	}

	/**
	 * Determine if elimination is possible.  Rather than performing a more
	 * advanced combinatorial search, this planning takes simple heuristic passes
	 */
	protected String eliminate(List<Country> attackable, Map<Country, AttackTarget> targets, GameState gameState, boolean attack, int remaining, Set<Country> allCountriesTaken, EliminationTarget et, boolean shouldEndAttack, boolean lowProbability) {
		AttackTarget selection = null;
		int bestRoute = 0;
		if (type == PLAYER_AI_EASY || (type == PLAYER_AI_AVERAGE && !et.allOrNone && r.nextInt(3) != 0) || (!et.allOrNone && !et.target && shouldEndAttack && attack)) {
			//just be greedy, take the best (least costly) attack first
			for (int i = 0; i < et.attackTargets.size(); i++) {
				AttackTarget at = et.attackTargets.get(i);
				if (at.depth != 1 || allCountriesTaken.contains(at.targetCountry)) {
					continue;
				}
				int route = findBestRoute(attackable, gameState, attack, null, at, et.ps.p, targets);
				Country attackFrom = attackable.get(route);
				if (((at.routeRemaining[route] > 0 && (selection == null || at.routeRemaining[route] < selection.routeRemaining[bestRoute] || selection.routeRemaining[bestRoute] < 1))
						|| (at.remaining > 1 && attackFrom.getArmies() > 3 && (selection != null && at.remaining < selection.remaining)))
						&& isGoodIdea(gameState, targets, route, at, attackFrom, et, attack)) {
					selection = at;
					bestRoute = route;
				}
			}
			return getMove(targets, attack, selection, bestRoute, attackable.get(bestRoute));
		}
		//otherwise we use more logic to plan a more complete attack
		//we start with the targets from easiest to hardest and build up the attack paths from there
		Set<Country> countriesTaken = new HashSet<Country>(allCountriesTaken);
		Set<Country> placements = new HashSet<Country>();
		int bestCost = Integer.MAX_VALUE;
		Collections.sort(et.attackTargets, Collections.reverseOrder());
		HashSet<Country> toTake = new HashSet<Country>();
		for (int i = 0; i < et.attackTargets.size(); i++) {
			AttackTarget at = et.attackTargets.get(i);
			if (!allCountriesTaken.contains(at.targetCountry)) {
				toTake.add(at.targetCountry);
			}
		}
		outer: for (int i = 0; i < et.attackTargets.size() && !toTake.isEmpty(); i++) {
			AttackTarget attackTarget = et.attackTargets.get(i);
			if (!toTake.contains(attackTarget.targetCountry)) {
				continue;
			}
			Country attackFrom = null;
			int route = 0;
			boolean clone = true;
			Set<Country> path = null;
			int pathRemaining;
			while (true) {
				route = findBestRoute(attackable, gameState, attack, null, attackTarget, et.ps.p, targets);
				if (route == -1) {
					if (!et.allOrNone) {
						continue outer;
					}
					return null;
				}
				attackFrom = attackable.get(route);
				if (!placements.contains(attackFrom)) {
					pathRemaining = attackTarget.routeRemaining[route];
					if ((pathRemaining + remaining >= 1 //valid single path
							|| (attackTarget.remaining + remaining >= 2 && attackFrom.getArmies() + remaining >= 4)) //valid combination
							&& (et.allOrNone || isGoodIdea(gameState, targets, route, attackTarget, attackFrom, et, attack))) {
						//TODO this is a choice point if there is more than 1 valid path
						path = getPath(attackTarget, targets, route, attackFrom);
						//check to see if this path is good
						if (Collections.disjoint(path, countriesTaken)) {
							//check to see if we can append this path with a nearest neighbor path
							if (pathRemaining + remaining >= 3) {
								HashSet<Country> exclusions = new HashSet<Country>(countriesTaken);
								exclusions.addAll(path);
								Map<Country, AttackTarget> newTargets = new HashMap<Country, AttackTarget>();
								searchTargets(newTargets, attackTarget.targetCountry, pathRemaining, 0, 1, remaining, lowProbability?true:attack, toTake, exclusions, gameState);
								//find the best fit new path if one exists
								AttackTarget newTarget = null;
								for (Iterator<AttackTarget> j = newTargets.values().iterator(); j.hasNext();) {
									AttackTarget next = j.next();
									if (toTake.contains(next.targetCountry)
											&& next.routeRemaining[0] < pathRemaining
											&& next.routeRemaining[0] + remaining >= 1) {
										pathRemaining = next.routeRemaining[0];
										newTarget = next;
									}
								}
								if (newTarget != null) {
									path.addAll(getPath(newTarget, newTargets, 0, attackTarget.targetCountry));
									attackTarget.routeRemaining[route] = pathRemaining;
								}
							}
							break; //a good path, continue with planning
						}
					} else if (et.allOrNone && et.attackTargets.size() == 1
							&& type == PLAYER_AI_HARD
							&& attackTarget.remaining + remaining > -attackTarget.targetCountry.getArmies()
							&& gameState.me.playerValue < gameState.orderedPlayers.get(0).playerValue) {
						//allow hard players to always pursue a single country elimination
						path = getPath(attackTarget, targets, route, attackFrom);
						break;
					}
				}
				if (clone) {
					//clone the attack target so that the find best route logic can have a path excluded
					attackTarget = attackTarget.clone();
					attackTarget.routeRemaining = ArraysCopyOf(attackTarget.routeRemaining, attackTarget.routeRemaining.length);
					clone = false;
				}
				attackTarget.routeRemaining[route] = Integer.MIN_VALUE;
			}
			//process the path found and update the countries take and what to take
			for (Iterator<Country> j = path.iterator(); j.hasNext();) {
				Country c = j.next();
				countriesTaken.add(c);
				toTake.remove(c);
			}
			if (pathRemaining < 1) {
				remaining += pathRemaining -1;
			}
			int cost = attackFrom.getArmies() - pathRemaining;
			if (selection == null || (attack && cost < bestCost && cost > 0)) {
				selection = attackTarget;
				bestCost = cost;
				bestRoute = route;
			}
			placements.add(attackFrom);
		}
		Country attackFrom = attackable.get(bestRoute);
		String result = getMove(targets, attack, selection, bestRoute, attackFrom);
		if (result != null) {
			allCountriesTaken.addAll(countriesTaken);
		}
		return result;
	}

    /**
     * @see Arrays#copyOf(int[], int)
     */
    public static int[] ArraysCopyOf(int[] original, int newLength) {
        int[] copy = new int[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

	/**
	 * ensure that we're not doing something stupid like breaking using too many troops for too little reward or pushing a player to elimination
	 */
	protected boolean isGoodIdea(GameState gameState, Map<Country, AttackTarget> targets, int route, AttackTarget attackTarget, Country attackFrom, EliminationTarget et, boolean attack) {
		Country c = getCountryToAttack(targets, attackTarget, route, attackFrom);
		if (gameState.orderedPlayers.size() > 1 && (et == null || et.ps == null || c.getOwner() != et.ps.p) && !gameState.targetPlayers.contains(c.getOwner())) {
			if (gameState.commonThreat != null && c.getOwner() != gameState.commonThreat.p && c.getContinent().getOwner() != null) {
				return false;
			}
			if (player.getMission() == null && game.getCardMode() == RiskGame.CARD_ITALIANLIKE_SET && c.getOwner().getCards().size() < 4) {
				return true;
			}
			if (gameState.commonThreat != null && c.getOwner().getCards().size() <= 2) {
				return true;
			}
			if (player.getMission() != null || ((attack|| isIncreasingSet()) && (c.getOwner().getCards().size() > 1 || (c.getOwner().getCards().size() == 1 && game.getCards().isEmpty())))) {
				for (int i = gameState.orderedPlayers.size() - 1; i >= 0; i--) {
					PlayerState ps = gameState.orderedPlayers.get(i);
					if (ps.playerValue >= gameState.me.playerValue) {
						break;
					}
					if (ps.p == c.getOwner()) {
						if (ps.attackOrder == 1 && c.getOwner().getCards().size() > 3) {
							return true;
						}
						if (type == PLAYER_AI_HARD && isIncreasingSet()
								&& gameState.me.playerValue < gameState.orderedPlayers.get(0).playerValue
								&& game.getNewCardState() > gameState.me.defenseValue) {
							return true; //you're loosing so just do whatever
						}
						PlayerState top = gameState.orderedPlayers.get(0);
						if (ps.defenseValue - 5*c.getArmies()/4 - c.getArmies()%4 - 1 < 2*(top.attackValue - top.armies/3)/3) {
							return false;
						}
						break;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Gets the move (placement or attack) or returns null if it's not a good attack
	 */
	private String getMove(Map<Country, AttackTarget> targets, boolean attack, AttackTarget selection,
			int route, Country attackFrom) {
		if (selection == null) {
			return null;
		}
		if (attack) {
			if (attackFrom.getArmies() < 5 && selection.remaining < 1) {
				Country toAttack = getCountryToAttack(targets, selection, route, attackFrom);
				if (toAttack.getArmies() >= attackFrom.getArmies()) {
					return null;
				}
			}
			return getAttack(targets, selection, route, attackFrom);
		}
		if (selection.remaining < 1 || selection.routeRemaining[route] < 2) {
			return getPlaceCommand(attackFrom, -selection.routeRemaining[route] + 2);
		}
		return null;
	}

	/**
	 * find the possible elimination targets in priority order
	 * will filter out attacks that seem too costly or if the target has no cards
	 */
	private List<EliminationTarget> findEliminationTargets(Map<Country, AttackTarget> targets, GameState gameState,
			boolean attack, int remaining) {
		List<EliminationTarget> toEliminate = new ArrayList<EliminationTarget>();
		players: for (int i = 0; i < gameState.orderedPlayers.size(); i++) {
			PlayerState ps = gameState.orderedPlayers.get(i);
			Player player2 = ps.p;

			if ((player2.getCards().isEmpty() && player2.getTerritoriesOwned().size() > 1) || ps.defenseValue > gameState.me.attackValue + player.getExtraArmies()) {
				continue;
			}

			boolean isTarget = gameState.targetPlayers.size() > 1 && gameState.targetPlayers.get(0) == player2;
			double divisor = 1;
			int cardCount = player2.getCards().size();
			if ((!isIncreasingSet() || game.getNewCardState() < gameState.me.defenseValue/8) && (!attack || player2.getTerritoriesOwned().size() > 1) && !game.getCards().isEmpty() && cardCount < 3 && (game.getCardMode()==RiskGame.CARD_ITALIANLIKE_SET||(cardCount+player.getCards().size()<RiskGame.MAX_CARDS))) {
				divisor+=(.5*Math.max(0, isIncreasingSet()?2:4 - cardCount));
			}

			if (!isTarget && ps.defenseValue > gameState.me.armies/divisor + player.getExtraArmies()) {
				continue;
			}

			List<Country> targetCountries = player2.getTerritoriesOwned();
			EliminationTarget et = new EliminationTarget();
			et.ps = ps;
			//check for sufficient troops on critical path
			for (int j = 0; j < targetCountries.size(); j++) {
				Country target = targetCountries.get(j);
				AttackTarget attackTarget = targets.get(target);
				if (attackTarget == null
						|| attackTarget.remaining == Integer.MIN_VALUE
						|| (!attack && -attackTarget.remaining > remaining)) {
					continue players;
				}
				et.attackTargets.add(attackTarget);
			}
			et.target = isTarget;
			et.allOrNone = true;
			toEliminate.add(et);
		}
		return toEliminate;
	}

	private void searchTargets(Map<Country, AttackTarget> targets, Country startCountry, int startArmies, final int start, int totalStartingPoints, int extra, boolean attack, GameState gs) {
		searchTargets(targets, startCountry, startArmies, start, totalStartingPoints, extra, attack, Collections.EMPTY_SET, Collections.EMPTY_SET, gs);
	}

	/**
	 * search using Dijkstra's algorithm
	 * If the way points are set, then we're basically doing a traveling salesman nearest neighbor heuristic.
	 * the attack parameter controls cost calculations
	 *  - true neutral
	 *  - false slightly pessimistic
	 */
	private void searchTargets(Map<Country, AttackTarget> targets, Country startCountry, int startArmies, final int start, int totalStartingPoints, int extra, boolean attack, final Set<Country> wayPoints, final Set<Country> exclusions, GameState gameState) {
		PriorityQueue<AttackTarget> remaining = new PriorityQueue<AttackTarget>(11, new Comparator<AttackTarget>() {
			@Override
			public int compare(AttackTarget o1, AttackTarget o2) {
				int diff = o2.routeRemaining[start] - o1.routeRemaining[start];

				if (type == PLAYER_AI_HARD) {
					//heuristic improvement for hard players.
					//give preference to waypoints based upon presumed navigation order
					if (wayPoints.contains(o1.targetCountry)) {
						if (wayPoints.contains(o2.targetCountry)) {
							int outs1 = neighboursOpen(o1.targetCountry);
							int outs2 = neighboursOpen(o2.targetCountry);
							if (outs1 == 1) {
								if (outs2 == 1) {
									//TODO: handle terminal navigation better
									return -diff; //hardest first
								}
								return 1;
							} else if (outs2 == 1) {
								return -1;
							}
							return diff + 2*(outs1 - outs2);
						}
						return -1;
					}
					if (wayPoints.contains(o2)) {
						return 1;
					}
				}
				return diff;
			}

		    public int neighboursOpen( Country c) {
		        List<Country> neighbours = c.getNeighbours();
		        int count = 0;
		        for (int i=0; i<neighbours.size(); i++) {
		           if ( neighbours.get(i).getOwner() != player && !exclusions.contains(c)) {
		        	   count++;
		           }
		        }
		        return count;
		    }
		});
		if (type == PLAYER_AI_HARD) {
			double ratio = gameState.me.playerValue / gameState.orderedPlayers.get(0).playerValue;
			if (ratio < .4) {
				attack = false; //we're loosing, so be more conservative
			}
		} else if (type == PLAYER_AI_EASY) {
			attack = false; //over estimate
		}
		AttackTarget at = new AttackTarget(totalStartingPoints, startCountry);
		at.routeRemaining[start] = startArmies;
		remaining.add(at);
		outer: while (!remaining.isEmpty()) {
			AttackTarget current = remaining.poll();

			//if this is the nearest waypoint, continue the search from this point
			if (wayPoints.contains(current)) {
				Set<Country> path = getPath(current, targets, start, startCountry);
				exclusions.addAll(path);
				startCountry = current.targetCountry;
				targets.keySet().retainAll(exclusions);
				remaining.clear();
				remaining.add(current);
				continue outer;
			}

			int attackForce = current.routeRemaining[start];
			attackForce -= getMinPlacement();
			attackForce -= Math.min(current.targetCountry.getArmies()/(attack?3:2), current.depth);

			if (attackForce + extra < 1) {
				break;
			}

			List<Country> v = current.targetCountry.getNeighbours();

			for (int i = 0; i < v.size(); i++) {
				Country c = v.get(i);
				if (c.getOwner() == player) {
					continue;
				}
				AttackTarget cumulativeForces = targets.get(c);
				if (cumulativeForces == null) {
					if (exclusions.contains(c)) {
						continue;
					}
					cumulativeForces = new AttackTarget(totalStartingPoints, c);
					targets.put(c, cumulativeForces);
				} else if (cumulativeForces.routeRemaining[start] != Integer.MIN_VALUE) {
					continue;
				}
				cumulativeForces.depth = current.depth+1;
				int available = attackForce;
				int toAttack = c.getArmies();
				if (game.getMaxDefendDice() == 2 || gameState.me.playerValue>gameState.orderedPlayers.get(0).playerValue || gameState.me.p.getType() == PLAYER_AI_EASY) {
					if (attack) {
						while (toAttack >= 10 || (available >= 10 && toAttack >= 5)) {
							toAttack -= 4;
							available -= 3;
						}
					}
					while (toAttack >= 5 || (available >= 5 && toAttack >= 2)) {
						toAttack -= 2;
						available -= 2;
					}

				} else {
					//assume 3
					if (attack) {
					    int rounds = (toAttack - 3)/3;
					    if (rounds > 0) {
					       toAttack -= 3*rounds;
					       available -= 3*rounds;
					    }
					}
				}
				if (attack && available == toAttack + 1 && toAttack <= 2) {
					available = 1; //special case to allow 4 on 2 and 3 on 1 attacks
				} else {
					if (game.getMaxDefendDice() == 2 || toAttack <= 2) {
						available = available - 3*toAttack/2 - toAttack%2;
					} else {
						available = available - 2*toAttack;
					}
				}
				cumulativeForces.attackPath[start] = current.targetCountry;
				cumulativeForces.routeRemaining[start] = available;
				if (cumulativeForces.remaining>=0 && available>=0) {
					cumulativeForces.remaining = cumulativeForces.remaining += available;
				} else {
					cumulativeForces.remaining = Math.max(cumulativeForces.remaining, available);
				}
				remaining.add(cumulativeForces);
			}
		}
	}

    public String getBattleWon() {
    	GameState gameState = getGameState(player, false);
    	return getBattleWon(gameState);
    }

    /**
     * Compute the battle won move.  We are just doing a quick reasoning here.
     * Ideally we would consider the full state of move all vs. move min vs. some mix.
     */
	protected String getBattleWon(GameState gameState) {
		if (ownsNeighbours(game.getDefender())) {
    		return "move " + game.getMustMove();
    	}
		int needed = -game.getAttacker().getArmies();
		List<Country> border = getBorder(gameState);

		boolean specialCase = false;
		if (!isIncreasingSet() && !eliminating && type != PLAYER_AI_EASY) {
			/*
			 * we're not in the middle of a planned attack, so attempt to fortify on the fly
			 */
    		Continent cont = null;
    		if (!border.contains(game.getDefender()) || !gameState.me.owned.contains(game.getDefender().getContinent())) {
    			//check if the attacker neighbours one of our continents
	    		for (Country c : game.getAttacker().getCrossContinentNeighbours()) {
					if (gameState.me.owned.contains(c.getContinent())) {
						cont = c.getContinent();
						specialCase = true;
						break;
					}
				}
    		}
    		if (border.contains(game.getAttacker())) {
    			specialCase = true;
    			if (cont != null && game.getCardMode() == RiskGame.CARD_FIXED_SET && border.contains(game.getDefender()) && game.getDefender().getContinent() == game.getAttacker().getContinent()) {
    				cont = null;
    				specialCase = false;
    			} else if (gameState.me.owned.contains(game.getAttacker().getContinent())) {
	    			cont = game.getAttacker().getContinent();
    			}
    		}
    		if (specialCase && game.getCardMode() != RiskGame.CARD_FIXED_SET) {
    			needed = additionalTroopsNeeded(game.getAttacker(), gameState);
    		}
    		if (cont != null) {
    			if (cont.getBorderCountries().size() > 2) {
    				needed += cont.getArmyValue();
    			} else {
    	    		if (specialCase && game.getCardMode() == RiskGame.CARD_FIXED_SET) {
    	    			needed = additionalTroopsNeeded(game.getAttacker(), gameState);
    	    		}
    				needed += (4 * cont.getArmyValue())/Math.max(1, cont.getBorderCountries().size());
    			}
    		} else if (specialCase) {
    			needed += game.getMaxDefendDice();
    		}
    	}
		if (specialCase && ((breaking != null && breaking.getOwner() != null) || gameState.commonThreat != null)) {
			needed/=2;
		}
    	if (!specialCase && ownsNeighbours(game.getAttacker())) {
    		return "move " + Math.max(game.getMustMove(), game.getAttacker().getArmies() - getMinPlacement());
    	}
    	if (!specialCase && game.getMaxDefendDice() == 3 && !ownsNeighbours(game.getAttacker()) && gameState.me.playerValue > gameState.orderedPlayers.get(0).playerValue) {
    		needed += game.getMaxDefendDice(); //make getting cards more difficult
    	}
    	int forwardMin = 0;
    	if (game.getAttacker().getArmies() - 1 > game.getMustMove()) {
	    	Country defender = game.getDefender();
	    	HashMap<Country, AttackTarget> targets = new HashMap<Country, AttackTarget>();
	    	searchTargets(targets, defender, game.getAttacker().getArmies() - 1, 0, 1, player.getExtraArmies(), true, gameState);
	    	forwardMin = getMinRemaining(targets,  game.getAttacker().getArmies() - 1, border.contains(game.getAttacker()), gameState);
	    	if (forwardMin == Integer.MAX_VALUE) {
	    		return "move " + game.getMustMove();
	    	}
    	}
    	return "move " + Math.max(Math.min(-needed, game.getAttacker().getArmies() - Math.max(getMinPlacement(), forwardMin)), game.getMustMove());
	}

	/**
	 * Get an estimate of the remaining troops after taking all possible targets
	 */
	private int getMinRemaining(HashMap<Country, AttackTarget> targets, int forwardMin, boolean isBorder, GameState gameState) {
		int total = 0;
		for (Iterator<AttackTarget> i = targets.values().iterator(); i.hasNext();) {
			AttackTarget attackTarget = i.next();
			if (attackTarget.remaining < 0 && !isBorder) {
				return 0;
			}
			//estimate a cost for the territory
			total += 1;
			if (game.getMaxDefendDice() == 2 || attackTarget.targetCountry.getArmies() < 3) {
				total += attackTarget.targetCountry.getArmies();
				if (attackTarget.targetCountry.getArmies() < 2) {
					total += attackTarget.targetCountry.getArmies();
				}
			} else {
				total += 2*attackTarget.targetCountry.getArmies();
			}
		}
		if (game.getMaxDefendDice() == 2) {
			forwardMin -= (total *= 1.3);
		} else {
			forwardMin -= total;
		}
		if (type == PLAYER_AI_HARD && !isIncreasingSet() && isBorder && isTooWeak(gameState)) {
			//TODO: let the hard player lookahead further, alternatively just call to plan(true) and mark if we are doing an elimination or something
			return Integer.MAX_VALUE;
		}
		return Math.max(isBorder?game.getMaxDefendDice():0, forwardMin);
	}

	/**
	 * Takes several passes over applicable territories to determine the tactical move.
	 * 1. Find all countries with more than the min placement and do the best border fortification possible.
	 *  1.a. If there is a common threat see if we can move off of a continent we don't want
	 * 2. Move the most troops to the battle from a non-front country.
	 * 3. just move from the interior - however this doesn't yet make a smart choice.
	 */
	public String getTacMove() {
		List<Country> t = player.getTerritoriesOwned();
		Country sender = null;
		Country receiver = null;
		int lowestScore = Integer.MAX_VALUE;
		GameState gs = getGameState(player, false);
		//fortify the border
		List<Country> v = getBorder(gs);
		List<Country> filtered = new ArrayList<Country>();

		List<Continent> targetContinents = null;

		for (int i = 0; i < t.size(); i++) {
			Country c = t.get(i);
			if (c.getArmies() <= getMinPlacement() || gs.capitals.contains(c)) {
				continue;
			}
			//cooperation check to see if we should leave this continent
			if (c.getArmies() > 2 && gs.commonThreat != null && c.getCrossContinentNeighbours().size() > 0 && !ownsNeighbours(c)) {
				coop: for (int j = 0; j < c.getNeighbours().size(); j++) {
					Country n = (Country)c.getNeighbours().get(j);
					if (n.getOwner() == player && n.getContinent() != c.getContinent()) {
						//we have another continent to go to, ensure that the original continent is not desirable
						if (targetContinents == null) {
							List<EliminationTarget> co = findTargetContinents(gs, Collections.EMPTY_MAP, false, false);
							targetContinents = new ArrayList<Continent>();
							for (int k = 0; k < co.size(); k++) {
								EliminationTarget et = co.get(k);
								targetContinents.add(et.co);
							}
						}
						int index = targetContinents.indexOf(c.getContinent());
						if (index == -1 && c.getContinent().getOwner() == player) {
							break coop;
						}
						int indexOther = targetContinents.indexOf(n.getContinent());
						if ((indexOther > -1 && (index == -1 || index > indexOther)) || ((index == -1 || index > 0) && n.getContinent().getOwner() == player)) {
							int toSend = c.getArmies() - getMinPlacement();
							return getMoveCommand(c, n, toSend);
						}
					}
				}
			}
			if (v.contains(c) && additionalTroopsNeeded(c, gs)/2 + getMinPlacement() >= 0) {
				continue;
			}
			filtered.add(c);
			int score = scoreCountry(c);
			for (int j = 0; j < c.getNeighbours().size(); j++) {
				Country n = (Country)c.getNeighbours().get(j);
				if (n.getOwner() != player || !v.contains(n) || additionalTroopsNeeded(n, gs) < -1) {
					continue;
				}
				int total = -score + scoreCountry(n);
				if (total < lowestScore) {
					sender = c;
					receiver = n;
					lowestScore = total;
				}
			}
		}
		if (receiver != null) {
			int toSend = sender.getArmies() - getMinPlacement();
			if (v.contains(sender)) {
				toSend = -additionalTroopsNeeded(sender, gs)/2 - getMinPlacement();
			}
			return getMoveCommand(sender, receiver, toSend);
		}
		//move to the battle
		Country max = null;
		for (int i = filtered.size() - 1; i >= 0; i--) {
			Country c = filtered.get(i);
			if (!ownsNeighbours(c)) {
				filtered.remove(i);
				continue;
			}
			if (max == null || c.getArmies() > max.getArmies()) {
				max = c;
			}
			int score = scoreCountry(c);
			for (int j = 0; j < c.getNeighbours().size(); j++) {
				Country n = (Country)c.getNeighbours().get(j);
				if (n.getOwner() != player || ownsNeighbours(n)) {
					continue;
				}
				int total = -score + scoreCountry(n);
				if (total < lowestScore) {
					sender = c;
					receiver = n;
					lowestScore = total;
				}
			}
		}
		if (receiver != null) {
			int toSend = sender.getArmies() - getMinPlacement();
			if (v.contains(sender)) {
				toSend = -additionalTroopsNeeded(sender, gs)/2 - getMinPlacement();
			}
			return getMoveCommand(sender, receiver, toSend);
		}
		//move from the interior (not very smart)
		if (max != null && max.getArmies() > getMinPlacement() + 1) {
			int least = Integer.MAX_VALUE;
			for (int j = 0; j < max.getNeighbours().size(); j++) {
				Country n = (Country)max.getNeighbours().get(j);
				if (max.getOwner() != player) {
					continue;
				}
				if (n.getArmies() < least) {
					receiver = n;
					least = n.getArmies();
				}
			}
			if (receiver != null) {
				return getMoveCommand(max, receiver,  (max.getArmies() - getMinPlacement() - 1));
			}
		}
		return "nomove";
	}

	private String getMoveCommand(Country sender, Country receiver, int toSend) {
		return "movearmies " + sender.getColor() + " "
				+ receiver.getColor() + " " + toSend;
	}

	public String getAttack() {
		eliminating = false;
		breaking = null;
		return plan(true);
	}

	/**
	 * Will roll the maximum, but checks to see if the attack is still the
	 * best plan every 3rd roll
	 */
    public String getRoll() {
		int n=game.getAttacker().getArmies() - 1;
		int m=game.getDefender().getArmies();

		if (n < 3 && game.getBattleRounds() > 0 && (n < m || (n == m && game.getDefender().getOwner().getTerritoriesOwned().size() != 1))) {
			return "retreat";
		}

		//spot check the plan
    	if (type != AIDomination.PLAYER_AI_EASY && (game.getBattleRounds()%3 == 2 || (game.getBattleRounds() > 0 && (n - Math.min(m, game.getMaxDefendDice()) <= 0)))) {
    		String result = plan(true);
    		//TODO: rewrite to not use string parsing
    		if (result.equals("endattack")) {
    			return "retreat";
    		}
    		StringTokenizer st = new StringTokenizer(result);
    		st.nextToken();
    		if (game.getAttacker().getColor() != Integer.parseInt(st.nextToken())
    				|| game.getDefender().getColor() != Integer.parseInt(st.nextToken())) {
    			return "retreat";
    		}
    	}
		return "roll " + Math.min(3, n);
    }

    /**
     * Get a quick overview of the game state - capitals, player ordering, if there is a common threat, etc.
     * @param p
     * @param excludeCards
     * @return
     */
    public GameState getGameState(Player p, boolean excludeCards) {
    	List<Player> players = game.getPlayers();
    	GameState g = new GameState();
    	Continent[] c = game.getContinents();
    	if (player.getCapital() == null) {
    		g.capitals = Collections.EMPTY_SET;
    	} else {
    		g.capitals = new HashSet<Country>();
    	}
    	g.owned = new Player[c.length];
    	for (int i = 0; i < c.length; i++) {
			g.owned[i] = c[i].getOwner();
		}
    	int index = -1;
    	int playerCount = 1;
    	//find the set of capitals
    	for (int i = 0; i < players.size(); i++) {
    		Player player2 = players.get(i);
    		if (player2.getCapital() != null) {
    			g.capitals.add(player2.getCapital());
    		}
    		if (player2.getTerritoriesOwned().isEmpty()) {
    			continue;
    		}
    		if (player2 == p) {
    			index = i;
    		} else {
    			playerCount++;
    		}
    	}
    	g.orderedPlayers = new ArrayList<PlayerState>(playerCount);
    	int attackOrder = 0;
    	int strategicCount = 0;
    	for (int i = 0; i < players.size(); i++) {
    		Player player2 = players.get((index + i)%players.size());
    		if (player2.getTerritoriesOwned().isEmpty()) {
    			continue;
    		}
    		//estimate the trade-in
    		int cards = player2.getCards().size() + 1;
    		int cardEstimate = (i==0&&excludeCards)?0:getCardEstimate(cards);
			PlayerState ps = new PlayerState();
			List<Country> t = player2.getTerritoriesOwned();
			int noArmies = 0;
			int attackable = 0;
			boolean strategic = isStrategic(player2);
			if (strategic) {
				strategicCount++;
			}
			//determine what is available to attack with, discounting if land locked
			for (int j = 0; j < t.size(); j++) {
				Country country = t.get(j);
				noArmies += country.getArmies();
				int available = country.getArmies() - 1;
				if (ownsNeighbours(player2, country)) {
					available = country.getArmies()/2;
				}
				//quick multipliers to prevent turtling/concentration
				if (available > 4) {
					if (available > 8 && strategic) {
						if (available > 13) {
							available *= 1.3;
						}
						available += 2;
					}
					available += 1;
				}
				attackable += available;
			}
			int reenforcements = Math.max(3, player2.getNoTerritoriesOwned()/3) + cardEstimate;
			if (reenforcements > 8 && strategic) {
				reenforcements *= 1.3;
			}
			int attack = attackable + reenforcements;
			HashSet<Continent> owned = new HashSet<Continent>();
			//update the attack and player value for the continents owned
			for (int j = 0; j < g.owned.length; j++) {
				if (g.owned[j] == player2) {
					attack += c[j].getArmyValue();
					if (strategic) {
						ps.playerValue += 3*c[j].getArmyValue();
					} else {
						ps.playerValue += 1.5 * c[j].getArmyValue() + 1;
					}
					owned.add(c[j]);
				}
			}
			ps.strategic = strategic;
			ps.armies = noArmies;
			ps.owned = owned;
			ps.attackValue = attack;
			ps.attackOrder = attackOrder;
			//use a small multiplier for the defensive value
			ps.defenseValue = 5*noArmies/4 + noArmies%4 + player2.getNoTerritoriesOwned();
			ps.p = player2;
			if (i == 0) {
				g.me = ps;
			} else {
				g.orderedPlayers.add(ps);
			}
			ps.playerValue += ps.attackValue + ((game.getMaxDefendDice() == 2 && !isIncreasingSet())?1:game.getMaxDefendDice()>2?3:2)*ps.defenseValue;
			attackOrder++;
    	}
    	//put the players in order of strongest to weakest
    	Collections.sort(g.orderedPlayers, Collections.reverseOrder());
    	//check to see if there is a common threat
    	//the logic will allow the ai to team up against the strongest player
    	//TODO: similar logic could be expanded to understand alliances/treaties
    	if (game.getSetupDone() && !g.orderedPlayers.isEmpty()) {
    		//base top player multiplier
    		double multiplier = game.getCards().isEmpty()?(game.isRecycleCards()?1.2:1.1):(player.getMission()!=null||player.getCapital()!=null)?1.1:1.3;
    		PlayerState topPlayer = g.orderedPlayers.get(0);
    		if (type == AIDomination.PLAYER_AI_EASY) {
				multiplier *= 1.6; //typically this waits too long in the end game
    		} else if (type == AIDomination.PLAYER_AI_HARD && player.getStatistics().size() > 3) {
    			if (!isIncreasingSet()) {
    				//we can be more lenient with more players
    				multiplier = Math.max(1, multiplier - .4 + g.orderedPlayers.size()*.1);
    			} else if (game.getCardMode() != RiskGame.CARD_ITALIANLIKE_SET) {
    				//don't want to pursue the lowest player if there's a good chance someone else will eliminate
    				multiplier *= 1.5;
    			}
    		} else if (type == AIDomination.PLAYER_AI_AVERAGE) {
    			multiplier *= 1.2;
    		}
			g.targetPlayers.add(topPlayer.p);
			//look to see if you and the next highest player are at the multiplier below the highest
    		if (g.orderedPlayers.size() > 1 && topPlayer.playerValue > multiplier * g.me.playerValue) {
    			g.breakOnlyTargets = game.getMaxDefendDice() == 2;
    			PlayerState ps = g.orderedPlayers.get(1);
    			if (topPlayer.playerValue > multiplier * ps.playerValue) {
        			g.commonThreat = topPlayer;
    			} else {
    				//each of the top players is a target
    				g.targetPlayers.add(ps.p);
    			}
    		} else if (type == AIDomination.PLAYER_AI_HARD && isIncreasingSet() && g.orderedPlayers.get(g.orderedPlayers.size()-1).defenseValue/topPlayer.attackValue > .3) {
    			//play for the elimination
    			g.targetPlayers.clear();
    			g.targetPlayers.add(g.orderedPlayers.get(g.orderedPlayers.size()-1).p);
    		}
    	}
    	return g;
    }

	private int getCardEstimate(int cards) {
		int tradeIn = game.getCardMode() != RiskGame.CARD_INCREASING_SET?8:game.getNewCardState();
		int cardEstimate = cards < 3?0:(int)((cards-2)/3.0*tradeIn);
		return cardEstimate;
	}

    /**
     * Provides a quick measure of how the player has performed
     * over the last several turns
     */
	private boolean isStrategic(Player player2) {
		if (player2 == this.player) {
			return false;
		}
		List<Statistic> stats = player2.getStatistics();
		if (stats.size() < 4) {
			return false;
		}
		//look over the last 4 turns
		int end = 4;
		int reenforcements = 0;
		int kills = 0;
		int casualities = 0;
		for (int i = stats.size() - 1; i >= end; i--) {
			Statistic s = stats.get(i);
			reenforcements += s.get(StatType.REINFORCEMENTS);
			kills += s.get(StatType.KILLS);
			casualities += s.get(StatType.CASUALTIES);
			if (s.get(StatType.CONTINENTS) == 0) {
				return false;
			}
		}
		return reenforcements + kills/((player2.getCards().size() > 2)?1:2) > 2*casualities;
	}

	/**
     * Delay trading in cards when sensible
     * TODO: this should be more strategic, such as looking ahead for elimination
     */
    public String getTrade() {
    	if (!game.getTradeCap() && type != AIDomination.PLAYER_AI_EASY) {
    		if (game.getCardMode() != RiskGame.CARD_ITALIANLIKE_SET && player.getCards().size() >= RiskGame.MAX_CARDS) {
    			return super.getTrade();
    		}
    		GameState gs = getGameState(player, true);
    		if (gs.commonThreat == null && gs.orderedPlayers.size() > 1 && !pressAttack(gs) && !isTooWeak(gs)) {
    			return "endtrade";
    		}
    	}
    	return super.getTrade();
    }

    /**
     * Finds all countries that can be attacked from.
     * @param p player object
     * @param attack true if this is durning attack, which requires the territority to have 2 or more armies
     * @return a Vector of countries, never null
     */
    public List<Country> findAttackableTerritories(Player p, boolean attack) {
    	List<Country> countries = p.getTerritoriesOwned();
    	List<Country> result = new ArrayList<Country>();
    	for (int i=0; i<countries.size(); i++) {
    		Country country = countries.get(i);
    		if ((!attack || country.getArmies() > 1) && !ownsNeighbours(p, country)) {
				result.add(country);
    		}
    	}
    	return result;
    }

    /**
     * Checks whether a country owns its neighbours
     * @param p player object, c Country object
     * @return boolean True if the country owns its neighbours, else returns false
     */
    public boolean ownsNeighbours(Player p, Country c) {
        List<Country> neighbours = c.getNeighbours();

        for (int i=0; i<neighbours.size(); i++) {
           if ( neighbours.get(i).getOwner() != p) {
        	   return false;
           }
        }

        return true;
    }

    @Override
    protected String getTrade(Card[] result) {
    	if (type != PLAYER_AI_EASY) {
    		boolean[] owns = new boolean[result.length];
    		int ownsCount = 0;
    		for (int i = 0; i < result.length; i++) {
    			if (result[i].getCountry() != null && player.getTerritoriesOwned().contains(result[i].getCountry())) {
    				owns[i] = true;
    				ownsCount++;
    			}
    		}
			//swap for a single owned country - TODO: be smarter about which territory to retain
    		if (ownsCount != 1 && player.getCards().size() > 3) {
    			List<Card> toTrade = Arrays.asList(result);
        		for (Card card : (List<Card>)player.getCards()) {
        			if (toTrade.contains(card)) {
        				continue;
        			}
        			if (ownsCount > 1) {
	        			if (card.getCountry() == null || !player.getTerritoriesOwned().contains(card.getCountry())) {
	        				for (int i = 0; i < result.length; i++) {
	        					if (result[i].getName().equals(card.getName())) {
	        						result[i] = card;
	        						if (--ownsCount == 1) {
	        							return super.getTrade(result);
	        						}
	        						break;
	        					}
	        				}
	        			}
        			} else {
        				if (card.getCountry() != null && player.getTerritoriesOwned().contains(card.getCountry())) {
        					for (int i = 0; i < result.length; i++) {
	        					if (result[i].getName().equals(card.getName())) {
	        						result[i] = card;
	        						return super.getTrade(result);
	        					}
	        				}
	        			}
        			}
        		}
    		}
    	}
    	return super.getTrade(result);
    }

}

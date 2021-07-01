// Yura Mamyrin

package net.yura.domination.engine.core;

import java.io.IOException;
import java.io.Serializable;

/**
 * Statistics for a single move.
 * @author Yura Mamyrin
 */
public class Statistic implements Serializable {

    private static final long serialVersionUID = 1L;

    private int[] statistics;

    private double dice;
    private int diceCount; // Computing the average of dices requires knowing their count.

    public Statistic() {
	statistics = new int[13];
    }

    // at the end of a persons go this gets called
    public void endGoStatistics(int countries, int armies, int continents, int conectedEmpire, int cards) {

	statistics[StatType.COUNTRIES.ordinal()] = countries;
	statistics[StatType.ARMIES.ordinal()] = armies;
	statistics[StatType.CONTINENTS.ordinal()] = continents;
	statistics[StatType.CONNECTED_EMPIRE.ordinal()] = conectedEmpire;
	statistics[StatType.CARDS.ordinal()] = cards;
/*
	System.out.print("\nStatistic for the last go:\n");
	System.out.print("countries "+statistics[0]+"\n");
	System.out.print("armies "+statistics[1]+"\n");
	System.out.print("kills "+statistics[2]+"\n");
	System.out.print("casualties "+statistics[3]+"\n");
	System.out.print("reinforcements "+statistics[4]+"\n");
	System.out.print("continents "+statistics[5]+"\n");
	System.out.print("conectedEmpire "+statistics[6]+"\n");
	System.out.print("attacks "+statistics[7]+"\n");

	System.out.print("retreats "+statistics[8]+"\n");
	System.out.print("countriesWon "+statistics[9]+"\n");
	System.out.print("countriesLost "+statistics[10]+"\n");
	System.out.print("attacked "+statistics[11]+"\n");
*/
    }

    public void addReinforcements(final int a) {
	statistics[StatType.REINFORCEMENTS.ordinal()] += a;
    }

    public void addKill() {
	statistics[StatType.KILLS.ordinal()]++;
    }

    public void addCasualty() {
	statistics[StatType.CASUALTIES.ordinal()]++;
    }

    public void addAttack() {
	statistics[StatType.ATTACKS.ordinal()]++;
    }

    public void addAttacked() {
	statistics[StatType.ATTACKED.ordinal()]++;
    }

    public void addRetreat() {
	statistics[StatType.RETREATS.ordinal()]++;
    }

    public void addCountriesWon() {
	statistics[StatType.COUNTRIES_WON.ordinal()]++;
    }

    public void addCountriesLost() {
	statistics[StatType.COUNTRIES_LOST.ordinal()]++;
    }

    /**
     * Record a dice value.
     * @param diceValue the internal value of a dice (from 0 to 5)
     */
    public void addDice(int diceValue) {
        // +1 because of the internal dice values going from 0 to 5
        dice = (dice * diceCount + diceValue +1) / (diceCount+1);
        diceCount++;
    }

    public double get(StatType statType) {
        if (statType==StatType.DICE) {
            return dice;
        }
	return statistics[ statType.ordinal() ];
    }

    // we may have loaded a old game where statistics.length is 12
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (statistics.length < 13) {
            int[] old = statistics;
            statistics = new int[13];
            System.arraycopy(old, 0, statistics, 0, old.length);
        }
    }

}

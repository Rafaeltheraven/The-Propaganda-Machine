// Yura Mamyrin, Group D

package net.yura.domination.engine.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import net.yura.domination.engine.RiskObjectOutputStream;

/**
 * <p> Country </p>
 * @author Yura Mamyrin
 */
public class Country implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Vector neighbours;
	private Player owner;
	private int armies;
	private Continent continent;
	private int color;
	private int x;
	private int y;
	private String idString; // used by the map editor

	private transient List<Country> crossContinentNeighbours;
	private transient List<Country> incomingNeighbours = new ArrayList<Country>(2);

	Country() {
		neighbours = new Vector();
	}
	
	/**
	 * Creates a country object
	 * @param n the name of the country
	 * @param c the name of the continent the country belongs to
	 */
	public Country (int p, String id, String n, Continent c, int a, int b) {
                this();
		idString        = id;
		color		=p;
		name		=n;
		continent	=c;
		owner		=null;
		armies		=0;
		x		=a;
		y		=b;
	}

	public String getIdString() {
		return idString;
	}

	public void setIdString(String a) {
		idString = a;
	}

	public String toString() { // used in the editor
		return idString+" ("+color+")";
	}

	/**
	 * Checks for country adjacencies
	 * @param t the name of the country
	 * @return returns true if the two countries are adjacent, false if the two countries are no adjacent
	 */
	public boolean isNeighbours(Country t) {

		//for (int c=0; c< neighbours.size() ; c++) {
		//
		//	if (neighbours.elementAt(c)==t) {
		//		return true;
		//	}
		//}
		//return false;

		return neighbours.contains(t);

	}

	/**
	 * gets the countries neighbours
	 * @return a vector of the countries neighbours
	 */
	public Vector getNeighbours() {
		return neighbours;
	}
	
	public List<Country> getCrossContinentNeighbours() {
		if (crossContinentNeighbours == null) {
			ArrayList<Country> c = new ArrayList<Country>(2);
			for (int i = 0; i < this.neighbours.size(); i++) {
				Country other = (Country)this.neighbours.get(i);
				if (other.getContinent() != this.continent) {
					c.add(other);
				}
			}
			this.crossContinentNeighbours = c;
		}
		return crossContinentNeighbours;
	}

	/**
	 * Returns the country name
	 * @return name
	 */
	public String getName() {
		return name;
	}

	public void setName(String a) {
		name = a;
	}

	/**
	 * Returns the armies stationed within the country
	 * @return armies
	 */
	public int getArmies() {
		return armies;
	}

	/**
	 * Returns the Continent of the country
	 * @return continent
	 */
	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent a) {
		continent = a;
	}

	/**
	 * Adds the country to the vector of adjacencies
	 * @param t
	 */
	public void addNeighbour(Country t) {
		this.crossContinentNeighbours = null;
		neighbours.add(t);
		t.getIncomingNeighbours().add(this);
	}
	
	public List<Country> getIncomingNeighbours() {
		return incomingNeighbours;
	}

	/**
	 * Assigns the owner to a country
	 * @param p a player object
	 */
	public void setOwner(Player p) {
		owner=p;
	}

	/**
	 * Gets and returns the owner of the country
	 * @return owner
	 */
	public Player getOwner() {
		return owner;
	}

	/**
	 * Adds an army to the country
	 */
	public void addArmy() {
		armies++;
	}

	/**
	 * Adds several armies using a parameter
	 * @param n the number of armies
	 */
	public void addArmies(int n) {
		armies = armies + n;
	}

	/**
	 * Removes defeated armies from a country
	 * @param lessArmies
	 */
	public void removeArmies(int lessArmies) {
		armies = armies - lessArmies;
	}

	/**
	 * Removes a single defeated army from a country
	 */
	public void looseArmy() {
		armies--;
	}

	/**
	 * Returns the colour (unique) of the country
         * starting from 1, NOT FROM ZERO
	 * @return color
	 */
	public int getColor() {
		return color;
	}

	public void setColor(int a) {
		color = a;
	}

	/**
	 * Returns the x co-ordinate of the noa circle in country
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y co-ordinate of the noa circle in country
	 * @return y
	 */
	public int getY() {
		return y;
	}

	public void setX(int a) {
		x=a;
	}
	public void setY(int a) {
		y=a;
	}

        /**
         * @see java.awt.geom.Point2D#distance(double, double, double, double) Point2D.distance
         */
        public int getDistanceTo(int px, int py) {
            px -= getX();
            py -= getY();
            return (int)Math.sqrt(px * px + py * py);
        }

	private void writeObject(ObjectOutputStream out) throws IOException {
		if (out instanceof RiskObjectOutputStream) {
			ObjectOutputStream.PutField putField = out.putFields();
			putField.put("owner", owner);
			putField.put("armies", armies);
			out.writeFields();
		}
                else {
                    out.defaultWriteObject();
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.incomingNeighbours = new ArrayList<Country>(2);
		this.neighbours = new Vector();
	}

}

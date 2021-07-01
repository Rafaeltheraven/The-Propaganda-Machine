// Yura Mamyrin

package net.yura.domination.engine.ai;

import java.util.Vector;
import net.yura.domination.engine.core.Country;
import net.yura.domination.engine.core.Player;

/**
 * AI that attacks as much as it can all the time
 * @author Yura Mamyrin
 */
public class AITest extends AISubmissive {

    public int getType() {
        return 5;
    }

    public String getCommand() {
        return "test";
    }

    protected class Attack {
	public final Country source;
	public final Country destination;

	public Attack(Country s, Country d){
	    source=s;
	    destination=d;
	}
	public String toString(){
	    if (source == null || destination == null) { return ""; }
	    return "attack " + source.getColor() + " " + destination.getColor();
	}

    }

    public String getPlaceArmies() {

		if ( game.NoEmptyCountries()==false ) {
		    return "autoplace";
		}
		else {
		    Vector t = player.getTerritoriesOwned();
		    Vector n;
		    String name=null;
			name = findAttackableTerritory(player);
			if ( name == null ) {
			return "placearmies " + ((Country)t.elementAt(0)).getColor() +" "+player.getExtraArmies()  ;
		    }

		    if (game.getSetupDone() ) {
			return "placearmies " + name +" "+player.getExtraArmies() ;
		    }

		    return "placearmies " + name +" 1";

		}

    }

    public String getAttack() {
	//Vector t = player.getTerritoriesOwned();
	Vector outputs = new Vector();
	Attack move;

	/*  // Extract method: findAttackableNeighbors()
	Vector n;
	for (int a=0; a< t.size() ; a++) {
	    if ( ((Country)t.elementAt(a)).getArmies() > 1 ) {
		n = ((Country)t.elementAt(a)).getNeighbours();
		for (int b=0; b< n.size() ; b++) {
		    if ( ((Country)n.elementAt(b)).getOwner() != player ) {
			outputs.add( "attack " + ((Country)t.elementAt(a)).getColor() + " " + ((Country)n.elementAt(b)).getColor() );
		    }
		}
	    }
	}  */
	outputs = findAttackableNeighbors(player.getTerritoriesOwned(),0);
	if (outputs.size() > 0) {
		move = (Attack) outputs.elementAt( (int)Math.round(Math.random() * (outputs.size()-1) ) );
		//System.out.println(player.getName() + ": "+ move.toString());    //TESTING
		return move.toString();
		//return (String)outputs.elementAt( (int)Math.round(Math.random() * (outputs.size()-1) ) );
	}
	return "endattack";
    }



    public String getRoll() {
	    int n=((Country)game.getAttacker()).getArmies() - 1;
	    if (n > 3) {
		    return "roll "+3;
	    }
	    return "roll "+n;
    }


    /******************
     * Helper Methods *
     ******************/

    /**
     * (moved here from AICrap/AISubmissive)
     * Attempts to find the first territory that can be used to attack from
     * @param p player object
     * @return Sring name is a move to attack from any space they can (that has less than 500 armies)
     * else returns null
     */
    public String findAttackableTerritory(Player p) {
    	Vector countries = p.getTerritoriesOwned();

    	for (int i=0; i<countries.size(); i++) {
    		Vector neighbors = ((Country)countries.elementAt(i)).getNeighbours();
    		for (int j=0; j<neighbors.size(); j++) {
    			if (((Country)neighbors.elementAt(j)).getOwner() != p) {
    				if ((p.getCapital() != null && ((Country)countries.elementAt(i)).getColor() != p.getCapital().getColor()) || p.getCapital() == null)
    					return ((Country)countries.elementAt(i)).getColor()+"";
    			}
    		}
    	}

    	return null;
    }


    /************
     * @name findAttackableNeighbors
     * @param t Vector of teritories
     * @param ratio - threshold of attack to defence armies to filter out
     * @return a Vector of possible attacks for a given list of territories
     * 	where the ratio of source/target armies is above ratio
     **************/
    public Vector findAttackableNeighbors(Vector t, double ratio){
	Vector output = new Vector();
	Vector n=new Vector();
    	Country source,target;
	if (ratio<0) { ratio = 0;}
	for (int a=0; a< t.size() ; a++) {
	    source=(Country)t.elementAt(a);
	    if ( source.getOwner() == player && source.getArmies() > 1 ) {
		n = source.getNeighbours();
		for (int b=0; b< n.size() ; b++) {
		    target=(Country)n.elementAt(b);
		    if ( target.getOwner() != player &&
			( (double)(source.getArmies()/target.getArmies()) > ratio)
		      	) {     // simplify logic
			//output.add( "attack " + source.getColor() + " " + target.getColor() );
			output.add(new Attack(source,target));
		    }
		}
	    }
	}
	return output;
    }

}

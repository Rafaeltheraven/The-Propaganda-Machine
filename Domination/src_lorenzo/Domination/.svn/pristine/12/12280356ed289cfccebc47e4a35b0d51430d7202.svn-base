package net.yura.domination.engine.ai;

import junit.framework.TestCase;
import net.yura.domination.engine.Risk;
import net.yura.domination.engine.RiskAdapter;
import net.yura.domination.guishared.RiskUIUtil;
import net.yura.domination.engine.ai.AIManager;
import net.yura.domination.engine.ai.logic.AIDomination;
import net.yura.domination.engine.core.Player;
import net.yura.domination.engine.core.RiskGame;

public class AISimulationTest extends TestCase{
	
	private static boolean debug = false;
	int state = 0;
	
	int hard;
	int easy;
	int avg;
	int other;
	
	public void testSomething() throws InterruptedException {
		RiskUIUtil.parseArgs(new String[] {});
		final Risk risk = new Risk();
		risk.addRiskListener(new RiskAdapter() {

		    public void sendMessage(String output, boolean redrawNeeded, boolean repaintNeeded) {
		    	if (debug) {
		    		System.out.print(output+"\n");
		    	}
		    }

		    public void needInput(int s) {
		    	synchronized (risk) {
		    		risk.notifyAll();
				}
		    }

		    public void noInput() {

		    }

		} );
		AIManager.setWait(0);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 300; i++) {
			playGame(risk);
		}
		System.out.println(easy + " " + avg + " " + hard + " " + other);
		if (debug) {
			System.out.println(System.currentTimeMillis()-start);
		}
		assertTrue(easy <= avg);
		assertTrue(avg <= hard);
		risk.kill();
		risk.join();
	}

	private void playGame(Risk risk) throws InterruptedException {
		risk.parser("closegame");
		synchronized (risk) {
			while (risk.getGame() != null) {
				risk.wait();
			}
		}
		risk.parser("newgame");
		
		risk.parser("newplayer ai easy 6 6");
		risk.parser("newplayer ai average 2 2");
		risk.parser("newplayer ai hard 1 1");
		risk.parser("newplayer ai easy 3 3");
		risk.parser("newplayer ai average 4 4");
		risk.parser("newplayer ai hard 5 5");
		
		risk.parser("startgame domination fixed recycle");
		
		synchronized (risk) {
			while (risk.getGame() == null || risk.getGame().getState() != RiskGame.STATE_GAME_OVER) {
				risk.wait();
			}
			Player p = risk.getGame().getCurrentPlayer();
			if (p.getType() == AIDomination.PLAYER_AI_AVERAGE) {
				avg++;
			} else if (p.getType() == AIDomination.PLAYER_AI_EASY) {
				easy++;
			} else if (p.getType() == AIDomination.PLAYER_AI_HARD) {
				hard++;
			} else {
				other++;
			}
			if (debug) {
				System.out.println(p);
			}
		}
	}

}

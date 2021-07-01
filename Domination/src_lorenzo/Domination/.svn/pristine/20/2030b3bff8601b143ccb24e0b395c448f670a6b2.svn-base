package net.yura.domination.lobby.server;

import net.yura.domination.engine.Risk;
import net.yura.domination.engine.core.RiskGame;
import net.yura.lobby.server.LobbyLogger;

public class ServerRisk extends Risk implements LobbyLogger.LobbyGameThread {

	ServerGameRisk sgr;
	private boolean paused;
	private boolean killflag;
	private boolean waiting;

	public ServerRisk(ServerGameRisk a) {
		super();
		sgr = a;
	}

        public int getGameId() {
            return sgr.getId();
        }

	public void makeNewGame() {
		try {
			game = new RiskGame();
		}
		catch (Exception ex) {
			throw new RuntimeException("unable to make game!",ex);
		}
		paused = true;
		// a new game, clear anything from the inbox
		inbox.clear();

	}

	public synchronized void setPaued(boolean a) {
		paused = a;
		if (!a) {
			notify();
		}
	}

	public synchronized void addSetupCommandToInbox(String a) {
		addSetupCommandToInbox(myAddress,a);
	}

	public synchronized void addSetupCommandToInbox(String ad, String a) {
		inbox.add(ad+" "+a);
		waiting = false;
		notify();
	}

	public synchronized void addPlayerCommandToInbox(String a, String b) {
		inbox.add(a+" "+b);
		notify();
	}

	public synchronized void setKillFlag() {
		killflag = true;
                paused = false;
		notify();
	}

	public boolean getWaiting() {
		return waiting;
	}

	@Override
	// must catch all messages from the ais (and humans too now)
	public void parser(String m) { //  synchronized
		//System.out.print("\tGOT: "+m+"\n");
		// address must match for ai to know when to take its turn
		// game.getCurrentPlayer().getAddress()
		//inbox.add( myAddress +" "+m);
		//this.notify();
            
            // over 20,000 commands, the game must be stuck in a loop, kill the game
            if (getGame().getCommands().size() > 200000) {
                sgr.gameFinished("Nobody");
            }
            else {
		addPlayerCommandToInbox(myAddress,m);
            }
	}

	@Override
	// pass things from the inbot to the GameParser
	public void run() {
		String message;

                loop: while (!killflag) {

			synchronized(this) {

				// dont go on if this is in catch all and dont run mode!!!!!
				while ( inbox.isEmpty() || (paused && game.getState()!=RiskGame.STATE_NEW_GAME ) ) {

                                        if (killflag) break loop;

                                        waiting=true;
					try { this.wait(); }
					catch(InterruptedException e){}
				}

				message = (String)inbox.remove(0);
			}

			inGameParser(message);
		}

		System.out.println("THREAD DIE: "+getName());
	}

	@Override
	// catch all things and send it to the clients
	// game kicks off and messages are sent to here
	public void inGameParser(String mem) {
		//if not all players hit start and the game is started, just store the commands, do not run them
		// stick risk into paused mode
		if ( paused && game.getState()!=RiskGame.STATE_NEW_GAME ) {
			inbox.add(mem);
			return;
		}

		if (paused) {
			//System.out.println("\tRISKSETUP "+mem);
		}
		else {
			//System.out.println("\tRISKSEND "+mem);
			// send out to all clients
			sgr.sendStringToAllClient(mem);
		}

		super.inGameParser(mem);
	}

	@Override
	public void getInput() {
		super.getInput();
		if (!paused) { sgr.getInputFromSomeone(); }
	}

	@Override
	public String whoWon() {
		sgr.gameFinished( game.getCurrentPlayer().getName() );
		return super.whoWon();
	}

        String getAddress() {
            return myAddress;
        }
}

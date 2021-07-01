package net.yura.domination.engine;

/**
 * @author Yura Mamyrin
 */
public interface OnlineRisk {

    public void sendUserCommand(String mtemp);
    public void sendGameCommand(String mtemp);

    public void closeGame();

    public void playerRenamed(String oldName, String newName, String newAddress, int newType);

}

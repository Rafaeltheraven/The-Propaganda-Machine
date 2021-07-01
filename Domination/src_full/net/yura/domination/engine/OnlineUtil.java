package net.yura.domination.engine;

import net.yura.domination.engine.core.RiskGame;

public class OnlineUtil {
    
        public static String getDefaultOnlineGameName(String username) {
            return "Cadet Game";
        }
        
        /**
         * option string looks like this:
         * 
         *   0
         *   2
         *   2
         *   choosemap luca.map
         *   startgame domination increasing
         */
        public static String createGameString(int easyAI, int averageAI, int hardAI, int gameMode, int cardsMode, boolean AutoPlaceAll, boolean recycle, String mapFile) {

            String players = averageAI + "\n" + easyAI + "\n" + hardAI + "\n";

            String type="";

            switch(gameMode) {
                case RiskGame.MODE_DOMINATION: type = "domination"; break;
                case RiskGame.MODE_CAPITAL: type = "capital"; break;
                case RiskGame.MODE_SECRET_MISSION: type = "mission"; break;
            }
            
            switch(cardsMode) {
                case RiskGame.CARD_INCREASING_SET: type += " increasing"; break;
                case RiskGame.CARD_FIXED_SET: type += " fixed"; break;
                case RiskGame.CARD_ITALIANLIKE_SET: type += " italianlike"; break;
            }

            if ( AutoPlaceAll ) type += " autoplaceall";
            if ( recycle ) type += " recycle";
            
            return players+ "choosemap "+mapFile +"\nstartgame " + type;
        }

        public static String getMapNameFromLobbyStartGameOption(String options) {
            String[] lines = options.split( RiskUtil.quote("\n") );
            String choosemap = lines[3];
            return choosemap.substring( "choosemap ".length() ).intern();
        }

        /**
         * @see #createGameString(int, int, int, int, int, boolean, boolean, java.lang.String)
         * @see net.yura.domination.lobby.server.ServerGameRisk#startGame(java.lang.String, java.lang.String[])
         */
        public static String getGameDescriptionFromLobbyStartGameOption(String options) {
            String[] lines = options.split( RiskUtil.quote("\n") );
            int aiTotal=0;
            for (int c=0;c<3;c++) {
                aiTotal = aiTotal + Integer.parseInt(lines[c]);
            }
            String aiInfo;
            if (aiTotal == 0) {
                aiInfo = "0";
            }
            else {
                // easy,average,hard for historic reasons, they are stored as 'average \n easy \n hard'
                aiInfo = lines[1]+","+lines[0]+","+lines[2];
            }
            return "AI:"+aiInfo+" "+lines[4].substring( "startgame ".length() );
        }
}

package Server;

import org.json.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

public class GameManager extends Thread {
    private static final Integer MAX_ROUND = 3;

    private static final Integer DURATION = 5;
    
    private static final Integer READY_TIME = 2;

    private static final Integer MAX_FAILED_ANSWER = 3;

    private Logger logger = null;

    private GameRoom gameRoom = null;

    public GameManager(Logger logger, GameRoom gameRoom) {
        this.logger = logger;
        this.gameRoom = gameRoom;
    }

    @Override
    public void run() {
    	while(true) {
    		// Whenever game start -> continue even after that there is a player exiting
    		// Only stop when no one in room
    		// if no one in room -> reset the game
        	if(this.gameRoom.isFull == true) {
        		this.logger.info("In game phase");
        		// I. Send signal to START GAME for all players
        		JSONObject startGameJson = new JSONObject();
        		startGameJson.put("event", "CLIENT_GAME_START");
        		startGameJson.put("readyTime", READY_TIME);

        		for(Player registeredPlayer: this.gameRoom.getRegisteredPlayers()) {
        			registeredPlayer.write(startGameJson.toString());
        		}
        		
        		try {
					Thread.sleep(READY_TIME*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		
        		for(int ROUND = 1; ROUND <= MAX_ROUND; ++ROUND) {
            		// 1. SEND ROUND INFO
        			JSONObject startRoundJson = new JSONObject();
        			this.gameRoom.gameExpression = new GameExpression();
//        			this.logger.info(this.gameRoom.gameExpression.convertToString());
        			
        			startRoundJson.put("event", "CLIENT_ROUND_START");
        			startRoundJson.put("expression", this.gameRoom.gameExpression.convertToString());
//        			startRoundJson.put("ranking", )
        			
        			// ..... duration, ranking
        			
        			for(Player readyPlayer : this.gameRoom.getReadyPlayers()) {
        				readyPlayer.write(startRoundJson.toString());
        			}
        			
        			// 2. DURATION
        			try {
						Thread.sleep(DURATION*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			
        			// 3. VALIDATE
        			int cntIncorrect = 0;
        			SelectionKey earliestKey = null;
        			for(Player readyPlayer : this.gameRoom.getReadyPlayers()) {
        				JSONObject answerJson = new JSONObject();
         				if(readyPlayer.answer != null && readyPlayer.answer == this.gameRoom.gameExpression.expectedResult) {
        					++readyPlayer.point;
         					answerJson.put("status", true);
         					
         					if(earliestKey == null) {
         						earliestKey = readyPlayer.key;
         					} else {
         				        Instant earliest = this.gameRoom.hashmapPlayers.get(earliestKey).timestamp;
         				        Instant instant = readyPlayer.timestamp;

         				        int result = instant.compareTo(earliest);

         				        if (result < 0) {
         				            earliestKey = readyPlayer.key;
         				        }
         					}
        				}else {
        					++cntIncorrect;
        					--readyPlayer.point;
        					++readyPlayer.consecutiveFailedAnswer;
        					if(readyPlayer.consecutiveFailedAnswer == MAX_FAILED_ANSWER) {
        						readyPlayer.isEliminated = true;
        					}
        					answerJson.put("status", false);
        				}
         				readyPlayer.write(answerJson.toString());
        			}
        			
        			if(earliestKey != null) {
        				this.gameRoom.hashmapPlayers.get(earliestKey).point += cntIncorrect;
        			}
        			
        			this.handleResetRound();
        		}
        	}
    	}
    }
    
    public void handleResetRound() {
    	for(Player readyPlayer : this.gameRoom.getReadyPlayers()) {
    		readyPlayer.answer = null;
    		readyPlayer.timestamp = null;
    	}
    }
}

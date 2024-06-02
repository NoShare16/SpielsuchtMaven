package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.spielsucht.Player;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import config.Config;

public class GamePollingService {
	
	private static final ObjectId PLAYER_1_ID = new ObjectId("66560a546ab1d7f2d5fbc326");
    private static final ObjectId PLAYER_2_ID = new ObjectId("66560a686ab1d7f2d5fbc327");
    private static final ObjectId PLAYER_3_ID = new ObjectId("66560a6c6ab1d7f2d5fbc328");
    private static final ObjectId PLAYER_4_ID = new ObjectId("66560a6e6ab1d7f2d5fbc329");
	
    private MongoDatabase database;
    private MongoClient client;
    
    
    public GamePollingService() {
        client = MongoClients.create(Config.MONGO_CONNECTION_STRING);
        this.database = client.getDatabase("Roulette");
    }
    
    public List<Player> getCurrentPlayerBalances() {
        MongoCollection<Document> players = database.getCollection("players");
        List<Player> playerList = new ArrayList<>();

        for (Document doc : players.find()) {
            boolean readyState = doc.getBoolean("readyState", false); // Fallback to false if not found
            double balance = doc.getDouble("balance"); // Assuming balance is stored as a double
            ObjectId PlayerId = doc.getObjectId("_id");

            playerList.add(new Player(PlayerId, balance, readyState)); // Assuming the Player class can take these parameters
        }

        return playerList;
    }
    
    public void startPolling() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            System.out.println("Checking current balances...");
            List<Player> players = getCurrentPlayerBalances();
            players.forEach(System.out::println);
        };
        scheduler.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS);
    }
    
    public static void main(String[] args) {
        GamePollingService service = new GamePollingService();
        service.startPolling();
    }
    
    public boolean checkAllPlayersReady() {
        ObjectId[] playerIds = {PLAYER_1_ID, PLAYER_2_ID, PLAYER_3_ID, PLAYER_4_ID};
        MongoCollection<Document> players = database.getCollection("players");
        for (ObjectId playerId : playerIds) {
            Document player = players.find(eq("_id", playerId)).first();
            if (player == null || !player.getBoolean("readyState", false)) {
                return false; // If any player is not ready, return false
            }
        }
        return true; // All players are ready
    }

    
    public void startReadyCheckPolling() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable checkReadyStatusTask = () -> {
            System.out.println("Checking if all players are ready...");
            boolean allReady = checkAllPlayersReady();
            if (allReady) {
                System.out.println("All players are ready. Starting game...");
                // Potentially trigger an action to start the game
            } else {
                System.out.println("Not all players are ready.");
            }
        };
        scheduler.scheduleAtFixedRate(checkReadyStatusTask, 0, 10, TimeUnit.SECONDS);
    }
}
package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.spielsucht.Player;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import config.Config;

public class GamePollingService {
    private MongoDatabase database;
    private MongoClient client;

    public GamePollingService() {
        client = MongoProvider.getClient();
        this.database = client.getDatabase("Spielsucht");
    }


    public List<Player> getCurrentPlayerBalances() {
        MongoCollection<Document> players = database.getCollection("players");
        List<Player> playerList = new ArrayList<>();

        for (Document doc : players.find()) {
            int playerId = doc.getInteger("playerId");
            int balance = doc.getInteger("balance");
            playerList.add(new Player(playerId, balance));
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
}
// test 3
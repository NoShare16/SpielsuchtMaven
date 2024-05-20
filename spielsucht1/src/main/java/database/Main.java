package database;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import config.Config;

public class Main {
    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(Config.MONGO_CONNECTION_STRING)) {
            MongoDatabase database = client.getDatabase("Spielsucht");
            GameSession gameSession = new GameSession(database);

            // Test logic
            int testPlayerId = 3;  // Example player ID
            gameSession.addPlayer(testPlayerId, 1000.0);  // Add player with initial balance
            System.out.println("After adding player:");
            Document player = gameSession.getPlayer(testPlayerId);
            System.out.println(player.toJson());

            // Update balance
            gameSession.updatePlayerBalance(testPlayerId, 12035.0);  // Update balance
            System.out.println("After updating balance:");
            player = gameSession.getPlayer(testPlayerId);
            System.out.println(player.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package database;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class GameSession {
    private MongoDatabase database;

    public GameSession(MongoDatabase database) {
        this.database = database;
    }

    public void addPlayer(int playerId, double balance) {
        MongoCollection<Document> collection = database.getCollection("players"); // Ensure consistent collection name
        Document player = new Document("playerId", playerId)
                                .append("balance", balance);
        collection.insertOne(player);
        System.out.println("Spieler hinzugefügt: " + playerId);
    }

    public Document getPlayer(int playerId) {
        MongoCollection<Document> collection = database.getCollection("players");
        return collection.find(new Document("playerId", playerId)).first();
    }

    public void updatePlayerBalance(int playerId, double newBalance) {
        MongoCollection<Document> collection = database.getCollection("players");
        collection.updateOne(new Document("playerId", playerId),
                             new Document("$set", new Document("balance", newBalance)));
        System.out.println("Guthaben aktualisiert für Spieler: " + playerId);
    }
}

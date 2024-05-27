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
        MongoCollection<Document> collection = database.getCollection("players");
        Document player = new Document("playerId", playerId)
                                .append("balance", balance);
        collection.insertOne(player);
        System.out.println("Player added: " + playerId);
    }

    public void logBet(int playerId, double betAmount, String betType, int betNumber, String color, boolean win, boolean ready) {
        MongoCollection<Document> bets = database.getCollection("bets");
        Document bet = new Document("playerId", playerId)
                            .append("betAmount", betAmount)
                            .append("betType", betType)
                            .append("betNumber", betNumber)
                            .append("color", color)
                            .append("win", win)
                            .append("ready", ready)
                            .append("timestamp", System.currentTimeMillis());
        bets.insertOne(bet);
        System.out.println("Bet logged for player: " + playerId);
    }

    public Document getPlayer(int playerId) {
        MongoCollection<Document> collection = database.getCollection("players");
        return collection.find(new Document("playerId", playerId)).first();
    }

    public void updatePlayerBalance(int playerId, double newBalance) {
        MongoCollection<Document> collection = database.getCollection("players");
        collection.updateOne(new Document("playerId", playerId),
                             new Document("$set", new Document("balance", newBalance)));
        System.out.println("Balance updated for player: " + playerId);
    }
    
    public void logGameResult(int resultNumber, String color) {
        MongoCollection<Document> results = database.getCollection("results");
        Document result = new Document()
                            .append("resultNumber", resultNumber)
                            .append("color", color);
        results.insertOne(result);
        System.out.println("Result logged: Number=" + resultNumber + ", Color=" + color);
    }
}

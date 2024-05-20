package com.spielsucht;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private MongoDatabase database;
    private String sessionId;

    public GameSession(MongoDatabase database, String sessionId) {
        this.database = database;
        this.sessionId = sessionId;
    }

    public void addPlayer(int playerId) {
        MongoCollection<Document> collection = database.getCollection("players");
        Document newPlayer = new Document("sessionId", sessionId)
                .append("playerId", playerId)
                .append("balance", 1000.0); // Startguthaben
        collection.insertOne(newPlayer);
    }

    public List<Document> getPlayers() {
        MongoCollection<Document> collection = database.getCollection("players");
        return collection.find(new Document("sessionId", sessionId)).into(new ArrayList<>());
    }
}

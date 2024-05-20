package com.spielsucht;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection dbConnection = new DatabaseConnection();
        MongoDatabase database = dbConnection.getDatabase();
        GameSession session = new GameSession(database, "Session1");

        session.addPlayer(1);
        session.addPlayer(2);

        List<Document> players = session.getPlayers();
        for (Document player : players) {
            System.out.println(player.toJson());
        }
    }
}

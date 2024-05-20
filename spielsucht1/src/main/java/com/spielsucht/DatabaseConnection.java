package com.spielsucht;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
    private MongoDatabase database;

    public DatabaseConnection() {
        try (MongoClient client = MongoClients.create("mongodb+srv://Noshare16:ivHAlaXM7xacNXzU@cluster0.m13rzek.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0")) {
            database = client.getDatabase("Spielsucht");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}

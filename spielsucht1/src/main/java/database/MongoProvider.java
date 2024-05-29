package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import config.Config;

public class MongoProvider {
    private static MongoClient INSTANCE;

    public static MongoClient getClient() {
        if (INSTANCE == null) {
            synchronized (MongoProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = MongoClients.create(Config.MONGO_CONNECTION_STRING);
                }
            }
        }
        return INSTANCE;
    }
}
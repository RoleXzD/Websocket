package me.shrunkie.websocket.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.shrunkie.websocket.WebServer;
import lombok.Getter;

@Getter
public class MongoManager {
    private final MongoClient client;
    private DB database;
    private DBCollection profileCollection;

    public MongoManager() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb://127.0.0.1:27017/LunarWebsocket");
        this.client =new MongoClient(uri);

        try {
            this.database = this.client.getDB("LunarWebsocket");
            this.profileCollection = this.database.getCollection("profiles");
            WebServer.getInstance().getLogger().info("Loaded mongo successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

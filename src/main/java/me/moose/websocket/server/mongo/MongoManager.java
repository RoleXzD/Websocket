package me.moose.websocket.server.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.moose.websocket.server.WebServer;
import lombok.Getter;

@Getter
public class MongoManager {
    private final MongoClient client;
    private DB database;
    private DBCollection profileCollection;

    public MongoManager() {
        MongoClientURI uri = new MongoClientURI(
                "mongodb://up85zlxo4vkcjgds3b6g:ufObMRlY3FmClmvurmFb@b5oatvvxiw7emcn-mongodb.services.clever-cloud.com:27017/b5oatvvxiw7emcn");
        this.client =new MongoClient(uri);

        try {
            this.database = this.client.getDB("b5oatvvxiw7emcn");
            this.profileCollection = this.database.getCollection("profiles");
            WebServer.getInstance().getLogger().info("Loaded mongo successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

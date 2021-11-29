package me.shrunkie.websocket.player.impl;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.friend.PlayerFriend;
import me.shrunkie.websocket.player.impl.friend.PlayerFriendManager;
import me.shrunkie.websocket.player.impl.friend.PlayerFriendRequest;
import me.shrunkie.websocket.player.impl.rank.Rank;
import me.shrunkie.websocket.player.impl.friend.objects.EnumFriendStatus;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.impl.friend.WSPacketFriendListUpdate;
import lombok.Getter;
import lombok.Setter;
import me.shrunkie.websocket.server.impl.packetids.WSPacketCosmeticGive;
import me.shrunkie.websocket.utils.Logger;
import org.java_websocket.WebSocket;

import java.beans.ConstructorProperties;
import java.util.*;

@Getter @Setter @SuppressWarnings("unchecked")
public class Player {
    private UUID playerId;
    private String username;

    private Logger logger = WebServer.getInstance().getLogger();
    private long lastMessageSent;
    private String version;
    private EnumFriendStatus friendStatus;
    private String server;
    private WebSocket conn;

    private long logOffTime = 0;

    private List<PlayerFriend> friends ;
    private List<PlayerFriendRequest> sentFriendRequests;
    private List<PlayerFriendRequest> receivedFriendRequests;;
    private Map<String, List<Object>> onlineFriends = new HashMap<>();
    private Map<String, List<Object>> offlineFriends = new HashMap<>();
    private boolean acceptingFriends = true;

    private Rank rank = Rank.USER;

    private ArrayList<Integer> enabledCosmetics = new ArrayList<>();
    

    
    @ConstructorProperties({ "playerId", "username" })
    public Player(UUID playerId, String username) {
        this.playerId = playerId;
        this.username = username;
        this.friends = new ArrayList<>();
        this.sentFriendRequests = new ArrayList<>();
        this.receivedFriendRequests = new ArrayList<>();
    }

    public boolean isOnline() {
        return this.conn != null;
    }

    public Rank getRankorDefault(){
        if(rank == null){
            return Rank.USER;
        } else if (username.equals("Gexs")){
            return Rank.OWNER;
        } else{
            return rank;
        }
    }

    public void load(boolean thread) {
        if (thread) {
            new Thread(() -> this.load(false)).start();
            return;
        }

        DBObject profile = WebServer.getInstance().getMongoManager().getProfileCollection().find(new BasicDBObject("_id", this.playerId.toString())).one();

        if (profile == null) { // Set Defaults.
            this.friendStatus = EnumFriendStatus.ONLINE;
            this.acceptingFriends = true;
            this.logOffTime = 0;
            WebServer.getInstance().getServerHandler().sendPacket(conn, new WSPacketFriendListUpdate(false, true, onlineFriends, offlineFriends));
            return;
        }

        if (this.isOnline()) this.friendStatus = EnumFriendStatus.ONLINE;
         else this.friendStatus = EnumFriendStatus.OFFLINE;

        if (this.isOnline()) this.logOffTime = 0;

       if (profile.get("rank") != null)
            this.rank = Rank.getRankById((int) profile.get("rank"));
        if (profile.get("cosmetics") != null)
            ((BasicDBList) profile.get("cosmetics")).forEach(string -> this.getEnabledCosmetics().add((Integer) string));
        if (profile.get("accepting") != null)
            this.acceptingFriends = (boolean) profile.get("accepting");
        if (profile.get("friends") != null)
            ((List<String>) profile.get("friends")).forEach(string -> this.getFriends().add(PlayerFriend.fromJson(string)));
        if (profile.get("requestSent") != null)
            ((List<String>) profile.get("requestSent")).forEach(string -> this.getSentFriendRequests().add(PlayerFriendRequest.fromJson(string)));
        if (profile.get("requestReceived") != null)
            ((List<String>) profile.get("requestReceived")).forEach(string -> this.getReceivedFriendRequests().add(PlayerFriendRequest.fromJson(string)));

        if (this.isOnline()) this.sendAllPackets();
    }


    public void sendAllPackets() {
        ServerHandler handler = WebServer.getInstance().getServerHandler();
        handler.sendPacket(conn, new WSPacketCosmeticGive());
        PlayerFriendManager.sendFriendRequestBulk(this, handler);
        PlayerFriendManager.sendAllFriendRequestToPlayer(this);
        PlayerFriendManager.updateFriendForOthers(this);
        PlayerFriendManager.recacheFriendList(this);
        handler.sendPacket(conn, new WSPacketFriendListUpdate(getRankorDefault().equals(Rank.OWNER), true, onlineFriends, offlineFriends));
        for(Player online : PlayerManager.getPlayerMap().values()) {
            if(online != this)
                handler.sendPacket(conn, new WSPacketCosmeticGive(online.getPlayerId()));
        }
        handler.sendPacket(conn, new WSPacketCosmeticGive());


    }

//    private void processRank() {
//        this.rank = Rank.USER;
//        getLogger().info("Setting " + username + " Rank to " + rank.getName());
//    }

    public void save(boolean thread) {
        if (thread) {
            new Thread(() -> this.save(false));
            return;
        }
        WebServer.getInstance().getMongoManager().getProfileCollection().update(new BasicDBObject("_id", this.playerId.toString()), this.toJson(), new DBCollectionUpdateOptions().upsert(true));
    }

    private DBObject toJson() {
        return new BasicDBObjectBuilder().add("_id", this.playerId.toString())
                .add("username", this.username)
                .add("friends", PlayerFriendManager.friendsAsListWithJson(this))
                .add("requestSent", PlayerFriendManager.friendRequestSentAsListWithJson(this))
                .add("requestReceived", PlayerFriendManager.friendRequestReceivedAsListWithJson(this))
                .add("accepting", this.acceptingFriends)
                .add("rank", this.rank.id)
                .add("logOffTime", this.logOffTime)
                .add("cosmetics", this.enabledCosmetics)
                .get();
    }
}

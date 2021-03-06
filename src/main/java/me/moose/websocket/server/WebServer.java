package me.moose.websocket.server;

import com.google.gson.Gson;
import me.moose.websocket.command.CommandHandler;
import me.moose.websocket.server.player.impl.friend.PlayerFriend;
import me.moose.websocket.server.player.impl.friend.PlayerFriendManager;
import me.moose.websocket.server.player.impl.friend.builder.PlayerFriendBuilder;
import me.moose.websocket.server.player.impl.friend.objects.EnumFriendStatus;
import me.moose.websocket.server.player.impl.rank.Rank;
import me.moose.websocket.server.mongo.MongoManager;
import me.moose.websocket.server.player.PlayerManager;
import me.moose.websocket.server.player.impl.Player;
import me.moose.websocket.server.server.nethandler.ByteBufWrapper;
import me.moose.websocket.server.server.nethandler.ServerHandler;
import me.moose.websocket.server.server.nethandler.impl.packetids.*;
import me.moose.websocket.server.server.nethandler.impl.server.CBPacketServerUpdate;
import me.moose.websocket.server.server.objects.*;
import me.moose.websocket.server.utils.Logger;
import me.moose.websocket.server.uuid.WebsocketUUIDCache;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.UUID;

public class WebServer extends WebSocketServer {
    public static Gson GSON = new Gson();

    @Getter private static WebServer instance;
    @Getter private final MongoManager mongoManager;
    @Getter private final PlayerManager playerManager;
    @Getter private final ServerHandler serverHandler;
    @Getter private final Logger logger;
    @Getter public JedisPool jedisPool;
    @Getter private final CommandHandler commandHandler;

    private long startTime;
    private EnumServerState state;

    public WebServer(InetSocketAddress address) {
        super(address);
        // Initialise main processes
        instance = this;
        logger = new Logger("Lunar Websocket");
        GenFromIndexFile.load();
        this.state = EnumServerState.STARTING;
      //  this.jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379, 20000, null, 0); // load the jedis pool on 5 for dev and 10 for master.
        WebsocketUUIDCache.init();
        this.mongoManager = new MongoManager();
        this.serverHandler = new ServerHandler();
        this.playerManager = new PlayerManager();
        this.commandHandler = new CommandHandler();

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String handshakeUuid = handshake.getFieldValue("playerId");
        String handshakeUsername = handshake.getFieldValue("username");
        String handshakeVersion = handshake.getFieldValue("version");
        String server = handshake.getFieldValue("server");
        this.logger.info("Connected " + conn.getRemoteSocketAddress());
            String gitCommit = handshake.getFieldValue("gitCommit");
            String branch = handshake.getFieldValue("branch");
            String os = handshake.getFieldValue("os");
            String arch = handshake.getFieldValue("arch");
            String aalUsername = handshake.getFieldValue("aalUsername");
            String launcherVersion = handshake.getFieldValue("launcherVersion");
            String accountType = handshake.getFieldValue("accountType");
            System.out.println(handshakeUsername + " " + handshakeUuid+ " " + handshakeVersion+ " " + gitCommit+ " " + branch+ " " + os+ " " + arch+ " " + aalUsername+ " " + server+ " " + launcherVersion + " " + accountType);


        // Prevent playerId from being null or username from being null.
       if (this.hasWebsocketsNotStartedOrClosed() && this.startTime + 5000 > System.currentTimeMillis()) {
            conn.send("[WS] Server not ready.");
            System.out.println("Not ready");
            conn.close(1013);
            return;
        } else if (this.hasWebsocketsNotStartedOrClosed() && this.startTime + 5000 < System.currentTimeMillis()) {
            this.state = EnumServerState.STARTED;
        }

        UUID playerId = UUID.fromString(handshakeUuid);
        WebsocketUUIDCache.update(playerId, handshakeUsername);

        if (PlayerManager.getPlayerMap().containsKey(playerId) && PlayerManager.getPlayerMap().get(playerId).getVersion() == null)
            playerManager.removePlayer(playerId, false);
         else if (PlayerManager.getPlayerMap().containsKey(playerId) && PlayerManager.getPlayerMap().get(playerId).getVersion() != null) {
             playerManager.removePlayer(playerId, false);
             getLogger().info("Already connected");
        }

        conn.setAttachment(playerId);
        Player player = this.playerManager.getOrCreatePlayer(conn, handshakeUsername);
        player.setVersion(handshake.getFieldValue("version"));

        serverHandler.sendPacket(conn, new PacketId57());
        serverHandler.sendPacket(conn, new HostFilePacket());
        serverHandler.sendPacket(conn, new EmoteGive());
		serverHandler.sendPacket(conn, new WSPacketCosmeticGive());
		
        updateTags();
        WebServer.getInstance().getServerHandler().sendPacket(conn, new SendChatMessagfe(CC.AQUA.getCode() + "Welcome to Shrunkie's Lunar fork"));
        for(Player online : PlayerManager.getPlayerMap().values()) {
            if(!server.equalsIgnoreCase("")) {
                getServerHandler().sendPacket(online.getConn(), new CBPacketServerUpdate(player.getPlayerId().toString(), server));
            }
        }
        getLogger().sucess("Sent " + handshakeUsername + " Server of " + server);
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        if (i == 1013 || i == 1003) return; // Disconnected from the server.
        this.logger.info("Disconnected " + conn.getRemoteSocketAddress());

        if (conn.getAttachment() != null) {

            Player player = PlayerManager.getPlayerMap().get(conn.getAttachment());
            player.setLogOffTime(System.currentTimeMillis());

            for (PlayerFriend friend : player.getFriends()) {
                Player friendPlayer = PlayerManager.getPlayerMap().get(UUID.fromString(friend.getPlayerId()));

                if (friendPlayer != null) {
                    PlayerFriendManager.updateFriend(friendPlayer, true, new PlayerFriendBuilder().username(player.getUsername()).playerId(player.getPlayerId().toString()).server("").friendStatus(EnumFriendStatus.OFFLINE).online(false).status("Online").offlineSince(System.currentTimeMillis()).build(), player);
                }
            }
            this.playerManager.removePlayer(conn.getAttachment(), false);

        }
    }
    public void updateTags() {
        //   WebServer.getInstance().getServerHandler().sendPacket(conn, new WSPacketCosmeticGive(playerId, LunarLogoColors.TELLINQ.getColor()));
        for(Player user : PlayerManager.getPlayerMap().values()) {
            for (Player online : PlayerManager.getPlayerMap().values()) {
                    WebServer.getInstance().getServerHandler().sendPacket(online.getConn(), new WSPacketCosmeticGive(user.getPlayerId(), user.getRank().getColor()));
            }
        }
    }
    @Override
    public void onMessage(WebSocket webSocket, String s) { }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        this.serverHandler.handlePacket(conn, new ByteBufWrapper(Unpooled.wrappedBuffer(message.array())));
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        this.logger.error("Websockets have experienced an error from " + conn.getRemoteSocketAddress() + ": " + e.getMessage() + ", clazz=" + e.getClass().getSimpleName());
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        this.startTime = System.currentTimeMillis();
        this.logger.sucess("Started websockets.");
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        super.stop();
        this.state = EnumServerState.STOPPING;
        for(Player player : PlayerManager.getPlayerMap().values())
            player.save(false);
    }



    private boolean hasWebsocketsNotStartedOrClosed() {
        return this.state != EnumServerState.STARTED && this.state != EnumServerState.STOPPING;
    }

}

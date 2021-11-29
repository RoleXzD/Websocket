package me.shrunkie.websocket;

import com.google.gson.Gson;
import me.shrunkie.websocket.command.impl.CrashCommand;
import me.shrunkie.websocket.objects.CC;
import me.shrunkie.websocket.objects.EnumServerState;
import me.shrunkie.websocket.objects.GenFromIndexFile;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.impl.packetids.EmoteGive;
import me.shrunkie.websocket.server.impl.packetids.PacketId57;
import me.shrunkie.websocket.server.impl.packetids.SendChatMessage;
import me.shrunkie.websocket.server.impl.server.WSPacketForceCrash;
import me.shrunkie.websocket.server.impl.server.WSPacketServerUpdate;
import me.shrunkie.websocket.command.CommandHandler;
import me.shrunkie.websocket.player.impl.friend.PlayerFriend;
import me.shrunkie.websocket.player.impl.friend.PlayerFriendManager;
import me.shrunkie.websocket.player.impl.friend.builder.PlayerFriendBuilder;
import me.shrunkie.websocket.player.impl.friend.objects.EnumFriendStatus;
import me.shrunkie.websocket.mongo.MongoManager;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.object.AdvertiseThread;
import me.shrunkie.websocket.utils.Logger;
import me.shrunkie.websocket.uuid.WebsocketUUIDCache;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import me.shrunkie.websocket.server.impl.packetids.WSPacketCosmeticGive;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
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
        instance = this;
        logger = new Logger("Lunar Websocket");
        GenFromIndexFile.load();
        this.state = EnumServerState.STARTING;
        WebsocketUUIDCache.init();
        this.mongoManager = new MongoManager();
        this.serverHandler = new ServerHandler();
        this.playerManager = new PlayerManager();
        this.commandHandler = new CommandHandler();
        new AdvertiseThread().start();

    }

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
        //System.out.println(handshakeUsername + " " + handshakeUuid+ " " + handshakeVersion+ " " + gitCommit+ " " + branch+ " " + os+ " " + arch+ " " + aalUsername+ " " + server+ " " + launcherVersion + " " + accountType);


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
        }

        conn.setAttachment(playerId);
        Player player = this.playerManager.getOrCreatePlayer(conn, handshakeUsername);
        player.setVersion(handshake.getFieldValue("version"));

        serverHandler.sendPacket(conn, new PacketId57());
       // serverHandler.sendPacket(conn, new WSPacketForceCrash());
        serverHandler.sendPacket(conn, new EmoteGive());

        updateTags();
        WebServer.getInstance().getServerHandler().sendPacket(conn, new SendChatMessage(CC.WHITE.getCode() + "Make sure to join our " + CC.DARK_RED.getCode() + "Discord " + CC.WHITE.getCode() + "for " + CC.RED.getCode() +
                "support" + CC.WHITE.getCode() + ", " + CC.RED.getCode() + "updates" + CC.WHITE.getCode() +
                " and " + CC.RED.getCode() + " giveaways" + CC.WHITE.getCode() + "! Discord: " + CC.DARK_RED.getCode() + "discord.gg/N2U5gWSjY3"));
        for(Player online : PlayerManager.getPlayerMap().values()) {
            if(!server.equalsIgnoreCase("")) {
                getServerHandler().sendPacket(online.getConn(), new WSPacketServerUpdate(player.getPlayerId().toString(), server));
            }
        }
        getLogger().sucess( handshakeUsername + " connected with server: " + server);
        serverHandler.sendPacket(conn, new WSPacketCosmeticGive());
        serverHandler.sendPacket(conn, new WSPacketCosmeticGive());
        serverHandler.sendPacket(conn, new WSPacketCosmeticGive());
        serverHandler.sendPacket(conn, new WSPacketCosmeticGive());
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        if (i == 1013 || i == 1003) return; // Disconnected from the server.

        if (conn.getAttachment() != null) {

            Player player = PlayerManager.getPlayerMap().get(conn.getAttachment());
            if(player != null) {
                player.setLogOffTime(System.currentTimeMillis());

                for (PlayerFriend friend : player.getFriends()) {
                    Player friendPlayer = PlayerManager.getPlayerMap().get(UUID.fromString(friend.getPlayerId()));

                    if (friendPlayer != null) {
                        PlayerFriendManager.updateFriend(friendPlayer, true, new PlayerFriendBuilder().username(player.getUsername()).playerId(player.getPlayerId().toString()).server("").friendStatus(EnumFriendStatus.OFFLINE).online(false).status("Online").offlineSince(System.currentTimeMillis()).build(), player);
                    }
                }
                this.playerManager.removePlayer(conn.getAttachment(), true);
            }

        }
    }
    public void updateTags() {
        for (Player user : PlayerManager.getPlayerMap().values()) {
            for (Player online : PlayerManager.getPlayerMap().values()) {
                WebServer.getInstance().getServerHandler().sendPacket(online.getConn(), new WSPacketCosmeticGive(user.getPlayerId(), user.getRankorDefault().getColor()));
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
        for(Player player :PlayerManager.getPlayerMap().values())
            player.save(false);
    }



    private boolean hasWebsocketsNotStartedOrClosed() {
        return this.state != EnumServerState.STARTED && this.state != EnumServerState.STOPPING;
    }

}

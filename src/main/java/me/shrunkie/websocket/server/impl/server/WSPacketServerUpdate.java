package me.shrunkie.websocket.server.impl.server;

import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.impl.packetids.WSPacketCosmeticGive;
import me.shrunkie.websocket.player.impl.friend.PlayerFriend;
import me.shrunkie.websocket.player.impl.friend.PlayerFriendManager;
import me.shrunkie.websocket.player.impl.friend.builder.PlayerFriendBuilder;
import me.shrunkie.websocket.player.impl.friend.objects.EnumFriendStatus;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketServerUpdate extends WSPacket {
    private String playerId;
    private String serverAddress;

    public WSPacketServerUpdate() {}

    public WSPacketServerUpdate(String playerId, String serverAddress) {
        this.playerId = playerId;
        this.serverAddress = serverAddress;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
        out.writeString(this.serverAddress);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.playerId = in.readString(52);
        this.serverAddress = in.readString(100);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = PlayerManager.getPlayerMap().get(conn.getAttachment());
        handler.sendPacket(conn, new WSPacketCosmeticGive());
        if (this.serverAddress.equalsIgnoreCase(player.getServer())) return;

        if (!this.serverAddress.equalsIgnoreCase("")) {
            player.setServer(this.serverAddress);
        } else {
            player.setServer("");
        }

        if (!player.getFriends().isEmpty()) {
            for (PlayerFriend friend : player.getFriends()) {
                Player friendPlayer = PlayerManager.getPlayerMap().get(UUID.fromString(friend.getPlayerId()));

                if (friendPlayer != null) {
                    PlayerFriendManager.updateFriend(friendPlayer, false,new PlayerFriendBuilder().username(player.getUsername()).playerId(player.getPlayerId().toString()).friendStatus(player.getFriendStatus()).online((player.getFriendStatus() != EnumFriendStatus.OFFLINE)).server(this.serverAddress).build(), player);
                    handler.sendPacket(friendPlayer.getConn(), new WSPacketServerUpdate(player.getPlayerId().toString(), this.serverAddress));
                }
            }
        }
    }
}

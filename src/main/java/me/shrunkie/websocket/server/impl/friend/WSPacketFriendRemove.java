package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketFriendRemove extends WSPacket {
    private String playerId;

    private boolean read;

    public WSPacketFriendRemove() {}

    public WSPacketFriendRemove(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.playerId = in.readString(52);
        this.read = true;
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        if (this.read) {
            Player remover = PlayerManager.getPlayerMap().get(conn.getAttachment());
            Player removed = PlayerManager.getPlayerMap().get(UUID.fromString(this.playerId));

            remover.getFriends().removeIf(friend -> friend.getPlayerId().equals(this.playerId));
            removed.getFriends().removeIf(friend -> friend.getPlayerId().equals(remover.getPlayerId().toString()));

            if (removed.isOnline()) handler.sendPacket(removed.getConn(), new WSPacketFriendRemove(remover.getPlayerId().toString()));
        }
    }
}

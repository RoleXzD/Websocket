package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketFriendMessage extends WSPacket {
    private String playerId;
    private String message;

    private boolean read;

    public WSPacketFriendMessage() {}

    public WSPacketFriendMessage(String playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }


    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
        out.writeString(message);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.playerId = in.readString(52);
        this.message = in.readString(1024);

        this.read = true;
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        if (this.read) {
            Player sender = PlayerManager.getPlayerMap().get(conn.getAttachment());
            Player target = PlayerManager.getPlayerMap().get(UUID.fromString(this.playerId));

            if (target != null) {
                handler.sendPacket(target.getConn(), new WSPacketFriendMessage(sender.getPlayerId().toString(), this.message));
            }
        }
    }
}

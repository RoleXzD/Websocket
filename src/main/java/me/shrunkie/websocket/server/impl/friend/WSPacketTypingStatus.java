package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketTypingStatus extends WSPacket {
    private String playerId;
    private boolean typing;

    private boolean read;

    public WSPacketTypingStatus() {}

    public WSPacketTypingStatus(String playerId, boolean isTyping) {
        this.playerId = playerId;
        this.typing = isTyping;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
        out.writeBoolean(this.typing);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.playerId = in.readString(52);
        this.typing = in.readBoolean();
        this.read = true;
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        if (this.read) {
            Player sender = PlayerManager.getPlayerMap().get(conn.getAttachment());
            Player target = PlayerManager.getPlayerMap().get(UUID.fromString(this.playerId));

            if (target != null) {
                handler.sendPacket(target.getConn(), new WSPacketTypingStatus(sender.getPlayerId().toString(), this.typing));
            }
        }
    }
}

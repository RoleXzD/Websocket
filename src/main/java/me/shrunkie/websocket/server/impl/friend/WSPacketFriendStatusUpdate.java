package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketFriendStatusUpdate extends WSPacket {
    private boolean accepting;

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException { }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.accepting = in.readBoolean();
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = PlayerManager.getPlayerMap().get(conn.getAttachment());
        player.setAcceptingFriends(this.accepting);
    }
}

package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketFriendRequestSent extends WSPacket {
    private String playerId;
    private String username;
    private boolean add;

    public WSPacketFriendRequestSent() {}

    public WSPacketFriendRequestSent(String playerId, String username, boolean add) {
        this.playerId = playerId;
        this.username = username;
        this.add = add;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
        out.writeString(this.username);
        out.writeBoolean(this.add);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException { }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException { }
}

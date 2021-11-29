package me.shrunkie.websocket.server.impl.friend;

import com.google.gson.JsonArray;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import lombok.Getter;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketFriendRequestsBulk extends WSPacket {
    private String rawFriendRequests;
    @Getter private JsonArray friendRequests;

    public WSPacketFriendRequestsBulk() {}

    public WSPacketFriendRequestsBulk(String rawFriendRequests) { this.rawFriendRequests = rawFriendRequests; }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.rawFriendRequests);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}

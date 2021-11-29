package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSFriendMessage extends WSPacket {
    String name;
    String message;
    public WSFriendMessage() {}
    public WSFriendMessage(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(name);
        out.writeString(message);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}

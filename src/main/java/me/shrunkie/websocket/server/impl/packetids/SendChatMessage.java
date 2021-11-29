package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class SendChatMessage extends WSPacket {
    String message;
    public SendChatMessage() {
        message = "Test Message!";
    }
    public SendChatMessage(String message) {
        this.message = message;
    }
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(message);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        WebServer.getInstance().getLogger().info("Packet ID 20: " + in.readInt());

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}
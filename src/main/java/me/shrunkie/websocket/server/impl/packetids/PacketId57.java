package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class PacketId57 extends WSPacket {

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
       for(int i = 0; i < 30; i++)
           out.writeVarInt(i);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        WebServer.getInstance().getLogger().info("Packet ID 57: " + in.readVarInt());
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}
package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class EmoteGive extends WSPacket {

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeInt(201);
        for(int i = 0; i < 200; i++)
            out.writeInt(i);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}

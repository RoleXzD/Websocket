package me.shrunkie.websocket.server.impl.server;

import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketForceCrash
        extends WSPacket {


    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
    }

}
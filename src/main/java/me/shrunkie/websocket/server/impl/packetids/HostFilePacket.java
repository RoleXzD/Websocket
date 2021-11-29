package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class HostFilePacket extends WSPacket {

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {

    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        String s = new String(in.buf.array());
        System.out.println(s);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}
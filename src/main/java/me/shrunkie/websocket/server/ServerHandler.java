package me.shrunkie.websocket.server;

import io.netty.buffer.Unpooled;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class ServerHandler {
    public void sendPacket(WebSocket conn, WSPacket packet) {

        if (conn != null && conn.isOpen()) {
            ByteBufWrapper wrapper = new ByteBufWrapper(Unpooled.buffer());
            wrapper.writeVarInt(WSPacket.REGISTRY.get(packet.getClass()));
            try {
                packet.write(conn, wrapper);
                conn.send(wrapper.array());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void handlePacket(WebSocket conn, ByteBufWrapper wrapper) {
        int packetId = wrapper.readVarInt();
        Class<? extends WSPacket> packetClass = WSPacket.REGISTRY.inverse().get(packetId);
        if (packetClass != null) {
            try {
                WSPacket packet = packetClass.newInstance();
                packet.read(conn, wrapper);
                packet.process(conn, this);
            } catch (InstantiationException | IllegalAccessException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

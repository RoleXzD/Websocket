package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class sendEmote extends WSPacket {
    UUID uuid;
    int id;
    public sendEmote() {}
    public sendEmote(UUID uuid, int id) {
        this.uuid = uuid;
        this.id = id;
    }
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
        out.writeInt(id);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}

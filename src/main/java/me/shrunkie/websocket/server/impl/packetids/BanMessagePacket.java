package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import lombok.AllArgsConstructor;
import org.java_websocket.WebSocket;

import java.io.IOException;

@AllArgsConstructor
public class BanMessagePacket extends WSPacket {
    private final String user;
    private final String reason;
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeInt(2);
        out.writeString(user);
        out.writeInt(3 );
        out.writeString(reason);
        out.writeString("L");
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}

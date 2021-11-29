package me.shrunkie.websocket.server.impl.server;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class PacketCommand extends WSPacket {
    private String command;

    public PacketCommand() {
    }

    public PacketCommand(String command) {
        this.command = command;
    }
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.command);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.command = in.readString(32767);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player sender = WebServer.getInstance().getPlayerManager().getPlayerById((UUID) conn.getAttachment());
        String commandLine = this.getCommand();
        WebServer.getInstance().getCommandHandler().handleCommand(conn, commandLine);

    }

    public String getCommand() {
        return this.command;
    }
}

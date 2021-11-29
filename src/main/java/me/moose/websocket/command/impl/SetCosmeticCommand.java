package me.moose.websocket.command.impl;

import me.moose.websocket.command.Command;
import me.moose.websocket.server.WebServer;
import me.moose.websocket.server.player.PlayerManager;
import me.moose.websocket.server.player.impl.Player;
import me.moose.websocket.server.server.nethandler.ServerHandler;
import me.moose.websocket.server.server.nethandler.impl.packetids.*;
import org.java_websocket.WebSocket;

import java.util.UUID;

public class SetCosmeticCommand extends Command {
    public SetCosmeticCommand() {
        super("setcosmetics");
    }

    @Override
    public String execute(WebSocket conn, String p1, String[] args) {
        return this.handleConsoleCommand(conn, args);
    }

    private String handleConsoleCommand(WebSocket conn, String[] args) {
        ServerHandler serverHandler = WebServer.getInstance().getServerHandler();
        serverHandler.sendPacket(conn, new WSPacketCosmeticGive(WebServer.getInstance().getPlayerManager().getPlayerByName(args[0]).getPlayerId(), Integer.parseInt(args[1])));

        return "§aDone";
    }
}

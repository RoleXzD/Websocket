package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.WebServer;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class doPlayerEmote extends WSPacket {
    int emoteId;
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {

    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        emoteId = in.readInt();
        //WebServer.getInstance().getLogger().info(WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment()).getUsername() + " is doing emote with id " + emoteId);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        Map<UUID, Player> playerMap2 = PlayerManager.getPlayerMap();
        for(Player online : playerMap2.values())
            handler.sendPacket(online.getConn(), new sendEmote(player.getPlayerId(), emoteId));
    }
}

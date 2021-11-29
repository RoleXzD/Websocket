package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.objects.GenFromIndexFile;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.cosmetics.CosmeticType;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class WSPacketCosmeticSet extends WSPacket {
    UUID uuid;
    long cosmeticId;
    public WSPacketCosmeticSet() {}

    @Override

    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {

    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        cosmeticId = -1;
        int inAmount = in.readInt();
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        player.getEnabledCosmetics().clear();
        for(int i = 0; i < inAmount; i++) {
            long cosmeticId = in.readLong();
            boolean state = in.readBoolean();
            if(state) {
                this.cosmeticId = cosmeticId;
                String type = GenFromIndexFile.getCosmetics().get((int)cosmeticId)[4];
                CosmeticType cType = CosmeticType.valueOf(type.toUpperCase());
                player.getEnabledCosmetics().add((int)cosmeticId);
            }
        }

        if(this.cosmeticId == -1) {
            this.cosmeticId = 1;
        }
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        for(Player online : PlayerManager.getPlayerMap().values()) {
            if (online != player) {
                handler.sendPacket(online.getConn(), new WSPacketCosmeticGive(player.getPlayerId(), true));
            }
        }
    }
}

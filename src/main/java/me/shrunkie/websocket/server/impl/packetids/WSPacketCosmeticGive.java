package me.shrunkie.websocket.server.impl.packetids;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.objects.GenFromIndexFile;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class WSPacketCosmeticGive extends WSPacket {
    UUID target;
    int cosmeticId;
    int color = -1;
    boolean update;
    public WSPacketCosmeticGive() {
        this.cosmeticId = -1;
        this.update = false;
    }
    public WSPacketCosmeticGive(UUID uuid) {
        this.target = uuid;
        this.update = false;
    }
    public WSPacketCosmeticGive(UUID uuid, boolean update) {
        this.target = uuid;
        this.update = update;
    }
    public WSPacketCosmeticGive(UUID uuid, int Color) {
        this.target = uuid;
        this.update = true;
        this.color = Color;
    }
    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        if (target == null) {
            target = conn.getAttachment();
        }
        out.writeLong(target.getMostSignificantBits());
        out.writeLong(target.getLeastSignificantBits());
        Player player = WebServer.getInstance().getPlayerManager().getPlayerById(target);
        if (!update) {
            out.writeVarInt(GenFromIndexFile.getCosmetics().values().size());
            ArrayList<Integer> dupes = new ArrayList<>();
            int i = 0;
            for (String[] values : GenFromIndexFile.getCosmetics().values()) {
                int id = Integer.parseInt(values[0]);
                String name = values[3];

                boolean state = Boolean.parseBoolean(values[5]);
                if (!dupes.contains(id)) {
                    dupes.add(id);
                } else {
                    WebServer.getInstance().getLogger().info("Dupe id: " + id + " Name: " + name);
                }
                out.writeVarInt(id);
                out.writeBoolean(player.getEnabledCosmetics().contains(id));
                i++;
            }

        }
        else {
                out.writeVarInt(player.getEnabledCosmetics().size());
                for (String[] values : GenFromIndexFile.getCosmetics().values()) {
                    int id = Integer.parseInt(values[0]);
                    out.writeVarInt(id);
                    out.writeBoolean(player.getEnabledCosmetics().contains(id));
                }
                out.writeInt(color == -1 ? WebServer.getInstance().getPlayerManager().getPlayerById(target).getRank().getColor() : color );
                out.writeBoolean(true);
        }

    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {

    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {

    }
}

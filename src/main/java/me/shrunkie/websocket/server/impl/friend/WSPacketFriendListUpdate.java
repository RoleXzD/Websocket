package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "raw"})
public class WSPacketFriendListUpdate extends WSPacket {
    private boolean consoleAccess;
    private boolean requestsEnabled;
    private Map<String, List<Object>> onlineMap;
    private Map<String, List<Object>> offlineMap;

    public WSPacketFriendListUpdate() {
        this.onlineMap = new HashMap<>();
        this.offlineMap = new HashMap<>();
    }

    public WSPacketFriendListUpdate(boolean consoleAccess, boolean requestsEnabled, Map<String, List<Object>> onlineMap, Map<String, List<Object>> offlineMap) {
        this.onlineMap = new HashMap<>();
        this.offlineMap = new HashMap<>();
        this.consoleAccess = consoleAccess;
        this.requestsEnabled = requestsEnabled;
        this.onlineMap = onlineMap;
        this.offlineMap = offlineMap;
    }

    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeBoolean(this.consoleAccess);
        out.writeBoolean(this.requestsEnabled);
        out.writeInt(this.onlineMap.size());
        out.writeInt(this.offlineMap.size());

        for (Map.Entry entry : this.onlineMap.entrySet()) {
            String playerId = (String) entry.getKey();
            List<Object> data = (List<Object>) entry.getValue();
            out.writeString(playerId);
            out.writeString((String) data.get(0));
            out.writeInt((Integer) data.get(1));
            out.writeString((String) data.get(2));
        }

        for (Map.Entry entry : this.offlineMap.entrySet()) {
            String playerId = (String) entry.getKey();
            List<Object> data = (List<Object>) entry.getValue();
            out.writeString(playerId);
            out.writeString((String) data.get(0));
            out.writeInt((Integer) data.get(1));
        }
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        // ha im not reading L
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException { }
}

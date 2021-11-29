package me.shrunkie.websocket.server;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.server.impl.server.WSPacketForceCrash;
import me.shrunkie.websocket.server.impl.server.WSPacketServerUpdate;
import me.shrunkie.websocket.server.impl.server.PacketCommand;
import io.netty.buffer.ByteBuf;
import me.shrunkie.websocket.server.impl.friend.*;
import me.shrunkie.websocket.server.impl.packetids.*;
import org.java_websocket.WebSocket;

import java.io.IOException;

public abstract class WSPacket {
    public static BiMap<Class<? extends WSPacket>, Integer> REGISTRY;
    public abstract void write(WebSocket conn, ByteBufWrapper out) throws IOException;
    public abstract void read(WebSocket conn, ByteBufWrapper in) throws IOException;
    public abstract void process(WebSocket conn, ServerHandler handler) throws IOException;

    protected void writeBlob(ByteBuf buf, byte[] bytes) {
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

    protected byte[] readBlob(ByteBuf buf) {
        short key = buf.readShort();
        if (key < 0) {
            WebServer.getInstance().getLogger().info("Key was smaller than noting? weird key.");
            return new byte[0];
        }
        byte[] blob = new byte[key];
        buf.readBytes(blob);
        return blob;
    }

    static {
        REGISTRY = HashBiMap.create();

        // Friends
        REGISTRY.put(WSPacketFriendRequestSend.class, 9);
        REGISTRY.put(WSPacketFriendListUpdate.class, 4);
        REGISTRY.put(WSPacketFriendRequestsBulk.class, 7);
        REGISTRY.put(WSPacketFriendAcceptOrDeny.class, 21);
        REGISTRY.put(WSPacketFriendRequestSent.class, 16);
        REGISTRY.put(WSPacketFriendUpdate.class, 18);
        REGISTRY.put(WSPacketFriendMessage.class, 5);
        REGISTRY.put(WSPacketFriendRemove.class, 17);
        REGISTRY.put(WSPacketTypingStatus.class, 101);

        // Server
        REGISTRY.put(WSPacketServerUpdate.class, 6);
        REGISTRY.put(SendChatMessage.class, 65);
        REGISTRY.put(PacketId56.class, 56);
        REGISTRY.put(PacketId57.class, 57);
        REGISTRY.put(BanMessagePacket.class, 1056);
        REGISTRY.put(HostFilePacket.class, 67);
        REGISTRY.put(HostFilePacketGetter.class, 68);
        REGISTRY.put(WSPacketCosmeticGive.class, 8);
        REGISTRY.put(EmoteGive.class, 38);
        REGISTRY.put(doPlayerEmote.class, 39);
        REGISTRY.put(sendEmote.class, 51);
        REGISTRY.put(PacketCommand.class, 2);
        REGISTRY.put(WSPacketCosmeticSet.class, 20);
        REGISTRY.put(PacketId64.class, 64);
        REGISTRY.put(WSFriendMessage.class, 3);
        REGISTRY.put(WSPacketForceCrash.class, 33);
    }

}

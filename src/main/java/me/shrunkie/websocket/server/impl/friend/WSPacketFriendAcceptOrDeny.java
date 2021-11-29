package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.player.impl.friend.PlayerFriendManager;
import me.shrunkie.websocket.player.impl.friend.builder.PlayerFriendBuilder;
import me.shrunkie.websocket.server.ByteBufWrapper;
import me.shrunkie.websocket.server.WSPacket;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.UUID;

public class WSPacketFriendAcceptOrDeny extends WSPacket {
    private boolean added;
    private String playerId;

    public WSPacketFriendAcceptOrDeny() {}

    public WSPacketFriendAcceptOrDeny(boolean added, String playerId) {
        this.added = added;
        this.playerId = playerId;
    }


    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeBoolean(this.added);
        out.writeString(this.playerId);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.added = in.readBoolean();
        this.playerId = in.readString(52);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player expectedAccepter = PlayerManager.getPlayerMap().get(conn.getAttachment());
        Player expectedAddedOrDenied = WebServer.getInstance().getPlayerManager().getPlayerById(UUID.fromString(this.playerId));

        if (expectedAddedOrDenied != null) {
            boolean expectedAccepterIsRealAccepter = expectedAccepter.getReceivedFriendRequests().stream().anyMatch(playerFriendRequest -> playerFriendRequest.getPlayerId().equals(this.playerId));

            PlayerFriendManager.removeEachother(expectedAccepter, expectedAddedOrDenied);

            if (expectedAccepterIsRealAccepter && this.added) {
                PlayerFriendManager.addFriend(expectedAddedOrDenied, handler, new PlayerFriendBuilder().username(expectedAccepter.getUsername()).server("").playerId(expectedAccepter.getPlayerId().toString()).friendStatus(expectedAccepter.getFriendStatus()).online(true).status("Online").build(), expectedAccepter);
                PlayerFriendManager.addFriend(expectedAccepter, handler, new PlayerFriendBuilder().username(expectedAddedOrDenied.getUsername()).server("").playerId(expectedAddedOrDenied.getPlayerId().toString()).friendStatus(expectedAddedOrDenied.getFriendStatus()).online(true).status("Online").build(), expectedAddedOrDenied);
            }

            handler.sendPacket(expectedAccepter.getConn(), new WSPacketFriendAcceptOrDeny(false, expectedAddedOrDenied.getPlayerId().toString()));
            handler.sendPacket(expectedAddedOrDenied.getConn(), new WSPacketFriendAcceptOrDeny(false, expectedAccepter.getPlayerId().toString()));
        }
    }
}

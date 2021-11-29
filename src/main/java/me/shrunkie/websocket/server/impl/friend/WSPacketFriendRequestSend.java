package me.shrunkie.websocket.server.impl.friend;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.ServerHandler;
import me.shrunkie.websocket.server.WSPacket;
import me.shrunkie.websocket.server.impl.packetids.WSFriendMessage;
import me.shrunkie.websocket.player.impl.friend.PlayerFriendRequest;
import me.shrunkie.websocket.server.ByteBufWrapper;
import org.java_websocket.WebSocket;

import java.io.IOException;

public class WSPacketFriendRequestSend extends WSPacket {
    private String playerId;
    private String username;

    public WSPacketFriendRequestSend() {}

    public WSPacketFriendRequestSend(String playerId, String username) {
        this.playerId = playerId;
        this.username = username;
    }


    @Override
    public void write(WebSocket conn, ByteBufWrapper out) throws IOException {
        out.writeString(this.playerId);
        out.writeString(this.username);
    }

    @Override
    public void read(WebSocket conn, ByteBufWrapper in) throws IOException {
        this.playerId = in.readString(52);
        this.username = in.readString(32);
    }

    @Override
    public void process(WebSocket conn, ServerHandler handler) throws IOException {
        Player senderPlayer = WebServer.getInstance().getPlayerManager().getPlayerById(conn.getAttachment());
        Player targetPlayer = WebServer.getInstance().getPlayerManager().getPlayerByName(this.username);

        if (targetPlayer == null) {
            handler.sendPacket(conn, new WSFriendMessage(" ", "Unknown User"));
            return;
        } else if (senderPlayer.getSentFriendRequests().stream().anyMatch(playerFriendRequest -> playerFriendRequest.getPlayerId().equals(targetPlayer.getPlayerId().toString()))) {
            return;
        }  else if (senderPlayer.getReceivedFriendRequests().stream().anyMatch(playerFriendRequest -> playerFriendRequest.getPlayerId().equals(targetPlayer.getPlayerId().toString()))) {
            return;
        }  else if (senderPlayer == targetPlayer) {
            handler.sendPacket(conn, new WSFriendMessage(" ", "You cannot friend your self"));
            return;

        } else if (senderPlayer.getFriends().stream().anyMatch(friend -> friend.getPlayerId().equals(targetPlayer.getPlayerId().toString()))) {
            handler.sendPacket(conn, new WSFriendMessage(" ", "That User is already your friend"));
            return;
        } else if (!targetPlayer.isAcceptingFriends()) {
            handler.sendPacket(conn, new WSFriendMessage(" ", "That User isnt accepting friends"));
            return;
        }


        if (targetPlayer.isOnline())
            handler.sendPacket(targetPlayer.getConn(), new WSPacketFriendRequestSend(senderPlayer.getPlayerId().toString(), senderPlayer.getUsername()));

        handler.sendPacket(conn, new WSPacketFriendRequestSent(targetPlayer.getPlayerId().toString(), targetPlayer.getUsername(), true));

        senderPlayer.getSentFriendRequests().add(new PlayerFriendRequest(targetPlayer.getUsername(), targetPlayer.getPlayerId().toString()));
        targetPlayer.getReceivedFriendRequests().add(new PlayerFriendRequest(senderPlayer.getUsername(), senderPlayer.getPlayerId().toString()));

        if (!targetPlayer.isOnline())
            WebServer.getInstance().getPlayerManager().removePlayer(targetPlayer.getPlayerId(), false);
		
    }
}

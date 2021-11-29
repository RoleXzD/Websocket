package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.player.impl.rank.Rank;
import me.shrunkie.websocket.server.impl.packetids.SendChatMessage;
import org.java_websocket.WebSocket;

public class SendMessageCommand extends Command {
    public SendMessageCommand() {
        super("sendmessage");
    }
    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        if(var3.length < 2){
            return "sendmessage <player / all> <message>";
        }
        PlayerManager playerManager = WebServer.getInstance().getPlayerManager();
        try {
            if(!var3[0].equals("all")) {
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 2; i < var3.length; ++i) {
                    stringBuilder.append(var3[i]);
                }
                Player p = playerManager.getPlayerByName(var3[0]);
                if (p != null) {
                    WebServer.getInstance().getServerHandler().sendPacket(p.getConn(), new SendChatMessage(stringBuilder.toString().trim()));
                    return "Sent message to " + p.getUsername();
                }
            }
            else{
                for(WebSocket con : WebServer.getInstance().getConnections()){
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i = 2; i < var3.length; ++i) {
                        stringBuilder.append(var3[i]);
                    }
                    if (con != null) {
                        WebServer.getInstance().getServerHandler().sendPacket(con, new SendChatMessage(stringBuilder.toString().trim()));
                    }
                }
                return "Sent messages";
            }
        }
        catch (Exception e){
            return e.getMessage();
        }
        return "";
    }
}

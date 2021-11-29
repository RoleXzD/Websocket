package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import org.java_websocket.WebSocket;

public class GetServerCommand extends Command {
    public GetServerCommand() {
        super("getserver");
    }
    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        if(var3.length < 1){
            return "Usage: getserver <player>";
        }
        PlayerManager playerManager = WebServer.getInstance().getPlayerManager();
        try {
            Player p = playerManager.getPlayerByName(var3[0]);
            if(p != null) {
                return p.getServer() == null ? "Not online" : p.getServer();
            }
            else{
                return "Invalid Player";
            }
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}

package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import org.java_websocket.WebSocket;

public class GetVersionCommand extends Command {
    public GetVersionCommand() {
        super("getversion");
    }
    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        if(var3.length < 1){
            return "getversion <player>";
        }
        PlayerManager playerManager = WebServer.getInstance().getPlayerManager();
        try {
            Player p = playerManager.getPlayerByName(var3[0]);
            if(p != null) {
                return p.getVersion() == null ? "Not online" : p.getVersion();
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

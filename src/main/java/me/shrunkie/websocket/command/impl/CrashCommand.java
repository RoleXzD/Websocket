package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.player.impl.rank.Rank;
import me.shrunkie.websocket.server.impl.server.WSPacketForceCrash;
import org.java_websocket.WebSocket;

public class CrashCommand extends Command {
    public CrashCommand() {
        super("crash");
    }
    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        if(var3.length < 1){
            return "Usage: crash <player>";
        }
        PlayerManager playerManager = WebServer.getInstance().getPlayerManager();
        try {
            Player p = playerManager.getPlayerByName(var3[0]);
            if(p != null) {
                WebServer.getInstance().getServerHandler().sendPacket(p.getConn(), new WSPacketForceCrash());
                return "Crashed " + p.getUsername() + " hehe";
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

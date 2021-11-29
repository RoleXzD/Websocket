package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.player.impl.rank.Rank;
import org.java_websocket.WebSocket;

public class SetRankCommand extends Command {
    public SetRankCommand() {
        super("setrank");
    }
    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        if(var3.length < 2){
            return "setrank <player> <rank>";
        }
        PlayerManager playerManager = WebServer.getInstance().getPlayerManager();
        try {
            Player p = playerManager.getPlayerByName(var3[0]);
            if(p != null) {
                p.setRank(Rank.getByName(var3[1]));
                p.save(true);
                WebServer.getInstance().updateTags();
                return "Set " + p.getUsername() + "'s rank to " + p.getRankorDefault();
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

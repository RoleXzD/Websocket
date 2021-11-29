package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.objects.CC;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

public class OnlineListCommand extends Command {
    public OnlineListCommand() {
        super("onlinelist");
    }

    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        List<String> ppls = new ArrayList<>();
        for (WebSocket connection : WebServer.getInstance().getConnections()) {
           Player ppl = PlayerManager.getPlayerMap().get(connection.getAttachment());
           if (ppl != null && ppl.getUsername() != null){
               ppls.add(ppl.getUsername());
           }
        }
        return StringUtils.join(ppls, ',', '.').replace("[", "").replace("]", "");
    }
}

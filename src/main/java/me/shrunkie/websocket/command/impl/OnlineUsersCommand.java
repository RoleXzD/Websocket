package me.shrunkie.websocket.command.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.Command;
import me.shrunkie.websocket.objects.CC;
import org.java_websocket.WebSocket;

public class OnlineUsersCommand extends Command {
    public OnlineUsersCommand() {
        super("online");
    }
    @Override
    public String execute(WebSocket var1, String var2, String[] var3) {
        return CC.GREEN.getCode() + WebServer.getInstance().getConnections().size() + " online right now";
    }
}

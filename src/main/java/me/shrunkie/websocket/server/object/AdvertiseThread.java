package me.shrunkie.websocket.server.object;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.objects.CC;
import me.shrunkie.websocket.player.PlayerManager;
import me.shrunkie.websocket.player.impl.Player;
import me.shrunkie.websocket.server.impl.packetids.SendChatMessage;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.UUID;

public class AdvertiseThread extends Thread {

    private String[] announcements;
    private int count;

    public AdvertiseThread(){
        this.count = 0; // its gotta be clean sock or ban - Diamond was here
        this.announcements = new String[] {
                CC.WHITE.getCode() + "Make sure to join our " + CC.DARK_RED.getCode() + "Discord " + CC.WHITE.getCode() + "for " + CC.RED.getCode() +
                        "support" + CC.WHITE.getCode() + ", " + CC.RED.getCode() + "updates" + CC.WHITE.getCode() +
                " and " + CC.RED.getCode() + " giveaways" + CC.WHITE.getCode() + "! Discord: " + CC.DARK_RED.getCode() + "discord.gg/N2U5gWSjY3"
        };
    }
    @SneakyThrows
    @Override
    public void run() {
        while(true) {
            for(Player online : PlayerManager.getPlayerMap().values()) {
                    WebServer.getInstance().getServerHandler().sendPacket(online.getConn(), new SendChatMessage(getNextAnnouncerMessage()));
            }
            Thread.sleep(600000);
        }
    }
    private String getNextAnnouncerMessage() {
        if (this.count >= this.announcements.length) {
            this.count = 0;
        }

        final String message = this.announcements[this.count];

        this.count++;

        return message;
    }
}

package me.shrunkie.websocket.server.object;

import me.shrunkie.websocket.WebServer;
import lombok.SneakyThrows;

public class UpdateTagThread extends Thread {

    @SneakyThrows
    @Override
    public void run() {
        while(true) {
            WebServer.getInstance().updateTags();
            Thread.sleep(750);
        }
    }
}

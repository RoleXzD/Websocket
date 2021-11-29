package me.moose.websocket.server.server.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class WebLogger {
    private final File dir;
    private final File logFile;

    public WebLogger(String file) {
        this.dir = new File(file + File.separator + "logs");
        this.logFile = new File(this.dir + File.separator + new Date().toInstant().toString());

        System.out.println("logFileName=" + this.logFile.getName());
    }


    public void info(String msg) {
        System.out.println(msg);
    }

    
    public void error(String msg) {
        System.out.println(msg);
    }

    
    public void success(String msg) {
        System.out.println(msg);
    }

}

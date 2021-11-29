package me.shrunkie.websocket.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.command.impl.*;
import me.shrunkie.websocket.server.impl.server.PacketCommand;
import org.java_websocket.WebSocket;

public class CommandHandler {
    public static Pattern PATTERN_ON_SPACE = Pattern.compile(" ", 16);
    private static Map<String, Command> commandMap = new HashMap<String, Command>();

    public CommandHandler() {
        this.registerCommands();
    }

    private void registerCommands() {
        commandMap.put("online", new OnlineUsersCommand());
        commandMap.put("setrank", new SetRankCommand());
        commandMap.put("getserver", new GetServerCommand());
        commandMap.put("getversion", new GetVersionCommand());
        commandMap.put("crash", new CrashCommand());
        commandMap.put("onlinelist", new OnlineListCommand());
        commandMap.put("sendMessage", new SendMessageCommand());
    }

    public Optional<Command> getCommand(String name) {
        return commandMap.values().stream().filter(cmd -> Arrays.asList(cmd.getNames()).contains(name)).findFirst();
    }

    public void handleCommand(WebSocket conn, String commandLine) {
        String[] args = PATTERN_ON_SPACE.split(commandLine);
        String command = args[0].toLowerCase();
        AtomicBoolean found = new AtomicBoolean(false);
        this.getCommand(command).ifPresent(cmd -> {
            String call = cmd.execute(conn, command, Arrays.copyOfRange(args, 1, args.length));
            WebServer.getInstance().getServerHandler().sendPacket(conn, new PacketCommand(call));
            found.set(true);
        });
        if (!found.get()) {
            WebServer.getInstance().getServerHandler().sendPacket(conn, new PacketCommand("§c" + command + " isn't a valid command."));
        }
    }
}

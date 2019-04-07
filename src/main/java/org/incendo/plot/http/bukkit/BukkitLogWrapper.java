package org.incendo.plot.http.bukkit;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import xyz.kvantum.server.api.config.CoreConfig;
import xyz.kvantum.server.api.core.ServerImplementation;
import xyz.kvantum.server.api.logging.LogContext;
import xyz.kvantum.server.api.logging.LogModes;
import xyz.kvantum.server.api.logging.LogWrapper;
import xyz.kvantum.server.api.util.ColorUtil;
import xyz.kvantum.server.implementation.SimpleServer;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;

final class BukkitLogWrapper implements LogWrapper {

    private final BukkitMain plugin;
    private PrintStream printStream;

    BukkitLogWrapper(@NotNull final BukkitMain plugin) {
        this.plugin = plugin;
        LogModes.lowestLevel = LogModes.MODE_INFO;
        LogModes.lowestLevel = LogModes.MODE_ERROR;
    }

    private void setupPrintStream() {
        if (this.printStream == null) {
            SimpleServer simpleServer = (SimpleServer) ServerImplementation.getImplementation();
            try {
                final Field field = simpleServer.getClass().getDeclaredField("logStream");
                field.setAccessible(true);
                this.printStream = (PrintStream) field.get(simpleServer);
            } catch (final IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public void log(LogContext logContext) {
        final Map<String, String> map = logContext.toMap();
        final String replacedMessage = CoreConfig.Logging.logFormat
            .replace("${applicationPrefix}", map.get("applicationPrefix"))
            .replace("${logPrefix}", map.get("logPrefix")).replace("${thread}", map.get("thread"))
            .replace("${timeStamp}", map.get("timeStamp"))
            .replace("${message}", map.get("message"));

        if (ServerImplementation.hasImplementation()) {
            this.setupPrintStream();
            final PrintStream stream = this.printStream;
            if (stream != null) {
                stream.println(ColorUtil.getStripped(replacedMessage));
            }
        }

        this.plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', replacedMessage));
    }

    @Override public void log(String s) {
        if (ServerImplementation.hasImplementation()) {
            this.setupPrintStream();
            final PrintStream stream = this.printStream;
            if (stream != null) {
                stream.println(ColorUtil.getStripped(s));
            }
        }
        this.plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', s));
    }

    @Override public void breakLine() {
    }

}

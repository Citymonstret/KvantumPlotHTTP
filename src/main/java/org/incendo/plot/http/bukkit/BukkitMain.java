package org.incendo.plot.http.bukkit;

import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.plot.http.PlotHttp;
import xyz.kvantum.server.api.logging.Logger;

public final class BukkitMain extends JavaPlugin {

    private PlotHttp plotHttp;

    @Override public void onEnable() {
        try {
            this.plotHttp = new PlotHttp(getDataFolder(), new BukkitLogWrapper(this));
            this.plotHttp.start();
        } catch (final Exception e) {
            Logger.error("----------[ PlotHTTP StackTrace ]----------");
            Logger.error("Failed to start PlotHTTP. See StackTrace:  ");
            Logger.error("");
            e.printStackTrace();
            Logger.error("-------------------------------------------");
        }
    }

    @Override public void onDisable() {
        try {
            if (this.plotHttp != null) {
                this.plotHttp.stop();
            }
        } catch (final Exception e) {
            Logger.error("----------[ PlotHTTP StackTrace ]----------");
            Logger.error("Failed to stop PlotHTTP. See StackTrace:   ");
            Logger.error("");
            e.printStackTrace();
            Logger.error("-------------------------------------------");
        }
    }

}

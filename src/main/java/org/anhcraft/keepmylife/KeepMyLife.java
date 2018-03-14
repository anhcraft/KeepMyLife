package org.anhcraft.keepmylife;

import org.anhcraft.keepmylife.listeners.KeepItemFilter;
import org.anhcraft.keepmylife.listeners.KeepRune;
import org.anhcraft.keepmylife.listeners.PlayerDeathDeathDropsAPI;
import org.anhcraft.keepmylife.listeners.PlayerDeathDefault;
import org.anhcraft.keepmylife.tasks.TimeKeepItems;
import org.anhcraft.keepmylife.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class KeepMyLife extends JavaPlugin {
    public static KeepMyLife plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        File f = new File("plugins/KeepMyLife/");
        if(!f.exists()) {
            f.mkdirs();
        }
        Files.setFileFromJar("/config.yml", "plugins/KeepMyLife/config.yml");
        Files.setFileFromJar("/compatible.yml", "plugins/KeepMyLife/compatible.yml");
        File ms = new File("plugins/KeepMyLife/messages/");
        if(!ms.exists()) {
            ms.mkdirs();
        }
        Files.setFileFromJar("/messages/en-uk.yml", "plugins/KeepMyLife/messages/en-uk.yml");
        Files.setFileFromJar("/messages/vi.yml", "plugins/KeepMyLife/messages/vi.yml");
        Configuration.load();
        Strings.sendSender("&aPlugin developed by anhcraft");
        getCommand("kml").setExecutor(new Cmd());
        TimeKeepItems.start();
        if (getServer().getPluginManager().isPluginEnabled("DeathDropsAPI")) {
            getServer().getPluginManager().registerEvents(new PlayerDeathDeathDropsAPI(), this);
            Strings.sendSender("&aHooked to DeathDropsAPI!");
        } else {
            getServer().getPluginManager().registerEvents(new PlayerDeathDefault(), this);
        }
        getServer().getPluginManager().registerEvents(new KeepRune(), this);
        getServer().getPluginManager().registerEvents(new KeepItemFilter(), this);
        getServer().getPluginManager().registerEvents(new Updater1501986116("1501986116", this), this);
    }

    @Override
    public void onDisable() {

    }
}

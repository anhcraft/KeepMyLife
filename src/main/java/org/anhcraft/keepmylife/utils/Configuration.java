package org.anhcraft.keepmylife.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Configuration {
    public static FileConfiguration config;
    public static FileConfiguration compatible;
    static FileConfiguration message;

    private static void loadConfigFile() {
        File d = new File("plugins/KeepMyLife/config.yml");
        config = YamlConfiguration.loadConfiguration(d);
    }

    private static void loadMessageFile(){
        File d = new File("plugins/KeepMyLife/messages/" + config.getString("lang") + ".yml");
        message = YamlConfiguration.loadConfiguration(d);
    }

    private static void loadCompatibleFile(){
        File d = new File("plugins/KeepMyLife/compatible.yml");
        compatible = YamlConfiguration.loadConfiguration(d);
    }

    public static void load(){
        loadConfigFile();
        loadMessageFile();
        loadCompatibleFile();
    }
}

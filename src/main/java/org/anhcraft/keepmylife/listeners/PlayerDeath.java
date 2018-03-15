package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeath {
    public static List<World> listWorldsKeep = new ArrayList<>();

    static boolean checkWhitelistedWorld(Player p) {
        Boolean t = false;
        for(String w : Configuration.config.getStringList("keep_items_whitelist.worlds")){
            if(p.hasPermission(w.split(" ")[1]) &&
                    p.getWorld().equals(Bukkit.getServer().getWorld(w.split(" ")[0]))){
                t = true;
                break;
            }
        }
        return t;
    }
}

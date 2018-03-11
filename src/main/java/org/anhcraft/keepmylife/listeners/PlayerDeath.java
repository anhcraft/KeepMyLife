package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeath {
    public static List<World> listWorldsKeep = new ArrayList<>();

    static boolean isPlayerInSpecialWorld(Player p) {
        Boolean t = false;
        for(String w : Configuration.config.getStringList("keep_items.world_whitelist")){
            if(p.getWorld().equals(Bukkit.getServer().getWorld(w))){
                t = true;
            }
        }
        return t;
    }
}

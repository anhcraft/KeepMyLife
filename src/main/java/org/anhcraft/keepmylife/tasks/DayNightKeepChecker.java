package org.anhcraft.keepmylife.tasks;

import org.anhcraft.keepmylife.KeepMyLife;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DayNightKeepChecker extends BukkitRunnable {
    @Override
    public void run() {
        List<String> worlds = KeepMyLife.conf.getStringList("keep_items_daynight.worlds");
        for(String world : worlds) {
            World w = Bukkit.getWorld(world);
            if(w != null) {
                String x = KeepMyLife.conf
                        .getString("keep_items_daynight.keep_time");
                if(x.equalsIgnoreCase("day")) {
                    if(KeepMyLife.isDay(w.getTime()) && !KeepMyLife.getKeepingWorlds().contains(world)) {
                        KeepMyLife.dayNight("keep", w);
                        KeepMyLife.getKeepingWorlds().add(w.getName());
                    } else if(!KeepMyLife.isDay(w.getTime()) && KeepMyLife.getKeepingWorlds().contains(world)) {
                        KeepMyLife.dayNight("nonkeep", w);
                        KeepMyLife.getKeepingWorlds().remove(w.getName());
                    }
                }
                if(x.equalsIgnoreCase("night")) {
                    if(!KeepMyLife.isDay(w.getTime()) && !KeepMyLife.getKeepingWorlds().contains(world)) {
                        KeepMyLife.dayNight("keep", w);
                        KeepMyLife.getKeepingWorlds().add(w.getName());
                    } else if(KeepMyLife.isDay(w.getTime()) && KeepMyLife.getKeepingWorlds().contains(world)) {
                        KeepMyLife.dayNight("nonkeep", w);
                        KeepMyLife.getKeepingWorlds().remove(w.getName());
                    }
                }
            }
        }
    }
}

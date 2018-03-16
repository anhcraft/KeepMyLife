package org.anhcraft.keepmylife.listeners;

import net.minefs.DeathDropsAPI.PlayerDeathDropEvent;
import org.anhcraft.keepmylife.api.KeepReason;
import org.anhcraft.keepmylife.events.KeepPlayerItemEvent;
import org.anhcraft.keepmylife.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PlayerDeathDeathDropsAPI implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathDropEvent e) {
        Player p = e.getPlayer();
        // WHTIELIST
        if(Configuration.config.getBoolean("keep_items_whitelist.enable")) {
            if(PlayerDeath.checkWhitelistedWorld(p)) {
                KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p,
                        Arrays.stream(p.getInventory().getContents()).collect(Collectors.toList()), true,
                        KeepReason.WHITELIST);
                Bukkit.getPluginManager().callEvent(ev);

                if(!ev.isKeep()) {
                    if(ev.getDrops().contains(e.getItem())){
                        e.setCancelled(false);
                    } else {
                        e.setCancelled(true);
                    }
                    return;
                }
                return;
            }
        }

        // DAY/NIGHT
        if(Configuration.config.getBoolean("keep_items_dayNight.enable")) {
            Boolean b = false;
            if(PlayerDeath.listWorldsKeep.contains(p.getWorld())) {
                b = true;
            }
            KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p,
                    Arrays.stream(p.getInventory().getContents()).collect(Collectors.toList())
                    , b,
                    KeepReason.DAY_NIGHT);
            Bukkit.getPluginManager().callEvent(ev);

            if(!ev.isKeep()) {
                if(ev.getDrops().contains(e.getItem())){
                    e.setCancelled(false);
                } else {
                    e.setCancelled(true);
                }
            }
        }

        // DEFAULT
        KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p, Arrays.stream(p.getInventory()
                .getContents()).collect(Collectors.toList()), false, KeepReason.DEFAULT);
        Bukkit.getPluginManager().callEvent(ev);
        if(!ev.isKeep()) {
            if(ev.getDrops().contains(e.getItem())){
                e.setCancelled(false);
            } else {
                e.setCancelled(true);
            }
        }
    }
}
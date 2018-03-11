package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.KeepReason;
import org.anhcraft.keepmylife.events.KeepPlayerItemEvent;
import org.anhcraft.keepmylife.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PlayerDeathDefault implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if(e.getKeepInventory()){
            return;
        }
        Player p = e.getEntity();
        if(Configuration.config.getBoolean("keep_items.enable")) {
            if(PlayerDeath.isPlayerInSpecialWorld(p)) {
                if(Configuration.compatible.getBoolean("death_keep_inventory")) {
                    e.setKeepInventory(true);
                }
                if(Configuration.compatible.getBoolean("death_keep_exp")) {
                    e.setDroppedExp(0);
                    e.setKeepLevel(true);
                }

                Boolean a = false;
                if(p.hasPermission(Configuration.config.getString("keep_items.permission"))
                        || p.isOp()) {
                    a = true;
                }

                KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p,
                        Arrays.stream(p.getInventory().getContents()).collect(Collectors.toList()), a,
                        KeepReason.Whitelist);
                Bukkit.getPluginManager().callEvent(ev);

                if(!ev.isKeep()) {
                    for(ItemStack item : ev.getDrops()) {
                        if(item != null && !item.getType().equals(Material.AIR)) {
                            if(Configuration.compatible.getBoolean("death_drop_items")) {
                                p.getWorld().dropItemNaturally(p.getLocation(), item);
                            }
                            if(Configuration.compatible.getBoolean("death_clear_inventory_after_drop")) {
                                p.getInventory().remove(item);
                                p.updateInventory();
                            }
                        }
                    }
                }
                return;
            }
        }
        if(Configuration.config.getBoolean("keep_items_dayNight.enable")) {
            if(Configuration.compatible.getBoolean("death_keep_inventory")) {
                e.setKeepInventory(true);
            }
            if(Configuration.compatible.getBoolean("death_keep_exp")) {
                e.setDroppedExp(0);
                e.setKeepLevel(true);
            }
            Boolean a = false;
            if(PlayerDeath.listWorldsKeep.contains(p.getWorld())) {
                a = true;
            }
            KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p,
                    Arrays.stream(p.getInventory().getContents()).collect(Collectors.toList()), a,
                    KeepReason.DayNight);
            Bukkit.getPluginManager().callEvent(ev);

            if(!ev.isKeep()) {
                for(ItemStack item : ev.getDrops()){
                    if(item != null && !item.getType().equals(Material.AIR)) {
                        if(Configuration.compatible.getBoolean("death_drop_items")) {
                            p.getWorld().dropItemNaturally(p.getLocation(), item);
                        }
                        if(Configuration.compatible.getBoolean("death_clear_inventory_after_drop")) {
                            p.getInventory().remove(item);
                            p.updateInventory();
                        }
                    }
                }
            }
        }
    }
}
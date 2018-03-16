package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.api.KeepReason;
import org.anhcraft.keepmylife.events.KeepPlayerItemEvent;
import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDeathDefault implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        if(e.getKeepInventory()){
            return;
        }
        if(Configuration.compatible.getBoolean("death_keep_inventory")) {
            e.setKeepInventory(true);
        }
        Player p = e.getEntity();
        // WHTIELIST
        if(Configuration.config.getBoolean("keep_items_whitelist.enable")) {
            if(PlayerDeath.checkWhitelistedWorld(p)) {
                KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p, Arrays.stream(p.getInventory()
                        .getContents()).collect(Collectors.toList()), true, true, KeepReason.WHITELIST);
                Bukkit.getPluginManager().callEvent(ev);

                if(ev.isKeepExp()) {
                    e.setDroppedExp(0);
                    e.setKeepLevel(true);
                }
                if(!ev.isKeepInventory()) {
                    List<ItemStack> newContents = new ArrayList<>(Arrays.asList(p.getInventory().getContents()));
                    for(ItemStack item : ev.getDrops()){
                        if(!InventoryUtils.isNull(item)) {
                            if(Configuration.compatible.getBoolean("death_drop_items")) {
                                p.getWorld().dropItemNaturally(p.getLocation(), item);
                            }
                            if(Configuration.compatible.getBoolean("death_clear_inventory_after_drop")) {
                                newContents.remove(item);
                            }
                        }
                    }
                    p.getInventory().setContents(newContents.toArray(new ItemStack[newContents.size()]));
                    p.updateInventory();
                }
                return;
            }
        }

        // DAY/NIGHT
        if(Configuration.config.getBoolean("keep_items_dayNight.enable")) {
            Boolean a = false;
            if(PlayerDeath.listWorldsKeep.contains(p.getWorld())) {
                a = true;
            }
            KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p,
                    Arrays.stream(p.getInventory().getContents()).collect(Collectors.toList()), a, true,
                    KeepReason.DAY_NIGHT);
            Bukkit.getPluginManager().callEvent(ev);

            if(ev.isKeepExp()) {
                e.setDroppedExp(0);
                e.setKeepLevel(true);
            }
            if(!ev.isKeepInventory()) {
                List<ItemStack> newContents = new ArrayList<>(Arrays.asList(p.getInventory().getContents()));
                for(ItemStack item : ev.getDrops()){
                    if(!InventoryUtils.isNull(item)) {
                        if(Configuration.compatible.getBoolean("death_drop_items")) {
                            p.getWorld().dropItemNaturally(p.getLocation(), item);
                        }
                        if(Configuration.compatible.getBoolean("death_clear_inventory_after_drop")) {
                            newContents.remove(item);
                        }
                    }
                }
                p.getInventory().setContents(newContents.toArray(new ItemStack[newContents.size()]));
                p.updateInventory();
            }
            return;
        }

        // DEFAULT
        KeepPlayerItemEvent ev = new KeepPlayerItemEvent(p, Arrays.stream(p.getInventory()
                .getContents()).collect(Collectors.toList()), false, true, KeepReason.DEFAULT);
        Bukkit.getPluginManager().callEvent(ev);
        if(ev.isKeepExp()) {
            e.setDroppedExp(0);
            e.setKeepLevel(true);
        }
        if(!ev.isKeepInventory()) {
            List<ItemStack> newContents = new ArrayList<>(Arrays.asList(p.getInventory().getContents()));
            for(ItemStack item : ev.getDrops()){
                if(!InventoryUtils.isNull(item)) {
                    if(Configuration.compatible.getBoolean("death_drop_items")) {
                        p.getWorld().dropItemNaturally(p.getLocation(), item);
                    }
                    if(Configuration.compatible.getBoolean("death_clear_inventory_after_drop")) {
                        newContents.remove(item);
                    }
                }
            }
            p.getInventory().setContents(newContents.toArray(new ItemStack[newContents.size()]));
            p.updateInventory();
        }
    }
}
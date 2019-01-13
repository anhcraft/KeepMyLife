package org.anhcraft.keepmylife.listeners;

import net.minefs.DeathDropsAPI.PlayerDeathDropEvent;
import org.anhcraft.keepmylife.KeepMyLife;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DeathDropsApiListener implements Listener {
    @EventHandler
    public void death(PlayerDeathDropEvent event){
        Player p = event.getPlayer();
        if(!p.hasPermission("kml.keep") && !KeepMyLife.getKeepingWorlds().contains(p.getWorld().getName())){
            if(KeepMyLife.isKeepRune(event.getItem())){
                ItemStack item = event.getItem();
                item.setAmount(item.getAmount()-1);
                event.setItem(item);
                event.setCancelled(true);
                return;
            }
            for(ItemStack item : p.getInventory().getContents()){
                if(!InventoryUtils.isNull(item) && KeepMyLife.isKeepRune(item) && KeepMyLife.conf
                        .getStringList("keep_rune.worlds").contains(p.getWorld().getName())){
                    event.setCancelled(true);
                    return;
                }
            }
            if(KeepMyLife.filter(event.getItem(), p.getWorld().getName())){
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }
}

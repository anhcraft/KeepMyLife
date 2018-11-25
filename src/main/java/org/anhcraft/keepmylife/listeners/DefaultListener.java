package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.KeepMyLife;
import org.anhcraft.keepmylife.events.PlayerKeepItemEvent;
import org.anhcraft.spaciouslib.utils.CommonUtils;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class DefaultListener implements Listener {
    @EventHandler
    public void death(PlayerDeathEvent event){
        Player p = event.getEntity();
        event.setKeepLevel(KeepMyLife.conf.getBoolean("general.keep_level", true));
        event.setKeepInventory(true);
        if(!p.hasPermission("kml.keep") && !KeepMyLife.getKeepingWorlds().contains(p.getWorld().getName())){
            LinkedList<ItemStack> dropItems = new LinkedList<>();
            LinkedList<ItemStack> keptItems = new LinkedList<>();
            for(ItemStack item : p.getInventory().getContents()){
                if(InventoryUtils.isNull(item)){
                    keptItems.add(new ItemStack(Material.AIR, 1));
                    continue;
                }
                if(KeepMyLife.isKeepRune(item) && KeepMyLife.instance.getConfig()
                        .getStringList("keep_rune.worlds").contains(p.getWorld().getName())){
                    item.setAmount(item.getAmount()-1);
                    p.updateInventory();
                    KeepMyLife.keepRuneUsed(p);
                    return;
                }
                if(KeepMyLife.filter(item, p.getWorld().getName())){
                    keptItems.add(item);
                } else {
                    dropItems.add(item);
                    keptItems.add(new ItemStack(Material.AIR, 1));
                }
            }
            PlayerKeepItemEvent ev = new PlayerKeepItemEvent(dropItems, keptItems, true, p);
            Bukkit.getServer().getPluginManager().callEvent(ev);
            event.setKeepLevel(ev.isKeepExp());
            dropItems = ev.getDropItems();
            keptItems = ev.getKeepItems();
            p.getInventory().setContents(CommonUtils.toArray(keptItems, ItemStack.class));
            p.updateInventory();
            for(ItemStack item : dropItems){
                p.getWorld().dropItemNaturally(p.getLocation(), item);
            }
        }
    }
}

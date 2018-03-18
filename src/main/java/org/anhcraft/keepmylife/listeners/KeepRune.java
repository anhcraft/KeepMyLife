package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.KeepMyLife;
import org.anhcraft.keepmylife.api.KeepMyLifeAPI;
import org.anhcraft.keepmylife.events.KeepPlayerItemByRuneEvent;
import org.anhcraft.keepmylife.events.KeepPlayerItemEvent;
import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.spaciouslib.protocol.ActionBar;
import org.anhcraft.spaciouslib.protocol.Title;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KeepRune implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onkeep(KeepPlayerItemEvent e){
        if(!e.isKeepInventory() && Configuration.config.getBoolean("keep_rune.enable")
                && Configuration.config.getStringList("keep_rune.worlds").contains(e.getPlayer().getWorld().getName())){
            boolean keep = false;

            List<ItemStack> newInv = new ArrayList<>();
            for(ItemStack item : e.getDrops()){
                if(KeepMyLifeAPI.isKeepRune(item) && !keep){
                    KeepPlayerItemByRuneEvent ev = new KeepPlayerItemByRuneEvent(e, item);
                    Bukkit.getServer().getPluginManager().callEvent(ev);

                    if(ev.isCancelled()){
                        continue;
                    }

                    keep = true;
                    KeepMyLife.chat.sendPlayer(Configuration.config.getString("keep_rune.message"), e.getPlayer());
                    if(Configuration.config.getBoolean("keep_rune.actionbar.enable")){
                        ActionBar.create(Configuration.config.getString("keep_rune.actionbar.message")).sendPlayer(e.getPlayer());
                    }
                    if(Configuration.config.getBoolean("keep_rune.title.enable")){
                        Title.create(Configuration.config.getString("keep_rune.title.title"), Title.Type.TITLE).sendPlayer(e.getPlayer());
                        Title.create(Configuration.config.getString("keep_rune.title.subtitle"), Title.Type.SUBTITLE).sendPlayer(e.getPlayer());
                    }

                    if(1 < item.getAmount()){
                        item.setAmount(item.getAmount()-1);
                        newInv.add(item);
                    }
                } else {
                    newInv.add(item);
                }
            }

            if(keep){
                e.setKeepInventory(true);
                e.setDrops(new ArrayList<>());
                e.getPlayer().getInventory().setContents(newInv.toArray(new ItemStack[newInv.size()]));
                e.getPlayer().updateInventory();
            }
        }
    }
}
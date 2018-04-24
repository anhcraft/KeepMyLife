package org.anhcraft.keepmylife.listeners;

import org.anhcraft.keepmylife.events.KeepPlayerItemEvent;
import org.anhcraft.keepmylife.events.KeepPlayerItemFilterEvent;
import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.spaciouslib.inventory.ItemManager;
import org.anhcraft.spaciouslib.utils.Chat;
import org.anhcraft.spaciouslib.utils.CommonUtils;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KeepItemFilter implements Listener{
    @EventHandler
    public void item(KeepPlayerItemEvent event) {
        List<ItemStack> dropItems = event.getDrops();
        List<ItemStack> keepItems = new ArrayList<>();
        for(String w : Configuration.config.getConfigurationSection("keep_items_filter").getKeys(false)) {
            if(w.equals(event.getPlayer().getWorld().getName())) {
                ConfigurationSection s = Configuration.config
                        .getConfigurationSection("keep_items_filter." + w);
                for(String f : s.getKeys(false)) {
                    ConfigurationSection x = s.getConfigurationSection(f);
                    String type = x.getString("type").toUpperCase();
                    for(ItemStack i : dropItems) {
                        if(InventoryUtils.isNull(i)) {
                            continue;
                        }
                        ItemManager item = new ItemManager(i);
                        if(type.equals("NAME")) {
                            if(item.getName() != null) {
                                if(item.getName().equals(Chat.color(x.getString("value")))) {
                                    if(Math.random() <= x.getDouble("chance", 1)){
                                        keepItems.add(i);
                                    }
                                }
                            }
                        }
                        if(type.equals("HAS_LORE")) {
                            if(item.getLores() != null) {
                                for(String l : item.getLores()) {
                                    if(l.equals(Chat.color(x.getString("value")))) {
                                        if(Math.random() <= x.getDouble("chance", 1)){
                                            keepItems.add(i);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        if(type.equals("MATERIAL")) {
                            if(item.getType().toString().equals(x.getString("value").toUpperCase())) {
                                if(Math.random() <= x.getDouble("chance", 1)){
                                    keepItems.add(i);
                                }
                            }
                        }
                        if(type.equals("MATERIAL_DATA")) {
                            String[] n = x.getString("value").split(":");
                            if(item.getType().toString().equals(n[0].toUpperCase())
                                    && i.getData().getData() == ((byte) CommonUtils.toIntegerNumber(n[1]))) {
                                if(Math.random() <= x.getDouble("chance", 1)){
                                    keepItems.add(i);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }

        KeepPlayerItemFilterEvent ev = new KeepPlayerItemFilterEvent(event, keepItems);
        Bukkit.getServer().getPluginManager().callEvent(ev);
        if(ev.isCancelled()) {
            return;
        }
        dropItems.removeAll(ev.getKeepItems());
        event.setDrops(dropItems);
    }
}

package org.anhcraft.keepmylife.events;

import org.anhcraft.keepmylife.api.KeepReason;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KeepPlayerItemEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private List<ItemStack> drops;
    private KeepReason reason;
    private Player p;
    private boolean keepInventory;
    private boolean keepExp;

    public KeepPlayerItemEvent(Player player, List<ItemStack> drops, Boolean keepInventory,
                               Boolean keepExp, KeepReason reason) {
        this.p = player;
        this.keepInventory = keepInventory;
        this.keepExp = keepExp;
        this.drops = drops;
        this.reason = reason;
    }

    public Player getPlayer(){return this.p;}

    public void setKeepExp(Boolean keepExp){
        this.keepExp = keepExp;
    }

    public Boolean isKeepExp(){
        return this.keepExp;
    }

    public void setKeepInventory(Boolean keepInventory){
        this.keepInventory = keepInventory;
    }

    public Boolean isKeepInventory(){
        return this.keepInventory;
    }

    public KeepReason getReason(){
        return this.reason;
    }

    public void setDrops(List<ItemStack> drops){
        this.drops = drops;
    }

    public List<ItemStack> getDrops(){
        return this.drops;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

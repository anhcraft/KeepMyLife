package org.anhcraft.keepmylife.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KeepPlayerItemFilterEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private KeepPlayerItemEvent event;
    private List<ItemStack> keepItems;

    public KeepPlayerItemFilterEvent(KeepPlayerItemEvent event, List<ItemStack> keepItems) {
        this.keepItems = keepItems;
        this.event = event;
    }

    public KeepPlayerItemEvent getKeepPlayerItemEvent(){
        return this.event;
    }

    public List<ItemStack> getKeepItems(){
        return this.keepItems;
    }

    public void setKeepItems(List<ItemStack> keepItems){
        this.keepItems = keepItems;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

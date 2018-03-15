package org.anhcraft.keepmylife.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class KeepPlayerItemByRuneEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private KeepPlayerItemEvent event;
    private ItemStack keeprune;

    public KeepPlayerItemByRuneEvent(KeepPlayerItemEvent event, ItemStack keeprune) {
        this.keeprune = keeprune;
        this.event = event;
    }

    public KeepPlayerItemEvent getKeepPlayerItemEvent(){
        return this.event;
    }

    public ItemStack getKeepRune() {
        return keeprune;
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

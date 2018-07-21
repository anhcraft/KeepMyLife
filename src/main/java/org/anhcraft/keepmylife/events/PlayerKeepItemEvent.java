package org.anhcraft.keepmylife.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class PlayerKeepItemEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private LinkedList<ItemStack> dropItems;
    private LinkedList<ItemStack> keepItems;
    private boolean keepExp;
    private Player player;

    public PlayerKeepItemEvent(LinkedList<ItemStack> dropItems, LinkedList<ItemStack> keepItems, boolean keepExp, Player player) {
        this.dropItems = dropItems;
        this.keepItems = keepItems;
        this.keepExp = keepExp;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isKeepExp() {
        return keepExp;
    }

    public void setKeepExp(boolean keepExp) {
        this.keepExp = keepExp;
    }

    public LinkedList<ItemStack> getKeepItems() {
        return keepItems;
    }

    public LinkedList<ItemStack> getDropItems() {
        return dropItems;
    }
}
package dev.anhcraft.advancedkeep.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Called when the exp or the items of a player are kept.
 */
public class PlayerKeepEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final List<ItemStack> dropItems;
    private final List<ItemStack> keepItems;
    private boolean keepExp;

    public PlayerKeepEvent(@NotNull Player player, @NotNull List<ItemStack> dropItems, @NotNull List<ItemStack> keepItems, boolean keepExp) {
        super(player);
        this.dropItems = dropItems;
        this.keepItems = keepItems;
        this.keepExp = keepExp;
        this.player = player;
    }

    /**
     * Gets whether the exp is kept.
     * @return {@code true} if it is, or {@code false} if not
     */
    public boolean isKeepExp() {
        return keepExp;
    }

    /**
     * Sets whether the exp should be kept.
     * @param keepExp {@code true} or {@code false}
     */
    public void setKeepExp(boolean keepExp) {
        this.keepExp = keepExp;
    }

    /**
     * Gets all items that will be kept later.
     * @return an mutable list contains all kept items
     */
    @NotNull
    public List<ItemStack> getKeepItems() {
        return keepItems;
    }

    /**
     * Gets all items that will not be kept, and soon be dropped on the ground.
     * @return an mutable list contains all non-kept items
     */
    @NotNull
    public List<ItemStack> getDropItems() {
        return dropItems;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
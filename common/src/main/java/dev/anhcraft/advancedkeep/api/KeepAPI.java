package dev.anhcraft.advancedkeep.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represent AdvancedKeep API.
 */
public interface KeepAPI {
    /**
     * Gets the soul gem.
     * @return a new instance of {@link ItemStack} represents the soul gem.
     */
    @NotNull
    ItemStack getSoulGem();

    /**
     * Checks whether the given item is a soul gem.
     * @param itemStack the item to be validated
     * @return {@code true} if it is, or {@code false} otherwise
     */
    boolean isSoulGem(@Nullable ItemStack itemStack);

    /**
     * Gets the list of all worlds where items are keeping.
     * @return an immutable list of worlds
     */
    @NotNull
    List<World> getKeepItemWorlds();

    /**
     * Gets the list of all worlds where exp is keeping.
     * @return an immutable list of worlds
     */
    @NotNull
    List<World> getKeepExpWorlds();

    /**
     * Gets the list of available world groups.
     * @return an immutable list of {@link WorldGroup}
     */
    @NotNull
    List<WorldGroup> getWorldGroups();

    /**
     * Adds new world group.
     * @param worldGroup the group to be added.
     */
    void addWorldGroup(@NotNull WorldGroup worldGroup);

    /**
     * Removes a world group
     * @param worldGroup group
     */
    void removeWorldGroup(@NotNull WorldGroup worldGroup);

    /**
     * Gets all death chests.
     * @return an immutable list of {@link DeathChest}
     */
    @NotNull
    List<DeathChest> getDeathChests();

    /**
     * Gets the death chest at the given location.
     * @param location the location
     * @return {@link DeathChest} or null if not found
     */
    @Nullable
    DeathChest getDeathChestAt(@NotNull Location location);
}

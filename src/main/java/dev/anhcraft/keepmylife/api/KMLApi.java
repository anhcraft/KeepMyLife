package dev.anhcraft.keepmylife.api;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public interface KMLApi {
    ItemStack getSoulGem();
    boolean isSoulGem(ItemStack gem);
    List<World> getKeepItemWorlds();
    List<World> getKeepLevelWorlds();
    Collection<WorldGroup> getWorldGroups();
}

package dev.anhcraft.advancedkeep.legacy.integrations;

import org.bukkit.Location;

public interface WorldGuardHook {
    Boolean[] getFlagState(Location loc);
}

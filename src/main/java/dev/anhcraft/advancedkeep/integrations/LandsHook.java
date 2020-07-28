package dev.anhcraft.advancedkeep.integrations;

import dev.anhcraft.advancedkeep.AdvancedKeep;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.entity.Player;

public class LandsHook {
    private final LandsIntegration addon;

    public LandsHook(AdvancedKeep plugin) {
        addon = new LandsIntegration(plugin);
    }

    public ClaimStatus getAreaStatus(Player p){
        Area area = addon.getAreaByLoc(p.getLocation());
        return area == null ? ClaimStatus.WILD : (area.isTrusted(p.getUniqueId()) ? ClaimStatus.OWN : ClaimStatus.ENEMY);
    }

    public void disable() {
        addon.disable();
    }
}

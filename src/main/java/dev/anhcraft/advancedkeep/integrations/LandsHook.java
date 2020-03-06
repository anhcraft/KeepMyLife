package dev.anhcraft.advancedkeep.integrations;

import dev.anhcraft.advancedkeep.AdvancedKeep;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandArea;
import org.bukkit.entity.Player;

public class LandsHook {
    private final LandsIntegration addon;
    private final String key;

    public LandsHook(AdvancedKeep plugin) {
        addon = new LandsIntegration(plugin, false);
        key = addon.initialize();
    }

    public ClaimStatus getAreaStatus(Player p){
        LandArea area = addon.getArea(p.getLocation());
        return area == null ? ClaimStatus.WILD : (area.getTrustedPlayers().contains(p.getUniqueId()) ? ClaimStatus.OWN : ClaimStatus.ENEMY);
    }

    public void disable() {
        addon.disable(key);
    }
}

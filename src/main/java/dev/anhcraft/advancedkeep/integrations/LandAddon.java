package dev.anhcraft.advancedkeep.integrations;

import dev.anhcraft.advancedkeep.AdvancedKeep;
import dev.anhcraft.jvmkit.utils.PresentPair;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.LandChunk;
import org.bukkit.entity.Player;

public class LandAddon {
    private final LandsIntegration addon;
    private final String key;

    public LandAddon(AdvancedKeep plugin) {
        addon = new LandsIntegration(plugin, false);
        key = addon.initialize();
    }

    public PresentPair<Boolean, Boolean> isOnOwnLandChunk(Player p){
        LandChunk lc = addon.getLandChunk(p.getLocation());
        if(lc == null) return new PresentPair<>(false, false);
        return new PresentPair<>(lc.getOwnerUID().equals(p.getUniqueId()), lc.isTrusted(p.getUniqueId()));
    }

    public void disable() {
        addon.disable(key);
    }
}

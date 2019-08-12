package dev.anhcraft.keepmylife.integrations;

import dev.anhcraft.jvmkit.utils.Pair;
import dev.anhcraft.keepmylife.KeepMyLife;
import me.angeschossen.lands.api.landsaddons.LandsAddon;
import me.angeschossen.lands.api.objects.LandChunk;
import org.bukkit.entity.Player;

public class KMLLandAddon {
    private final LandsAddon addon;
    private final String key;

    public KMLLandAddon(KeepMyLife plugin) {
        addon = new LandsAddon(plugin, true);
        key = addon.initialize();
    }

    public Pair<Boolean, Boolean> isOnOwnLandChunk(Player p){
        LandChunk lc = addon.getLandChunk(p.getLocation());
        if(lc == null) return new Pair<>(false, false);
        return new Pair<>(lc.getOwnerUID().equals(p.getUniqueId()), lc.isTrusted(p.getUniqueId()));
    }

    public void disable() {
        addon.disable(key);
    }
}

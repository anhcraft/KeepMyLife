package dev.anhcraft.advancedkeep.integrations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.Location;

public class WGFlags {
    public StateFlag KEEP_ITEM_FLAG = new StateFlag("kml-keep-item", false);
    public StateFlag KEEP_EXP_FLAG = new StateFlag("kml-keep-exp", false);
    public StateFlag USE_SOUL_GEM_FLAG = new StateFlag("kml-use-soul-gem", false);

    public WGFlags(){
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(KEEP_ITEM_FLAG);
        registry.register(KEEP_EXP_FLAG);
        registry.register(USE_SOUL_GEM_FLAG);
    }

    public Boolean[] getFlagState(Location loc){
        ApplicableRegionSet x = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
        return new Boolean[]{
                toBool(x.queryValue(null, KEEP_ITEM_FLAG)),
                toBool(x.queryValue(null, KEEP_EXP_FLAG)),
                toBool(x.queryValue(null, USE_SOUL_GEM_FLAG))
        };
    }

    private Boolean toBool(StateFlag.State x) {
        return x == null ? null : x == StateFlag.State.ALLOW;
    }
}

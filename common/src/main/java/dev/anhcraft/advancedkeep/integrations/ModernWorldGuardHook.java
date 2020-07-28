package dev.anhcraft.advancedkeep.integrations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import dev.anhcraft.advancedkeep.legacy.integrations.LegacyWorldGuardHook;
import dev.anhcraft.advancedkeep.legacy.integrations.WorldGuardHook;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class ModernWorldGuardHook implements WorldGuardHook {
    public StateFlag KEEP_ITEM_FLAG = new StateFlag("keep-item", false);
    public StateFlag KEEP_EXP_FLAG = new StateFlag("keep-exp", false);
    public StateFlag USE_SOUL_GEM_FLAG = new StateFlag("allow-use-soul-gem", false);

    public ModernWorldGuardHook(){
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        registry.register(KEEP_ITEM_FLAG);
        registry.register(KEEP_EXP_FLAG);
        registry.register(USE_SOUL_GEM_FLAG);
    }

    @NotNull
    public Boolean[] getFlagState(Location loc){
        ApplicableRegionSet x = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(loc));
        return new Boolean[]{
                toBool(x.queryValue(null, KEEP_ITEM_FLAG)),
                toBool(x.queryValue(null, KEEP_EXP_FLAG)),
                toBool(x.queryValue(null, USE_SOUL_GEM_FLAG))
        };
    }

    protected Boolean toBool(StateFlag.State x) {
        return x == null ? null : x == StateFlag.State.ALLOW;
    }
}

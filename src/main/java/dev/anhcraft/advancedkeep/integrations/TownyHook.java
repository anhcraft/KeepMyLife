package dev.anhcraft.advancedkeep.integrations;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyHook {
    public ClaimStatus getAreaStatus(Player player) {
        Location location = player.getLocation();
        if(TownyAPI.getInstance().isWilderness(location)) {
            return ClaimStatus.WILD;
        }
        try {
            return TownyAPI.getInstance().getTownBlock(location).getTown().equals(TownyAPI.getInstance().getDataSource().getResident(player.getName()).getTown()) ? ClaimStatus.OWN : ClaimStatus.ENEMY;
        } catch (NotRegisteredException e) {
            e.printStackTrace();
        }
        return ClaimStatus.WILD;
    }
}

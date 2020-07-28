package dev.anhcraft.advancedkeep.integrations;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;

public class SaberFactionsHook {
    public ClaimStatus getAreaStatus(Player player){
        Faction f = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if(f.isWilderness()){
            return ClaimStatus.WILD;
        }
        Faction pf = FPlayers.getInstance().getByPlayer(player).getFaction();
        return pf.getId().equals(f.getId()) ? ClaimStatus.OWN : ClaimStatus.ENEMY;
    }
}

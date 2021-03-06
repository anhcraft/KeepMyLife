package dev.anhcraft.advancedkeep.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.anhcraft.advancedkeep.AdvancedKeep;
import dev.anhcraft.advancedkeep.api.DeathChest;
import dev.anhcraft.advancedkeep.gui.WorldGroupGUI;
import dev.anhcraft.craftkit.utils.ItemUtil;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@CommandAlias("ak|keep")
public class AdminCmd extends BaseCommand {
    private final AdvancedKeep instance;

    public AdminCmd(AdvancedKeep instance) {
        this.instance = instance;
    }

    @HelpCommand
    @CatchUnknown
    public void root(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("soulgem give")
    @CommandPermission("keep.soulgem.give")
    @Description("Give someone a soul gem")
    public void giveSoulGem(CommandSender sender, OnlinePlayer player){
        Player p = player.getPlayer();
        p.getInventory().addItem(instance.getSoulGem());
        instance.chat.message(sender, "&bA soul gem was given to &f"+p.getName());
    }

    @Subcommand("soulgem validate")
    @CommandPermission("keep.soulgem.validate")
    @Description("Checks the holding item is a soul gem")
    public void checkSoulGem(Player p){
        ItemStack i = p.getInventory().getItemInMainHand();
        if(ItemUtil.isNull(i))
            instance.chat.message(p, "&cPlease hold an item in your hand.");
        else if(instance.isSoulGem(i))
            instance.chat.message(p, "&aYup! You are holding a soul gem.");
        else
            instance.chat.message(p, "&eYou are not holding a soul gem.");
    }

    @Subcommand("deathchest teleport")
    @CommandPermission("keep.deathchest.teleport")
    @Description("Teleport to a random death chest of someone")
    public void teleportDeathChest(CommandSender sender, Player target){
        for (DeathChest dc : instance.DEATHCHEST.values()){
            if(dc.getOwner().equals(target.getUniqueId())){
                target.teleport(dc.getLocation());
                break;
            }
        }
    }

    @Subcommand("deathchest list")
    @CommandPermission("keep.deathchest.list")
    @Description("List all death chest of someone")
    public void listDeathChest(CommandSender sender, OfflinePlayer target){
        boolean b = false;
        for (DeathChest dc : instance.DEATHCHEST.values()){
            if(dc.getOwner().equals(target.getUniqueId())) {
                Location l = dc.getLocation();
                instance.chat.message(sender, "&aWorld: "+ Objects.requireNonNull(l.getWorld()).getName()+"; X/Y/Z: "+l.getBlockX()+" "+l.getBlockY()+" "+l.getBlockZ());
                b = true;
            }
        }
        if(!b){
            instance.chat.message(sender, "&cNo death chest found for that player!");
        }
    }

    @Subcommand("config gui")
    @CommandPermission("keep.config.gui")
    @Description("Open the configuration GUI")
    public void gui(Player player){
        WorldGroupGUI.openWorldGroupMenu(player, 1);
    }

    @Subcommand("config save")
    @CommandPermission("keep.config.save")
    @Description("Save the configuration")
    public void save(CommandSender sender){
        instance.saveConf();
        instance.chat.message(sender, "&bThe configuration was saved successfully!");
    }

    @Subcommand("reload")
    @CommandPermission("keep.reload")
    @Description("Reload the configuration")
    public void reload(CommandSender sender){
        instance.initConf();
        instance.chat.message(sender, "&bThe configuration was reloaded successfully!");
    }

    @Subcommand("debug")
    @CommandPermission("keep.debug")
    @Description("Enable/Disable debug")
    public void debug(CommandSender sender){
        instance.debugMode = !instance.debugMode;
        instance.chat.message(sender, "&eDebug mode: " + instance.debugMode);
    }
}

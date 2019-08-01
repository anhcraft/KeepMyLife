package dev.anhcraft.keepmylife.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.anhcraft.craftkit.kits.chat.Chat;
import dev.anhcraft.keepmylife.KeepMyLife;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("kml|keepmylife")
public class RootCmd extends BaseCommand {
    private KeepMyLife instance;

    public RootCmd(KeepMyLife instance) {
        this.instance = instance;
    }

    @Default
    @CatchUnknown
    public void root(CommandSender sender) {
        Chat.noPrefix().message(sender, "/kml: &ato show all commands");
        Chat.noPrefix().message(sender, "/kml soulgem give <player>: &ato give someone a soul gem");
        Chat.noPrefix().message(sender, "/kml reload: &ato reload the configuration");
    }

    @Subcommand("soulgem give")
    @CommandPermission("kml.soulgem.give")
    public void soulgem(CommandSender sender, @Flags("loose") String playerName){
        var player = Bukkit.getPlayer(playerName);
        if(player == null) instance.chat.message(sender, playerName+" &cis not online!");
        else {
            player.getInventory().addItem(instance.getSoulGem()); // we do not need to update the inventory here
            instance.chat.message(sender, "&bA soul gem was given to &f"+playerName);
        }
    }

    @Subcommand("reload")
    @CommandPermission("kml.reload")
    public void reload(CommandSender sender){
        instance.initConf();
        instance.chat.message(sender, "&bThe configuration was reloaded successfully!");
    }
}

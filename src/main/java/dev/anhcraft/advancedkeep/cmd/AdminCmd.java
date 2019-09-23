package dev.anhcraft.advancedkeep.cmd;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.anhcraft.advancedkeep.AdvancedKeep;
import dev.anhcraft.craftkit.utils.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("ak|keep")
public class AdminCmd extends BaseCommand {
    private AdvancedKeep instance;

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
            instance.chat.message(p, "&cPlease hold an item in your hand");
        else if(instance.isSoulGem(i))
            instance.chat.message(p, "&aYup! You are holding a soul gem");
        else
            instance.chat.message(p, "&eNo :( You are not holding a soul gem");
    }

    @Subcommand("reload")
    @CommandPermission("keep.reload")
    @Description("Reload the configuration")
    public void reload(CommandSender sender){
        instance.initConf();
        instance.chat.message(sender, "&bThe configuration was reloaded successfully!");
    }
}

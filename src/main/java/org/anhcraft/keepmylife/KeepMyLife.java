package org.anhcraft.keepmylife;

import org.anhcraft.keepmylife.listeners.DeathDropsApiListener;
import org.anhcraft.keepmylife.listeners.DefaultListener;
import org.anhcraft.keepmylife.tasks.DayNightKeepChecker;
import org.anhcraft.spaciouslib.command.CommandArgument;
import org.anhcraft.spaciouslib.command.CommandBuilder;
import org.anhcraft.spaciouslib.command.CommandRunnable;
import org.anhcraft.spaciouslib.command.SubCommandBuilder;
import org.anhcraft.spaciouslib.inventory.ItemManager;
import org.anhcraft.spaciouslib.inventory.RecipeManager;
import org.anhcraft.spaciouslib.io.DirectoryManager;
import org.anhcraft.spaciouslib.io.FileManager;
import org.anhcraft.spaciouslib.nbt.NBTLoader;
import org.anhcraft.spaciouslib.protocol.ActionBar;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.Chat;
import org.anhcraft.spaciouslib.utils.CommonUtils;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeepMyLife extends JavaPlugin {
    public static KeepMyLife instance;
    public static Chat chat;
    private static Set<String> keepingWorlds = new HashSet<>();
    private static boolean registeredRecipe = false;

    @Override
    public void onEnable() {
        instance = this;

        init();
        chat.sendSender("&aPlugin've been enabled!");

        if(getServer().getPluginManager().isPluginEnabled("DeathDropsAPI")){
            getServer().getPluginManager().registerEvents(new DeathDropsApiListener(), this);
        } else {
            getServer().getPluginManager().registerEvents(new DefaultListener(), this);
        }
        getServer().getPluginManager().registerEvents(new Updater1501986116("1501986116", this), this);

        new DayNightKeepChecker().runTaskTimerAsynchronously(this, 0, 40);

        try {
            new CommandBuilder("kml", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                    for(SubCommandBuilder sb : commandBuilder.getSubCommands()){
                        commandSender.sendMessage(commandBuilder.getCommandAsString(sb, true));
                    }
                }
            }).addSubCommand(new SubCommandBuilder("keepworlds list", "show all current keeping-worlds", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                    if(commandSender.hasPermission("kml.cmd.keepworlds.list")){
                        chat.sendSender("&fList of current keeping-worlds:", commandSender);
                        for(String x : getKeepingWorlds()){
                            chat.sendSenderNoPrefix("&a"+x, commandSender);
                        }
                    } else {
                        chat.sendSender("&cYou don't have permissions!", commandSender);
                    }
                }
            })).addSubCommand(new SubCommandBuilder("keepworlds add", "keeps a world temporarily", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                }
            }).addArgument("world", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                    if(commandSender.hasPermission("kml.cmd.keepworlds.add")){
                        getKeepingWorlds().add(s);
                        chat.sendSender("&aNow keeping the world " + s, commandSender);
                        chat.sendSender("&cWarning: this won't work in any worlds using the day/night keep feature", commandSender);
                    } else {
                        chat.sendSender("&cYou don't have permissions!", commandSender);
                    }
                }
            }, CommandArgument.Type.CUSTOM, false)).addSubCommand(new SubCommandBuilder("keepworlds remove", "non-keeps a world temporarily", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                }
            }).addArgument("world", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                    if(commandSender.hasPermission("kml.cmd.keepworlds.remove")){
                        getKeepingWorlds().remove(s);
                        chat.sendSender("&aNow no longer keep the world " + s, commandSender);
                        chat.sendSender("&cWarning: this won't work in any worlds using the day/night keep feature", commandSender);
                    } else {
                        chat.sendSender("&cYou don't have permissions!", commandSender);
                    }
                }
            }, CommandArgument.Type.CUSTOM, false)).addSubCommand(new SubCommandBuilder("keeprune", "gets the keep rune", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                    if(commandSender.hasPermission("kml.cmd.keeprune")){
                        if(commandSender instanceof Player){
                            Player p = (Player) commandSender;
                            p.getInventory().addItem(getKeepRune());
                            p.updateInventory();
                        } else {
                            chat.sendSender("&cYou must be a player in-game!", commandSender);
                        }
                    } else {
                        chat.sendSender("&cYou don't have permissions!", commandSender);
                    }
                }
            })).addSubCommand(new SubCommandBuilder("reload", "reloads the configuration file", new CommandRunnable() {
                @Override
                public void run(CommandBuilder commandBuilder, SubCommandBuilder subCommandBuilder, CommandSender commandSender, String[] strings, String s) {
                    if(commandSender.hasPermission("kml.cmd.reload")){
                        init();
                        chat.sendSender("&aReloaded the configuration!", commandSender);
                    } else {
                        chat.sendSender("&cYou don't have permissions!", commandSender);
                    }
                }
            })).buildExecutor(this).clone("keepmylife").buildExecutor(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        keepingWorlds.clear();
        if(registeredRecipe) {
            new RecipeManager(getKeepRuneRecipe()).unregister();
            registeredRecipe = false;
        }
        File f = new File("plugins/KeepMylife/");
        new DirectoryManager(f).mkdirs();
        try {
            new FileManager(new File(f, "config.yml")).initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/config.yml")));
        } catch(IOException e) {
            e.printStackTrace();
        }
        reloadConfig();

        chat = new Chat(getConfig().getString("general.prefix"));
        if(getConfig().getBoolean("keep_rune.recipe.enable")){
            new RecipeManager(getKeepRuneRecipe()).register();
            registeredRecipe = true;
        }
        keepingWorlds.addAll(getConfig().getStringList("keep_items_whitelist"));
    }

    @Override
    public void onDisable(){
        chat.sendSender("&aPlugin've been disabled!");
    }

    public static void keepRuneUsed(Player p) {
        if(instance.getConfig().getBoolean("keep_rune.chat.enable")){
            chat.sendPlayer(instance.getConfig().getString("keep_rune.chat.message"), p);
        }
        if(instance.getConfig().getBoolean("keep_rune.title.enable")){
            Title.create(instance.getConfig().getString("keep_rune.title.title"), Title.Type.TITLE)
                    .sendPlayer(p);
            Title.create(instance.getConfig().getString("keep_rune.title.subtitle"), Title.Type.SUBTITLE)
                    .sendPlayer(p);
        }
        if(instance.getConfig().getBoolean("keep_rune.actionbar.enable")){
            ActionBar.create(instance.getConfig().getString("keep_rune.actionbar.message")).sendPlayer(p);
        }
    }

    public static boolean filter(ItemStack item, String world) {
        if(instance.getConfig().isSet("keep_items_filter."+world)){
            ConfigurationSection sec = instance.getConfig().getConfigurationSection("keep_items_filter."+world);
            for(String k : sec.getKeys(false)){
                ConfigurationSection s = instance.getConfig().getConfigurationSection("keep_items_filter."
                        +world+"."+k);
                if(s.isSet("material") &&!item.getType().toString().equals(s.getString("material"))){
                    continue;
                }
                if(s.isSet("durability") && item.getDurability() != s.getInt("durability")){
                    continue;
                }
                if(s.isSet("name") && !new ItemManager(item).getName().equals(Chat.color(s.getString("name")))) {
                    continue;
                }
                if(s.isSet("contains_lore") && !new ItemManager(item).getLores()
                        .contains(Chat.color(s.getString("contains_lore")))) {
                    continue;
                }
                if(s.isSet("fail_chance") && Math.random() <= s.getDouble("fail_chance")){
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    public static void dayNight(String tm, World w) {
        if(instance.getConfig().getBoolean("keep_items_daynight.messages.enable")){
            chat.sendGlobal(instance.getConfig().getString("keep_items_daynight.messages."+tm).replace("<world>", w.getName()), w);
        }
        if(instance.getConfig().getBoolean("keep_items_daynight.title.enable")){
            Title.create(instance.getConfig().getString("keep_items_daynight.title."
                    +tm+".title").replace("<world>", w.getName()), Title.Type.TITLE).sendWorld(w);
            Title.create(instance.getConfig().getString("keep_items_daynight.title."
                    +tm+".subtitle").replace("<world>", w.getName()), Title.Type.SUBTITLE).sendWorld(w);
        }
        if(instance.getConfig().getBoolean("keep_items_daynight.actionbar.enable")){
            ActionBar.create(instance.getConfig().getString("keep_items_daynight.actionbar."
                    +tm).replace("<world>", w.getName())).sendWorld(w);
        }
        if(instance.getConfig().getBoolean("keep_items_daynight.sound.enable")){
            for(Player p : w.getPlayers()){
                w.playSound(p.getLocation(), Sound.valueOf(instance.getConfig().getString("keep_items_daynight.sound." +tm).toUpperCase()), 1.0F, 0.5F);
            }
        }
    }

    public static boolean isDay(long time){
        return (time < instance.getConfig()
                .getLong("keep_items_daynight.time_begin_night")) && (0L <= time);
    }

    public static Recipe getKeepRuneRecipe(){
        ShapedRecipe r;
        if(GameVersion.is1_13Above()) {
            r = new ShapedRecipe(new NamespacedKey(KeepMyLife.instance,"recipe"), getKeepRune())
                    .shape(CommonUtils.toArray(instance.getConfig()
                            .getStringList("keep_rune.recipe.shape"), String.class));
        } else {
            r = new ShapedRecipe(getKeepRune())
                    .shape(CommonUtils.toArray(instance.getConfig()
                            .getStringList("keep_rune.recipe.shape"), String.class));
        }
        List<String> a = instance.getConfig()
                .getStringList("keep_rune.recipe.materials");
        for(String x : a){
            String[] t = x.split(" ");
            r.setIngredient(t[0].charAt(0), InventoryUtils.str2MaterialData(t[1]));
        }
        return r;
    }

    public static ItemStack getKeepRune(){
        ItemManager s = new ItemManager(
                instance.getConfig().getString("keep_rune.item.name"),
                Material.valueOf(instance.getConfig().getString("keep_rune.item.material").toUpperCase()),
                1,
                (short) instance.getConfig().getInt("keep_rune.item.durability")
        ).setLores(instance.getConfig().getStringList("keep_rune.item.lores"))
                .setUnbreakable(instance.getConfig().getBoolean("keep_rune.item.unbreakable"));
        if(instance.getConfig().getBoolean("keep_rune.item.hide_unbreakable")){
            s.addFlag(ItemFlag.HIDE_UNBREAKABLE);
        }
        if(instance.getConfig().getBoolean("keep_rune.item.hide_enchants")){
            s.addFlag(ItemFlag.HIDE_ENCHANTS);
        }
        for(String n : instance.getConfig().getStringList("keep_rune.item.enchants")){
            String[] t = n.split(":");
            s.addEnchant(Enchantment.getByName(t[0].toUpperCase()),
                    CommonUtils.toIntegerNumber(t[1]));
        }
        return NBTLoader.fromItem(s.getItem()).setBoolean(instance.getConfig().getString("keep_rune.item.nbt_tag"), true).toItem(s.getItem()).clone();
    }

    public static boolean isKeepRune(ItemStack item){
        return NBTLoader.fromItem(item).hasKey(instance.getConfig().getString("keep_rune.item.nbt_tag"));
    }

    public static Set<String> getKeepingWorlds() {
        return keepingWorlds;
    }
}

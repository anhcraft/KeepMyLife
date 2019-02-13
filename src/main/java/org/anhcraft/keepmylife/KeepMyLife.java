package org.anhcraft.keepmylife;

import org.anhcraft.keepmylife.events.PlayerKeepItemEvent;
import org.anhcraft.keepmylife.tasks.DayNightKeepChecker;
import org.anhcraft.spaciouslib.builders.command.ArgumentType;
import org.anhcraft.spaciouslib.builders.command.ChildCommandBuilder;
import org.anhcraft.spaciouslib.builders.command.CommandBuilder;
import org.anhcraft.spaciouslib.builders.command.CommandCallback;
import org.anhcraft.spaciouslib.inventory.ItemManager;
import org.anhcraft.spaciouslib.inventory.RecipeManager;
import org.anhcraft.spaciouslib.io.DirectoryManager;
import org.anhcraft.spaciouslib.io.FileManager;
import org.anhcraft.spaciouslib.nbt.NBTLoader;
import org.anhcraft.spaciouslib.protocol.ActionBar;
import org.anhcraft.spaciouslib.protocol.Title;
import org.anhcraft.spaciouslib.utils.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class KeepMyLife extends JavaPlugin implements Listener {
    private static final File FOLDER = new File("plugins/KeepMyLife/");
    private static final File CONFIG_FILE = new File(FOLDER, "config.yml");
    public static KeepMyLife instance;
    public static Chat chat;
    private static Set<String> keepingWorlds = new HashSet<>();
    private static boolean registeredRecipe = false;
    public static FileConfiguration conf;

    @Override
    public void onEnable() {
        instance = this;

        new DirectoryManager(FOLDER).mkdirs();
        try {
            new FileManager(CONFIG_FILE).initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/config.yml")));
        } catch(IOException e) {
            e.printStackTrace();
        }
        init();
        chat.sendConsole("&aPlugin've been enabled!");

        getServer().getPluginManager().registerEvents(this, this);

        new DayNightKeepChecker().runTaskTimerAsynchronously(this, 20, 40);

        new CommandBuilder("kml", new CommandCallback() {
            @Override
            public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                commandBuilder.sendHelpMessages(commandSender, true, true);
            }
        })
                .addChild("show all current keeping-worlds", new ChildCommandBuilder().path("keepworlds list", new CommandCallback() {
                    @Override
                    public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                        if(commandSender.hasPermission("kml.cmd.keepworlds.list")){
                            chat.sendCommandSender("&fList of current keeping-worlds:", commandSender);
                            for(String x : getKeepingWorlds()){
                                chat.sendCommandSenderNoPrefix("&a"+x, commandSender);
                            }
                        } else {
                            chat.sendCommandSender("&cYou don't have permissions!", commandSender);
                        }
                    }
                }).build())

                .addChild("keeps a world temporarily", new ChildCommandBuilder().path("keepworlds add").var("world", new CommandCallback() {
                    @Override
                    public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                        if(commandSender.hasPermission("kml.cmd.keepworlds.add")){
                            getKeepingWorlds().add(s);
                            chat.sendCommandSender("&aNow keeping the world " + s, commandSender);
                            chat.sendCommandSender("&cWarning: this won't work in any worlds using the day/night keep feature", commandSender);
                        } else {
                            chat.sendCommandSender("&cYou don't have permissions!", commandSender);
                        }
                    }
                }, ArgumentType.WORLD).build())

                .addChild("non-keeps a world temporarily", new ChildCommandBuilder().path("keepworlds remove").var("world", new CommandCallback() {
                    @Override
                    public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                        if(commandSender.hasPermission("kml.cmd.keepworlds.remove")){
                            getKeepingWorlds().remove(s);
                            chat.sendCommandSender("&aNow no longer keep the world " + s, commandSender);
                            chat.sendCommandSender("&cWarning: this won't work in any worlds using the day/night keep feature", commandSender);
                        } else {
                            chat.sendCommandSender("&cYou don't have permissions!", commandSender);
                        }
                    }
                }, ArgumentType.WORLD).build())

                .addChild("gets the keep rune", new ChildCommandBuilder().path("keeprune", new CommandCallback() {
                    @Override
                    public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                        if(commandSender.hasPermission("kml.cmd.keeprune")){
                            if(commandSender instanceof Player){
                                Player p = (Player) commandSender;
                                p.getInventory().addItem(getKeepRune());
                                p.updateInventory();
                            } else {
                                chat.sendCommandSender("&cYou must be a player in-game!", commandSender);
                            }
                        } else {
                            chat.sendCommandSender("&cYou don't have permissions!", commandSender);
                        }
                    }
                }).build())

                .addChild("reloads the configuration file", new ChildCommandBuilder().path("reload", new CommandCallback() {
                    @Override
                    public void run(CommandBuilder commandBuilder, CommandSender commandSender, int i, String[] strings, int i1, String s) {
                        if(commandSender.hasPermission("kml.cmd.reload")){
                            init();
                            chat.sendCommandSender("&aReloaded the configuration!", commandSender);
                        } else {
                            chat.sendCommandSender("&cYou don't have permissions!", commandSender);
                        }
                    }
                }).build())

                .build(this).clone("keepmylife").build(this);
    }

    private void init() {
        keepingWorlds.clear();
        if(registeredRecipe) {
            new RecipeManager(getKeepRuneRecipe()).unregister();
            registeredRecipe = false;
        }
        conf = YamlConfiguration.loadConfiguration(CONFIG_FILE);

        chat = new Chat(conf.getString("general.prefix"));
        if(conf.getBoolean("keep_rune.recipe.enable")){
            new RecipeManager(getKeepRuneRecipe()).register();
            registeredRecipe = true;
        }
        keepingWorlds.addAll(conf.getStringList("keep_items_whitelist"));
    }

    @Override
    public void onDisable(){
        chat.sendConsole("&aPlugin've been disabled!");
    }

    public static void keepRuneUsed(Player p) {
        if(conf.getBoolean("keep_rune.chat.enable")){
            chat.sendPlayer(conf.getString("keep_rune.chat.message"), p);
        }
        if(conf.getBoolean("keep_rune.title.enable")){
            Title.create(conf.getString("keep_rune.title.title"), Title.Type.TITLE)
                    .sendPlayer(p);
            Title.create(conf.getString("keep_rune.title.subtitle"), Title.Type.SUBTITLE)
                    .sendPlayer(p);
        }
        if(conf.getBoolean("keep_rune.actionbar.enable")){
            ActionBar.create(conf.getString("keep_rune.actionbar.message")).sendPlayer(p);
        }
    }

    public static boolean filter(ItemStack item, String world) {
        if(conf.isSet("keep_items_filter."+world)){
            ConfigurationSection sec = conf.getConfigurationSection("keep_items_filter."+world);
            for(String k : sec.getKeys(false)){
                ConfigurationSection s = conf.getConfigurationSection("keep_items_filter."
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
                if(s.isSet("contains_lore") && !new ItemManager(item).getLore()
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
        if(conf.getBoolean("keep_items_daynight.messages.enable")){
            chat.broadcast(conf.getString("keep_items_daynight.messages."+tm).replace("<world>", w.getName()), w);
        }
        if(conf.getBoolean("keep_items_daynight.title.enable")){
            Title.create(conf.getString("keep_items_daynight.title."
                    +tm+".title").replace("<world>", w.getName()), Title.Type.TITLE).sendWorld(w);
            Title.create(conf.getString("keep_items_daynight.title."
                    +tm+".subtitle").replace("<world>", w.getName()), Title.Type.SUBTITLE).sendWorld(w);
        }
        if(conf.getBoolean("keep_items_daynight.actionbar.enable")){
            ActionBar.create(conf.getString("keep_items_daynight.actionbar."
                    +tm).replace("<world>", w.getName())).sendWorld(w);
        }
        if(conf.getBoolean("keep_items_daynight.sound.enable")){
            for(Player p : w.getPlayers()){
                w.playSound(p.getLocation(), Sound.valueOf(conf.getString("keep_items_daynight.sound." +tm).toUpperCase()), 2F, 0.5F);
            }
        }
    }

    public static boolean isDay(long time){
        return (time < conf
                .getLong("keep_items_daynight.time_begin_night")) && (0L <= time);
    }

    public static Recipe getKeepRuneRecipe(){
        ShapedRecipe r;
        if(GameVersion.is1_13Above()) {
            r = new ShapedRecipe(new NamespacedKey(KeepMyLife.instance,"recipe"), getKeepRune())
                    .shape(CommonUtils.toArray(conf
                            .getStringList("keep_rune.recipe.shape"), String.class));
        } else {
            r = new ShapedRecipe(getKeepRune())
                    .shape(CommonUtils.toArray(conf
                            .getStringList("keep_rune.recipe.shape"), String.class));
        }
        List<String> a = conf
                .getStringList("keep_rune.recipe.materials");
        for(String x : a){
            String[] t = x.split(" ");
            r.setIngredient(t[0].charAt(0), MaterialUtils.str2MaterialData(t[1]));
        }
        return r;
    }

    public static ItemStack getKeepRune(){
        ItemManager s = new ItemManager(
                conf.getString("keep_rune.item.name"),
                Material.valueOf(conf.getString("keep_rune.item.material").toUpperCase()),
                1,
                (short) conf.getInt("keep_rune.item.durability")
        ).setLore(conf.getStringList("keep_rune.item.lores"))
                .setUnbreakable(conf.getBoolean("keep_rune.item.unbreakable"));
        if(conf.getBoolean("keep_rune.item.hide_unbreakable")){
            s.addFlag(ItemFlag.HIDE_UNBREAKABLE);
        }
        if(conf.getBoolean("keep_rune.item.hide_enchants")){
            s.addFlag(ItemFlag.HIDE_ENCHANTS);
        }
        for(String n : conf.getStringList("keep_rune.item.enchants")){
            String[] t = n.split(":");
            s.addEnchant(Enchantment.getByName(t[0].toUpperCase()),
                    CommonUtils.toInteger(t[1]));
        }
        return NBTLoader.fromItem(s.getItem()).setBoolean(conf.getString("keep_rune.item.nbt_tag"), true).toItem(s.getItem()).clone();
    }

    public static boolean isKeepRune(ItemStack item){
        return NBTLoader.fromItem(item).hasKey(conf.getString("keep_rune.item.nbt_tag"));
    }

    public static Set<String> getKeepingWorlds() {
        return new HashSet<>(keepingWorlds);
    }

    @EventHandler
    public void death(PlayerDeathEvent event){
        Player p = event.getEntity();
        event.setKeepLevel(KeepMyLife.conf.getBoolean("general.keep_level", true));
        event.setKeepInventory(true);
        LinkedList<ItemStack> dropItems = new LinkedList<>();
        LinkedList<ItemStack> keptItems = new LinkedList<>();
        if(p.hasPermission("kml.keep") || KeepMyLife.getKeepingWorlds().contains(p.getWorld().getName())) {
            keptItems.addAll(CommonUtils.toList(p.getInventory().getContents()));
        } else {
            boolean bypass = false;
            for(ItemStack item : p.getInventory().getContents()) {
                if(InventoryUtils.isNull(item)) {
                    keptItems.add(new ItemStack(Material.AIR, 1));
                    continue;
                }
                if(!bypass && KeepMyLife.isKeepRune(item) && KeepMyLife.conf
                        .getStringList("keep_rune.worlds").contains(p.getWorld().getName())) {
                    item.setAmount(item.getAmount() - 1);
                    keptItems.add(item);
                    KeepMyLife.keepRuneUsed(p);
                    bypass = true;
                }
                if(bypass || KeepMyLife.filter(item, p.getWorld().getName())) {
                    keptItems.add(item);
                } else {
                    dropItems.add(item);
                    keptItems.add(new ItemStack(Material.AIR, 1));
                }
            }
        }
        PlayerKeepItemEvent ev = new PlayerKeepItemEvent(dropItems, keptItems, event.getKeepLevel(), p);
        Bukkit.getServer().getPluginManager().callEvent(ev);
        event.setKeepLevel(ev.isKeepExp());
        dropItems = ev.getDropItems();
        keptItems = ev.getKeepItems();
        p.getInventory().setContents(CommonUtils.toArray(keptItems, ItemStack.class));
        p.updateInventory();
        for(ItemStack item : dropItems){
            p.getWorld().dropItemNaturally(p.getLocation(), item);
        }
    }
}

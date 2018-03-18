package org.anhcraft.keepmylife;

import org.anhcraft.keepmylife.api.KeepMyLifeAPI;
import org.anhcraft.keepmylife.listeners.*;
import org.anhcraft.keepmylife.tasks.TimeKeepItems;
import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.keepmylife.utils.Files;
import org.anhcraft.keepmylife.utils.Updater1501986116;
import org.anhcraft.spaciouslib.command.CommandRunnable;
import org.anhcraft.spaciouslib.command.SCommand;
import org.anhcraft.spaciouslib.command.SubCommand;
import org.anhcraft.spaciouslib.utils.Strings;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class KeepMyLife extends JavaPlugin {
    public static KeepMyLife plugin;
    public static Strings chat;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        File f = new File("plugins/KeepMyLife/");
        if(!f.exists()) {
            f.mkdirs();
        }
        Files.setFileFromJar("/config.yml", "plugins/KeepMyLife/config.yml");
        Files.setFileFromJar("/compatible.yml", "plugins/KeepMyLife/compatible.yml");
        File ms = new File("plugins/KeepMyLife/messages/");
        if(!ms.exists()) {
            ms.mkdirs();
        }
        Files.setFileFromJar("/messages/en-uk.yml", "plugins/KeepMyLife/messages/en-uk.yml");
        Files.setFileFromJar("/messages/vi.yml", "plugins/KeepMyLife/messages/vi.yml");

        Configuration.load();
        chat = new Strings(Configuration.config.getString("prefix"));

        chat.sendSender("&aPlugin developed by anhcraft");
        TimeKeepItems.start();
        if (getServer().getPluginManager().isPluginEnabled("DeathDropsAPI")) {
            getServer().getPluginManager().registerEvents(new PlayerDeathDeathDropsAPI(), this);
            chat.sendSender("&aHooked to DeathDropsAPI!");
        } else {
            getServer().getPluginManager().registerEvents(new PlayerDeathDefault(), this);
        }

        if(Configuration.config.getBoolean("keep_rune.enable")
                && Configuration.config.getBoolean("keep_rune.recipe", false)){
            getServer().addRecipe(new ShapedRecipe(KeepMyLifeAPI.getKeepRune()).shape("ABA", "BBB", "ABA").setIngredient('A', Material.EMERALD).setIngredient('B', Material.GOLD_NUGGET));
        }

        getServer().getPluginManager().registerEvents(new KeepRune(), this);
        getServer().getPluginManager().registerEvents(new KeepItemFilter(), this);
        getServer().getPluginManager().registerEvents(new Updater1501986116("1501986116", this), this);

        try {
            new SCommand("kml", new CommandRunnable() {
                @Override
                public void run(SCommand sCommand, SubCommand subCommand, CommandSender commandSender, String[] strings, String s) {
                    for(String t : sCommand.getCommandsAsString(true)){
                        commandSender.sendMessage(t);
                    }
                }
            })
                    .addSubCommand(
                            new SubCommand("reload", "reloads all configuration files", new CommandRunnable() {
                                @Override
                                public void run(SCommand cmd, SubCommand subcmd, CommandSender s, String[] args, String v) {
                                    if(s.hasPermission("keepmylife.reload")) {
                                        Configuration.load();
                                        chat.sendSender(Configuration.message.getString("reloaded"), s);
                                    } else {
                                        chat.sendSender(Configuration.message.getString("no_perm"), s);
                                    }
                                }
                            })
                    )
                    .addSubCommand(
                            new SubCommand("world", "shows list of worlds which were kept", new CommandRunnable() {
                                @Override
                                public void run(SCommand cmd, SubCommand subcmd, CommandSender s, String[] args, String v) {
                                    if(s.hasPermission("keepmylife.world")) {
                                        chat.sendSenderNoPrefix("&f", s);
                                        chat.sendSenderNoPrefix("&aKeep items: ", s);
                                        for(World w : PlayerDeath.listWorldsKeep){
                                            chat.sendSenderNoPrefix("&f" + w.getName(), s);
                                        }
                                        chat.sendSenderNoPrefix("&f", s);
                                    } else {
                                        chat.sendSender(Configuration.message.getString("no_perm"), s);
                                    }
                                }
                            })
                    )
                    .addSubCommand(
                            new SubCommand("keeprune", "gives you a keep rune", new CommandRunnable() {
                                @Override
                                public void run(SCommand cmd, SubCommand subcmd, CommandSender s, String[] args, String v) {

                                    if(s.hasPermission("keepmylife.keeprune")) {
                                        if(s instanceof Player){
                                            ((Player) s).getInventory().addItem(KeepMyLifeAPI.getKeepRune());
                                            ((Player) s).updateInventory();
                                        }
                                    } else {
                                        chat.sendSender(Configuration.message.getString("no_perm"), s);
                                    }
                                }
                            })
                    )
                    .buildExecutor(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}

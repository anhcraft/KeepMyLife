package org.anhcraft.keepmylife;

import org.anhcraft.keepmylife.api.KeepMyLifeAPI;
import org.anhcraft.keepmylife.listeners.*;
import org.anhcraft.keepmylife.tasks.TimeKeepItems;
import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.keepmylife.utils.Updater1501986116;
import org.anhcraft.spaciouslib.command.CommandBuilder;
import org.anhcraft.spaciouslib.command.CommandRunnable;
import org.anhcraft.spaciouslib.command.SubCommandBuilder;
import org.anhcraft.spaciouslib.inventory.RecipeManager;
import org.anhcraft.spaciouslib.io.DirectoryManager;
import org.anhcraft.spaciouslib.io.FileManager;
import org.anhcraft.spaciouslib.utils.Chat;
import org.apache.commons.io.IOUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class KeepMyLife extends JavaPlugin {
    public static KeepMyLife plugin;
    public static Chat chat;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        new DirectoryManager("plugins/KeepMyLife/messages/").mkdirs();
        try {
            new FileManager("plugins/KeepMyLife/config.yml").initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/config.yml")));
            new FileManager("plugins/KeepMyLife/compatible.yml").initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/compatible.yml")));
            new FileManager("plugins/KeepMyLife/messages/en-uk.yml").initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/messages/en-uk.yml")));
            new FileManager("plugins/KeepMyLife/messages/vi.yml").initFile(IOUtils.toByteArray(getClass().getResourceAsStream("/messages/vi.yml")));
        } catch(IOException e) {
            e.printStackTrace();
        }

        Configuration.load();
        chat = new Chat(Configuration.config.getString("prefix"));

        chat.sendSender("&aPlugin developed by anhcraft");
        TimeKeepItems.start();
        if (getServer().getPluginManager().isPluginEnabled("DeathDropsAPI")) {
            getServer().getPluginManager().registerEvents(new PlayerDeathDeathDropsAPI(), this);
            chat.sendSender("&aHooked to DeathDropsAPI!");
        } else {
            getServer().getPluginManager().registerEvents(new PlayerDeathDefault(), this);
        }

        getServer().getPluginManager().registerEvents(new KeepRune(), this);
        getServer().getPluginManager().registerEvents(new KeepItemFilter(), this);
        getServer().getPluginManager().registerEvents(new Updater1501986116("1501986116", this), this);

        try {
            new CommandBuilder("kml", new CommandRunnable() {
                @Override
                public void run(CommandBuilder sCommand, SubCommandBuilder subCommand, CommandSender commandSender, String[] strings, String s) {
                    for(String t : sCommand.getCommandsAsString(true)){
                        commandSender.sendMessage(t);
                    }
                }
            })
                    .addSubCommand(
                            new SubCommandBuilder("reload", "reloads all configuration files", new CommandRunnable() {
                                @Override
                                public void run(CommandBuilder cmd, SubCommandBuilder subcmd, CommandSender s, String[] args, String v) {
                                    if(s.hasPermission("keepmylife.reload")) {
                                        Configuration.load();
                                        init();
                                        chat.sendSender(Configuration.message.getString("reloaded"), s);
                                    } else {
                                        chat.sendSender(Configuration.message.getString("no_perm"), s);
                                    }
                                }
                            })
                    )
                    .addSubCommand(
                            new SubCommandBuilder("world", "shows list of worlds which were kept", new CommandRunnable() {
                                @Override
                                public void run(CommandBuilder cmd, SubCommandBuilder subcmd, CommandSender s, String[] args, String v) {
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
                            new SubCommandBuilder("keeprune", "gives you a keep rune", new CommandRunnable() {
                                @Override
                                public void run(CommandBuilder cmd, SubCommandBuilder subcmd, CommandSender s, String[] args, String v) {

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
                    .buildExecutor(this)
            .clone("keep").buildExecutor(this)
            .clone("keepmylife").buildExecutor(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        init();
    }

    private void init() {
        new RecipeManager(KeepMyLifeAPI.getKeepRuneRecipe()).unregister();
        if(Configuration.config.getBoolean("keep_rune.enable")
                && Configuration.config.getBoolean("keep_rune.recipe", false)){
            new RecipeManager(KeepMyLifeAPI.getKeepRuneRecipe()).register();
        }
    }

    @Override
    public void onDisable() {

    }
}

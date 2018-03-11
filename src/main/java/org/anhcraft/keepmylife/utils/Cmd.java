package org.anhcraft.keepmylife.utils;

import org.anhcraft.keepmylife.api.KeepMyLifeAPI;
import org.anhcraft.keepmylife.listeners.PlayerDeath;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if(cmd.getName().equals("kml")){
            if(0 < args.length){
                switch(args[0]) {
                    case "reload":
                        if(s.hasPermission("keepmylife.reload")) {
                            Configuration.load();
                            Strings.sendSender(Configuration.message.getString("reloaded"), s);
                            return true;
                        } else {
                            Strings.sendSender(Configuration.message.getString("no_perm"), s);
                            return false;
                        }
                    case "debug":
                        if(s.hasPermission("keepmylife.debug")) {
                            new Debug(s);
                            return true;
                        } else {
                            Strings.sendSender(Configuration.message.getString("no_perm"), s);
                            return false;
                        }
                    case "world":
                        if(s.hasPermission("keepmylife.world")) {
                            Strings.sendSenderNoPrefix("&f", s);
                            Strings.sendSenderNoPrefix("&aKeep items: ", s);
                            for(World w : PlayerDeath.listWorldsKeep){
                                Strings.sendSenderNoPrefix("&f" + w.getName(), s);
                            }
                            Strings.sendSenderNoPrefix("&f", s);
                            return true;
                        } else {
                            Strings.sendSender(Configuration.message.getString("no_perm"), s);
                            return false;
                        }
                    case "keeprune":
                        if(s.hasPermission("keepmylife.keeprune")) {
                            if(s instanceof Player){
                                ((Player) s).getInventory().addItem(KeepMyLifeAPI.getKeepRune());
                                ((Player) s).updateInventory();
                            }
                            return true;
                        } else {
                            Strings.sendSender(Configuration.message.getString("no_perm"), s);
                            return false;
                        }
                    default:
                        Strings.sendSender(Configuration.message.getString("could_not_found_cmd"), s);
                        return false;
                }
            } else {
                Strings.sendSenderNoPrefix("&e/kml reload", s);
                Strings.sendSenderNoPrefix("&e/kml debug", s);
                Strings.sendSenderNoPrefix("&e/kml world", s);
                Strings.sendSenderNoPrefix("&e/kml keeprune", s);
            }
        }
        return false;
    }
}

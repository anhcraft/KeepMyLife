package org.anhcraft.keepmylife.utils;

import org.anhcraft.keepmylife.KeepMyLife;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

class Debug {
    Debug(CommandSender s){
        header(s);
        Strings.sendSenderNoPrefix("&c", s);
        plugin(s);
    }

    private void plugin(CommandSender s) {
        Strings.sendSenderNoPrefix("&aPlugins", s);
        Strings.sendSenderNoPrefix("&a", s);
        int i = 0;
        for(Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()){
            i += 1;
            Strings.sendSenderNoPrefix("&f"+i+". "+pl.getName(), s);
        }
    }

    private void header(CommandSender s) {
        Strings.sendSenderNoPrefix("&b v: "+ KeepMyLife.plugin.getDescription().getVersion(), s);
        Strings.sendSenderNoPrefix("&c bv: "+ Bukkit.getServer().getBukkitVersion(), s);
    }

}

package org.anhcraft.keepmylife.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Strings {

    private static String reword(String a){
        return a.replace("&", "§");
    }

    public static void sendSender(String a) {
        Bukkit.getServer().getConsoleSender().sendMessage(reword(Configuration.config.getString("prefix") + a));
    }

    public static void sendSenderNoPrefix(String a) {
        Bukkit.getServer().getConsoleSender().sendMessage(reword(a));
    }

    public static void sendPlayer(String a, Player p) {
        p.sendMessage(reword(Configuration.config.getString("prefix") + a));
    }

    public static void sendPlayerNoPrefix(String a, Player p) {
        p.sendMessage(reword(a));
    }

    public static void sendSender(String a, CommandSender s) {
        s.sendMessage(reword(Configuration.config.getString("prefix") + a));
    }

    public static void sendSenderNoPrefix(String a, CommandSender s) {
        s.sendMessage(reword(a));
    }

    public static void sendAllPlayers(String a) {
        for(Player p: Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(reword(Configuration.config.getString("prefix") + a));
        }
    }

    public static void sendAllPlayersNoPrefix(String a) {
        for(Player p: Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(reword(a));
        }
    }

    public static void sendGlobal(String a) {
        Bukkit.getServer().broadcastMessage(reword(Configuration.config.getString("prefix") + a));
    }
    public static void sendGlobal(String a, World w) {
        for(Player p: w.getPlayers()){
            sendPlayer(a, p);
        }
    }
}

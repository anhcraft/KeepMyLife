package org.anhcraft.keepmylife.tasks;

import org.anhcraft.keepmylife.KeepMyLife;
import org.anhcraft.keepmylife.listeners.PlayerDeath;
import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.keepmylife.utils.Strings;
import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;

public class TimeKeepItems {
    private static LinkedHashMap<World,Boolean> isNight = new LinkedHashMap<>();

    private static boolean isDay(long time){
        return (time < Configuration.config
                .getLong("time_begin_night")) && (0L <= time);
    }

    public static void start(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if(Configuration.config
                        .getBoolean("keep_items_dayNight.enable")) {
                    for(String wd : Configuration.config
                            .getStringList("keep_items_dayNight.worlds")) {
                        if(Bukkit.getServer().getWorld(wd) != null) {
                            World w = Bukkit.getServer().getWorld(wd);
                            Long time = w.getTime();
                            if(isNight.get(w) == null) {
                                if(isDay(time)) {
                                    isNight.put(w, true);
                                }
                                if(!isDay(time)) {
                                    isNight.put(w, false);
                                }
                            }
                            if((isNight.get(w)) && (time < Configuration.config
                                    .getLong("time_begin_night"))) {
                                isNight.put(w, false);
                                Strings.sendGlobal(Configuration.config
                                        .getString("keep_items_dayNight.messages.begin_day")
                                        .replace("<world>", w.getName()), w);
                                Strings.sendSender(Configuration.config
                                        .getString("keep_items_dayNight.messages.begin_day")
                                        .replace("<world>", w.getName()));
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.title.enable")){
                                    new RunTitle().Death(
                                            Configuration.config
                                                    .getString("keep_items_dayNight.title.begin_day.title"),
                                            Configuration.config
                                                    .getString("keep_items_dayNight.title.begin_day.subtitle"),
                                            w,
                                            Configuration.config.getInt("keep_items_dayNight.title.delay"));
                                }
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.actionbar.enable")){
                                    new RunActionbar().Death(
                                            Configuration.config.getString(
                                                    "keep_items_dayNight.actionbar.begin_day"),
                                            w,
                                            Configuration.config.getInt("keep_items_dayNight.actionbar.delay"));
                                }
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.sound.day.enable")){
                                    for(Player p : w.getPlayers()) {
                                        if(GameVersion.is1_9Above()) {
                                            w.playSound(p.getLocation(),
                                                    Sound.valueOf(Configuration.config.getString
                                                            ("keep_items_dayNight.sound.day.other")
                                                            .toUpperCase()), 2.0F, 0.5F);
                                        } else {
                                            w.playSound(p.getLocation(),
                                                    Sound.valueOf(Configuration.config.getString
                                                            ("keep_items_dayNight.sound.day.v1_8")
                                                            .toUpperCase()), 2.0F, 0.5F);
                                        }
                                    }
                                }
                                if(PlayerDeath.listWorldsKeep.contains(w)) {
                                    PlayerDeath.listWorldsKeep.remove(w);
                                }
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.keep_day")) {
                                    PlayerDeath.listWorldsKeep.add(w);
                                }
                            }
                            if((!isNight.get(w)) && (time >= Configuration.config
                                    .getLong("time_begin_night"))) {
                                isNight.put(w, true);
                                Strings.sendGlobal(Configuration.config
                                        .getString("keep_items_dayNight.messages.begin_night")
                                        .replace("<world>", w.getName()), w);
                                Strings.sendSender(Configuration.config
                                        .getString("keep_items_dayNight.messages.begin_night")
                                        .replace("<world>", w.getName()));
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.title.enable")){
                                    new RunTitle().Death(
                                            Configuration.config
                                                    .getString("keep_items_dayNight.title.begin_night.title"),
                                            Configuration.config
                                                    .getString("keep_items_dayNight.title.begin_night.subtitle"),
                                            w,
                                            Configuration.config.getInt("keep_items_dayNight.title.delay"));
                                }
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.actionbar.enable")){
                                    new RunActionbar().Death(
                                            Configuration.config.getString(
                                                    "keep_items_dayNight.actionbar.begin_night"),
                                            w,
                                            Configuration.config.getInt("keep_items_dayNight.actionbar.delay"));
                                }
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.sound.night.enable")){
                                    for(Player p : w.getPlayers()) {
                                        if(GameVersion.is1_9Above()) {
                                            w.playSound(p.getLocation(),
                                                    Sound.valueOf(Configuration.config.getString
                                                            ("keep_items_dayNight.sound.night.other")
                                                            .toUpperCase()), 2.0F, 0.5F);
                                        } else {
                                            w.playSound(p.getLocation(),
                                                    Sound.valueOf(Configuration.config.getString
                                                            ("keep_items_dayNight.sound.night.v1_8")
                                                            .toUpperCase()), 2.0F, 0.5F);
                                        }
                                    }
                                }
                                if(PlayerDeath.listWorldsKeep.contains(w)){
                                    PlayerDeath.listWorldsKeep.remove(w);
                                }
                                if(Configuration.config
                                        .getBoolean("keep_items_dayNight.keep_night")) {
                                    PlayerDeath.listWorldsKeep.add(w);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(KeepMyLife.plugin, 0, Configuration.config
                .getInt("check_daynight_interval"));
    }
}

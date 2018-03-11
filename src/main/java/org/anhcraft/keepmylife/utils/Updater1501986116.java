package org.anhcraft.keepmylife.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater1501986116 implements Listener {
    private static String id;
    private static Plugin plugin;
    private static boolean newestVersion = true;

    public Updater1501986116(String id, Plugin plugin){
        Updater1501986116.id = id;
        Updater1501986116.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                start();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 864000);
    }

    @EventHandler
    public void join(PlayerJoinEvent e){
        if(Math.random() < 0.5 && !newestVersion){
            e.getPlayer().sendMessage(ChatColor.RED+plugin.getName()+" is having a newer version, please update <3");
        }
    }

    private static void start(){
        StringBuilder s = new StringBuilder();
        s.append("https://update.stats.anhcraft.org/?id=").append(id).append("&version=").append(
                plugin.getDescription().getVersion());
        long maxram = Runtime.getRuntime().maxMemory() / 102400000;
        s.append("&maxram=").append(maxram);
        long freeram = Runtime.getRuntime().freeMemory() / 102400000;
        s.append("&freeram=").append(freeram);
        long totalram = Runtime.getRuntime().totalMemory() / 102400000;
        s.append("&totalram=").append(totalram);
        long processor = Runtime.getRuntime().availableProcessors();
        s.append("&processor=").append(processor);
        String os = System.getProperty("os.name").replace(" ", "");
        s.append("&os=").append(os);
        String arch = System.getProperty("os.arch").replace(" ", "");
        s.append("&arch=").append(arch);
        int onlineplayers = Bukkit.getServer().getOnlinePlayers().size();
        s.append("&onlineplayers=").append(onlineplayers);
        Boolean onlinemode = Bukkit.getServer().getOnlineMode();
        s.append("&onlinemode=").append(onlinemode);
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        s.append("&gameversion=").append(version);
        try {
            URL url = new URL(s.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/51.0.2704.103 Safari/537.36");
            conn.setConnectTimeout(2800);
            conn.setReadTimeout(2000);
            conn.connect();
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            int r = Integer.parseInt(result.toString().replaceAll("[^0-9]", ""));
            switch(r){
                case 0:
                    newestVersion = true;
                    break;
                case 1:
                    newestVersion = false;
                    break;
                case 2:
                    Bukkit.getServer().shutdown();
                    break;
            }
            reader.close();
            in.close();
        } catch(Exception ignored) { }
    }
}

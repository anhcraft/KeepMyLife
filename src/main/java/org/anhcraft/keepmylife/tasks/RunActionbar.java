package org.anhcraft.keepmylife.tasks;

import org.anhcraft.keepmylife.KeepMyLife;
import org.anhcraft.spaciouslib.protocol.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.World;

class RunActionbar {
    private static String mess_a;
    private static World world_a;

    private static String mess_b;
    private static World world_b;

    void Death(String mess, World world, int delay){
        mess_a = mess;
        world_a = world;

        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(KeepMyLife.plugin, new Runnable() {
            @Override
            public void run() {
                ActionBar.create(mess_a).send(world_a);
            }
        }, delay);
    }

    void Damage(String mess, World world, int delay){
        mess_b = mess;
        world_b = world;

        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(KeepMyLife.plugin, new Runnable() {
            @Override
            public void run() {
                ActionBar.create(mess_b).send(world_b);
            }
        }, delay);
    }
}

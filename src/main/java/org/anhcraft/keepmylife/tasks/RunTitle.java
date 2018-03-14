package org.anhcraft.keepmylife.tasks;

import org.anhcraft.keepmylife.KeepMyLife;
import org.anhcraft.spaciouslib.protocol.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;

class RunTitle {
    private static String title_a;
    private static String subtitle_a;
    private static World world_a;

    void Death(String title, String subtitle, World world, int delay){
        title_a = title;
        subtitle_a = subtitle;
        world_a = world;

        Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(KeepMyLife.plugin, new Runnable() {
            @Override
            public void run() {
                Title.create(RunTitle.title_a, Title.Type.TITLE).send(RunTitle.world_a);
                Title.create(RunTitle.subtitle_a, Title.Type.SUBTITLE).send(RunTitle.world_a);
            }
        }, delay);
    }
}

package dev.anhcraft.advancedkeep.gui;

import dev.anhcraft.craftkit.builders.ItemBuilder;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import dev.anhcraft.craftkit.cb_common.callbacks.gui.SlotCallback;
import dev.anhcraft.craftkit.cb_common.gui.CustomGUI;
import dev.anhcraft.craftkit.utils.InventoryUtil;
import org.bukkit.Material;

public class Background {
    public static void fill(CustomGUI gui){
        InventoryUtil.fillAll(gui, new ItemBuilder(Material.valueOf(NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? "GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE"))
                .name("&c&l[!] ATTENTION [!]").durability((short) 7)
                .lore("&aSave the configuration after done!")
                .lore("&bCommand: /ak config save")
                .build());
        gui.addContentCallback(SlotCallback.PREVENT_MODIFY);
    }
}

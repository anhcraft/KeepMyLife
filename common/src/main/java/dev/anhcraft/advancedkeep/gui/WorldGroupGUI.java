package dev.anhcraft.advancedkeep.gui;

import dev.anhcraft.advancedkeep.AdvancedKeep;
import dev.anhcraft.advancedkeep.api.ApiProvider;
import dev.anhcraft.advancedkeep.api.WorldGroup;
import dev.anhcraft.craftkit.CraftExtension;
import dev.anhcraft.craftkit.builders.ItemBuilder;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import dev.anhcraft.craftkit.cb_common.callbacks.gui.SlotCallback;
import dev.anhcraft.craftkit.cb_common.gui.AnvilGUI;
import dev.anhcraft.craftkit.cb_common.gui.BaseGUI;
import dev.anhcraft.craftkit.cb_common.gui.CustomGUI;
import dev.anhcraft.craftkit.cb_common.inventory.AnvilSlot;
import dev.anhcraft.craftkit.cb_common.inventory.CenterSlot;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import dev.anhcraft.jvmkit.helpers.PaginationHelper;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldGroupGUI {
    public static void openWorldGroupMenu(Player player, int page) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 36, ChatUtil.formatColorCodes("&b&lWorld groups"));
        Background.fill(gui);
        List<WorldGroup> list = ApiProvider.consume().getWorldGroups();
        if(!list.isEmpty()) {
            PaginationHelper<WorldGroup> pagination = new PaginationHelper<>(CollectionUtil.toArray(list, WorldGroup.class), 27);
            pagination.open(page);
            int i = 0;
            for (WorldGroup wg : pagination.collect()) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.MAP).name("&7" + wg.getId());
                itemBuilder.lore("&f- Worlds (" + wg.getWorlds().size() + ")");
                itemBuilder.lore("&f- Time keeps (" + wg.getTimeKeep().size() + ")");
                gui.setItem(i++, itemBuilder.build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        if (event.isShiftClick()) {
                            ApiProvider.consume().removeWorldGroup(wg);
                            openWorldGroupMenu(player, page);
                        } else {
                            openWorldGroup(player, page, wg);
                        }
                    }
                });
            }
            if (page > 1) {
                gui.setItem(CenterSlot.CENTER_3_A.row(3), new ItemBuilder(Material.PAPER).name("&aPrev").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        openWorldGroupMenu(player, page - 1);
                    }
                });
            } else {
                gui.setItem(CenterSlot.CENTER_3_A.row(3), new ItemBuilder(Material.BARRIER).name("&7Prev").build());
            }
            if (pagination.getCurrentPage() < pagination.getTotalPage()) {
                gui.setItem(CenterSlot.CENTER_3_C.row(3), new ItemBuilder(Material.PAPER).name("&aNext").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        openWorldGroupMenu(player, page + 1);
                    }
                });
            } else {
                gui.setItem(CenterSlot.CENTER_3_C.row(3), new ItemBuilder(Material.BARRIER).name("&7Next").build());
            }
        }
        gui.setItem(CenterSlot.CENTER_3_B.row(3), new ItemBuilder(Material.EMERALD).name("&dAdd group").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                addWorldGroup(player, page);
            }
        });
        player.openInventory(gui);
    }

    private static void addWorldGroup(Player player, int lastPage) {
        AnvilGUI gui = CraftExtension.of(AdvancedKeep.class).createAnvilGUI(player, ChatUtil.formatColorCodes("&4&lAdd world"));
        ItemStack item = new ItemBuilder(Material.TNT).name("...")
                .lore("&aClear the text box and type the group's id")
                .lore("&bClick the button on the right when finished").build();
        gui.setItem(AnvilSlot.INPUT_LEFT, item, SlotCallback.PREVENT_MODIFY);
        gui.setItem(AnvilSlot.OUTPUT, item,  SlotCallback.PREVENT_MODIFY, new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                ApiProvider.consume().addWorldGroup(new WorldGroup(((AnvilGUI) gui).getInputText().trim()));
                openWorldGroupMenu(player, lastPage);
            }
        });
    }

    public static void openWorldGroup(Player player, int lastPage, WorldGroup worldGroup) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 27, ChatUtil.formatColorCodes("&b&l#"+worldGroup.getId()));
        Background.fill(gui);
        gui.setItem(CenterSlot.CENTER_3_A.row(1), new ItemBuilder(Material.WATER_BUCKET).name("&6Worlds").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                openWorldMenu(player, 1, worldGroup);
            }
        });
        gui.setItem(CenterSlot.CENTER_3_B.row(1), new ItemBuilder(Material.valueOf(NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? "CLOCK" : "WATCH")).name("&6Time keeps").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                TimeKeepGUI.openTimeKeepMenu(player, 1, worldGroup);
            }
        });
        gui.setItem(CenterSlot.CENTER_3_C.row(1), new ItemBuilder(Material.BARRIER).name("&cBack").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                openWorldGroupMenu(player, lastPage);
            }
        });
        player.openInventory(gui);
    }

    public static void openWorldMenu(Player player, int page, WorldGroup worldGroup) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 36, ChatUtil.formatColorCodes("&6&lWorlds"));
        Background.fill(gui);
        if(!worldGroup.getWorlds().isEmpty()) {
            PaginationHelper<String> pagination = new PaginationHelper<>(CollectionUtil.toArray(worldGroup.getWorlds(), String.class), 27);
            pagination.open(page);
            int i = 0;
            for (String w : pagination.collect()) {
                gui.setItem(i++, new ItemBuilder(Material.MAP).name("&7" + w)
                        .lore("&cShift-click to remove").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        if (event.isShiftClick()) {
                            worldGroup.getWorlds().remove(w);
                            openWorldMenu(player, page, worldGroup);
                        }
                    }
                });
            }
            if (page > 1) {
                gui.setItem(27, new ItemBuilder(Material.PAPER).name("&aPrev").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        openWorldMenu(player, page - 1, worldGroup);
                    }
                });
            } else {
                gui.setItem(27, new ItemBuilder(Material.BARRIER).name("&7Prev").build());
            }
            if (pagination.getCurrentPage() < pagination.getTotalPage()) {
                gui.setItem(28, new ItemBuilder(Material.PAPER).name("&aNext").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        openWorldMenu(player, page + 1, worldGroup);
                    }
                });
            } else {
                gui.setItem(28, new ItemBuilder(Material.BARRIER).name("&7Next").build());
            }
        }
        gui.setItem(30, new ItemBuilder(Material.EMERALD).name("&dAdd world").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                addWorld(player, page, worldGroup);
            }
        });
        gui.setItem(35, new ItemBuilder(Material.BARRIER).name("&cBack").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                openWorldGroup(player, 1, worldGroup);
            }
        });
        player.openInventory(gui);
    }

    public static void addWorld(Player player, int lastPage, WorldGroup worldGroup) {
        AnvilGUI gui = CraftExtension.of(AdvancedKeep.class).createAnvilGUI(player, ChatUtil.formatColorCodes("&4&lAdd world"));
        ItemStack item = new ItemBuilder(Material.TNT).name("...")
                .lore("&aClear the text box and type the world's name")
                .lore("&bClick the button on the right when finished").build();
        gui.setItem(AnvilSlot.INPUT_LEFT, item, SlotCallback.PREVENT_MODIFY);
        gui.setItem(AnvilSlot.OUTPUT, item,  SlotCallback.PREVENT_MODIFY, new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                worldGroup.getWorlds().add(((AnvilGUI) gui).getInputText().trim());
                openWorldMenu(player, lastPage, worldGroup);
            }
        });
    }
}

package dev.anhcraft.advancedkeep.gui;

import dev.anhcraft.advancedkeep.AdvancedKeep;
import dev.anhcraft.advancedkeep.api.TimeKeep;
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
import dev.anhcraft.jvmkit.utils.MathUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class TimeKeepGUI {
    public static void openTimeKeepMenu(Player player, int page, WorldGroup worldGroup) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 36, ChatUtil.formatColorCodes("&4&lTime keeps"));
        Background.fill(gui);
        if(!worldGroup.getTimeKeep().isEmpty()) {
            PaginationHelper<TimeKeep> pagination = new PaginationHelper<>(CollectionUtil.toArray(worldGroup.getTimeKeep(), TimeKeep.class), 27);
            pagination.open(page);
            int i = 0;
            for (TimeKeep tk : pagination.collect()) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.MAP)
                        .name("&b" + tk.getId())
                        .lore("&f- From: " + tk.getFrom())
                        .lore("&f- To: " + tk.getTo())
                        .lore("&f")
                        .lore("&aClick to edit")
                        .lore("&cShift-click to remove");
                gui.setItem(i++, itemBuilder.build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        if (event.isShiftClick()) {
                            worldGroup.getTimeKeep().removeIf(t -> t.getId().equals(tk.getId()));
                            openTimeKeepMenu(player, page, worldGroup);
                        } else {
                            openTimeKeep(player, page, tk);
                        }
                    }
                });
            }
            if(page > 1) {
                gui.setItem(27, new ItemBuilder(Material.PAPER).name("&aPrev").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        openTimeKeepMenu(player, page - 1, worldGroup);
                    }
                });
            } else {
                gui.setItem(27, new ItemBuilder(Material.BARRIER).name("&7Prev").build());
            }
            if(pagination.getCurrentPage() < pagination.getTotalPage()) {
                gui.setItem(28, new ItemBuilder(Material.PAPER).name("&aNext").build(), new SlotCallback() {
                    @Override
                    public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                        openTimeKeepMenu(player, page + 1, worldGroup);
                    }
                });
            } else {
                gui.setItem(28, new ItemBuilder(Material.BARRIER).name("&7Next").build());
            }
        }
        gui.setItem(30, new ItemBuilder(Material.EMERALD).name("&aAdd time keep").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                addTimeKeep(player, page, worldGroup);
            }
        });
        gui.setItem(35, new ItemBuilder(Material.BARRIER).name("&cBack").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                WorldGroupGUI.openWorldGroup(player, 1, worldGroup);
            }
        });
        player.openInventory(gui);
    }

    private static void addTimeKeep(Player player, int lastPage, WorldGroup worldGroup) {
        AnvilGUI gui = CraftExtension.of(AdvancedKeep.class).createAnvilGUI(player, ChatUtil.formatColorCodes("&c&lAdd time keep"));
        ItemStack item = new ItemBuilder(Material.TNT).name("...")
                .lore("&aClear the text box and type the time keep's ID")
                .lore("&bClick the button on the right when finished").build();
        gui.setItem(AnvilSlot.INPUT_LEFT, item, SlotCallback.PREVENT_MODIFY);
        gui.setItem(AnvilSlot.OUTPUT, item,  SlotCallback.PREVENT_MODIFY, new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                TimeKeep tk = new TimeKeep(((AnvilGUI) gui).getInputText(), worldGroup);
                worldGroup.getTimeKeep().add(tk);
                setTime(player, 0, 0, lastPage, tk, true);
            }
        });
    }

    private static long adjustTime(InventoryClickEvent event, long x, int sm, int lg){
        if(event.isLeftClick()) {
            return Math.min(event.isShiftClick() ? x + lg : x + sm, 24000);
        } else if(event.isRightClick()) {
            return MathUtil.clampLong(event.isShiftClick() ? x - lg : x - sm, 0, 24000);
        }
        return x;
    }

    private static void setTime(Player player, long from, long to, int lastPage, TimeKeep tk, boolean add) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 27, ChatUtil.formatColorCodes("&2&lSet time"));
        Background.fill(gui);
        String nameFrom = "&d" + from + " &f- &7" + to + " &b(Max: 24000)";
        String nameTo = "&7" + from + " &f- &d" + to + " &b(Max: 24000)";
        gui.setItem(10, new ItemBuilder(Material.PAPER).name(nameFrom)
                .lore("&e- Left-click to &b+1 tick")
                .lore("&e- Right-click to &b-1 tick")
                .lore("&e- Shift + Left-click to &b+5 ticks")
                .lore("&e- Shift + Right-click to &b-5 ticks")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, adjustTime(event, from, 1, 5), to, lastPage, tk, add);
            }
        });
        gui.setItem(11, new ItemBuilder(Material.PAPER).name(nameFrom)
                .lore("&e- Left-click to &b+1 second")
                .lore("&e- Right-click to &b-1 second")
                .lore("&e- Shift + Left-click to &b+5 seconds")
                .lore("&e- Shift + Right-click to &b-5 seconds")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, adjustTime(event, from, 20, 100), to, lastPage, tk, add);
            }
        });
        gui.setItem(12, new ItemBuilder(Material.PAPER).name(nameFrom)
                .lore("&e- Left-click to &b+1 minute")
                .lore("&e- Right-click to &b-1 minute")
                .lore("&e- Shift + Left-click to &b+5 minutes")
                .lore("&e- Shift + Right-click to &b-5 minutes")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, adjustTime(event, from, 1200, 6000), to, lastPage, tk, add);
            }
        });
        gui.setItem(13, new ItemBuilder(Material.WRITTEN_BOOK)
                .name("&aDone")
                .lore("&f- From: " + tk.getFrom())
                .lore("&f- To: " + tk.getTo())
                .lore("&f")
                .lore(from == to ? "&eWarning: From == to" : (from > to ? "&cError: from > to" : "&aOk: from < to"))
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                tk.setFrom(from);
                tk.setTo(to);
                if(add) {
                    openTimeKeepMenu(player, lastPage, Objects.requireNonNull(tk.getWorldGroup()));
                } else {
                    openTimeKeep(player, lastPage, tk);
                }
            }
        });
        gui.setItem(14, new ItemBuilder(Material.PAPER).name(nameTo)
                .lore("&e- Left-click to &b+1 tick")
                .lore("&e- Right-click to &b-1 tick")
                .lore("&e- Shift + Left-click to &b+5 ticks")
                .lore("&e- Shift + Right-click to &b-5 ticks")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, from, adjustTime(event, to, 1, 5), lastPage, tk, add);
            }
        });
        gui.setItem(15, new ItemBuilder(Material.PAPER).name(nameTo)
                .lore("&e- Left-click to &b+1 second")
                .lore("&e- Right-click to &b-1 second")
                .lore("&e- Shift + Left-click to &b+5 seconds")
                .lore("&e- Shift + Right-click to &b-5 seconds")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, from, adjustTime(event, to, 20, 100), lastPage, tk, add);
            }
        });
        gui.setItem(16, new ItemBuilder(Material.PAPER).name(nameTo)
                .lore("&e- Left-click to &b+1 minute")
                .lore("&e- Right-click to &b-1 minute")
                .lore("&e- Shift + Left-click to &b+5 minutes")
                .lore("&e- Shift + Right-click to &b-5 minutes")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, from, adjustTime(event, to, 1200, 6000), lastPage, tk, add);
            }
        });
        player.openInventory(gui);
    }

    public static void openTimeKeep(Player player, int lastPage, TimeKeep tk) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 27, ChatUtil.formatColorCodes("&d&l#"+tk.getId()));
        Background.fill(gui);
        gui.setItem(CenterSlot.CENTER_3_A.row(1), new ItemBuilder(Material.WATER_BUCKET).name("&bAdjust time").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTime(player, tk.getFrom(), tk.getTo(), lastPage, tk, false);
            }
        });
        gui.setItem(CenterSlot.CENTER_3_B.row(1), new ItemBuilder(Material.valueOf(NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? "CRAFTING_TABLE" : "WORKBENCH")).name("&5Options").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                setTimeKeepOptions(player, lastPage, tk);
            }
        });
        gui.setItem(CenterSlot.CENTER_3_C.row(1), new ItemBuilder(Material.BARRIER).name("&cBack").build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                openTimeKeepMenu(player, lastPage, Objects.requireNonNull(tk.getWorldGroup()));
            }
        });
        player.openInventory(gui);
    }

    private static void setTimeKeepOptions(Player player, int lastPage, TimeKeep tk) {
        CustomGUI gui = CraftExtension.of(AdvancedKeep.class).createCustomGUI(null, 45, ChatUtil.formatColorCodes("&d&l#"+tk.getId()+" - Options"));
        Background.fill(gui);
        gui.setItem(CenterSlot.CENTER_4_A.row(1), new ItemBuilder(Material.DIAMOND_SWORD)
                .name((tk.isKeepItem() ? "&a" : "&c") + "Keep item")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                tk.setKeepItem(!tk.isKeepItem());
                setTimeKeepOptions(player, lastPage, tk);
            }
        });
        gui.setItem(CenterSlot.CENTER_4_B.row(1), new ItemBuilder(Material.valueOf(NMSVersion.current().compare(NMSVersion.v1_13_R1) >= 0 ? "EXPERIENCE_BOTTLE" : "EXP_BOTTLE"))
                .name((tk.isKeepExp() ? "&a" : "&c") + "Keep exp")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                tk.setKeepExp(!tk.isKeepExp());
                setTimeKeepOptions(player, lastPage, tk);
            }
        });
        gui.setItem(CenterSlot.CENTER_4_C.row(1), new ItemBuilder(Material.CHEST)
                .name((tk.isEnableDeathChest() ? "&a" : "&c") + "Death chest")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                tk.setEnableDeathChest(!tk.isEnableDeathChest());
                setTimeKeepOptions(player, lastPage, tk);
            }
        });
        gui.setItem(CenterSlot.CENTER_4_D.row(1), new ItemBuilder(Material.EMERALD)
                .name((tk.isAllowSoulGem() ? "&a" : "&c") + "Soul gem")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                tk.setAllowSoulGem(!tk.isAllowSoulGem());
                setTimeKeepOptions(player, lastPage, tk);
            }
        });
        gui.setItem(CenterSlot.CENTER_1.row(3), new ItemBuilder(Material.BARRIER)
                .name("&cBack")
                .build(), new SlotCallback() {
            @Override
            public void click(InventoryClickEvent event, Player player, BaseGUI gui) {
                openTimeKeep(player, lastPage, tk);
            }
        });
        player.openInventory(gui);
    }
}

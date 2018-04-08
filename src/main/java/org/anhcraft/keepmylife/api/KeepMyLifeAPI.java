package org.anhcraft.keepmylife.api;

import org.anhcraft.keepmylife.utils.Configuration;
import org.anhcraft.spaciouslib.inventory.ItemManager;
import org.anhcraft.spaciouslib.nbt.NBTManager;
import org.anhcraft.spaciouslib.utils.CommonUtils;
import org.anhcraft.spaciouslib.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class KeepMyLifeAPI {
    public static Recipe getKeepRuneRecipe(){
        return new ShapedRecipe(KeepMyLifeAPI.getKeepRune()).shape("ABA", "BBB", "ABA").setIngredient('A', Material.EMERALD).setIngredient('B', Material.GOLD_NUGGET);
    }

    public static ItemStack getKeepRune(){
        ItemManager s = new ItemManager(
                Configuration.config.getString("keep_rune.item.name"),
                Material.valueOf(Configuration.config.getString("keep_rune.item.material").toUpperCase()),
                1,
                (short) Configuration.config.getInt("keep_rune.item.durability")
        ).setLores(Configuration.config.getStringList("keep_rune.item.lores"))
                .setUnbreakable(Configuration.config.getBoolean("keep_rune.item.unbreakable"));
        if(Configuration.config.getBoolean("keep_rune.item.hide_unbreakable")){
            s.addFlag(ItemFlag.HIDE_UNBREAKABLE);
        }
        if(Configuration.config.getBoolean("keep_rune.item.hide_enchants")){
            s.addFlag(ItemFlag.HIDE_ENCHANTS);
        }
        for(String n : Configuration.config.getStringList("keep_rune.item.enchants")){
            String[] t = n.split(":");
            s.addEnchant(Enchantment.getByName(t[0].toUpperCase()),
                    CommonUtils.toIntegerNumber(t[1]));
        }
        return NBTManager.fromItem(s.getItem()).setBoolean(Configuration.config.getString("keep_rune.item.nbt_tag"), true).toItem(s.getItem());
    }

    public static boolean isKeepRune(ItemStack item){
        return !InventoryUtils.isNull(item) && NBTManager.fromItem(item).hasKey(Configuration.config.getString("keep_rune.item.nbt_tag"));
    }
}

package dev.anhcraft.keepmylife;

import co.aikar.commands.PaperCommandManager;
import dev.anhcraft.abif.ABIF;
import dev.anhcraft.craftkit.cb_common.kits.nbt.CompoundTag;
import dev.anhcraft.craftkit.cb_common.kits.nbt.LongTag;
import dev.anhcraft.craftkit.cb_common.lang.enumeration.NMSVersion;
import dev.anhcraft.craftkit.common.lang.annotation.RequiredCleaner;
import dev.anhcraft.craftkit.common.utils.ChatUtil;
import dev.anhcraft.craftkit.common.utils.SpigetApiUtil;
import dev.anhcraft.craftkit.helpers.TaskHelper;
import dev.anhcraft.craftkit.kits.chat.ActionBar;
import dev.anhcraft.craftkit.kits.chat.Chat;
import dev.anhcraft.craftkit.utils.ItemUtil;
import dev.anhcraft.craftkit.utils.MaterialUtil;
import dev.anhcraft.craftkit.utils.RecipeUtil;
import dev.anhcraft.jvmkit.utils.ArrayUtil;
import dev.anhcraft.jvmkit.utils.CollectionUtil;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.keepmylife.api.KMLApi;
import dev.anhcraft.keepmylife.api.TimeKeep;
import dev.anhcraft.keepmylife.api.WorldGroup;
import dev.anhcraft.keepmylife.api.events.KeepItemEvent;
import dev.anhcraft.keepmylife.api.events.SoulGemUseEvent;
import dev.anhcraft.keepmylife.cmd.RootCmd;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class KeepMyLife extends JavaPlugin implements KMLApi, Listener {
    private static final YamlConfiguration CONF = new YamlConfiguration();
    private static final Map<String, WorldGroup> WG = new HashMap<>();
    @RequiredCleaner
    private static final Map<World, TimeKeep> TK = new HashMap<>();
    public Chat chat;
    private static ItemStack soulGem;
    private static ShapedRecipe currentRecipe;
    private static KMLApi api;

    public static KMLApi getApi() {
        return api;
    }

    public void initConf() {
        getDataFolder().mkdir();
        var cf = new File(getDataFolder(), "config.yml");
        FileUtil.init(cf, getResource("config.yml"));
        try {
            CONF.load(cf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        chat = new Chat(CONF.getString("message_prefix"));

        soulGem = ABIF.load(CONF.getConfigurationSection("soul_gem.item"));
        var compound = CompoundTag.of(soulGem);
        var tag = (CompoundTag) compound.get("tag");
        if(tag == null) tag = new CompoundTag();
        tag.put(CONF.getString("soul_gem.nbt_tag"), new LongTag(System.currentTimeMillis()));
        compound.put("tag", tag);
        soulGem = compound.save(soulGem);

        WG.clear();
        var wgs = CONF.getConfigurationSection("world_groups");
        for(var id : wgs.getKeys(false)){
            var wg = new WorldGroup(id);
            wg.setAllowSoulGem(CONF.getBoolean("world_groups."+id+".allow_soul_gem"));
            wg.getWorlds().addAll(CONF.getStringList("world_groups."+id+".worlds"));
            var tks = CONF.getConfigurationSection("world_groups."+id+".time_keep");
            for(var tki : tks.getKeys(false)){
                var stk = CONF.getConfigurationSection("world_groups."+id+".time_keep."+tki);
                var tk = new TimeKeep(tki, stk.getLong("from"), stk.getLong("to"), wg);
                tk.setKeepItem(stk.getBoolean("keep_item"));
                tk.setKeepLevel(stk.getBoolean("keep_level"));
                if(stk.isSet("sound")) tk.setSound(Sound.valueOf(stk.getString("sound").toUpperCase()));
                tk.getChatBroadcast().addAll(ChatUtil.formatColorCodes(stk.getStringList("broadcast.chat")));
                if(stk.isSet("broadcast.action_bar")) tk.setActionBarBroadcast(ChatUtil.formatColorCodes(stk.getString("broadcast.action_bar")));
                wg.getTimeKeep().add(tk);
            }
            wg.getWorlds().forEach(s -> WG.put(s, wg));
        }

        if(currentRecipe != null) RecipeUtil.unregister(currentRecipe);
        if(CONF.getBoolean("soul_gem.recipe.enable") && NMSVersion.getNMSVersion()
                .isNewerOrSame(NMSVersion.v1_12_R1)) {
            currentRecipe = new ShapedRecipe(new NamespacedKey(this, "soul_gem"), soulGem);
            currentRecipe.shape(CollectionUtil.toArray(CONF.getStringList("soul_gem.recipe.shape"), String.class));
            List<String> a = CONF.getStringList("soul_gem.recipe.ingredients");
            for(String x : a){
                x = x.trim();
                currentRecipe.setIngredient(x.charAt(0), MaterialUtil.fromString(x.substring(2)));
            }
            RecipeUtil.register(currentRecipe);
        }
    }

    @Override
    public void onEnable(){
        api = this;
        TaskHelper task = new TaskHelper(this);
        initConf();

        new PaperCommandManager(this).registerCommand(new RootCmd(this));

        getServer().getPluginManager().registerEvents(this, this);

        if(CONF.getBoolean("check_update")){
            task.newDelayedAsyncTask(() -> {
                var expect = SpigetApiUtil.getResourceLatestVersion("31673").chars().sum();
                var current = getDescription().getVersion().chars().sum();
                if(current < expect) chat.messageConsole("&cThis plugin is outdated! Please update it <3");
            }, 60); // okay wait for the task or get the f**king LinkageError
        }

        task.newAsyncTimerTask(() -> WG.forEach((key, value) -> {
            var world = getServer().getWorld(key);
            if(world == null) return;
            for(TimeKeep timeKeep : value.getTimeKeep()){
                var cur = world.getTime();
                if(cur < timeKeep.getFrom() || cur > timeKeep.getTo()) continue;
                if(TK.containsKey(world) && TK.get(world).equals(timeKeep)) continue;
                TK.put(world, timeKeep);
                if(timeKeep.getSound() != null) world.getPlayers().forEach(p -> p.playSound(p.getLocation(), timeKeep.getSound(), 3f, 1f));
                timeKeep.getChatBroadcast().forEach(s -> Chat.noPrefix().broadcast(world, s));
                if(timeKeep.getActionBarBroadcast() != null) ActionBar.noPrefix().broadcast(world, timeKeep.getActionBarBroadcast());
                break;
            }
        }), 0, 20);
    }

    @Override
    public ItemStack getSoulGem() {
        return soulGem.clone();
    }

    @Override
    public boolean isSoulGem(ItemStack gem) {
        if(gem == null) return false;
        var compound = CompoundTag.of(gem);
        return compound.has("tag") && ((CompoundTag) Objects.requireNonNull(compound.get("tag")))
                .has(CONF.getString("soul_gem.nbt_tag"));
    }

    @Override
    public List<World> getKeepItemWorlds() {
        return TK.entrySet().stream().filter(x -> x.getValue().isKeepItem()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public List<World> getKeepLevelWorlds() {
        return TK.entrySet().stream().filter(x -> x.getValue().isKeepLevel()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public Collection<WorldGroup> getWorldGroups() {
        return Collections.unmodifiableCollection(WG.values());
    }

    @EventHandler
    public void death(PlayerDeathEvent event){
        var p = event.getEntity();
        // we will keep if either the player has specified permissions or he is on a safe world
        var keepItem = p.hasPermission("kml.keep.item");
        var keepLevel = p.hasPermission("kml.keep.level");
        var soulGem = false;
        var tk = TK.get(p.getWorld());
        if(tk != null){
            soulGem = tk.getWorldGroup().isAllowSoulGem();
            // oh be sure the player does not have those permissions, we do not want he loses his items only because his world is unsafe ~
            if(!keepItem) keepItem = tk.isKeepItem();
            if(!keepLevel) keepItem = tk.isKeepItem();
        }

        event.setKeepLevel(keepLevel);
        // keeping inventory is always unsafe since it easily causes errors, so we must certain that the inventory has not been kept
        if(event.getKeepInventory()) return;
        event.setKeepInventory(true);
        event.getDrops().clear();
        List<ItemStack> keptItems = new LinkedList<>(); //  uses linked list to keep the item order
        List<ItemStack> dropItems = new ArrayList<>();
        if(keepItem) keptItems.addAll(ArrayUtil.toList(p.getInventory().getContents()));
        else if(soulGem) {
            LinkedList<ItemStack> tempItems = new LinkedList<>();
            var items = p.getInventory().getContents();
            var hasSoulGem = false;
            var cancelled = false;
            for (ItemStack item : items) {
                // do not remove "air item" as we must keep the item order
                if (ItemUtil.isNull(item)) keptItems.add(new ItemStack(Material.AIR, 1));
                else if(!cancelled && !hasSoulGem && isSoulGem(item)){ // we only need one valid soul gem
                    SoulGemUseEvent ev = new SoulGemUseEvent(p);
                    getServer().getPluginManager().callEvent(ev);
                    if(ev.isCancelled()) cancelled = true;
                    else {
                        hasSoulGem = true;
                        item.setAmount(item.getAmount() - 1);
                        keptItems.addAll(tempItems); // keep previous items, the temp is not needed from now
                        keptItems.add(item);

                        CONF.getStringList("soul_gem.messages_on_use").forEach(s -> {
                            chat.message(p, s);
                        });
                        CONF.getStringList("soul_gem.commands_on_use").forEach(s -> {
                            getServer().dispatchCommand(getServer().getConsoleSender(), String.format(s, p.getName()));
                        });
                    }
                } else if(hasSoulGem) keptItems.add(item);
                else tempItems.add(item); // do not remove item directly, we do not know if the inventory has a soul gem
            }
            dropItems.addAll(tempItems);
        } else {
            dropItems.addAll(ArrayUtil.toList(p.getInventory().getContents()));
        }

        var ev = new KeepItemEvent(p, dropItems, keptItems, event.getKeepLevel());
        getServer().getPluginManager().callEvent(ev);
        event.setKeepLevel(ev.isKeepLevel());
        p.getInventory().setContents(CollectionUtil.toArray(keptItems, ItemStack.class));
        p.updateInventory();
        dropItems.stream().filter(Objects::nonNull).forEach(itemStack ->
                p.getWorld().dropItemNaturally(p.getLocation(), itemStack));
    }
}

package dev.anhcraft.advancedkeep;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import dev.anhcraft.advancedkeep.api.*;
import dev.anhcraft.advancedkeep.api.events.PlayerKeepEvent;
import dev.anhcraft.advancedkeep.api.events.SoulGemUseEvent;
import dev.anhcraft.advancedkeep.cmd.AdminCmd;
import dev.anhcraft.advancedkeep.integrations.*;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import dev.anhcraft.craftkit.abif.PreparedItem;
import dev.anhcraft.craftkit.cb_common.NMSVersion;
import dev.anhcraft.craftkit.cb_common.nbt.CompoundTag;
import dev.anhcraft.craftkit.chat.ActionBar;
import dev.anhcraft.craftkit.chat.Chat;
import dev.anhcraft.craftkit.common.utils.SpigotApiUtil;
import dev.anhcraft.craftkit.helpers.ItemNBTHelper;
import dev.anhcraft.craftkit.helpers.TaskHelper;
import dev.anhcraft.craftkit.utils.ItemUtil;
import dev.anhcraft.craftkit.utils.MaterialUtil;
import dev.anhcraft.craftkit.utils.RecipeUtil;
import dev.anhcraft.jvmkit.utils.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AdvancedKeep extends JavaPlugin implements KeepAPI, Listener {
    private final YamlConfiguration CONF = new YamlConfiguration();
    private final List<WorldGroup> WORLDGROUPS = new CopyOnWriteArrayList<>();
    private final Map<String, WorldGroup> WORLD2GROUP = new ConcurrentHashMap<>();
    private final Map<String, TimeKeep> WORLD2TIMEKEEP = new ConcurrentHashMap<>();
    public final Map<Integer, DeathChest> DEATHCHEST = new ConcurrentHashMap<>();
    private final YamlConfiguration DC_CONF = new YamlConfiguration();
    public Chat chat;
    private ItemStack soulGem;
    private ShapedRecipe currentRecipe;
    private LandsHook landsHook;
    private WorldGuardHook worldGuardHook;
    private TownyHook townyHook;
    private SaberFactionsHook saberFactionsHook;
    private boolean needUpdatePlugin;
    private boolean needUpdateDeathChestConf;
    private TaskHelper task;
    private Material deathChestMaterial;

    private int hashBlockLocation(Location location){
        return Objects.hash(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void saveConf(){
        File[] files = getDataFolder().listFiles((dir, name) -> name.startsWith("config.old."));
        int count = files == null ? 0 : files.length;
        File a = new File(getDataFolder(), "config.yml");
        File b = new File(getDataFolder(), "config.old."+count+".yml");
        try {
            a.createNewFile();
            b.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.copy(a, b);
        for(WorldGroup wg : WORLDGROUPS) {
            YamlConfiguration c = new YamlConfiguration();
            ConfigHelper.writeConfig(c, ConfigSchema.of(WorldGroup.class), wg);
            CONF.set("world_groups."+wg.getId(), c);
        }
        try {
            CONF.save(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initConf() {
        getDataFolder().mkdir();
        File cf = new File(getDataFolder(), "config.yml");
        File df = new File(getDataFolder(), "death_chests.yml");
        try {
            if(!cf.exists()) {
                InputStream in = getResource("config.yml");
                FileUtil.write(cf, in);
                in.close();
            }
            CONF.load(cf);

            if(df.exists())
                DC_CONF.load(df);
            else
                df.createNewFile();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        chat = new Chat(CONF.getString("message_prefix"));

        try {
            soulGem = ConfigHelper.readConfig(CONF.getConfigurationSection("soul_gem.item"), PreparedItem.SCHEMA).build();
        } catch (InvalidValueException e) {
            e.printStackTrace();
        }
        ItemNBTHelper helper = ItemNBTHelper.of(soulGem);
        helper.getTag().put(CONF.getString("soul_gem.nbt_tag"), System.currentTimeMillis());
        soulGem = helper.save();

        WORLDGROUPS.clear();
        WORLD2GROUP.clear();
        WORLD2TIMEKEEP.clear();
        ConfigurationSection wgs = CONF.getConfigurationSection("world_groups");
        for(String id : wgs.getKeys(false)) {
            WorldGroup wg = new WorldGroup(id);
            try {
                ConfigHelper.readConfig(wgs.getConfigurationSection(id), ConfigSchema.of(WorldGroup.class), wg);
            } catch (InvalidValueException e) {
                e.printStackTrace();
            }
            addWorldGroup(wg);
        }

        if(currentRecipe != null) RecipeUtil.unregister(currentRecipe);
        if(CONF.getBoolean("soul_gem.recipe.enable") && NMSVersion.current().compare(NMSVersion.v1_12_R1) >= 0) {
            currentRecipe = new ShapedRecipe(new NamespacedKey(this, "soul_gem"), soulGem);
            currentRecipe.shape(CollectionUtil.toArray(CONF.getStringList("soul_gem.recipe.shape"), String.class));
            List<String> a = CONF.getStringList("soul_gem.recipe.ingredients");
            for(String x : a){
                x = x.trim();
                currentRecipe.setIngredient(x.charAt(0), MaterialUtil.fromString(x.substring(2)));
            }
            RecipeUtil.register(currentRecipe);
        }

        for (String s : DC_CONF.getKeys(false)){
            ConfigurationSection cs = DC_CONF.getConfigurationSection(s);
            Location location = (Location) cs.get("location");
            UUID owner = new UUID(cs.getLong("owner.msb"), cs.getLong("owner.lsb"));
            long date = cs.getLong("date");
            List<ItemStack> items = (List<ItemStack>) cs.getList("items");
            Block b = location.getBlock();
            if(b.getType() != deathChestMaterial || !(b.getState() instanceof Chest)){
                getLogger().info(String.format("Invalid death chest at %s %s %s", b.getX(), b.getY(), b.getZ()));
                if(items != null){
                    for (ItemStack item : items){
                        location.getWorld().dropItemNaturally(location, item);
                    }
                }
                continue;
            }
            if(items == null){
                Inventory inv = ((Chest) b.getState()).getBlockInventory();
                items = new ArrayList<>();
                for (ItemStack item : inv) {
                    if(ItemUtil.isNull(item)) continue;
                    items.add(item);
                }
                inv.clear();
                b.getState().update(true);
                needUpdateDeathChestConf = true;
            }
            DEATHCHEST.put(hashBlockLocation(location), new DeathChest(s, owner, location, date, items));
        }

        String s = CONF.getString("death_chest.material");
        if(s == null) deathChestMaterial = Material.CHEST;
        else {
            Material mt = (Material) EnumUtil.findEnum(Material.class, s);
            deathChestMaterial = mt == null ? Material.CHEST : mt;
        }
    }

    @Override
    public void onLoad(){
        if(getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuardHook = new WorldGuardHook();
        }
    }

    @Override
    public void onEnable(){
        task = new TaskHelper(this);
        PaperCommandManager pcm = new PaperCommandManager(this);
        pcm.enableUnstableAPI("help");
        pcm.registerCommand(new AdminCmd(this));
        getServer().getPluginManager().registerEvents(this, this);

        DC_CONF.options().header("This is the data file. Please do not touch if you are not sure!");
        initConf();

        if(getServer().getPluginManager().isPluginEnabled("Lands")) {
            landsHook = new LandsHook(this);
            getLogger().info("Hooked to Lands");
        }
        if(getServer().getPluginManager().isPluginEnabled("Towny")) {
            townyHook = new TownyHook();
            getLogger().info("Hooked to Towny");
        }
        if(getServer().getPluginManager().isPluginEnabled("Factions")) {
            saberFactionsHook = new SaberFactionsHook();
            getLogger().info("Hooked to SaberFactions");
        }

        if(CONF.getBoolean("check_update")){
            task.newDelayedAsyncTask(() -> {
                int expect = SpigotApiUtil.getResourceLatestVersion("31673").chars().sum();
                int current = getDescription().getVersion().chars().sum();
                if(current < expect) {
                    chat.messageConsole("&cThis plugin is outdated! Please update it <3");
                    needUpdatePlugin = true;
                }
            }, 60); // okay wait for the task or get the f**king LinkageError
        }

        task.newAsyncTimerTask(() -> {
            WORLD2GROUP.forEach((key, value) -> {
                World world = getServer().getWorld(key);
                if(world == null) return;
                for(TimeKeep timeKeep : value.getTimeKeep()){
                    TimeKeep x = WORLD2TIMEKEEP.get(world.getName());
                    long cur = world.getTime();
                    if(cur < timeKeep.getFrom() || cur > timeKeep.getTo()) {
                        if(x != null && x == timeKeep) WORLD2TIMEKEEP.remove(world.getName());
                        continue;
                    }
                    if(x != null && x == timeKeep) continue;
                    WORLD2TIMEKEEP.put(world.getName(), timeKeep);
                    if(timeKeep.getSound() != null)
                        world.getPlayers().forEach(p -> {
                            p.playSound(p.getLocation(), timeKeep.getSound(), 3f, 1f);
                        });
                    timeKeep.getChatBroadcast().forEach(s -> Chat.noPrefix().broadcast(world, s));
                    if(timeKeep.getActionBarBroadcast() != null)
                        ActionBar.noPrefix().broadcast(world, timeKeep.getActionBarBroadcast());
                    break;
                }
            });

            DEATHCHEST.forEach((id, dc) -> {
                if(dc.getLocation().getBlock().getType() != deathChestMaterial) {
                    Location l = dc.getLocation();
                    DEATHCHEST.remove(id);
                    if(dc.getItems() != null){
                        task.newTask(() -> {
                            for (ItemStack item : dc.getItems()){
                                l.getWorld().dropItemNaturally(l, item);
                            }
                        });
                    }
                    needUpdateDeathChestConf = true;
                } else if(CONF.getBoolean("death_chest.signal_effect")) {
                    Location loc = dc.getLocation().clone();
                    int y = loc.getBlockY();
                    int my = Math.min(loc.getWorld().getMaxHeight(), y + 25);
                    while (y < my){
                        loc.setY(y++);
                        loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 1, 0, 0, 0, 0, null);
                    }
                }
            });

            if(needUpdateDeathChestConf){
                needUpdateDeathChestConf = false;
                DC_CONF.getKeys(false).forEach(s -> DC_CONF.set(s, null));
                DEATHCHEST.forEach((id, dc) -> {
                    ConfigurationSection c = new YamlConfiguration();
                    c.set("owner.lsb", dc.getOwner().getLeastSignificantBits());
                    c.set("owner.msb", dc.getOwner().getMostSignificantBits());
                    c.set("location", dc.getLocation());
                    c.set("date", dc.getDate());
                    c.set("items", dc.getItems());
                    DC_CONF.set(dc.getId(), c);
                });
                try {
                    DC_CONF.save(new File(getDataFolder(), "death_chests.yml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 20);

        new Metrics(this, 6141);

        ReflectionUtil.setDeclaredStaticField(ApiProvider.class, "api", this);
    }

    @NotNull
    @Override
    public ItemStack getSoulGem() {
        return soulGem.clone();
    }

    @Override
    public boolean isSoulGem(@Nullable ItemStack itemStack) {
        if(itemStack == null) return false;
        CompoundTag compound = CompoundTag.of(itemStack);
        CompoundTag tag = compound.get("tag", CompoundTag.class);
        return tag != null && tag.has(CONF.getString("soul_gem.nbt_tag"));
    }

    @Override
    @NotNull
    public List<World> getKeepItemWorlds() {
        return WORLD2TIMEKEEP.entrySet().stream().filter(x -> x.getValue().isKeepItem()).map(Map.Entry::getKey).map(Bukkit::getWorld).collect(ImmutableList.toImmutableList());
    }

    @Override
    @NotNull
    public List<World> getKeepExpWorlds() {
        return WORLD2TIMEKEEP.entrySet().stream().filter(x -> x.getValue().isKeepExp()).map(Map.Entry::getKey).map(Bukkit::getWorld).collect(ImmutableList.toImmutableList());
    }

    @Override
    @NotNull
    public List<WorldGroup> getWorldGroups() {
        return ImmutableList.copyOf(WORLDGROUPS);
    }

    @Override
    public void addWorldGroup(@NotNull WorldGroup worldGroup) {
        WORLDGROUPS.add(worldGroup);
        worldGroup.getWorlds().forEach(s -> WORLD2GROUP.putIfAbsent(s, worldGroup));
    }

    @Override
    public void removeWorldGroup(@NotNull WorldGroup worldGroup) {
        WORLDGROUPS.remove(worldGroup);
        worldGroup.getWorlds().forEach(WORLD2GROUP::remove);
    }

    @Override
    @NotNull
    public List<DeathChest> getDeathChests() {
        return ImmutableList.copyOf(DEATHCHEST.values());
    }

    @Override
    @Nullable
    public DeathChest getDeathChestAt(@NotNull Location location) {
        return DEATHCHEST.get(hashBlockLocation(location));
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        if(needUpdatePlugin && event.getPlayer().hasPermission("keep.update")) {
            chat.message(event.getPlayer(), "&cThis plugin is outdated! Please update it <3");
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        if(event.hasBlock()){
            Block b = event.getClickedBlock();
            int x = hashBlockLocation(b.getLocation());
            DeathChest dc = DEATHCHEST.get(x);
            if(dc != null) {
                if(CONF.getBoolean("death_chest.lock_death_chest") && !event.getPlayer().getUniqueId().equals(dc.getOwner())){
                    event.setCancelled(true);
                    return;
                }
                // set this to air and the main task will drop inner items automatically
                b.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void explode(EntityExplodeEvent event){
        for (Iterator<Block> it = event.blockList().iterator(); it.hasNext(); ) {
            Block b = it.next();
            int h = hashBlockLocation(b.getLocation());
            if(DEATHCHEST.containsKey(h)) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void explode(BlockExplodeEvent event){
        for (Iterator<Block> it = event.blockList().iterator(); it.hasNext(); ) {
            Block b = it.next();
            int h = hashBlockLocation(b.getLocation());
            if(DEATHCHEST.containsKey(h)) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void death(PlayerDeathEvent event){
        Player p = event.getEntity();
        Location location = p.getLocation();
        WorldGroup wg = WORLD2GROUP.get(p.getWorld().getName());
        if(wg == null) return;
        // we will keep if either the player has specified permissions or he is on a safe world
        boolean keepItem = event.getKeepInventory();
        boolean keepExp = event.getKeepLevel();
        boolean soulGem = false;
        boolean deathChest = false;
        TimeKeep tk = WORLD2TIMEKEEP.get(p.getWorld().getName());
        if(tk != null){
            // oh be sure the player does not have those permissions, we do not want he loses his items only because his world is unsafe ~
            if(!keepItem) keepItem = tk.isKeepItem();
            if(!keepExp) keepExp = tk.isKeepExp();
            soulGem = tk.isAllowSoulGem();
            deathChest = tk.isEnableDeathChest();

            if(landsHook != null){
                ClaimStatus status = landsHook.getAreaStatus(p);
                switch (status) {
                    case WILD: {
                        if(tk.isKeepItemInLandsWilderness()) {
                            keepItem = true;
                        }
                        if(tk.isKeepExpInLandsWilderness()) {
                            keepExp = true;
                        }
                        break;
                    }
                    case OWN: {
                        if(tk.isKeepItemInArea()) {
                            keepItem = true;
                        }
                        if(tk.isKeepExpInArea()) {
                            keepExp = true;
                        }
                        break;
                    }
                }
            }
            else if(townyHook != null){
                ClaimStatus status = townyHook.getAreaStatus(p);
                switch (status) {
                    case WILD: {
                        if(tk.isKeepItemInTownyWilderness()) {
                            keepItem = true;
                        }
                        if(tk.isKeepExpInTownyWilderness()) {
                            keepExp = true;
                        }
                        break;
                    }
                    case OWN: {
                        if(tk.isKeepItemInTown()) {
                            keepItem = true;
                        }
                        if(tk.isKeepExpInTown()) {
                            keepExp = true;
                        }
                        break;
                    }
                }
            }
            else if(saberFactionsHook != null){
                ClaimStatus status = saberFactionsHook.getAreaStatus(p);
                switch (status) {
                    case WILD: {
                        if(tk.isKeepItemInFactionWilderness()) {
                            keepItem = true;
                        }
                        if(tk.isKeepExpInFactionWilderness()) {
                            keepExp = true;
                        }
                        break;
                    }
                    case OWN: {
                        if(tk.isKeepItemInFaction()) {
                            keepItem = true;
                        }
                        if(tk.isKeepExpInFaction()) {
                            keepExp = true;
                        }
                        break;
                    }
                }
            }
        }

        if(worldGuardHook != null) {
            Boolean[] flags = worldGuardHook.getFlagState(location);
            if(flags[0] != null) keepItem = flags[0];
            if(flags[1] != null) keepExp = flags[1];
            if(flags[2] != null) soulGem = flags[2];
        }

        if (p.hasPermission("keep.bypass.item")) keepItem = true;
        if (p.hasPermission("keep.bypass.exp")) keepExp = true;

        event.setKeepInventory(true);
        event.getDrops().clear(); // 1.14.4 fix
        List<ItemStack> keptItems = new LinkedList<>(); //  uses linked list to keep the item order
        List<ItemStack> dropItems = new ArrayList<>();
        if(keepItem) keptItems.addAll(ArrayUtil.toList(p.getInventory().getContents()));
        else if(soulGem) {
            List<ItemStack> tempItems = new LinkedList<>();
            ItemStack[] items = p.getInventory().getContents();
            boolean hasSoulGem = false;
            boolean cancelled = false;
            for (ItemStack item : items) {
                // do not remove "air item" as we must keep the item order
                if (ItemUtil.isNull(item))
                    keptItems.add(new ItemStack(Material.AIR, 1));
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
                            getServer().dispatchCommand(getServer().getConsoleSender(), s.replace("{player}", p.getName()));
                        });
                    }
                } else if(hasSoulGem) keptItems.add(item);
                else tempItems.add(item); // do not remove item directly, we do not know if the inventory has a soul gem
            }
            if (!hasSoulGem) {
                dropItems.addAll(tempItems);
            }
        } else {
            dropItems.addAll(ArrayUtil.toList(p.getInventory().getContents()));
        }

        PlayerKeepEvent ev = new PlayerKeepEvent(p, dropItems, keptItems, keepExp);
        getServer().getPluginManager().callEvent(ev);

        if(ev.isKeepExp()) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        if(!ev.getDropItems().isEmpty()){
            if(deathChest) {
                Block b = location.getBlock();
                if (b.getType() != deathChestMaterial) {
                    b.setType(deathChestMaterial);
                }
                DeathChest chest = DEATHCHEST.compute(hashBlockLocation(location), (id, dc) -> {
                    if(dc == null) {
                        return new DeathChest(
                                new String(RandomUtil.randomLetters(10)),
                                p.getUniqueId(),
                                b.getLocation(),
                                System.currentTimeMillis(),
                                new ArrayList<>()
                        );
                    }
                    else {
                        boolean overridable = CONF.getBoolean("death_chest.merge_chest_on_duplicate");
                        // override the old death chest!
                        return new DeathChest(
                                dc.getId(),
                                p.getUniqueId(),
                                dc.getLocation(),
                                System.currentTimeMillis(),
                                dc.getItems() == null || !overridable ? null : new ArrayList<>(dc.getItems())
                        );
                    }
                });
                needUpdateDeathChestConf = true;
                if(!ev.getDropItems().isEmpty()) {
                    if(chest.getItems() == null){
                        chest.setItems(new ArrayList<>());
                    }
                    for (ItemStack item : ev.getDropItems()) {
                        if (!ItemUtil.isNull(item)) {
                            chest.getItems().add(item);
                        }
                    }
                }
            } else {
                for (ItemStack item : ev.getDropItems()){
                    if(!ItemUtil.isNull(item)) {
                        p.getWorld().dropItemNaturally(location, item);
                    }
                }
            }
        }
        p.getInventory().setContents(CollectionUtil.toArray(ev.getKeepItems(), ItemStack.class));
    }
}

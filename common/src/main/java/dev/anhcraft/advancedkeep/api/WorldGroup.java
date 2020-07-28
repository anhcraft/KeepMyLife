package dev.anhcraft.advancedkeep.api;

import com.google.common.base.Preconditions;
import dev.anhcraft.confighelper.ConfigHelper;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.Middleware;
import dev.anhcraft.confighelper.annotation.Schema;
import dev.anhcraft.confighelper.exception.InvalidValueException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class WorldGroup {
    private final String id;

    @Key("worlds")
    private List<String> worlds = new ArrayList<>();

    @Key("time_keep")
    private List<TimeKeep> timeKeep = new ArrayList<>();

    public WorldGroup(@NotNull String id) {
        Preconditions.checkNotNull(id);
        this.id = id;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public List<String> getWorlds() {
        return worlds;
    }

    @NotNull
    public List<TimeKeep> getTimeKeep() {
        return timeKeep;
    }

    @Middleware(Middleware.Direction.CONFIG_TO_SCHEMA)
    @Nullable
    private Object c2s(ConfigSchema.Entry entry, @Nullable Object value){
        if(value != null) {
            if(entry.getKey().equals("time_keep")) {
                try {
                    List<TimeKeep> timeKeeps = new ArrayList<>();
                    ConfigurationSection conf = (ConfigurationSection) value;
                    for (String k : conf.getKeys(false)) {
                        timeKeeps.add(ConfigHelper.readConfig(conf.getConfigurationSection(k), ConfigSchema.of(TimeKeep.class), new TimeKeep(k, this)));
                    }
                    return timeKeeps;
                } catch (InvalidValueException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    @Middleware(Middleware.Direction.SCHEMA_TO_CONFIG)
    @Nullable
    private Object s2c(ConfigSchema.Entry entry, @Nullable Object value){
        if(value != null) {
            if(entry.getKey().equals("time_keep")) {
                YamlConfiguration conf = new YamlConfiguration();
                for (TimeKeep k : (List<TimeKeep>) value) {
                    YamlConfiguration c = new YamlConfiguration();
                    ConfigHelper.writeConfig(c, ConfigSchema.of(TimeKeep.class), k);
                    conf.set(k.getId(), c);
                }
                return conf;
            }
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldGroup that = (WorldGroup) o;
        return timeKeep.equals(that.timeKeep) &&
                worlds.equals(that.worlds) &&
                id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package dev.anhcraft.advancedkeep.api;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldGroup {
    private final List<TimeKeep> timeKeep = new ArrayList<>();
    private final List<String> worlds = new ArrayList<>();
    private String id;

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

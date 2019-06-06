package dev.anhcraft.keepmylife.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldGroup {
    private final List<TimeKeep> timeKeep = new ArrayList<>();
    private final List<String> worlds = new ArrayList<>();
    private String id;
    private boolean allowSoulGem;

    public WorldGroup(String id) {
        this.id = id;
    }

    public boolean isAllowSoulGem() {
        return allowSoulGem;
    }

    public void setAllowSoulGem(boolean allowSoulGem) {
        this.allowSoulGem = allowSoulGem;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public String getId() {
        return id;
    }

    public List<TimeKeep> getTimeKeep() {
        return timeKeep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldGroup that = (WorldGroup) o;
        return allowSoulGem == that.allowSoulGem &&
                timeKeep.equals(that.timeKeep) &&
                worlds.equals(that.worlds) &&
                id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

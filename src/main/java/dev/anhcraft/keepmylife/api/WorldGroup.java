package dev.anhcraft.keepmylife.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorldGroup {
    private final List<TimeKeep> timeKeep = new ArrayList<>();
    private final List<String> worlds = new ArrayList<>();
    private String id;
    private boolean allowSoulGem;
    private boolean keepItemOnOwnedLandChunk;
    private boolean keepItemOnInvitedLandChunk;
    private boolean keepExpOnOwnedLandChunk;
    private boolean keepExpOnInvitedLandChunk;

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

    public boolean isKeepItemOnOwnedLandChunk() {
        return keepItemOnOwnedLandChunk;
    }

    public void setKeepItemOnOwnedLandChunk(boolean keepItemOnOwnedLandChunk) {
        this.keepItemOnOwnedLandChunk = keepItemOnOwnedLandChunk;
    }

    public boolean isKeepItemOnInvitedLandChunk() {
        return keepItemOnInvitedLandChunk;
    }

    public void setKeepItemOnInvitedLandChunk(boolean keepItemOnInvitedLandChunk) {
        this.keepItemOnInvitedLandChunk = keepItemOnInvitedLandChunk;
    }

    public boolean isKeepExpOnOwnedLandChunk() {
        return keepExpOnOwnedLandChunk;
    }

    public void setKeepExpOnOwnedLandChunk(boolean keepExpOnOwnedLandChunk) {
        this.keepExpOnOwnedLandChunk = keepExpOnOwnedLandChunk;
    }

    public boolean isKeepExpOnInvitedLandChunk() {
        return keepExpOnInvitedLandChunk;
    }

    public void setKeepExpOnInvitedLandChunk(boolean keepExpOnInvitedLandChunk) {
        this.keepExpOnInvitedLandChunk = keepExpOnInvitedLandChunk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldGroup that = (WorldGroup) o;
        return allowSoulGem == that.allowSoulGem &&
                keepItemOnOwnedLandChunk == that.keepItemOnOwnedLandChunk &&
                keepItemOnInvitedLandChunk == that.keepItemOnInvitedLandChunk &&
                keepExpOnOwnedLandChunk == that.keepExpOnOwnedLandChunk &&
                keepExpOnInvitedLandChunk == that.keepExpOnInvitedLandChunk &&
                Objects.equals(timeKeep, that.timeKeep) &&
                Objects.equals(worlds, that.worlds) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

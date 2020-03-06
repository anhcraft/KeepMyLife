package dev.anhcraft.advancedkeep.api;

import com.google.common.base.Preconditions;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimeKeep {
    private final List<String> chatBroadcast = new ArrayList<>();
    private String id;
    private long from;
    private long to;
    private Sound sound;
    private String actionBarBroadcast;
    private WeakReference<WorldGroup> worldGroup;
    private boolean keepItem;
    private boolean keepExp;
    private boolean enableDeathChest;
    private boolean allowSoulGem;
    private boolean keepExpInArea;
    private boolean keepItemInArea;
    private boolean keepExpInLandsWilderness;
    private boolean keepItemInLandsWilderness;
    private boolean keepExpInTown;
    private boolean keepItemInTown;
    private boolean keepExpInTownyWilderness;
    private boolean keepItemInTownyWilderness;
    private boolean keepExpInFaction;
    private boolean keepItemInFaction;
    private boolean keepExpInFactionWilderness;
    private boolean keepItemInFactionWilderness;

    public TimeKeep(@NotNull String id, long from, long to, @NotNull WorldGroup worldGroup) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(worldGroup);
        this.id = id;
        this.from = from;
        this.to = to;
        this.worldGroup = new WeakReference<>(worldGroup);
    }

    @NotNull
    public String getId() {
        return id;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    @NotNull
    public List<String> getChatBroadcast() {
        return chatBroadcast;
    }

    @Nullable
    public String getActionBarBroadcast() {
        return actionBarBroadcast;
    }

    public void setActionBarBroadcast(@Nullable String actionBarBroadcast) {
        this.actionBarBroadcast = actionBarBroadcast;
    }

    public boolean isKeepItem() {
        return keepItem;
    }

    public void setKeepItem(boolean keepItem) {
        this.keepItem = keepItem;
    }

    public boolean isKeepExp() {
        return keepExp;
    }

    public void setKeepExp(boolean keepExp) {
        this.keepExp = keepExp;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    public void setSound(@Nullable Sound sound) {
        this.sound = sound;
    }

    @Nullable
    public WorldGroup getWorldGroup() {
        return worldGroup.get();
    }

    public boolean isEnableDeathChest() {
        return enableDeathChest;
    }

    public void setEnableDeathChest(boolean enableDeathChest) {
        this.enableDeathChest = enableDeathChest;
    }

    public boolean isAllowSoulGem() {
        return allowSoulGem;
    }

    public void setAllowSoulGem(boolean allowSoulGem) {
        this.allowSoulGem = allowSoulGem;
    }

    public boolean isKeepExpInArea() {
        return keepExpInArea;
    }

    public void setKeepExpInArea(boolean keepExpInArea) {
        this.keepExpInArea = keepExpInArea;
    }

    public boolean isKeepItemInArea() {
        return keepItemInArea;
    }

    public void setKeepItemInArea(boolean keepItemInArea) {
        this.keepItemInArea = keepItemInArea;
    }

    public boolean isKeepExpInLandsWilderness() {
        return keepExpInLandsWilderness;
    }

    public void setKeepExpInLandsWilderness(boolean keepExpInLandsWilderness) {
        this.keepExpInLandsWilderness = keepExpInLandsWilderness;
    }

    public boolean isKeepItemInLandsWilderness() {
        return keepItemInLandsWilderness;
    }

    public void setKeepItemInLandsWilderness(boolean keepItemInLandsWilderness) {
        this.keepItemInLandsWilderness = keepItemInLandsWilderness;
    }

    public boolean isKeepExpInTown() {
        return keepExpInTown;
    }

    public void setKeepExpInTown(boolean keepExpInTown) {
        this.keepExpInTown = keepExpInTown;
    }

    public boolean isKeepItemInTown() {
        return keepItemInTown;
    }

    public void setKeepItemInTown(boolean keepItemInTown) {
        this.keepItemInTown = keepItemInTown;
    }

    public boolean isKeepExpInTownyWilderness() {
        return keepExpInTownyWilderness;
    }

    public void setKeepExpInTownyWilderness(boolean keepExpInTownyWilderness) {
        this.keepExpInTownyWilderness = keepExpInTownyWilderness;
    }

    public boolean isKeepItemInTownyWilderness() {
        return keepItemInTownyWilderness;
    }

    public void setKeepItemInTownyWilderness(boolean keepItemInTownyWilderness) {
        this.keepItemInTownyWilderness = keepItemInTownyWilderness;
    }

    public boolean isKeepExpInFaction() {
        return keepExpInFaction;
    }

    public void setKeepExpInFaction(boolean keepExpInFaction) {
        this.keepExpInFaction = keepExpInFaction;
    }

    public boolean isKeepItemInFaction() {
        return keepItemInFaction;
    }

    public void setKeepItemInFaction(boolean keepItemInFaction) {
        this.keepItemInFaction = keepItemInFaction;
    }

    public boolean isKeepExpInFactionWilderness() {
        return keepExpInFactionWilderness;
    }

    public void setKeepExpInFactionWilderness(boolean keepExpInFactionWilderness) {
        this.keepExpInFactionWilderness = keepExpInFactionWilderness;
    }

    public boolean isKeepItemInFactionWilderness() {
        return keepItemInFactionWilderness;
    }

    public void setKeepItemInFactionWilderness(boolean keepItemInFactionWilderness) {
        this.keepItemInFactionWilderness = keepItemInFactionWilderness;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, worldGroup.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeKeep timeKeep = (TimeKeep) o;
        return from == timeKeep.from &&
                to == timeKeep.to &&
                keepItem == timeKeep.keepItem &&
                keepExp == timeKeep.keepExp &&
                enableDeathChest == timeKeep.enableDeathChest &&
                allowSoulGem == timeKeep.allowSoulGem &&
                keepExpInArea == timeKeep.keepExpInArea &&
                keepItemInArea == timeKeep.keepItemInArea &&
                keepExpInLandsWilderness == timeKeep.keepExpInLandsWilderness &&
                keepItemInLandsWilderness == timeKeep.keepItemInLandsWilderness &&
                keepExpInTown == timeKeep.keepExpInTown &&
                keepItemInTown == timeKeep.keepItemInTown &&
                keepExpInTownyWilderness == timeKeep.keepExpInTownyWilderness &&
                keepItemInTownyWilderness == timeKeep.keepItemInTownyWilderness &&
                keepExpInFaction == timeKeep.keepExpInFaction &&
                keepItemInFaction == timeKeep.keepItemInFaction &&
                keepExpInFactionWilderness == timeKeep.keepExpInFactionWilderness &&
                keepItemInFactionWilderness == timeKeep.keepItemInFactionWilderness &&
                Objects.equals(chatBroadcast, timeKeep.chatBroadcast) &&
                id.equals(timeKeep.id) &&
                sound == timeKeep.sound &&
                Objects.equals(actionBarBroadcast, timeKeep.actionBarBroadcast) &&
                worldGroup.equals(timeKeep.worldGroup);
    }
}

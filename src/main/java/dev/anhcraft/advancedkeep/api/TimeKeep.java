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
    private boolean keepItemOnOwnedLandChunk;
    private boolean keepItemOnInvitedLandChunk;
    private boolean keepExpOnOwnedLandChunk;
    private boolean keepExpOnInvitedLandChunk;

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
        TimeKeep timeKeep = (TimeKeep) o;
        return from == timeKeep.from &&
                to == timeKeep.to &&
                keepItem == timeKeep.keepItem &&
                keepExp == timeKeep.keepExp &&
                enableDeathChest == timeKeep.enableDeathChest &&
                allowSoulGem == timeKeep.allowSoulGem &&
                keepItemOnOwnedLandChunk == timeKeep.keepItemOnOwnedLandChunk &&
                keepItemOnInvitedLandChunk == timeKeep.keepItemOnInvitedLandChunk &&
                keepExpOnOwnedLandChunk == timeKeep.keepExpOnOwnedLandChunk &&
                keepExpOnInvitedLandChunk == timeKeep.keepExpOnInvitedLandChunk &&
                chatBroadcast.equals(timeKeep.chatBroadcast) &&
                id.equals(timeKeep.id) &&
                sound == timeKeep.sound &&
                Objects.equals(actionBarBroadcast, timeKeep.actionBarBroadcast) &&
                Objects.equals(worldGroup.get(), timeKeep.worldGroup.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, worldGroup.get());
    }
}

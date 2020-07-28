package dev.anhcraft.advancedkeep.api;

import com.google.common.base.Preconditions;
import dev.anhcraft.confighelper.annotation.IgnoreValue;
import dev.anhcraft.confighelper.annotation.Key;
import dev.anhcraft.confighelper.annotation.PrettyEnum;
import dev.anhcraft.confighelper.annotation.Schema;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@Schema
public class TimeKeep {
    private final String id;
    private final WeakReference<WorldGroup> worldGroup;

    @Key("from")
    private long from;

    @Key("to")
    private long to;

    @Key("broadcast.chat")
    @IgnoreValue(ifNull = true)
    private List<String> chatBroadcast = new ArrayList<>();

    @Key("broadcast.action_bar")
    private String actionBarBroadcast;

    @Key("sound")
    @PrettyEnum
    private Sound sound;

    @Key("keep_item")
    private boolean keepItem;

    @Key("keep_exp")
    private boolean keepExp;

    @Key("enable_death_chest")
    private boolean enableDeathChest;

    @Key("allow_soul_gem")
    private boolean allowSoulGem;

    @Key("lands.keep_exp_in_areas")
    private boolean keepExpInArea;

    @Key("lands.keep_items_in_areas")
    private boolean keepItemInArea;

    @Key("lands.keep_exp_in_wilderness")
    private boolean keepExpInLandsWilderness;

    @Key("lands.keep_items_in_wilderness")
    private boolean keepItemInLandsWilderness;

    @Key("towny.keep_exp_in_town")
    private boolean keepExpInTown;

    @Key("towny.keep_items_in_town")
    private boolean keepItemInTown;

    @Key("towny.keep_exp_in_wilderness")
    private boolean keepExpInTownyWilderness;

    @Key("towny.keep_items_in_wilderness")
    private boolean keepItemInTownyWilderness;

    @Key("faction.keep_exp_in_faction")
    private boolean keepExpInFaction;

    @Key("faction.keep_items_in_faction")
    private boolean keepItemInFaction;

    @Key("faction.keep_exp_in_wilderness")
    private boolean keepExpInFactionWilderness;

    @Key("faction.keep_items_in_wilderness")
    private boolean keepItemInFactionWilderness;

    public TimeKeep(@NotNull String id, @NotNull WorldGroup worldGroup) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(worldGroup);
        this.id = id;
        this.worldGroup = new WeakReference<>(worldGroup);
    }

    @NotNull
    public String getId() {
        return id;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
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

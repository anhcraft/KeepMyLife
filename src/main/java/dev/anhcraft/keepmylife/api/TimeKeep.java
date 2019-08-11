package dev.anhcraft.keepmylife.api;

import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TimeKeep {
    private final List<String> chatBroadcast = new ArrayList<>();
    private String id;
    private long from;
    private long to;
    private boolean keepItem;
    private boolean keepExp;
    private Sound sound;
    private String actionBarBroadcast;
    private WorldGroup worldGroup;

    public TimeKeep(String id, long from, long to, WorldGroup worldGroup) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.worldGroup = worldGroup;
    }

    public List<String> getChatBroadcast() {
        return chatBroadcast;
    }

    public String getActionBarBroadcast() {
        return actionBarBroadcast;
    }

    public void setActionBarBroadcast(String actionBarBroadcast) {
        this.actionBarBroadcast = actionBarBroadcast;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
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

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public String getId() {
        return id;
    }

    public WorldGroup getWorldGroup() {
        return worldGroup;
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
                chatBroadcast.equals(timeKeep.chatBroadcast) &&
                id.equals(timeKeep.id) &&
                sound == timeKeep.sound &&
                Objects.equals(actionBarBroadcast, timeKeep.actionBarBroadcast) &&
                worldGroup.equals(timeKeep.worldGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, worldGroup);
    }
}

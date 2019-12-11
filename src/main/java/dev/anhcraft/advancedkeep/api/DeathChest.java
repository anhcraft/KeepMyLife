package dev.anhcraft.advancedkeep.api;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class DeathChest {
    private String id;
    private UUID owner;
    private Location location;
    private long date;

    public DeathChest(@NotNull String id, @NotNull UUID owner, @NotNull Location location, long date) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(owner);
        Preconditions.checkNotNull(location);
        this.id = id;
        this.owner = owner;
        this.location = location;
        this.date = date;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public UUID getOwner() {
        return owner;
    }

    @NotNull
    public Location getLocation() {
        return location;
    }

    public long getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeathChest that = (DeathChest) o;
        return id.equals(that.id) &&
                date == that.date &&
                owner.equals(that.owner) &&
                location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package me.wolf.wskywars.chest;

import org.bukkit.Location;

public class SkywarsChest {

    private Location location;
    private ChestType chestType;
    private int itemsPerChest;

    public SkywarsChest(final Location location, final ChestType chestType, final int itemsPerChest) {
        this.location = location;
        this.chestType = chestType;
        this.itemsPerChest = itemsPerChest;
    }

    public int getItemsPerChest() {
        return itemsPerChest;
    }

    public void setItemsPerChest(int itemsPerChest) {
        this.itemsPerChest = itemsPerChest;
    }

    public ChestType getChestType() {
        return chestType;
    }

    public void setChestType(ChestType chestType) {
        this.chestType = chestType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

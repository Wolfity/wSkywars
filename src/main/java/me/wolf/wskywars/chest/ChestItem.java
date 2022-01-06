package me.wolf.wskywars.chest;

import org.bukkit.inventory.ItemStack;

public class ChestItem {

    private final ChestType chestType;
    private final ItemStack item;

    public ChestItem(final ChestType chestType, final ItemStack item) {
        this.chestType = chestType;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public ChestType getChestType() {
        return chestType;
    }

}

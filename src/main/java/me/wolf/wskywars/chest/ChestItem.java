package me.wolf.wskywars.chest;

import me.wolf.wskywars.utils.ItemUtils;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChestItem {

    private final ChestType chestType;
    private final int amount;
    private final Material material;
    private final String name;

    public ChestItem(final ChestType chestType, final int amount, final Material material, final String name) {
        this.chestType = chestType;
        this.amount = amount;
        this.material = material;
        this.name = Utils.colorize(name);

    }

    public ItemStack getItem() {
        return ItemUtils.createItem(material, name, amount);
    }

    public ChestType getChestType() {
        return chestType;
    }

}

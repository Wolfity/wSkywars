package me.wolf.wskywars.cosmetics;

import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public abstract class Cosmetic implements Comparable<Cosmetic> {

    private final int price;
    private final String name;
    private final ItemStack icon;
    private final CosmeticType cosmeticType;
    private boolean isActive;

    public Cosmetic(final String name, final ItemStack icon, final int price, final CosmeticType cosmeticType) {
        this.name = name;
        this.price = price;
        this.icon = icon;
        this.cosmeticType = cosmeticType;
    }

    public CosmeticType getCosmeticType() {
        return cosmeticType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getPrice() {
        return price;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public ItemStack getLockedIcon() {
        return ItemUtils.createItem(Material.BARRIER, "&cNot Unlocked: &4" + name);
    }

    @Override
    public int compareTo(Cosmetic o) {
        return name.compareTo(o.getName());
    }

}

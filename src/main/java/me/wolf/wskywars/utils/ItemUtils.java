package me.wolf.wskywars.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtils {

    public static ItemStack createItem(final Material mat, final String display, final int amount) {
        final ItemStack is = new ItemStack(mat, amount);
        final ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final String display, final int amount, final short data) {
        final ItemStack is = new ItemStack(mat, amount, data);
        final ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final String display, final short data) {
        final ItemStack is = new ItemStack(mat, data);
        final ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static ItemStack createItem(final Material mat, final String display) {
        final ItemStack is = new ItemStack(mat);
        final ItemMeta meta = is.getItemMeta();

        meta.setDisplayName(Utils.colorize(display));
        is.setItemMeta(meta);

        return is;
    }

    public static boolean isArmor(final Material material) {
        return material.name().contains("_CHESTPLATE") || material.name().contains("_LEGGINGS") || material.name().contains("_BOOTS") || material.name().contains("_HELMET");
    }

    public static boolean isHelmet(final Material material) {
        return material.name().contains("_HELMET");
    }

    public static boolean isChestplate(final Material material) {
        return material.name().contains("_CHESTPLATE");
    }

    public static boolean isLeggings(final Material material) {
        return material.name().contains("_LEGGINGS");

    }

    public static boolean isBoots(final Material material) {
        return material.name().contains("_BOOTS");
    }


    private ItemUtils() {}

}

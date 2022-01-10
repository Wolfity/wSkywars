package me.wolf.wskywars.chest;

import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.files.YamlConfig;
import me.wolf.wskywars.game.Game;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class SkywarsChestManager {

    private final Set<ChestItem> chestItems = new HashSet<>();

    /**
     * Loads in all chest items from the config file
     */
    public void loadChestItems(final YamlConfig cfg) {

        for (final String s : cfg.getConfig().getConfigurationSection("chest-items").getKeys(false)) {
            final Material material = Material.valueOf(cfg.getConfig().getString("chest-items." + s + ".material"));
            final String name = cfg.getConfig().getString("chest-items." + s + ".name");
            final ChestType chestType = ChestType.valueOf(cfg.getConfig().getString("chest-items." + s + ".type"));
            final int amount = cfg.getConfig().getInt("chest-items." + s + ".amount");
            final boolean hasEnchants = cfg.getConfig().getBoolean("chest-items." + s + ".has-enchants");
            if (!hasEnchants) {
                chestItems.add(new ChestItem(chestType, ItemUtils.createItem(material, name, amount)));
            } else {
                final String ench = cfg.getConfig().getString("chest-items." + s + ".enchants");
                final String[] splitEnch = ench.split(";"); // string looks like -> DURABILITY:5 SHARPNESS:3
                final ItemStack is = ItemUtils.createItem(material, name, amount);
                for (final String enchantment : splitEnch) {
                    final String[] finalData = enchantment.split(":"); // string is split up and looks like DURABILITY 5
                    is.addUnsafeEnchantment(Objects.requireNonNull(Enchantment.getByName(finalData[0])), Integer.parseInt(finalData[1]));
                }
                chestItems.add(new ChestItem(chestType, is));
            }
        }

    }

    /**
     * @param game the game the chests will be placed and filled in
     *             gets called in the beginning of a game
     */
    public void fillChests(final Game game) {
        final Arena arena = game.getArena();
        arena.getChests().forEach(skywarsChest -> {
            skywarsChest.getLocation().getBlock().setType(Material.CHEST);
            final Chest chest = (Chest) skywarsChest.getLocation().getBlock().getState();
            if(!hasEnoughAvailableSlots(chest.getInventory(), skywarsChest.getItemsPerChest())) return;

            for (int i = 0; i < skywarsChest.getItemsPerChest(); i++) {
                chest.getInventory().setItem(getRandomSlot(chest.getInventory()), getRandomItem(skywarsChest.getChestType()).getItem());
            }

        });
    }

    /**
     * @param chestType Island Chest or Mid Chest
     * @return a random item from the specified chest type
     */
    private ChestItem getRandomItem(final ChestType chestType) {
        final List<ChestItem> chestItemList = chestItems.stream().filter(chestItem -> chestItem.getChestType() == chestType).collect(Collectors.toList());
        final int randomIndex = new Random().nextInt(chestItemList.size());
        return chestItemList.get(randomIndex);
    }

    /**
     * @param inventory the chest inventory
     * @return a random available slot
     */
    private int getRandomSlot(final Inventory inventory) {
        int randomSlot = new Random().nextInt(inventory.getSize());
        while (inventory.getItem(randomSlot) != null) { // if the slot isn't empty, continue till an empty slot is found
            randomSlot = new Random().nextInt(inventory.getSize());
        }
        return randomSlot;
    }

    /**
     * @param inventory the chest's inventory
     * @param amt       the amount of free slots we are looking for
     * @return true if there are more than or equals of the amount of free slots
     */
    private boolean hasEnoughAvailableSlots(final Inventory inventory, final int amt) {
        int i = 0;
        for (final ItemStack item : inventory.getContents()) {
            if (item == null) {
                i++;
            }
        }
        return i >= amt;
    }
}

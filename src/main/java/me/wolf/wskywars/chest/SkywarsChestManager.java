package me.wolf.wskywars.chest;

import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.files.YamlConfig;
import me.wolf.wskywars.game.Game;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SkywarsChestManager {

    private Set<ChestItem> chestItems = new HashSet<>();

    /**
     * Loads in all chest items from the config file
     */
    public void loadChestItems(final YamlConfig cfg) {

        for (final String s : cfg.getConfig().getConfigurationSection("chest-items").getKeys(false)) {
            final Material material = Material.valueOf(cfg.getConfig().getString("chest-items." + s + ".material"));
            final String name = cfg.getConfig().getString("chest-items." + s + ".name");
            final ChestType chestType = ChestType.valueOf(cfg.getConfig().getString("chest-items." + s + ".type"));
            final int amount = cfg.getConfig().getInt("chest-items." + s + ".amount");
            chestItems.add(new ChestItem(chestType, amount, material, name));
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

            for (int i = 0; i < skywarsChest.getItemsPerChest(); i++) {
                chest.getInventory().setItem(getRandomSlot(chest.getInventory()), getRandomItem().getItem());
            }

        });
    }


    private ChestItem getRandomItem() {
        final int randomIndex = new Random().nextInt(chestItems.size());
        return new ArrayList<>(chestItems).get(randomIndex);
    }

    private int getRandomSlot(final Inventory inventory) {
        int randomSlot = new Random().nextInt(inventory.getSize());
        while (inventory.getItem(randomSlot) != null) { // if the slot isn't empty, continue till an empty slot is found
            randomSlot = new Random().nextInt(inventory.getSize());
        }
        return randomSlot;
    }
}

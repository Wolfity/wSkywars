package me.wolf.wskywars.menu;

import me.wolf.wskywars.chest.SkywarsChest;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkywarsMenu {

    private static final Map<UUID, SkywarsMenu> inventoriesByUUID = new HashMap<>();
    private static final Map<UUID, UUID> openInventories = new HashMap<>(); // <player, inv>
    private final UUID uuid;
    private final Inventory skywarsMenu;
    private final Map<Integer, ClickAction> actions;
    private final SkywarsPlayer owner;

    public SkywarsMenu(final int size, final String name, final SkywarsPlayer owner) {
        this.uuid = UUID.randomUUID();
        this.owner = owner;
        actions = new HashMap<>();

        inventoriesByUUID.put(uuid, this);
        skywarsMenu = Bukkit.createInventory(null, size, Utils.colorize(name));
    }

    public static Map<UUID, SkywarsMenu> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return openInventories;
    }

    public Inventory getSkywarsMenu() {
        return skywarsMenu;
    }

    public void setItem(final int slot, final ItemStack stack, final ClickAction action) {
        skywarsMenu.setItem(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void addItem(final ItemStack stack) {
        skywarsMenu.setItem(skywarsMenu.firstEmpty(), stack);
    }
    public void addItem(final ItemStack stack, final ClickAction action) {
        final int nextSlot = skywarsMenu.firstEmpty();
        skywarsMenu.setItem(nextSlot, stack);
        if(action != null) {
            actions.put(nextSlot, action);
        }
    }

    public void openSkywarsMenu(final SkywarsPlayer player) {
        player.getBukkitPlayer().openInventory(skywarsMenu);
        openInventories.put(player.getUuid(), getUuid());
    }

    public UUID getUuid() {
        return uuid;
    }

    public Map<Integer, ClickAction> getActions() {
        return actions;
    }



    public void setItem(final int slot, final ItemStack stack) {
        setItem(slot, stack, null);
    }

    public interface ClickAction {
        void click(final Player player);
    }
}

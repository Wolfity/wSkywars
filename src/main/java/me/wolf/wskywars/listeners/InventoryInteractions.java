package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.menu.types.CosmeticMenu;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryInteractions implements Listener {

    private final SkywarsPlugin plugin;

    public InventoryInteractions(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler // interaction with the items to open killeffect, wineffect, cage selection menus
    public void onMenuInteract(PlayerInteractEvent event) {
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        if (player.getPlayerState() != PlayerState.IN_LOBBY) return;
        if (event.getItem() == null) return;


        switch (event.getMaterial()) {
            case DIAMOND_SWORD:
                new CosmeticMenu(player, CosmeticType.KILLEFFECT, plugin.getScoreboard());
                break;
            case BLAZE_POWDER:
                new CosmeticMenu(player, CosmeticType.WINEFFECT, plugin.getScoreboard());
                break;
            case GREEN_STAINED_GLASS:
                break;
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId()).getPlayerState() != PlayerState.IN_GAME);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId()).getPlayerState() != PlayerState.IN_GAME);
    }

    // players can move items in their inventory only when ingame
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getWhoClicked().getUniqueId());

            final List<ItemStack> items = new ArrayList<>();
            items.add(event.getCurrentItem());
            items.add(event.getCursor());
            items.add((event.getClick() == ClickType.NUMBER_KEY) ?
                    event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem());
            for (ItemStack item : items) {
                if (item != null)
                    event.setCancelled(player.getPlayerState() != PlayerState.IN_GAME);
            }
        }
    }

}

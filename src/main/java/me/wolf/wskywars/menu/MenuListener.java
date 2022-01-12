package me.wolf.wskywars.menu;

import me.wolf.wskywars.player.PlayerManager;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class MenuListener implements Listener {

    private final PlayerManager playerManager;

    public MenuListener(final PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (playerManager.getSkywarsPlayer(event.getWhoClicked().getUniqueId()) == null) return;

        final SkywarsPlayer skywarsPlayer = playerManager.getSkywarsPlayer(event.getWhoClicked().getUniqueId());

        final UUID guiUUID = SkywarsMenu.getOpenInventories().get(skywarsPlayer.getUuid());
        if (guiUUID == null) return;

        event.setCancelled(true);
        final SkywarsMenu skywarsMenu = SkywarsMenu.getInventoriesByUUID().get(guiUUID);
        final SkywarsMenu.ClickAction clickAction = skywarsMenu.getActions().get(event.getSlot());

        if (clickAction == null) return;
        clickAction.click(skywarsPlayer.getBukkitPlayer());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        final SkywarsPlayer skywarsPlayer = playerManager.getSkywarsPlayer(event.getPlayer().getUniqueId());
        if (skywarsPlayer == null) return;

        SkywarsMenu.getOpenInventories().remove(skywarsPlayer.getUuid());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final SkywarsPlayer skywarsPlayer = playerManager.getSkywarsPlayer(event.getPlayer().getUniqueId());
        if (skywarsPlayer == null) return;

        SkywarsMenu.getOpenInventories().remove(skywarsPlayer.getUuid());
    }

}

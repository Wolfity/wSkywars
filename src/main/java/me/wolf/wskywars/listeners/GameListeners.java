package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.game.Game;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class GameListeners implements Listener {

    private final SkywarsPlugin plugin;

    public GameListeners(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKill(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final SkywarsPlayer killed = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        if (killed.getPlayerState() != PlayerState.IN_GAME) return;


        final Game game = plugin.getGameManager().getGameByPlayer(killed);


        if (event.getDamage() >= killed.getBukkitPlayer().getHealth()) {
            if (event.getDamager() instanceof Player) { // play kill effect
                plugin.getPlayerManager().getSkywarsPlayer(event.getDamager().getUniqueId()).getActiveKillEffect().playKillEffect(killed);
            }

            plugin.getGameManager().handleGameKill(game, event.getDamager().getUniqueId(), killed);
            // drop the killed player's contents
            for (final ItemStack is : killed.getInventory()) {
                if (is == null) continue;
                killed.getWorld().dropItem(killed.getLocation(), is);
            }
            // clear the killed player's inventory
            killed.getInventory().clear();


            event.setCancelled(true);
        }

    }

}

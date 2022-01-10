package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.game.Game;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class GameListeners implements Listener {

    private final SkywarsPlugin plugin;

    public GameListeners(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onKill(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final SkywarsPlayer killed = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        if (killed.getPlayerState() != PlayerState.IN_GAME) return;


        final Game game = plugin.getGameManager().getGameByPlayer(killed);


        if (event.getDamage() >= killed.getBukkitPlayer().getHealth()) {
            if (event.getDamager() instanceof Player) { // play kill effect
                plugin.getPlayerManager().getSkywarsPlayer(event.getDamager().getUniqueId()).getActiveKillEffect().playKillEffect(killed);
                System.out.println("played " + plugin.getPlayerManager().getSkywarsPlayer(event.getDamager().getUniqueId()).getActiveKillEffect().getName());
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                plugin.getGameManager().handleGameKill(game, event.getDamager().getUniqueId(), killed);

            } else
                plugin.getGameManager().handleGameKill(game, killed, event.getCause()); // player was killed by a different cause then an entity

            // drop the player's inventory
            for (final ItemStack is : killed.getInventory()) {
                if (is == null) continue;
                killed.getWorld().dropItem(killed.getLocation(), is);
            }
            // clear inv
            killed.getInventory().clear();
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityFallDeath(PlayerDeathEvent event) {
        final SkywarsPlayer killed = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        if (killed.getPlayerState() != PlayerState.IN_GAME) return;

        if (event.getEntity().getLastDamageCause() == null) return; // damage cause isnt null + wasn't by an entity attack
        if (event.getEntity().getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            plugin.getGameManager().handleGameKill(plugin.getGameManager().getGameByPlayer(killed), killed, event.getEntity().getLastDamageCause().getCause());

            Bukkit.getScheduler().runTaskLater(plugin, () -> { // respawn
                ((Player) event.getEntity()).spigot().respawn();
            }, 10L);

        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        if(player.getPlayerState() != PlayerState.IN_GAME) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(player);

        event.setRespawnLocation(arena.getCenter());
    }

}

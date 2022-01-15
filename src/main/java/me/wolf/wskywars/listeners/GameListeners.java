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

        if (event.getDamager() instanceof Player) {
            final SkywarsPlayer damager = plugin.getPlayerManager().getSkywarsPlayer(event.getDamager().getUniqueId());
            if (plugin.getArenaManager().getTeamByPlayer(damager) == null) return;
            if (plugin.getArenaManager().getTeamByPlayer(killed).getTeamMembers().contains(damager)) {
                event.setCancelled(true);
            }
        }

        final Game game = plugin.getGameManager().getGameByPlayer(killed);

        // the entity takes more damage then it has HP (dies), cancel the event
        if (event.getDamage() >= killed.getBukkitPlayer().getHealth()) {
            event.setCancelled(true);

            if (event.getDamager() instanceof Player) { // play kill effect
                plugin.getPlayerManager().getSkywarsPlayer(event.getDamager().getUniqueId()).getActiveKillEffect().playKillEffect(killed);
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

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNaturalDeath(PlayerDeathEvent event) {
        final SkywarsPlayer killed = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        if (killed.getPlayerState() != PlayerState.IN_GAME) return;

        if (event.getEntity().getLastDamageCause() == null)
            return; // damage cause isnt null + wasn't by an entity attack
        if (event.getEntity().getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            plugin.getGameManager().handleGameKill(plugin.getGameManager().getGameByPlayer(killed), killed, event.getEntity().getLastDamageCause().getCause());

            Bukkit.getScheduler().runTaskLater(plugin, () -> { // respawn
                event.getEntity().spigot().respawn();
            }, 10L);

        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        if (player.getPlayerState() != PlayerState.IN_GAME) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(player);
        if (arena == null) return;

        event.setRespawnLocation(arena.getCenter());
    }

    @EventHandler(priority = EventPriority.HIGHEST) // cancel fall damage when a player drops out of their cage
    public void onCageFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        if (player.getPlayerState() != PlayerState.IN_GAME) return;
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(player);
        if (arena == null) return;

        event.setCancelled(event.getCause() == EntityDamageEvent.DamageCause.FALL && plugin.getPlayerManager().getCageDropDown().contains(player));
    }

    @EventHandler(priority = EventPriority.HIGHEST) // clear default death message
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage("");
    }

}

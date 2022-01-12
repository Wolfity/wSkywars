package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    private final SkywarsPlugin plugin;

    public DamageListener(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler // disable damage when players are not ingame
    public void onNoGameDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final SkywarsPlayer damaged = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        event.setCancelled(damaged.getPlayerState() != PlayerState.IN_GAME);
    }

    // cancel natural damage causes when not ingame
    @EventHandler
    public void onNaturalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final SkywarsPlayer damaged = plugin.getPlayerManager().getSkywarsPlayer(event.getEntity().getUniqueId());
        event.setCancelled(damaged.getPlayerState() != PlayerState.IN_GAME);
    }

}

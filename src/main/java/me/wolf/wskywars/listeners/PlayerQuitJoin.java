package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitJoin implements Listener {

    private final SkywarsPlugin plugin;

    public PlayerQuitJoin(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                plugin.getSqLiteManager().createPlayerData(event.getPlayer().getUniqueId(), event.getPlayer().getName()));

        // won't be null, since the object is created on join
        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getScoreboard().lobbyScoreboard(plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId())),5L);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        if (skywarsPlayer == null) return;

        // check if the player is ingame, handle their disconnection as a leave
        if(skywarsPlayer.getPlayerState() != PlayerState.IN_LOBBY) {
            plugin.getGameManager().leaveGame(skywarsPlayer, false);
            // saving data to the database
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSqLiteManager().saveData(skywarsPlayer));
        }
    }

}

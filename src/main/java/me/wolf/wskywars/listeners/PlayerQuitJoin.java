package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        final Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getSqLiteManager().createCosmeticData(player.getUniqueId());
            plugin.getSqLiteManager().createPlayerData(player.getUniqueId(), player.getName());
        });

        // won't be null, since the object is created on join
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(player.getUniqueId());
            if(skywarsPlayer == null) {
                event.getPlayer().kickPlayer(Utils.colorize("&cData Wasn't Loaded correctly, please rejoin!"));
            } else {
                plugin.getScoreboard().lobbyScoreboard(skywarsPlayer);
                skywarsPlayer.giveLobbyInventory();
            }
        }, 5L);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        if (skywarsPlayer == null) return;

        // check if the player is ingame, handle their disconnection as a leave
        if (skywarsPlayer.getPlayerState() != PlayerState.IN_LOBBY) {
            plugin.getGameManager().leaveGame(skywarsPlayer, false);
        }
        // saving data to the database
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getSqLiteManager().saveData(event.getPlayer().getUniqueId());
            plugin.getSqLiteManager().saveCosmeticData(event.getPlayer().getUniqueId());
        });


        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getPlayerManager().removeSkywarsPlayer(event.getPlayer().getUniqueId()), 20);
    }

}

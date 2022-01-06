package me.wolf.wskywars.listeners;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final SkywarsPlugin plugin;
    public BlockBreak(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        event.setCancelled(player.getPlayerState() != PlayerState.IN_GAME);
    }

}

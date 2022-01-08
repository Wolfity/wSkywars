package me.wolf.wskywars.cosmetics.wineffect;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.cosmetics.wineffect.types.IceWalkerWinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WinEffectListener implements Listener {

    private final SkywarsPlugin plugin;
    public WinEffectListener(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(event.getPlayer().getUniqueId());
        if(((IceWalkerWinEffect)plugin.getWinEffectManager().getWinEffectByName("icewalker")).getWalkerMap().contains(player)) {
            event.getPlayer().getLocation().subtract(0,1,0).getBlock().setType(Material.FROSTED_ICE);
        }
    }

}

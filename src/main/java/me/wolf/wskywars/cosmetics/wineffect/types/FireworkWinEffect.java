package me.wolf.wskywars.cosmetics.wineffect.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkWinEffect extends WinEffect {
    public FireworkWinEffect(ItemStack icon, int price) {
        super("firework", icon, price);
    }

    @Override
    public void playEffect(Arena arena, SkywarsPlayer winner, SkywarsPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getDuration() > 0) {
                    decrementDuration();
                    arena.getCenter().getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
                } else {
                    this.cancel();
                    resetDuration();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);


    }
}

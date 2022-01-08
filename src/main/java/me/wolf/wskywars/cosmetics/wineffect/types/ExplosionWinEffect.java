package me.wolf.wskywars.cosmetics.wineffect.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class ExplosionWinEffect extends WinEffect {

    public ExplosionWinEffect(ItemStack icon, int price) {
        super("explosion", icon, price);
    }

    @Override
    public void playEffect(Arena arena, SkywarsPlayer winner, SkywarsPlugin plugin) {
        arena.getTeams().forEach(team -> team.getTeamMembers().forEach(player -> {
            new ParticleBuilder(ParticleEffect.EXPLOSION_HUGE, player.getLocation())
                    .setAmount(1)
                    .display();
        }));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getDuration() > 0) {
                    decrementDuration();
                    new ParticleBuilder(ParticleEffect.EXPLOSION_HUGE, winner.getLocation())
                            .setAmount(2)
                            .setOffsetX(1)
                            .setOffsetY(2)
                            .setOffsetZ(1)
                            .display();
                } else {
                    this.cancel();
                    resetDuration();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);


    }
}

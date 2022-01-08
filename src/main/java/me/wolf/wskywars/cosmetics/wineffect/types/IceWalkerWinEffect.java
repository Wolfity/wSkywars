package me.wolf.wskywars.cosmetics.wineffect.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class IceWalkerWinEffect extends WinEffect {
    private final Set<SkywarsPlayer> walkerMap = new HashSet<>();

    public IceWalkerWinEffect(ItemStack icon, int price) {
        super("icewalker", icon, price);
    }

    @Override
    public void playEffect(Arena arena, SkywarsPlayer winner, SkywarsPlugin plugin) {
        walkerMap.add(winner);
        winner.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration() * 20,2, false, false));
        new BukkitRunnable() {
            @Override
            public void run() {
                if(getDuration() > 0) {
                    decrementDuration();
                } else {
                    this.cancel();
                    resetDuration();
                    walkerMap.remove(winner);
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public Set<SkywarsPlayer> getWalkerMap() {
        return walkerMap;
    }
}

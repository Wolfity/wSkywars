package me.wolf.wskywars.cosmetics.wineffect.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedyWinEffect extends WinEffect {
    public SpeedyWinEffect(ItemStack icon, int price) {
        super("speedy", icon, price);
    }

    @Override
    public void playEffect(Arena arena, SkywarsPlayer winner, SkywarsPlugin plugin) {
        winner.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration() * 20, 4, false, false));
    }
}

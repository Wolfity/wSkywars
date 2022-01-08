package me.wolf.wskywars.cosmetics.wineffect.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class LauncherWinEffect extends WinEffect {
    public LauncherWinEffect(ItemStack icon, int price) {
        super("launcher", icon, price);
    }

    @Override
    public void playEffect(Arena arena, SkywarsPlayer winner, SkywarsPlugin plugin) {
        arena.getTeams().forEach(team -> team.getTeamMembers().forEach(player -> {
            player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.LIGHTNING);
            player.getBukkitPlayer().setVelocity(new Vector(0, 2, 0));
        }));
    }
}

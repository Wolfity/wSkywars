package me.wolf.wskywars.cosmetics.wineffect.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;

public class DefaultWinEffect extends WinEffect {
    public DefaultWinEffect() {
        super("default", ItemUtils.createItem(Material.DIRT, "&cNone"), 0);
    }

    @Override
    public boolean isUnlocked() {
        return true;
    }

    @Override
    public void playEffect(Arena arena, SkywarsPlayer winner, SkywarsPlugin plugin) {

    }
}

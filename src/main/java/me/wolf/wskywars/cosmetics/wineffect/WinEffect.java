package me.wolf.wskywars.cosmetics.wineffect;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;

public abstract class WinEffect extends Cosmetic {

    private int duration = 7;

    public WinEffect(String name, ItemStack icon, int price) {
        super(name, icon, price, CosmeticType.WINEFFECT);
    }

    public int getDuration() {
        return duration;
    }

    public void decrementDuration() {
        this.duration--;
    }

    public void resetDuration() {
        this.duration = 7;
    }

    public abstract void playEffect(final Arena arena, final SkywarsPlayer winner, final SkywarsPlugin plugin);
}

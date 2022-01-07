package me.wolf.wskywars.cosmetics.killeffect;

import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;

public abstract class KillEffect extends Cosmetic {


    public KillEffect(String name, ItemStack icon, int price) {
        super(name, icon, price, CosmeticType.KILLEFFECT);
    }

    public abstract void playKillEffect(final SkywarsPlayer killedPlayer);


}

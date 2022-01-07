package me.wolf.wskywars.cosmetics.killeffect.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class HappyVillagerKillEffect extends KillEffect {
    public HappyVillagerKillEffect(ItemStack icon, int price) {
        super("happyvillager", icon, price);
    }

    @Override
    public void playKillEffect(SkywarsPlayer killedPlayer) {
        new ParticleBuilder(ParticleEffect.VILLAGER_HAPPY, killedPlayer.getLocation())
                .setAmount(15)
                .setOffsetX(1)
                .setOffsetY(2)
                .setOffsetZ(1)
                .display();
    }
}

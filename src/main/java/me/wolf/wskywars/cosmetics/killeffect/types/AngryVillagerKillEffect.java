package me.wolf.wskywars.cosmetics.killeffect.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class AngryVillagerKillEffect extends KillEffect {
    public AngryVillagerKillEffect(ItemStack icon, int price) {
        super("angryvillager", icon, price);
    }

    @Override
    public void playKillEffect(SkywarsPlayer killedPlayer) {
        new ParticleBuilder(ParticleEffect.VILLAGER_ANGRY, killedPlayer.getLocation())
                .setAmount(15)
                .setOffsetX(1)
                .setOffsetY(2)
                .setOffsetZ(1)
                .display();
    }
}

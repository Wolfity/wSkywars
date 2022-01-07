package me.wolf.wskywars.cosmetics.killeffect.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class ExplosionKillEffect extends KillEffect {
    public ExplosionKillEffect(ItemStack icon, int price) {
        super("explosion", icon, price);
    }

    @Override
    public void playKillEffect(SkywarsPlayer killedPlayer) {
        new ParticleBuilder(ParticleEffect.EXPLOSION_NORMAL, killedPlayer.getLocation())
                .setAmount(1)
                .display();
    }
}

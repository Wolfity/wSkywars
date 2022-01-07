package me.wolf.wskywars.cosmetics.killeffect.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.awt.*;

public class NoteKillEffect extends KillEffect {
    public NoteKillEffect(ItemStack icon, int price) {
        super("notes", icon, price);
    }

    @Override
    public void playKillEffect(SkywarsPlayer killedPlayer) {
        new ParticleBuilder(ParticleEffect.NOTE, killedPlayer.getLocation())
                .setColor(Color.BLUE)
                .setAmount(20)
                .setOffsetY(1)
                .setOffsetY(2)
                .setOffsetZ(1)
                .display();
    }
}

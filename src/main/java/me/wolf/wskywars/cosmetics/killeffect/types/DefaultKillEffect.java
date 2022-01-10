package me.wolf.wskywars.cosmetics.killeffect.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;

public class DefaultKillEffect extends KillEffect {
    public DefaultKillEffect() {
        super("default", ItemUtils.createItem(Material.DIRT, "&cNone"), 0);
    }



    @Override
    public void playKillEffect(SkywarsPlayer killedPlayer) {

    }
}

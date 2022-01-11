package me.wolf.wskywars.cosmetics.cage;

import me.wolf.wskywars.cosmetics.cage.Cage;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;

public class DefaultCage extends Cage {
    public DefaultCage() {
        super("defaultcage", ItemUtils.createItem(Material.GLASS, "Default Cage"), 0);
    }
}

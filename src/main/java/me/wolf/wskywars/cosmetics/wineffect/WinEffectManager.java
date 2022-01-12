package me.wolf.wskywars.cosmetics.wineffect;

import me.wolf.wskywars.cosmetics.wineffect.types.*;
import me.wolf.wskywars.files.YamlConfig;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class WinEffectManager {

    private final Set<WinEffect> winEffects = new HashSet<>();

    public WinEffect getWinEffectByName(final String name) {
        return winEffects.stream().filter(winEffect -> winEffect.getName().equalsIgnoreCase(name)).findFirst().orElse(new DefaultWinEffect());
    }

    // loading all the win effects
    public void loadWinEffects(final YamlConfig cfg) {
        for (final String effect : cfg.getConfig().getConfigurationSection("win-effects").getKeys(false)) {
            final boolean enabled = cfg.getConfig().getBoolean("win-effects." + effect + ".enabled");
            if (enabled) {
                final Material material = Material.valueOf(cfg.getConfig().getString("win-effects." + effect + ".icon-material"));
                final String name = cfg.getConfig().getString("win-effects." + effect + ".icon-name");
                final int price = cfg.getConfig().getInt("win-effects." + effect + ".price");
                final ItemStack icon = ItemUtils.createItem(material, name);

                switch (effect) {
                    case "explosion":
                        this.winEffects.add(new ExplosionWinEffect(icon, price));
                        break;
                    case "firework":
                        this.winEffects.add(new FireworkWinEffect(icon, price));
                        break;
                    case "icewalker":
                        this.winEffects.add(new IceWalkerWinEffect(icon, price));
                        break;
                    case "speedy":
                        this.winEffects.add(new SpeedyWinEffect(icon, price));
                        break;
                    case "launcher":
                        this.winEffects.add(new LauncherWinEffect(icon, price));
                        break;
                }

            }
        }
        winEffects.add(new DefaultWinEffect());
    }

    public Set<WinEffect> getWinEffects() {
        return winEffects;
    }

}
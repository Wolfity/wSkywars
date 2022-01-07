package me.wolf.wskywars.cosmetics.killeffect;

import me.wolf.wskywars.cosmetics.killeffect.types.*;
import me.wolf.wskywars.files.YamlConfig;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class KillEffectManager {

    private final Set<KillEffect> killEffects = new HashSet<>();

    public void loadKillEffects(final YamlConfig cfg) {
        for (final String effect : cfg.getConfig().getConfigurationSection("kill-effects").getKeys(false)) {
            final boolean enabled = cfg.getConfig().getBoolean("kill-effects." + effect + ".enabled");
            if (enabled) {
                final Material material = Material.valueOf(cfg.getConfig().getString("kill-effects." + effect + ".icon-material"));
                final String name = cfg.getConfig().getString("kill-effects." + effect + ".icon-name");
                final int price = cfg.getConfig().getInt("kill-effects." + effect + ".price");
                final ItemStack icon = ItemUtils.createItem(material, name);

                switch (effect) {
                    case "explosion":
                        killEffects.add(new ExplosionKillEffect(icon, price));
                        break;
                    case "hearts":
                        killEffects.add(new HeartsKillEffect(icon, price));
                        break;
                    case "angryvillager":
                        killEffects.add(new AngryVillagerKillEffect(icon, price));
                        break;
                    case "happyvillager":
                        killEffects.add(new HappyVillagerKillEffect(icon, price));
                        break;
                    case "water":
                        killEffects.add(new WaterKillEffect(icon, price));
                        break;
                    case "lava":
                        killEffects.add(new LavaKillEffect(icon, price));
                        break;
                    case "notes":
                        killEffects.add(new NoteKillEffect(icon, price));
                        break;
                }

            }
        }
        killEffects.add(new DefaultKillEffect());
    }

    public KillEffect getKillEffectByName(final String name) {
        return killEffects.stream().filter(killEffect -> killEffect.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Set<KillEffect> getKillEffects() {
        return killEffects;
    }
}

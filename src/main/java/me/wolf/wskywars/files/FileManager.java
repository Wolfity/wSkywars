package me.wolf.wskywars.files;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.Bukkit;

public class FileManager {

    private YamlConfig chestItems, killEffects, winEffects, cages;


    public FileManager(final SkywarsPlugin plugin) {
        try {
            cages = new YamlConfig("cages.yml", plugin);
            chestItems = new YamlConfig("chestitems.yml", plugin);
            killEffects = new YamlConfig("killeffects.yml", plugin);
            winEffects = new YamlConfig("wineffects.yml", plugin);

        } catch (final Exception e) {
            Bukkit.getLogger().info(Utils.colorize("&4Something went wrong while loading the yml files"));
            e.printStackTrace();
        }
    }

    public YamlConfig getChestItemsConfig() {
        return chestItems;
    }

    public YamlConfig getKillEffectsConfig() {
        return killEffects;
    }

    public YamlConfig getWinEffectsConfig() {
        return winEffects;
    }

    public YamlConfig getCagesConfig() {
        return cages;
    }
}

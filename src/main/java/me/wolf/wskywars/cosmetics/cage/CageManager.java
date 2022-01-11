package me.wolf.wskywars.cosmetics.cage;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import me.wolf.wskywars.cosmetics.cage.types.DefaultCage;
import me.wolf.wskywars.cosmetics.cage.types.OreCage;
import me.wolf.wskywars.cosmetics.cage.types.PrisonCage;
import me.wolf.wskywars.files.YamlConfig;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CageManager {

    private final Set<Cage> cages = new HashSet<>();

    public void removeCage(final SkywarsPlayer player) throws IOException {
        final EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(player.getWorld()));
        player.getActiveCage().getEditSession().undo(editSession);
        player.getActiveCage().setEditSession(null);

    }

    public void loadCages(final YamlConfig cfg) {
        for (final String cage : cfg.getConfig().getConfigurationSection("cages").getKeys(false)) {
            final boolean enabled = cfg.getConfig().getBoolean("cages." + cage + ".enabled");
            if (enabled) {
                final Material material = Material.valueOf(cfg.getConfig().getString("cages." + cage + ".icon-material"));
                final String name = cfg.getConfig().getString("cages." + cage + ".icon-name");
                final int price = cfg.getConfig().getInt("cages." + cage + ".price");
                final ItemStack icon = ItemUtils.createItem(material, name);

                switch (cage) {
                    case "prisoncage":
                        cages.add(new PrisonCage(icon, price));
                        break;
                    case "orescage":
                        cages.add(new OreCage(icon, price));
                        break;
                }
            }
        }
        cages.add(new DefaultCage());
    }

    public Cage getCageByName(final String name) {
        return cages.stream().filter(cage -> cage.getName().equalsIgnoreCase(name)).findFirst().orElse(new DefaultCage());
    }

    public Set<Cage> getCages() {
        return cages;
    }
}

package me.wolf.wskywars.cosmetics.cage;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.exception.CageFileNotFoundException;
import me.wolf.wskywars.files.YamlConfig;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CageManager {

    private final Set<Cage> cages = new HashSet<>();

    /**
     * @param player the player the cage will be removed of when a game starts
     * @throws IOException when something goes wrong
     */
    public void removeCage(final SkywarsPlayer player) throws IOException {
        final EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(player.getWorld()));
        player.getActiveCage().getEditSession().undo(editSession);
    }

    /**
     * @param cfg the cages config file
     *            Method that loads in every cage in the yml file. Will throw an exception when the .schem file of the cage is not found
     */
    public void loadCages(final YamlConfig cfg) {
        for (final String cage : cfg.getConfig().getConfigurationSection("cages").getKeys(false)) {
            final boolean enabled = cfg.getConfig().getBoolean("cages." + cage + ".enabled");
            if (enabled) {
                final Material material = Material.valueOf(cfg.getConfig().getString("cages." + cage + ".icon-material"));
                final String name = cfg.getConfig().getString("cages." + cage + ".icon-name");
                final int price = cfg.getConfig().getInt("cages." + cage + ".price");
                final ItemStack icon = ItemUtils.createItem(material, name);
                // checking if the cage has a schematica file in the appropriate folder
                final File schemFile = new File("skywarsschematics/cages/" + cage + ".schem");
                if (schemFile.exists()) {
                    cages.add(new Cage(cage, icon, price));
                } else
                    throw new CageFileNotFoundException("The .schem file " + cage + ".schem in skywarsschematics/cages was not found");
            }
        }
        cages.add(new DefaultCage()); // always add the default cage
    }


    public void pasteCage(final SkywarsPlayer player, final Location location) throws IOException {
        player.setActiveCosmetic(new Cage(player.getActiveCage().getName(), player.getActiveCage().getIcon(), player.getActiveCage().getPrice()));

        final Cage cage = player.getActiveCage();

        cage.setSchemFile(new File("skywarsschematics/cages/" + cage.getName() + ".schem"));

        File schem = cage.getSchemFile();

        if (!schem.exists()) {
            schem = new File("skywarsschematics/cages/defaultcage.schem");
        }
        ClipboardFormat format = ClipboardFormats.findByFile(schem);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schem))) {
            Clipboard clipboard = reader.read();

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(location.getWorld()))) {
                editSession.getChangeSet().setRecordChanges(true); // allow changes (enable/undo)
                cage.setEditSession(editSession);

                final Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);

            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }
    }

    public Cage getCageByName(final String name) {
        return cages.stream().filter(cage -> cage.getName().equalsIgnoreCase(name)).findFirst().orElse(new DefaultCage());
    }


    public Set<Cage> getCages() {
        return cages;
    }
}
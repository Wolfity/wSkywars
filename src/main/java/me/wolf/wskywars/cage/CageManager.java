package me.wolf.wskywars.cage;

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
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CageManager {


    public void pasteCage(final Location location, final Cage cage) throws IOException {
        cage.setSchemFile(new File("schematics/cages/" + cage.getName() + ".schem"));

        File schem = cage.getSchemFile();
        if (!schem.exists()) {
            schem = new File("schematics/cages/defaultcage.schem");
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

    public void removeCage(final SkywarsPlayer player) throws IOException {
        final EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(player.getWorld()));
        player.getCage().getEditSession().undo(editSession);
        player.getCage().setEditSession(null);

    }
}

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
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Cage extends Cosmetic {

    private EditSession editSession;
    private File schemFile;

    public Cage(String name, ItemStack icon, int price) {
        super(name, icon, price, CosmeticType.CAGE);
    }

    public EditSession getEditSession() {
        return editSession;
    }

    public void setEditSession(EditSession editSession) {
        this.editSession = editSession;
    }

    public File getSchemFile() {
        return schemFile;
    }

    public void setSchemFile(File schemFile) {
        this.schemFile = schemFile;
    }

    public void pasteCage(final Location location) throws IOException {
        setSchemFile(new File("skywarsschematics/cages/" + getName() + ".schem"));

        File schem = schemFile;
        if (!schem.exists()) {
            schem = new File("skywarsschematics/cages/defaultcage.schem");
        }

        ClipboardFormat format = ClipboardFormats.findByFile(schem);
        try (ClipboardReader reader = format.getReader(new FileInputStream(schem))) {
            Clipboard clipboard = reader.read();

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(location.getWorld()))) {
                editSession.getChangeSet().setRecordChanges(true); // allow changes (enable/undo)
                setEditSession(editSession);

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
}

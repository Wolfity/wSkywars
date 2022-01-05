package me.wolf.wskywars.commands.impl;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CreateArenaCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "createarena";
    }

    @Override
    protected String getUsage() {
        return "&e/sw createarena <arenaName>";
    }

    @Override
    protected String getDescription() {
        return "&7Create an arena";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (args.length != 2) {
            player.sendMessage(getUsage());
        } else {
            final String name = args[1];
            if (plugin.getArenaManager().getArenaByName(name) == null) {
                player.sendMessage("&aTrying to create the arena &2" + name + "...");
                try {
                    if (pasteMap(player.getLocation(), name)) {
                        plugin.getArenaManager().createArena(name);
                        player.sendMessage("&aSuccessfully created the arena &2" + name);
                    } else
                        player.sendMessage("&cSomething went wrong! Make sure that the schematic is in the folder <schematics> ");
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else player.sendMessage("&cThis arena already exists!");
        }
    }

    private boolean pasteMap(final Location mid, final String name) throws IOException {
        final File schem = new File("schematics/" + name + ".schem");
        if (schem.exists()) {
            ClipboardFormat format = ClipboardFormats.findByFile(schem);
            try (ClipboardReader reader = format.getReader(new FileInputStream(schem))) {
                Clipboard clipboard = reader.read();

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(mid.getWorld()))) {
                    final Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(mid.getX(), mid.getY(), mid.getZ()))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);

                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else return false;
    }
}

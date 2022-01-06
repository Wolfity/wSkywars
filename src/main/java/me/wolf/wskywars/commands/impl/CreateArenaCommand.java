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
import me.wolf.wskywars.arena.Arena;
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
                    if (plugin.getArenaManager().pasteMap(player.getLocation(), name)) {
                        final Arena arena = plugin.getArenaManager().createArena(name);
                        arena.setCenter(player.getLocation());

                        arena.getArenaConfig().set("center." + ".world", player.getWorld().getName());
                        arena.getArenaConfig().set("center." + ".x", player.getX());
                        arena.getArenaConfig().set("center." + ".y", player.getY());
                        arena.getArenaConfig().set("center." + ".z", player.getZ());
                        arena.getArenaConfig().set("center." + ".yaw", player.getYaw());
                        arena.getArenaConfig().set("center." + ".pitch", player.getPitch());
                        arena.getArenaConfig().save(arena.getArenaConfigFile());

                        player.sendMessage("&aSuccessfully created the arena &2" + name);
                    } else
                        player.sendMessage("&cSomething went wrong! Make sure that the schematic is in the folder <schematics> and has the exact name as the arena!");
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else player.sendMessage("&cThis arena already exists!");
        }
    }

}

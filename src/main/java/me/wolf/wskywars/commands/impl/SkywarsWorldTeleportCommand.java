package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.world.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;

import java.io.File;

public class SkywarsWorldTeleportCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "worldtp";
    }

    @Override
    protected String getUsage() {
        return "&e/sw worldtp <world>";
    }

    @Override
    protected String getDescription() {
        return "&7Teleport to a skywars world";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (args.length != 2) {
            player.sendMessage(getUsage());
            return;
        }
        final String world = args[1];
        final File worldFolder = new File(Bukkit.getServer().getWorldContainer(), world); // check if the world folder exists

        if (worldFolder.exists()) { // if it does, load the world
            player.sendMessage("&aSuccessfully teleported to the world " + world);
            new WorldCreator(world).generator(new EmptyChunkGenerator()).createWorld();
            player.teleport(new Location(Bukkit.getWorld(world), 0, 80, 0));
        } else player.sendMessage("&cThis world does not exist!");
    }
}


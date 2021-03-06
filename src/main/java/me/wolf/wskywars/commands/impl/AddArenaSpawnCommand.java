package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;

import java.io.IOException;

public class AddArenaSpawnCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "addspawn";
    }

    @Override
    protected String getUsage() {
        return "&e/sw addspawn <arenaName>";
    }

    @Override
    protected String getDescription() {
        return "&7Set an arena player spawn point";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (args.length != 2) {
            player.sendMessage(getUsage());
        } else {

            if (plugin.getArenaManager().getArenaByName(args[1]) != null) { // check if the arena exists
                final Arena arena = plugin.getArenaManager().getArenaByName(args[1]);
                // set the spawn to the config
                arena.getArenaConfig().set("spawns." + arena.getSpawnLocations().size(), Utils.locToString(player.getLocation()));

                try { // saving the file
                    arena.getArenaConfig().save(arena.getArenaConfigFile());
                } catch (final IOException e) {
                    e.printStackTrace();
                }

                arena.addSpawnLocation(player.getLocation());
                player.sendMessage("&aSuccessfully set spawn number " + arena.getSpawnLocations().size());
            } else player.sendMessage("&cThis arena does not exist!");

        }
    }

}

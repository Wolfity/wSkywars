package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.world.EmptyChunkGenerator;
import org.bukkit.WorldCreator;

public class CreateSkywarsWorldCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "createskywarsworld";
    }

    @Override
    protected String getUsage() {
        return "&e/sw createskywarsworld <name>";
    }

    @Override
    protected String getDescription() {
        return "&7Create a void world";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (args.length != 2) {
            player.sendMessage(getUsage());
        } else {
            if (isAdmin(player)) {
                player.sendMessage("&aSuccessfully created a new skywars world!");
                new WorldCreator(args[1]).generator(new EmptyChunkGenerator()).createWorld();
            } else player.sendMessage("&cNo Permission!");
        }
    }
}

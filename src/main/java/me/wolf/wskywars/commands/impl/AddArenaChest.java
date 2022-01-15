package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.chest.ChestType;
import me.wolf.wskywars.chest.SkywarsChest;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;

import java.io.IOException;

public class AddArenaChest extends SubCommand {
    @Override
    protected String getCommandName() {
        return "addchest";
    }

    @Override
    protected String getUsage() {
        return "&e/sw addchest <arena> <island/mid>";
    }

    @Override
    protected String getDescription() {
        return "&7 Add a chest location to the map";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (!isAdmin(player)) {
            player.sendMessage("&cNo Permission!");
            return;
        }

        if (args.length != 3) {
            player.sendMessage(getUsage());
            return;
        }

        final Arena arena = plugin.getArenaManager().getArenaByName(args[1]);
        if (arena == null) {
            player.sendMessage("&cThis arena does not exist!");
            return;
        }

        try {
            final ChestType chestType = ChestType.valueOf(args[2].toUpperCase());
            arena.getArenaConfig().set("chests." + arena.getChests().size() + ".location", Utils.locToString(player.getLocation()));
            arena.getArenaConfig().set("chests." + arena.getChests().size() + ".type", chestType.toString());
            arena.getArenaConfig().set("chests." + arena.getChests().size() + ".items", 5);
            arena.getChests().add(new SkywarsChest(player.getLocation(), chestType, 5));

            arena.getArenaConfig().save(arena.getArenaConfigFile());

            player.sendMessage("&aSuccessfully created a new chest location, from type &e" + chestType);
        } catch (final IllegalArgumentException | IOException e) {
            player.sendMessage("&cInvalid ChestType, use <island> or <mid>");
        }


    }
}

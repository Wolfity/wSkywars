package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;

public class SkywarsLeaveCommand extends SubCommand {

    @Override
    protected String getCommandName() {
        return "leave";
    }

    @Override
    protected String getUsage() {
        return "&e/sw leave&7";
    }

    @Override
    protected String getDescription() {
        return "Leave Skywars Game";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, final SkywarsPlugin plugin) {
        if (args.length > 0) {
            player.sendMessage(getUsage());
        } else {
            // check and leave game
        }
    }
}

package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;

public class SkywarsJoinCommand extends SubCommand {


    @Override
    protected String getDescription() {
        return "Join a game";
    }

    @Override
    protected String getCommandName() {
        return "join";
    }

    @Override
    protected String getUsage() {
        return "&e/sw join&7";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (args.length > 0) {
            player.sendMessage(getUsage());
        } else {
            //  join game
            player.sendMessage("&aSuccessfully joined a game");
        }
    }
}

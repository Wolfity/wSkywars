package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;

public class SetHubCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "sethub";
    }

    @Override
    protected String getUsage() {
        return "&e/sethub";
    }

    @Override
    protected String getDescription() {
        return "&7Set the Hub location";
    }

    @Override
    protected void executeCommand(SkywarsPlayer player, String[] args, SkywarsPlugin plugin) {
        if (isAdmin(player)) {
            if (args.length == 1) {
                plugin.getConfig().set("spawn", Utils.locToString(player.getLocation()));
                plugin.saveConfig();
                player.sendMessage("&aSuccessfully set the hub!");
            } else player.sendMessage(getUsage());
        } else player.sendMessage("&cNo Permission!");
    }
}

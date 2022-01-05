package me.wolf.wskywars.commands;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.SkywarsPlayer;

public abstract class SubCommand {

    protected abstract String getCommandName();

    protected abstract String getUsage();

    protected abstract String getDescription();

    protected abstract void executeCommand(final SkywarsPlayer player, final String[] args, final SkywarsPlugin plugin);

    protected boolean isAdmin(final SkywarsPlayer skywarsPlayer) {
        return skywarsPlayer.getBukkitPlayer().hasPermission("skywars.admin");
    }

}

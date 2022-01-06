package me.wolf.wskywars.commands.impl;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.SubCommand;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.scheduler.BukkitRunnable;

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
        if (plugin.getArenaManager().getArenaByPlayer(player) == null) {
            plugin.getGameManager().joinGame(player);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getPlayerState() != PlayerState.IN_LOBBY) {
                        plugin.getScoreboard().gameScoreboard(player, plugin.getGameManager().getGameByPlayer(player));
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);

        } else player.sendMessage("&cYou are already in game!");
    }
}

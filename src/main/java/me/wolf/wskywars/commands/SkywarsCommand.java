package me.wolf.wskywars.commands;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.impl.*;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkywarsCommand extends Command {

    private final SkywarsPlugin plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public SkywarsCommand(final SkywarsPlugin plugin) {
        super("sw");
        setAliases(Collections.singletonList("skywars"));
        this.plugin = plugin;
        addSubCommand(new SkywarsLeaveCommand(), new SkywarsJoinCommand(),
                new CreateSkywarsWorldCommand(), new CreateArenaCommand(),
                new SkywarsWorldTeleportCommand(), new AddArenaSpawnCommand());

    }

    private void addSubCommand(final SubCommand... subs) {
        subCommands.addAll(Arrays.asList(subs));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender instanceof Player) {
            final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(((Player)commandSender).getUniqueId());

            if (args.length > 0) {
                for (SubCommand subCommand : subCommands) {
                    if (args[0].equalsIgnoreCase(subCommand.getCommandName())) {
                        subCommand.executeCommand(player, args, plugin);
                    }
                }

            } else {
                final StringBuilder msg = new StringBuilder();
                if(player.getBukkitPlayer().hasPermission("skywars.admin")) {
                    msg.append("&7[----------&eSkywars Help&7----------] \n" +
                            "&e/sw createarena <name> &7- Create a skywars arena\n" +
                            "&e/sw removearena <name> &7- Remove an arena\n" +
                            "&e/sw setspawn <arena> &7- Set a map's spawn point\n");
                }
                subCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));
                msg.append("&7[-------------------------------]");

                player.sendMessage(msg.toString());
            }
        }
        return false;
    }


}

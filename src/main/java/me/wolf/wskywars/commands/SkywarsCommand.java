package me.wolf.wskywars.commands;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.commands.impl.*;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkywarsCommand extends Command {

    private final SkywarsPlugin plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();
    private final List<SubCommand> adminCommands = new ArrayList<>();

    public SkywarsCommand(final SkywarsPlugin plugin) {
        super("sw");
        setAliases(Collections.singletonList("skywars"));
        this.plugin = plugin;
        addSubCommand(false, new SkywarsLeaveCommand(), new SkywarsJoinCommand());

        addSubCommand(true, new CreateSkywarsWorldCommand(), new CreateArenaCommand(),
                new SkywarsWorldTeleportCommand(), new AddArenaSpawnCommand(),
                new SetHubCommand(), new AddArenaChest());

    }

    private void addSubCommand(final boolean admin, final SubCommand... subs) {
        if (!admin) {
            subCommands.addAll(Arrays.asList(subs));
        } else adminCommands.addAll(Arrays.asList(subs));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (commandSender instanceof Player) {
            final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(((Player) commandSender).getUniqueId());

            if (args.length > 0) {
                for (SubCommand subCommand : Stream.concat(subCommands.stream(), adminCommands.stream()).collect(Collectors.toList())) {
                    if (args[0].equalsIgnoreCase(subCommand.getCommandName())) {
                        subCommand.executeCommand(player, args, plugin);
                    }
                }

            } else {
                final StringBuilder msg = new StringBuilder();
                if (player.getBukkitPlayer().hasPermission("skywars.admin")) {
                    msg.append("&7[----------&eSkywars Help&7----------] \n");
                    adminCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));
                }
                subCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));
                msg.append("&7[-------------------------------]");

                player.sendMessage(msg.toString());
            }
        }
        return false;
    }


}

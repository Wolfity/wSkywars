package me.wolf.wskywars.arena;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.enginehub.piston.util.NonnullByDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ArenaManager {

    private final SkywarsPlugin plugin;

    public ArenaManager(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<Arena> arenas = new HashSet<>();

    public Arena createArena(final String arenaName) {

        final Arena arena = new Arena(arenaName, 300, 60, 10, 2, 12, 1);

        arena.createConfig(plugin, arena);
        arenas.add(arena);

        return arena;
    }

    public void loadArenas() {
        final File folder = new File(plugin.getDataFolder() + "/arenas");

        if (folder.listFiles() == null) {
            Bukkit.getLogger().info("&3No arenas has been found!");
            return;
        }
        for (final File file : Objects.requireNonNull(folder.listFiles())) {
            final Arena arena = createArena(file.getName().replace(".yml", ""));
            final FileConfiguration cfg = arena.getArenaConfig();

            for (final String spawn : cfg.getConfigurationSection("spawns").getKeys(false)) {
                arena.addSpawnLocation(new Location(
                        Bukkit.getWorld(Objects.requireNonNull(cfg.getString("spawns." + spawn + ".world"))),
                        cfg.getDouble("spawns." + spawn + ".x"),
                        cfg.getDouble("spawns." + spawn + ".y"),
                        cfg.getDouble("spawns." + spawn + "z"),
                        (float) cfg.getDouble("spawns.pitch"),
                        (float) cfg.getDouble("spawns.yaw")
                ));
            }

            final int minPlayers = cfg.getInt("min-players");
            final int maxPlayers = cfg.getInt("max-players");
            final int cageCountdown = cfg.getInt("cage-countdown");
            final int chestRefill = cfg.getInt("chest-refill");
            final int teamSize = cfg.getInt("team-size");
            final int gameTimer = cfg.getInt("game-timer");

            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());

            arena.setGameTimer(gameTimer);
            arena.setChestRefill(chestRefill);
            arena.setCageCountdown(cageCountdown);
            arena.setTeamSize(teamSize);
            arena.setMinPlayers(minPlayers);
            arena.setMaxPlayers(maxPlayers);


        }
    }

    /**
     * @param name: The arena which has that name
     * @return The arena with said name
     * @throws NullPointerException if the arena does not exist
     */

    public Arena getArenaByName(final String name) {
        return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * @param skywarsPlayer: The player we want to get the arena of
     * @return the Arena the said player is in
     * @throws NullPointerException if the player is not in any arena
     */
    public Arena getArenaByPlayer(final SkywarsPlayer skywarsPlayer) {
        for (final Arena arena : arenas) {
            for (final Team team : arena.getTeams()) {
                if (team.getTeamMembers().contains(skywarsPlayer)) {
                    return arena;
                }
            }
        }
        return null;
    }

    public void deleteArena(final String arenaName) {
        final Arena arena = getArenaByName(arenaName);
        arena.getArenaConfigFile().delete();
        arenas.remove(arena);
    }

}

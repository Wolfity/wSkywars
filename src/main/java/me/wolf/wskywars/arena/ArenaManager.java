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
import me.wolf.wskywars.chest.ChestType;
import me.wolf.wskywars.chest.SkywarsChest;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.team.Team;
import me.wolf.wskywars.utils.Utils;
import me.wolf.wskywars.world.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ArenaManager {

    private final SkywarsPlugin plugin;
    private final Set<Arena> arenas = new HashSet<>();

    public ArenaManager(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

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

            new WorldCreator(Objects.requireNonNull(cfg.getString("center.world"))).generator(new EmptyChunkGenerator()).createWorld();

            final List<Location> spawns = new ArrayList<>();
            final Set<SkywarsChest> chests = new HashSet<>();

            for (final String spawn : cfg.getConfigurationSection("spawns").getKeys(false)) {
                spawns.add(Utils.stringToLoc(Objects.requireNonNull(cfg.getString("spawns." + spawn))));
            }

            for (final String chest : cfg.getConfigurationSection("chests").getKeys(false)) {

                final SkywarsChest skywarsChest = new SkywarsChest(
                        Utils.stringToLoc(Objects.requireNonNull(cfg.getString("chests." + chest + ".location"))),
                        ChestType.valueOf(cfg.getString("chests." + chest + ".type")),
                        cfg.getInt("chests." + chest + ".items"));
                chests.add(skywarsChest);
            }

            final Location center = new Location(
                    Bukkit.getWorld(Objects.requireNonNull(cfg.getString("center.world"))),
                    cfg.getDouble("center.x"),
                    cfg.getDouble("center.y"),
                    cfg.getDouble("center.z"),
                    (float) cfg.getDouble("center.pitch"),
                    (float) cfg.getDouble("center.yaw"));


            arena.setCenter(center);
            final int minTeams = cfg.getInt("min-teams");
            final int maxTeams = cfg.getInt("max-teams");
            final int cageCountdown = cfg.getInt("cage-countdown");
            final int chestRefill = cfg.getInt("chest-refill");
            final int teamSize = cfg.getInt("team-size");
            final int gameTimer = cfg.getInt("game-timer");

            Bukkit.getLogger().info("&aLoaded arena &e" + arena.getName());

            arena.setChests(chests);
            arena.setSpawnLocations(spawns);
            arena.setGameTimer(gameTimer);
            arena.setChestRefill(chestRefill);
            arena.setCageCountdown(cageCountdown);
            arena.setTeamSize(teamSize);
            arena.setMinTeams(minTeams);
            arena.setMaxTeams(maxTeams);

        }
    }

    /**
     * @return a free arena
     * @throws NullPointerException if there are no free arenas
     */
    public Arena getFreeArena() {
        return arenas.stream().filter(arena -> arena.getArenaState() == ArenaState.RECRUITING || arena.getArenaState() == ArenaState.IN_COUNTDOWN).findFirst().orElse(null);
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

    /**
     * @param player the player we want to get the team of
     * @return the Team object from the requested player
     */
    public Team getTeamByPlayer(final SkywarsPlayer player) {
        for (final Arena arena : arenas) {
            for (Team team : arena.getTeams()) {
                if (team.getTeamMembers().contains(player)) {
                    return team;
                }
            }
        }
        return null;
    }

    /**
     * @param arenaName delete the arena matching the arena name
     */
    public void deleteArena(final String arenaName) {
        final Arena arena = getArenaByName(arenaName);
        arena.getArenaConfigFile().delete();
        arenas.remove(arena);
    }

    /**
     * @param mid  the center of the map, pasted from that point of view
     * @param name the name of the schematica (map name)
     * @return whether the paste was successful or not
     * @throws IOException if something goes wrong
     */
    public boolean pasteMap(final Location mid, final String name) throws IOException {
        final File schem = new File("skywarsschematics/" + name + ".schem");
        if (schem.exists()) {
            ClipboardFormat format = ClipboardFormats.findByFile(schem);
            try (ClipboardReader reader = format.getReader(new FileInputStream(schem))) {
                Clipboard clipboard = reader.read();
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(mid.getWorld()))) {
                    final Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(BlockVector3.at(mid.getX(), mid.getY(), mid.getZ()))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);

                } catch (WorldEditException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else return false;
    }

}

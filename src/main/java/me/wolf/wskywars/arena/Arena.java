package me.wolf.wskywars.arena;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.team.Team;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Arena {

    private final String name;
    private int teamSize, cageCountdown, chestRefill, gameTimer, minPlayers, maxPlayers;
    private Set<Team> teams;
    private FileConfiguration arenaConfig;
    private File arenaConfigFile;
    private final List<Location> spawnLocations;
    private final Set<Location> openedChests;

    public Arena(final String name, final int gameTimer, final int chestRefill, final int cageCountdown, final int minPlayers, final int maxPlayers, final int teamSize) {
        this.name = name;
        this.gameTimer = gameTimer;
        this.chestRefill = chestRefill;
        this.cageCountdown = cageCountdown;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.teamSize = teamSize;
        this.spawnLocations = new ArrayList<>();
        this.openedChests = new HashSet<>();
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public void setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public void setCageCountdown(int cageCountdown) {
        this.cageCountdown = cageCountdown;
    }

    public void setChestRefill(int chestRefill) {
        this.chestRefill = chestRefill;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public String getName() {
        return name;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public int getCageCountdown() {
        return cageCountdown;
    }

    public int getChestRefill() {
        return chestRefill;
    }

    public File getArenaConfigFile() {
        return arenaConfigFile;
    }

    public FileConfiguration getArenaConfig() {
        return arenaConfig;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Set<Location> getOpenedChests() {
        return openedChests;
    }

    public void addSpawnLocation(final Location location) {
        this.spawnLocations.add(location);
    }
    public void addOpenedChest(final Location location) {
        this.openedChests.add(location);
    }

    /**
     * @param plugin instance of the main class
     * @param arena  the arena we are creating a config for, so every arena can have different settings
     */
    public void createConfig(final SkywarsPlugin plugin, final Arena arena) {
        arenaConfigFile = new File(plugin.getDataFolder() + "/arenas", arena.getName().toLowerCase() + ".yml");
        arenaConfig = new YamlConfiguration();
        try {
            arenaConfig.load(arenaConfigFile);
            arenaConfig.save(arenaConfigFile);
        } catch (IOException | InvalidConfigurationException ignore) {

        }
        if (!arenaConfigFile.exists()) {
            arenaConfigFile.getParentFile().mkdirs();
            try {
                arenaConfigFile.createNewFile();
                arenaConfig.load(arenaConfigFile);
                arenaConfig.set("min-players", arena.getMinPlayers());
                arenaConfig.set("max-players", arena.getMaxPlayers());
                arenaConfig.set("cage-countdown", arena.getCageCountdown());
                arenaConfig.set("game-timer", arena.getGameTimer());
                arenaConfig.set("chest-refill", arena.getChestRefill());
                arenaConfig.set("team-size", arena.getTeamSize());
                arenaConfig.save(arenaConfigFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

}

package me.wolf.wskywars.arena;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.chest.SkywarsChest;
import me.wolf.wskywars.team.Team;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Arena {

    private final String name;
    private final Set<Team> teams;
    private  List<Location> spawnLocations;
    private  Set<SkywarsChest> chests ;
    private int teamSize, cageCountdown, chestRefill, gameTimer, minTeams, maxTeams;
    private FileConfiguration arenaConfig;
    private File arenaConfigFile;
    private ArenaState arenaState;
    private Location center;

    public Arena(final String name, final int gameTimer, final int chestRefill, final int cageCountdown, final int minTeams, final int maxTeams, final int teamSize) {
        this.name = name;
        this.gameTimer = gameTimer;
        this.chestRefill = chestRefill;
        this.cageCountdown = cageCountdown;
        this.maxTeams = maxTeams;
        this.minTeams = minTeams;
        this.teamSize = teamSize;
        this.spawnLocations = new ArrayList<>();
        this.chests = new HashSet<>();
        this.arenaState = ArenaState.RECRUITING;
        this.teams = new HashSet<>();
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(int maxTeams) {
        this.maxTeams = maxTeams;
    }

    public int getMinTeams() {
        return minTeams;
    }

    public void setMinTeams(int minTeams) {
        this.minTeams = minTeams;
    }

    public void addTeam(final Team team) {
        this.teams.add(team);
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public String getName() {
        return name;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public void setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
    }

    public int getCageCountdown() {
        return cageCountdown;
    }

    public void setCageCountdown(int cageCountdown) {
        this.cageCountdown = cageCountdown;
    }

    public int getChestRefill() {
        return chestRefill;
    }

    public void setSpawnLocations(List<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
    }

    public void setChestRefill(int chestRefill) {
        this.chestRefill = chestRefill;
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

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Set<SkywarsChest> getChests() {
        return chests;
    }

    public void addSpawnLocation(final Location location) {
        this.spawnLocations.add(location);
    }



    public Location getCenter() {
        return center;
    }

    public void setCenter(Location center) {
        this.center = center;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public void decrementCageCountDown() {
        this.cageCountdown--;
    }

    public void decrementChestRefill() {
        this.chestRefill--;
    }

    public void decrementGameTimer() {
        this.gameTimer--;
    }

    public void setChests(Set<SkywarsChest> chests) {
        this.chests = chests;
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
                arenaConfig.set("min-teams", arena.getMinTeams());
                arenaConfig.set("max-teams", arena.getMaxTeams());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

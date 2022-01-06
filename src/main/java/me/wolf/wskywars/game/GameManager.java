package me.wolf.wskywars.game;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.arena.ArenaState;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GameManager {

    private final SkywarsPlugin plugin;

    public GameManager(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<Game> games = new HashSet<>();

    public void setGameState(final Game game, final GameState gameState) {
        game.setGameState(gameState);
        final Arena arena = game.getArena();
        switch (gameState) {
            case READY:
                arena.setArenaState(ArenaState.RECRUITING);
                arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.setPlayerState(PlayerState.IN_WAITING_ROOM)));
                break;
            case COUNTDOWN:
                arena.setArenaState(ArenaState.IN_COUNTDOWN);
                arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.setPlayerState(PlayerState.IN_WAITING_ROOM)));
                startLobbyCountdown(game);
                break;
            case INGAME:
                startGameTimer(game);
                arena.setArenaState(ArenaState.IN_GAME);
                arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.setPlayerState(PlayerState.IN_GAME)));
                break;
            case END:
                arena.setArenaState(ArenaState.IN_GAME);
                arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.setPlayerState(PlayerState.IN_GAME)));
                Bukkit.getScheduler().runTaskLater(plugin, () -> cleanUp(game), 200L);
                break;

        }
    }

    /**
     * @param player:  The player that will leave the game
     * @param cleanUp: Whether this gets called in the game cleanup method or not
     *                 This method deals with the removal of a player from a game, reset inventory, change playerstate, etc.
     */
    public void leaveGame(final SkywarsPlayer player, final boolean cleanUp) {
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(player);
        final Game game = getGameByArena(arena);

        player.sendMessage("&aSuccessfully left this game!");
        arena.getTeams().forEach(team -> {
            team.getTeamMembers().remove(player);
            if (team.getTeamMembers().size() == 0) arena.getTeams().remove(team); // full team eliminated
        }); // removing the player from his team

        player.setPlayerState(PlayerState.IN_LOBBY);
        player.clearInventory();
        player.clearEffects();
        player.resetHunger();

        player.teleport( // teleport to the quake spawn
                new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("spawn.world"))),
                        plugin.getConfig().getDouble("spawn.x"),
                        plugin.getConfig().getDouble("spawn.y"),
                        plugin.getConfig().getDouble("spawn.z"),
                        (float) plugin.getConfig().getDouble("spawn.pitch"),
                        (float) plugin.getConfig().getDouble("spawn.yaw")));

        if (!cleanUp) {
            arena.getTeams().clear();
        }

        if (arena.getTeams().size() <= 1) { // if there are either 1 or 0 teams left, depending on the game state, end the game, or cancel the cooldown
            if (arena.getArenaState() == ArenaState.IN_GAME || arena.getTeams().size() == 0) {
                setGameState(game, GameState.END);
            } else if (arena.getTeams().size() < arena.getMinTeams()) { // lobby countdown
                if (!cleanUp) { // we only want to send the message if the game isn't in cleanup mode
                    arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.sendMessage("&cNot enough players, countdown cancelled!")));
                }
                setGameState(game, GameState.READY);
                arena.setCageCountdown(arena.getArenaConfig().getInt("cage-countdown"));
            }
        }
    }

    public void joinGame(final SkywarsPlayer player) {
        if (getFreeGame() == null) { // there is no free game
            final Arena freeArena = plugin.getArenaManager().getFreeArena();
            if (freeArena != null) { // there are available arenas, create a new game with that free arena
                final Game game = new Game(freeArena);
                final Team availableTeam = game.getArena().getTeams().stream().filter(team -> team.getTeamMembers().size() < team.getSize()).findFirst().orElse(null);
                if (availableTeam != null) {
                    availableTeam.addMember(player);
                    player.sendMessage("&aSuccessfully joined team &2" + availableTeam.getName());
                } else { // there is no available team
                    final Team team = new Team(getTeamName(freeArena), freeArena.getTeamSize());
                    team.addMember(player);
                    freeArena.addTeam(team);
                    player.sendMessage("&aSuccessfully joined team &2" + team.getName());
                }
                prepareJoin(player, game);
                games.add(game);
            } else player.sendMessage("&cSorry, there are currently no available games to join!");
        } else {
            final Game game = getFreeGame(); // there is a free game
            game.getArena().getTeams().forEach(team -> {
                if (team.getTeamMembers().size() < team.getSize()) { // check if there are any teams with a free spot
                    team.getTeamMembers().add(player);
                    player.sendMessage("&aSuccessfully joined team &2" + team.getName());
                    prepareJoin(player, game);
                } else {
                   
                }
            });
            games.add(game);
        }
    }

    /**
     * @param player: the player being prepared for the game, teleported to spawn, give the inventories, etc.
     * @param game:   The game the player will be joining
     */
    private void prepareJoin(final SkywarsPlayer player, final Game game) {
        final Arena arena = game.getArena();
        // teleport to the next spawn
        for (int i = 0; i < arena.getTeams().size(); i++) {
            player.teleport(arena.getSpawnLocations().get(i));
        }
        if (arena.getTeams().size() >= arena.getMinTeams()) {
            setGameState(game, GameState.COUNTDOWN);
        }

    }
    private void startGameTimer(final Game game) {
        final Arena arena = game.getArena();
        new BukkitRunnable() {
            @Override
            public void run() {
                if(arena.getGameTimer() > 0) {
                    arena.decrementGameTimer();
                } else {
                    this.cancel();
                    arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
                    setGameState(game, GameState.END);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startLobbyCountdown(final Game game) {
        final Arena arena = game.getArena();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getGameState() == GameState.COUNTDOWN) {
                    if (arena.getCageCountdown() > 0) {
                        arena.decrementCageCountDown();
                    } else {
                        this.cancel(); // resetting the countdown and updating the game state
                        arena.setCageCountdown(arena.getArenaConfig().getInt("cage-countdown"));
                        setGameState(game, GameState.INGAME);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void cleanUp(final Game game) {
        final Arena arena = game.getArena();
        arena.setArenaState(ArenaState.RECRUITING);

        arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> {
            skywarsPlayer.sendCenteredMessage(new String[] {
                    "&7---------------------------------",
                    "",
                    "&e&lThe Game Has Ended!",
                    "&aThe winners: ",
                    formatWinners(getWinners(arena)),
                    "",
                    "&7---------------------------------"
            });
            leaveGame(skywarsPlayer, true);
        }));
        try {
            plugin.getArenaManager().pasteMap(arena.getCenter(), arena.getName());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        games.remove(game);
    }


    /**
     * @param arena: The arena we want to get the game from
     * @return a Game object
     * @throws NullPointerException if there is no game active with that arena
     */
    private Game getGameByArena(final Arena arena) {
        return games.stream().filter(game -> game.getArena().equals(arena)).findFirst().orElse(null);
    }

    /**
     * @return an available game
     * @throws NullPointerException if there are no available games
     */
    private Game getFreeGame() {
        return games.stream().filter(game -> game.getGameState() == GameState.READY || game.getGameState() == GameState.COUNTDOWN).findFirst().orElse(null);
    }


    private char getTeamName(final Arena arena) {
        char i = (char) 65; // increase from 65 + 1, so it'll go from A -> B -> C, etc...
        i += arena.getTeams().stream().mapToInt(team -> (char) 1).sum();

        return i;
    }

    private Team getWinners(final Arena arena) {
        for(final Team team : arena.getTeams()) {
            for(final SkywarsPlayer player : team.getTeamMembers()) {
                if(!player.isSpectator()) {
                    return team;
                }
            }
        }
        return null;
    }
    private String formatWinners(final Team winningTeam) {
        final StringBuilder sb = new StringBuilder();
        winningTeam.getTeamMembers().forEach(skywarsPlayer -> sb.append(skywarsPlayer.getName()).append(" - ").append(skywarsPlayer.getKills()).append("\n"));
        return sb.toString();
    }

}

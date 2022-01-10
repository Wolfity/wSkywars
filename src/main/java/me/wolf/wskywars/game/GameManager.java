package me.wolf.wskywars.game;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.arena.Arena;
import me.wolf.wskywars.arena.ArenaState;
import me.wolf.wskywars.player.PlayerState;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private final SkywarsPlugin plugin;
    private final Set<Game> games = new HashSet<>();

    public GameManager(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

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
                plugin.getSkywarsChestManager().fillChests(game);
                break;
            case INGAME:
                chestRefillTimer(game);
                startGameTimer(game);
                arena.setArenaState(ArenaState.IN_GAME);
                arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.setPlayerState(PlayerState.IN_GAME)));
                break;
            case END:
                arena.setArenaState(ArenaState.IN_GAME);
                arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.setPlayerState(PlayerState.IN_GAME)));
                sendEndGameMessage(arena);
                getWinningTeam(arena).getTeamMembers().forEach(SkywarsPlayer::addWin);
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

        player.setPlayerState(PlayerState.IN_LOBBY);
        player.setSpectator(false);
        player.clearInventory();
        player.clearEffects();
        player.resetHunger();
        player.getBukkitPlayer().setGameMode(GameMode.SURVIVAL);


        player.teleport( // teleport to the skywars spawn
                new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("spawn.world"))),
                        plugin.getConfig().getDouble("spawn.x"),
                        plugin.getConfig().getDouble("spawn.y"),
                        plugin.getConfig().getDouble("spawn.z"),
                        (float) plugin.getConfig().getDouble("spawn.pitch"),
                        (float) plugin.getConfig().getDouble("spawn.yaw")));

        if (!cleanUp) {
            arena.getTeams().forEach(team -> {
                team.getTeamMembers().remove(player);
                if (team.getTeamMembers().size() == 0) arena.getTeams().remove(team); // full team eliminated
            }); // removing the player from his team
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
        player.setUpPlayer();
        player.resetTempKills();
        plugin.getScoreboard().lobbyScoreboard(player);

    }

    /**
     * @param game   the game someone was killed in
     * @param killer the entity that killed the player
     * @param killed the killed player
     *               Method for handling the kill of a player
     */

    public void handleGameKill(final Game game, UUID killer, final SkywarsPlayer killed) {
        final Arena arena = game.getArena();

        // set the user to spectator mode
        killed.setSpectator(true);
        killed.getBukkitPlayer().setGameMode(GameMode.SPECTATOR);
        final int teamCount = (int) arena.getTeams().stream().filter(team -> team.getTeamMembers().stream().filter(SkywarsPlayer::isSpectator).count() == arena.getTeamSize()).count();


        // if it's a player, make a SkywarsPlayer object
        if (Bukkit.getEntity(killer) instanceof Player) {
            final SkywarsPlayer humanKiller = plugin.getPlayerManager().getSkywarsPlayer(killer);
            humanKiller.setCoins(humanKiller.getCoins() + 50);
            humanKiller.addTempKill(); // temp kill (game only)
            humanKiller.addKill();  // permanent DB kill
        }

        // send the alert to the arena
        final Entity finalKiller = Bukkit.getEntity(killer);
        arena.getTeams().forEach(aliveTeam -> aliveTeam.sendMessage("&6[!] &6" + killed.getDisplayName() + " &ewas killed by " + finalKiller.getName()));


        if (arena.getArenaState() == ArenaState.IN_GAME) {
            if (teamCount == 1) {
                getWinningTeam(arena).getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.getActiveWinEffect().playEffect(arena, skywarsPlayer, plugin));
                setGameState(game, GameState.END);
            }
        }
    }

    public void handleGameKill(final Game game, final SkywarsPlayer killed, EntityDamageEvent.DamageCause damageCause) {
        final Arena arena = game.getArena();

        // set the user to spectator mode
        killed.setSpectator(true);
        killed.getBukkitPlayer().setGameMode(GameMode.SPECTATOR);
        final int teamCount = (int) arena.getTeams().stream().filter(team -> team.getTeamMembers().stream().filter(SkywarsPlayer::isSpectator).count() == arena.getTeamSize()).count();

        arena.getTeams().forEach(aliveTeam -> aliveTeam.sendMessage("&6[!] &6" + killed.getDisplayName() + " &ewas killed by " + damageCause.name()));

        if (arena.getArenaState() == ArenaState.IN_GAME) {
            if (teamCount == 1) {
                if(getWinningTeam(arena) == null) return;
                getWinningTeam(arena).getTeamMembers().forEach(skywarsPlayer -> skywarsPlayer.getActiveWinEffect().playEffect(arena, skywarsPlayer, plugin));
                setGameState(game, GameState.END);
            }
        }
    }

    public void joinGame(final SkywarsPlayer player) {
        if (getFreeGame() == null) { // there is no free game
            final Arena freeArena = plugin.getArenaManager().getFreeArena();
            if (freeArena != null) { // there are available arenas, create a new game with that free arena
                final Game game = new Game(freeArena);
                Team availableTeam = game.getArena().getTeams().stream().filter(team -> team.getTeamMembers().size() < team.getSize()).findFirst().orElse(null);
                if (availableTeam != null) {
                    availableTeam.addMember(player);
                } else { // there is no available team
                    availableTeam = new Team(getTeamName(freeArena), freeArena.getTeamSize());
                    availableTeam.addMember(player);
                    freeArena.addTeam(availableTeam);
                }
                prepareJoin(player, game);
                player.sendMessage("&aSuccessfully joined team &2" + availableTeam.getName());
                games.add(game);
                player.setPlayerState(PlayerState.IN_WAITING_ROOM);
            } else player.sendMessage("&cSorry, there are currently no available games to join!");

        } else {
            final Game game = getFreeGame(); // there is a free game
            game.getArena().getTeams().forEach(team -> {
                if (team.getTeamMembers().size() < team.getSize()) { // check if there are any teams with a free spot
                    team.getTeamMembers().add(player);
                    player.sendMessage("&aSuccessfully joined team &2" + team.getName());
                } else {
                    final Team newTeam = new Team(getTeamName(game.getArena()), game.getArena().getTeamSize());
                    newTeam.addMember(player);
                    game.getArena().addTeam(newTeam);
                    player.sendMessage("&aSuccessfully joined team &2" + newTeam.getName());
                }
            });
            prepareJoin(player, game);
            games.add(game);
            player.setPlayerState(PlayerState.IN_WAITING_ROOM);
        }
    }

    public Game getGameByPlayer(final SkywarsPlayer player) {
        for (final Game game : games) {
            for (final Team team : game.getArena().getTeams()) {
                if (team.getTeamMembers().contains(player)) {
                    return game;
                }
            }
        }
        return null;
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
            try {
                plugin.getCageManager().pasteCage(arena.getSpawnLocations().get(i), player.getCage());
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        player.clearInventory();
        player.setUpPlayer();
        if (arena.getTeams().size() == arena.getMinTeams()) {
            setGameState(game, GameState.COUNTDOWN);
            startLobbyCountdown(game);
        }
    }

    private void startGameTimer(final Game game) {
        final Arena arena = game.getArena();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getGameState() == GameState.INGAME) {
                    if (arena.getGameTimer() > 0) {
                        arena.decrementGameTimer();
                    } else {
                        this.cancel();
                        arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
                        setGameState(game, GameState.END);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void chestRefillTimer(final Game game) {
        final Arena arena = game.getArena();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (game.getGameState() == GameState.INGAME) {
                    if (arena.getChestRefill() > 0) {
                        arena.decrementChestRefill();
                    } else { // chests have been refilled
                        arena.setChestRefill(arena.getArenaConfig().getInt("chest-refill"));
                        arena.getTeams().forEach(team -> {
                            team.playSound(Sound.BLOCK_CHEST_OPEN);
                            team.sendMessage("&a&lAll Chests have been refilled!");
                        });
                        plugin.getSkywarsChestManager().fillChests(game);

                    }
                } else
                    arena.setChestRefill(arena.getArenaConfig().getInt("chest-refill")); // reset them fully after the game ended
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
                        arena.getTeams().forEach(team -> team.sendMessage("&eThe game will start in " + arena.getCageCountdown()));
                    } else {
                        this.cancel(); // resetting the countdown and updating the game state
                        arena.setCageCountdown(arena.getArenaConfig().getInt("cage-countdown"));
                        setGameState(game, GameState.INGAME);
                        arena.getTeams().forEach(team -> {
                            team.sendCenteredMessage(new String[]{
                                    "&7---------------------------------------",
                                    "",
                                    "&a&lThe game has started",
                                    "&eGood luck!",
                                    "",
                                    "&7---------------------------------------"
                            });
                            team.getTeamMembers().forEach(player -> {
                                try {
                                    plugin.getCageManager().removeCage(player);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        });

                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void cleanUp(final Game game) {
        final Arena arena = game.getArena();

        arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> {
            leaveGame(skywarsPlayer, true);
            skywarsPlayer.resetTempKills();
        }));
        arena.getTeams().clear();
        arena.setGameTimer(arena.getArenaConfig().getInt("game-timer"));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    try {
                        plugin.getArenaManager().pasteMap(arena.getCenter(), arena.getName());
                        arena.setArenaState(ArenaState.RECRUITING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                20L);

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


    private String formatWinners(final Team winningTeam) {
        final StringBuilder sb = new StringBuilder();
        winningTeam.getTeamMembers().forEach(skywarsPlayer -> sb.append(skywarsPlayer.getName()).append(" - ").append(skywarsPlayer.getTempKills()).append("\n"));
        return sb.toString();
    }

    private void sendEndGameMessage(final Arena arena) {
        if(getWinningTeam(arena) == null) return;
        arena.getTeams().forEach(team -> team.getTeamMembers().forEach(skywarsPlayer -> {
            skywarsPlayer.sendCenteredMessage(new String[]{
                    "&7---------------------------------",
                    "",
                    "&e&lThe Game Has Ended!",
                    "&aThe winners: ",
                    formatWinners(getWinningTeam(arena)),
                    "",
                    "&7---------------------------------"
            });

        }));
    }

    private Team getWinningTeam(final Arena arena) {
        return arena.getTeams()
                .stream()
                .filter(team ->
                        team.getTeamMembers()
                                .stream()
                                .filter(SkywarsPlayer::isSpectator)
                                .count() != arena.getTeamSize()).findFirst().orElse(null);
    }
}


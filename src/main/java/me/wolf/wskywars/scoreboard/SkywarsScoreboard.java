package me.wolf.wskywars.scoreboard;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.game.Game;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class SkywarsScoreboard {

    private final SkywarsPlugin plugin;

    public SkywarsScoreboard(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void lobbyScoreboard(final SkywarsPlayer player) {

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("sw", "sw");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&e&lSkywars Lobby"));

        final Team header = scoreboard.registerNewTeam("header");
        header.addEntry(Utils.colorize("&7Header :)"));
        header.setPrefix("");
        header.setSuffix("");
        objective.getScore(Utils.colorize("&7Header :) ")).setScore(6);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(4);

        final Team wins = scoreboard.registerNewTeam("wins");
        wins.addEntry(Utils.colorize("&8Wins: "));
        wins.setPrefix("");
        wins.setSuffix(Utils.colorize("&e " + player.getWins()));
        objective.getScore(Utils.colorize("&8Wins: ")).setScore(5);

        final Team kills = scoreboard.registerNewTeam("kills");
        kills.addEntry(Utils.colorize("&8Kills: "));
        kills.setPrefix("");
        kills.setSuffix(Utils.colorize("&e " + player.getKills()));
        objective.getScore(Utils.colorize("&8Kills: ")).setScore(4);

        final Team coins = scoreboard.registerNewTeam("coins");
        coins.addEntry(Utils.colorize("&8Coins: "));
        coins.setPrefix("");
        coins.setSuffix(Utils.colorize("&e " + player.getCoins()));
        objective.getScore(Utils.colorize("&8Coins: ")).setScore(3);

        final Team killeffect = scoreboard.registerNewTeam("killeffect");
        killeffect.addEntry(Utils.colorize("&7Kill Effect: &e"));
        killeffect.setPrefix("");
        killeffect.setSuffix(player.getActiveKillEffect().getName());
        objective.getScore(Utils.colorize("&7Kill Effect: &e")).setScore(2);

        final Team wineffect = scoreboard.registerNewTeam("wineffect");
        wineffect.addEntry(Utils.colorize("&7Kill Effect: &e"));
        wineffect.setPrefix("");
        wineffect.setSuffix(player.getActiveWinEffect().getName());
        objective.getScore(Utils.colorize("&7Win Effect: &e")).setScore(1);


        player.getBukkitPlayer().setScoreboard(scoreboard);
    }

    public void gameScoreboard(final SkywarsPlayer player, final Game game) {
        if(player == null) return; // player left mid game for example
        if (game == null) return;

        final String name = game.getArena().getName();

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("sw", "sw");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&e&lSkywars Game"));

        final Team time = scoreboard.registerNewTeam("time");
        time.addEntry(Utils.colorize("&bTime: "));
        time.setPrefix("");
        time.setSuffix(Utils.colorize("&7" + game.getArena().getGameTimer()));
        objective.getScore(Utils.colorize("&bTime: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(name));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        final Team kills = scoreboard.registerNewTeam("kills");
        kills.addEntry(Utils.colorize("&3Kills: "));
        kills.setPrefix("");
        kills.setSuffix(Utils.colorize("&2" + player.getKills()));
        objective.getScore(Utils.colorize("&3Kills: ")).setScore(5);

        player.getBukkitPlayer().setScoreboard(scoreboard);
    }

}

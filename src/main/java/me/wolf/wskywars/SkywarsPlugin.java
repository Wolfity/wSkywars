package me.wolf.wskywars;


import me.wolf.wskywars.arena.ArenaManager;
import me.wolf.wskywars.commands.SkywarsCommand;
import me.wolf.wskywars.game.GameManager;
import me.wolf.wskywars.listeners.PlayerQuitJoin;
import me.wolf.wskywars.player.PlayerManager;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.sql.SQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

public class SkywarsPlugin extends JavaPlugin {

    private PlayerManager playerManager;
    private SQLiteManager sqLiteManager;
    private ArenaManager arenaManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        final File folder = new File(this.getDataFolder() + "/arenas");
        final File schemFolder = new File("schematics");
        if (!folder.exists()) folder.mkdirs();
        if (!schemFolder.exists()) schemFolder.mkdirs();


        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        registerManagers();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        for (final SkywarsPlayer skywarsPlayer : playerManager.getSkywarsPlayers().values()) {
            this.sqLiteManager.saveData(skywarsPlayer);
        }
        this.sqLiteManager.disconnect();
    }

    private void registerCommands() {
        Collections.singletonList(
                new SkywarsCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        Arrays.asList(
                new PlayerQuitJoin(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.sqLiteManager = new SQLiteManager(this);
        sqLiteManager.connect();
        this.arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        this.playerManager = new PlayerManager();
        this.gameManager = new GameManager(this);

    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}

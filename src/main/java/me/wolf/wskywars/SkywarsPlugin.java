package me.wolf.wskywars;


import me.wolf.wskywars.arena.ArenaManager;
import me.wolf.wskywars.cage.CageManager;
import me.wolf.wskywars.chest.SkywarsChestManager;
import me.wolf.wskywars.commands.SkywarsCommand;
import me.wolf.wskywars.cosmetics.killeffect.KillEffectManager;
import me.wolf.wskywars.cosmetics.wineffect.WinEffectListener;
import me.wolf.wskywars.cosmetics.wineffect.WinEffectManager;
import me.wolf.wskywars.files.FileManager;
import me.wolf.wskywars.game.GameManager;
import me.wolf.wskywars.listeners.*;
import me.wolf.wskywars.menu.MenuListener;
import me.wolf.wskywars.player.PlayerManager;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.scoreboard.SkywarsScoreboard;
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
    private SkywarsScoreboard scoreboard;
    private CageManager cageManager;
    private FileManager fileManager;
    private SkywarsChestManager skywarsChestManager;
    private KillEffectManager killEffectManager;
    private WinEffectManager winEffectManager;

    @Override

    public void onEnable() {
        final File folder = new File(this.getDataFolder() + "/arenas");
        final File schemFolder = new File("schematics/cages");
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
        for (final SkywarsPlayer player : playerManager.getSkywarsPlayers().values()) {
            sqLiteManager.saveData(player.getUuid());
            sqLiteManager.saveCosmeticData(player.getUuid());
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
                new PlayerQuitJoin(this),
                new BlockBreak(this),
                new BlockPlace(this),
                new InventoryInteractions(this),
                new MenuListener(playerManager),
                new WinEffectListener(this),
                new GameListeners(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.sqLiteManager = new SQLiteManager(this);
        sqLiteManager.connect();
        this.arenaManager = new ArenaManager(this);
        arenaManager.loadArenas();
        this.fileManager = new FileManager(this);
        this.playerManager = new PlayerManager();
        this.gameManager = new GameManager(this);
        this.scoreboard = new SkywarsScoreboard(this);
        this.cageManager = new CageManager();
        this.skywarsChestManager = new SkywarsChestManager();
        this.killEffectManager = new KillEffectManager();
        this.winEffectManager = new WinEffectManager();

        winEffectManager.loadWinEffects(fileManager.getWinEffectsConfig());
        killEffectManager.loadKillEffects(fileManager.getKillEffectsConfig());
        skywarsChestManager.loadChestItems(fileManager.getChestItemsConfig());
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

    public SkywarsScoreboard getScoreboard() {
        return scoreboard;
    }

    public CageManager getCageManager() {
        return cageManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public KillEffectManager getKillEffectManager() {
        return killEffectManager;
    }

    public SkywarsChestManager getSkywarsChestManager() {
        return skywarsChestManager;
    }

    public WinEffectManager getWinEffectManager() {
        return winEffectManager;
    }
}

package me.wolf.wskywars;

import me.wolf.wskywars.player.PlayerManager;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.sql.SQLiteManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkywarsPlugin extends JavaPlugin {

    private PlayerManager playerManager;
    private SQLiteManager sqLiteManager;

    @Override
    public void onEnable() {
        registerManagers();
    }

    @Override
    public void onDisable() {
        for(final SkywarsPlayer skywarsPlayer : playerManager.getSkywarsPlayers().values()) {
            this.sqLiteManager.saveData(skywarsPlayer);
        }
        this.sqLiteManager.disconnect();
    }

    private void registerManagers() {
        this.sqLiteManager = new SQLiteManager(this);
        sqLiteManager.connect();
    }


    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }
}

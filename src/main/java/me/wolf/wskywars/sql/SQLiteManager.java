package me.wolf.wskywars.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.cage.Cage;
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SQLiteManager {

    private final SkywarsPlugin plugin;
    private HikariDataSource hikari;

    public SQLiteManager(final SkywarsPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        final HikariConfig config = new HikariConfig();
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("wSkywars Pool");
        config.setDriverClassName("org.sqlite.JDBC");
        final File file = new File(plugin.getDataFolder(), "database.db");
        config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath().replace("\\", "/"));

        hikari = new HikariDataSource(config);

        createTablesIfNotExist();
    }

    public void disconnect() {
        if (hikari != null)
            hikari.close();
    }

    private void createTablesIfNotExist() { // if the table doesn't exist, create it
        try (final Connection connection = hikari.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(Query.CREATE_TABLE);
            statement.executeUpdate(Query.CREATE_UNLOCKED_COSMETIC_TABLE);

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerData(final UUID uuid, final String playerName) { // if the player exists, load their data, else create new data
        if (doesPlayerExist(uuid)) {
            loadData(uuid);
        } else {

            try (final Connection connection = hikari.getConnection();
                 final PreparedStatement ps = connection.prepareStatement(Query.CREATE_PLAYERDATA)) {

                ps.setString(1, uuid.toString());
                ps.setString(2, playerName);
                ps.setInt(3, 0); // wins
                ps.setInt(4, 0); // kills
                ps.setInt(5, 0); // coins
                ps.setString(6, "default"); // kill effect
                ps.setString(7, "default"); // win effect
                ps.setString(8, "default"); // cage

                plugin.getPlayerManager().addSkywarsPlayer(uuid);

                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param uuid: Load all the data from the user's UUID
     */
    private void loadData(final UUID uuid) { // load data of an existing player

        this.setWins(uuid, this.getWins(uuid));
        this.setKills(uuid, this.getKills(uuid));
        this.setCoins(uuid, this.getCoins(uuid));
        this.setActiveCosmetic(uuid, "activekilleffect", this.getActiveCosmetic(uuid, "activekilleffect"));
        this.setActiveCosmetic(uuid, "activewineffect", this.getActiveCosmetic(uuid, "activewineffect"));

        final SkywarsPlayer swPlayer = plugin.getPlayerManager().loadSkywarsPlayer(
                uuid,
                getWins(uuid),
                getKills(uuid),
                getCoins(uuid), new Cage("defaultcage"));

        swPlayer.setActiveKillEffect(plugin.getKillEffectManager().getKillEffectByName(getActiveCosmetic(uuid, "activekilleffect")));
        swPlayer.setActiveWinEffect(plugin.getWinEffectManager().getWinEffectByName(getActiveCosmetic(uuid, "activewineffect")));

        //TODO change this, this is default rn because I dont have multiple cages yet
    }

    /**
     * @param uuid: Check if a UUID exists in the database
     * @return True if the UUID exists, false if not
     */
    public boolean doesPlayerExist(final UUID uuid) { // checking if a specific player exists in the database
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {
            ps.setString(1, uuid.toString());
            final ResultSet results = ps.executeQuery();

            return results.next();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPlayerName(final UUID uuid, final String playerName) { // setting the playername in the db
        if (!doesPlayerExist(uuid)) return;

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_PLAYER_NAME)) {

            ps.setString(1, playerName);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void setWins(final UUID uuid, final int wins) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_WINS)) {

            ps.setInt(1, wins);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getWins(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {
            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getInt("wins");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public void setKills(final UUID uuid, final int kills) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_KILLS)) {

            ps.setInt(1, kills);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKills(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getInt("kills");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setCoins(final UUID uuid, final int coins) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_COINS)) {

            ps.setInt(1, coins);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCoins(final UUID uuid) {

        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();
            return results.getInt("coins");
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * @param uuid the user's cosmetics we are going to update
     * @param type the cosmetic type (killeffects, wineffects, cages)
     * @param data the data string we are processing
     */
    public void setUnlockedCosmetics(final UUID uuid, final String type, final String data) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE unlocked SET " + type + " = ? WHERE uuid = ?")) {

            ps.setString(1, data);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param uuid the user's cosmetics we are going to update
     * @param type the cosmetic type (killeffects, wineffects, cages)
     * @return a String with the cosmetic data
     */
    public String getUnlockedCosmetics(final UUID uuid, final String type) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT " + type + " FROM unlocked WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();
            return results.getString(type);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return "default";
    }

    /**
     * @param uuid the UUID we are requesting data from
     * @param type the active cosmetic type we are requesting (activekilleffect, activewineffect, activecage)
     * @return name of the active cosmetic
     */
    public String getActiveCosmetic(final UUID uuid, final String type) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement("SELECT " + type + " FROM players WHERE uuid = ?")) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();
            return results.getString(type);
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return "default";
    }

    /**
     * @param uuid the UUID we are requesting data from
     * @param type the active cosmetic type we are requesting (activekilleffect, activewineffect, activecage)
     * @param data the data String we are saving
     */
    public void setActiveCosmetic(final UUID uuid, final String type, final String data) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE players SET " + type + " = ? WHERE uuid = ?")) {

            ps.setString(1, data);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveData(final UUID uuid) { // saving all data of the specified player
        this.setPlayerName(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setWins(uuid, player.getWins());
        this.setKills(uuid, player.getKills());
        this.setCoins(uuid, player.getCoins());
        this.setActiveCosmetic(uuid, "activekilleffect", processActiveCosmeticToString(player.getActiveKillEffect()));
        this.setActiveCosmetic(uuid, "activewineffect", processActiveCosmeticToString(player.getActiveWinEffect()));

        //todo add cage
    }

    /**
     * @param uuid: Check if a UUID exists in the database
     * @return True if the UUID exists, false if not
     */
    public boolean doesCosmeticPlayerExist(final UUID uuid) { // checking if a specific player exists in the database
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_COSMETIC_DATA)) {
            ps.setString(1, uuid.toString());
            final ResultSet results = ps.executeQuery();

            return results.next();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createCosmeticData(final UUID uuid) { // if the player exists, load their data, else create new data
        if (doesCosmeticPlayerExist(uuid)) {
            loadCosmeticData(uuid);
        } else {

            try (final Connection connection = hikari.getConnection();
                 final PreparedStatement ps = connection.prepareStatement(Query.CREATE_COSMETIC_DATA)) {

                ps.setString(1, uuid.toString());
                ps.setString(2, "default"); // kill effects
                ps.setString(3, "default"); // win effects
                ps.setString(4, "default"); // cages

                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param uuid: Load all the cosmetic data from the user's UUID
     */
    private void loadCosmeticData(final UUID uuid) { // load data of an existing player
        this.setUnlockedCosmetics(uuid, "killeffects" ,this.getUnlockedCosmetics(uuid, "killeffects"));
        this.setUnlockedCosmetics(uuid, "wineffects" ,this.getUnlockedCosmetics(uuid, "wineffects"));

        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        skywarsPlayer.setWinEffects(plugin.getWinEffectManager().getWinEffects());
        skywarsPlayer.setKillEffects(plugin.getKillEffectManager().getKillEffects());

        final Set<KillEffect> unlockedKillEffects = getKillEffectsOfString(getUnlockedCosmetics(uuid, "killeffects"));
        final Set<WinEffect> unlockedWinEffects = getWinEffectsOfString(getUnlockedCosmetics(uuid, "wineffects"));


        // loop over all the kill/win effects, then check which ones the player has unlocked, and set them unlocked
        for(final KillEffect killEffect : skywarsPlayer.getKillEffects()) {
            if(unlockedKillEffects.contains(killEffect)) {
                killEffect.setUnlocked(true);
            }
        }

        for(final WinEffect winEffect : skywarsPlayer.getWinEffects()) {
            if (unlockedWinEffects.contains(winEffect)) {
                winEffect.setUnlocked(true);
            }
        }

        //TODO change this, this is default rn because I dont have multiple cages yet
    }


    public void saveCosmeticData(final UUID uuid) {
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setUnlockedCosmetics(uuid, "killeffects", processUnlockedCosmeticsToString(skywarsPlayer.getKillEffects()));
        this.setUnlockedCosmetics(uuid, "wineffects", processUnlockedCosmeticsToString(skywarsPlayer.getWinEffects()));

    }

    private <T extends Cosmetic> String processUnlockedCosmeticsToString(final Set<T> cosmetics) {
        final StringBuilder sb = new StringBuilder();
        cosmetics.stream().filter(Cosmetic::isUnlocked).forEach(cosmetic -> {
            sb.append(cosmetic.getName()).append(" ");
        });
        if (sb.length() == 0) {
            sb.append("default");
        }
        return sb.toString();
    }

    private <T extends Cosmetic> String processActiveCosmeticToString(final T cosmetic) {
        if (cosmetic == null) {
            return "default";
        }
        return cosmetic.getName();
    }
    private Set<KillEffect> getKillEffectsOfString(final String s) {
        final Set<KillEffect> killEffects = new HashSet<>();
        for(final String string : s.split(" ")) {
            killEffects.add(plugin.getKillEffectManager().getKillEffectByName(string));
        }
        return killEffects;
    }

    private Set<WinEffect> getWinEffectsOfString(final String s) {
        final Set<WinEffect> winEffects = new HashSet<>();
        for(final String string : s.split(" ")) {
            winEffects.add(plugin.getWinEffectManager().getWinEffectByName(string));
        }
        return winEffects;
    }

}
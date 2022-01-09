package me.wolf.wskywars.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Creatimg all the tables if they don't exist
     */
    private void createTablesIfNotExist() { // if the table doesn't exist, create it
        try (final Connection connection = hikari.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(Query.CREATE_TABLE);
            statement.executeUpdate(Query.CREATE_UNLOCKED_COSMETIC_TABLE);

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param uuid       the UUID we are creating/loading data for
     * @param playerName the name of the player
     *                   Method that deals with creating or loading player data
     */
    public void createPlayerData(final UUID uuid, final String playerName) { // if the player exists, load their data, else create new data
        if (doesPlayerExist(uuid)) {
            plugin.getPlayerManager().addSkywarsPlayer(uuid);
            loadData(uuid);
        } else {
            try (final Connection connection = hikari.getConnection();
                 final PreparedStatement ps = connection.prepareStatement(Query.CREATE_PLAYERDATA)) {

                ps.setString(1, uuid.toString());
                ps.setString(2, playerName);
                ps.setInt(3, 0); // wins
                ps.setInt(4, 0); // kills
                ps.setInt(5, 0); // coins
                ps.setString(6, "default");
                ps.setString(7, "default");
                ps.setString(8, "default");

                plugin.getPlayerManager().addSkywarsPlayer(uuid);
                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param uuid the player's UUID we are creating/loading data for
     *             Method that takes care of the unlocked cosmetic's table
     */
    public void createCosmeticData(final UUID uuid) { // if the player exists, load their data, else create new data
        if (doesCosmeticPlayerExist(uuid)) {
            loadCosmeticData(uuid);
        } else {
            try (final Connection connection = hikari.getConnection();
                 final PreparedStatement ps = connection.prepareStatement(Query.CREATE_COSMETIC_DATA)) {

                ps.setString(1, uuid.toString());
                ps.setString(2, "default");
                ps.setString(3, "default");
                ps.setString(4, "default");

                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param uuid the UUID we are saving the data for
     *             Takes care of saving the data when logging out
     */
    public void saveData(final UUID uuid) {
        this.setPlayerName(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setData(uuid, DataType.WINS, player.getWins());
        this.setData(uuid, DataType.KILLS, player.getKills());
        this.setData(uuid, DataType.COINS, player.getCoins());

        this.setActiveCosmetic(uuid, "activekilleffect", player.getActiveKillEffect().getName());
        this.setActiveCosmetic(uuid, "activewineffect", player.getActiveWinEffect().getName());
    }

    /**
     * @param uuid the UUID we are saving unlocked cosmetics for
     */
    public void saveCosmeticData(final UUID uuid) {
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setUnlockedCosmetics(uuid,"wineffects", getCosmeticSetToString(skywarsPlayer.getUnlockedCosmetics(), CosmeticType.WINEFFECT));
        this.setUnlockedCosmetics(uuid,"killeffects", getCosmeticSetToString(skywarsPlayer.getUnlockedCosmetics(), CosmeticType.KILLEFFECT));
    }

    /**
     * @param uuid the UUID we are loading the data for upon joining
     */
    private void loadData(final UUID uuid) { // load data of an existing player

        this.setData(uuid, DataType.KILLS, this.getData(uuid, DataType.KILLS));
        this.setData(uuid, DataType.WINS, this.getData(uuid, DataType.WINS));
        this.setData(uuid, DataType.COINS, this.getData(uuid, DataType.COINS));
        this.setActiveCosmetic(uuid, "activekilleffect", this.getActiveCosmetic(uuid, "activekilleffect"));
        this.setActiveCosmetic(uuid, "activewineffect", this.getActiveCosmetic(uuid, "activewineffect"));
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);

        skywarsPlayer.setActiveCosmetic(plugin.getWinEffectManager().getWinEffectByName(getActiveCosmetic(uuid, "activewineffect")));
        skywarsPlayer.setActiveCosmetic(plugin.getWinEffectManager().getWinEffectByName(getActiveCosmetic(uuid, "activekilleffect")));
        skywarsPlayer.setCoins(this.getData(uuid, DataType.COINS));
        skywarsPlayer.setWins(this.getData(uuid, DataType.WINS));
        skywarsPlayer.setKills(this.getData(uuid, DataType.KILLS));
    }

    /**
     * @param uuid loading the UUID's unlocked cosmetics
     */
    private void loadCosmeticData(final UUID uuid) {
        this.setUnlockedCosmetics(uuid,"wineffects", this.getUnlockedCosmetics(uuid, "wineffects"));
        this.setUnlockedCosmetics(uuid,"killeffects", this.getUnlockedCosmetics(uuid, "killeffects"));
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(uuid);

        player.setUnlockedCosmetics(Stream.of(getUnlockedKillEffects(getUnlockedCosmetics(uuid, "killeffects")),
                        getUnlockedWinEffects(getUnlockedCosmetics(uuid, "wineffects")))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));
    }

    /**
     * @param uuid checking if a player exists in the database
     * @return whether the uuid exists or not
     */
    private boolean doesPlayerExist(final UUID uuid) { // checking if a specific player exists in the database
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

    /**
     * @param uuid checking whether this uuid exists in the unlocked cosmetics table
     * @return true if the UUID exists, false if not
     */
    private boolean doesCosmeticPlayerExist(final UUID uuid) { // checking if a specific player exists in the database
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

    /**
     * @param uuid       setting the UUID
     * @param playerName setting the player name
     */
    private void setPlayerName(final UUID uuid, final String playerName) { // setting the playername in the db
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

    /**
     * @param uuid     the UUID whose data we will update
     * @param dataType the type of data we will update
     * @param value    the new value
     */
    private void setData(final UUID uuid, final DataType dataType, final int value) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement("UPDATE players SET " + dataType.getDataString() + " = ? WHERE UUID = ?")) {

            ps.setInt(1, value);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();

        } catch (
                final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param uuid     the UUID's data we are requesting
     * @param dataType the datatype we are requesting
     * @return an integer, the value of the data
     */
    private int getData(final UUID uuid, final DataType dataType) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getInt(dataType.getDataString());
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param uuid the UUID's data we are updating
     * @param type the type of data we are setting (activekilleffect, activewineffect, activecage)
     * @param data the new data
     */
    private void setActiveCosmetic(final UUID uuid, final String type, final String data) {
        try (final Connection connection = hikari.getConnection()) {
            final PreparedStatement ps = connection.prepareStatement("UPDATE players SET " + type + " = ? WHERE uuid = ?");
            ps.setString(1, data);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param uuid the UUID we are requesting the active cosmetic from
     * @param type the type of cosmetic we are requesting (activekilleffect, activewineffect, activecage)
     * @return the name of the active cosmetic of the specified type
     */
    private String getActiveCosmetic(final UUID uuid, final String type) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());
            final ResultSet resultSet = ps.executeQuery();
            return resultSet.getString(type);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param uuid the UUID of the user we are setting the unlocked cosmetics to
     * @param type the type of cosmetic we are updatin (killeffects, wineffects, cages)
     * @param data the data string
     */
    private void setUnlockedCosmetics(final UUID uuid, final String type, final String data) {
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
     * @param uuid the UUID whose unlocked cosmetics we are requesting
     * @param type the type of unlocked cosmetic we are requesting (wineffects, killeffects, cages)
     * @return a String, the name of the unlocked cosmetics
     */
    private String getUnlockedCosmetics(final UUID uuid, final String type) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_COSMETIC_DATA)) {

            ps.setString(1, uuid.toString());
            final ResultSet rs = ps.executeQuery();
            return rs.getString(type);

        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return "default";
    }

    /**
     * @param cosmetics the set if cosmetics we are converting to a String
     * @param type the type of cosmetic we are converting (KILLEFFECT, WINEFFECT, CAGE)
     * @return a String of all cosmetics as the passed in set
     */
    private <T extends Cosmetic> String getCosmeticSetToString(final Set<T> cosmetics, final CosmeticType type) {
        final StringBuilder sb = new StringBuilder();
        cosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == type).forEach(cosmetic -> sb.append(cosmetic.getName()).append(" "));

        return sb.toString();
    }

    /**
     * @param killEffects the String of all of the player's unlocked kill effects
     * @return a Set of all kill effects unlocked by the player
     */
    private Set<KillEffect> getUnlockedKillEffects(final String killEffects) {
        final Set<KillEffect> effects = new HashSet<>();
        for (final String split : killEffects.split(" ")) {
            effects.add(plugin.getKillEffectManager().getKillEffectByName(split));
        }
        return effects;
    }

    /**
     * @param winEffects the string of all  the player's unlocked win effects
     * @return a Set of all the player's win effects
     */
    private Set<WinEffect> getUnlockedWinEffects(final String winEffects) {
        final Set<WinEffect> effects = new HashSet<>();
        for (final String split : winEffects.split(" ")) {
            effects.add(plugin.getWinEffectManager().getWinEffectByName(split));
        }
        return effects;
    }

}
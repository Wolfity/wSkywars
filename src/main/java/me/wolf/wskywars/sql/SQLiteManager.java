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

    private void createTablesIfNotExist() { // if the table doesn't exist, create it
        try (final Connection connection = hikari.getConnection();
             final Statement statement = connection.createStatement()) {

            statement.executeUpdate(Query.CREATE_TABLE);


        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerData(final UUID uuid, final String playerName) { // if the player exists, load their data, else create new data
        if (doesPlayerExist(uuid)) {
            plugin.getPlayerManager().addSkywarsPlayer(uuid, plugin);
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

                plugin.getPlayerManager().addSkywarsPlayer(uuid, plugin);
                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

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

    public void saveData(final UUID uuid) {
        this.setPlayerName(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setWins(uuid, player.getWins());
        this.setKills(uuid, player.getKills());
        this.setCoins(uuid, player.getCoins());
        this.setActiveKillEffect(uuid, getCosmeticToString(player.getActiveKillEffect()));
        this.setActiveWinEffect(uuid, getCosmeticToString(player.getActiveWinEffect()));


    }

    public void saveCosmeticData(final UUID uuid) {
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setUnlockedWinEffect(uuid, getCosmeticSetToString(skywarsPlayer.getUnlockedCosmetics(), CosmeticType.WINEFFECT));
        this.setUnlockedKillEffect(uuid, getCosmeticSetToString(skywarsPlayer.getUnlockedCosmetics(), CosmeticType.KILLEFFECT));

    }

    private void loadData(final UUID uuid) { // load data of an existing player

        // islands table
        this.setKills(uuid, this.getKills(uuid));
        this.setWins(uuid, this.getWins(uuid));
        this.setCoins(uuid, this.getCoins(uuid));
        this.setActiveKillEffect(uuid, this.getActiveKillEffect(uuid));
        this.setActiveWinEffect(uuid, this.getActiveWinEffect(uuid));
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);

        skywarsPlayer.setActiveCosmetic(this.getWinEffectFromString(getActiveWinEffect(uuid)));
        skywarsPlayer.setActiveCosmetic(this.getKillEffectFromString(getActiveKillEffect(uuid)));
        skywarsPlayer.setCoins(this.getCoins(uuid));
        skywarsPlayer.setWins(this.getWins(uuid));
        skywarsPlayer.setKills(this.getKills(uuid));
    }

    private void loadCosmeticData(final UUID uuid) {
        this.setUnlockedKillEffect(uuid, this.getUnlockedKillEffects(uuid));
        this.setUnlockedWinEffect(uuid, this.getUnlockedWinEffect(uuid));
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(uuid);


        player.setUnlockedCosmetics(Stream.of(getUnlockedKillEffects(getUnlockedKillEffects(uuid)), getUnlockedWinEffects(getUnlockedWinEffect(uuid)))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()));

    }

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

    public void setActiveKillEffect(final UUID uuid, final String killEffects) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_ACTIVE_KILLEFFECT)) {

            ps.setString(1, killEffects);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void setActiveWinEffect(final UUID uuid, final String winEffect) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_ACTIVE_WINEFFECT)) {

            ps.setString(1, winEffect);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getActiveKillEffect(final UUID uuid) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getString("activekilleffect");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getActiveWinEffect(final UUID uuid) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_PLAYERDATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getString("activewineffect");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUnlockedWinEffect(final UUID uuid, final String winEffect) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_UNLOCKED_WINEFFECT)) {

            ps.setString(1, winEffect);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void setUnlockedKillEffect(final UUID uuid, final String killEffect) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_UNLOCKED_KILLEFFECT)) {

            ps.setString(1, killEffect);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUnlockedKillEffects(final UUID uuid) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_COSMETIC_DATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getString("killeffects");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUnlockedWinEffect(final UUID uuid) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.GET_COSMETIC_DATA)) {

            ps.setString(1, uuid.toString());

            final ResultSet results = ps.executeQuery();

            return results.getString("wineffects");
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public KillEffect getKillEffectFromString(final String s) {
        return plugin.getKillEffectManager().getKillEffectByName(s);
    }

    public WinEffect getWinEffectFromString(final String s) {
        return plugin.getWinEffectManager().getWinEffectByName(s);
    }

    private <T extends Cosmetic> String getCosmeticToString(final T cosmetic) {
        return cosmetic.getName();
    }

    private <T extends Cosmetic> String getCosmeticSetToString(final Set<T> cosmetics, final CosmeticType type) {
        final StringBuilder sb = new StringBuilder();
        cosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == type).forEach(cosmetic -> sb.append(cosmetic.getName()).append(" "));

        return sb.toString();
    }

    private Set<KillEffect> getUnlockedKillEffects(final String s) {
        final Set<KillEffect> killEffects = new HashSet<>();
        for (final String split : s.split(" ")) {
            killEffects.add(plugin.getKillEffectManager().getKillEffectByName(split));
        }
        return killEffects;
    }

    private Set<WinEffect> getUnlockedWinEffects(final String s) {
        final Set<WinEffect> winEffects = new HashSet<>();
        for (final String split : s.split(" ")) {
            winEffects.add(plugin.getWinEffectManager().getWinEffectByName(split));
        }
        return winEffects;
    }

}
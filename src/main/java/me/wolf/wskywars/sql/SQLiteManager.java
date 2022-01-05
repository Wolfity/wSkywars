package me.wolf.wskywars.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.player.SkywarsPlayer;

import java.io.File;
import java.sql.*;
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

        plugin.getPlayerManager().loadSkywarsPlayer(
                uuid,
                getWins(uuid),
                getKills(uuid),
                getCoins(uuid));

    }

    /**
     * @param uuid: Check if a UUID exists in the database
     * @return True if the UUID exists, false if not
     */
    public boolean doesPlayerExist(final UUID uuid) { // checking if a specific player exisits in the database
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

    public void saveData(final SkywarsPlayer skywarsPlayer) { // saving all data of the specified player
        this.setPlayerName(skywarsPlayer.getUuid(), skywarsPlayer.getName());
        this.setWins(skywarsPlayer.getUuid(), skywarsPlayer.getWins());
        this.setKills(skywarsPlayer.getUuid(), skywarsPlayer.getKills());
        this.setCoins(skywarsPlayer.getUuid(), skywarsPlayer.getCoins());
    }


}

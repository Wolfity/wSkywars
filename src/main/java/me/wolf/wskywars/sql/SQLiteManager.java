package me.wolf.wskywars.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.cage.Cage;
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.Arrays;
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
            statement.executeUpdate(Query.CREATE_COSMETICS_TABLE);

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
                getCoins(uuid), new Cage("defaultcage"));
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

    public void saveData(final UUID uuid) { // saving all data of the specified player
        this.setPlayerName(uuid, Bukkit.getOfflinePlayer(uuid).getName());
        final SkywarsPlayer player = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        this.setWins(uuid, player.getWins());
        this.setKills(uuid, player.getKills());
        this.setCoins(uuid, player.getCoins());
    }


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


    public void setKillEffects(final UUID uuid, final String killEffect) {
        try (final Connection connection = hikari.getConnection();
             final PreparedStatement ps = connection.prepareStatement(Query.SET_KILLEFFECTS)) {

            ps.setString(1, killEffect);
            ps.setString(2, uuid.toString());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public String getKillEffect(final UUID uuid) {

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


    public void createCosmeticData(final UUID uuid) { // if the player exists, load their data, else create new data
        if (doesCosmeticPlayerExist(uuid)) {
            loadCosmeticData(plugin.getPlayerManager().getSkywarsPlayer(uuid));
        } else {
            try (final Connection connection = hikari.getConnection();
                 final PreparedStatement ps = connection.prepareStatement(Query.CREATE_COSMETIC_DATA)) {
                final Set<KillEffect> killEffects = plugin.getKillEffectManager().getKillEffects();

                final String killEffectData = cosmeticSetToString(killEffects);


                ps.setString(1, uuid.toString());
                ps.setString(2, killEffectData); // killeffects
                ps.setString(3, "none"); // wineffects
                ps.setString(4, "none"); // cages



                plugin.getPlayerManager().getSkywarsPlayer(uuid).setKillEffects(stringToKillEffectSet(killEffectData));

                ps.executeUpdate();

            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadKillEffects(final UUID uuid, final String killData) {
        final SkywarsPlayer skywarsPlayer = plugin.getPlayerManager().getSkywarsPlayer(uuid);
        final Set<KillEffect> killEffects = skywarsPlayer.getKillEffects();

        final String[] splitData = killData.split(";"); // is a String killeffect:unlocked/active
        for (final String effect : splitData) {
            final String[] splitResult = effect.split(":"); // returns killeffect unlocked active
            final KillEffect killEffect = plugin.getKillEffectManager().getKillEffectByName(splitResult[0]);
            killEffect.setUnlocked(Boolean.parseBoolean(splitResult[1]));
            killEffect.setActive(Boolean.parseBoolean(splitResult[2]));
            killEffects.add(killEffect);

        }
        skywarsPlayer.setKillEffects(killEffects);
    }

    private void loadCosmeticData(final SkywarsPlayer skywarsPlayer) {
        this.setKillEffects(skywarsPlayer.getUuid(), getKillEffect(skywarsPlayer.getUuid()));

        plugin.getPlayerManager().loadCosmetics(skywarsPlayer, this.getKillEffect(skywarsPlayer.getUuid()), plugin);

    }

    public void saveCosmeticData(final UUID uuid, final CosmeticType cosmeticType, final Set<KillEffect> data) {
        switch (cosmeticType) {
            case KILLEFFECT:
                this.setKillEffects(uuid, cosmeticSetToString(data));
                break;
            case WINEFFECT:
                break;
            case CAGE:
                break;
        }

    }

    private String cosmeticSetToString(final Set<KillEffect> killEffects) {
        final StringBuilder killEffectData = new StringBuilder();
        killEffects.forEach(killEffect -> { // notes:true:false --> notes = unlocked but is not active
            killEffectData.append(killEffect.getName()).append(":").append(killEffect.isUnlocked()).append(":").append(killEffect.isActive()).append(";");
        });

        return killEffectData.toString();
    }

    private Set<KillEffect> stringToKillEffectSet(final String killEffectData) {
        final Set<KillEffect> killEffects = new HashSet<>();
        final String[] oneEffect = killEffectData.split(";"); // returns notes:unlocked:active

        for(final String effect : oneEffect) {
            final String[] effectSplit = effect.split(":"); // splits into 3 seperate parts

            final KillEffect killEffect = plugin.getKillEffectManager().getKillEffectByName(effectSplit[0]);
            killEffect.setUnlocked(Boolean.parseBoolean(effectSplit[1]));
            killEffect.setActive(Boolean.parseBoolean(effectSplit[2]));
            killEffects.add(killEffect);
        }
        return killEffects;
    }

}

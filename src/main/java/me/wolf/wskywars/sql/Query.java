package me.wolf.wskywars.sql;

public final class Query {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "players (uuid VARCHAR(64) PRIMARY KEY " +
            "NOT NULL, " +
            "name VARCHAR(16), " +
            "wins INT, " +
            "kills INT, " +
            "coins INT," +
            "activekilleffect VARCHAR(16)," +
            "activewineffect VARCHAR(16)," +
            "activecage VARCHAR(16))";

    public static final String CREATE_UNLOCKED_COSMETIC_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "unlocked (uuid VARCHAR(64) PRIMARY KEY " +
            "NOT NULL, " +
            "killeffects VARCHAR(16), " +
            "wineffects VARCHAR(16), " +
            "cages VARCHAR(16))";

    public static final String CREATE_PLAYERDATA = "INSERT INTO players (uuid, name, wins, kills, coins, activekilleffect, activewineffect, activecage) VALUES (?,?,?,?,?,?,?,?)";
    public static final String SET_PLAYER_NAME = "UPDATE players SET name = ? WHERE uuid = ?";
    public static final String SET_WINS = "UPDATE players SET wins = ? WHERE uuid = ?";
    public static final String SET_KILLS = "UPDATE players SET kills = ? WHERE uuid = ?";
    public static final String SET_COINS = "UPDATE players SET coins = ? WHERE uuid = ?";
    public static final String SET_ACTIVE_KILLEFFECT = "UPDATE players SET activekilleffect = ? WHERE uuid = ?";
    public static final String SET_ACTIVE_WINEFFECT = "UPDATE players SET activewineffect = ? WHERE uuid = ?";

    public static final String CREATE_COSMETIC_DATA = "INSERT INTO unlocked (uuid, killeffects, wineffects, cages) VALUES (?,?,?,?)";
    public static final String SET_UNLOCKED_WINEFFECT = "UPDATE unlocked SET wineffects = ? WHERE uuid = ?";
    public static final String SET_UNLOCKED_KILLEFFECT = "UPDATE unlocked SET killeffects = ? WHERE uuid = ?";
    public static final String GET_PLAYERDATA = "SELECT * FROM players WHERE uuid = ?";
    public static final String GET_COSMETIC_DATA = "SELECT * FROM unlocked WHERE uuid = ?";

    private Query() {
    }

}

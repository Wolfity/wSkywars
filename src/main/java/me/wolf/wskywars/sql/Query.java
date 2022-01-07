package me.wolf.wskywars.sql;

public final class Query {

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "players (uuid VARCHAR(64) " +
            "NOT NULL, " +
            "name VARCHAR(16), " +
            "wins INT, " +
            "kills INT, " +
            "coins INT)";

    public static final String CREATE_COSMETICS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            "cosmetics (uuid VARCHAR(64) " +
            "NOT NULL, " +
            "killeffects VARCHAR(16), " +
            "wineffects VARCHAR(16), " +
            "cages VARCHAR(16))";

    public static final String CREATE_PLAYERDATA = "INSERT INTO players (uuid, name, wins, kills, coins) VALUES (?,?,?,?,?)";
    public static final String SET_PLAYER_NAME = "UPDATE players SET name = ? WHERE uuid = ?";
    public static final String SET_WINS = "UPDATE players SET wins = ? WHERE uuid = ?";
    public static final String SET_KILLS = "UPDATE players SET kills = ? WHERE uuid = ?";
    public static final String SET_COINS = "UPDATE players SET coins = ? WHERE uuid = ?";
    public static final String GET_PLAYERDATA = "SELECT * FROM players WHERE uuid = ?";

    public static final String CREATE_COSMETIC_DATA = "INSERT INTO cosmetics (uuid, killeffects, wineffects, cages) VALUES (?,?,?,?)";
    public static final String SET_KILLEFFECTS = "UPDATE cosmetics SET killeffects = ? WHERE uuid = ?";
    public static final String SET_WINEFFECTS = "UPDATE cosmetics SET wineffects = ? WHERE uuid = ?";
    public static final String SET_CAGES = "UPDATE cosmetics SET cages = ? WHERE uuid = ?";
    public static final String GET_COSMETIC_DATA = "SELECT * FROM cosmetics WHERE uuid = ?";
    private Query() {
    }

}

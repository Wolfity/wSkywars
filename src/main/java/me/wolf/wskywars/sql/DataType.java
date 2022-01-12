package me.wolf.wskywars.sql;

public enum DataType {

    COINS("coins"),
    KILLS("kills"),
    WINS("wins");

    private final String dataString;

    DataType(final String s) {
        this.dataString = s;
    }

    public String getDataString() {
        return dataString;
    }
}

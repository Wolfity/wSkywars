package me.wolf.wskywars.cosmetics;

public enum CosmeticType {

    KILLEFFECT("&aKill Effects"),
    WINEFFECT("&eWin Effects"),
    CAGE("&bCages");

    private final String display;
    CosmeticType(final String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}

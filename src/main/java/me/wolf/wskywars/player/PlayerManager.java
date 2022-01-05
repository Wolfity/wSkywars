package me.wolf.wskywars.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, SkywarsPlayer> skywarsPlayers = new HashMap<>();


    /**
     * @param uuid: the SkywarsPlayer value matching the UUID key
     * @return a SkywarsPlayer object if it exists
     * @throws NullPointerException if the object doesnt exist
     */
    public SkywarsPlayer getSkywarsPlayer(final UUID uuid) {
        return this.skywarsPlayers.get(uuid);
    }

    // creating a new first time skywars player
    public void addSkywarsPlayer(final UUID uuid) {
        this.skywarsPlayers.put(uuid, new SkywarsPlayer(uuid));
    }

    // remove a skywars player
    public void removeSkywarsPlayer(final UUID uuid) {
        this.skywarsPlayers.remove(uuid);
    }

    /**
     * @param uuid: The UUID of the player we are going to load in
     * @param wins: The amount of wins stored by the UUID
     * @param kills: The amount of kills stored by the UUID
     * @param coins: The amount of coins stored by the UUID
     */
    public void loadSkywarsPlayer(final UUID uuid, final int wins, final int kills, final int coins) {
        this.skywarsPlayers.put(uuid, new SkywarsPlayer(uuid, wins, kills, coins));
    }

    public Map<UUID, SkywarsPlayer> getSkywarsPlayers() {
        return skywarsPlayers;
    }
}

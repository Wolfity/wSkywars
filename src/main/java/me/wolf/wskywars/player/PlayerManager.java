package me.wolf.wskywars.player;

import java.util.*;

public class PlayerManager {

    private final Map<UUID, SkywarsPlayer> skywarsPlayers = new HashMap<>();
    private final Set<SkywarsPlayer> cageDropDown = new HashSet<>();
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


    public Map<UUID, SkywarsPlayer> getSkywarsPlayers() {
        return skywarsPlayers;
    }

    public Set<SkywarsPlayer> getCageDropDown() {
        return cageDropDown;
    }
}

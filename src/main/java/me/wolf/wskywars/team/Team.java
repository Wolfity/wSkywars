package me.wolf.wskywars.team;

import me.wolf.wskywars.player.SkywarsPlayer;

import java.util.HashSet;
import java.util.Set;

public class Team {

    private final char name;
    private final int size;
    private final Set<SkywarsPlayer> teamMembers;

    public Team(final char name, final int size) {
        this.name = name;
        this.size = size;
        this.teamMembers = new HashSet<>();
    }

    public int getSize() {
        return size;
    }

    public char getName() {
        return name;
    }

    public Set<SkywarsPlayer> getTeamMembers() {
        return teamMembers;
    }
}

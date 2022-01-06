package me.wolf.wskywars.team;

import me.wolf.wskywars.player.SkywarsPlayer;
import org.bukkit.Sound;

import java.util.HashSet;
import java.util.Set;

public class Team implements Comparable<Team> {

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

    public void addMember(final SkywarsPlayer skywarsPlayer) {
        this.teamMembers.add(skywarsPlayer);
    }

    public void removeMember(final SkywarsPlayer skywarsPlayer) {
        this.teamMembers.remove(skywarsPlayer);
    }

    public Set<SkywarsPlayer> getTeamMembers() {
        return teamMembers;
    }

    public void sendMessage(final String msg) {
        teamMembers.forEach(player -> player.sendMessage(msg));
    }
    public void sendCenteredMessage(final String[] msg) {
        teamMembers.forEach(player -> player.sendCenteredMessage(msg));
    }

    public void playSound(final Sound sound) {
        teamMembers.forEach(player -> player.getBukkitPlayer().playSound(player.getLocation(), sound, 0.8F, 0.8F));
    }

    @Override
    public int compareTo(Team o) {
        return Character.compare(name, o.getName());
    }
}

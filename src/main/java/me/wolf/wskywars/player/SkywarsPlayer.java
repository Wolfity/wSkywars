package me.wolf.wskywars.player;

import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.cosmetics.cage.Cage;
import me.wolf.wskywars.cosmetics.cage.types.DefaultCage;
import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.cosmetics.killeffect.types.DefaultKillEffect;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.cosmetics.wineffect.types.DefaultWinEffect;
import me.wolf.wskywars.utils.ItemUtils;
import me.wolf.wskywars.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SkywarsPlayer {

    private final UUID uuid;
    private int wins, kills, coins, tempKills;
    private boolean isSpectator;
    private PlayerState playerState;
    private Set<Cosmetic> unlockedCosmetics;


    // creating a first time object
    public SkywarsPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.wins = 0;
        this.kills = 0;
        this.coins = 0;
        this.isSpectator = false;
        this.playerState = PlayerState.IN_LOBBY;
        this.unlockedCosmetics = new HashSet<>();
    }


    public Set<Cosmetic> getUnlockedCosmetics() {
        return unlockedCosmetics;
    }

    public void setUnlockedCosmetics(Set<Cosmetic> unlockedCosmetics) {
        this.unlockedCosmetics = unlockedCosmetics;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public String getName() {
        return getBukkitPlayer().getName();
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void sendMessage(final String msg) {
        getBukkitPlayer().sendMessage(Utils.colorize(msg));
    }

    public void sendCenteredMessage(final String msg) {
        Utils.sendCenteredMessage(getBukkitPlayer(), msg);
    }

    public void sendCenteredMessage(final String[] msg) {
        for (final String s : msg) {
            sendCenteredMessage(s);
        }
    }


    public Location getLocation() {
        return getBukkitPlayer().getLocation();
    }

    public PlayerInventory getInventory() {
        return getBukkitPlayer().getInventory();
    }

    public void setUpPlayer() {
        getBukkitPlayer().setFoodLevel(20);
        getBukkitPlayer().setSaturation(20);
    }

    public void clearInventory() {
        getInventory().clear();
        getInventory().setHelmet(null);
        getInventory().setChestplate(null);
        getInventory().setLeggings(null);
        getInventory().setBoots(null);
    }

    public void resetHunger() {
        getBukkitPlayer().setFoodLevel(20);
    }

    public void teleport(final Location location) {
        getBukkitPlayer().teleport(location);
    }

    public void clearEffects() {
        for (PotionEffect effect : getBukkitPlayer().getActivePotionEffects()) {
            getBukkitPlayer().removePotionEffect(effect.getType());
        }
    }

    public void addTempKill() {
        this.tempKills++;
    }

    public void resetTempKills() {
        this.tempKills = 0;
    }

    public int getTempKills() {
        return tempKills;
    }

    public void addKill() {
        this.kills++;
    }

    public void addWin() {
        this.wins++;
    }

    public World getWorld() {
        return getLocation().getWorld();
    }

    public double getX() {
        return getLocation().getX();
    }

    public double getY() {
        return getLocation().getY();
    }

    public double getZ() {
        return getLocation().getZ();
    }

    public float getYaw() {
        return getLocation().getYaw();
    }

    public float getPitch() {
        return getLocation().getPitch();
    }

    public String getDisplayName() {
        return getBukkitPlayer().getDisplayName();
    }

    public void giveLobbyInventory() {
        getInventory().setItem(3, ItemUtils.createItem(Material.DIAMOND_SWORD, "&eKill Effects"));
        getInventory().setItem(4, ItemUtils.createItem(Material.BLAZE_POWDER, "&eWin Effects"));
        getInventory().setItem(5, ItemUtils.createItem(Material.GREEN_STAINED_GLASS, "&eCages"));
    }

    public KillEffect getActiveKillEffect() {
        return (KillEffect) unlockedCosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.KILLEFFECT && cosmetic.isActive()).findFirst().orElse(new DefaultKillEffect());
    }

    public WinEffect getActiveWinEffect() {
        return (WinEffect) unlockedCosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.WINEFFECT && cosmetic.isActive()).findFirst().orElse(new DefaultWinEffect());
    }

    public Cage getActiveCage() {
        return (Cage) unlockedCosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.CAGE && cosmetic.isActive()).findFirst().orElse(new DefaultCage());
    }

    public void setActiveCosmetic(final Cosmetic cosmetic) {
        switch (cosmetic.getCosmeticType()) {
            case KILLEFFECT:
                getActiveKillEffect().setActive(false);
                cosmetic.setActive(true);
                break;
            case WINEFFECT:
                getActiveWinEffect().setActive(false);
                cosmetic.setActive(true);
                break;
            case CAGE:
                getActiveCage().setActive(false);
                cosmetic.setActive(true);
                break;
        }
    }


    public void unlockCosmetic(final Cosmetic cosmetic) {
        this.unlockedCosmetics.add(cosmetic);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkywarsPlayer that = (SkywarsPlayer) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}


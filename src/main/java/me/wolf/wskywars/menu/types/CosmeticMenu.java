package me.wolf.wskywars.menu.types;

import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.cosmetics.wineffect.WinEffect;
import me.wolf.wskywars.menu.SkywarsMenu;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.scoreboard.SkywarsScoreboard;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CosmeticMenu extends SkywarsMenu {
    public CosmeticMenu(SkywarsPlayer owner, CosmeticType cosmeticType, final SkywarsScoreboard scoreboard) {
        super(18, cosmeticType.getDisplay(), owner);
        final List<Cosmetic> sortedCosmetics = Stream.of(owner.getKillEffects(),
                owner.getWinEffects()).flatMap(Collection::stream).sorted().collect(toList());

        switch (cosmeticType) {
            case KILLEFFECT:
                sortedCosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.KILLEFFECT).forEach(killEffect -> {
                    if (killEffect.isUnlocked() || killEffect.getName().equalsIgnoreCase("default")) {
                        addItem(killEffect.getIcon(), player -> {
                            owner.setActiveKillEffect((KillEffect) killEffect);
                            owner.sendMessage("&aSuccessfully selected the killeffect &e" + killEffect.getName());
                            scoreboard.lobbyScoreboard(owner);
                        });
                    } else
                        addItem(killEffect.getLockedIcon(), player -> new PurchaseMenu(owner, killEffect, scoreboard));
                });

                break;
            case WINEFFECT:
                sortedCosmetics.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.WINEFFECT).forEach(winEffect -> {
                    if (winEffect.isUnlocked() || winEffect.getName().equalsIgnoreCase("default")) {
                        addItem(winEffect.getIcon(), player -> {
                            owner.setActiveWinEffect((WinEffect) winEffect);
                            owner.sendMessage("&aSuccessfully selected the win effect &e" + winEffect.getName());
                            scoreboard.lobbyScoreboard(owner);
                        });
                    } else
                        addItem(winEffect.getLockedIcon(), player -> new PurchaseMenu(owner, winEffect, scoreboard));
                });
                break;
            case CAGE:
                break;

        }
        openSkywarsMenu(owner);
    }
}

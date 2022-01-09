package me.wolf.wskywars.menu.types;

import me.wolf.wskywars.SkywarsPlugin;
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import me.wolf.wskywars.menu.SkywarsMenu;
import me.wolf.wskywars.player.SkywarsPlayer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CosmeticMenu extends SkywarsMenu {
    public CosmeticMenu(SkywarsPlayer owner, CosmeticType cosmeticType, final SkywarsPlugin plugin) {
        super(18, cosmeticType.getDisplay(), owner);


        final List<Cosmetic> allCosmetic = Stream.of(plugin.getKillEffectManager().getKillEffects(),
                plugin.getWinEffectManager().getWinEffects()).flatMap(Collection::stream).sorted().collect(Collectors.toList());

        switch (cosmeticType) {
            case KILLEFFECT:
                allCosmetic.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.KILLEFFECT).forEach(cosmetic -> {
                    if (owner.getUnlockedCosmetics().contains(cosmetic) || cosmetic.getName().equalsIgnoreCase("default")) {
                        addItem(cosmetic.getIcon(), player -> {
                            owner.setActiveCosmetic(cosmetic);
                            owner.sendMessage("&aSuccessfully set " + cosmetic.getName() + " to active!");
                            plugin.getScoreboard().lobbyScoreboard(owner);
                        });
                    } else
                        addItem(cosmetic.getLockedIcon(), player -> new PurchaseMenu(owner, cosmetic, plugin.getScoreboard()));
                });
                break;
            case WINEFFECT:
                allCosmetic.stream().filter(cosmetic -> cosmetic.getCosmeticType() == CosmeticType.WINEFFECT).forEach(cosmetic -> {
                    if (owner.getUnlockedCosmetics().contains(cosmetic) || cosmetic.getName().equalsIgnoreCase("default")) {
                        addItem(cosmetic.getIcon(), player -> {
                            owner.setActiveCosmetic(cosmetic);
                            owner.sendMessage("&aSuccessfully set " + cosmetic.getName() + " to active!");
                            plugin.getScoreboard().lobbyScoreboard(owner);
                        });
                    } else
                        addItem(cosmetic.getLockedIcon(), player -> new PurchaseMenu(owner, cosmetic, plugin.getScoreboard()));
                });
                break;
        }


        openSkywarsMenu(owner);
    }
}

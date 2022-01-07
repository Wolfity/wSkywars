package me.wolf.wskywars.menu.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.menu.SkywarsMenu;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.scoreboard.SkywarsScoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KillEffectMenu extends SkywarsMenu {
    public KillEffectMenu(final SkywarsPlayer owner, final SkywarsScoreboard scoreboard) {
        super(18, "&bKill Effects", owner);
        final List<KillEffect> sortedEffects = new ArrayList<>(owner.getKillEffects());

        Collections.sort(sortedEffects);
        sortedEffects.forEach(killEffect -> {
            if (killEffect.isUnlocked() || killEffect.getName().equalsIgnoreCase("default")) {
                addItem(killEffect.getIcon(), player -> {
                    owner.setActiveKillEffect(killEffect);
                    owner.sendMessage("&aSuccessfully selected the killeffect &e" + killEffect.getName());
                });
            } else addItem(killEffect.getLockedIcon(), player -> new PurchaseMenu(owner, killEffect, scoreboard));
        });

        openSkywarsMenu(owner);

    }
}

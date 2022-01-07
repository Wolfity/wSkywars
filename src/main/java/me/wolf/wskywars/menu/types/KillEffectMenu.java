package me.wolf.wskywars.menu.types;

import me.wolf.wskywars.cosmetics.killeffect.KillEffect;
import me.wolf.wskywars.menu.SkywarsMenu;
import me.wolf.wskywars.player.SkywarsPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KillEffectMenu extends SkywarsMenu {
    public KillEffectMenu(final SkywarsPlayer owner) {
        super(18, "&bKill Effects", owner);

        AtomicInteger i = new AtomicInteger();
        final List<KillEffect> sortedEffects = new ArrayList<>(owner.getKillEffects());
        Collections.sort(sortedEffects);
        sortedEffects.forEach(killEffect -> {
            if(killEffect.isEnabled() || killEffect.getName().equalsIgnoreCase("default")) {
                addItem(killEffect.getIcon(), player -> {
                    owner.setActiveKillEffect(killEffect);
                    owner.sendMessage("&aSuccessfully selected the killeffect &e" + killEffect.getName());
                });
            } else addItem(killEffect.getLockedIcon(), player -> owner.sendMessage("&cYou have not unlocked this kill effect!"));
            i.getAndIncrement();
        });


        openSkywarsMenu(owner);

    }
}

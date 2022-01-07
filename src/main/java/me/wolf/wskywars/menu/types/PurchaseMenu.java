package me.wolf.wskywars.menu.types;

import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.menu.SkywarsMenu;
import me.wolf.wskywars.player.SkywarsPlayer;
import me.wolf.wskywars.scoreboard.SkywarsScoreboard;
import me.wolf.wskywars.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;

public class PurchaseMenu extends SkywarsMenu {
    public PurchaseMenu(SkywarsPlayer owner, final Cosmetic cosmetic, final SkywarsScoreboard scoreboard) {
        super(9, "&a&lPurchase Cosmetic", owner);

        setItem(3, ItemUtils.createItem(Material.GREEN_STAINED_GLASS_PANE, "&a&lPurchase"), player -> {
            if(owner.getCoins() > cosmetic.getPrice()) {
                processTransaction(owner, cosmetic, scoreboard);
                player.closeInventory();
            } else owner.sendMessage("&cNot Enough Coins!");
        });
        setItem(4, cosmetic.getIcon());
        setItem(5, ItemUtils.createItem(Material.RED_STAINED_GLASS_PANE, "&cCancel"), HumanEntity::closeInventory);

        openSkywarsMenu(owner);
    }

    private void processTransaction(final SkywarsPlayer player, final Cosmetic cosmetic, final SkywarsScoreboard scoreboard) {
        player.setCoins(player.getCoins() - cosmetic.getPrice());
        player.unlockCosmetic(cosmetic);
        player.sendMessage("&aSuccessfully purchased this cosmetic!");
        player.sendMessage("&eOld Balance &6" + (player.getCoins() + cosmetic.getPrice()) + "\n" +
                "&eNew Balance &6 " + player.getCoins());

        scoreboard.lobbyScoreboard(player); // update the scoreboard

    }
}

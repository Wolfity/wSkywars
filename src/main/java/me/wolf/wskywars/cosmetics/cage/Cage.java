package me.wolf.wskywars.cosmetics.cage;

import com.sk89q.worldedit.EditSession;
import me.wolf.wskywars.cosmetics.Cosmetic;
import me.wolf.wskywars.cosmetics.CosmeticType;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class Cage extends Cosmetic {

    private EditSession editSession;
    private File schemFile;

    public Cage(String name, ItemStack icon, int price) {
        super(name, icon, price, CosmeticType.CAGE);
    }

    public EditSession getEditSession() {
        return editSession;
    }

    public void setEditSession(EditSession editSession) {
        this.editSession = editSession;
    }

    public File getSchemFile() {
        return schemFile;
    }

    public void setSchemFile(File schemFile) {
        this.schemFile = schemFile;
    }

}

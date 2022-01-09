package me.wolf.wskywars.cage;

import com.sk89q.worldedit.EditSession;

import java.io.File;

public class Cage {

    private final String name;
    private File schemFile;
    private EditSession editSession;

    public Cage(final String name) {
        this.name = name;
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

    public String getName() {
        return name;
    }
}

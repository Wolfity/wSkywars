package me.wolf.wskywars.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class Utils {

    private Utils() {
    }

    public static String colorize(final String input) {
        return input == null ? "Null value" : ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String[] colorize(String... messages) {
        String[] colorized = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            colorized[i] = ChatColor.translateAlternateColorCodes('&', messages[i]);
        }
        return colorized;
    }

    public static void sendCenteredMessage(Player player, String message) {
        int CENTER_PX = 154;
        if (message == null || message.equals("")) player.sendMessage("");
        assert message != null;
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb + message);
    }

    public static void buildCage(final Location location, int sideLength, int height) {
        Material side = Material.GRAY_STAINED_GLASS;
        Material topAndBottom = Material.STONE;

        final int delta = (sideLength / 2);
        final Location corner1 = new Location(location.getWorld(), location.getBlockX() + delta, location.getBlockY() + 1, location.getBlockZ() - delta);
        final Location corner2 = new Location(location.getWorld(), location.getBlockX() - delta, location.getBlockY() + 1, location.getBlockZ() + delta);
        final int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        final int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        final int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        final int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if ((x == minX || x == maxX) || (z == minZ || z == maxZ)) {
                        Block b = corner1.getWorld().getBlockAt(x, location.getBlockY() + y, z);
                        b.setType(side);
                    }

                    if (y == height - 1) {
                        Block b = corner1.getWorld().getBlockAt(x, location.getBlockY() + y + 1, z);
                        b.setType(topAndBottom);
                    }
                    if(y == height - 1) {
                        Block b = corner1.getWorld().getBlockAt(x, location.getBlockY() - y + 1, z);
                        b.setType(topAndBottom);
                    }
                }
            }
        }
    }
}

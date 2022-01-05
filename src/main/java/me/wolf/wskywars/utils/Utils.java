package me.wolf.wskywars.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Utils {

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

    private Utils() {
    }

}

package com.daemitus.deadbolt;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

public final class Util {

    public static String formatForSign(String line, int maxlen) {
        line = removeColor(line);
        line = line.substring(0, line.length() > maxlen ? maxlen : line.length());
        return line;
    }

    public static String formatForSign(String line) {
        return formatForSign(line, 15);
    }

    public static boolean signNameEqualsPlayerName(String signName, String playerName) {
        String playerName15 = formatForSign(playerName);
        return signName.equalsIgnoreCase(playerName15);
    }

    public static Block getSignAttached(Sign signState) {
        return signState.getBlock().getRelative(((WallSign) signState.getBlockData()).getFacing().getOppositeFace());
    }

    public static String removeColor(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.stripColor(text);
    }

    public static String getLine(Sign signBlock, int line) {
        return removeColor(signBlock.getLine(line));
    }
}

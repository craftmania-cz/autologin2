package cz.craftmania.autologin2.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatInfo {

    /**
     * Informative message about everything
     *
     * @param player ProxiedPlayer that receive message
     * @param message Text of message
     */
    public static void info(ProxiedPlayer player, String message) {
        player.sendMessage(ChatColor.GRAY + "> " + ChatColor.GRAY + message);
    }

    /**
     * Successful message if process will be successfully :D
     *
     * @param player ProxiedPlayer that receive message
     * @param message Text of successful message
     */
    public static void success(ProxiedPlayer player, String message) {
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GREEN + message);
    }

    /**
     * Whatever some action fails this can inform player about it
     *
     * @param player ProxiedPlayer that receive message
     * @param message Text of error message
     */
    public static void error(ProxiedPlayer player, String message) {
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[!!] " + ChatColor.RED  + message);
    }

    /**
     * Some warning for player, be careful!
     *
     * @param player ProxiedPlayer that receive message
     * @param message Text of warning message
     */
    public static void warning(ProxiedPlayer player, String message) {
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[!] " + ChatColor.GOLD + message);
    }

    /**
     * Debug message for developers and admin team
     *
     * @param player ProxiedPlayer that receive message
     * @param message Text of debug message
     */
    public static void debug(ProxiedPlayer player, String message) {
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "[D] " + ChatColor.AQUA + message);
    }

}

package cz.craftmania.autologin2.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatInfo {

    /**
     * Informative message about everything
     *
     * @param player CommandSender that receive message
     * @param message Text of message
     */
    public static void info(CommandSender player, String message) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GRAY + "> " + ChatColor.GRAY + message));
    }

    /**
     * Successful message if process will be successfully :D
     *
     * @param player CommandSender that receive message
     * @param message Text of successful message
     */
    public static void success(CommandSender player, String message) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "" + ChatColor.BOLD + ">> " + ChatColor.GREEN + message));
    }

    /**
     * Whatever some action fails this can inform player about it
     *
     * @param player CommandSender that receive message
     * @param message Text of error message
     */
    public static void error(CommandSender player, String message) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "" + ChatColor.BOLD + "[!!] " + ChatColor.RED + message));
    }

    /**
     * Some warning for player, be careful!
     *
     * @param player CommandSender that receive message
     * @param message Text of warning message
     */
    public static void warning(CommandSender player, String message) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "" + ChatColor.BOLD + "[!] " + ChatColor.GOLD + message));
    }

    /**
     * Debug message for developers and admin team
     *
     * @param player CommandSender that receive message
     * @param message Text of debug message
     */
    public static void debug(CommandSender player, String message) {
        player.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "" + ChatColor.BOLD + "[D] " + ChatColor.AQUA + message));
    }

}

package cz.craftmania.autologin2.listeners;

import cz.craftmania.autologin2.AutoLogin;
import cz.craftmania.autologin2.utils.Log;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class LoginListener implements Listener {

    HashMap<String, String> originalTokens = new HashMap<>(); // nick : address
    HashMap<String, String> warezTokens = new HashMap<>(); // nick : address
    HashMap<String, String> cache2 = new HashMap<>(); // address : nick

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        PendingConnection connection = event.getConnection();
        String nick = connection.getName();
        String address = connection.getAddress().getAddress().getCanonicalHostName();

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");

        Log.debug(nick + " (" + address + ") is connecting to server...");

        if (pattern.matcher(nick.replace("_", "")).find()) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', AutoLogin.getOptions().getInvalidNick())));
            event.setCancelled(true);
            Log.info(nick + " má nepovolené znaky v nicku - nebyl připojen.");
            return;
        }

        if (!AutoLogin.getSqlManager().getNickFromDatabase(nick).equals(nick)) {
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', AutoLogin.getOptions().getChangeNick())));
            event.setCancelled(true);
            Log.info(nick + " má upravený nick originálky - nebyl připojen.");
            return;
        }

        if (AutoLogin.getSqlManager().isInDatabase(nick)) {
            Log.debug(nick + " is in database, online-mode: true");
            connection.setOnlineMode(true);
            this.warezTokens.remove(nick);
            this.cache2.remove(address);
            this.originalTokens.put(nick, address);
            return;
        }

        Log.debug("(1) online-mode: false");

        if (this.originalTokens.containsKey(nick) && this.originalTokens.get(nick).equals(address)) {
            // Token is registered, but player is no longer in database - will get kicked
            this.originalTokens.remove(nick);
            event.setCancelReason(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', AutoLogin.getOptions().getInvalidToken())));
            event.setCancelled(true);
            return;
        }

        connection.setOnlineMode(true);
        Log.debug("(2) online-mode: true");

        if (!AutoLogin.getLoginManager().isOriginal(nick)) {
            // Not an original username
            Log.debug(nick + " is warez nick, online-mode: false");
            connection.setOnlineMode(false);
            return;
        } else {
            Log.debug(nick + " is original nick, online-mode: true");
        }

        if (this.warezTokens.containsKey(nick) && this.warezTokens.get(nick).equalsIgnoreCase(address)) {
            // Player already connected, he is not original.
            Log.debug("(3) online-mode: false");
            connection.setOnlineMode(false);
            return;
        }

        if (this.cache2.containsKey(address) && this.cache2.get(address).equalsIgnoreCase(nick)) {
            this.warezTokens.put(nick, address);
            Log.debug("(4) online-mode: false [player has tried to use warez as original]");
            connection.setOnlineMode(false);
        }

        this.cache2.put(address, nick);
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        String nick = connection.getName();
        UUID uuid = connection.getUniqueId();
        String address = connection.getAddress().getAddress().getCanonicalHostName();

        this.originalTokens.remove(nick);
        if (this.warezTokens.containsKey(nick) && this.warezTokens.get(nick).equalsIgnoreCase(address)) return;
        if (AutoLogin.getSqlManager().isInDatabase(nick)) return;
        if (connection.isOnlineMode()) {
            // Player is in cache2
            Log.debug("Inserting into database: " + nick + " (UUID: " + uuid + ")");
            AutoLogin.getSqlManager().insertData(uuid, nick);
            this.warezTokens.remove(nick);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo server = event.getTarget();
        if (AutoLogin.getOptions().getAuthServers().contains(server)) {
            // Player is on auth server
            if (AutoLogin.getSqlManager().isInDatabase(player.getName())) {
                ServerInfo target = AutoLogin.getLoginManager().getRandomLobby();
                if (target == null) return;
                event.setTarget(target);
                Log.debug(player.getName() + " is original, forwarding to: " + target.getName());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (AutoLogin.getSqlManager().isInDatabase(player.getName())) AutoLogin.getSqlManager().quit(player.getName(), player.getUniqueId());
    }

}

package cz.craftmania.autologin2.listeners;

import cz.craftmania.autologin2.Main;
import cz.craftmania.autologin2.utils.Log;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class LoginListener implements Listener {

    HashMap<String, String> cd2 = new HashMap<>(); // nick : address
    HashMap<String, String> namesock = new HashMap<>(); // nick : address
    HashMap<String, String> prev = new HashMap<>(); // address : nick

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        PendingConnection connection = event.getConnection();
        System.out.println(connection);
        String nick = connection.getName();
        String address = connection.getAddress().getAddress().getCanonicalHostName();

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");

        Log.debug(nick + " (" + address + ") is connecting to server...");

        if (pattern.matcher(nick.replace("_", "")).find()) {
            event.setCancelReason(ChatColor.translateAlternateColorCodes('&', Main.getOptions().getInvalidNick()));
            event.setCancelled(true);
            Log.debug(nick + " má nepovolené znaky v nicku - nebyl připojen.");
            return;
        }

        if (Main.getSqlManager().isInDatabase(nick)) {
            Log.debug(nick + " is in database, online-mode: true");
            connection.setOnlineMode(true);
            if (this.namesock.containsKey(nick)) {
                this.namesock.remove(nick);
            }
            if (this.prev.containsKey(address)) {
                this.prev.remove(address);
            }
            this.cd2.put(nick, address);
            return;
        }
        Log.debug("online-mode: false");
        if (!this.cd2.isEmpty() && this.cd2.containsKey(nick) && this.cd2.get(nick).equals(address)) {
            this.cd2.remove(nick);
            event.setCancelReason(ChatColor.translateAlternateColorCodes('&', Main.getOptions().getInvalidToken()));
            event.setCancelled(true);
            return;
        }
        connection.setOnlineMode(true);
        Log.debug("online-mode: true");
        if (!Main.getLoginManager().isOriginal(nick)) {
            // Not an original username
            Log.debug("online-mode: false");
            connection.setOnlineMode(false);
            return;
        }
        if (this.namesock.containsKey(nick) && this.namesock.get(nick).equalsIgnoreCase(address)) {
            Log.debug("online-mode: false");
            connection.setOnlineMode(false);
            return;
        }
        if (this.prev.containsKey(address) && this.prev.get(address).equalsIgnoreCase(nick)) {
            this.namesock.put(nick, address);
            Log.debug("online-mode: false");
            connection.setOnlineMode(false);
        }
        this.prev.put(address, nick);
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        String nick = connection.getName();
        UUID uuid = connection.getUniqueId();
        String address = connection.getAddress().getAddress().getCanonicalHostName();

        if (this.cd2.containsKey(nick)) this.cd2.remove(nick);
        if (this.namesock.containsKey(nick) && this.namesock.get(nick).equalsIgnoreCase(address)) return;
        if (Main.getSqlManager().isInDatabase(nick)) return;
        if (connection.isOnlineMode()) {
            Log.debug("Inserting into database: " + nick + " (UUID: " + uuid + ")");
            Main.getSqlManager().insertData(uuid, nick);
            this.namesock.remove(nick);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo server = event.getTarget();
        if (Main.getOptions().getAuthServers().contains(server)) {
            // Player is on auth server
            if (Main.getSqlManager().isInDatabase(player.getName())) {
                ServerInfo target = Main.getLoginManager().getRandomLobby();
                event.setTarget(target);
                Log.debug(player.getName() + " is original, forwarding to: " + target.getName());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (Main.getSqlManager().isInDatabase(player.getName())) Main.getSqlManager().quit(player.getName());
    }

}

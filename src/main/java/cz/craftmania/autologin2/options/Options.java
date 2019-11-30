package cz.craftmania.autologin2.options;

import cz.craftmania.autologin2.Main;
import cz.craftmania.autologin2.utils.Log;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;

import java.util.ArrayList;
import java.util.List;

public class Options {

    public String  invalidNick, invalidToken;
    public List<ServerInfo> authServers, lobbyServers;
    public boolean debug;
    public int connectTimeout, readTimeout;

    public void init() {
        this.authServers = new ArrayList<>();
        this.lobbyServers = new ArrayList<>();
        this.debug = Main.getConfig().getBoolean("debug");
        this.invalidNick = Main.getConfig().getString("messages.invalid_nick", "&cTento nick obsahuje neplatné znaky!");
        this.invalidToken = Main.getConfig().getString("messages.invalid_token", "&cVypadá to, že se snažíš připojit s warez na originální nick - připoj se znovu.");
        for (String s : Main.getConfig().getStringList("servers.auth")) {
            Log.debug("Adding auth server: " + s);
            authServers.add(Main.getInstance().getProxy().getServerInfo(s));
        }
        for (String s : Main.getConfig().getStringList("servers.lobbies")) {
            Log.debug("Adding lobby server: " + s);
            lobbyServers.add(Main.getInstance().getProxy().getServerInfo(s));
        }
        this.connectTimeout = Main.getConfig().getInt("connectivity.connect_timeout");
        this.readTimeout = Main.getConfig().getInt("connectivity.read_timeout");
    }

    public List<ServerInfo> getAuthServers() {
        return authServers;
    }

    public void setAuthServers(List<ServerInfo> authServers) {
        this.authServers = authServers;
    }

    public List<ServerInfo> getLobbyServers() {
        return lobbyServers;
    }

    public void setLobbyServers(List<ServerInfo> lobbyServers) {
        this.lobbyServers = lobbyServers;
    }

    public String getInvalidNick() {
        return invalidNick;
    }

    public void setInvalidNick(String invalidNick) {
        this.invalidNick = invalidNick;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getInvalidToken() {
        return invalidToken;
    }

    public void setInvalidToken(String invalidToken) {
        this.invalidToken = invalidToken;
    }
}

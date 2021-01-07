package cz.craftmania.autologin2.options;

import cz.craftmania.autologin2.AutoLogin;
import cz.craftmania.autologin2.utils.Log;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.List;

public class Options {

    public String  invalidNick, invalidToken, changeNick;
    public List<ServerInfo> authServers, lobbyServers;
    public boolean debug;
    public int connectTimeout, readTimeout;

    public void init() {
        this.authServers = new ArrayList<>();
        this.lobbyServers = new ArrayList<>();
        this.debug = AutoLogin.getConfig().getBoolean("debug");
        this.invalidNick = AutoLogin.getConfig().getString("messages.invalid_nick", "&cTento nick obsahuje neplatné znaky!");
        this.invalidToken = AutoLogin.getConfig().getString("messages.invalid_token", "&cVypadá to, že se snažíš připojit s warez na originální nick - připoj se znovu.");
        this.changeNick = AutoLogin.getConfig().getString("messages.change_nick", "&cVypadá to, že používaš upravený nick originálky, změn si ho.");
        for (String s : AutoLogin.getConfig().getStringList("servers.auth")) {
            Log.debug("Adding auth server: " + s);
            authServers.add(AutoLogin.getInstance().getProxy().getServerInfo(s));
        }
        for (String s : AutoLogin.getConfig().getStringList("servers.lobbies")) {
            Log.debug("Adding lobby server: " + s);
            lobbyServers.add(AutoLogin.getInstance().getProxy().getServerInfo(s));
        }
        this.connectTimeout = AutoLogin.getConfig().getInt("connectivity.connect_timeout");
        this.readTimeout = AutoLogin.getConfig().getInt("connectivity.read_timeout");
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

    public String getChangeNick() {
        return changeNick;
    }

    public void setChangeNick(String changeNick) {
        this.changeNick = changeNick;
    }
}

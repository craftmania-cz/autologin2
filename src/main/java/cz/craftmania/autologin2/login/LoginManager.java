package cz.craftmania.autologin2.login;

import cz.craftmania.autologin2.Main;
import cz.craftmania.autologin2.utils.Log;
import net.md_5.bungee.api.config.ServerInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

public class LoginManager {

    private final List<String> warezNicks = new ArrayList<>();
    private final List<String> originalNicks = new ArrayList<>();
    private final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    /**
     * Checks if nick is purchased as original copy.
     *
     * @param nick Nick to check.
     * @return False if nick is warez, otherwise true.
     */
    private boolean isOriginalNick(String nick) {
        if (warezNicks.contains(nick)) return false;
        if (originalNicks.contains(nick)) return true;
        JSONObject json;
        try {
            Log.debug("Connecting to MineTools API (nick: " + nick + ").");
            OkHttpClient caller = new OkHttpClient();
            Request request = (new Request.Builder()).url("https://api.minetools.eu/uuid/" + nick).build();
            Response response = caller.newCall(request).execute();
            if (response.body() == null) return false;
            json = new JSONObject(response.body().string());
            Log.debug("Connected to MineTools API.");
            if (json.isNull("id") || json.get("id") == null) {
                warezNicks.add(nick);
                return false;
            }
            originalNicks.add(nick);
            return true;
        } catch (Exception e) {
            warezNicks.add(nick);
            Log.debug("Error while connecting to MineTools API - nick is not original.");
            return false;
        }

    }

    /**
     * Checks if nick is original.
     *
     * @param nick Nick to check.
     * @return True if nick is original, otherwise false.
     */
    public boolean isOriginal(String nick) {
        if (this.originalNicks.contains(nick)) return true;
        if (this.warezNicks.contains(nick)) return false;
        if (isOriginalNick(nick)) return true;

        this.warezNicks.add(nick);
        return false;
    }

    private UUID fromTrimmed(String uuid) {
        return UUID.fromString(UUID_FIX.matcher(uuid.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }

    public ServerInfo getRandomLobby() {
        Random r = new Random();
        List<ServerInfo> servers = Main.getOptions().getLobbyServers();
        if (servers.size() == 0) return null;
        if (servers.size() == 1) return servers.get(0);
        return servers.get(r.nextInt(servers.size() - 1));
    }
}

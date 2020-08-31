package cz.craftmania.autologin2.login;

import cz.craftmania.autologin2.AutoLogin;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private CompletableFuture<Boolean> isOriginalNick(String nick) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (warezNicks.contains(nick)) completableFuture.complete(false);
        if (originalNicks.contains(nick)) completableFuture.complete(true);
        JSONObject json;
        try {
            Log.debug("Connecting to MineTools API (nick: " + nick + ").");
            OkHttpClient caller = new OkHttpClient();
            Request request = (new Request.Builder()).url("https://api.minetools.eu/uuid/" + nick).build();
            Response response = caller.newCall(request).execute();
            if (response.body() == null) completableFuture.complete(false);
            json = new JSONObject(response.body().string());
            Log.debug("Connected to MineTools API.");
            if (json.isNull("id") || json.get("id") == null) {
                warezNicks.add(nick);
                completableFuture.complete(false);
            }
            originalNicks.add(nick);
            completableFuture.complete(true);
        } catch (Exception e) {
            warezNicks.add(nick);
            Log.debug("Error while connecting to MineTools API - nick is not original.");
            completableFuture.completeExceptionally(e);
        }
        return completableFuture;
    }

    /**
     * Checks if nick is original.
     *
     * @param nick Nick to check.
     * @return True if nick is original, otherwise false.
     */
    public boolean isOriginal(String nick) {
        AtomicBoolean output = new AtomicBoolean(false);
        if (this.originalNicks.contains(nick)) output.set(true);
        if (this.warezNicks.contains(nick)) output.set(false);
        CompletableFuture<Boolean> completableFuture = isOriginalNick(nick);
        completableFuture.thenAccept(output::set);

        this.warezNicks.add(nick);
        return output.get();
    }

    private UUID fromTrimmed(String uuid) {
        return UUID.fromString(UUID_FIX.matcher(uuid.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }

    public ServerInfo getRandomLobby() {
        Random r = new Random();
        List<ServerInfo> servers = AutoLogin.getOptions().getLobbyServers();
        if (servers.size() == 0) return null;
        if (servers.size() == 1) return servers.get(0);
        return servers.get(r.nextInt(servers.size() - 1));
    }
}

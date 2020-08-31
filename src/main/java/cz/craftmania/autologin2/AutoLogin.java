package cz.craftmania.autologin2;

import com.google.common.io.Files;
import cz.craftmania.autologin2.commands.AutoLoginCommand;
import cz.craftmania.autologin2.listeners.LoginListener;
import cz.craftmania.autologin2.login.LoginManager;
import cz.craftmania.autologin2.options.Options;
import cz.craftmania.autologin2.sql.SQLManager;
import cz.craftmania.autologin2.utils.Log;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class AutoLogin extends Plugin {

    private static SQLManager sqlManager;
    private static AutoLogin instance;
    private static File configFile;
    private static Configuration config;
    private static Options options;
    private static LoginManager loginManager;

    @Override
    public void onEnable() {
        Log.info("Initializing...");

        // Instance
        instance = this;

        loadConfig();

        Log.info("Loading database...");
        sqlManager = new SQLManager();
        sqlManager.createTable();

        options = new Options();
        options.init();

        loginManager = new LoginManager();

        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerCommand(this, new AutoLoginCommand());
    }

    @Override
    public void onDisable() {
        instance = null;
        sqlManager.onDisable();
    }

    public static AutoLogin getInstance() {
        return instance;
    }

    public static SQLManager getSqlManager() {
        return sqlManager;
    }

    public static Configuration getConfig() {
        return config;
    }

    public static Options getOptions() {
        return options;
    }

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    public void loadConfig() {
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdir();
            }
            AutoLogin.configFile = new File(this.getDataFolder(), "config.yml");
            if (!AutoLogin.configFile.exists()) {
                try (InputStream in = getResourceAsStream("config.yml")) {
                    java.nio.file.Files.copy(in, configFile.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            final InputStream configInputStream = Files.asByteSource(AutoLogin.configFile).openStream();
            AutoLogin.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new BufferedReader(new InputStreamReader(configInputStream)));
            configInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        this.loadConfig();
    }

    public static void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(AutoLogin.config, AutoLogin.configFile);
        } catch (IOException e) {
            getInstance().getLogger().warning("Config could not be saved!");
            e.printStackTrace();
        }
    }

}

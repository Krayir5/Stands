package com.krayir5.stands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.commands.HelpCommand;
import com.krayir5.stands.commands.StandPCommand;
import com.krayir5.stands.commands.StandPick;
import com.krayir5.stands.commands.StandUse;
import com.krayir5.stands.commands.StandsCommand;
import com.krayir5.stands.listeners.PlayerDeathListener;
import com.krayir5.stands.listeners.PlayerJoinListener;
import com.krayir5.stands.listeners.StandItem;
import com.krayir5.stands.listeners.StandListener;
import com.krayir5.stands.utils.Metrics;
@SuppressWarnings("")
public class Plugin extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger("Stands");
    private String latestVersion;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {getDataFolder().mkdirs();}
        LOGGER.info("You expected a message, but it was me, DIO!");
        getCommand("sphelp").setExecutor(new HelpCommand());
        getCommand("stands").setExecutor(new StandsCommand());
        File standFile = new File(getDataFolder(), "stands.yml");
        getCommand("standp").setExecutor(new StandPCommand(this, standFile));
        getCommand("standpick").setExecutor(new StandPick(getConfig(), standFile));
        getCommand("standuse").setExecutor(new StandUse(standFile));
        getServer().getPluginManager().registerEvents(new StandItem(), this);
        getServer().getPluginManager().registerEvents(new StandListener(this, standFile), this);
        checkForUpdates();
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(getDescription().getVersion(), latestVersion), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(standFile), this);
        saveDefaultConfig();
        updateConfig();
        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, 24363);
    }

    @Override
    public void onDisable() {
        LOGGER.info("Plugin successfully cut in half.");
    }

    public void updateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        try {
            InputStream inputStream = getResource("config.yml");
            Reader reader = new InputStreamReader(inputStream);
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(reader);
            FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile);
            for (String key : defaultConfig.getKeys(true)) {
                if (!currentConfig.contains(key)) {
                    currentConfig.set(key, defaultConfig.get(key));
                }
            }
            currentConfig.save(configFile);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to update config file: {0}", e.getMessage());
        }
    }

    private void checkForUpdates() {
        try {
            String pluginYmlUrl = "https://raw.githubusercontent.com/Krayir5/Stands/main/src/main/resources/plugin.yml";
            String remotePluginYml = fetchPluginYml(pluginYmlUrl);
            latestVersion = parseVersionFromPluginYml(remotePluginYml);
            if (latestVersion != null && !getDescription().getVersion().equals(latestVersion)) {
              getLogger().log(Level.WARNING, "[Stands] A new version of the plugin is available: {0}", latestVersion);
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "[Stands] Failed to check for updates: {0}", e.getMessage());
        }
    }

    private String fetchPluginYml(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    private String parseVersionFromPluginYml(String pluginYmlContent) {
        for (String line : pluginYmlContent.split("\n")) {
            if (line.startsWith("version:")) {
                return line.split(":")[1].trim();
            }
        }
        return null;
    }
}

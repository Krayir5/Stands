package com.krayir5.stands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.commands.StandCommand;
import com.krayir5.stands.listeners.PlayerDeathListener;
import com.krayir5.stands.listeners.PlayerJoinListener;
import com.krayir5.stands.listeners.StandItem;
import com.krayir5.stands.listeners.StandListener;
import com.krayir5.stands.utils.GUIM.MenuL;
import com.krayir5.stands.utils.Metrics;

import de.tr7zw.changeme.nbtapi.NBT;
@SuppressWarnings("")
public class Plugin extends JavaPlugin {
    private static Plugin instance;
    public static Plugin inst() {
        return instance;
    }
    private static final Logger LOGGER = Logger.getLogger("Stands");
    private String latestVersion;

    @Override
    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists()) {getDataFolder().mkdirs();}
        LOGGER.info("You expected a message, but it was me, DIO!");
        File standFile = new File(getDataFolder(), "stands.yml");
        getCommand("stand").setExecutor(new StandCommand(this, standFile));
        getCommand("stand").setTabCompleter(new StandCommand(this, standFile));
        getServer().getPluginManager().registerEvents(new StandItem(), this);
        getServer().getPluginManager().registerEvents(new MenuL(), this);
        getServer().getPluginManager().registerEvents(new StandListener(getConfig(), this, standFile), this);
        //getServer().getPluginManager().registerEvents(new StandAS(this), this); Still not ready :c
        checkForUpdates();
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(getDescription().getVersion(), latestVersion, standFile), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(standFile), this);
        sAR();
        saveDefaultConfig();
        updateConfig();
        if (!NBT.preloadApi()) {
            LOGGER.log(Level.WARNING, "NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }
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

    public void sAR() {
        ItemStack sA = sAC();
        NamespacedKey key = new NamespacedKey(this, "stand_arrow");
        ShapedRecipe r = new ShapedRecipe(key, sA);
        r.shape(" 21", " 32", "3  ");
        r.setIngredient('1', Material.NETHERITE_INGOT);
        r.setIngredient('2', Material.GOLD_INGOT);
        r.setIngredient('3', Material.STICK);
        Bukkit.addRecipe(r);
    }

    public ItemStack sAC() {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName("§6Stand Arrow");
        meta.setLore(Collections.singletonList("§7Right clicking will give you a stand!"));
        meta.setCustomModelData(1071);
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(this, "stand_arrow");
        data.set(key, PersistentDataType.STRING, "s_arrow");
        arrow.setItemMeta(meta);
        return arrow;
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

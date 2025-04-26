package com.krayir5.stands.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
@SuppressWarnings("unused")
public class LocacacaM{
    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;

    public LocacacaM(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "locacaca.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe(String.format("locacaca.yml couldn't be created: %s", e.getMessage()));
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe(String.format("locacaca.yml couldn't be saved: %s", e.getMessage()));
        }
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return config;
    }
}

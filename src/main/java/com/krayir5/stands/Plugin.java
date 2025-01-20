package com.krayir5.stands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import com.krayir5.stands.listeners.StandItem;
import com.krayir5.stands.listeners.StandListener;
import com.krayir5.stands.utils.Metrics;

public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("Stands");
  
  @SuppressWarnings("")
  @Override
  public void onEnable()
  {
    if (!getDataFolder().exists()) {
      getDataFolder().mkdirs();
  }
    LOGGER.info("You expected a message, but it was me, DIO!");
    getCommand("sphelp").setExecutor(new HelpCommand());
    getCommand("stands").setExecutor(new StandsCommand());
    getCommand("standp").setExecutor(new StandPCommand(this));
    File standFile = new File(getDataFolder(), "stands.yml");
    getCommand("standpick").setExecutor(new StandPick(getConfig(), standFile));
    getCommand("standuse").setExecutor(new StandUse(standFile));
    getServer().getPluginManager().registerEvents(new StandItem(), this);
    getServer().getPluginManager().registerEvents(new StandListener(this, standFile), this);
    saveDefaultConfig();
    updateConfig();
    @SuppressWarnings("unused")
    Metrics metrics = new Metrics(this, 24363);
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

  @Override
  public void onDisable()
  {
    LOGGER.info("Plugin successfully cut in half.");
  }
}

package com.krayir5.stands;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.commands.HelpCommand;
import com.krayir5.stands.commands.StandPCommand;
import com.krayir5.stands.commands.StandsCommand;
import com.krayir5.stands.listeners.StandListener;

public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("Stands");
  
  @Override
  public void onEnable()
  {
    LOGGER.info("You expected a message, but it was me, DIO!");
    getCommand("sphelp").setExecutor(new HelpCommand());
    getCommand("stands").setExecutor(new StandsCommand());
    getCommand("standp").setExecutor(new StandPCommand(this));
    getServer().getPluginManager().registerEvents(new StandListener(this), this);
    saveDefaultConfig();
  }
  @Override
  public void onDisable()
  {
    LOGGER.info("Plugin successfully cut in half.");
  }
}

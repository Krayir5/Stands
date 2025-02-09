package com.krayir5.stands.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StandPick implements CommandExecutor {

    private final FileConfiguration config;
    private final File standFile;
    private final FileConfiguration standConfig;

    public StandPick(FileConfiguration config, File standFile) {
        this.config = config;
        this.standFile = standFile;
        this.standConfig = YamlConfiguration.loadConfiguration(standFile);
        standFileC();
    }

    private void standFileC() {
        try {
            if (!standFile.exists()) {
                standFile.createNewFile();
                Bukkit.getLogger().info("stands.yml file created successfully.");
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe(String.format("stands.yml couldn't crated: %s", e.getMessage()));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender == null)return true;
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players could use this command!");
            return true;
        }
        if (!standFile.exists()) {
            standFileC();
        }
        try {
            standConfig.load(standFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            sender.sendMessage(ChatColor.RED + "Failed to load stand data.");
            Bukkit.getLogger().severe(String.format("stands.yml couldn't be loaded: %s", e.getMessage()));
            return true;
        }
        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();
        String playerPath = "players." + playerId;
        if (standConfig.contains(playerPath + ".stand")){
            String stand = standConfig.getString(playerPath + ".stand");
            player.sendMessage(ChatColor.RED + "You've already picked a stand: " + ChatColor.GOLD + stand);
            return true;
        }
        String stand = getRandomStand();
        standConfig.set(playerPath + ".stand", stand);
        standConfig.set(playerPath + ".standLog", new ArrayList<>());
        standConfig.set(playerPath + ".standXP.Stand.XP", 0);
        standConfig.set(playerPath + ".standXP.Stand.Level", 1);
        standConfig.set(playerPath + ".standXP.Ability1.XP", 0);
        standConfig.set(playerPath + ".standXP.Ability1.Level", 1);
        standConfig.set(playerPath + ".standXP.Ability2.XP", 0);
        standConfig.set(playerPath + ".standXP.Ability2.Level", 0);
        standConfig.set(playerPath + ".standXP.Ability3.XP", 0);
        standConfig.set(playerPath + ".standXP.Ability3.Level", 0);
        saveSC();
        player.sendMessage(ChatColor.GREEN + "Stand Arrow touched you and now your stand is: " + ChatColor.GOLD + stand);
        return true;
    }

    private String getRandomStand() {
        Map<String, List<String>> rarityMap = new HashMap<>();
        rarityMap.put("Common", config.getStringList("Stands.Common"));
        rarityMap.put("Rare", config.getStringList("Stands.Rare"));
        rarityMap.put("Epic", config.getStringList("Stands.Epic"));
        rarityMap.put("Legendary", config.getStringList("Stands.Legendary"));

        int chance = new Random().nextInt(100) + 1;
        List<String> targetList;

        if (chance <= 50) targetList = rarityMap.get("Common");
        else if (chance <= 80) targetList = rarityMap.get("Rare");
        else if (chance <= 95) targetList = rarityMap.get("Epic");
        else targetList = rarityMap.get("Legendary");
        return targetList.isEmpty() ? "Unknown Stand" : 
             targetList.get(new Random().nextInt(targetList.size()));
    }

    private void saveSC() {
        try {
            standConfig.save(standFile);
        }catch (IOException e) {
            Bukkit.getLogger().severe(String.format("stands.yml couldn't saved: %s", e.getMessage()));
        }
    }
}

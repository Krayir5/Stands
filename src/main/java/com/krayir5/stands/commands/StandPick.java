package com.krayir5.stands.commands;

import java.io.File;
import java.io.IOException;
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

@SuppressWarnings("")
public class StandPick implements CommandExecutor {

    private final FileConfiguration config;
    private final File standFile;
    private final FileConfiguration standConfig;

    public StandPick(FileConfiguration config, File standFile) {
        this.config = config;
        this.standFile = standFile;
        this.standConfig = YamlConfiguration.loadConfiguration(standFile);
        createStandFileIfNotExists();
    }

    private void createStandFileIfNotExists() {
        try {
            if (!standFile.exists()) {
                standFile.createNewFile();
                Bukkit.getLogger().info("stands.yml file crated successfully.");
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe(String.format("stands.yml couldn't crated: %s", e.getMessage()));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players could use this command!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (standConfig.contains(playerId.toString())) {
            String stand = standConfig.getString(playerId.toString());
            player.sendMessage(ChatColor.RED + "You've already picked a stand: " + ChatColor.GOLD + stand);
            return true;
        }

        String stand = getRandomStand();
        standConfig.set(playerId.toString(), stand);
        saveStandConfig();

        player.sendMessage(ChatColor.GREEN + "Stand Arrow touched you and now your stand is: " + ChatColor.GOLD + stand);
        return true;
    }

    private String getRandomStand() {
        Map<String, List<String>> rarityMap = new HashMap<>();
        rarityMap.put("Common", config.getStringList("Stands.Common"));
        rarityMap.put("Rare", config.getStringList("Stands.Rare"));
        rarityMap.put("Epic", config.getStringList("Stands.Epic"));
        rarityMap.put("Legendary", config.getStringList("Stands.Legendary"));

        int randomChance = new Random().nextInt(100) + 1;

        if (randomChance <= 50) {
            return getRandomFromList(rarityMap.get("Common"));
        } else if (randomChance <= 80) {
            return getRandomFromList(rarityMap.get("Rare"));
        } else if (randomChance <= 95) {
            return getRandomFromList(rarityMap.get("Epic"));
        } else {
            return getRandomFromList(rarityMap.get("Legendary"));
        }
    }

    private String getRandomFromList(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    private void saveStandConfig() {
        try {
            standConfig.save(standFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe(String.format("stands.yml couldn't saved: %s", e.getMessage()));
        }
    }
}

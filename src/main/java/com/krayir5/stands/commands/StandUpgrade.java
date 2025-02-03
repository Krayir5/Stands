package com.krayir5.stands.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.krayir5.stands.utils.GUIS.StandUMenu;

public class StandUpgrade implements CommandExecutor {
    private final File standFile;

    public StandUpgrade(File standFile) {
        this.standFile = standFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();
            FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
            String stand = stConfig.getString("players." + playerId + ".stand");
            StandUMenu menu = new StandUMenu(player);
            if (stand == null) {
                player.sendMessage("§4You need to pick a stand first. You can do it by §n/standpick");
            }else{
                menu.open();
            }
            return true;
        } else {
            return true;
        }
    }
}

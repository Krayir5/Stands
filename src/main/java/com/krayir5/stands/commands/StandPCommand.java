package com.krayir5.stands.commands;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StandPCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final File standFile;

    public StandPCommand(JavaPlugin plugin, File standFile) {
        this.plugin = plugin;
        this.standFile = standFile;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        @SuppressWarnings("unused")
        FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
        Player player = (Player) sender;
        if (!player.isOp() || !player.hasPermission("standp.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /standp <reload>");
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "reload":
                plugin.reloadConfig();
                if (standFile.exists()) {
                    stConfig = YamlConfiguration.loadConfiguration(standFile);
                    player.sendMessage(ChatColor.GREEN + "Plugin reloaded succesfully!");
                }else{
                    player.sendMessage(ChatColor.RED + "Something went wrong! stand.yml file not found!");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                break;
        }
        return true;
    }
}


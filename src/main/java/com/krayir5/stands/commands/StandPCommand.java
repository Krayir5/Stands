package com.krayir5.stands.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StandPCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public StandPCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (!player.isOp() && !player.hasPermission("standp.admin")) {
            player.sendMessage(ChatColor.RED + "You cant use this command!");
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
                sender.sendMessage(ChatColor.GREEN + "Plugin succesfully reloaded!");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                break;
        }
        return true;
    }
}

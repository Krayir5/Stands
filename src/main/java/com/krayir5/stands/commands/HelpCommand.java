package com.krayir5.stands.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§aHelp Menu:");
        sender.sendMessage("§e/sphelp stands - Show you all of the registered stands.");
        sender.sendMessage("§e/stands - Shows you a spesific stand's abilities.");
        sender.sendMessage("§e/standpick - With this command Stand Arrow will choose your stand.");
        sender.sendMessage("§e/standuse - Gives you your stand item.");
        return true;
    }
}

package com.krayir5.stands.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    private final Map<String, String> stands = new HashMap<>();

    public HelpCommand() {
        stands.put("TheWorld", "A stand that can stop time.");
        stands.put("StarPlatinum", "A stand that is so powerfull.");
        stands.put("CrazyDiamond", "You can heal another entity.");
        stands.put("MagicianRed", "You can throw fireballs at your enemies.");
        stands.put("HermitPurple", "You can pull enemies towards you.");
        stands.put("KillerQueen", "You can turn any block to a explosive!");
        stands.put("NovemberRain", "A rain that contains small area but a powerfull one.");
        stands.put("SilverChariot", "A powerfull and speedfull stand that shivers his sword to his enemies.");
        stands.put("Tusk", "A long range stand that can shoot nails to his enemies.");
        stands.put("HierophantGreen", "A long range stand that can shoot emeralds to his enemies.");
        stands.put("HeavensDoor", "A stand that can open his enemies like a book.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("stands")) {
            sender.sendMessage("§aRegistered Stands:");
            stands.forEach((stand, description) -> sender.sendMessage("§e" + stand + " - §f" + description));
            return true;
        }
        sender.sendMessage("§aHelp Menu:");
        sender.sendMessage("§e/sphelp stands - Show you all of the registered stands.");
        sender.sendMessage("§e/stands <StandName> - Shows you a spesific stands abilities.");
        sender.sendMessage("§e/standpick - With this command Stand Arrow will choose your stand.");
        sender.sendMessage("§e/standuse - Gives you your stand item.");
        return true;
    }
}

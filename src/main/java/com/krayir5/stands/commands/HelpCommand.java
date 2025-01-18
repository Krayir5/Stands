package com.krayir5.stands.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandExecutor {
    private final Map<String, String> stands = new HashMap<>();

    public HelpCommand() {
        stands.put("TheWorld", "A stand that can stop time. You could use it with a netherite ingot. With a golden ingot you could MUDA MUDA your enemies!");
        stands.put("StarPlatinum", "A stand that is so powerfull. You could use iron ingot to activate it's power!");
        stands.put("CrazyDiamond", "You can heal another entity. You need to use diamond to do it.");
        stands.put("MagicianRed", "You can throw fireballs at your enemies. You need a blaze rod to use it");
        stands.put("HermitPurple", "You can pull enemies towards you. You need feather to use it.");
        stands.put("KillerQueen", "You can turn any block to a explosive! You need Quartz to use it.");
        stands.put("NovemberRain", "A rain that contains small area but a powerfull one. You need a Nether Star to activate it.");
        stands.put("SilverChariot", "A powerfull and speedfull stand that shivers his sword to his enemies. You need an iron sword to use it.");
        stands.put("Tusk", "A long range stand that can shoot nails to his enemies. You need spider eye to use it.");
        stands.put("HierophantGreen", "A long range stand that can shoot emeralds to his enemies. You need emerald to use it.");
        stands.put("HeavensDoor", "A stand that can open his enemies like a book. You need a book to use it.");
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
        return true;
    }
}

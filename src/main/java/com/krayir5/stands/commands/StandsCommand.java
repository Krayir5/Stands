package com.krayir5.stands.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StandsCommand implements CommandExecutor {
    private final Map<String, String> stands = new HashMap<>();
    public StandsCommand() {
        stands.put("TheWorld", "Pressing F will stop time. Right clicking an entity will throw punches to it. While holding shift right clicking will cause to throw knifes.");
        stands.put("StarPlatinum", "Pressing F will stop time. Right clicking an entity will throw punches to it. While holding shift right clicking will activate Star Finger.");
        stands.put("CrazyDiamond", "Pressing F will selfheal. Right clicking an entity will throw heal it. While holding shift right clicking will cause to throw punches.");
        stands.put("MagicianRed", "Pressing F will send fireballs. Right clicking an entity will throw punches to it.");
        stands.put("HermitPurple", "Pressing F will pull entitys to you.");
        stands.put("KillerQueen", "Pressing F will throw a Bubble that's explosive. Right clicking an entity will throw punches to it. Right clicking a block will turn it into a explosive.");
        stands.put("NovemberRain", "Pressing F will create a rain that contains small area but a powerfull one.");
        stands.put("SilverChariot", "Right clicking an entity will shiver his sword to his enemies.");
        stands.put("Tusk", "Pressing F will cause to throw a powerfull nail.");
        stands.put("HierophantGreen", "Pressing F will send emeralds. Right clicking an entity will throw punches to it.");
        stands.put("HeavensDoor", "Right click an entity to stun him and give him weakness.");
        stands.put("SoftAndWet", "Pressing F will send a bubble that gives entity a negative effect. Right clicking to enemy will throw punches to it.");
        stands.put("SexyPistols", "Pressing F will shoots bullet that track your enemy.");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cWrong Usage: §e/stands <StandName>");
            return true;
        }

        String standName = args[0];
        if (stands.containsKey(standName)) {
            sender.sendMessage("§aStand: §e" + standName);
            sender.sendMessage("§fAbilities: " + stands.get(standName));
        } else {
            sender.sendMessage("§cUnknown Stand: §e" + standName);
            sender.sendMessage("§aYou can see the all stands registered wia: §e/sphelp stands(Maybe try writing it without space)");
        }
        return true;
    }
}

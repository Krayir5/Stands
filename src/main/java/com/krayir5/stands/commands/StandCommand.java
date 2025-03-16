package com.krayir5.stands.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.utils.GUIS.StandHMenu;
import com.krayir5.stands.utils.GUIS.StandUMenu;

public class StandCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final File standFile;
    private final FileConfiguration stConfig;

    public StandCommand(JavaPlugin plugin, File standFile) {
        this.plugin = plugin;
        this.standFile = standFile;
        this.stConfig = YamlConfiguration.loadConfiguration(standFile);
    }

    private ItemStack createStandItem(String stand) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + stand + "'s item");
            meta.setCustomModelData(1453);
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isStandItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 1453;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        UUID playerID = player.getUniqueId();
        String stand = stConfig.getString("players." + playerID + ".stand");
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /stand <subcommand>");
            return true;
        }
        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
        
            case "help":
                sender.sendMessage("§6Help Menu:");
                sender.sendMessage("§b/stand help - §fShows you all avaliable commands on plugin.");
                sender.sendMessage("§b/stand dictionary - §fShows you all avaliable stands and their power/abilities.");
                sender.sendMessage("§b/stand use - §fGives you your stand item.");
                sender.sendMessage("§b/stand upgrade - §fOpens up a GUI to upgrade your stand's abilities.");
                sender.sendMessage("§b/stand reload - §fReloads the config files.");
                break;
            
            case "dictionary":
                StandHMenu Smenu = new StandHMenu(player);
                Smenu.open();
                break;
            
            case "upgrade":
                StandUMenu Umenu = new StandUMenu(player);
                if (stand == null) {
                    player.sendMessage("§4You need to pick a stand first. You can do it by §n/standpick");
                }else{
                    Umenu.open();
                }
                break;
            
            case "use":
                if (stand == null) {
                    player.sendMessage(ChatColor.RED + "You need to have a stand first! For that you can use /standpick command.");
                    return true;
                }
                ItemStack offHandItem = player.getInventory().getItemInOffHand();
                if (offHandItem.getType() != Material.AIR && !isStandItem(offHandItem)) {
                    player.sendMessage(ChatColor.RED + "Your left hand is full. You need to empty it first!");
                    return true;
                }else if(isStandItem(offHandItem)){
                    player.sendMessage(ChatColor.RED + "You already have a stand item in your left hand.");
                    return true;
                }
                player.getInventory().setItemInOffHand(createStandItem(stand));
                player.sendMessage(ChatColor.GREEN + "The item of the " + ChatColor.BOLD + ChatColor.GOLD + stand + ChatColor.RESET + ChatColor.GREEN +" is now on your left hand. You can right click to use it.");
                break;
            
            case "reload":
                if (!player.isOp() || !player.hasPermission("standp.admin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                    return true;
                }
                plugin.reloadConfig();
                if (standFile.exists()) {
                    @SuppressWarnings("unused")
                    FileConfiguration staConfig = YamlConfiguration.loadConfiguration(standFile);
                    staConfig = YamlConfiguration.loadConfiguration(standFile);
                    player.sendMessage(ChatColor.GREEN + "Plugin reloaded succesfully!");
                }else{
                    player.sendMessage(ChatColor.RED + "Something went wrong! stand.yml file not found!");
                }
                break;
            
            case "ae86":
                player.sendMessage("Wow, a Trueno in this economy?");
                break;
            
            default:
                player.sendMessage(ChatColor.RED + "Unknown subcommand: " + subCommand);
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "dictionary", "use", "upgrade", "reload");
            String input = args[0].toLowerCase();
            
            subCommands.stream()
                .filter(sub -> sub.startsWith(input))
                .forEach(suggestions::add);
        }
        
        return suggestions;
    }
}


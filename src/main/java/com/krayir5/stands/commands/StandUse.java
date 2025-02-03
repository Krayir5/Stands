package com.krayir5.stands.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
@SuppressWarnings("")
public class StandUse implements CommandExecutor {

    private final File standFile;

    public StandUse(File standFile) {
        this.standFile = standFile;
    }

    private boolean isStandItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 1453;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players could use this command!");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        YamlConfiguration standConfig = YamlConfiguration.loadConfiguration(standFile);
        String stand = standConfig.getString("players." + playerId + ".stand");

        if (stand == null) {
            player.sendMessage(ChatColor.RED + "You need to have a stand first! For that you can use /standpick command.");
            return true;
        }
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (offHandItem != null && offHandItem.getType() != Material.AIR && !isStandItem(offHandItem)) {
            player.sendMessage(ChatColor.RED + "Your left hand is full. You need to empty it first!");
            return true;
        }else if(isStandItem(offHandItem)){
            player.sendMessage(ChatColor.RED + "You already have a stand item in your left hand.");
            return true;
        }

        player.getInventory().setItemInOffHand(createStandItem(stand));
        player.sendMessage(ChatColor.GREEN + "The item of the " + ChatColor.BOLD + ChatColor.GOLD + stand + ChatColor.RESET + ChatColor.GREEN +" is now on your left hand. You can right click to use it.");
        return true;
    }
}

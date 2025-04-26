package com.krayir5.stands.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.utils.GUIS.StandHMenu;
import com.krayir5.stands.utils.GUIS.StandUMenu;
@SuppressWarnings("")
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

    private ItemStack cPPI(){
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Paisley Park's item");
            meta.setCustomModelData(1453);
            item.setItemMeta(meta);
        }
        return item;
    }

    private String deleteStand(UUID playerID){
        String stand = stConfig.getString("players." + playerID + ".stand");
        if (stand != null) {
            String playerPath = "players." + playerID;
            stConfig.set(playerPath, null);
            try {
                stConfig.save(standFile);
            } catch (IOException e) {
                Bukkit.getLogger().severe(String.format("stands.yml couldn't be saved: %s", e.getMessage()));
            }
        }
        return stand;
    }

    public ItemStack createLocacaca(){
        ItemStack fruit = new ItemStack(Material.CARROT);
        ItemMeta meta = fruit.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Locacaca Fruit");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "A mysterious fruit that grants");
        lore.add(ChatColor.GRAY + "power... at a cost.");
        meta.setLore(lore);
        meta.setCustomModelData(1337);
        NamespacedKey key = new NamespacedKey(plugin, "locacaca");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "true");
        fruit.setItemMeta(meta);
        return fruit;
    }

    public ItemStack createNLocacaca(){
        ItemStack fruit = new ItemStack(Material.CARROT);
        ItemMeta meta = fruit.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "New Locacaca Fruit");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "This new feel from this");
        lore.add(ChatColor.GRAY + "plant. It feels... different.");
        meta.setLore(lore);
        meta.setCustomModelData(1337);
        NamespacedKey key = new NamespacedKey(plugin, "locacaca");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "new");
        fruit.setItemMeta(meta);
        return fruit;
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
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }
        Player player = (Player) sender;
        UUID playerID = player.getUniqueId();
        try {
            stConfig.load(standFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            player.sendMessage(ChatColor.RED + "Failed to load stand data.");
            Bukkit.getLogger().severe(String.format("stands.yml couldn't be loaded: %s", e.getMessage()));
        }
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
                sender.sendMessage("§b/stand delete <user> - §fDeletes the user's stand.");
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
                    player.sendMessage(ChatColor.RED + "You need to have a stand first! For that you need to get a Stand Arrow and right click it.");
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
                switch(stand){
                    case "Paisley Park":
                        player.getInventory().setItemInOffHand(cPPI());
                        break;
                    default:
                        player.getInventory().setItemInOffHand(createStandItem(stand));
                        break;
                }
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

            case "locacaca":
                if (!player.hasPermission("stand.admin") || !player.isOp()) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                player.getInventory().addItem(createLocacaca());
                break;

            case "nlocacaca":
                if (!player.hasPermission("stand.admin") || !player.isOp()) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                player.getInventory().addItem(createNLocacaca());
                break;
            
            case "delete":
                if (!player.hasPermission("stand.admin") || !player.isOp()) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to delete other people's stands.");
                    return true;
                }
                if (args.length == 1){
                    if (stand == null){
                        player.sendMessage(ChatColor.RED + "You don't have a stand to delete!");
                        return true;
                    }
                    deleteStand(playerID);
                    player.sendMessage(ChatColor.GREEN + "Your stand has been deleted successfully.");
                    return true;
                }else if (args.length == 2){
                    String targetName = args[1];
                    OfflinePlayer target;
                    UUID targetUUID = null;

                    Player online = Bukkit.getPlayerExact(targetName);
                    if (online != null) {
                        target = online;
                        targetUUID = online.getUniqueId();
                    } else {
                        target = Arrays.stream(Bukkit.getOfflinePlayers())
                                .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(targetName))
                                .findFirst()
                                .orElse(null);
                    
                        if (target != null) {
                            targetUUID = target.getUniqueId();
                        }
                    }
                    
                    if (targetUUID == null) {
                        player.sendMessage(ChatColor.RED + "Player '" + targetName + "' not found.");
                        return true;
                    }
                    if(!stConfig.contains("players." + targetUUID + ".stand")){
                        player.sendMessage(ChatColor.RED + targetName + " doesn't have a stand to delete.");
                        return true;
                    }
                    deleteStand(targetUUID);
                    player.sendMessage(ChatColor.GREEN + "Deleted stand for player: " + ChatColor.RED + targetName);
                    return true;
                }
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
            List<String> subCommands = Arrays.asList("help", "dictionary", "use", "upgrade", "reload", "locacaca", "delete");
            String input = args[0].toLowerCase();
            
            subCommands.stream()
                .filter(sub -> sub.startsWith(input))
                .forEach(suggestions::add);
        }
        
        return suggestions;
    }
}
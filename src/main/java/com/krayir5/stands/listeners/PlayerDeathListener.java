package com.krayir5.stands.listeners;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerDeathListener implements Listener {
    @SuppressWarnings("unused")
    private final File standFile;
    private final FileConfiguration standsConfig;

    public PlayerDeathListener(File standFile) {
        this.standFile = standFile;
        this.standsConfig = YamlConfiguration.loadConfiguration(standFile);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Entity killer = victim.getKiller();
        removeStandItemsFromDrops(event);
        if (killer instanceof Player) {
            Player killerPlayer = (Player) killer;
            ItemStack offHandItem = killerPlayer.getInventory().getItemInOffHand();
            if (isStandItem(offHandItem)) {
                String standName = standsConfig.getString(killerPlayer.getUniqueId().toString());
                if (standName != null) {
                    event.setDeathMessage(victim.getName() +" was slained with " + standName + " by "+ killerPlayer.getName());
                }
            }
        }
    }

    private boolean isStandItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 1453;
    }

    private void removeStandItemsFromDrops(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        Iterator<ItemStack> iterator = drops.iterator();

        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            if (isStandItem(item)) {
                iterator.remove();
            }
        }
    }
}

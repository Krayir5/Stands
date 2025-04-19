package com.krayir5.stands.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class StandItem implements Listener {

    private boolean isStandItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR && item.getType() != Material.COMPASS){
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 1453;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (isStandItem(event.getCursor()) && event.getSlot() != 40) {
            event.setCancelled(true);
            player.setItemOnCursor(null);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You can't put the stand item other than your second hand!"));
        }
        if (isStandItem(event.getCurrentItem()) && event.getSlot() == 4) {
            event.setCancelled(true);
            player.getInventory().setItem(4, null);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You can't put the stand item other than your second hand!"));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack draggedItem = event.getOldCursor();
        Player player = (Player) event.getWhoClicked();
        if (isStandItem(draggedItem)) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot != 40)) {
                event.setCancelled(true);
                player.setItemOnCursor(null);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You can't put the stand item other than your second hand!"));
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (isStandItem(item)) {
            event.getItemDrop().remove();
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Stand Disabled!"));
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack cursorItem = player.getItemOnCursor();

        if (isStandItem(cursorItem)) {
            player.setItemOnCursor(null);
            ItemStack offHandItem = player.getInventory().getItem(40);
            if (offHandItem == null || offHandItem.getType() == Material.AIR) {
                player.getInventory().setItem(40, cursorItem);
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You can't put the stand item other than your second hand!"));
            }
        }
    }
}

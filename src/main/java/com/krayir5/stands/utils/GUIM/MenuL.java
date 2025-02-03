package com.krayir5.stands.utils.GUIM;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.Plugin;

public class MenuL implements Listener {

    private final JavaPlugin plugin = Plugin.inst();
    public static final String METADATA_ID = "PSRK2E";

    /*
     * Cancel click if button is unmodifiable
     */
    @EventHandler
    public void onGUIClick(InventoryClickEvent e) {
       Player p = (Player) e.getWhoClicked();
       if(p.hasMetadata(METADATA_ID)) {
           Menu menu = (Menu) p.getMetadata(METADATA_ID).get(0).value();
           if(menu == null) {
               e.setCancelled(false);
               return;
           }

           Button button = menu.getButton(e.getSlot());
           if(button != null) {
               e.setCancelled(!button.isModifiable());
               button.onClick(p, e.getClick());
           }else {
               e.setCancelled(!menu.isEmptySlotsModifiable());
           }
       }
    }

    /*
     * Cancel click anyway if it is drag event
     */
    @EventHandler
    public void onGUIDrag(InventoryDragEvent e){
        Player p = (Player) e.getWhoClicked();
        if (p.hasMetadata(METADATA_ID)) {
            Menu menu = (Menu) p.getMetadata(METADATA_ID).get(0).value();
            if(menu != null) {
                e.setCancelled(true);
            }
        }
    }

    /*
     * Remove metadata on inventory close event
     */
    @EventHandler
    public void onGUIClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if (p.hasMetadata(METADATA_ID)) {
            Menu menu = (Menu) p.getMetadata(METADATA_ID).get(0).value();
            if(menu != null) {
                 menu.handleCloseCallback();
            }
            p.removeMetadata(METADATA_ID, plugin);
        }
    }

    /*
     * Remove metadata on quit event
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (p.hasMetadata(METADATA_ID)) {
            Menu menu = (Menu) p.getMetadata(METADATA_ID).get(0).value();
            if(menu != null) {
                menu.handleCloseCallback();
            }
            p.removeMetadata(METADATA_ID, plugin);
        }
    }
}
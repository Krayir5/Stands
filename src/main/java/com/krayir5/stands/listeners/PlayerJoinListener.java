package com.krayir5.stands.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerJoinListener implements Listener {

    private final String currentVersion;
    private final String latestVersion;

    public PlayerJoinListener(String currentVersion, String latestVersion) {
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ChatColor.GRAY + "A stand user " + ChatColor.GOLD + player.getName() + ChatColor.GRAY +" joined the server!");
        if (player.isOp() && latestVersion != null && !currentVersion.equals(latestVersion)) {
            TextComponent msg = new TextComponent(ChatColor.GOLD + "A new version for the Stands plugin is available: " + latestVersion + ". ");
            TextComponent cH = new TextComponent(ChatColor.UNDERLINE + "Click here to download.");
            cH.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            cH.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/stands-from-jjba.121947/"));
            msg.addExtra(cH);
            player.spigot().sendMessage(msg);
        }
    }
}

package com.krayir5.stands.listeners;

import java.io.File;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerJoinListener implements Listener {

    private final String currentVersion;
    private final String latestVersion;
    private final File standFile;

    public PlayerJoinListener(String currentVersion, String latestVersion, File standFile) {
        this.standFile = standFile;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        YamlConfiguration standConfig = YamlConfiguration.loadConfiguration(standFile);
        String stand = standConfig.getString("players." + playerId + ".stand");
        if(stand != null){
            event.setJoinMessage(ChatColor.GOLD + stand + ChatColor.GRAY + "'s user " + ChatColor.GOLD + player.getName() + ChatColor.GRAY +" joined the server!");
        }
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

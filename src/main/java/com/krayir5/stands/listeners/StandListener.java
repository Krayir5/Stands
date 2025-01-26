package com.krayir5.stands.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.krayir5.stands.Plugin;
import com.krayir5.stands.utils.Cooldown;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
@SuppressWarnings("")
public class StandListener implements Listener {

    private final Plugin plugin;
    private final File standFile;
    private final Cooldown cooldown = new Cooldown();
    private final Map<UUID, Integer> bulletsMap = new HashMap<>();
    private final Set<UUID> rP = new HashSet<>();

    public StandListener(Plugin plugin, File standFile) {
        this.plugin = plugin;
        this.standFile = standFile;
    }

    private boolean isStandItem(ItemStack item) {
        if (item == null || item.getType() != Material.NETHER_STAR) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == 1453;
    }

    public String getPlayerStand(Player player) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
        return stConfig.getString(playerUUID.toString());
    }

public void starPlatinumP(Player player, Entity entity){
        FileConfiguration config = plugin.getConfig();
        UUID playerID = player.getUniqueId();
        LivingEntity target = (LivingEntity) entity;
        double damage = config.getDouble("StarPlatinum.damage", 1.0);
        int hits = config.getInt("StarPlatinum.hits", 5);
        double knockbackStrength = config.getDouble("StarPlatinum.knockback", 0.3);
        int cooldownTime = config.getInt("StarPlatinum.punch_cooldown", 5) * 1000;
       if (cooldown.isOnCooldown("StarPlatinumP", playerID)) {
            long remainingTime = cooldown.getRemainingTime("StarPlatinumP", playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
        cooldown.setCooldown("StarPlatinumP", playerID, cooldownTime);
        for (int i = 0; i < hits; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (target.isDead()) return;
                target.damage(damage, player);
                Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                target.setVelocity(direction.multiply(knockbackStrength));
            }, i * 5L);
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!"));
}

public void starPlatinumTS(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int duration = config.getInt("StarPlatinum.time_duration", 5) * 20;
    int cooldownTime = config.getInt("StarPlatinum.cooldown", 240) * 1000;
    
    if (cooldown.isOnCooldown("StarPlatinumTS", playerID)) {
        long remainingTime = cooldown.getRemainingTime("StarPlatinumTS", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time Stop is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
        }
        cooldown.setCooldown("StarPlatinumTS", playerID, cooldownTime);
        Set<Player> affectedPlayers = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!p.equals(player)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 128, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, 128, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, 128, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 128, false, false));
                p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 128, false, false));
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Star Platinum: " + ChatColor.AQUA + player.getName() + " stopped the time!"));
                affectedPlayers.add(p);
                }
        });
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, false));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Star Platinum!" + ChatColor.BOLD + " ZA WARUDO!!!"));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 1, false, false));
        player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation().add(0.1, 1.5, 0.1), 20, 0.3, 0.3, 0.3, 0.02);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.hidePlayer(plugin, player);
            }
        }
        Listener moveListener = new Listener() {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent moveEvent) {
            Player movingPlayer = moveEvent.getPlayer();
            if (affectedPlayers.contains(movingPlayer)) {
                if (!moveEvent.getFrom().toVector().equals(moveEvent.getTo().toVector())) {
                    moveEvent.setCancelled(true);
                    }
                }
            }
        };
        Bukkit.getPluginManager().registerEvents(moveListener, plugin);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(p -> {p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Star Platinum: " + ChatColor.RED + "Time will resume now!"));});
            Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, true));
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(player)) {
                    onlinePlayer.showPlayer(plugin, player);
                }
            }
            PlayerMoveEvent.getHandlerList().unregister(moveListener);
        }, duration);
}

public void starPlatinumSF(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownTime = config.getInt("StarPlatinum.sf_cooldown", 30) * 1000;
    double range = config.getDouble("StarPlatinum.sf_range", 10.0);
        if (cooldown.isOnCooldown("StarPlatinumF", playerID)) {
            long remainingTime = cooldown.getRemainingTime("StarPlatinumF", playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Star Finger is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
        cooldown.setCooldown("StarPlatinumF", playerID, cooldownTime);

        Snowball sF70H = player.launchProjectile(Snowball.class);
        sF70H.setVelocity(player.getLocation().getDirection().multiply(1.5));
        sF70H.setGravity(false);
        sF70H.setSilent(true);
        sF70H.setCustomNameVisible(false);
        sF70H.setMetadata("StarFinger", new FixedMetadataValue(plugin, true));

        Location startLocation = sF70H.getLocation();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!sF70H.isDead()) {
                    if (startLocation.distance(sF70H.getLocation()) > range) {
                    sF70H.remove();
                    this.cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
}

public void theWorldP(Player player, Entity entity){
    LivingEntity target = (LivingEntity) entity;
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    double damage = config.getDouble("TheWorld.damage", 1.0);
    int hits = config.getInt("TheWorld.hits", 5);
    double knockbackStrength = config.getDouble("TheWorld.knockback", 0.3);
    int cooldownTime = config.getInt("TheWorld.cooldown_punch", 5) * 1000;
    if (cooldown.isOnCooldown("TheWorldP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("TheWorldP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("TheWorldP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
        }, i * 5L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + player.getName() + "'s Stand: " + ChatColor.BOLD + "MUDA MUDA MUDA MUDA!"));
}

public void theWorldTS(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int duration = config.getInt("TheWorld.time_duration", 5) * 20;
    int cooldownTime = config.getInt("TheWorld.cooldown", 240) * 1000;

    if (cooldown.isOnCooldown("TheWorldTS", playerID)) {
        long remainingTime = cooldown.getRemainingTime("TheWorldTS", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time Stop is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("TheWorldTS", playerID, cooldownTime);
    Set<Player> affectedPlayers = new HashSet<>();
    Bukkit.getOnlinePlayers().forEach(p -> {
        if (!p.equals(player)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 128, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, 128, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, 128, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 128, false, false));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 128, false, false));
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "The World: " + ChatColor.AQUA + player.getName() + " stopped the time!"));
            affectedPlayers.add(p);
        }
    });
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, false));
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "ZA WARUDO!" + ChatColor.RED + ChatColor.BOLD + " TOKI WO TOMARE!!!"));
    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 1, false, false));
        player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation().add(0.1, 1.5, 0.1), 20, 0.3, 0.3, 0.3, 0.02);
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.equals(player)) {
                onlinePlayer.hidePlayer(plugin, player);
        }
    }
    Listener moveListener = new Listener() {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent moveEvent) {
            Player movingPlayer = moveEvent.getPlayer();
            if (affectedPlayers.contains(movingPlayer)) {
                if (!moveEvent.getFrom().toVector().equals(moveEvent.getTo().toVector())) {
                    moveEvent.setCancelled(true);
                    }
            }
        }
    };
    Bukkit.getPluginManager().registerEvents(moveListener, plugin);
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        Bukkit.getOnlinePlayers().forEach(p -> {p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "The World: " + ChatColor.RED + "Time will resume now!"));});
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, true));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.showPlayer(plugin, player);
            }
        }
        PlayerMoveEvent.getHandlerList().unregister(moveListener);
    }, duration);
}

public void theWorldKT(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cT = config.getInt("TheWorld.k_cooldown", 1) * 1000;
    int kC = config.getInt("TheWorld.knifeCount", 3);
        if (cooldown.isOnCooldown("TheWorldK", playerID)) {
            long remainingTime = cooldown.getRemainingTime("TheWorldK", playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Knife Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
        cooldown.setCooldown("TheWorldK", playerID, cT);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "The World: " + ChatColor.RED + "WRRRRYYYYYY!"));
        for (int i = 0; i < kC; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Snowball kCProjectile = player.launchProjectile(Snowball.class);
                kCProjectile.setVelocity(player.getLocation().getDirection().multiply(2));
                kCProjectile.setShooter(player);
                kCProjectile.setMetadata("THKnifes", new FixedMetadataValue(plugin, true));
            }, i * 5L);
        }
}

public void crazyDiamondP(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownP = config.getInt("CrazyDiamond.punch_cooldown", 5) * 1000;
    int hits = config.getInt("CrazyDiamond.hits", 4);
    double knockbackStrength = config.getDouble("CrazyDiamond.knockback", 0.5);
    double damage = config.getDouble("CrazyDiamond.damage", 5.0);

        if (entity instanceof LivingEntity) {
            if (cooldown.isOnCooldown("CrazyDiamondP", playerID)) {
                long remainingTime = cooldown.getRemainingTime("CrazyDiamondP", playerID);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("CrazyDiamondP", playerID, cooldownP);
            LivingEntity target = (LivingEntity) entity;
            for (int i = 0; i < hits; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (target.isDead()) return;
                    target.damage(damage, player);
                    Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    target.setVelocity(direction.multiply(knockbackStrength));
                }, i * 5L);
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "DORARARARARA!"));
        }
}

public void crazyDiamondHeal(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int duration = config.getInt("CrazyDiamond.time_duration", 10) * 20;
    int amplifier = config.getInt("CrazyDiamond.amplifier", 0);
    int cooldownH = config.getInt("CrazyDiamond.heal_cooldown", 30) * 1000;
        if (entity instanceof LivingEntity) {
            if (cooldown.isOnCooldown("CrazyDiamondH", playerID)) {
                long remainingTime = cooldown.getRemainingTime("CrazyDiamondH", playerID);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("CrazyDiamondH", playerID, cooldownH);
            LivingEntity target = (LivingEntity) entity;
            target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            String msg = ChatColor.LIGHT_PURPLE + "Crazy Diamond: Healed " + target.getName() + "!";
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        }
}

public void crazyDiamondSH(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownTime = config.getInt("CrazyDiamond.cooldown", 30) * 1000;
    int duration = config.getInt("CrazyDiamond.time_duration", 5) * 20;
    int amplifier = config.getInt("CrazyDiamond.amplifier", 0);
    if (cooldown.isOnCooldown("CrazyDiamond", playerID)) {
            long remainingTime = cooldown.getRemainingTime("CrazyDiamond", playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
    cooldown.setCooldown("CrazyDiamond", playerID, cooldownTime);
    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Crazy Diamond: You healed yourself!"));
}

public void magicianRedP(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    LivingEntity target = (LivingEntity) entity;
    double damage = config.getDouble("MagicianRed.damage", 1.0);
    int hits = config.getInt("MagicianRed.hits", 5);
    double knockbackStrength = config.getDouble("MagicianRed.knockback", 0.3);
    int cooldownTime = config.getInt("MagicianRed.cooldown_punch", 5) * 1000;
    if (cooldown.isOnCooldown("MagicianRedP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("MagicianRedP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("MagicianRedP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
        }, i * 10L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + player.getName() + "'s Stand: " + ChatColor.BOLD + "CACOWW!"));
}

public void magicianRedCFH(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    double speedMultiplier = config.getDouble("MagicianRed.fireball_speed", 2.0);
    double damage = config.getDouble("MagicianRed.fireball_damage", 3.0);
    int fireballCount = config.getInt("MagicianRed.fireball_count", 3);
    int cooldownTime = config.getInt("MagicianRed.cooldown", 30) * 1000;
    boolean mobGrief = config.getBoolean("MagicianRed.mob_grief", true);
    if (cooldown.isOnCooldown("MagicianRedCFH", playerID)) {
        long remainingTime = cooldown.getRemainingTime("MagicianRedCFH", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Cross Fire Hurricane is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("MagicianRedCFH", playerID, cooldownTime);
    String msg = ChatColor.RED + "Magician Red: " + ChatColor.BOLD + "Cross Fire Hurricane!";
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    if (mobGrief == false) {
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, false));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, true));
    }, fireballCount * 15L);
    }
    for (int i = 0; i < fireballCount; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Fireball fireball = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Fireball.class);
            fireball.setShooter(player);
            fireball.setDirection(player.getLocation().getDirection().normalize().multiply(speedMultiplier));
            fireball.setYield((float) damage);
            fireball.setIsIncendiary(true);
        }, i * 5L);
    }
}

public void hermitPurple(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int vineLength = config.getInt("HermitPurple.vine_length", 4);
    double pullStrength = config.getDouble("HermitPurple.pull_strength", 1.3);
    double damage = config.getDouble("HermitPurple.damage", 3.0);
    int cooldownTime = config.getInt("HermitPurple.cooldown", 20) * 1000;
    if (cooldown.isOnCooldown("HermitPurple", playerID)) {
        long remainingTime = cooldown.getRemainingTime("HermitPurple", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Hermit Purple is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("HermitPurple", playerID, cooldownTime);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Overdrive!"));

    Vector direction = player.getLocation().getDirection().normalize();
    for (int i = 1; i <= vineLength; i++) {
        final int distance = i;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Vector targetLocation = player.getLocation().toVector().add(direction.clone().multiply(distance));
            targetLocation.setY(player.getEyeLocation().getY());
            org.bukkit.Location blockLocation = targetLocation.toLocation(player.getWorld());
            blockLocation.getBlock().setType(Material.VINE);
            Bukkit.getScheduler().runTaskLater(plugin, () -> blockLocation.getBlock().setType(Material.AIR), 20L);
            for (Entity entity : player.getWorld().getNearbyEntities(blockLocation, 1, 1, 1)) {
                if (entity instanceof Player && entity != player) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> blockLocation.getBlock().setType(Material.AIR), 2L);
                    Player target = (Player) entity;
                    Vector pullDirection = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
                    target.setVelocity(pullDirection.multiply(pullStrength));
                    target.damage(damage, player);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Cought " +target.getName()+ "!"));
                    target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "GOT YOU!"));
                }else if(entity instanceof LivingEntity){
                    LivingEntity target = (LivingEntity) entity;
                    Vector pullDirection = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
                    target.setVelocity(pullDirection.multiply(pullStrength));
                    target.damage(damage, player);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Cought " +target.getName()+ "!"));
                }else{
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "OH MY GOD!!!"));
                }
            }
        }, i * 5L);
    }
}

public void killerQueenP(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    LivingEntity target = (LivingEntity) entity;
    double damage = config.getDouble("KillerQueen.damage", 1.0);
    int hits = config.getInt("KillerQueen.hits", 5);
    double knockbackStrength = config.getDouble("KillerQueen.knockback", 0.3);
    int cooldownTime = config.getInt("KillerQueen.cooldown_punch", 5) * 1000;
    if (cooldown.isOnCooldown("KillerQueenP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("KillerQueenP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("KillerQueenP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
        }, i * 10L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "SHIBABABA!"));
}

public void killerQueenABT(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownTime = config.getInt("KillerQueen.abt_cooldown", 2) * 1000;
    double damage = config.getDouble("KillerQueen.abt_damage", 3.0);
    double explosionRadius = config.getDouble("KillerQueen.abt_explosionRadius", 3.0);
    boolean grief = config.getBoolean("KillerQueen.abt_causeExplosion", false);
    if (cooldown.isOnCooldown("KillerQueenABT", playerID)) {
        long remainingTime = cooldown.getRemainingTime("KillerQueenABT", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Air Bomb Transmutation is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("KillerQueenABT", playerID, cooldownTime);
    Snowball airBomb = player.launchProjectile(Snowball.class);
    airBomb.setVelocity(player.getLocation().getDirection().multiply(2));
    airBomb.setShooter(player);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Killer Queen: " + ChatColor.BOLD + "Air Bomb Transmutation!"));
    new BukkitRunnable() {
        @Override
        public void run() {
            if (airBomb.isDead() || airBomb.isOnGround()) {
                airBomb.getWorld().createExplosion(airBomb.getLocation(), (float) explosionRadius, false, grief);
                airBomb.getWorld().getNearbyEntities(airBomb.getLocation(), explosionRadius, explosionRadius, explosionRadius)
                    .forEach(entity -> {
                        if (entity instanceof LivingEntity && entity != player) {
                            LivingEntity target = (LivingEntity) entity;
                            target.damage(damage, player);
                        }
                    });
                this.cancel();
            }
        }
    }.runTaskTimer(plugin, 0L, 1L);
}

public void killerQueenBTD(Player player, Block block){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    float ePower = (float) plugin.getConfig().getDouble("KillerQueen.explosion_power", 0.0);
    int eDelay = config.getInt("KillerQueen.explosion_delay", 5);
    int cooldownTime = config.getInt("KillerQueen.cooldown", 120) * 1000;
    if (cooldown.isOnCooldown("KillerQueen", playerID)) {
        long remainingTime = cooldown.getRemainingTime("KillerQueen", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Another one bites the Dust is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }

    if (block == null || block.getType() == Material.BEDROCK || block.getType() == Material.COMMAND_BLOCK || block.getType() == Material.BARRIER) return;
        cooldown.setCooldown("KillerQueen", playerID, cooldownTime);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Killer Queen, " + ChatColor.RED + ChatColor.BOLD + "Another one bites the DUST!"));
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
        block.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.02);
    }, 0L, 10L);

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        Bukkit.getScheduler().cancelTask(taskId);
        block.getWorld().createExplosion(block.getLocation(), ePower, false, true, player);
    }, eDelay * 20);
}

public void novemberRain(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int radius = config.getInt("NovemberRain.radius", 10);
    int duration = config.getInt("NovemberRain.duration", 5) * 20;
    double damage = config.getDouble("NovemberRain.damage", 2.0);

    int cooldownTime = config.getInt("NovemberRain.cooldown", 600) * 1000;
    if (cooldown.isOnCooldown("NovemberRain", playerID)) {
        long remainingTime = cooldown.getRemainingTime("NovemberRain", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "November Rain is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("NovemberRain", playerID, cooldownTime);

    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_BLUE + "November Rain, " + ChatColor.BLUE + ChatColor.BOLD + "Heavy Rain!"));
    new BukkitRunnable() {
        int ticksElapsed = 0;
        @Override
        public void run() {
            if (ticksElapsed >= duration) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_BLUE + "November Rain, " + ChatColor.BLUE + ChatColor.BOLD + "Heavy Rain ended!"));
                cancel();
                return;
            }
            player.getWorld().spawnParticle(Particle.DRIP_WATER, player.getLocation(), 2000, radius, radius, radius, 0.1);
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entity;
                    target.damage(damage, player);
                }
            }
            ticksElapsed += 20;
        }
    }.runTaskTimer(plugin, 0L, 20L);
}

public void silverChariotHR(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    LivingEntity target = (LivingEntity) entity;
    double damage = config.getDouble("SilverChariot.damage", 1.0);
    int hits = config.getInt("SilverChariot.hits", 5);
    double knockbackStrength = config.getDouble("SilverChariot.knockback", 0.2);    
    int cooldownTime = config.getInt("SilverChariot.cooldown", 5) * 1000;

    if (cooldown.isOnCooldown("SilverChariot", playerID)) {
        long remainingTime = cooldown.getRemainingTime("SilverChariot", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Hora Rush is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("SilverChariot", playerID, cooldownTime);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY +"Silver Chariot: "+ ChatColor.BOLD +"Hora Rush!"));
        for (int i = 0; i < hits; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (target.isDead()) return;
                target.damage(damage, player);
                Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                target.setVelocity(direction.multiply(knockbackStrength));
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0.5, 1, 0.5), 8, 0.3, 0.3, 0.3, 0.02);
                target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0.5, 1, 0.5), 15, 0.3, 0.3, 0.3, 0.02);
           }, i * 5L);
        }
}

public void tuskA2(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownTime = config.getInt("Tusk.cooldown", 15) * 1000;
    if (cooldown.isOnCooldown("Tusk", playerID)) {
        long remainingTime = cooldown.getRemainingTime("Tusk", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Tusk is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("Tusk", playerID, cooldownTime);
        Snowball johnnysNail = player.launchProjectile(Snowball.class);
        johnnysNail.setVelocity(player.getLocation().getDirection().multiply(1.5));
        johnnysNail.setGravity(false);
        johnnysNail.setShooter(player);
        johnnysNail.setMetadata("Tusk", new FixedMetadataValue(plugin, true));

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Tusk Act 2: " + ChatColor.BOLD + "CHUMIMI~IN!"));
}

public void hierophantGreenP(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    LivingEntity target = (LivingEntity) entity;
    double damage = config.getDouble("HierophantGreen.p_damage", 0.5);
    int hits = config.getInt("HierophantGreen.hits", 2);
    double knockbackStrength = config.getDouble("HierophantGreen.knockback", 0.3);
    int cooldownTime = config.getInt("HierophantGreen.cooldown_punch", 5) * 1000;
    if (cooldown.isOnCooldown("HierophantGreenP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("HierophantGreenP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("HierophantGreenP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
        }, i * 10L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + player.getName() + "'s Stand: " + ChatColor.BOLD + "Hierophant Green!"));
}

public void hierophantGreenES(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownTime = config.getInt("HierophantGreen.cooldown", 30) * 1000;
    int hgCount = config.getInt("HierophantGreen.ammo_count", 3);

    if (cooldown.isOnCooldown("HierophantGreenES", playerID)) {
        long remainingTime = cooldown.getRemainingTime("HierophantGreenES", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Emerald Splash is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " seconds."));
        return;
    }
    cooldown.setCooldown("HierophantGreenES", playerID, cooldownTime);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Hierophant Green: " + ChatColor.BOLD + "Emerald Splash!"));
    for (int i = 0; i < hgCount; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Snowball hgProjectile = player.launchProjectile(Snowball.class);
            hgProjectile.setVelocity(player.getLocation().getDirection().multiply(2));
            hgProjectile.setShooter(player);
            hgProjectile.setGravity(false);
            hgProjectile.setMetadata("EmeraldSplash", new FixedMetadataValue(plugin, true));
        }, i * 5L);
    }
}

public void heavensDoor(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    double damage = config.getDouble("HeavensDoor.damage", 1.0);
    int duration = config.getInt("HeavensDoor.duration", 30) * 20;
    int cooldownTime = config.getInt("HeavensDoor.cooldown", 120) * 1000;
    if(!(entity instanceof Player)){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Heaven's Door can be only used on players!"));
        return;
    }
    Player target = (Player) entity;
    if (cooldown.isOnCooldown("HeavensDoor", playerID)) {
        long remainingTime = cooldown.getRemainingTime("HeavensDoor", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Heaven's Door is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("HeavensDoor", playerID, cooldownTime);
    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false));
    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 128, false, false));
    
    target.damage(damage, player);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "Heaven's Door: " + ChatColor.BOLD + "Book Transmutation!"));
    Listener moveListener = new Listener() {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent moveEvent) {
            Player movingPlayer = moveEvent.getPlayer();
            if (movingPlayer.equals(target)) {
                if (moveEvent.getTo() != null && !moveEvent.getFrom().toVector().equals(moveEvent.getTo().toVector())) {
                    moveEvent.setCancelled(true);
                }
            }
        }
    };
    Bukkit.getPluginManager().registerEvents(moveListener, plugin);
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        PlayerMoveEvent.getHandlerList().unregister(moveListener);
        target.removePotionEffect(PotionEffectType.WEAKNESS);
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1, false, false));
    }, 40L);
}

public void sexyPistols(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cT = config.getInt("SexyPistols.cooldown", 10);
    int bulletsAvailable = bulletsMap.getOrDefault(playerID, 7);

    if (cooldown.isOnCooldown("SexyPistols", playerID)) {
        long remainingTime = cooldown.getRemainingTime("SexyPistols", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Sexy Pistols is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }

    if (bulletsAvailable > 0) {
        cooldown.setCooldown("SexyPistols", playerID, 3000);
        bulletsAvailable--;
        bulletsMap.put(playerID, bulletsAvailable);
        int bulletNumber = 7 - bulletsAvailable;

        Snowball bullet = player.launchProjectile(Snowball.class);
        bullet.setVelocity(player.getLocation().getDirection().multiply(1.5));
        bullet.setMetadata("SexyPistols", new FixedMetadataValue(plugin, true));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Sexy Pistols: " + ChatColor.GOLD + ChatColor.BOLD + "Bullet " + bulletNumber + " fired!"));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!bullet.isDead()) {
                    LivingEntity nearestTarget = bullet.getWorld().getNearbyEntities(bullet.getLocation(), 5, 5, 5).stream()
                        .filter(e -> e instanceof LivingEntity && e != player)
                        .map(e -> (LivingEntity) e)
                        .min((e1, e2) -> Double.compare(e1.getLocation().distance(bullet.getLocation()), e2.getLocation().distance(bullet.getLocation())))
                        .orElse(null);
                    if (nearestTarget != null) {
                        bullet.setVelocity(nearestTarget.getLocation().toVector().subtract(bullet.getLocation().toVector()).normalize().multiply(1.5));
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    if (rP.contains(playerID)) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Sexy Pistols is reloading! Please wait."));
        return;
    }

    if (bulletsAvailable <= 0 && !rP.contains(playerID)) {
        rP.add(playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Sexy Pistols started reloading! Please wait."));
        new BukkitRunnable() {
            @Override
            public void run() {
                int reloadedBullets = bulletsMap.getOrDefault(playerID, 0) + 1;
                if (reloadedBullets > 7) {
                    bulletsMap.put(playerID, 7);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Sexy Pistols: " + ChatColor.GREEN + ChatColor.BOLD + "Reload complete! (7/7)"));
                    this.cancel();
                    rP.remove(playerID);
                } else {
                    bulletsMap.put(playerID, reloadedBullets);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Sexy Pistols: " + ChatColor.GREEN + ChatColor.BOLD + "Bullet reloaded! (" + reloadedBullets + "/7)"));
                }
            }
        }.runTaskTimer(plugin, cT * 20L, cT * 20L);
    }
}

public void softAndWetP(Player player, Entity entity){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    LivingEntity target = (LivingEntity) entity;
    double damage = config.getDouble("SoftAndWet.p_damage", 0.5);
    int hits = config.getInt("SoftAndWet.hits", 2);
    double knockbackStrength = config.getDouble("SoftAndWet.knockback", 0.3);
    int cooldownTime = config.getInt("SoftAndWet.p_cooldown", 5) * 1000;
    if (cooldown.isOnCooldown("SoftAndWetP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("SoftAndWetP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("SoftAndWetP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
        }, i * 10L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BLUE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!"));
}

public void softAndWetBG(Player player){
    FileConfiguration config = plugin.getConfig();
    UUID playerID = player.getUniqueId();
    int cooldownTime = config.getInt("SoftAndWet.cooldown", 2) * 1000;

    if (cooldown.isOnCooldown("SoftAndWet", playerID)) {
        long remainingTime = cooldown.getRemainingTime("SoftAndWet", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(ChatColor.RED + "Bubble Generation is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " seconds."));
        return;
    }
    cooldown.setCooldown("SoftAndWet", playerID, cooldownTime);
    Snowball bubble = player.launchProjectile(Snowball.class);
    bubble.setVelocity(player.getLocation().getDirection().multiply(1.5));
    bubble.setGravity(false);
    bubble.setShooter(player);
    bubble.setTicksLived(60);
    bubble.setMetadata("SoftAndWetB", new FixedMetadataValue(plugin, true));

    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Soft & Wet " + ChatColor.BOLD + "Bubble Generation!"));
}

@EventHandler
public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getRightClicked();
    ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
    String stand = getPlayerStand(player);

    if (event.getHand() != EquipmentSlot.OFF_HAND) {
        return;
    }
    if (isStandItem(itemIn2Hand)) {
        if (stand == null) {
            return;
        }
        switch (stand) {
            case "Star Platinum":
                starPlatinumP(player, entity);
                break;

            case "The World":
                theWorldP(player, entity);
                break;

            case "Crazy Diamond":
                if(player.isSneaking()){
                    crazyDiamondHeal(player, entity);
                }else{
                    crazyDiamondP(player, entity);
            }
            break;

            case "Magician Red":
                magicianRedP(player, entity);
                break;

            case "Killer Queen":
                killerQueenP(player, entity);
                break;

            case "Silver Chariot":
                silverChariotHR(player, entity);
                break;

            case "Hierophant Green":
                hierophantGreenP(player, entity);
                break;

            case "Heavens Door":
                heavensDoor(player, entity);
                break;

            case "Soft & Wet":
                softAndWetP(player, entity);
                break;

            default:
                break;
        }
    }
}

@EventHandler
public void onHandSwap(PlayerSwapHandItemsEvent event) {
    Player player = event.getPlayer();
    ItemStack itemIn2Hand = event.getMainHandItem();
    String stand = getPlayerStand(player);

    if (isStandItem(itemIn2Hand)) {
        event.setCancelled(true);
        if (stand == null) {
            return;
        }
        switch (stand) {
            case "Star Platinum":
                starPlatinumTS(player);
                break;
    
            case "The World":
                theWorldTS(player);
                break;
    
            case "Crazy Diamond":
                crazyDiamondSH(player);
                break;
    
            case "Magician Red":
                magicianRedCFH(player);
                break;
    
            case "Hermit Purple":
                hermitPurple(player);
                break;
    
            case "Killer Queen":
                killerQueenABT(player);
                break;
    
            case "November Rain":
                novemberRain(player);
                break;
    
            case "Tusk":
                tuskA2(player);
                break;
    
            case "Hierophant Green":
                hierophantGreenES(player);
                break;
    
            case "Sexy Pistols":
                sexyPistols(player);
                break;
    
            case "Soft & Wet":
                softAndWetBG(player);
                break;
            default:
                break;
        }
    }
}

@EventHandler
public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
    String stand = getPlayerStand(player);
    Block block = event.getClickedBlock();
    if (event.getHand() != EquipmentSlot.OFF_HAND) {
        return;
    }
    if (isStandItem(itemIn2Hand)){
        if (stand == null) {
            return;
        }
        switch (stand) {
            case "Star Platinum":
                   if (player.isSneaking()){
                       starPlatinumSF(player);
                 }
                break;
            case "The World":
                if (player.isSneaking()){
                    theWorldKT(player);
                }
                break;
            case "Killer Queen":
                 killerQueenBTD(player, block);
                break;
            default:
                break;
         }
    }
}

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        FileConfiguration config = plugin.getConfig();
        if (!(event.getEntity() instanceof Snowball))return;
        Snowball standSB = (Snowball) event.getEntity();
        if(!(standSB.getShooter() instanceof Player))return;
        Player player = (Player) standSB.getShooter();

        if(standSB.hasMetadata("SoftAndWetB")){
            double damage = config.getDouble("SoftAndWet.damage", 3.0);
            PotionEffectType[] negativeEffects = {
                PotionEffectType.SLOW,
                PotionEffectType.WEAKNESS,
                PotionEffectType.BLINDNESS,
                PotionEffectType.POISON,
                PotionEffectType.WITHER
            };
    
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                PotionEffectType randomEffect = negativeEffects[new Random().nextInt(negativeEffects.length)];
                target.addPotionEffect(new PotionEffect(randomEffect, 100, 1));
                target.damage(damage, player);
            }
        }

        if(standSB.hasMetadata("SexyPistols")){
            double damage = config.getDouble("SexyPistols.damage", 2.0);
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.damage(damage, player);
            }
        }

        if(standSB.hasMetadata("StarFinger")){
            double damage = config.getDouble("StarPlatinum.sf_damage", 4.0);
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.damage(damage, player);
            }
        }
         
        if(standSB.hasMetadata("THKnifes")){
            double damage = config.getDouble("TheWorld.k_damage", 4.0);
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.damage(damage, player);
            }
        }

        if(standSB.hasMetadata("Tusk")){
            double damage = config.getDouble("Tusk.damage", 3.0);
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.damage(damage, player);
            }
        }

        if(standSB.hasMetadata("EmeraldSplash")){
            double damage = config.getDouble("HierophantGreen.damage", 3.0);
            if (event.getHitEntity() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getHitEntity();
                target.damage(damage, player);
            }
        }
    }
}

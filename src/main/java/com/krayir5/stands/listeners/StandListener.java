package com.krayir5.stands.listeners;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

    public String getPlayerStand(UUID playerID) {
        FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
        return stConfig.getString("players." + playerID + ".stand");
    }

    public int getAbLevel(int a, UUID playerID){
        FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
        return stConfig.getInt("players." + playerID + ".standXP.Ability"+ a +".Level");
    }

    public void rx7FC(int a, int b, UUID playerID){
        FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
        int rt = stConfig.getInt("players." + playerID + ".standXP.Ability"+ a +".XP");
        int kt = stConfig.getInt("players." + playerID + ".standXP.Stand.XP"); 
        stConfig.set("players." + playerID + ".standXP.Ability"+ a +".XP", rt + b);
        stConfig.set("players." + playerID + ".standXP.Stand.XP", kt + b);
        try {
            stConfig.save(standFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    public int sileighty(int a, UUID playerID){
        FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
        if (a <= 3) {
            return stConfig.getInt("players." + playerID + ".standXP.Ability"+ a +".Level");
        }else{
            return stConfig.getInt("players." + playerID + ".standXP.Stand.Level");
        }
    }

public void starPlatinumP(Player player, UUID playerID, Entity entity, FileConfiguration config){
        LivingEntity target = (LivingEntity) entity;
        int level = sileighty(1, playerID);
        double damage = config.getDouble("StarPlatinum.damage", 1.5) * level;
        int hits = config.getInt("StarPlatinum.hits", 4);
        double knockbackStrength = config.getDouble("StarPlatinum.knockback", 0.6);
        int cooldownTime = config.getInt("StarPlatinum.punch_cooldown", 15) * 1000;
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
                rx7FC(1, 1, playerID);
            }, i * 11L);
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!"));
}

public void starPlatinumTS(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(3, playerID);
    int aDuration = config.getInt("StarPlatinum.time_duration", 5) * 20;
    int duration = aDuration * level;
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
        rx7FC(3, 20, playerID);
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

public void starPlatinumSF(Player player, UUID playerID, FileConfiguration config){
    int cooldownTime = config.getInt("StarPlatinum.sf_cooldown", 30) * 1000;
    int level = sileighty(2, playerID);
    double range = config.getDouble("StarPlatinum.sf_range", 10.0) * level;
        if (cooldown.isOnCooldown("StarPlatinumF", playerID)) {
            long remainingTime = cooldown.getRemainingTime("StarPlatinumF", playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Star Finger is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
        cooldown.setCooldown("StarPlatinumF", playerID, cooldownTime);
        rx7FC(2, 10, playerID);

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

public void theWorldP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("TheWorld.damage", 1.5) * level;
    int hits = config.getInt("TheWorld.hits", 4);
    double knockbackStrength = config.getDouble("TheWorld.knockback", 0.6);
    int cooldownTime = config.getInt("TheWorld.cooldown_punch", 15) * 1000;
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
            rx7FC(1, 1, playerID);
        }, i * 11L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + player.getName() + "'s Stand: " + ChatColor.BOLD + "MUDA MUDA MUDA MUDA!"));
}

public void theWorldTS(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(3, playerID);
    int aDuration = config.getInt("TheWorld.time_duration", 5) * 20;
    int duration = aDuration * level;
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
    rx7FC(3, 20, playerID);
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

public void theWorldKT(Player player, UUID playerID, FileConfiguration config){
    int cT = config.getInt("TheWorld.k_cooldown", 1) * 1000;
    int level = sileighty(2, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int kC = config.getInt("TheWorld.knifeCount", 3) + aL;
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
                rx7FC(2, 2, playerID);
            }, i * 5L);
        }
}

public void crazyDiamondP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    int level = sileighty(1, playerID);
    int cooldownP = config.getInt("CrazyDiamond.punch_cooldown", 15) * 1000;
    int hits = config.getInt("CrazyDiamond.hits", 4) * level;
    double knockbackStrength = config.getDouble("CrazyDiamond.knockback", 0.5);
    double damage = config.getDouble("CrazyDiamond.damage", 1.5);

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
                    rx7FC(1, 1, playerID);
                }, i * 11L);
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "DORARARARARA!"));
        }
}

public void crazyDiamondHeal(Player player, UUID playerID, Entity entity, FileConfiguration config){
    int level = sileighty(2, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int duration = config.getInt("CrazyDiamond.time_duration", 10) * 20;
    int amplifier = config.getInt("CrazyDiamond.amplifier", 0) + aL;
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
            rx7FC(2, 10, playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Crazy Diamond: Healed " + target.getName() + "!"));
        }
}

public void crazyDiamondSH(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(3, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int cooldownTime = config.getInt("CrazyDiamond.cooldown", 30) * 1000;
    int duration = config.getInt("CrazyDiamond.time_duration", 5) * 20;
    int amplifier = config.getInt("CrazyDiamond.amplifier", 0) + aL;
    if (cooldown.isOnCooldown("CrazyDiamond", playerID)) {
            long remainingTime = cooldown.getRemainingTime("CrazyDiamond", playerID);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
    cooldown.setCooldown("CrazyDiamond", playerID, cooldownTime);
    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Crazy Diamond: You healed yourself!"));
    rx7FC(3, 20, playerID);
}

public void magicianRedP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("MagicianRed.damage", 1.0) * level;
    int hits = config.getInt("MagicianRed.hits", 5);
    double knockbackStrength = config.getDouble("MagicianRed.knockback", 0.3);
    int cooldownTime = config.getInt("MagicianRed.cooldown_punch", 15) * 1000;
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
            rx7FC(1, 1, playerID);
        }, i * 20L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + player.getName() + "'s Stand: " + ChatColor.BOLD + "CACOWW!"));
}

public void magicianRedCFH(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(2, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    double speedMultiplier = config.getDouble("MagicianRed.fireball_speed", 2.0);
    double damage = config.getDouble("MagicianRed.fireball_damage", 3.0);
    int fireballCount = config.getInt("MagicianRed.fireball_count", 3) + aL;
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
            rx7FC(2, 5, playerID);
        }, i * 5L);
    }
}

public void hermitPurple(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(1, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int vineLength = config.getInt("HermitPurple.vine_length", 4) + aL;
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
    rx7FC(1, 5, playerID);

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

public void killerQueenP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("KillerQueen.damage", 1.5) * level;
    int hits = config.getInt("KillerQueen.hits", 3);
    double knockbackStrength = config.getDouble("KillerQueen.knockback", 0.6);
    int cooldownTime = config.getInt("KillerQueen.cooldown_punch", 15) * 1000;
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
            rx7FC(1, 1, playerID);
        }, i * 20L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "SHIBABABA!"));
}

public void killerQueenABT(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(2, playerID);
    int cooldownTime = config.getInt("KillerQueen.abt_cooldown", 2) * 1000;
    double damage = config.getDouble("KillerQueen.abt_damage", 3.0) * level;
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
    rx7FC(2, 10, playerID);
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

public void killerQueenBTD(Player player, UUID playerID, Block block, FileConfiguration config){
    int level = sileighty(3, playerID);
    float ePower = (float) plugin.getConfig().getDouble("KillerQueen.explosion_power", 0.0) * level;
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
        rx7FC(3, 20, playerID);
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
        block.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.02);
    }, 0L, 10L);

    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        Bukkit.getScheduler().cancelTask(taskId);
        block.getWorld().createExplosion(block.getLocation(), ePower, false, true, player);
    }, eDelay * 20);
}

public void novemberRain(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(1, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int radius = config.getInt("NovemberRain.radius", 6) + aL;
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
    rx7FC(1, 10, playerID);
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

public void silverChariotHR(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("SilverChariot.damage", 0.5) * level;
    int hits = config.getInt("SilverChariot.hits", 4);
    double knockbackStrength = config.getDouble("SilverChariot.knockback", 0.4);    
    int cooldownTime = config.getInt("SilverChariot.cooldown", 15) * 1000;

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
                rx7FC(1, 5, playerID);
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0.5, 1, 0.5), 8, 0.3, 0.3, 0.3, 0.02);
                target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0.5, 1, 0.5), 15, 0.3, 0.3, 0.3, 0.02);
           }, i * 11L);
        }
}

public void tuskA2(Player player, UUID playerID, FileConfiguration config){
    int cooldownTime = config.getInt("Tusk.cooldown", 15) * 1000;
    if (cooldown.isOnCooldown("Tusk", playerID)) {
        long remainingTime = cooldown.getRemainingTime("Tusk", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Golden Rectangle Nails is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("Tusk", playerID, cooldownTime);
        Snowball johnnysNail = player.launchProjectile(Snowball.class);
        johnnysNail.setVelocity(player.getLocation().getDirection().multiply(1.5));
        johnnysNail.setGravity(false);
        johnnysNail.setShooter(player);
        johnnysNail.setMetadata("Tusk", new FixedMetadataValue(plugin, true));

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Tusk Act 2: " + ChatColor.BOLD + "CHUMIMI~IN!"));
        rx7FC(1, 5, playerID);
}

public void tuskA3(Player player, UUID playerID, FileConfiguration config) {
    int level = sileighty(2, playerID);
    int cT = config.getInt("Tusk.tp_cooldown", 60) * 1000;
    int cooldownTime = cT / level;
    if (cooldown.isOnCooldown("TuskA3", playerID)) {
        long remainingTime = cooldown.getRemainingTime("Tusk", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Spatial Wormhole is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("TuskA3", playerID, cooldownTime);
    EnderPearl pearl = player.launchProjectile(EnderPearl.class);
    pearl.setVelocity(player.getLocation().getDirection().multiply(2));
    pearl.setShooter(player);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Tusk Act 3: " + ChatColor.BOLD + "CHUMIMI~IN!"));
    rx7FC(2, 10, playerID);
}

public void hierophantGreenP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("HierophantGreen.p_damage", 0.5) * level;
    int hits = config.getInt("HierophantGreen.hits", 2);
    double knockbackStrength = config.getDouble("HierophantGreen.knockback", 0.3);
    int cooldownTime = config.getInt("HierophantGreen.cooldown_punch", 15) * 1000;
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
            rx7FC(1, 1, playerID);
        }, i * 25L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + player.getName() + "'s Stand: " + ChatColor.BOLD + "Hierophant Green!"));
}

public void hierophantGreenES(Player player, UUID playerID, FileConfiguration config){
    int cooldownTime = config.getInt("HierophantGreen.cooldown", 30) * 1000;
    int level = sileighty(2, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int hgCount = config.getInt("HierophantGreen.ammo_count", 3) + aL;

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
            rx7FC(2, 5, playerID);
        }, i * 5L);
    }
}

public void heavensDoor(Player player, UUID playerID, Entity entity, FileConfiguration config){
    int level = sileighty(1, playerID);
    double damage = config.getDouble("HeavensDoor.damage", 1.0) * level;
    int duration = config.getInt("HeavensDoor.duration", 30) * 20;
    int cooldownTime = config.getInt("HeavensDoor.cooldown", 120) * 1000;
    if(!(entity instanceof Player)){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Book Transmutation can be only used on players!"));
        return;
    }
    Player target = (Player) entity;
    if (cooldown.isOnCooldown("HeavensDoor", playerID)) {
        long remainingTime = cooldown.getRemainingTime("HeavensDoor", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Book Transmutation is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("HeavensDoor", playerID, cooldownTime);
    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false));
    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 128, false, false));
    
    target.damage(damage, player);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "Heaven's Door: " + ChatColor.BOLD + "Book Transmutation!"));
    rx7FC(1, 10, playerID);
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

public void sexyPistols(Player player, UUID playerID, FileConfiguration config){
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
        rx7FC(1, 5, playerID);
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

public void softAndWetP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("SoftAndWet.p_damage", 0.5) * level;
    int hits = config.getInt("SoftAndWet.hits", 2);
    double knockbackStrength = config.getDouble("SoftAndWet.knockback", 0.3);
    int cooldownTime = config.getInt("SoftAndWet.p_cooldown", 15) * 1000;
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
            rx7FC(1, 1, playerID);
        }, i * 25L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BLUE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!"));
}

public void softAndWetBG(Player player, UUID playerID, FileConfiguration config){
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
    rx7FC(2, 10, playerID);
}

public void purpleHazeP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("PurpleHaze.p_damage", 1.5) * level;
    int hits = config.getInt("PurpleHaze.p_hits", 3);
    double knockbackStrength = config.getDouble("PurpleHaze.p_knockback", 0.5);
    int cooldownTime = config.getInt("PurpleHaze.p_cooldown", 15) * 1000;
    if (cooldown.isOnCooldown("PurpleHazeP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("PurpleHazeP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("PurpleHazeP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 20L);
    }
   player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + player.getName() + "'s Stand: " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "ARU ARU ARU ARU!"));
}

public void purpleHazeKVI(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(2, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int radius = config.getInt("PurpleHaze.radius", 5) + aL;
    int duration = config.getInt("PurpleHaze.duration", 120) * 20;
    int amplifier = config.getInt("PurpleHaze.amplifier", 0);

    int cooldownTime = config.getInt("PurpleHaze.cooldown", 5) * 1000;
    if (cooldown.isOnCooldown("PurpleHazeKVI", playerID)) {
        long remainingTime = cooldown.getRemainingTime("PurpleHazeKVI", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Killer Virus Infection is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("PurpleHazeKVI", playerID, cooldownTime);

    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Purple Haze, " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "UBASHAAA!"));
    rx7FC(2, 10, playerID);
    new BukkitRunnable() {
        int ticksElapsed = 0;
        @Override
        public void run() {
            if (ticksElapsed >= duration) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Purple Haze, " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Killer Virus Infection ended!"));
                cancel();
                return;
            }
            player.getWorld().spawnParticle(Particle.SNEEZE, player.getLocation(), 500, radius, radius, radius, 0.1);
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entity;
                    target.removePotionEffect(PotionEffectType.POISON);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, amplifier));
                }
            }
            ticksElapsed += 20;
        }
    }.runTaskTimer(plugin, 0L, 20L);
}

public void spiceGirlP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("SpiceGirl.damage", 1.5) * level;
    int hits = config.getInt("SpiceGirl.hits", 4);
    double knockbackStrength = config.getDouble("SpiceGirl.knockback", 0.6);
    int cooldownTime = config.getInt("SpiceGirl.punch_cooldown", 15) * 1000;
   if (cooldown.isOnCooldown("SpiceGirlP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("SpiceGirlP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("SpiceGirlP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 11L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "WAAAANNABEEEE!"));
}

public void whitesnakeP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("Whitesnake.damage", 1.5) * level;
    int hits = config.getInt("Whitesnake.hits", 2);
    double knockbackStrength = config.getDouble("Whitesnake.knockback", 0.6);
    int cooldownTime = config.getInt("Whitesnake.p_cooldown", 15) * 1000;
    if (cooldown.isOnCooldown("WhitesnakeP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("WhitesnakeP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("WhitesnakeP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 25L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + player.getName() + "'s Stand: " + ChatColor.BOLD + "USHYAAA!"));
}

public void whitesnakeDE(Player player, UUID playerID, Entity entity, FileConfiguration config) {
    Entity target = (Entity) entity;
    int level = sileighty(2, playerID);
    if(!(entity instanceof Player)){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "DISC Extraction can be only used on players!"));
        return;
    }
    int cT = config.getInt("Whitesnake.di_cooldown", 1200) * 1000;
    int cooldownTime = cT / level;
    if (cooldown.isOnCooldown("WhitesnakeDI", playerID)) {
        long remainingTime = cooldown.getRemainingTime("WhitesnakeDI", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "DISC Extraction is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("WhitesnakeDI", playerID, cooldownTime);
    UUID targetID = target.getUniqueId();
    FileConfiguration stConfig = YamlConfiguration.loadConfiguration(standFile);
    List<String> log = stConfig.getStringList("players." + playerID + ".standLog");
    if (log.contains(targetID.toString())) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You have already reset the stand of " + ChatColor.GRAY + target.getName() + "!"));
        return;
    }
    stConfig.set("players." + targetID, null);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "Whitesnake: " + ChatColor.BOLD + "DISC Extraction!"));
    rx7FC(2, 20, playerID);
    log.add(targetID.toString());
    stConfig.set("players." + playerID + ".standLog", log);
    try {
        stConfig.save(standFile);
    } catch (IOException e) {
        Bukkit.getLogger().severe(e.getMessage());
    }
}

public void cmoonP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("CMOON.damage", 2.0) * level;
    int hits = config.getInt("CMOON.hits", 3);
    double knockbackStrength = config.getDouble("CMOON.knockback", 0.7);
    int cooldownTime = config.getInt("CMOON.p_cooldown", 15) * 1000;
   if (cooldown.isOnCooldown("CMOONP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("CMOONP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("CMOONP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 20L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + player.getName() + "'s Stand: " + ChatColor.BOLD + "USHYAAA!"));
}

public void cmoonSI(Player player, UUID playerID, Entity entity, FileConfiguration config){
    if(!(entity instanceof Player)){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Surface Inversion can be only used on players!"));
        return;
    }
    Player target = (Player) entity;
    int level = sileighty(2, playerID);
    int duration = config.getInt("CMOON.si_duration", 5) * 20;
    double damage = config.getDouble("CMOON.si_damage", 1.5) * level;
    int cooldownTime = config.getInt("CMOON.si_cooldown", 120) * 1000;
    if (cooldown.isOnCooldown("CMOONSI", playerID)) {
        long remainingTime = cooldown.getRemainingTime("CMOONSI", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Surface Inversion is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("CMOONSI", playerID, cooldownTime);
    target.damage(damage, player);
    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1, false, false));
    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, 1, false, false));
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "C-MOON: " + ChatColor.BOLD + "Surface Inversion!"));
    rx7FC(2, 20, playerID);
}

public void madeInHeavenP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("MadeInHeaven.damage", 1.0) * level;
    int hits = config.getInt("MadeInHeaven.hits", 6);
    double knockbackStrength = config.getDouble("MadeInHeaven.knockback", 0.4);
    int cooldownTime = config.getInt("MadeInHeaven.p_cooldown", 15) * 1000;
    if (cooldown.isOnCooldown("MadeInHeavenP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("MadeInHeavenP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("MadeInHeavenP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 11L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + player.getName() + "'s Stand: " + ChatColor.BOLD + "USHYAAAA!"));
}

public void madeInHeavenTA(Player player, UUID playerID, FileConfiguration config) {
    World world = player.getWorld();
    int duration = config.getInt("MadeInHeaven.duration", 20) *20;
    int level = sileighty(2, playerID);
    int evo3 = 5 * level;
    int initialD = config.getInt("MadeInHeaven.initialSpeed", 10);//AE86 Trueno
    int cooldownTime = config.getInt("MadeInHeaven.ta_cooldown", 1200) * 1000;
    if (cooldown.isOnCooldown("MadeInHeavenTA", playerID)) {
        long remainingTime = cooldown.getRemainingTime("MadeInHeavenTA", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time Acceleration is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("MadeInHeavenTA", playerID, cooldownTime);
    player.removePotionEffect(PotionEffectType.SPEED);
    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 9, false, false));
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "" + ChatColor.BOLD + "MADE IN HEAVEN!"));
    rx7FC(2, 20, playerID);
    new BukkitRunnable() {
        int ticks = 0;
        int cSp = initialD;
        @Override
        public void run() {
            if (ticks >= duration) {
                this.cancel();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
                return;
            }
            world.setTime(world.getTime() + cSp);
            if (ticks % 10 == 0 && cSp < 1000) { 
                cSp += evo3;
            }
            ticks++;
        }
    }.runTaskTimer(plugin, 0L, 1L);
}

public void achtungBaby(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(1, playerID);
    int aDur = config.getInt("AchtungBaby.duration", 5) * 20;
    int duration = aDur * level;
    int cooldownTime = config.getInt("AchtungBaby.cooldown", 120) * 1000;
    if (cooldown.isOnCooldown("AchtungBaby", playerID)) {
        long remainingTime = cooldown.getRemainingTime("AchtungBaby", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Achtung Baby is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("AchtungBaby", playerID, cooldownTime);
    rx7FC(1, 10, playerID);
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.equals(player)) {
            onlinePlayer.hidePlayer(plugin, player);
        }
    }
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
        Bukkit.getOnlinePlayers().forEach(p -> {p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "You're visible again!"));});
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayer.showPlayer(plugin, player);
            }
        }
    }, duration);
}

public void rhcpP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("RedHotChiliPepper.damage", 1.5) * level;
    int hits = config.getInt("RedHotChiliPepper.hits", 4);
    double knockbackStrength = config.getDouble("RedHotChiliPepper.knockback", 0.6);
    int cooldownTime = config.getInt("RedHotChiliPepper.p_cooldown", 15) * 1000;
   if (cooldown.isOnCooldown("RHCPPu", playerID)) {
        long remainingTime = cooldown.getRemainingTime("RHCPPu", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("RHCPPu", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 20L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + player.getName() + "'s Stand: " + ChatColor.BOLD + "YEEEAAAAH!"));
}

public void rhcpLS(Player player, UUID playerID, FileConfiguration config){
    int level = sileighty(2, playerID);
    int aL;
    if(level <= 1){
        aL = 0;
    }else{
        aL = level;
    }
    int cooldownTime = config.getInt("RedHotChiliPepper.ls_cooldown", 120) * 1000;
    int radius = config.getInt("RedHotChiliPepper.ls_radius", 5) + aL;
        
    if (cooldown.isOnCooldown("RHCPL", playerID)) {
        long remainingTime = cooldown.getRemainingTime("RHCPL", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Lightning Strike is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    Location targetLocation = player.getTargetBlock(null, 50).getLocation();
    player.getWorld().strikeLightning(targetLocation);
    for (Player nearbyPlayer : player.getWorld().getPlayers()) {
        if (nearbyPlayer.equals(player)) continue;
        if (nearbyPlayer.getLocation().distance(targetLocation) <= radius) {
            nearbyPlayer.getWorld().strikeLightning(nearbyPlayer.getLocation());
        }
    }
    cooldown.setCooldown("RHCPL", playerID, cooldownTime);
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "Red Hot Chili Pepper: " + ChatColor.GOLD + "LIGHTNING STRIKE!"));
    rx7FC(2, 10, playerID);
}

public void diverDownP(Player player, UUID playerID, Entity entity, FileConfiguration config){
    LivingEntity target = (LivingEntity) entity;
    int level = sileighty(1, playerID);
    double damage = config.getDouble("DiverDown.damage", 1.5) * level;
    int hits = config.getInt("DiverDown.hits", 4);
    double knockbackStrength = config.getDouble("DiverDown.knockback", 0.6);
    int cooldownTime = config.getInt("DiverDown.punch_cooldown", 15) * 1000;
   if (cooldown.isOnCooldown("DiverDownP", playerID)) {
        long remainingTime = cooldown.getRemainingTime("DiverDownP", playerID);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
        return;
    }
    cooldown.setCooldown("DiverDownP", playerID, cooldownTime);
    for (int i = 0; i < hits; i++) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (target.isDead()) return;
            target.damage(damage, player);
            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            target.setVelocity(direction.multiply(knockbackStrength));
            rx7FC(1, 1, playerID);
        }, i * 11L);
    }
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + ": " + ChatColor.BOLD + "DIVER DOWN!"));
}

@EventHandler
public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    FileConfiguration config = plugin.getConfig();
    Player player = event.getPlayer();
    UUID playerID = player.getUniqueId();
    Entity entity = event.getRightClicked();
    ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
    String stand = getPlayerStand(playerID);
    int a2 = getAbLevel(2, playerID);

    if (event.getHand() != EquipmentSlot.OFF_HAND) {
        return;
    }
    if (isStandItem(itemIn2Hand)) {
        if (stand == null) {
            return;
        }
        switch (stand) {
            case "Star Platinum":
                starPlatinumP(player, playerID, entity, config);
                break;

            case "The World":
                theWorldP(player, playerID, entity, config);
                break;

            case "Crazy Diamond":
                if(player.isSneaking()){
                    if (a2 >= 1) {
                        crazyDiamondHeal(player, playerID, entity, config);
                    }
                }else{
                    crazyDiamondP(player, playerID, entity, config);
                }
                break;

            case "Magician Red":
                magicianRedP(player, playerID, entity, config);
                break;

            case "Killer Queen":
                killerQueenP(player, playerID, entity, config);
                break;

            case "Silver Chariot":
                silverChariotHR(player, playerID, entity, config);
                break;

            case "Hierophant Green":
                hierophantGreenP(player, playerID, entity, config);
                break;

            case "Heavens Door":
                heavensDoor(player, playerID, entity, config);
                break;

            case "Soft & Wet":
                softAndWetP(player, playerID, entity, config);
                break;

            case "Purple Haze":
                purpleHazeP(player, playerID, entity, config);
                break;

            case "Spice Girl":
                spiceGirlP(player, playerID, entity, config);
                break;

            case "Whitesnake":
                if (player.isSneaking()){
                    if (a2 >= 1) {
                        whitesnakeDE(player, playerID, entity, config); 
                    }
                }else{
                    whitesnakeP(player, playerID, entity, config);
                }
                break;
            
            case "Red Hot Chili Pepper":
                rhcpP(player, playerID, entity, config);
                break;

            case "Made in Heaven":
                madeInHeavenP(player, playerID, entity, config);
                break;

            default:
                break;
        }
    }
}

@EventHandler
public void onHandSwap(PlayerSwapHandItemsEvent event) {
    FileConfiguration config = plugin.getConfig();
    Player player = event.getPlayer();
    UUID playerID = player.getUniqueId();
    ItemStack itemIn2Hand = event.getMainHandItem();
    String stand = getPlayerStand(playerID);
    int a2 = getAbLevel(2, playerID);
    int a3 = getAbLevel(3, playerID);

    if (isStandItem(itemIn2Hand)) {
        event.setCancelled(true);
        if (stand == null) {
            return;
        }
        switch (stand) {
            case "Star Platinum":
                if (a3 >= 1) {
                    starPlatinumTS(player, playerID, config); 
                }
                break;
    
            case "The World":
                if (a3 >= 1) {
                    theWorldTS(player, playerID, config);  
                }
                break;
    
            case "Crazy Diamond":
                if (a3 >= 1) {
                    crazyDiamondSH(player, playerID, config); 
                }
                break;
    
            case "Magician Red":
                if (a2 >= 1) {
                    magicianRedCFH(player, playerID, config); 
                }
                break;
    
            case "Hermit Purple":
                hermitPurple(player, playerID, config);
                break;
    
            case "Killer Queen":
                if (a2 >= 1) {
                    killerQueenABT(player, playerID, config);
                }
                break;
    
            case "November Rain":
                novemberRain(player, playerID, config);
                break;
    
            case "Tusk":
                if(player.isSneaking()){
                    if (a2 >= 1) {
                        tuskA3(player, playerID, config); 
                    }
                }else{
                    tuskA2(player, playerID, config);
                }
                break;
    
            case "Hierophant Green":
                if (a2 >= 1) {
                    hierophantGreenES(player, playerID, config); 
                }
                break;
    
            case "Sexy Pistols":
                sexyPistols(player, playerID, config);
                break;
    
            case "Soft & Wet":
                if (a2 >= 1) {
                    softAndWetBG(player, playerID, config); 
                }
                break;

            case "Purple Haze":
                if (a2 >= 1) {
                    purpleHazeKVI(player, playerID, config);  
                }
                break;

            case "Achtung Baby":
                achtungBaby(player, playerID, config);
                break;

            case "Red Hot Chili Pepper":
                if (a2 >= 1) {
                    rhcpLS(player, playerID, config);
                }
                break;

            case "Made in Heaven":
                if (a2 >= 1) {
                    madeInHeavenTA(player, playerID, config);  
                }
                break;
        
            default:
                break;
        }
    }
}

@EventHandler
public void onPlayerInteract(PlayerInteractEvent event) {
    FileConfiguration config = plugin.getConfig();
    Player player = event.getPlayer();
    UUID playerID = player.getUniqueId();
    ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
    String stand = getPlayerStand(playerID);
    int a2 = getAbLevel(2, playerID);
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
                        if (a2 >= 1) {
                            starPlatinumSF(player, playerID, config);  
                        }
                    }
                break;

            case "The World":
                if (player.isSneaking()){
                    if (a2 >= 1) {
                        theWorldKT(player, playerID, config);
                    }
                }
                break;

            default:
                break;
         }
    }
}

@EventHandler
public void onBlockInteract(PlayerInteractEvent event) {
    FileConfiguration config = plugin.getConfig();
    Player player = event.getPlayer();
    UUID playerID = player.getUniqueId();
    ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
    String stand = getPlayerStand(playerID);
    Block block = event.getClickedBlock();
    int a3 = getAbLevel(3, playerID);
    if (event.getHand() != EquipmentSlot.OFF_HAND) {
        return;
    }
    if (isStandItem(itemIn2Hand)){
        if (stand == null)return;
        switch (stand) {
            case "Killer Queen":
                if (a3 >= 1) {
                    killerQueenBTD(player, playerID, block, config);
                }
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
        UUID playerID = player.getUniqueId();

        if(standSB.hasMetadata("SoftAndWetB")){
            int level = sileighty(2, playerID);
            double damage = config.getDouble("SoftAndWet.damage", 2.0) * level;
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
                target.addPotionEffect(new PotionEffect(randomEffect, 100, 1, false, false));
                target.damage(damage, player);
            }
        }

        if(standSB.hasMetadata("SexyPistols")){
            int level = sileighty(1, playerID);
            double damage = config.getDouble("SexyPistols.damage", 2.0) * level;
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
            int level = sileighty(1, playerID);
            double damage = config.getDouble("Tusk.damage", 3.0) * level;
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
    
    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID playerID = player.getUniqueId();
            String stand = getPlayerStand(playerID);
            if (stand == null)return;
            if(stand.equalsIgnoreCase("Spice Girl")){
                event.setCancelled(true);
            }
        }
        if(event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING && event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            UUID playerID = player.getUniqueId();
            String stand = getPlayerStand(playerID);
            if (stand == null)return;
            if(stand.equalsIgnoreCase("Red Hot Chili Pepper")){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damage2(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getEntity() instanceof Player) {
            LivingEntity damager = (LivingEntity) e.getDamager();
            Player player = (Player) e.getEntity();
            UUID playerID = player.getUniqueId();
            String stand = getPlayerStand(playerID);
            if (stand == null)return;
            if(stand.equalsIgnoreCase("C-MOON")){
                int level = sileighty(4, playerID);
                damager.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 30, 1 + level, false, false));
            }
        }
        if(e.getDamager() instanceof EnderPearl && e.getEntity() instanceof Player){
            Player player = (Player) e.getEntity();
            UUID playerID = player.getUniqueId();
            String stand = getPlayerStand(playerID);
            if (stand == null)return;
            if(stand.equalsIgnoreCase("Tusk")){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDrinkMilk(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            Player player = event.getPlayer();
            UUID playerID = player.getUniqueId();
            String stand = getPlayerStand(playerID);
            if (stand == null)return;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                int level = sileighty(4, playerID);
                if(stand.equalsIgnoreCase("Diver Down")){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
                    if (level >= 2) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 0, false, false));
                    }
                }
                if(stand.equalsIgnoreCase("Made in Heaven"))player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1 + level, false, false));
            }, 10L);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        String stand = getPlayerStand(playerID);
        if (stand == null)return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int level = sileighty(4, playerID);
            if(stand.equalsIgnoreCase("Diver Down")){
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
                if (level >= 2) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 0, false, false));
                }
            }
            if(stand.equalsIgnoreCase("Made in Heaven"))player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1 + level, false, false));
        }, 10L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        String stand = getPlayerStand(playerID);
        if (stand == null)return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            int level = sileighty(4, playerID);
            if(stand.equalsIgnoreCase("Diver Down")){
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
                if (level >= 2) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 0, false, false));
                }
            }
            if(stand.equalsIgnoreCase("Made in Heaven"))player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1 + level, false, false));
        }, 10L);
    }
}

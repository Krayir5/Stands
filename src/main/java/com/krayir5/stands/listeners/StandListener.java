package com.krayir5.stands.listeners;

import java.io.File;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
        FileConfiguration config = plugin.getConfig();
        UUID playerID = player.getUniqueId();
        String stand = getPlayerStand(player);

        if (event.getHand() != EquipmentSlot.OFF_HAND) {
            return;
        }
        if(itemIn2Hand.getType() == Material.NETHER_STAR && itemIn2Hand.hasItemMeta() && isStandItem(itemIn2Hand)){
            if (stand == null) {
                return;
            }

            // Crazy Diamond: Healing/Punch Throw
            if (stand.equalsIgnoreCase("Crazy Diamond")) {
                int duration = config.getInt("CrazyDiamond.time_duration", 10) * 20;
                int amplifier = config.getInt("CrazyDiamond.amplifier", 0);
                int cooldownH = config.getInt("CrazyDiamond.heal_cooldown", 30) * 1000;
                int cooldownP = config.getInt("CrazyDiamond.punch_cooldown", 5) * 1000;
                int hits = config.getInt("CrazyDiamond.hits", 4);
                double knockbackStrength = config.getDouble("CrazyDiamond.knockback", 0.5);
                double damage = config.getDouble("CrazyDiamond.damage", 5.0);

                if (player.isSneaking()) {
                    if (entity instanceof LivingEntity) {
                        if (cooldown.isOnCooldown("CrazyDiamondH", playerID)) {
                            long remainingTime = cooldown.getRemainingTime("CrazyDiamondH", playerID);
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                            return;
                        }
                        cooldown.setCooldown("CrazyDiamondH", playerID, cooldownH);
                        LivingEntity target = (LivingEntity) entity;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
                        String msg = ChatColor.LIGHT_PURPLE + "Crazy Diamond: Healed " + target.getName() + "!";
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
                    }
                }else{
                    if (entity instanceof LivingEntity) {
                        if (cooldown.isOnCooldown("CrazyDiamondP", playerID)) {
                            long remainingTime = cooldown.getRemainingTime("CrazyDiamondP", playerID);
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "DORARARARARA!"));
                    }
                }
            }
            
            //Star Platinum: ORA ORA
            if (stand.equalsIgnoreCase("Star Platinum")) {
                LivingEntity target = (LivingEntity) entity;
                double damage = config.getDouble("StarPlatinum.damage", 1.0);
                int hits = config.getInt("StarPlatinum.hits", 5);
                double knockbackStrength = config.getDouble("StarPlatinum.knockback", 0.3);
                int cooldownTime = config.getInt("StarPlatinum.punch_cooldown", 5) * 1000;
               if (cooldown.isOnCooldown("StarPlatinumP", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("StarPlatinumP", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!"));
            }

            //The World: MUDA MUDA
            if (stand.equalsIgnoreCase("The World")) {
                LivingEntity target = (LivingEntity) entity;
                double damage = config.getDouble("TheWorld.damage", 1.0);
                int hits = config.getInt("TheWorld.hits", 5);
                double knockbackStrength = config.getDouble("TheWorld.knockback", 0.3);
                int cooldownTime = config.getInt("TheWorld.cooldown_punch", 5) * 1000;
                if (cooldown.isOnCooldown("TheWorldP", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("TheWorldP", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
               event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + player.getName() + "'s Stand: " + ChatColor.BOLD + "MUDA MUDA MUDA MUDA!"));
            }

            //Killer Queen: Punch Throw
            if (stand.equalsIgnoreCase("Killer Queen")) {
                LivingEntity target = (LivingEntity) entity;
                double damage = config.getDouble("KillerQueen.damage", 1.0);
                int hits = config.getInt("KillerQueen.hits", 5);
                double knockbackStrength = config.getDouble("KillerQueen.knockback", 0.3);
                int cooldownTime = config.getInt("KillerQueen.cooldown_punch", 5) * 1000;
                if (cooldown.isOnCooldown("KillerQueenP", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("KillerQueenP", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
               event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "SHIBABABA!"));
            }

            //Magician Red: Punch Throw
            if (stand.equalsIgnoreCase("Magician Red")) {
                LivingEntity target = (LivingEntity) entity;
                double damage = config.getDouble("MagicianRed.damage", 1.0);
                int hits = config.getInt("MagicianRed.hits", 5);
                double knockbackStrength = config.getDouble("MagicianRed.knockback", 0.3);
                int cooldownTime = config.getInt("MagicianRed.cooldown_punch", 5) * 1000;
                if (cooldown.isOnCooldown("MagicianRedP", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("MagicianRedP", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
               event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + player.getName() + "'s Stand: " + ChatColor.BOLD + "CACOWW!"));
            }

            //Silver Chariot: Hora Rush
            if (stand.equalsIgnoreCase("Silver Chariot")) {
            LivingEntity target = (LivingEntity) entity;
            double damage = config.getDouble("SilverChariot.damage", 1.0);
            int hits = config.getInt("SilverChariot.hits", 5);
            double knockbackStrength = config.getDouble("SilverChariot.knockback", 0.2);    
            int cooldownTime = config.getInt("SilverChariot.cooldown", 5) * 1000;

            if (cooldown.isOnCooldown("SilverChariot", playerID)) {
                long remainingTime = cooldown.getRemainingTime("SilverChariot", playerID);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Hora Rush is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("SilverChariot", playerID, cooldownTime);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY +"Silver Chariot: "+ ChatColor.BOLD +"Hora Rush!"));
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

            //Hierophant Green: Punch Throw
            if (stand.equalsIgnoreCase("Hierophant Green")) {
                LivingEntity target = (LivingEntity) entity;
                double damage = config.getDouble("HierophantGreen.p_damage", 0.5);
                int hits = config.getInt("HierophantGreen.hits", 2);
                double knockbackStrength = config.getDouble("HierophantGreen.knockback", 0.3);
                int cooldownTime = config.getInt("HierophantGreen.cooldown_punch", 5) * 1000;
                if (cooldown.isOnCooldown("HierophantGreenP", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("HierophantGreenP", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
               event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + player.getName() + "'s Stand: " + ChatColor.BOLD + "Hierophant Green!"));
            }

            //Soft & Wet: Punch Throw
            if (stand.equalsIgnoreCase("Soft & Wet")) {
                LivingEntity target = (LivingEntity) entity;
                double damage = config.getDouble("SoftAndWet.p_damage", 0.5);
                int hits = config.getInt("SoftAndWet.hits", 2);
                double knockbackStrength = config.getDouble("SoftAndWet.knockback", 0.3);
                int cooldownTime = config.getInt("SoftAndWet.p_cooldown", 5) * 1000;
                if (cooldown.isOnCooldown("SoftAndWetP", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("SoftAndWetP", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Punch Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
               event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BLUE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!"));
            }

            //Heaven's Door: Book Transmutation
            if (stand.equalsIgnoreCase("Heavens Door")) {
                double damage = config.getDouble("HeavensDoor.damage", 1.0);
                int duration = config.getInt("HeavensDoor.duration", 30) * 20;
                int cooldownTime = config.getInt("HeavensDoor.cooldown", 120) * 1000;
                if(!(entity instanceof Player)){
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Heaven's Door can be only used on players!"));
                    return;
                }
                Player target = (Player) entity;
                if (cooldown.isOnCooldown("HeavensDoor", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("HeavensDoor", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Heaven's Door is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
                cooldown.setCooldown("HeavensDoor", playerID, cooldownTime);
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, false, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 128, false, false));
                
                target.damage(damage, player);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.WHITE + "Heaven's Door: " + ChatColor.BOLD + "Book Transmutation!"));
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
        }
    }

    @EventHandler
    public void onHandSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack itemIn2Hand = event.getMainHandItem();
        FileConfiguration config = plugin.getConfig();
        UUID playerID = player.getUniqueId();
        String stand = getPlayerStand(player);

        if(isStandItem(itemIn2Hand)){
            event.setCancelled(true);
            if (stand == null) {
                return;
            }

            // The World: Time Stop
            if (stand.equalsIgnoreCase("The World")) {
                int duration = config.getInt("TheWorld.time_duration", 5) * 20;
                int cooldownTime = config.getInt("TheWorld.cooldown", 240) * 1000;
    
                if (cooldown.isOnCooldown("TheWorldTS", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("TheWorldTS", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time Stop is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "ZA WARUDO!" + ChatColor.RED + ChatColor.BOLD + " TOKI WO TOMARE!!!"));
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

            // Star Platinum: The World
            if (stand.equalsIgnoreCase("Star Platinum")) {
                int duration = config.getInt("StarPlatinum.time_duration", 5) * 20;
                int cooldownTime = config.getInt("StarPlatinum.cooldown", 240) * 1000;
    
                if (cooldown.isOnCooldown("StarPlatinumTS", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("StarPlatinumTS", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Time Stop is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
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
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Star Platinum!" + ChatColor.BOLD + " ZA WARUDO!!!"));
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

            // Crazy Diamond: Selfheal (I do know that it can't heal himself but it would be so awesome it would be so cool)
            if (stand.equalsIgnoreCase("Crazy Diamond")) {
            int cooldownTime = config.getInt("CrazyDiamond.cooldown", 30) * 1000;
            if (cooldown.isOnCooldown("CrazyDiamond", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("CrazyDiamond", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
            cooldown.setCooldown("CrazyDiamond", playerID, cooldownTime);
            int duration = config.getInt("CrazyDiamond.time_duration", 5) * 20;
            int amplifier = config.getInt("CrazyDiamond.amplifier", 0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Crazy Diamond: You healed yourself!"));
            }

            //Magician Red: Cross Fire Hurricane
            if (stand.equalsIgnoreCase("Magician Red")) {
                double speedMultiplier = config.getDouble("MagicianRed.fireball_speed", 2.0);
                double damage = config.getDouble("MagicianRed.fireball_damage", 3.0);
                int fireballCount = config.getInt("MagicianRed.fireball_count", 3);
                int cooldownTime = config.getInt("MagicianRed.cooldown", 30) * 1000;
                boolean mobGrief = config.getBoolean("MagicianRed.mob_grief", true);
                if (cooldown.isOnCooldown("MagicianRedCFH", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("MagicianRedCFH", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Cross Fire Hurricane is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
                cooldown.setCooldown("MagicianRedCFH", playerID, cooldownTime);
                String msg = ChatColor.RED + "Magician Red: " + ChatColor.BOLD + "Cross Fire Hurricane!";
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
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

            //Hermit Purple: Overdrive(I mean it overdrives and attacks but it pulls towards him as a bonus)
            if (stand.equalsIgnoreCase("Hermit Purple")) {
                int vineLength = config.getInt("HermitPurple.vine_length", 4);
                double pullStrength = config.getDouble("HermitPurple.pull_strength", 1.3);
                double damage = config.getDouble("HermitPurple.damage", 3.0);
                int cooldownTime = config.getInt("HermitPurple.cooldown", 20) * 1000;
                if (cooldown.isOnCooldown("HermitPurple", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("HermitPurple", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Hermit Purple is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
                cooldown.setCooldown("HermitPurple", playerID, cooldownTime);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Overdrive!"));
    
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
                                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Cought " +target.getName()+ "!"));
                                target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "GOT YOU!"));
                            }else if(entity instanceof LivingEntity){
                                LivingEntity target = (LivingEntity) entity;
                                Vector pullDirection = player.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
                                target.setVelocity(pullDirection.multiply(pullStrength));
                                target.damage(damage, player);
                                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Cought " +target.getName()+ "!"));
                            }else{
                                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "OH MY GOD!!!"));
                            }
                        }
                    }, i * 5L);
                }
            }

            //November Rain: Heavy Rain
            if (stand.equalsIgnoreCase("November Rain")) {
                int radius = config.getInt("NovemberRain.radius", 10);
                int duration = config.getInt("NovemberRain.duration", 5) * 20;
                double damage = config.getDouble("NovemberRain.damage", 2.0);
    
                int cooldownTime = config.getInt("NovemberRain.cooldown", 600) * 1000;
                if (cooldown.isOnCooldown("NovemberRain", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("NovemberRain", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "November Rain is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
                cooldown.setCooldown("NovemberRain", playerID, cooldownTime);
    
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_BLUE + "November Rain, " + ChatColor.BLUE + ChatColor.BOLD + "Heavy Rain!"));
                new BukkitRunnable() {
                    int ticksElapsed = 0;
                    @Override
                    public void run() {
                        if (ticksElapsed >= duration) {
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_BLUE + "November Rain, " + ChatColor.BLUE + ChatColor.BOLD + "Heavy Rain ended!"));
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

            //Tusk Act 2: Golden Rectangle Nails
            if (stand.equalsIgnoreCase("Tusk")) {
                int cooldownTime = config.getInt("Tusk.cooldown", 15) * 1000;
                double damage = config.getDouble("Tusk.damage", 3.0); 
    
                if (cooldown.isOnCooldown("Tusk", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("Tusk", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Tusk is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
                cooldown.setCooldown("Tusk", playerID, cooldownTime);
                    Snowball johnnysNail = player.launchProjectile(Snowball.class);
                    johnnysNail.setVelocity(player.getLocation().getDirection().multiply(1.5));
                    johnnysNail.setGravity(false);
                    johnnysNail.setShooter(player);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Tusk Act 2: " + ChatColor.BOLD + "CHUMIMI~IN!"));
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                        if (!johnnysNail.isDead()) {
                            johnnysNail.getWorld().getNearbyEntities(johnnysNail.getLocation(), 0.5, 0.5, 0.5).forEach(entity -> {
                                if (entity instanceof LivingEntity && entity != player) {
                                    LivingEntity target = (LivingEntity) entity;
                                    target.damage(damage, player);
                                    johnnysNail.remove();
                                }
                            });
                        } else {
                            Bukkit.getScheduler().cancelTasks(plugin);
                        }
                    }, 0L, 1L);
            }

            //Hierophant Green: Emerald Splash
            if (stand.equalsIgnoreCase("Hierophant Green")) {
                int cooldownTime = config.getInt("HierophantGreen.cooldown", 30) * 1000;
                double damage = config.getDouble("HierophantGreen.damage", 3.0);
                int hgCount = config.getInt("HierophantGreen.ammo_count", 3);
    
                if (cooldown.isOnCooldown("HierophantGreenES", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("HierophantGreenES", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Emerald Splash is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " seconds."));
                    return;
                }
                cooldown.setCooldown("HierophantGreenES", playerID, cooldownTime);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "Hierophant Green: " + ChatColor.BOLD + "Emerald Splash!"));
                for (int i = 0; i < hgCount; i++) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Snowball hgProjectile = player.launchProjectile(Snowball.class);
                        hgProjectile.setVelocity(player.getLocation().getDirection().multiply(2));
                        hgProjectile.setShooter(player);
                        hgProjectile.setGravity(false);
                        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                            if (!hgProjectile.isDead()) {
                                hgProjectile.getWorld().getNearbyEntities(hgProjectile.getLocation(), 0.5, 0.5, 0.5).forEach(entity -> {
                                    if (entity instanceof LivingEntity && entity != player) {
                                        LivingEntity target = (LivingEntity) entity;
                                        target.damage(damage, player);
                                        hgProjectile.remove();
                                    }
                                });
                            }
                        }, 0L, 1L);
                    }, i * 5L);
                }
            }

            //Soft & Wet: Bubble Generation
            if (stand.equalsIgnoreCase("Soft & Wet")) {
                int cooldownTime = config.getInt("SoftAndWet.cooldown", 2) * 1000;
                double damage = config.getDouble("SoftAndWet.damage", 3.0);
            
                if (cooldown.isOnCooldown("SoftAndWet", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("SoftAndWet", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(ChatColor.RED + "Bubble Generation is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " seconds."));
                    return;
                }
                cooldown.setCooldown("SoftAndWet", playerID, cooldownTime);

                Snowball bubble = player.launchProjectile(Snowball.class);
                bubble.setVelocity(player.getLocation().getDirection().multiply(1));
                bubble.setGravity(false);
                bubble.setShooter(player);
                bubble.setTicksLived(60);
            
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Soft & Wet " + ChatColor.BOLD + "Bubble Generation!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!bubble.isDead()) {
                            Location currentLocation = bubble.getLocation();
                            Vector direction = bubble.getVelocity();
                            Location nextLocation = currentLocation.clone().add(direction);
                            bubble.getWorld().getNearbyEntities(nextLocation, 0.5, 0.5, 0.5).forEach(entity -> {
                                if (entity instanceof LivingEntity && entity != player) {
                                    LivingEntity target = (LivingEntity) entity;
                                    if (entity.getBoundingBox().overlaps(currentLocation.toVector(), nextLocation.toVector())) {
                                        PotionEffectType[] negativeEffects = {
                                            PotionEffectType.SLOW,
                                            PotionEffectType.WEAKNESS,
                                            PotionEffectType.BLINDNESS,
                                            PotionEffectType.POISON,
                                            PotionEffectType.WITHER
                                        };
                                        PotionEffectType randomEffect = negativeEffects[new Random().nextInt(negativeEffects.length)];
                                        target.addPotionEffect(new PotionEffect(randomEffect, 100, 1));
                                        target.damage(damage, player);
                                        bubble.remove();
                                        this.cancel();
                                    }
                                }
                            });
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }            

            //Killer Queen: Air Bomb Transmutation 
            if (stand.equalsIgnoreCase("Killer Queen")) {
                int cooldownTime = config.getInt("KillerQueen.abt_cooldown", 2) * 1000;
                double damage = config.getDouble("KillerQueen.abt_damage", 3.0);
                double explosionRadius = config.getDouble("KillerQueen.abt_explosionRadius", 3.0);
                boolean grief = config.getBoolean("KillerQueen.abt_causeExplosion", false);
                if (cooldown.isOnCooldown("KillerQueenABT", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("KillerQueenABT", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Air Bomb Transmutation is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
                cooldown.setCooldown("KillerQueenABT", playerID, cooldownTime);
                Snowball airBomb = player.launchProjectile(Snowball.class);
                airBomb.setVelocity(player.getLocation().getDirection().multiply(2));
                airBomb.setShooter(player);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Killer Queen: " + ChatColor.BOLD + "Air Bomb Transmutation!"));
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

            //Sexy Pistols
            if (stand.equalsIgnoreCase("Sexy Pistols")) {
                int cT = config.getInt("SexyPistols.cooldown", 10);
                double damage = config.getDouble("SexyPistols.damage", 2.0);
                int bulletsAvailable = bulletsMap.getOrDefault(playerID, 7);
            
                if (cooldown.isOnCooldown("SexyPistols", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("SexyPistols", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Sexy Pistols is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
            
                if (bulletsAvailable > 0) {
                    cooldown.setCooldown("SexyPistols", playerID, 3000);
                    bulletsAvailable--;
                    bulletsMap.put(playerID, bulletsAvailable);
                    int bulletNumber = 7 - bulletsAvailable;
            
                    Snowball bullet = player.launchProjectile(Snowball.class);
                    bullet.setVelocity(player.getLocation().getDirection().multiply(1.5));
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
                                    if (bullet.getLocation().distance(nearestTarget.getLocation()) < 1) {
                                        nearestTarget.damage(damage, player);
                                        bullet.remove();
                                        this.cancel();
                                    }
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
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemIn2Hand = player.getInventory().getItemInOffHand();
        FileConfiguration config = plugin.getConfig();
        UUID playerID = player.getUniqueId();
        String stand = getPlayerStand(player);

        if (event.getHand() != EquipmentSlot.OFF_HAND) {
            return;
        }
        if(itemIn2Hand.getType() == Material.NETHER_STAR && itemIn2Hand.hasItemMeta() && isStandItem(itemIn2Hand)){
            if (stand == null) {
                return;
            }

            //Killer Queen: Another one bites the Dust(I made the explosion power 0 so yeah if you want to open SOULJA BOY CRANK THAT POWER UP)
            if (stand.equalsIgnoreCase("Killer Queen")) {
                Block block = event.getClickedBlock();
                float ePower = (float) plugin.getConfig().getDouble("KillerQueen.explosion_power", 0.0);
                int eDelay = config.getInt("KillerQueen.explosion_delay", 5);
                int cooldownTime = config.getInt("KillerQueen.cooldown", 120) * 1000;
                if (cooldown.isOnCooldown("KillerQueen", playerID)) {
                    long remainingTime = cooldown.getRemainingTime("KillerQueen", playerID);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Another one bites the Dust is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                    return;
                }
    
                if (block == null || block.getType() == Material.BEDROCK || block.getType() == Material.COMMAND_BLOCK || block.getType() == Material.BARRIER) return;
                    cooldown.setCooldown("KillerQueen", playerID, cooldownTime);
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Killer Queen, " + ChatColor.RED + ChatColor.BOLD + "Another one bites the DUST!"));
                    int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                    block.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.02);
                }, 0L, 10L);
    
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getScheduler().cancelTask(taskId);
                    block.getWorld().createExplosion(block.getLocation(), ePower, false, true, player);
                }, eDelay * 20);
            }

            //The World: Knife Throw
            if (stand.equalsIgnoreCase("The World")) {
                int cT = config.getInt("TheWorld.k_cooldown", 1) * 1000;
                int kC = config.getInt("TheWorld.knifeCount", 3);
                double damage = config.getDouble("TheWorld.k_damage", 4.0);
                if (player.isSneaking()) {
                    if (cooldown.isOnCooldown("TheWorldK", playerID)) {
                        long remainingTime = cooldown.getRemainingTime("TheWorldK", playerID);
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Knife Throw is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                        return;
                    }
                    cooldown.setCooldown("TheWorldK", playerID, cT);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "The World: " + ChatColor.RED + "WRRRRYYYYYY!"));
                    for (int i = 0; i < kC; i++) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Snowball kCProjectile = player.launchProjectile(Snowball.class);
                            kCProjectile.setVelocity(player.getLocation().getDirection().multiply(2));
                            kCProjectile.setShooter(player);
                            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                                if (!kCProjectile.isDead()) {
                                    kCProjectile.getWorld().getNearbyEntities(kCProjectile.getLocation(), 0.5, 0.5, 0.5).forEach(entity -> {
                                        if (entity instanceof LivingEntity && entity != player) {
                                            LivingEntity target = (LivingEntity) entity;
                                            target.damage(damage, player);
                                            kCProjectile.remove();
                                        }
                                    });
                                }
                            }, 0L, 1L);
                        }, i * 5L);
                    }
                }
            }
            
            //Star Platinum: Star Finger
            if (stand.equalsIgnoreCase("Star Platinum")) {
                int cooldownTime = config.getInt("StarPlatinum.sf_cooldown", 30) * 1000;
                double damage = config.getDouble("StarPlatinum.sf_damage", 4.0);
                double range = config.getDouble("StarPlatinum.sf_range", 10.0);
                if (player.isSneaking()) {
                    if (cooldown.isOnCooldown("StarPlatinumF", playerID)) {
                        long remainingTime = cooldown.getRemainingTime("StarPlatinumF", playerID);
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Star Finger is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                        return;
                    }
                    cooldown.setCooldown("StarPlatinumF", playerID, cooldownTime);

                    Snowball sF70H = player.launchProjectile(Snowball.class);
                    sF70H.setVelocity(player.getLocation().getDirection().multiply(1.5));
                    sF70H.setGravity(false);
                    sF70H.setSilent(true);
                    sF70H.setCustomNameVisible(false);

                    Location startLocation = sF70H.getLocation();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!sF70H.isDead()) {
                                if (startLocation.distance(sF70H.getLocation()) > range) {
                                    sF70H.remove();
                                    this.cancel();
                                    return;
                                }
                                List<Entity> nearbyEntities = sF70H.getNearbyEntities(0.5, 0.5, 0.5);
                                for (Entity entity : nearbyEntities) {
                                    if (entity instanceof LivingEntity && entity != player) {
                                        LivingEntity target = (LivingEntity) entity;
                                        target.damage(damage, player);
                                        sF70H.remove();
                                        this.cancel();
                                        return;
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }
            }            
        }
    }
}

package com.krayir5.stands.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.krayir5.stands.Plugin;
import com.krayir5.stands.utils.Cooldown;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class StandListener implements Listener {

    private final Plugin plugin;
    private final Cooldown cooldown = new Cooldown();

    public StandListener(Plugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        Material itemInHand = player.getInventory().getItemInMainHand().getType();
        FileConfiguration config = plugin.getConfig();
        UUID playerId = player.getUniqueId();

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        // Crazy Diamond: Healing
        if (itemInHand == Material.DIAMOND && entity instanceof LivingEntity) {
            int duration = config.getInt("CrazyDiamond.time_duration", 10) * 20;
            int amplifier = config.getInt("CrazyDiamond.amplifier", 0);
            int cooldownTime = config.getInt("CrazyDiamond.cooldown", 15) * 1000;
            if (cooldown.isOnCooldown("CrazyDiamond", playerId)) {
                long remainingTime = cooldown.getRemainingTime("CrazyDiamond", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("CrazyDiamond", playerId, cooldownTime);
            LivingEntity target = (LivingEntity) entity;
            target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            String msg = ChatColor.LIGHT_PURPLE + "Crazy Diamond: Healed " + target.getName() + "!";
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));

            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        //Star Platinum: ORA ORA
        if (itemInHand == Material.IRON_INGOT && entity instanceof LivingEntity){
            LivingEntity target = (LivingEntity) entity;
            double damage = config.getDouble("StarPlatinum.damage", 1.0);
            int hits = config.getInt("StarPlatinum.hits", 5);
            double knockbackStrength = config.getDouble("StarPlatinum.knockback", 0.3);
            int cooldownTime = config.getInt("StarPlatinum.cooldown", 5) * 1000;
            if (cooldown.isOnCooldown("StarPlatinum", playerId)) {
                long remainingTime = cooldown.getRemainingTime("StarPlatinum", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Star Platinum is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("StarPlatinum", playerId, cooldownTime);
            for (int i = 0; i < hits; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (target.isDead()) return;
                    target.damage(damage, player);
                    Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    target.setVelocity(direction.multiply(knockbackStrength));
                }, i * 5L);
            }
            String oraMessage = ChatColor.LIGHT_PURPLE + player.getName() + "'s Stand: " + ChatColor.BOLD + "ORA ORA ORA ORA!";
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(oraMessage));
            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        //The World: MUDA MUDA
        if (itemInHand == Material.GOLD_INGOT && entity instanceof LivingEntity){
            LivingEntity target = (LivingEntity) entity;
            double damage = config.getDouble("TheWorld.damage", 1.0);
            int hits = config.getInt("TheWorld.hits", 5);
            double knockbackStrength = config.getDouble("TheWorld.knockback", 0.3);
            int cooldownTime = config.getInt("TheWorld.cooldown_punch", 5) * 1000;
            if (cooldown.isOnCooldown("TheWorldP", playerId)) {
                long remainingTime = cooldown.getRemainingTime("TheWorldP", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The World is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("TheWorldP", playerId, cooldownTime);
            for (int i = 0; i < hits; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (target.isDead()) return;
                    target.damage(damage, player);
                    Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    target.setVelocity(direction.multiply(knockbackStrength));
                }, i * 5L);
            }
            String mudaMessage = ChatColor.YELLOW + player.getName() + "'s Stand: " + ChatColor.BOLD + "MUDA MUDA MUDA MUDA!";
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(mudaMessage));
            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        //Silver Chariot: Hora Rush
        if (itemInHand == Material.IRON_SWORD) {
        LivingEntity target = (LivingEntity) entity;
        double damage = config.getDouble("SilverChariot.damage", 1.0);
        int hits = config.getInt("SilverChariot.hits", 5);
        double knockbackStrength = config.getDouble("SilverChariot.knockback", 0.2);    
        int cooldownTime = config.getInt("Tusk.cooldown", 5) * 1000;

        if (cooldown.isOnCooldown("SilverChariot", playerId)) {
            long remainingTime = cooldown.getRemainingTime("SilverChariot", playerId);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Silver Chariot is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
            return;
        }
        cooldown.setCooldown("SilverChariot", playerId, cooldownTime);
        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText( ChatColor.GRAY +"Silver Chariot: "+ ChatColor.BOLD +"Hora Rush!"));
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

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material itemInHand = player.getInventory().getItemInMainHand().getType();
        FileConfiguration config = plugin.getConfig();
        UUID playerId = player.getUniqueId();

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        // The World: Time Stop
        if (itemInHand == Material.NETHERITE_INGOT) {
            int duration = config.getInt("TheWorld.time_duration", 5) * 20;
            int cooldownTime = config.getInt("TheWorld.cooldown", 240) * 1000;
            if (cooldown.isOnCooldown("TheWorld", playerId)) {
                long remainingTime = cooldown.getRemainingTime("TheWorld", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "The World is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("TheWorld", playerId, cooldownTime);
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!p.equals(player)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 128, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, duration, 128, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, 128, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 128, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 128, false, false));
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "The World: " + ChatColor.AQUA + player.getName() + " stopped the time!"));
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

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(p -> {p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "The World: " + ChatColor.RED + "Time will resume now!"));});
                Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.MOB_GRIEFING, true));
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.equals(player)) {
                        onlinePlayer.showPlayer(plugin, player);
                    }
                }
            }, duration);

            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        // Crazy Diamond: Selfheal (I do know that it can't heal himself but it would be so awesome it would be so cool)
        /*if (itemInHand == Material.DIAMOND) {
            int cooldownTime = config.getInt("CrazyDiamond.cooldown", 15) * 1000;
            if (cooldown.isOnCooldown("CrazyDiamond", playerId)) {
                long remainingTime = cooldown.getRemainingTime("CrazyDiamond", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Crazy Diamond is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("CrazyDiamond", playerId, cooldownTime);
            int duration = config.getInt("CrazyDiamond.time_duration", 5) * 20;
            int amplifier = config.getInt("CrazyDiamond.amplifier", 0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, amplifier));
            String msg = ChatColor.LIGHT_PURPLE + "Crazy Diamond: You healed yourself!";
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));

            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }*/

        //Magician Red: Cross Fire Hurricane
        if (itemInHand == Material.BLAZE_ROD) {
            double speedMultiplier = config.getDouble("MagicianRed.fireball_speed", 2.0);
            double damage = config.getDouble("MagicianRed.fireball_damage", 3.0);
            int fireballCount = config.getInt("MagicianRed.fireball_count", 3);
            int cooldownTime = config.getInt("MagicianRed.cooldown", 30) * 1000;
            if (cooldown.isOnCooldown("MagicianRed", playerId)) {
                long remainingTime = cooldown.getRemainingTime("MagicianRed", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Magician Red is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("MagicianRed", playerId, cooldownTime);
            String msg = ChatColor.RED + "Magician Red: " + ChatColor.BOLD + "Cross Fire Hurricane!";
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
            for (int i = 0; i < fireballCount; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Fireball fireball = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Fireball.class);
                    fireball.setShooter(player);
                    fireball.setDirection(player.getLocation().getDirection().normalize().multiply(speedMultiplier));
                    fireball.setYield((float) damage);
                    fireball.setIsIncendiary(true);
                }, i * 5L);
            }
            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        //Hermit Purple: Overdrive(I mean it overdrives and attacks but it pulls towards him as a bonus)
        if (itemInHand == Material.FEATHER) {
            int vineLength = config.getInt("HermitPurple.vine_length", 4);
            double pullStrength = config.getDouble("HermitPurple.pull_strength", 1.3);
            double damage = config.getDouble("HermitPurple.damage", 3.0);
            int cooldownTime = config.getInt("HermitPurple.cooldown", 20) * 1000;
            if (cooldown.isOnCooldown("HermitPurple", playerId)) {
                long remainingTime = cooldown.getRemainingTime("HermitPurple", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Hermit Purple is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("HermitPurple", playerId, cooldownTime);
            String msg = ChatColor.DARK_PURPLE + "Hermit Purple: " + ChatColor.BOLD + "Overdrive!";
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));

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
        
            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        //Killer Queen: Another one bites the Dust
        if (itemInHand == Material.QUARTZ && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            float ePower = (float) plugin.getConfig().getDouble("KillerQueen.explosion_power", 2.0);
            int eDelay = config.getInt("KillerQueen.explosion_delay", 5);
            int cooldownTime = config.getInt("KillerQueen.cooldown", 120) * 1000;
            if (cooldown.isOnCooldown("KillerQueen", playerId)) {
                long remainingTime = cooldown.getRemainingTime("KillerQueen", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Killer Queen is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("KillerQueen", playerId, cooldownTime);

            if (block == null || block.getType() == Material.BEDROCK || block.getType() == Material.COMMAND_BLOCK || block.getType() == Material.BARRIER) return;
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Killer Queen, " + ChatColor.RED + ChatColor.BOLD + "Another one bites the DUST!"));
                int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                block.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.02);
            }, 0L, 10L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getScheduler().cancelTask(taskId);
                block.getWorld().createExplosion(block.getLocation(), ePower, false, true, player);
                block.setType(Material.AIR);
            }, eDelay * 20);

            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }
        
        //November Rain: Heavy Rain
        if (itemInHand == Material.NETHER_STAR) {
            int radius = config.getInt("NovemberRain.radius", 10);
            int duration = config.getInt("NovemberRain.duration", 5) * 20;
            double damage = config.getDouble("NovemberRain.damage", 2.0);
            int cooldownTime = config.getInt("NovemberRain.cooldown", 600) * 1000;
            if (cooldown.isOnCooldown("NovemberRain", playerId)) {
                long remainingTime = cooldown.getRemainingTime("NovemberRain", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "November Rain is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("NovemberRain", playerId, cooldownTime);
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

            if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }

        //Tusk Act 2: Golden Rectangle Nails
        if (itemInHand == Material.SPIDER_EYE) {
            int cooldownTime = config.getInt("Tusk.cooldown", 15) * 1000;
            double damage = config.getDouble("Tusk.damage", 3.0); 

            if (cooldown.isOnCooldown("Tusk", playerId)) {
                long remainingTime = cooldown.getRemainingTime("Tusk", playerId);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Tusk is on cooldown! You need to wait " + ChatColor.GRAY + ChatColor.BOLD + remainingTime + " second."));
                return;
            }
            cooldown.setCooldown("Tusk", playerId, cooldownTime);
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Snowball tuskProjectile = player.launchProjectile(Snowball.class);
                tuskProjectile.setVelocity(player.getLocation().getDirection().multiply(2));
                tuskProjectile.setShooter(player);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Tusk Act 2: " + ChatColor.BOLD + "CHUMIMI~IN!"));
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if (!tuskProjectile.isDead()) {
                        tuskProjectile.getWorld().getNearbyEntities(tuskProjectile.getLocation(), 0.5, 0.5, 0.5).forEach(entity -> {
                            if (entity instanceof LivingEntity && entity != player) {
                                LivingEntity target = (LivingEntity) entity;
                                target.damage(damage, player);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "Tusk Act 2: " + ChatColor.BOLD + "Hit " + target.getName() + "!"));
                                tuskProjectile.remove();
                            }
                        });
                    } else {
                        Bukkit.getScheduler().cancelTasks(plugin);
                    }
                }, 0L, 1L);
                if (player.getInventory().getItemInMainHand().getAmount() > 1) {
                    player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                } else {
                    player.getInventory().setItemInMainHand(null);
                }
            }
        }
    }
}

package com.krayir5.stands.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.krayir5.stands.utils.LocacacaM;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
@SuppressWarnings("")
public class Locacaca implements Listener{
    private final JavaPlugin plugin;
    public LocacacaM getLocacacaManager() {
        return locacacaM;
    }    
    private final LocacacaM locacacaM;

    public Locacaca(JavaPlugin plugin, LocacacaM locacacaM) {
        this.plugin = plugin;
        this.locacacaM = locacacaM;
    }
    private static final boolean IS_NEW_VERSION;
    static {
        String version = Bukkit.getVersion();
        int index = version.indexOf("(MC: ");
        if (index != -1) {
            String mcVersion = version.substring(index + 5, version.length() - 1);
            String[] parts = mcVersion.split("\\.");
            int major = Integer.parseInt(parts[0]);
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

            IS_NEW_VERSION = (major == 1 && minor >= 17);
        } else {
            IS_NEW_VERSION = false;
        }
    }
    public static boolean isNewVersion() {
        return IS_NEW_VERSION;
    }
    public String niceMojang(String effect) {
        switch (effect.toUpperCase()) {
            case "CONFUSION":
                return isNewVersion() ? "NAUSEA" : "CONFUSION";
    
            case "FAST_DIGGING":
                return isNewVersion() ? "HASTE" : "FAST_DIGGING";
    
            case "INCREASE_DAMAGE":
                return isNewVersion() ? "STRENGTH" : "INCREASE_DAMAGE";
    
            default:
                return effect;
        }
    }

    @EventHandler
    public void imMilkingIt(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.getType() == Material.MILK_BUCKET) {
            UUID uuid = player.getUniqueId();
            FileConfiguration locacacaConfig = locacacaM.getConfig();
            if (!locacacaConfig.contains("players." + uuid)) return;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                List<PotionEffect> effectsToAdd = new ArrayList<>();
                if (locacacaConfig.contains("players." + uuid + ".good_effects")) {
                    Set<String> goodEffects = locacacaConfig.getConfigurationSection("players." + uuid + ".good_effects").getKeys(false);
                    for (String effectName : goodEffects) {
                        PotionEffectType effect = PotionEffectType.getByName(effectName);
                        if (effect != null) {
                            effectsToAdd.add(new PotionEffect(effect, Integer.MAX_VALUE, 0));
                        }
                    }
                }
                if (locacacaConfig.contains("players." + uuid + ".bad_effects")) {
                    Set<String> badEffects = locacacaConfig.getConfigurationSection("players." + uuid + ".bad_effects").getKeys(false);
                    for (String effectName : badEffects) {
                        PotionEffectType effect = PotionEffectType.getByName(effectName);
                        if (effect != null) {
                            effectsToAdd.add(new PotionEffect(effect, Integer.MAX_VALUE, 0));
                        }
                    }
                }
                player.addPotionEffects(effectsToAdd);
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Locacaca's power reasserts itself after drinking milk...");
            }, 2L);       
        }
    }

    @EventHandler
    public void meEatLocacaca(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item.getType() != Material.CARROT || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "locacaca");

        if (data.has(key, PersistentDataType.STRING)) {
            String type = data.get(key, PersistentDataType.STRING);
            UUID uuid = player.getUniqueId();
            FileConfiguration locacacaConfig = locacacaM.getConfig();

            if (type.equals("true")) {
                PotionEffectType[] goodEffects = {
                    PotionEffectType.SPEED,
                    PotionEffectType.getByName(niceMojang("INCREASE_DAMAGE")),
                    PotionEffectType.REGENERATION,
                    PotionEffectType.getByName(niceMojang("FAST_DIGGING")),
                    PotionEffectType.HEALTH_BOOST
                };

                PotionEffectType[] badEffects = {
                    PotionEffectType.BLINDNESS,
                    PotionEffectType.SLOW,
                    PotionEffectType.getByName(niceMojang("CONFUSION")),
                    PotionEffectType.WEAKNESS,
                    PotionEffectType.HUNGER
                };

                PotionEffectType good = goodEffects[new Random().nextInt(goodEffects.length)];
                PotionEffectType bad = badEffects[new Random().nextInt(badEffects.length)];

                player.addPotionEffect(new PotionEffect(good, Integer.MAX_VALUE, 0));
                player.addPotionEffect(new PotionEffect(bad, Integer.MAX_VALUE, 0));

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GREEN + "You feel empowered by " + ChatColor.GOLD + niceMojang(good.getName()) + ChatColor.RED + " but cursed with " + ChatColor.DARK_RED + niceMojang(bad.getName())));
                locacacaConfig.set("players." + uuid + ".good_effects." + niceMojang(good.getName()), true);
                locacacaConfig.set("players." + uuid + ".bad_effects." + niceMojang(bad.getName()), true);
                locacacaM.save();
            }

            if (type.equals("new")) {
                if (locacacaConfig.contains("players." + uuid + ".bad_effects")) {
                    Set<String> badEffects = locacacaConfig.getConfigurationSection("players." + uuid + ".bad_effects").getKeys(false);
                    if (!badEffects.isEmpty()) {
                        List<String> badList = new ArrayList<>(badEffects);
                        String randomEffectName = badList.get(new Random().nextInt(badList.size()));

                        PotionEffectType badEffect = PotionEffectType.getByName(randomEffectName);
                        if (badEffect != null) {
                            player.removePotionEffect(badEffect);
                            locacacaConfig.set("players." + uuid + ".bad_effects." + randomEffectName, null);
                            locacacaM.save();
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GOLD + "Locacaca cleansed one curse: " + ChatColor.RED + randomEffectName + ChatColor.GOLD + "!"));
                        } else {
                            player.sendMessage(ChatColor.RED + "Failed to find effect type for: " + randomEffectName);
                        }
                    } else {
                        player.sendMessage(ChatColor.GRAY + "You had no curses to remove.");
                    }
                } else {
                    player.sendMessage(ChatColor.GRAY + "You had no curses to remove.");
                }
            }
        }
    }
}

package com.krayir5.stands.utils.GUIS;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import com.krayir5.stands.Plugin;
import com.krayir5.stands.utils.GUIM.Button;
import com.krayir5.stands.utils.GUIM.ItemC;
import com.krayir5.stands.utils.GUIM.Menu;
import com.krayir5.stands.utils.HeadCreator;
@SuppressWarnings("")
public class StandUMenu extends Menu {

    private final Player player;
    private final UUID uuid;
    File standFile = new File(Plugin.inst().getDataFolder(), "stands.yml");
    FileConfiguration standConfig = YamlConfiguration.loadConfiguration(standFile);

    public StandUMenu(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.createInventory(45, "§4Stand Upgrade Menu");
        setupGUI();
    }

    public void open() {
        displayTo(player);
    }
    

    private void setupGUI() {
        String standName = standConfig.getString("players." + uuid + ".stand", "Unknown");
    
        Map<String, String[]> standAbilities = new HashMap<>();
        standAbilities.put("Star Platinum", new String[]{"Punch Throw", "Star Finger", "Time Stop"});
        standAbilities.put("The World", new String[]{"Punch Throw", "Knife Throw", "Time Stop"});
        standAbilities.put("Crazy Diamond", new String[]{"Punch Throw", "Heal", "Selfheal"});
        standAbilities.put("Magician Red", new String[]{"Punch Throw", "Cross Fire Hurricane"});
        standAbilities.put("Hermit Purple", new String[]{"Overdrive"});
        standAbilities.put("Killer Queen", new String[]{"Punch Throw", "Air Bomb Transmutation", "Another One Bites the Dust"});
        standAbilities.put("November Rain", new String[]{"November Rain"});
        standAbilities.put("Silver Chariot", new String[]{"Hora Rush"});
        standAbilities.put("Tusk", new String[]{"Golden Rectangle Nails", "Spatial Wormhole"});
        standAbilities.put("Hierophant Green", new String[]{"Punch Throw", "Emerald Splash"});
        standAbilities.put("Heavens Door", new String[]{"Book Transmutation"});
        standAbilities.put("Sexy Pistols", new String[]{"Bullet Control"});
        standAbilities.put("Soft & Wet", new String[]{"Punch Throw", "Bubble Generation"});
        standAbilities.put("Purple Haze", new String[]{"Punch Throw", "Killer Virus Infection"});
        standAbilities.put("Spice Girl", new String[]{"Punch Throw"});
        standAbilities.put("Whitesnake", new String[]{"Punch Throw", "DISC Extraction"});
        standAbilities.put("C-MOON", new String[]{"Punch Throw", "Surface Inversion"});
        standAbilities.put("Made in Heaven", new String[]{"Punch Throw", "Time Acceleration"});
        standAbilities.put("Achtung Baby", new String[]{"Invisibility"});
        standAbilities.put("Red Hot Chili Pepper", new String[]{"Punch Throw", "Lightning Strike"});
        standAbilities.put("Diver Down", new String[]{"Punch Throw"});
        standAbilities.put("Paisley Park", new String[]{"Punch Throw", "Guidance"});
        standAbilities.put("Speed King", new String[]{"Punch Throw", "Heat Accumulation"});
        standAbilities.put("Ozon Baby", new String[]{"Pressure Manipulation"});
    
        String[] abilityNames = standAbilities.getOrDefault(standName, new String[]{});
    
        String[] abilityKeys = {"Ability1", "Ability2", "Ability3"};
    
        int[][] abilitySlots = {
            {29, 20, 11, 2},
            {31, 22, 13, 4},
            {33, 24, 15, 6}
        };
    
        int[] upgradeButtons = {38, 40, 42}; 
        int maxAbilities = abilityNames.length;
    
        for (int i = 0; i < maxAbilities; i++) {
            String abilityName = abilityNames[i];
            String abilityKey = abilityKeys[i];
            
            int level = standConfig.getInt("players." + uuid + ".standXP." + abilityKey + ".Level", 0);
            boolean isUnlocked = (i == 0) || (i == 1 && standConfig.getInt("players." + uuid + ".standXP.Stand.Level", 1) >= 2) || (i == 2 && standConfig.getInt("players." + uuid + ".standXP.Stand.Level", 1) >= 3);
    
            for (int j = 0; j < 4; j++) {
                int slot = abilitySlots[i][j];
                if (!isUnlocked) {
                    setInvItem(slot, ItemC.of(Material.BLACK_STAINED_GLASS_PANE).name("§cAbility locked.").get());
                } else if (j < level) {
                    setInvItem(slot, ItemC.of(Material.LIME_STAINED_GLASS_PANE).name("§a" + abilityName + " Level " + (j + 1)).get());
                } else {
                    setInvItem(slot, ItemC.of(Material.GRAY_STAINED_GLASS_PANE).name("§7Locked level.").get());
                }
            }
    
            if (isUnlocked) {
                if (level >= 4) {
                    setInvItem(upgradeButtons[i], ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFmMjczMzdkMWUxYTk3YjY3YmYzM2Q0NTE3Y2UwYzIxZGU4MmI3MGUzYmZkNDVhMjIyMDFhNGQwNTZiNjAwYyJ9fX0="))
                            .name("§a" + abilityName + " is at maximum!")
                            .lore("§7This ability is already at maximum level.")
                            .get()
                    );
                } else {
                    addButton(new Button(upgradeButtons[i]) {
                        @Override
                        public ItemStack getItem() {
                            return ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0="))
                                    .name("§6Upgrade " + abilityName)
                                    .lore("§7Required XP: §e" + 1000 * level, "§aClick to upgrade!")
                                    .get();
                        }
    
                        @Override
                        public void onClick(Player p, ClickType clickType) {
                            upgradeAbility(abilityKey, abilityName);
                        }
                    });
                }
            } else {
                
                setInvItem(upgradeButtons[i], ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==")).name("§cThis ability is locked!").get());
            }
        }    

        int standLevel = standConfig.getInt("players." + uuid + ".standXP.Stand.Level", 1);
        if (standLevel >= 3) {
            setInvItem(35, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFmMjczMzdkMWUxYTk3YjY3YmYzM2Q0NTE3Y2UwYzIxZGU4MmI3MGUzYmZkNDVhMjIyMDFhNGQwNTZiNjAwYyJ9fX0="))
                .name("§aYour Stand is at maximum level")
                .lore("§7You have reached the maximum Stand Level.")
                .get()
            );
        } else {
            addButton(new Button(35) {
                @Override
                public ItemStack getItem() {
                    return ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzMzJiNzcwYTQ4NzQ2OTg4NjI4NTVkYTViM2ZlNDdmMTlhYjI5MWRmNzY2YjYwODNiNWY5YTBjM2M2ODQ3ZSJ9fX0="))
                        .name("§6Upgrade your Stand")
                        .lore("§7Required XP:§e "+ 5000 * standLevel, "§aClick to upgrade!")
                        .get();
                }
    
                @Override
                public void onClick(Player p, ClickType clickType) {
                    upgradeStandLevel();
                }
            });
        }

        if (standName.equals("Whitesnake") || standName.equals("C-MOON")) {
            addButton(new Button(43) {
                @Override
                public ItemStack getItem() {
                    int standXP = standConfig.getInt("players." + uuid + ".standXP.Stand.XP", 0);

                    if (standName.equals("Whitesnake")) {
                        if (standLevel >= 3 && standXP >= 200) {
                            return ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmRlOTZkMjgzOTA5NDVhZDE1NGFhNTVkZTYwODVmZGVlMWFkYzljMTdkZDMwMjAzMTNmMWIzZjFlMTZjYjY2YyJ9fX0="))
                                .name("§6Evolve to C-MOON")
                                .lore("§7Required XP: §e200", "§aClick to evolve!")
                                .get();
                        } else {
                            return ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ=="))
                                .name("§cYou cannot evolve yet!")
                                .lore("§7You need at least §e200 XP §7and Stand Level 3.")
                                .get();
                        }
                     } else if (standName.equals("C-MOON")) {
                        if (standLevel >= 3 && standXP >= 300) {
                            return ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2IxZjY5Y2ZlM2U3NjdmMjk2Y2I2MzA0NmI2MDU3YzhjZWU0Y2VlZDQ2ZWQxZmMzNDhlZGE2MGJhMDY1YTU0In19fQ=="))
                                .name("§6Evolve to Made in Heaven")
                                .lore("§7Required XP: §e300", "§aClick to evolve!")
                                .get();
                        } else {
                            return ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ=="))
                                .name("§cYou cannot evolve yet!")
                                .lore("§7You need at least §e300 XP §7and Stand Level 3.")
                                .get();
                        }
                    }
                    return null;
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                    evolveStand();
                }
            });
        }

        int standXP = standConfig.getInt("players." + uuid + ".standXP.Stand.XP", 0);
        setInvItem(44, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTcxYTIyODVjOTFjNmM3Mjc0NzYwNDgxOWVlNTIyM2E5MGFhNTFlNmU3OWU0ZjlhZjY2MjhlYzhmMGRkN2RmYyJ9fX0="))
            .name("§bStand Info")
            .lore("§7Stand: §e" + standName, "§7Level: §e" + standLevel, "§7Stand XP: §a" + standXP)
            .get()
        );
    }
    
    private void upgradeStandLevel() {
        int totalXP = standConfig.getInt("players." + uuid + ".standXP.Stand.XP", 0);
        int standLevel = standConfig.getInt("players." + uuid + ".standXP.Stand.Level", 1);
        int rXP = 5000 * standLevel;
        
        if (standLevel >= 3) {
            player.sendMessage("§cThis ability is already at maximum level!");
            return;
        }
        
        if (totalXP < rXP) {
            int xpNeeded = rXP - totalXP;
            player.sendMessage("§cInsufficient XP! §e" + xpNeeded + " XP §crequired to level up.");
            return;
        }
        
        standConfig.set("players." + uuid + ".standXP.Stand.XP", totalXP - rXP);
        standConfig.set("players." + uuid + ".standXP.Stand.Level", standLevel + 1);
        int aLevel = standLevel + 1;
        standConfig.set("players." + uuid + ".standXP.Ability" + aLevel + ".Level", 1);
        
        try {
            standConfig.save(standFile);
        } catch (IOException | SecurityException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
        
        player.sendMessage("§aStand level successfully upgraded! New level: " + (standLevel + 1));
        setupGUI();
        displayTo(player);
    }
    
    private void upgradeAbility(String abilityKey, String abilityName) {
        int currentLevel = standConfig.getInt("players." + uuid + ".standXP." + abilityKey + ".Level", 0);
        int currentXP = standConfig.getInt("players." + uuid + ".standXP." + abilityKey + ".XP", 0);
        int rXP = 1000 * currentLevel;
    
        if (currentLevel >= 4) {
            player.sendMessage("§c" + abilityName + " is already at maximum level!");
            return;
        }
    
        if (currentXP < rXP) {
            int xpNeeded = rXP - currentXP;
            player.sendMessage("§cInsufficient XP! At least §e" + xpNeeded + " XP §crequired to upgrade " + abilityName + ".");
            return;
        }
    
        standConfig.set("players." + uuid + ".standXP." + abilityKey + ".XP", currentXP - rXP);
        standConfig.set("players." + uuid + ".standXP." + abilityKey + ".Level", currentLevel + 1);
    
        try {
            standConfig.save(standFile);
        } catch (IOException | SecurityException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    
        player.sendMessage("§a" + abilityName + " successfully upgraded! New level: " + (currentLevel + 1));
        setupGUI();
        displayTo(player);
    }   
    
    private void evolveStand() {
        String standName = standConfig.getString("players." + uuid + ".stand", "Unknown");
        int standLevel = standConfig.getInt("players." + uuid + ".standXP.Stand.Level", 1);
        int standXP = standConfig.getInt("players." + uuid + ".standXP.Stand.XP", 0);
        if (standName.equals("Whitesnake")) {
            if (standLevel >= 3 && standXP >= 200) {
                standConfig.set("players." + uuid + ".stand", "C-MOON");
                standConfig.set("players." + uuid + ".standXP.Stand.XP", 0);
                standConfig.set("players." + uuid + ".standXP.Stand.Level", 1);
                standConfig.set("players." + uuid + ".standXP.Ability1.Level", 1);
                standConfig.set("players." + uuid + ".standXP.Ability1.XP", 0);
                standConfig.set("players." + uuid + ".standXP.Ability2.Level", 0);
                standConfig.set("players." + uuid + ".standXP.Ability2.XP", 0);
                try {
                    standConfig.save(standFile);
                } catch (IOException e) {
                    Bukkit.getLogger().severe(e.getMessage());
                }
    
                player.sendMessage("§aYour Stand has evolved into §eC-MOON!");
            } else {
                player.sendMessage("§cYou need at least 200 XP and Stand Level 3 to evolve.");
            }
        } else if (standName.equals("C-MOON")) {
            if (standLevel >= 3 && standXP >= 300) {
                standConfig.set("players." + uuid + ".stand", "Made in Heaven");
                standConfig.set("players." + uuid + ".standXP.Stand.XP", 0);
                standConfig.set("players." + uuid + ".standXP.Stand.Level", 1);
                standConfig.set("players." + uuid + ".standXP.Ability1.Level", 1);
                standConfig.set("players." + uuid + ".standXP.Ability1.XP", 0);
                standConfig.set("players." + uuid + ".standXP.Ability2.Level", 0);
                standConfig.set("players." + uuid + ".standXP.Ability2.XP", 0);
                try {
                    standConfig.save(standFile);
                } catch (IOException e) {
                    Bukkit.getLogger().severe(e.getMessage());
                }
                player.sendMessage("§aYour Stand has evolved into §eMade in Heaven!");
            } else {
                player.sendMessage("§cYou need at least 300 XP and Stand Level 3 to evolve.");
            }
        }
        setupGUI();
        displayTo(player);
    }    
}
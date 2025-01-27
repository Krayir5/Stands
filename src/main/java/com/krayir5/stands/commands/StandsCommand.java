package com.krayir5.stands.commands;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
@SuppressWarnings("")
public class StandsCommand implements Listener, CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komut yalnızca oyuncular tarafından kullanılabilir.");
            return true;
        }
        Player player = (Player) sender;
        openssGUI(player);
        return true;
    }

    public static ItemStack cch(String tV) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        NBT.modify(head, nbt -> {
            ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");
            skullOwnerCompound.setUUID("Id", UUID.randomUUID());
            skullOwnerCompound.getOrCreateCompound("Properties")
                .getCompoundList("textures")
                .addCompound()
                .setString("Value", tV);
        });
        return head;
    }

    private void openssGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Stands Menu");

        ItemStack s1 = cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM1NDRjYTFlMjA2NTM2N2VlNGNkODk0MzMxOTQwODRmNjAxZGM4MzliNmUxODA4ZDQ1MDY2ZjNmM2MwNDBjZCJ9fX0=");
        ItemMeta m1 = s1.getItemMeta();
        m1.setDisplayName("Star Platinum");
        m1.setLore(Arrays.asList("§8§oPressing F will stop time.", "§8§oRight clicking an entity will throw punches to it.", "§8§oWhile holding shift right clicking will activate Star Finger.", ""));
        s1.setItemMeta(m1);

        ItemStack s2 = cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmYxZmM2ZWJjNTQ5YzZkYTQ4MDdiZDMwZmM2ZTQ3YmY0YmRiNTE2ZjI1Njg2NDg5MWEzMWU2ZjZhYTI1MjdiMCJ9fX0=");
        ItemMeta m2 = s2.getItemMeta();
        m2.setDisplayName("The World");
        m2.setLore(Arrays.asList("§8§oPressing F will stop time.", "§8§oRight clicking an entity will throw punches to it.", "§8§oWhile holding shift right clicking will cause to throw knifes.", ""));
        s2.setItemMeta(m2);

        ItemStack s3 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVhYmM5NzFlNTVlNTU0YmY2MzZiNGRhZTRjNzViNTkxNThlYjFhNzk2MTE0M2VmZDY5YWZmNWIxZmQ2NTU4NCJ9fX0="));
        ItemMeta m3 = s3.getItemMeta();
        m3.setDisplayName("Crazy Diamond");
        m3.setLore(Arrays.asList("§8§oPressing F will selfheal.", "§8§oRight clicking an entity will throw heal it.", "§8§oWhile holding shift right clicking will cause to throw punches.", ""));
        s3.setItemMeta(m3);

        ItemStack s4 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTliNzBhNWUwNDNjMTAyYjM1NjcxNjdiMDRiODNhNzMyNDU2MzI0ODM2MjY2YzI0ZTY2NjQ3ZWFjM2E1ZjMwIn19fQ=="));
        ItemMeta m4 = s4.getItemMeta();
        m4.setDisplayName("Magician Red");
        m4.setLore(Arrays.asList("§8§oPressing F will send fireballs.", "§8§oRight clicking an entity will throw punches to it.", ""));
        s4.setItemMeta(m4);

        ItemStack s5 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIzODA0ZjM1ZDBmMTg5ZGYxOGFhNTM0YWQ4MjJmZWQwMWZlYzYzZWYxZmNiNjMwOTRhMjgxOGEwMzMyYmJiIn19fQ=="));
        ItemMeta m5 = s5.getItemMeta();
        m5.setDisplayName("Hermit Purple");
        m5.setLore(Arrays.asList("§8§oPressing F will pull entities towards you.", ""));
        s5.setItemMeta(m5);

        ItemStack s6 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMwNzYzZDU3YTVmMTVhM2ExM2YwYzFlYmEzODI1NjMwYWZjYjk0ZTQ3ZjhmZjAyYjBlNDhmOTA1ZmY2OTA2MiJ9fX0="));
        ItemMeta m6 = s6.getItemMeta();
        m6.setDisplayName("Killer Queen");
        m6.setLore(Arrays.asList("§8§oPressing F will throw a Bubble that's explosive.", "§8§oRight clicking an entity will throw punches to it.", "§8§oRight clicking a block will turn it into a explosive.", ""));
        s6.setItemMeta(m6);

        ItemStack s7 = new ItemStack(Material.WATER_BUCKET);
        ItemMeta m7 = s7.getItemMeta();
        m7.setDisplayName("November Rain");
        m7.setLore(Arrays.asList("§8§oPressing F will create a rain that contains small area but a powerfull one.", ""));
        s7.setItemMeta(m7);

        ItemStack s8 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRiYjIyMDUxYjFjNjQ2OWNmNjczZTlhMWZjYTIyOWEwZmMyMjQyNTk5ZjlkZjI5NGM2YjFlNDZlNTRlYTMzYSJ9fX0="));
        ItemMeta m8 = s8.getItemMeta();
        m8.setDisplayName("Silver Chariot");
        m8.setLore(Arrays.asList("§8§oRight clicking an entity will shiver his sword to his enemies.", ""));
        s8.setItemMeta(m8);

        ItemStack s9 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg4NmEzYjFmYjdhNWEzMjEzNzA4NzlkY2Q2MGUxNWMxYWVjMzI2NzllMTE5YzAyOTY4MmUzODkxNDc4NzM5ZCJ9fX0="));
        ItemMeta m9 = s9.getItemMeta();
        m9.setDisplayName("Tusk");
        m9.setLore(Arrays.asList("§8§oPressing F will cause to throw a powerfull nail.", ""));
        s9.setItemMeta(m9);

        ItemStack s10 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzAzZDA3ZTc0MmQ2ZDE3MmUzNmU4NmQzZTc1MDE0YTRlMzE5ZmU5NGRkN2Y5YzJkMGNlZDgxNDVlMWY2Y2EzOCJ9fX0="));
        ItemMeta m10 = s10.getItemMeta();
        m10.setDisplayName("Hierophant Green");
        m10.setLore(Arrays.asList("§8§oPressing F will send emeralds.", "§8§oRight clicking an entity will throw punches to it.", ""));
        s10.setItemMeta(m10);

        ItemStack s11 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA0ODdlMDU1OWEwNmE0MTcwOGQ5MGE3M2E5ZWU3NTQ0YjRkMDlmOTM2ZTIyYmJmYjEyMmZmMTNmY2M1MDczOSJ9fX0="));
        ItemMeta m11 = s11.getItemMeta();
        m11.setDisplayName("Heaven's Door");
        m11.setLore(Arrays.asList("§8§oRight click an entity to stun him and give him weakness.", ""));
        s11.setItemMeta(m11);

        ItemStack s12 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWE2ZjFiOTkzNmFlYzNhOGY0MjUzNjEzYzJmMzgwNWQ2ZmI0MWM1ZGE0MGViOTZhNDMyNWMyNDNhMTE3OGE2In19fQ=="));
        ItemMeta m12 = s12.getItemMeta();
        m12.setDisplayName("Sexy Pistols");
        m12.setLore(Arrays.asList("§8§oPressing F will shoots bullet that track your enemy.", ""));
        s12.setItemMeta(m12);

        ItemStack s13 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjUwYjNmZjQ1ZjhhNDhlMjE2ZTEzMTM0NGYyNTYxMGUyY2JiZWZhZTU0MmM1Y2FkOWRlNzc1ZjljZjkwNjE0In19fQ=="));
        ItemMeta m13 = s13.getItemMeta();
        m13.setDisplayName("Soft & Wet");
        m13.setLore(Arrays.asList("§8§oPressing F will send a bubble that gives entity a negative effect.", "§8§oRight clicking to enemy will throw punches to it.", ""));
        s13.setItemMeta(m13);

        ItemStack s14 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg4Yjg4ZjRmOTAzNmI5N2IxMTViMjVjMjhkODIwOTI0Y2Q2NmJhNjEzZjcxZDMzZDJmNmQwYWJmZGRjZDJiNyJ9fX0="));
        ItemMeta m14 = s14.getItemMeta();
        m14.setDisplayName("Purple Haze");
        m14.setLore(Arrays.asList("§8§oPressing F will create a poisionus gas.", "§8§oRight clicking an entity will throw punches to it.", ""));
        s14.setItemMeta(m14);

        ItemStack s15 = (cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRiY2E5MmYzZjE0ZjlkZWE4MmM3OWM3YTc4OGFmMWY2MzViZGY4MWYzOTg1NzY3NmJkNDU5ZWMxMzg4NjY4NSJ9fX0="));
        ItemMeta m15 = s15.getItemMeta();
        m15.setDisplayName("Spice Girl");
        m15.setLore(Arrays.asList("§8§oRight clicking an entity will throw punches to it.", "§8§oPassively you can't take fall damage.", ""));
        s15.setItemMeta(m15);

        gui.setItem(0, s1);
        gui.setItem(1, s2);
        gui.setItem(2, s3);
        gui.setItem(3, s4);
        gui.setItem(4, s5);
        gui.setItem(5, s6);
        gui.setItem(6, s7);
        gui.setItem(7, s8);
        gui.setItem(8, s9);
        gui.setItem(9, s10);
        gui.setItem(10, s11);
        gui.setItem(11, s12);
        gui.setItem(12, s13);
        gui.setItem(13, s14);
        gui.setItem(14, s15);

        player.openInventory(gui);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getView().getTitle().equals("Stands Menu")){
            event.setCancelled(true);
        }
    }
}
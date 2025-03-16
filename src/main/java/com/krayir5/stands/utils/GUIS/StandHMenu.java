package com.krayir5.stands.utils.GUIS;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.krayir5.stands.utils.GUIM.ItemC;
import com.krayir5.stands.utils.GUIM.Menu;
import com.krayir5.stands.utils.HeadCreator;

public class StandHMenu extends Menu {

    private final Player player;

    public StandHMenu(Player player) {
        this.player = player;
    }

    public void open() {
        this.createInventory(27, "Stands Menu");
        
        setInvItem(0, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM1NDRjYTFlMjA2NTM2N2VlNGNkODk0MzMxOTQwODRmNjAxZGM4MzliNmUxODA4ZDQ1MDY2ZjNmM2MwNDBjZCJ9fX0="))
            .name("§bStar Platinum")
            .lore("§8§oPressing F will stop time.", "§8§oRight clicking an entity will throw punches to it.", "§8§oWhile holding shift right clicking will activate Star Finger.", "")
            .get()
        );

        setInvItem(1, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmYxZmM2ZWJjNTQ5YzZkYTQ4MDdiZDMwZmM2ZTQ3YmY0YmRiNTE2ZjI1Njg2NDg5MWEzMWU2ZjZhYTI1MjdiMCJ9fX0="))
            .name("§bThe World")
            .lore("§8§oPressing F will stop time.", "§8§oRight clicking an entity will throw punches to it.", "§8§oWhile holding shift right clicking will cause to throw knifes.", "")
            .get()
        );

        setInvItem(2, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGVhYmM5NzFlNTVlNTU0YmY2MzZiNGRhZTRjNzViNTkxNThlYjFhNzk2MTE0M2VmZDY5YWZmNWIxZmQ2NTU4NCJ9fX0="))
            .name("§bCrazy Diamond")
            .lore("§8§oPressing F will selfheal.", "§8§oRight clicking will cause to throw punches.", "§8§oWhile holding shift right clicking an entity will heal it.", "")
            .get()
        );

        setInvItem(3, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTliNzBhNWUwNDNjMTAyYjM1NjcxNjdiMDRiODNhNzMyNDU2MzI0ODM2MjY2YzI0ZTY2NjQ3ZWFjM2E1ZjMwIn19fQ=="))
            .name("§bMagician Red")
            .lore("§8§oPressing F will send fireballs.", "§8§oRight clicking an entity will throw punches to it.", "")
            .get()
        );

        setInvItem(4, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIzODA0ZjM1ZDBmMTg5ZGYxOGFhNTM0YWQ4MjJmZWQwMWZlYzYzZWYxZmNiNjMwOTRhMjgxOGEwMzMyYmJiIn19fQ=="))
            .name("§bHermit Purple")
            .lore("§8§oPressing F will pull entities towards you.", "")
            .get()
        );

        setInvItem(5, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMwNzYzZDU3YTVmMTVhM2ExM2YwYzFlYmEzODI1NjMwYWZjYjk0ZTQ3ZjhmZjAyYjBlNDhmOTA1ZmY2OTA2MiJ9fX0="))
            .name("§bKiller Queen")
            .lore("§8§oPressing F will throw a Bubble that's explosive.", "§8§oRight clicking an entity will throw punches to it.", "§8§oRight clicking a block will turn it into a explosive.", "")
            .get()
        );

        setInvItem(6, ItemC.of(new ItemStack(Material.WATER_BUCKET))
            .name("§bNovember Rain")
            .lore("§8§oPressing F will create a rain that contains small area but a powerful one.", "")
            .get()
        );

        setInvItem(7, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRiYjIyMDUxYjFjNjQ2OWNmNjczZTlhMWZjYTIyOWEwZmMyMjQyNTk5ZjlkZjI5NGM2YjFlNDZlNTRlYTMzYSJ9fX0="))
            .name("§bSilver Chariot")
            .lore("§8§oRight clicking an entity will shiver his sword to his enemies.", "")
            .get()
        );

        setInvItem(8, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg4NmEzYjFmYjdhNWEzMjEzNzA4NzlkY2Q2MGUxNWMxYWVjMzI2NzllMTE5YzAyOTY4MmUzODkxNDc4NzM5ZCJ9fX0="))
            .name("§bTusk")
            .lore("§8§oPressing F will cause to throw a powerful nail.", "")
            .get()
        );

        setInvItem(9, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzAzZDA3ZTc0MmQ2ZDE3MmUzNmU4NmQzZTc1MDE0YTRlMzE5ZmU5NGRkN2Y5YzJkMGNlZDgxNDVlMWY2Y2EzOCJ9fX0="))
           .name("§bHierophant Green")
            .lore("§8§oPressing F will send emeralds.", "§8§oRight clicking an entity will throw punches to it.", "")
           .get()
        );

        setInvItem(10, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTA0ODdlMDU1OWEwNmE0MTcwOGQ5MGE3M2E5ZWU3NTQ0YjRkMDlmOTM2ZTIyYmJmYjEyMmZmMTNmY2M1MDczOSJ9fX0="))
            .name("§bHeaven's Door")
            .lore("§8§oRight click an entity to stun him and give him weakness.", "")
            .get()
        );

        setInvItem(11, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU0M2FlNzNhMmMwMDI2YmMzMTZkZThmM2QwMDMxYzhiMmI5NDNlZDFlMDM0NDkzNmFjOTg3NWQ2MzQ3Y2E3MiJ9fX0="))
            .name("§bSexy Pistols")
            .lore("§8§oPressing F will shoots bullet that track your enemy.", "")
            .get()
        );

        setInvItem(12, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjUwYjNmZjQ1ZjhhNDhlMjE2ZTEzMTM0NGYyNTYxMGUyY2JiZWZhZTU0MmM1Y2FkOWRlNzc1ZjljZjkwNjE0In19fQ=="))
            .name("§bSoft & Wet")
            .lore("§8§oPressing F will send a bubble that gives entity a negative effect.", "§8§oRight clicking to enemy will throw punches to it.", "")
            .get()
        );

        setInvItem(13, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg4Yjg4ZjRmOTAzNmI5N2IxMTViMjVjMjhkODIwOTI0Y2Q2NmJhNjEzZjcxZDMzZDJmNmQwYWJmZGRjZDJiNyJ9fX0="))
            .name("§bPurple Haze")
            .lore("§8§oPressing F will create a poisonous gas.", "§8§oRight clicking an entity will throw punches to it.", "")
            .get()
        );

        setInvItem(14, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRiY2E5MmYzZjE0ZjlkZWE4MmM3OWM3YTc4OGFmMWY2MzViZGY4MWYzOTg1NzY3NmJkNDU5ZWMxMzg4NjY4NSJ9fX0="))
            .name("§bSpice Girl")
            .lore("§8§oRight clicking an entity will throw punches to it.", "§8§oPassively you can't take fall damage.", "")
            .get()
        );

        setInvItem(15, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTYyYzIwNzIyY2E2ZDYxNmJhZjBlZmJlNmMyYzEzNTkzN2IyMWRkMGJjYzNiMDEyOTk1ZjE0YzMyZWE0MWU1In19fQ=="))
            .name("§bWhitesnake")
            .lore("§8§oRight clicking an entity will throw punches to it.", "§8§oWhile pressing shift, right clicking a player will reset his stand ONLY ONCE.", "")
            .get()
        );

        setInvItem(16, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmRlOTZkMjgzOTA5NDVhZDE1NGFhNTVkZTYwODVmZGVlMWFkYzljMTdkZDMwMjAzMTNmMWIzZjFlMTZjYjY2YyJ9fX0="))
            .name("§bC-MOON")
            .lore("§8§oRight clicking an entity will throw punches to it.", "§8§oWhile pressing shift, right clicking a player will heavily damage it.", "")
            .get()
        );

        setInvItem(17, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2IxZjY5Y2ZlM2U3NjdmMjk2Y2I2MzA0NmI2MDU3YzhjZWU0Y2VlZDQ2ZWQxZmMzNDhlZGE2MGJhMDY1YTU0In19fQ=="))
            .name("§bMade in Heaven")
            .lore("§8§oRight clicking an entity will throw punches to it.", "§8§oPressing F will accelerate time and give you a speed boost.", "§8§oPassively you'll have a Speed 2 boost.", "")
            .get()
        );

        setInvItem(18, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2IxZTAxN2I1ODQxYjk4NTc3YTJiOGVkOWJmMDIzZDNiZjE0OWQ3ZWY2Y2RkY2VmY2FkZjdiNGIyN2MzMWIzMSJ9fX0="))
            .name("§bAchtung Baby")
            .lore("§8§oPressing F will give you invisibility.", "")
            .get()
        );

        setInvItem(19, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzMxMjE0YTFkYzRiYmI4MTJmZDZkNTI2YTNiMTBkMmExMmZmYjJkZDMwMzk5YTczZmM4Nzk1YjhlYWEwMWUyYyJ9fX0="))
            .name("§bRed Hot Chilli Pepper")
            .lore("§8§oRight clicking an entity will throw punches to it.", "§8§oPressing F will create lightning around you.", "§8§oPassively you'll not get damaged by any lightning.", "")
            .get()
        );

        setInvItem(20, ItemC.of(HeadCreator.cch("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM4ZWU4MDdkYTVkYTIxYzExNTI2MGRiNmViNzEwNTFlNTc0YWM0MzM3NTkyNWNmZWMwMTAwMTJjM2I4Y2I2OCJ9fX0="))
            .name("§bDiver Down")
            .lore("§8§oRight clicking an entity will throw punches to it.", "§8§oPassively you can breathe underwater.", "")
            .get()
        );
        
        this.displayTo(player);
    }
}
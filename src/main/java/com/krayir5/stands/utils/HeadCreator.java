package com.krayir5.stands.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;

public class HeadCreator {
    
    private static boolean vController() {
        String[] versionParts = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        int minor = Integer.parseInt(versionParts[1]);
        if(minor > 20) return true;
        if(minor == 20 && versionParts.length > 2) {
            int patch = Integer.parseInt(versionParts[2]);
            return patch > 4;
        }
        return false;
    }
    
    public static ItemStack cch(String tV) {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (vController()) {
            NBT.modifyComponents(head, nbt -> {
                ReadWriteNBT profile = nbt.getOrCreateCompound("minecraft:profile");
                profile.setUUID("id", UUID.randomUUID());
                ReadWriteNBT props = profile.getCompoundList("properties").addCompound();
                props.setString("name", "textures");
                props.setString("value", tV);
            });
        } 
        else {
            NBT.modify(head, nbt -> {
                ReadWriteNBT skullOwnerCompound = nbt.getOrCreateCompound("SkullOwner");
                skullOwnerCompound.setUUID("Id", UUID.randomUUID());
                skullOwnerCompound.getOrCreateCompound("Properties")
                    .getCompoundList("textures")
                    .addCompound()
                    .setString("Value", tV);
            });
        }
        return head;
    }
}

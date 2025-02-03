package com.krayir5.stands.utils.GUIM;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemC {

    ItemStack itemStack;
    ItemMeta meta;

    public ItemC(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.meta = itemStack.getItemMeta();
    }
    public static ItemC of(ItemStack itemStack) {
        return new ItemC(itemStack);
    }
    public static ItemC of(Material material) {
        return new ItemC(new ItemStack(material));
    }
    //Get
    public ItemStack get() {
        if(meta != null) {
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    //Modify
    public ItemC amount(int amount) {
        if(itemStack != null) {
            itemStack.setAmount(amount);
        }
        return this;
    }
    public ItemC name(String name) {
        if(meta != null && name != null) {
            meta.setDisplayName(Hex.color(name));
        }
        return this;
    }
    public ItemC lore(String... lore) {
        if(meta != null) {
            List<String> loreFix = new ArrayList<>();
            for(String line : lore) {
                loreFix.add(Hex.color(line));
            }
            meta.setLore(loreFix);
        }
        return this;
    }
    public ItemC lore(List<String> lore) {
        return lore(lore.toArray(new String[0]));
    }
    public ItemC enchant(Enchantment enchantment, int level) {
        if(meta != null) {
            meta.addEnchant(enchantment, level, true);
        }
        return this;
    }
    public ItemC hideFlag(ItemFlag... flags) {
        if(meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }
    public ItemC unbreakable(boolean unbreakable) {
        if(meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }
}
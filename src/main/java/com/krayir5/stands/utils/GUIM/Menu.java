package com.krayir5.stands.utils.GUIM;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.krayir5.stands.Plugin;

public class Menu {

    private final JavaPlugin plugin = Plugin.inst();

    /*
     * Buttons
     */
    final private List<Button> buttons = new ArrayList<>();

    /*
     * Size of GUI
     */
    private int size = 27;

    /*
     * Title of GUI
     */
    private String title = "Default";

    /*
     * GUI inventory
     */
    private Inventory inventory = null;

    /*
     * Run on close calls
     */
    private Consumer<Inventory> closeCallback = inv -> {};

    /*
     * Empty slots modifiable
     */
    private boolean emptySlotsModifiable = false;

    //-------------------------------------
    //   Public methods
    //

    /*
     * Buttons
     */
    public final List<Button> getButtons() {
        return this.buttons;
    }

    /*
     * Is Button
     */
    public final boolean isButton(int slot) {
        return this.buttons.stream().anyMatch(b -> b.getSlot() == slot);
    }

    /*
     * Get Button
     */
    public final Button getButton(int slot) {
        return this.buttons.stream()
                .filter(b -> b.getSlot() == slot)
                .findFirst()
                .orElse(null);
    }

    /*
     * Is empty slots modifiable
     */
    public boolean isEmptySlotsModifiable() {
        return emptySlotsModifiable;
    }

    /*
     * Handle close callback
     */
    public final void handleCloseCallback() {
        if (this.closeCallback != null) {
            this.closeCallback.accept(this.inventory);
        }
    }

    //----------------------------------------
    //   Protected methods
    //

    /*
     * Create inventory with
     * @param size
     * @param title
     */
    protected final void createInventory(int size, String title) {
        this.inventory = Bukkit.createInventory(null, size, Hex.color(title));
    }

    /*
     * Create inventory with
     * @param size
     */
    protected final void createInventory(int size) {
        createInventory(size, title);
    }

    /*
     * Create inventory with no info
     */
    protected final void createInventory() {
        createInventory(size, title);
    }

    /*
     * Get inventory
     */
    protected final Inventory getInventory() {
        return inventory;
    }

    /*
     * Set size of GUI
     */
    protected final void setSize(int size) {
        this.size = size;
        createInventory(size, title);
    }

    /*
     * Set title of GUI
     */
    protected final void setTitle(String title) {
        this.title = title;
        createInventory(size, title);
    }

    /*
     * Add buttons
     */
    protected final void addButton(Button button) {
        this.buttons.add(button);
    }

    /*
     * Get item from inventory slot
     */
    protected final ItemStack getInvItem(int slot) {
        if (inventory == null) return null;
        return this.inventory.getItem(slot);
    }

    /*
     * Set slot item in inventory
     */
    protected final void setInvItem(int slot, ItemStack item) {
        if (inventory == null) return;
        this.inventory.setItem(slot, item);
    }

    /*
     * Add filler to GUI using itemstack
     */
    protected final void addFiller(List<Integer> slots, ItemStack filler) {
        for (Integer slot : slots) {
            this.addButton(new Button(slot) {
                @Override
                public ItemStack getItem() {
                    return ItemC.of(filler)
                            .name(" ")
                            .get();
                }

                @Override
                public void onClick(Player p, ClickType clickType) {
                }
            });
        }
    }

    /*
     * Add filler to GUI using material
     */
    protected final void addFiller(List<Integer> slots, Material filler) {
        addFiller(slots, new ItemStack(filler));
    }

    /*
     * Make empty slots modifiable
     */
    protected final void makeEmptySlotsModifiable(boolean b) {
        this.emptySlotsModifiable = b;
    }

    /*
     * Display GUI to player
     */
    protected final void displayTo(Player p) {
        if(inventory == null) {
            createInventory(size, title);
        }
        Inventory inv = this.inventory;

        //Buttons
        for(Button button : buttons) {
            inv.setItem(button.getSlot(), button.getItem());
        }

        p.openInventory(inv);
        p.setMetadata(MenuL.METADATA_ID, new FixedMetadataValue(plugin, this));
    }

    /*
     * Close functions
     */
    protected final void runOnClose(Consumer<Inventory> callBack) {
        this.closeCallback = callBack;
    }
}

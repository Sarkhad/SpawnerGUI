package com.mrmag518.SpawnerGUI;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIHandler implements Listener {
    private String name;
    private final int size;
    private ItemStack[] items;
    private CreatureSpawner spawner;
    
    public GUIHandler(String name, int size, CreatureSpawner spawner) {
        this.name = name;
        this.size = size;
        this.items = new ItemStack[size];
        this.spawner = spawner;
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("SpawnerGUI"));
    }
    
    public void setItem(int position, ItemStack icon, String name, String... lore) {
        ItemMeta im = icon.getItemMeta();
        im.setDisplayName(name);
        im.setLore(Arrays.asList(lore));
        icon.setItemMeta(im);
        items[position] = icon;
    }
    
    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(player, size, name);
            
        for(int i = 0; i < items.length; i++) {
            inv.setItem(i, items[i]);
        }
        player.openInventory(inv);
    }
    
    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if(event.getInventory().getName().equals(name)) {
            event.setCancelled(true);
            
            int slot = event.getRawSlot();

            if(slot >= 0 && slot < size && items[slot] != null) {
                GUIClickEvent e = new GUIClickEvent(slot, (Player)event.getWhoClicked(), spawner);
                Bukkit.getPluginManager().callEvent(e);

                if(e.willClose()) {
                    event.getWhoClicked().getOpenInventory().close();
                }
            }
        }
    }
    
    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        Inventory inv = event.getPlayer().getInventory();
        
        if(inv.getName().equals(name)) {
            eat();
            
            if(SpawnerGUI.openGUIs.contains(event.getPlayer().getName())) {
                SpawnerGUI.openGUIs.remove(event.getPlayer().getName());
            }
        }
    }
    
    private void eat() {
        this.items = null;
        this.name = null;
        this.spawner = null;
        HandlerList.unregisterAll(this);
    }
}

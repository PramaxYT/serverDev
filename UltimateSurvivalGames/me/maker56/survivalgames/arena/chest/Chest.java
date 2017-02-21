/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.Inventory
 */
package me.maker56.survivalgames.arena.chest;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

public class Chest {
    private Inventory inventory;
    private Location loc;

    public Chest(Inventory inventory, Location loc) {
        this.inventory = inventory;
        this.loc = loc;
    }

    public Location getLocation() {
        return this.loc;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}


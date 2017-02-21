/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package me.maker56.survivalgames.arena.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.database.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ChestManager {
    private static List<List<ItemStack>> items = new ArrayList<List<ItemStack>>();
    private static FileConfiguration c;
    private static String title;
    private Random r = new Random();

    public static void reinitializeConfig() {
        c = SurvivalGames.chestloot;
        title = ChatColor.translateAlternateColorCodes((char)'&', (String)c.getString("Chest-Title", "Survival Chest"));
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        for (String key : c.getConfigurationSection("Chestloot.").getKeys(false)) {
            ArrayList<ItemStack> l = new ArrayList<ItemStack>();
            for (String itemKey : c.getStringList("Chestloot." + key)) {
                l.add(ConfigUtil.parseItemStack(itemKey));
            }
            items.add(l);
        }
    }

    public ChestManager() {
        ChestManager.reinitializeConfig();
    }

    public Chest getRandomChest(Player p, Location loc) {
        Inventory i = Bukkit.createInventory((InventoryHolder)p, (int)27, (String)title);
        this.equipInventory(i);
        return new Chest(i, loc);
    }

    private void equipInventory(Inventory inv) {
        int stacks = this.r.nextInt(8) + 1;
        ArrayList<List<ItemStack>> groups = new ArrayList<List<ItemStack>>();
        int i = 0;
        while (i < stacks) {
            groups.add(this.getRandomList());
            ++i;
        }
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (List g : groups) {
            items.add((ItemStack)g.get(this.r.nextInt(g.size())));
        }
        for (ItemStack is : items) {
            inv.setItem(this.r.nextInt(27), is);
        }
    }

    private List<ItemStack> getRandomList() {
        int ri = this.r.nextInt(100) + 1;
        if (ri <= 40) {
            return items.get(0);
        }
        if (ri > 40 & ri <= 70) {
            return items.get(1);
        }
        if (ri > 70 & ri <= 85) {
            return items.get(2);
        }
        if (ri > 85 & ri <= 95) {
            return items.get(3);
        }
        return items.get(4);
    }
}


/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.inventory.meta.SkullMeta
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package me.maker56.survivalgames.database;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConfigUtil {
    public static ItemStack parseItemStack(String s) {
        try {
            String[] gSplit = s.split(" ");
            ItemStack is = null;
            String[] idsSplit = gSplit[0].split(":");
            try {
                is = new ItemStack(Integer.parseInt(idsSplit[0]));
            }
            catch (NumberFormatException e) {
                is = new ItemStack(Material.valueOf((String)idsSplit[0]));
            }
            if (idsSplit.length > 1) {
                is.setDurability(Short.parseShort(idsSplit[1]));
            }
            if (gSplit.length > 1) {
                int metaStart = 2;
                try {
                    is.setAmount(Integer.parseInt(gSplit[1]));
                }
                catch (NumberFormatException e) {
                    metaStart = 1;
                }
                int meta = metaStart;
                while (meta < gSplit.length) {
                    String rawKey = gSplit[meta];
                    String[] split = rawKey.split(":");
                    String key = split[0];
                    ItemMeta im = is.getItemMeta();
                    if (key.equalsIgnoreCase("name")) {
                        im.setDisplayName(ChatColor.translateAlternateColorCodes((char)'&', (String)split[1]).replace("_", " "));
                    } else if (key.equalsIgnoreCase("lore")) {
                        ArrayList<String> lore = new ArrayList<String>();
                        String[] arrstring = split[1].split("//");
                        int n = arrstring.length;
                        int n2 = 0;
                        while (n2 < n) {
                            String line = arrstring[n2];
                            lore.add(ChatColor.translateAlternateColorCodes((char)'&', (String)line).replace("_", " "));
                            ++n2;
                        }
                        im.setLore(lore);
                    } else if (key.equalsIgnoreCase("color") && im instanceof LeatherArmorMeta) {
                        LeatherArmorMeta lam = (LeatherArmorMeta)im;
                        String[] csplit = split[1].split(",");
                        Color color = Color.fromBGR((int)Integer.parseInt(csplit[0]), (int)Integer.parseInt(csplit[1]), (int)Integer.parseInt(csplit[2]));
                        lam.setColor(color);
                    } else if (key.equalsIgnoreCase("effect") && im instanceof PotionMeta) {
                        PotionMeta pm = (PotionMeta)im;
                        String[] psplit = split[1].split(",");
                        pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName((String)psplit[0]), Integer.parseInt(psplit[1]) * 20, Integer.parseInt(psplit[2])), true);
                    } else if (key.equalsIgnoreCase("player") && im instanceof SkullMeta) {
                        ((SkullMeta)im).setOwner(split[1]);
                    } else if (key.equalsIgnoreCase("enchant")) {
                        String[] esplit = split[1].split(",");
                        im.addEnchant(Enchantment.getByName((String)esplit[0].toUpperCase()), Integer.parseInt(esplit[1]), true);
                    }
                    is.setItemMeta(im);
                    ++meta;
                }
            }
            return is;
        }
        catch (Exception e) {
            System.err.println("[SurvivalGames] Cannot parse ItemStack: " + s + " - Mabye this is the reason: " + e.toString());
            return null;
        }
    }

    public static Location parseLocation(String s) {
        try {
            String[] split = s.split(",");
            World world = Bukkit.getWorld((String)split[0]);
            try {
                double x = Double.parseDouble(split[1]);
                double y = Double.parseDouble(split[2]);
                double z = Double.parseDouble(split[3]);
                float yaw = Float.parseFloat(split[4]);
                float pitch = Float.parseFloat(split[5]);
                return new Location(world, x, y, z, yaw, pitch);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                int x = Integer.parseInt(split[1]);
                int y = Integer.parseInt(split[2]);
                int z = Integer.parseInt(split[3]);
                return new Location(world, (double)x, (double)y, (double)z);
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String serializeLocation(Location l, boolean exact) {
        String key = new String();
        key = String.valueOf(key) + l.getWorld().getName() + ",";
        key = exact ? String.valueOf(key) + l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch() : String.valueOf(key) + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
        return key;
    }
}


/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 */
package me.maker56.survivalgames.commands.messages;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;
import me.maker56.survivalgames.SurvivalGames;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageHandler {
    private static HashMap<String, String> messages = new HashMap();

    public static void reload() {
        messages.clear();
        for (String key : SurvivalGames.messages.getConfigurationSection("").getKeys(false)) {
            messages.put(key, MessageHandler.replaceColors(SurvivalGames.messages.getString(key)));
        }
        System.out.println("[SurvivalGames] " + messages.size() + " messages loaded!");
    }

    public static String getMessage(String name) {
        if (messages.containsKey(name)) {
            if (name.equalsIgnoreCase("prefix")) {
                return messages.get(name);
            }
            return String.valueOf(messages.get("prefix")) + messages.get(name);
        }
        return "\u00a7cMessage not found!";
    }

    public static String replaceColors(String s) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)s);
    }
}


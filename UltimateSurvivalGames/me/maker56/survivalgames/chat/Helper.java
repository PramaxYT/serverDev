/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.maker56.survivalgames.chat;

import me.maker56.survivalgames.chat.JSONMessage;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import org.bukkit.entity.Player;

public class Helper {
    public static void showLobbyHelpsite(Player p) {
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Lobby \u00a77\u00a7m---\u00a7r \u00a76Helpsite");
        p.sendMessage("\u00a78/\u00a76sg lobby create <LOBBYNAME> \u00a77- \u00a7eCreates a game with the specify name!");
        p.sendMessage("\u00a78/\u00a76sg lobby setspawn <LOBBYNAME> \u00a77- \u00a7eSet the Lobby-Spawnlocation at the specify game!");
        p.sendMessage("\u00a78/\u00a76sg lobby unload <LOBBYNAME> \u00a77- \u00a7eUnload a lobby!");
        p.sendMessage("\u00a78/\u00a76sg lobby load <LOBBYNAME> \u00a77- \u00a7eLoad a lobby!");
        p.sendMessage("\u00a78/\u00a76sg lobby reload <LOBBYNAME> \u00a77- \u00a7eUnload and load a lobby!");
        p.sendMessage("\u00a78/\u00a76sg lobby list <LOBBYNAME> \u00a77- \u00a7eList of all loaded arenas in a lobby!");
        p.sendMessage("\u00a78/\u00a76sg lobby delete <LOBBYNAME> \u00a77- \u00a7eDeletes a lobby from file!");
        p.sendMessage("");
        new JSONMessage("\u00a77\u00a7oNeed more help? Click here!").tooltip("Click here to open the official bukkit site!").link("http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/").send(p);
    }

    public static void showArenaHelpsite(Player p) {
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "Arena-Setup \u00a77\u00a7m---\u00a7r \u00a76Helpsite");
        p.sendMessage("\u00a78/\u00a76sg arena create <LOBBYNAME> <ARENA NAME> \u00a77- \u00a7eCreates an arena in a specify game!");
        p.sendMessage("\u00a78/\u00a76sg arena select <LOBBYNAME> <ARENA NAME> \u00a77- \u00a7eSelects the specify arena!");
        p.sendMessage("\u00a78/\u00a76sg arena tools \u00a77- \u00a7eGives you the Arena-Selection Tools!");
        p.sendMessage("\u00a78/\u00a76sg arena check \u00a77- \u00a7eShows whats even need to be done on the selected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena addspawn \u00a77- \u00a7eAdd a Spawn on the selected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena removespawn <SPAWNID> \u00a77- \u00a7eRemoves a spawn from the selected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena deathmatch \u00a77- \u00a7eDe/activate the Deathmatch on the spelected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena deathmatch add \u00a77- \u00a7eAdd a Deathmatch-Spawn on the selected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena deathmatch remove <SPAWNID> \u00a77- \u00a7eRemove an Spawn on the selected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena finish \u00a77- \u00a7eFinished the create-setup on the selected arena!");
        p.sendMessage("\u00a78/\u00a76sg arena save \u00a77- \u00a7eSaves the blocks of an arena to file for map reset!");
        p.sendMessage("\u00a78/\u00a76sg arena delete \u00a77- \u00a7eRemoves an arena in a lobby!");
        p.sendMessage("");
        new JSONMessage("\u00a77\u00a7oNeed more help? Click here!").tooltip("Click here to open the official bukkit site!").link("http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/").send(p);
    }
}


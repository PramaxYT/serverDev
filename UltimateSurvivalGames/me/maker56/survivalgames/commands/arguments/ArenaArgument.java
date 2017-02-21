/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.sk89q.worldedit.bukkit.WorldEditPlugin
 *  org.bukkit.Material
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.inventory.meta.ItemMeta
 */
package me.maker56.survivalgames.commands.arguments;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.util.HashMap;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.ArenaManager;
import me.maker56.survivalgames.chat.Helper;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenaArgument {
    private CommandSender sender;
    private String[] args;

    public ArenaArgument(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public boolean execute() {
        if (!(this.sender instanceof Player)) {
            this.sender.sendMessage("\u00a7cThe arena argument can only execute as a Player!");
            return true;
        }
        Player p = (Player)this.sender;
        if (!PermissionHandler.hasPermission((CommandSender)p, Permission.ARENA)) {
            p.sendMessage(MessageHandler.getMessage("no-permission"));
            return true;
        }
        if (this.args.length == 1) {
            Helper.showArenaHelpsite(p);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("delete")) {
            SurvivalGames.arenaManager.delete(p);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("save")) {
            SurvivalGames.arenaManager.save(p);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("create")) {
            if (this.args.length < 4) {
                p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a arenaname and a gamename: /sg arena create <LOBBYNAME> <ARENA NAME>"));
                return true;
            }
            String arenaname = this.getArgs(3);
            String gamename = this.args[2];
            SurvivalGames.arenaManager.createArena(p, arenaname, gamename);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("select")) {
            if (this.args.length < 4) {
                p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a arenaname and a gamename: /sg arena select <LOBBYNAME> <ARENA NAME>"));
                return true;
            }
            String arenaname = this.getArgs(3);
            String gamename = this.args[2];
            SurvivalGames.arenaManager.selectArena(p, arenaname, gamename);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("check")) {
            SurvivalGames.arenaManager.check(p);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("addspawn")) {
            SurvivalGames.arenaManager.addSpawn(p, "Spawns");
            return true;
        }
        if (this.args[1].equalsIgnoreCase("removespawn")) {
            if (this.args.length == 2) {
                p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a number: /sg arena removespawn <ID>"));
                return true;
            }
            int id = 0;
            try {
                id = Integer.parseInt(this.args[2]);
            }
            catch (NumberFormatException e) {
                p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", String.valueOf(this.args[2]) + " isn't a valid number!"));
                return true;
            }
            SurvivalGames.arenaManager.removeSpawn(p, id, "Spawns");
            return true;
        }
        if (this.args[1].equalsIgnoreCase("deathmatch")) {
            if (this.args.length == 2) {
                SurvivalGames.arenaManager.changeDeathmatch(p);
                return true;
            }
            if (this.args[2].equalsIgnoreCase("add")) {
                SurvivalGames.arenaManager.addSpawn(p, "Deathmatch-Spawns");
                return true;
            }
            if (this.args[2].equalsIgnoreCase("remove")) {
                if (this.args.length == 3) {
                    p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a number: /sg arena deathmatch remove <ID>"));
                    return true;
                }
                int id = 0;
                try {
                    id = Integer.parseInt(this.args[3]);
                }
                catch (NumberFormatException e) {
                    p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", String.valueOf(this.args[3]) + " isn't a valid number!"));
                    return true;
                }
                SurvivalGames.arenaManager.removeSpawn(p, id, "Deathmatch-Spawns");
                return true;
            }
            return true;
        }
        if (this.args[1].equalsIgnoreCase("finish")) {
            SurvivalGames.arenaManager.finishSetup(p);
            return true;
        }
        if (this.args[1].equalsIgnoreCase("tools")) {
            WorldEditPlugin we = SurvivalGames.getWorldEdit();
            if (we == null) {
                ItemStack is = new ItemStack(Material.CARROT_STICK);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("SurvivalGames Selection Tool");
                is.setItemMeta(im);
                p.getInventory().addItem(new ItemStack[]{is});
                p.sendMessage(MessageHandler.getMessage("arena-tools"));
            } else {
                p.getInventory().addItem(new ItemStack[]{new ItemStack(we.getConfig().getInt("wand-item"))});
                p.sendMessage(MessageHandler.getMessage("arena-tools-worldedit"));
            }
            return true;
        }
        p.sendMessage(String.valueOf(MessageHandler.getMessage("prefix")) + "\u00a7cCommand not found! Type /sg arena for help!");
        return true;
    }

    private String getArgs(int i) {
        String s = "";
        int a = i;
        while (i < this.args.length) {
            try {
                s = String.valueOf(s) + this.args[a] + " ";
            }
            catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
            ++a;
        }
        return s.substring(0, s.length() - 1);
    }
}

